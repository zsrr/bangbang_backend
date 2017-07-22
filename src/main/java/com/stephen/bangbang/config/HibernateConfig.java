package com.stephen.bangbang.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
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
        //sfb.setPackagesToScan("com.stephen.bangbang.domain");
        sfb.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        Properties properties = new Properties();
        properties.put("dialect", propertiesConfig.getDialect());
        properties.put("hbm2ddl.auto", propertiesConfig.hbm2ddl_auto());
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
