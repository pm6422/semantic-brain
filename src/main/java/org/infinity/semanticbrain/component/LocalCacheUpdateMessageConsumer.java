package org.infinity.semanticbrain.component;

import com.qianmi.ms.starter.rocketmq.annotation.RocketMQMessageListener;
import com.qianmi.ms.starter.rocketmq.core.RocketMQListener;
import com.qianmi.ms.starter.rocketmq.enums.ConsumeMode;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.infinity.semanticbrain.component.LocalCacheUpdateMessageProducer.LOCAL_CACHE_UPDATE_TOPIC;
import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.MethodOperation;

@Component
@RocketMQMessageListener(topic = LOCAL_CACHE_UPDATE_TOPIC, consumerGroup = "consumer-group", messageModel = MessageModel.BROADCASTING, consumeMode = ConsumeMode.ORDERLY)
public class LocalCacheUpdateMessageConsumer implements RocketMQListener<MethodOperation>, ApplicationContextAware {

    private static final Logger             LOGGER = LoggerFactory.getLogger(LocalCacheUpdateMessageConsumer.class);
    private              ApplicationContext applicationContext;
    @Autowired
    private              Environment        env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(MethodOperation methodOperation) {
        LOGGER.debug("Message consumer: {}:{}", MethodOperation.INSTANCE_NODE_ID, env.getProperty("server.port"));
        if (methodOperation.getInstanceNodeId().equals(MethodOperation.INSTANCE_NODE_ID)) {
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
}
