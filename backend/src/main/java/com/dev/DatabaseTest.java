package com.dev;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://123.2.158.180:3306/sale");
        dataSource.setUsername("root");
        dataSource.setPassword("stec@mysql#18");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        // 연결 테스트
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Database connected successfully :)");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database.");
        } finally {
            dataSource.close(); // 리소스 정리
        }
    }
}
