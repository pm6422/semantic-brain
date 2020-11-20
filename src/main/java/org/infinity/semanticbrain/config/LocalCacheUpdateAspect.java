package org.infinity.semanticbrain.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.infinity.semanticbrain.message.LocalCacheUpdateMessageProducer;
import org.infinity.semanticbrain.utils.NetworkIpUtils;
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
@Slf4j
public class LocalCacheUpdateAspect {

    public static final String                          BROADCAST_METHOD_PREFIX = "broadcast";
    private final       LocalCacheUpdateMessageProducer localCacheUpdateMessageProducer;

    public LocalCacheUpdateAspect(LocalCacheUpdateMessageProducer localCacheUpdateMessageProducer) {
        this.localCacheUpdateMessageProducer = localCacheUpdateMessageProducer;
    }

    @Around("execution(* " + ApplicationConstants.BASE_PACKAGE + ".service.impl.*CacheServiceImpl." + BROADCAST_METHOD_PREFIX + "*(..)) || " + BroadcastExecute.AROUND)
    public Object localCacheUpdatePointcut(ProceedingJoinPoint joinPoint) throws Throwable {
        String typeName = joinPoint.getSignature().getDeclaringTypeName();

        String originalMethodName = joinPoint.getSignature().getName();
        // Remove method name with BROADCAST_METHOD_PREFIX
        String methodName = StringUtils.uncapitalize(originalMethodName.substring(BROADCAST_METHOD_PREFIX.length()));
        Object[] methodArgs = joinPoint.getArgs();

        // Starting broadcast update
        // TODO: check result
        localCacheUpdateMessageProducer.syncSend(MethodOperation.of(typeName, methodName, methodArgs, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(new Date())));
        log.debug("Initiated a broadcast update");
        return joinPoint.proceed();
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
        String AROUND = "@annotation(" + ApplicationConstants.BASE_PACKAGE + ".config.LocalCacheUpdateAspect.BroadcastExecute)";
    }
}


