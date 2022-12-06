package org.talon.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
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

}
