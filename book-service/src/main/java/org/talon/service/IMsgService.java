package org.talon.service;

import org.springframework.stereotype.Service;
import org.talon.mongo.Message;

import java.util.List;
import java.util.Map;

@Service
public interface IMsgService {

    void createMsg(String fromUserId,
                   String toUserId,
                   Integer msgType,
                   Map msgContent);

    List<Message> queryMessages(String toUserId, Integer page, Integer pageSize);

    void deleteAllMessages(String fromUserId,
                String toUserId,
                Integer msgType,
                Map msgContent);

    void deleteAllMessagesByVlogId(String vlogId);

    void deleteAllMessagesByCommentId(String commentId);
}
