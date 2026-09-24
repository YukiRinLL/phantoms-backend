package com.phantoms.phantomsbackend.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryDataSourceConfig.class);

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

    @Value("${spring.datasource.hikari.startup-retry.max-attempts:6}")
    private int startupRetryMaxAttempts;

    @Value("${spring.datasource.hikari.startup-retry.interval-ms:10000}")
    private long startupRetryIntervalMs;

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

        return createDataSourceWithRetry(hikariConfig);
    }

    /**
     * 创建 HikariDataSource，启动期连接失败时进行有界重试。
     * 容器冷启动资源紧张时，首次 TLS 握手可能被对端中断，重试可避免一次瞬断直接终止整个应用；
     * 重试耗尽后仍按原行为快速失败（抛出最后一次异常，由 Spring 终止启动）。
     */
    private HikariDataSource createDataSourceWithRetry(HikariConfig hikariConfig) {
        int maxAttempts = Math.max(1, startupRetryMaxAttempts);
        long intervalMs = Math.max(0L, startupRetryIntervalMs);
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HikariDataSource dataSource = new HikariDataSource(hikariConfig);
                if (attempt > 1) {
                    LOGGER.info("{} - DataSource initialized successfully on attempt {}/{}",
                            hikariPoolName, attempt, maxAttempts);
                }
                return dataSource;
            } catch (RuntimeException ex) {
                lastFailure = ex;
                if (attempt >= maxAttempts) {
                    break;
                }
                LOGGER.warn("{} - DataSource initialization attempt {}/{} failed, retrying in {} ms: {}",
                        hikariPoolName, attempt, maxAttempts, intervalMs, ex.getMessage());
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }
        throw lastFailure;
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            @Qualifier("primaryDataSource") DataSource dataSource) {
        Map<String, Object> properties = new java.util.HashMap<>();
        // 优化内存配置：关闭所有不必要的扫描和缓存
        properties.put("hibernate.archive.autodetection", "none");  // 完全关闭归档自动检测
        properties.put("hibernate.javax.persistence.validation.mode", "none");  // 关闭Bean Validation
        properties.put("hibernate.cache.use_second_level_cache", "false");
        properties.put("hibernate.cache.use_query_cache", "false");
        properties.put("hibernate.generate_statistics", "false");
        properties.put("hibernate.auto_quote_keyword", "false");
        // 关键：禁用归档扫描器，避免ArchiveHelper.getBytesFromInputStream()加载整个JAR
        properties.put("hibernate.archive.scanner", "org.hibernate.boot.archive.scan.internal.DisabledScanner");
        properties.put("hibernate.ejb.resource_scanner", "org.hibernate.boot.archive.scan.internal.DisabledScanner");
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPersistenceUnitName("primary");
        em.setJpaPropertyMap(properties);
        em.setPackagesToScan("com.phantoms.phantomsbackend.pojo.entity.primary");
        
        // 必须设置 JpaVendorAdapter 指定持久化提供者
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);
        
        return em;
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}