package org.talon.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.talon.mongo.Message;

import java.util.List;

@Repository
public interface IMessageRepository extends MongoRepository<Message, String> {

    List<Message> findAllByToUserIdOrderByCreateTimeDesc(String toUserId,
                                                         Pageable pageable);

    void deleteAllByFromUserIdAndToUserIdAndMsgType(String fromUserId,
                                                    String toUserId,
                                                    Integer msgType);

    @Query(value = "{'fromUserId': '?0', 'msgType': ?1, 'msgContent.vlogId': '?2'}", delete = true)
    void deleteAllByFromUserIdAndMsgTypeAndVlogId(String fromUserId, Integer msgType, String vlogId);

    @Query(value = "{'fromUserId': '?0', 'msgType': ?1, 'msgContent.commentId': '?2'}", delete = true)
    void deleteAllByFromUserIdAndMsgTypeAndCommentId(String fromUserId, Integer msgType, String commentId);

    @Query(value = "{'msgContent.vlogId': '?0'}", delete = true)
    void deleteAllByVlogId(String vlogId);

    @Query(value = "{'msgContent.commentId': '?0'}", delete = true)
    void deleteAllByCommentId(String commentId);

}
