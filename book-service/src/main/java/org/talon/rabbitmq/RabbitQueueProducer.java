package org.talon.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.talon.datautils.JsonUtils;
import org.talon.enums.CrudOperationEnum;
import org.talon.enums.MessageEnum;
import org.talon.mongo.Message;

import java.util.Objects;

/**
 * Created by Zelong
 * On 2022/12/10
 **/

@Component
public class RabbitQueueProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishMongoMessage(Message mongoMessage, CrudOperationEnum action) {
        String body = JsonUtils.objectToJson(mongoMessage);
        String routingKeySuffix = "exception";
        for (MessageEnum msgEnumType : MessageEnum.values()) {
            if (Objects.equals(msgEnumType.type, mongoMessage.getMsgType())) {
                routingKeySuffix = msgEnumType.value;
                break;
            }
        }

        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_MSG,
                RabbitmqConfig.ROUTING_KEY_PREFIX + "." + action.value + "." + routingKeySuffix,
                body);
    }
}
