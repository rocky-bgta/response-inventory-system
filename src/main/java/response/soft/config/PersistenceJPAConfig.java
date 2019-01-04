
package response.soft.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class PersistenceJPAConfig {

    @Autowired
    private Environment env;

    private static HikariConfig hikariConfig = new HikariConfig();
    private static HikariDataSource hikariDataSource;
    static {
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl( "jdbc:postgresql://localhost:5432/response_electronic" );
        hikariConfig.setUsername( "postgres" );
        hikariConfig.setPassword( "postgres" );
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setConnectionTimeout(2000);
        hikariConfig.setIdleTimeout(300000);
        //hikariConfig.setSchema("inventory");
        //hikariConfig.setLeakDetectionThreshold(60000);
        hikariConfig.addDataSourceProperty( "cachePrepStmts" , "true" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048");
        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    @Bean
    //@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"response.soft.entities"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        //dataSource.setDriverClassName("org.postgresql.Driver");
        //dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/response_electronic");
        //dataSource.setUsername("postgres");
        //dataSource.setPassword("postgres");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.setProperty("hibernate.default_schema", "inventory");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults","false");

        return properties;
    }
}


