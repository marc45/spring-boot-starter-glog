package com.guichang.starter.glog;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置文件
 *
 * @author guichang
 * @date 2018/10/3
 */

@Data
@ConfigurationProperties(prefix = "glog")
public class GlogProperties {
}