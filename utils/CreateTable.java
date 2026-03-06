package utils;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            // إنشاء قاعدة البيانات
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS social_media");
            System.out.println("✅ Database social_media تم إنشاؤها أو موجودة بالفعل.");

            // استخدام قاعدة البيانات
            stmt.executeUpdate("USE social_media");
            System.out.println("✅ تم التبديل إلى قاعدة البيانات social_media.");

            // 1. جدول users
            String users = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(255), " +
                    "email VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL" +
                    ");";
            stmt.executeUpdate(users);
            System.out.println("✅ Table users created.");

            // 2. جدول profiles
            String profiles = "CREATE TABLE IF NOT EXISTS profiles (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT UNIQUE, " +
                    "bio VARCHAR(255), " +
                    "profile_picture VARCHAR(255), " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ");";
            stmt.executeUpdate(profiles);
            System.out.println("✅ Table profiles created.");

            // 3. جدول posts
            String posts = "CREATE TABLE IF NOT EXISTS posts (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT, " +
                    "content TEXT, " +
                    "privacy_level ENUM('public', 'friends', 'private'), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ");";
            stmt.executeUpdate(posts);
            System.out.println("✅ Table posts created.");

            // 4. جدول post_images
            String post_images = "CREATE TABLE IF NOT EXISTS post_images (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "post_id INT NOT NULL, " +
                    "image_path VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (post_id) REFERENCES posts(id)" +
                    ");";
            stmt.executeUpdate(post_images);
            System.out.println("✅ Table post_images created.");

            // 5. جدول comments
            String comments = "CREATE TABLE IF NOT EXISTS comments (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT, " +
                    "post_id INT, " +
                    "content TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (post_id) REFERENCES posts(id)" +
                    ");";
            stmt.executeUpdate(comments);
            System.out.println("✅ Table comments created.");

            // 6. جدول likes
            String likes = "CREATE TABLE IF NOT EXISTS likes (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT, " +
                    "post_id INT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(user_id, post_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (post_id) REFERENCES posts(id)" +
                    ");";
            stmt.executeUpdate(likes);
            System.out.println("✅ Table likes created.");

            // 7. جدول friends
            String friends = "CREATE TABLE IF NOT EXISTS friends (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT, " +
                    "friend_id INT, " +
                    "status ENUM('pending','accepted','rejected') DEFAULT 'pending', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (friend_id) REFERENCES users(id), " +
                    "UNIQUE(user_id, friend_id)" +
                    ");";
            stmt.executeUpdate(friends);
            System.out.println("✅ Table friends created.");

            // 8. جدول notifications
            String notifications = "CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT, " +
                    "sender_id INT, " +
                    "type ENUM('like','comment','friend_request') NOT NULL, " +
                    "post_id INT NULL, " +
                    "comment_id INT NULL, " +
                    "likes_id INT NULL, " +
                    "friendship_id INT NULL, " +
                    "is_read BOOLEAN DEFAULT FALSE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id), " +
                    "FOREIGN KEY (sender_id) REFERENCES users(id), " +
                    "FOREIGN KEY (post_id) REFERENCES posts(id), " +
                    "FOREIGN KEY (comment_id) REFERENCES comments(id), " +
                    "FOREIGN KEY (likes_id) REFERENCES likes(id), " +
                    "FOREIGN KEY (friendship_id) REFERENCES friends(id)" +
                    ");";
            stmt.executeUpdate(notifications);
            System.out.println("✅ Table notifications created.");

            System.out.println("\n🎉 تم إنشاء جميع الجداول بنجاح!");

        } catch (Exception e) {
            System.err.println("❌ خطأ أثناء إنشاء الجداول:");
            e.printStackTrace();
        }
    }
}