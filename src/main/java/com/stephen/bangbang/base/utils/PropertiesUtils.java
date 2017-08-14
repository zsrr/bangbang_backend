package com.stephen.bangbang.base.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    private Properties properties;

    public PropertiesUtils() {
        this.properties = new Properties();
    }

    public PropertiesUtils(Properties properties) {
        this.properties = properties;
    }

    // 加载类路径下的
    public boolean load(String filePath) {
        try {
            properties.load(this.getClass().getResourceAsStream(filePath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
