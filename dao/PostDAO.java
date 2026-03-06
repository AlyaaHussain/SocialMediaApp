package dao;

import model.Post;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    // جعلناها تُرجع رقم الـ ID للمنشور الجديد لنستخدمه في ربط الصور
    public int createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (user_id, content, privacy_level, created_at) VALUES (?, ?, ?, NOW())";
        // RETURN_GENERATED_KEYS تجلب الـ id الذي تم إنشاؤه تلقائياً في MySQL
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getPrivacyLevel());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // إرجاع الـ id
                }
            }
        }
        return -1;
    }

    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getString("privacy_level")
                );
                post.setId(rs.getInt("id")); // ضروري جداً!
                posts.add(post);
            }
        }
        return posts;
    }

    public List<Post> getPostsByUserId(int userId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post(
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getString("privacy_level")
                );
                post.setId(rs.getInt("id")); // ضروري جداً!
                posts.add(post);
            }
        }
        return posts;
    }
    
    public List<Post> getPostsPaginated(int limit, int offset, int currentUserId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE privacy_level = 'Public' " +
                     "OR user_id = ? " +
                     "OR (privacy_level = 'Friends' AND user_id IN (" +
                     "   SELECT friend_id FROM friends WHERE user_id = ? AND status = 'accepted' " +
                     "   UNION " +
                     "   SELECT user_id FROM friends WHERE friend_id = ? AND status = 'accepted'" +
                     ")) ORDER BY created_at DESC LIMIT ? OFFSET ?";
                     
        try (Connection conn = utils.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, currentUserId);
            stmt.setInt(3, currentUserId);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post(rs.getInt("user_id"), rs.getString("content"), rs.getString("privacy_level"));
                post.setId(rs.getInt("id"));
                posts.add(post);
            }
        }
        return posts;
    }
}