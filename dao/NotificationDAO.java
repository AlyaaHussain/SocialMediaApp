package dao;

import model.Notification;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void addNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, sender_id, type, is_read, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setInt(2, notification.getSenderId());
            stmt.setString(3, notification.getType());
            stmt.setBoolean(4, false);
            stmt.executeUpdate();
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("user_id"),
                        rs.getInt("sender_id"),
                        rs.getString("type")
                );
                notifications.add(n);
            }
        }
        return notifications;
    }
}