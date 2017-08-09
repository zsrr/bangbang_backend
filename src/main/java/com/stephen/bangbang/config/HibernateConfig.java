package com.stephen.bangbang.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {


    public static class HibernatePropertiesConfig {
        private final String dialect;
        private final String hbm2ddl_auto;

        public HibernatePropertiesConfig(String dialect, String hbm2ddl_auto) {
            this.dialect = dialect;
            this.hbm2ddl_auto = hbm2ddl_auto;
        }

        public String getDialect() {
            return dialect;
        }

        public String hbm2ddl_auto() {
            return hbm2ddl_auto;
        }
    }

    @Bean
    LocalSessionFactoryBean factoryBean(DataSource dataSource, HibernatePropertiesConfig propertiesConfig) {
        LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(dataSource);
        sfb.setAnnotatedPackages("com.stephen.bangbang.domain");
        sfb.setPackagesToScan("com.stephen.bangbang.domain");
        Properties properties = new Properties();
        properties.put("hibernate.dialect", propertiesConfig.getDialect());
        properties.put("hibernate.hbm2ddl.auto", propertiesConfig.hbm2ddl_auto());
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.cache.use_second_level_cache", "true");
        properties.put("hibernate.cache.use_query_cache", "true");
        properties.put("hibernate.cache.region.factory_class", "net.sf.ehcache.hibernate.SingletonEhCacheRegionFactory");
        properties.put("net.sf.ehcache.configurationResourceName", "classpath:config/ehcache.xml");
        properties.put("hibernate.cache.use_structured_entries", "false");
        properties.put("hibernate.generate_statistics", "true");
        sfb.setHibernateProperties(properties);
        return sfb;
    }

    @Bean
    @Profile("production")
    public HibernatePropertiesConfig productionDialect() {
        return new HibernatePropertiesConfig("org.hibernate.dialect.MySQL57Dialect", "update");
    }

    @Bean
    @Profile("test")
    public HibernatePropertiesConfig testDialect() {
        return new HibernatePropertiesConfig("org.hibernate.dialect.H2Dialect", "create");
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
