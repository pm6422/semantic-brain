package org.infinity.semanticbrain.config;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.reflect.Method;
import java.util.Date;

import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.INSTANCE_NODE_ID;
import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.MethodOperation;

@Configuration
@RabbitListener(queues = RabbitMessageConfiguration.BROADCAST_UPDATE_QUEUE)
public class RabbitMessageConfiguration implements ApplicationContextAware {

    private static final Logger             LOGGER                 = LoggerFactory.getLogger(RabbitMessageConfiguration.class);
    public static final  String             FANOUT_EXCHANGE        = "semanticBrainFanoutExchange";
    public static final  String             BROADCAST_UPDATE_QUEUE = "semanticBrainBroadcastUpdateQueue";
    private              ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Create a queue if it does not exist
     *
     * @return
     */
    @Bean
    public Queue broadcastUpdateLocalCacheQueue() {
        return new Queue(BROADCAST_UPDATE_QUEUE);
    }

    /**
     * 广播模式，所有消费者都可以收到消息
     * 只要队列绑定到该交换机就可以接收消息
     *
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindingExchange(Queue broadcastUpdateLocalCacheQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(broadcastUpdateLocalCacheQueue).to(fanoutExchange);
    }

    @RabbitHandler
    public void receiveMethodOperationMessage(@Payload MethodOperation methodOperation) {
        if (methodOperation.getInstanceNodeId().equals(INSTANCE_NODE_ID)) {
            // Do NOT execute the method operation which is initiated by current node itself
            return;
        }
        try {
            Object type = this.applicationContext.getBean(Class.forName(methodOperation.getTypeName()));
            Class<?>[] parameterTypes = new Class[methodOperation.getMethodArgs().length];
            if (ArrayUtils.isNotEmpty(methodOperation.getMethodArgs())) {
                for (int i = 0; i < methodOperation.getMethodArgs().length; i++) {
                    parameterTypes[i] = methodOperation.getMethodArgs()[i].getClass();
                }
            }

            Method method = type.getClass().getMethod(methodOperation.getMethodName(), parameterTypes);
            method.invoke(type, methodOperation.getMethodArgs());
            LOGGER.info("Executed broadcasting {}:{}", methodOperation.getTypeName(), methodOperation.getMethodName());
        } catch (Exception e) {
            LOGGER.error("Failed to execute broadcasting {}.{}", methodOperation.getTypeName(), methodOperation.getMethodName());
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @RabbitHandler
    public void receiveTestMessage(String message) {
        LOGGER.debug("Received message {} at {}", message, new Date());
    }
}
