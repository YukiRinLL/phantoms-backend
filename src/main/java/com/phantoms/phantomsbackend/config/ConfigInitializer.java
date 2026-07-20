package com.phantoms.phantomsbackend.config;

/**
 * 系统配置初始化器（已停用）
 * 
 * 原设计用途：在服务首次启动时，自动检查并初始化数据库中的系统配置项
 * 初始化逻辑包括：群ID配置、FF14新闻配置、房屋监控配置、监控相关配置等
 * 
 * 已停用原因：用户希望通过手动执行 SQL 脚本或 API 来初始化配置
 * 
 * 如果需要重新启用，请取消注释以下代码并实现 run() 方法：
 * 
 * @Component
 * public class ConfigInitializer implements CommandLineRunner {
 * 
 *     @Autowired
 *     private SystemConfigRepository systemConfigRepository;
 * 
 *     @Override
 *     public void run(String... args) throws Exception {
 *         // 初始化逻辑
 *     }
 * }
 * 
 * 初始化 SQL 脚本位置：doc/init-config.sql
 */
public class ConfigInitializer {
    // 空类，保留文件结构便于未来重新启用
}
