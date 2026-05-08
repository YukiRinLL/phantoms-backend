package com.phantoms.phantomsbackend.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

import javax.sql.DataSource;

import com.phantoms.phantomsbackend.pojo.entity.primary.AuthUser;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.ChatRecord;
import com.phantoms.phantomsbackend.pojo.entity.primary.ExpeditionaryTeam;
import com.phantoms.phantomsbackend.pojo.entity.primary.Image;
import com.phantoms.phantomsbackend.pojo.entity.primary.Message;
import com.phantoms.phantomsbackend.pojo.entity.primary.Password;
import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.pojo.entity.primary.SystemConfig;
import com.phantoms.phantomsbackend.pojo.entity.primary.User;
import com.phantoms.phantomsbackend.pojo.entity.primary.onebot.UserMessage;
import com.phantoms.phantomsbackend.pojo.entity.primary.UserProfile;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager",
        basePackages = {"com.phantoms.phantomsbackend.repository.primary"}
)
public class PrimaryDataSourceConfig {

    @Value("${spring.datasource.primary.url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryPassword;

    @Value("${spring.datasource.primary.driver-class-name}")
    private String primaryDriverClassName;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private long hikariIdleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private long hikariMaxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private long hikariConnectionTimeout;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int hikariMaximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int hikariMinimumIdle;

    @Value("${spring.datasource.primary.hikari.pool-name}")
    private String hikariPoolName;

    @Value("${spring.datasource.hikari.data-source-properties.preparedStatementCacheQueries}")
    private int hikariPreparedStatementCacheQueries;

    @Value("${spring.datasource.hikari.data-source-properties.preparedStatementCacheSizeMiB}")
    private int hikariPreparedStatementCacheSizeMiB;

    @Value("${spring.datasource.hikari.data-source-properties.prepareThreshold}")
    private int hikariPrepareThreshold;

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(primaryUrl);
        hikariConfig.setUsername(primaryUsername);
        hikariConfig.setPassword(primaryPassword);
        hikariConfig.setDriverClassName(primaryDriverClassName);

        hikariConfig.setIdleTimeout(hikariIdleTimeout);
        hikariConfig.setMaxLifetime(hikariMaxLifetime);
        hikariConfig.setConnectionTimeout(hikariConnectionTimeout);
        hikariConfig.setMaximumPoolSize(hikariMaximumPoolSize);
        hikariConfig.setMinimumIdle(hikariMinimumIdle);
        hikariConfig.setPoolName(hikariPoolName);

        hikariConfig.addDataSourceProperty("preparedStatementCacheQueries", hikariPreparedStatementCacheQueries);
        hikariConfig.addDataSourceProperty("preparedStatementCacheSizeMiB", hikariPreparedStatementCacheSizeMiB);
        hikariConfig.addDataSourceProperty("prepareThreshold", hikariPrepareThreshold);

        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource dataSource) {
        Map<String, Object> properties = new java.util.HashMap<>();
        // 优化内存配置：关闭所有不必要的扫描和缓存
        properties.put("hibernate.archive.autodetection", "none");  // 完全关闭归档自动检测
        properties.put("hibernate.javax.persistence.validation.mode", "none");  // 关闭Bean Validation
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("hibernate.generate_statistics", "false");
        properties.put("hibernate.auto_quote_keyword", "false");
        
        return builder
                .dataSource(dataSource)
                // .packages("com.phantoms.phantomsbackend.pojo.entity.primary")
                .persistenceUnit("primary")
                .properties(properties)
                // 显式指定实体类，避免扫描整个JAR文件
                .packages(
                        AuthUser.class,
                        ChatRecord.class,
                        ExpeditionaryTeam.class,
                        Image.class,
                        Message.class,
                        Password.class,
                        Recruitment.class,
                        SystemConfig.class,
                        User.class,
                        UserMessage.class,
                        UserProfile.class
                )
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}