package model;

import java.time.LocalDateTime;

public class Comment {

    private int id;
    private int userId;
    private int postId;
    private String content;
    private LocalDateTime createdAt;

    public Comment(int userId, int postId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

    public int getUserId() { return userId; }
    public int getPostId() { return postId; }
    public String getContent() { return content; }
}