package cn.xcz.haoke.im.websocket;

import cn.xcz.haoke.im.dao.MessageDAO;
import cn.xcz.haoke.im.pojo.Message;
import cn.xcz.haoke.im.pojo.UserData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandler extends TextWebSocketHandler {
    //主要是用来把对象转换成为一个json字符串返回到前端
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<Long, WebSocketSession> SESSIONS = new HashMap<>();
    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 经过拦截器拿到uid之后，将当前用户的session放置到map中，后面会使用相应的session通信
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");
        // 将当前用户的session放置到map中，后面会使用相应的session通信        
        SESSIONS.put(uid, session);
    }

    /**
     * mongodb对数据进行解析，将数据保存到 mongodb中，如果 toid 在线，更新状态，否则信息就放在 mongodb中
     * @param session
     * @param textMessage
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Long uid = (Long) session.getAttributes().get("uid");

        //提供的readTree，返回JsonNode对象，使用简单。
        JsonNode jsonNode = MAPPER.readTree(textMessage.getPayload());
        Long toId = jsonNode.get("toId").asLong();
        String msg = jsonNode.get("msg").asText();

        Message message = Message.builder()
                .from(UserData.USER_MAP.get(uid))
                .to(UserData.USER_MAP.get(toId))
                .msg(msg)
                .build();
        message = this.messageDAO.saveMessage(message);

        // 判断to用户是否在线        
        WebSocketSession toSession = SESSIONS.get(toId);
        if (toSession != null && toSession.isOpen()) {
            //TODO 具体格式需要和前端对接,序列化一个 json 进行发送          
            toSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(message)));
            // 更新消息状态为已读            
            this.messageDAO.updateMessageState(message.getId(), 2);
        }else{
            //该用户可能下线，也可能在其他节点中,发送消息到mq系统
            //需求：添加 tag，便于消费者对消息的筛选
            this.rocketMQTemplate.convertAndSend("haoke-im-send-message=topic:SEND_MESSAGE",MAPPER.writeValueAsString(message));
        }

    }
}
