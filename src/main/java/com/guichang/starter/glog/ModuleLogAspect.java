package com.guichang.starter.glog;

import com.alibaba.fastjson.JSONObject;
import com.guichang.starter.glog.annotation.ModuleLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import java.util.UUID;

/**
 * 定时任务统一拦截
 *
 * @author guichang
 * @since 2018/2/5
 */
@Slf4j
@Aspect
@Order(1)
public class ModuleLogAspect {
    private static final String TRACE = "trace";
    private static final String MODULE_NAME = "moduleName";

    @Around(value = "@annotation(moduleLog)")
    public Object aroundJob(ProceedingJoinPoint pPoint, ModuleLog moduleLog) throws Throwable {
        MDC.put(TRACE, UUID.randomUUID().toString().replaceAll("-", "").substring(3, 20));
        MDC.put(MODULE_NAME, moduleLog.value());
        log.info("请求报文：{}", JSONObject.toJSONString(pPoint.getArgs()));
        Object result = pPoint.proceed();
        log.info("返回报文：{}", JSONObject.toJSONString(result));
        return result;
    }

}