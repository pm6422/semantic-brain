package org.infinity.semanticbrain.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.infinity.semanticbrain.message.LocalCacheUpdateMessageProducer;
import org.infinity.semanticbrain.utils.NetworkIpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;


/**
 * Aspect for method execution of local cache service.
 */
@Aspect
@Configuration
public class LocalCacheUpdateAspect implements ApplicationContextAware {

    private static final Logger                          LOGGER                  = LoggerFactory.getLogger(LocalCacheUpdateAspect.class);
    public static final  String                          BROADCAST_METHOD_PREFIX = "broadcast";
    private              ApplicationContext              applicationContext;
    @Autowired
    private              LocalCacheUpdateMessageProducer localCacheUpdateMessageProducer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Around("execution(* " + ApplicationConstants.BASE_PACKAGE + ".service.impl.*CacheServiceImpl." + BROADCAST_METHOD_PREFIX + "*(..)) || " + BroadcastExecute.AROUND)
    public Object localCacheUpdatePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        String typeName = joinPoint.getSignature().getDeclaringTypeName();
        Object type = this.applicationContext.getBean(joinPoint.getSignature().getDeclaringType());
        if (type == null) {
            throw new RuntimeException("Incorrect usage of pointcut.");
        }

        String originalMethodName = joinPoint.getSignature().getName();
        String methodName = originalMethodName.contains(BROADCAST_METHOD_PREFIX) ? StringUtils.uncapitalize(originalMethodName.substring(BROADCAST_METHOD_PREFIX.length())) : originalMethodName;
        Object[] methodArgs = joinPoint.getArgs();

        // Starting broadcast update
        // TODO: check result
        localCacheUpdateMessageProducer.syncSend(MethodOperation.of(typeName, methodName, methodArgs, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date())));
        LOGGER.debug("Initiated a broadcast update");

        Object result = joinPoint.proceed();
        return result;
    }


    public static class MethodOperation implements Serializable {

        private static final long     serialVersionUID = 4648383076842560788L;
        public static final  String   INSTANCE_NODE_ID = NetworkIpUtils.INTERNET_IP; // 注意同一台机器上不可以同时部署多个应用，保证一个IP一个应用
        private              String   instanceNodeId;
        private              String   typeName;
        private              String   methodName;
        private              Object[] methodArgs;
        private              String   sentTime;

        public MethodOperation() {
            super();
        }

        public static MethodOperation of(String typeName, String methodName, Object[] methodArgs, String sentTime) {
            MethodOperation methodOperation = new MethodOperation();
            methodOperation.setInstanceNodeId(INSTANCE_NODE_ID);
            methodOperation.setTypeName(typeName);
            methodOperation.setMethodName(methodName);
            methodOperation.setMethodArgs(methodArgs);
            methodOperation.setSentTime(sentTime);

            return methodOperation;
        }

        public String getInstanceNodeId() {
            return instanceNodeId;
        }

        public void setInstanceNodeId(String instanceNodeId) {
            this.instanceNodeId = instanceNodeId;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Object[] getMethodArgs() {
            return methodArgs;
        }

        public void setMethodArgs(Object[] methodArgs) {
            this.methodArgs = methodArgs;
        }

        public String getSentTime() {
            return sentTime;
        }

        public void setSentTime(String sentTime) {
            this.sentTime = sentTime;
        }

        @Override
        public String toString() {
            return "MethodOperation{" +
                    "instanceNodeId='" + instanceNodeId + '\'' +
                    ", typeName='" + typeName + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", methodArgs=" + Arrays.toString(methodArgs) +
                    ", sentTime='" + sentTime + '\'' +
                    '}';
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface BroadcastExecute {

        public static final String AROUND = "@annotation(" + ApplicationConstants.BASE_PACKAGE + ".config.LocalCacheUpdateAspect.BroadcastExecute)";

    }
}


