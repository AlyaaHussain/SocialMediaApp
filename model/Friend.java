package model;

import java.time.LocalDateTime;

public class Friend {

    private int id;
    private int userId;
    private int friendId;
    private String status;
    private LocalDateTime createdAt;

    public Friend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = "pending";
    }

    public int getUserId() { return userId; }
    public int getFriendId() { return friendId; }
    public String getStatus() { return status; }
   

    public void setStatus(String status) {
        this.status = status;
    }
}