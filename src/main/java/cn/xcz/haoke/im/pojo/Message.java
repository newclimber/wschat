package cn.xcz.haoke.im.pojo;
 
import lombok.AllArgsConstructor; import lombok.Builder; import lombok.Data; import lombok.NoArgsConstructor; import org.bson.types.ObjectId; import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.index.Indexed; import org.springframework.data.mongodb.core.mapping.Document; import org.springframework.data.mongodb.core.mapping.Field;
 
import java.util.Date;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message") //指定表的名称
@Builder
public class Message {
 @Id
 private ObjectId id;
 private String msg;
 /**    
  * * 消息状态，1-未读，2-已读    
  * */
 @Indexed //这个字段有索引
 private Integer status;
 @Field("send_date")
 @Indexed //这个字段有索引
 private Date sendDate;
 @Field("read_date")
 private Date readDate;
 @Indexed //这个字段有索引
 private User from;
 @Indexed //这个字段有索引
 private User to;
}
 