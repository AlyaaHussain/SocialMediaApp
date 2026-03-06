package dao;

import model.Like;
import utils.DBConnection;

import java.sql.*;

public class LikeDAO {

    public void addLike(Like like) throws SQLException {
        String sql = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, like.getUserId());
            stmt.setInt(2, like.getPostId());
            stmt.executeUpdate();
        }
    }

    public void removeLike(Like like) throws SQLException {
        String sql = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, like.getUserId());
            stmt.setInt(2, like.getPostId());
            stmt.executeUpdate();
        }
    }
 // إضافة هذه الدالة لجلب عدد الإعجابات
    public int getLikesCount(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // إضافة هذه الدالة لمعرفة ما إذا كان المستخدم الحالي قد أعجب بالبوست (لتلوين الزر)
    public boolean isPostLikedByUser(int postId, int userId) throws SQLException {
        String sql = "SELECT * FROM likes WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}