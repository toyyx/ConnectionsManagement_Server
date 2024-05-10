package com.example.connection_server;

import jakarta.servlet.http.HttpServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager  {

    private static String username;//数据库用户名
    private static String password;//数据库连接密码
    private static String url;//数据库连接URL
    private static Connection connection;//数据库连接

    //获取数据库相关信息
    public static void init() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/usr/java/tomcat/apache-tomcat-10.1.19/webapps/connection_server-1.0-SNAPSHOT/WEB-INF/classes/config"));
            url = properties.getProperty("MySQL_URL");
            username = properties.getProperty("DB_username");
            password = properties.getProperty("DB_password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //连接数据库
    public static Connection getConnection() {
        init();
        System.out.println("尝试连接数据库："+url);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (connection==null){
            System.out.println("数据库连接失败");
        }else{
            System.out.println("数据库连接成功");
        }
        return connection;
    }

    //关闭连接
    public static void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeAll(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}