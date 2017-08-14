package com.stephen.bangbang.config;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import com.stephen.bangbang.Constants;
import com.stephen.bangbang.base.utils.PropertiesUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JPushConfig {
    @Bean
    public JPushClient jPushClient() {
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        propertiesUtils.load("/config/jpush.properties");

        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setApnsProduction(Constants.JIHUANG_CLIENT_IN_PRODUCTION);
        clientConfig.setConnectionTimeout(Constants.JIGUANG_CLIENT_CONNECTION_TIME_OUT);
        clientConfig.setMaxRetryTimes(Constants.JIGUANG_CLIENT_MAX_RETRY);

        return new JPushClient(propertiesUtils.getProperty("secret"),
                propertiesUtils.getProperty("appKey"), null, clientConfig);
    }
}
