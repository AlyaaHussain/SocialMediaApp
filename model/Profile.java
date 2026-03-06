package model;

import java.time.LocalDateTime;

public class Profile {

    private int id;
    private String bio;
    private int userId;
    private String profilePicture;
    private LocalDateTime updatedAt;

    public Profile(int userId, String bio, String profilePicture) {
        this.userId = userId;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }

    public int getUserId() { return userId; }
    public String getBio() { return bio; }
    public String getProfilePicture() { return profilePicture; }
}