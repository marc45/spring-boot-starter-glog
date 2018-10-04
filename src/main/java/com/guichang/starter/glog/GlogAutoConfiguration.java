package com.guichang.starter.glog;

import com.guichang.starter.glog.annotation.ModuleLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * glog配置类
 *
 * @author guichang
 */

@Configuration
@EnableConfigurationProperties(value = GlogProperties.class)
@ConditionalOnClass(ModuleLog.class)
@ConditionalOnProperty(prefix = "glog", value = "enable", matchIfMissing = true)
public class GlogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ModuleLogAspect.class)
    @ConditionalOnProperty(prefix = "glog", name = "aspect", havingValue = "true", matchIfMissing = true)
    public ModuleLogAspect moduleLogAspect() {
        return new ModuleLogAspect();
    }
}


