package org.infinity.semanticbrain.component;

import org.infinity.semanticbrain.config.RabbitMessageConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.MethodOperation;

@Component
public class RabbitMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String message) {
        //FanoutExchange类型的交换机，routingKey不起作用
        rabbitTemplate.convertAndSend(RabbitMessageConfiguration.FANOUT_EXCHANGE, "", message);
    }

    public void publish(MethodOperation methodOperation) {
        //FanoutExchange类型的交换机，routingKey不起作用
        rabbitTemplate.convertAndSend(RabbitMessageConfiguration.FANOUT_EXCHANGE, "", methodOperation);
    }
}
