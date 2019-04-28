package org.infinity.semanticbrain.message;

import com.qianmi.ms.starter.rocketmq.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.infinity.semanticbrain.config.LocalCacheUpdateAspect.MethodOperation;


@Component
public class LocalCacheUpdateMessageProducer {

    public static final String LOCAL_CACHE_UPDATE_TOPIC = "semanticBrainLocalCacheUpdate";

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void send(MethodOperation methodOperation) {
        rocketMQTemplate.convertAndSend(LOCAL_CACHE_UPDATE_TOPIC, methodOperation);
    }

    public void syncSend(MethodOperation methodOperation) {
        rocketMQTemplate.syncSend(LOCAL_CACHE_UPDATE_TOPIC, methodOperation);
    }
}
