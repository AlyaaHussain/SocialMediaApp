package dao;

import model.Profile;
import utils.DBConnection;

import java.sql.*;

public class ProfileDAO {

    public void createProfile(Profile profile) throws SQLException {
        String sql = "INSERT INTO profiles (user_id, bio, profile_picture, updated_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getBio());
            stmt.setString(3, profile.getProfilePicture());
            stmt.executeUpdate();
        }
    }

    public void updateProfile(Profile profile) throws SQLException {
        String sql = "UPDATE profiles SET bio = ?, profile_picture = ?, updated_at = NOW() WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getBio());
            stmt.setString(2, profile.getProfilePicture());
            stmt.setInt(3, profile.getUserId());
            stmt.executeUpdate();
        }
    }

    public Profile getProfileByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Profile(
                        rs.getInt("user_id"),
                        rs.getString("bio"),
                        rs.getString("profile_picture")
                );
            }
        }
        return null;
    }
}