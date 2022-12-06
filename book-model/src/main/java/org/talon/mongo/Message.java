package org.talon.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

/**
 * Created by Zelong
 * On 2022/12/4
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document("message")
public class Message {

    @Id
    private String id;

    @Field("fromUserId")
    private String fromUserId;
    @Field("fromNickname")
    private String fromNickname;
    @Field("fromFace")
    private String fromFace;

    @Field("toUserId")
    private String toUserId;
    @Field("toNickname")
    private String toNickname;
    @Field("toFace")
    private String toFace;

    @Field("msgType")
    private Integer msgType;

    @Field("msgContent")
    private Map msgContent;

    @Field("createTime")
    private Date createTime;

}
