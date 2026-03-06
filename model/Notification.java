package model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private int userId;
    private int senderId;
    private String type;
    private boolean isRead;
    private Integer postId;
    private Integer commentId;
    private Integer likesId;
    private Integer friendshipId;
    private LocalDateTime createdAt;

    public Notification(int userId, int senderId, String type) {
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.isRead = false;
    }

    public int getUserId() { return userId; }
    public int getSenderId() { return senderId; }
    public String getType() { return type; }
}