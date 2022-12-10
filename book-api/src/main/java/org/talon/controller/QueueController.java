package org.talon.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talon.rabbitmq.RabbitmqConfig;
import org.talon.rest.JsonResponse;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@RestController
public class QueueController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("produce")
    public Object produce(){
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_MSG,
                RabbitmqConfig.ROUTING_KEY_PREFIX + ".send",
                "This is a new msg");
        return JsonResponse.ok();
    }
}
