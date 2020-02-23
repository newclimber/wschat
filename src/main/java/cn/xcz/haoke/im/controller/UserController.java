//package cn.xcz.haoke.im.controller;
//
//import cn.xcz.haoke.im.pojo.Message;
//import cn.xcz.haoke.im.pojo.User;
//import cn.xcz.haoke.im.pojo.UserData;
//import cn.xcz.haoke.im.service.MessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@CrossOrigin
//@RequestMapping("user")
//public class UserController {
//
//    @Autowired
//    private MessageService messageService;
//
//    //拉取用户列表（模拟实现）    
//    @GetMapping
//    public List<Map<String, Object>> queryUserList(@RequestParam("fromId") Long fromId) {
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (Map.Entry<Long, User> userEntry : UserData.USER_MAP.entrySet()) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("id", userEntry.getValue().getId());
//            map.put("avatar", "http://itcast-haoke.oss-cnqingdao.aliyuncs.com/images/2018/12/08/15442410962743524.jpg");
//
//            map.put("from_user", fromId);
//            map.put("info_type", null);
//            map.put("to_user", map.get("id"));
//            map.put("username", userEntry.getValue().getUsername());
//
//            // 获取最后一条消息        
//            List<Message> messages = this.messageService.queryMessageList(fromId, userEntry.getValue().getId(), 1, 1);
//            if (messages != null && !messages.isEmpty()) {
//                Message message = messages.get(0);
//                map.put("chat_msg", message.getMsg());
//                map.put("chat_time", message.getSendDate().getTime());
//            }
//
//            result.add(map);
//        }
//
//        return result;
//    }
//
//}