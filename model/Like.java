package model;

public class Like {

    private int id;
    private int userId;
    private int postId;

    public Like(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public int getUserId() { return userId; }
    public int getPostId() { return postId; }
}