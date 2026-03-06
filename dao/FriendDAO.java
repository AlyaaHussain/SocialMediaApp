package dao;

import model.Friend;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    public void addFriend(Friend friend) throws SQLException {
        String sql = "INSERT INTO friends (user_id, friend_id, status, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, friend.getUserId());
            stmt.setInt(2, friend.getFriendId());
            stmt.setString(3, friend.getStatus());
            stmt.executeUpdate();
        }
    }

    public List<Friend> getFriendsByUserId(int userId) throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT * FROM friends WHERE user_id = ? AND status = 'accepted'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Friend f = new Friend(rs.getInt("user_id"), rs.getInt("friend_id"));
                friends.add(f);
            }
        }
        return friends;
    }
 // 1. تحديث حالة الطلب (قبول أو رفض)
    public void updateStatus(int userId, int friendId, String status) throws SQLException {
        String sql = "UPDATE friends SET status = ? WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (Connection conn = utils.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId); stmt.setInt(3, friendId);
            stmt.setInt(4, friendId); stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }

    // 2. جلب الطلبات المُرسلة إليّ (المعلقة)
    public List<Integer> getPendingRequests(int myId) throws SQLException {
        List<Integer> requesters = new ArrayList<>();
        String sql = "SELECT user_id FROM friends WHERE friend_id = ? AND status = 'pending'";
        try (Connection conn = utils.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, myId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requesters.add(rs.getInt("user_id"));
            }
        }
        return requesters;
    }

    // 3. التحقق من حالة الصداقة (لتغيير شكل الزر في البحث)
    public String checkStatus(int myId, int targetId) throws SQLException {
        String sql = "SELECT status FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (Connection conn = utils.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, myId); stmt.setInt(2, targetId);
            stmt.setInt(3, targetId); stmt.setInt(4, myId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("status");
        }
        return "none";
    }
}