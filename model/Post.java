package model;

import java.time.LocalDateTime;

public class Post {

    private int id;
    private int userId;
    private String content;
    private String privacyLevel;
    private LocalDateTime createdAt;

    public Post(int userId, String content, String privacyLevel) {
        this.userId = userId;
        this.content = content;
        this.privacyLevel = privacyLevel;
    }
    public void setId(int id) { this.id = id; }
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getPrivacyLevel() { return privacyLevel; }
}