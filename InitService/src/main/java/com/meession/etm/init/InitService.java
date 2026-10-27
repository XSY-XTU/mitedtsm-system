package com.meession.etm.init;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * 初始化服务 - 用于初始化TDengine数据库
 * 纯Java程序，不依赖Spring Boot
 */
public class InitService {

    private static final String DEFAULT_HOST = "tdengine";
    private static final int DEFAULT_PORT = 6041;
    private static final String DATABASE_NAME = "mitedtsm_database";
    private static final int MAX_RETRIES = 30;
    private static final int RETRY_INTERVAL_SECONDS = 2;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("初始化服务 - TDengine 数据库初始化器");
        System.out.println("==========================================");

        String host = getEnvOrDefault("TDENGINE_HOST", DEFAULT_HOST);
        int port = Integer.parseInt(getEnvOrDefault("TDENGINE_PORT", String.valueOf(DEFAULT_PORT)));

        System.out.println("[配置] TDengine 主机: " + host);
        System.out.println("[配置] TDengine 端口: " + port);
        System.out.println("[配置] 数据库名称: " + DATABASE_NAME);

        // 构建JDBC URL（使用REST API方式，无需客户端库）
        String jdbcUrl = String.format("jdbc:TAOS-RS://%s:%d/?user=root&password=taosdata", host, port);

        System.out.println("[步骤 1] 等待 TDengine 服务就绪...");

        Connection connection = null;
        int retryCount = 0;

        // 重试连接TDengine
        while (retryCount < MAX_RETRIES) {
            try {
                Class.forName("com.taosdata.jdbc.rs.RestfulDriver");
                connection = DriverManager.getConnection(jdbcUrl);
                System.out.println("[步骤 1] 成功连接到 TDengine。");
                break;
            } catch (Exception e) {
                retryCount++;
                System.out.println("[步骤 1] 等待 TDengine 启动... (" + retryCount + "/" + MAX_RETRIES + ")");
                if (retryCount < MAX_RETRIES) {
                    try {
                        TimeUnit.SECONDS.sleep(RETRY_INTERVAL_SECONDS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (connection == null) {
            System.err.println("[错误] 重试 " + MAX_RETRIES + " 次后仍无法连接到 TDengine。");
            System.exit(1);
        }

        try {
            Statement statement = connection.createStatement();

            // 创建数据库
            System.out.println("[步骤 2] 创建数据库 '" + DATABASE_NAME + "'...");
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            statement.execute(createDbSql);
            System.out.println("[步骤 2] 数据库创建成功。");

            // 验证数据库是否存在
            System.out.println("[步骤 3] 验证数据库...");
            String showDbsSql = "SHOW DATABASES";
            ResultSet rs = statement.executeQuery(showDbsSql);
            boolean databaseExists = false;
            while (rs.next()) {
                if (DATABASE_NAME.equals(rs.getString("name"))) {
                    databaseExists = true;
                    break;
                }
            }
            rs.close();

            if (databaseExists) {
                System.out.println("[步骤 3] 数据库验证通过。");
            } else {
                System.err.println("[错误] 数据库验证失败。");
                System.exit(1);
            }

            // 使用数据库验证
            System.out.println("[步骤 4] 测试数据库访问...");
            statement.execute("USE " + DATABASE_NAME);
            System.out.println("[步骤 4] 数据库访问测试通过。");

            statement.close();
            connection.close();

            System.out.println("==========================================");
            System.out.println("初始化服务执行完成。");
            System.out.println("==========================================");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("[错误] 初始化失败: " + e.getMessage());
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignored) {
            }
            System.exit(1);
        }
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
