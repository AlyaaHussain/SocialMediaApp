package model;

public class PostImage {

    private int id;
    private int postId;
    private String imagePath;

    public PostImage(int postId, String imagePath) {
        this.postId = postId;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public int getPostId() { return postId; }
    public String getImagePath() { return imagePath; }

    public void setId(int id) { this.id = id; }
}