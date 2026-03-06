package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ✅ تم التعديل هنا: نتصل مباشرة بـ social_media
    private static final String URL = 
        "jdbc:mysql://mysql-1a0f90e0-alyaa2003hussein100-f1e6.i.aivencloud.com:22333/social_media?sslMode=REQUIRED";

    private static final String USER = "avnadmin";
    private static final String PASS = ""YOUR_PASSWORD_HERE";   
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}