package dao;

import model.PostImage;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostImageDAO {

    // إضافة صورة جديدة للبوست
    public void addPostImage(PostImage postImage) throws SQLException {
        String sql = "INSERT INTO post_images (post_id, image_path) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postImage.getPostId());
            stmt.setString(2, postImage.getImagePath());
            stmt.executeUpdate();
        }
    }

    // جلب كل الصور لبوست معين
    public List<PostImage> getImagesByPostId(int postId) throws SQLException {
        List<PostImage> images = new ArrayList<>();
        String sql = "SELECT * FROM post_images WHERE post_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PostImage img = new PostImage(
                        rs.getInt("post_id"),
                        rs.getString("image_path")
                );
                img.setId(rs.getInt("id"));
                images.add(img);
            }
        }
        return images;
    }
}