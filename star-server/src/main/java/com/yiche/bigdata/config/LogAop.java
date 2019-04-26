package com.yiche.bigdata.config;

import com.yiche.bigdata.annotation.SystemLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogAop {
    private Logger logger = LoggerFactory.getLogger(LogAop.class);

    @Pointcut(value = "@annotation(com.yiche.bigdata.annotation.SystemLog)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {
        try {
            saveLog(point);
        } catch (Exception e) {
            logger.info("save log exception");
        }
        return point.proceed();
    }


    private void saveLog(ProceedingJoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;

        Object target = joinPoint.getTarget();
        Method method = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        String methodName = method.getName();
        String className = target.getClass().getName();

        SystemLog annotation = method.getAnnotation(SystemLog.class);
        String systemLogName = annotation.value();

        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] params = joinPoint.getArgs();

        int paramSize = parameterNames.length >= params.length ? params.length : parameterNames.length;
        Map<String, Object> parameters = new HashMap<>();
        for (int i = 0; i < paramSize; i++) {
            parameters.put(parameterNames[i], params[i]);
        }

        logger.info("业务:[{}],类:[{}],方法:[{}],参数：[{}]", systemLogName, className, methodName, parameters);
    }
}
