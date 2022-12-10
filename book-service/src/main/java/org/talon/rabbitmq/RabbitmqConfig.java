package org.talon.rabbitmq;

import lombok.Data;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Zelong
 * On 2022/11/13
 **/

@Configuration
@Data
public class RabbitmqConfig {

    public static final String EXCHANGE_MSG = "exchange_msg";

    public static final String QUEUE_SYS_MSG = "queue_sys_msg";

    public static final String ROUTING_KEY_PREFIX = "sys.msg";

    @Bean(EXCHANGE_MSG)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_MSG) // use topic mode
                .durable(true)               // whether make msg durable
                .build();
    }

    @Bean(QUEUE_SYS_MSG)
    public Queue queue() {
        return new Queue(QUEUE_SYS_MSG);
    }

    @Bean
    public Binding binding(@Qualifier(EXCHANGE_MSG) Exchange exchange,
                           @Qualifier(QUEUE_SYS_MSG) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY_PREFIX + ".*.*")    // 定义路由规则，类似requestmapping
                .noargs();
    }
}
