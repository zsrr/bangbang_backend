package com.stephen.bangbang.config;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class JPushConfig {
    @Bean
    public JPushClient jPushClient() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config/jpush.properties"));

            ClientConfig clientConfig = ClientConfig.getInstance();
            // 测试环境下
            clientConfig.setApnsProduction(false);
            clientConfig.setConnectionTimeout(10 * 1000);
            clientConfig.setMaxRetryTimes(5);

            return new JPushClient(properties.getProperty("secret"), properties.getProperty("appKey"), null, clientConfig);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
