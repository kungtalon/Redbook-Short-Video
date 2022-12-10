package org.talon.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.talon.datautils.JsonUtils;
import org.talon.enums.CrudOperationEnum;
import org.talon.mongo.Message;
import org.talon.service.IMsgService;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Zelong
 * On 2022/12/10
 **/

@Slf4j
@Component
public class RabbitQueueConsumer {

    @Autowired
    private IMsgService msgService;

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_SYS_MSG})
    public void watchQueue(String payload,
                           org.springframework.amqp.core.Message amqpMessage) {
        log.debug("==> Getting message: " + payload);

        String routingKey = amqpMessage.getMessageProperties().getReceivedRoutingKey();
        if (StringUtils.isBlank(routingKey)) {
            log.error("Empty routing key received from rabbitmq! Queue = " + RabbitmqConfig.QUEUE_SYS_MSG);
            return ;
        }

        Message mongoMessage = JsonUtils.jsonToPojo(payload, Message.class);
        if (mongoMessage == null) {
            log.error("Empty message received from Rabbitmq! Queue = " + RabbitmqConfig.QUEUE_SYS_MSG);
            return ;
        }

        if (routingKey.contains(CrudOperationEnum.CREATE.value)) {
            createMongoMessage(mongoMessage);
        } else if (routingKey.contains(CrudOperationEnum.DELETE.value)) {
            deleteMongoMessage(mongoMessage);
        } else {
            log.error("Invalid routing key received from Rabbitmq! Key = " + routingKey
                    + " Queue = " + RabbitmqConfig.QUEUE_SYS_MSG);
        }
    }

    private void createMongoMessage(Message mongoMessage) {
        msgService.createMsg(mongoMessage.getFromUserId(),
                mongoMessage.getToUserId(),
                mongoMessage.getMsgType(),
                mongoMessage.getMsgContent());
    }

    private void deleteMongoMessage(Message mongoMessage) {
        if (!StringUtils.isBlank(mongoMessage.getFromUserId())) {
            msgService.deleteAllMessages(mongoMessage.getFromUserId(),
                    mongoMessage.getToUserId(),
                    mongoMessage.getMsgType(),
                    mongoMessage.getMsgContent());
            return ;
        } else if (!CollectionUtils.isEmpty(mongoMessage.getMsgContent())) {
            // delete by vlogid or commentid
            Map<String, Object> msgContent = mongoMessage.getMsgContent();
            if (msgContent.containsKey("vlogId")) {
                msgService.deleteAllMessagesByVlogId((String) msgContent.get("vlogId"));
            } else if (msgContent.containsKey("commentId")) {
                msgService.deleteAllMessagesByCommentId((String) msgContent.get("commentId"));
            } else {
                log.error("Wrong message from rabbitmq! No field specified for deletion!");
            }
            return ;
        }
        log.error("Wrong message from rabbitmq! No field specified for deletion!");
    }
}
