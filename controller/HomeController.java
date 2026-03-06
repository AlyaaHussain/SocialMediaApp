package controller;
import dao.ProfileDAO;

import model.Profile;
import dao.CommentDAO;
import dao.LikeDAO;
import dao.PostDAO;
import dao.PostImageDAO;
import dao.UserDAO;
import model.Post;
import model.PostImage;
import model.User;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;

public class HomeController {

    @FXML private Label userNameLabel;
    @FXML private TextArea postContentArea;
    @FXML private ChoiceBox<String> privacyChoiceBox;
    @FXML private VBox postsVBox;
    @FXML private Label imageStatusLabel;

    private PostDAO postDAO = new PostDAO();
    private PostImageDAO postImageDAO = new PostImageDAO();
    private UserDAO userDAO = new UserDAO(); // لجلب اسم صاحب المنشور
    private List<String> selectedImagePaths = new java.util.ArrayList<>();
    private LikeDAO likeDAO = new LikeDAO();
    private CommentDAO commentDAO = new CommentDAO();
    private final int POSTS_PER_PAGE = 5;
    private int currentOffset = 0;

    @FXML
    public void initialize() {
        // إعداد بيانات المستخدم الحالي
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText("Hi, " + currentUser.getName());
        }

        // إعداد خيارات الخصوصية
        privacyChoiceBox.getItems().addAll("Public", "Friends", "Private");
        privacyChoiceBox.setValue("Public");

        // تحميل المنشورات
        loadPosts();
    }

    @FXML
    public void handleAddImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Post Images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        // السماح باختيار عدة صور
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            for (File file : selectedFiles) {
                selectedImagePaths.add(file.getAbsolutePath());
            }
            imageStatusLabel.setText(selectedImagePaths.size() + " image(s) attached!");
        }
    }

    @FXML
    public void handleCreatePost(ActionEvent event) {
        String content = postContentArea.getText().trim();
        String privacy = privacyChoiceBox.getValue();
        User user = SessionManager.getCurrentUser();

        if (content.isEmpty() && selectedImagePaths.isEmpty()) return;

        try {
            Post newPost = new Post(user.getId(), content, privacy);
            int generatedPostId = postDAO.createPost(newPost);

            if (!selectedImagePaths.isEmpty() && generatedPostId != -1) {
                // حفظ كل الصور المتعددة في قاعدة البيانات
                for (String path : selectedImagePaths) {
                    postImageDAO.addPostImage(new PostImage(generatedPostId, path));
                }
            }

            postContentArea.clear();
            selectedImagePaths.clear();
            imageStatusLabel.setText("");
            loadPosts(); // تحديث الـ Feed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPosts() {
        postsVBox.getChildren().clear();
        User currentUser = SessionManager.getCurrentUser();

        try {
        	// قم بتمرير الـ ID الخاص بالمستخدم الحالي كعنصر ثالث
        	List<Post> posts = postDAO.getPostsPaginated(POSTS_PER_PAGE, currentOffset, currentUser.getId());
            for (Post post : posts) {
                User author = userDAO.getUserById(post.getUserId());
                String authorName = (author != null) ? author.getName() : "Unknown User";

                // --- 1. بناء الكارت الأساسي ---
                VBox card = new VBox(10);
                card.getStyleClass().add("post-card");
                card.setStyle("-fx-padding: 20; -fx-background-radius: 10; -fx-background-color: #1E1E1E;");

                Label nameLabel = new Label(authorName + " • " + post.getPrivacyLevel());
                nameLabel.setStyle("-fx-text-fill: #1DA1F2; -fx-font-weight: bold; -fx-font-size: 16px;");

                Label contentLabel = new Label(post.getContent());
                contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                contentLabel.setWrapText(true);

                card.getChildren().addAll(nameLabel, contentLabel);

                // --- 2. عرض الصور (كما فعلنا سابقاً) ---
                List<PostImage> images = postImageDAO.getImagesByPostId(post.getId());
                if (!images.isEmpty()) {
                    javafx.scene.layout.FlowPane imagePane = new javafx.scene.layout.FlowPane();
                    imagePane.setHgap(10); imagePane.setVgap(10);
                    for (PostImage img : images) {
                        File file = new File(img.getImagePath());
                        if (file.exists()) {
                            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                            imageView.setFitWidth(150);
                            imageView.setFitHeight(150);
                            imageView.setPreserveRatio(true);
                            imagePane.getChildren().add(imageView);
                        }
                    }
                    card.getChildren().add(imagePane);
                }

                // --- 3. قسم التفاعل (الإعجابات) ---
                HBox interactionBox = new HBox(15);
                interactionBox.setStyle("-fx-padding: 10 0 5 0; -fx-border-color: #333 transparent transparent transparent; -fx-border-width: 1 0 0 0;");
                
                Button likeBtn = new Button();
                int currentLikesCount = likeDAO.getLikesCount(post.getId());
                boolean isLiked = likeDAO.isPostLikedByUser(post.getId(), currentUser.getId());

                // تحديث شكل زر اللايك
                updateLikeButtonUI(likeBtn, isLiked, currentLikesCount);

                // حدث الضغط على زر اللايك
                likeBtn.setOnAction(e -> {
                    try {
                        boolean currentlyLiked = likeDAO.isPostLikedByUser(post.getId(), currentUser.getId());
                        if (currentlyLiked) {
                            likeDAO.removeLike(new model.Like(currentUser.getId(), post.getId()));
                        } else {
                            likeDAO.addLike(new model.Like(currentUser.getId(), post.getId()));
                        }
                        // تحديث الزر بعد الضغط
                        int newCount = likeDAO.getLikesCount(post.getId());
                        updateLikeButtonUI(likeBtn, !currentlyLiked, newCount);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                interactionBox.getChildren().add(likeBtn);
                card.getChildren().add(interactionBox);

                // --- 4. قسم التعليقات ---
                VBox commentsSection = new VBox(10);
                commentsSection.getStyleClass().add("comments-section");

                // عرض التعليقات السابقة
                List<model.Comment> comments = commentDAO.getCommentsByPostId(post.getId());
                for (model.Comment c : comments) {
                    User commentAuthor = userDAO.getUserById(c.getUserId());
                    String cAuthorName = (commentAuthor != null) ? commentAuthor.getName() : "User";
                    
                    VBox commentBox = new VBox(2);
                    commentBox.getStyleClass().add("comment-box");
                    Label cNameLabel = new Label(cAuthorName);
                    cNameLabel.getStyleClass().add("comment-author");
                    Label cTextLabel = new Label(c.getContent());
                    cTextLabel.getStyleClass().add("comment-text");
                    commentBox.getChildren().addAll(cNameLabel, cTextLabel);
                    commentsSection.getChildren().add(commentBox);
                }

                // حقل إضافة تعليق جديد
                HBox addCommentBox = new HBox(10);
                TextField commentInput = new TextField();
                commentInput.setPromptText("Write a comment...");
                commentInput.getStyleClass().add("comment-input");
                javafx.scene.layout.HBox.setHgrow(commentInput, javafx.scene.layout.Priority.ALWAYS);
                
                Button sendCommentBtn = new Button("Reply");
                sendCommentBtn.getStyleClass().add("btn-secondary");
                
                sendCommentBtn.setOnAction(e -> {
                    String cText = commentInput.getText().trim();
                    if (!cText.isEmpty()) {
                        try {
                            commentDAO.addComment(new model.Comment(currentUser.getId(), post.getId(), cText));
                            loadPosts(); // إعادة تحميل المنشورات لظهور التعليق فوراً
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                addCommentBox.getChildren().addAll(commentInput, sendCommentBtn);
                commentsSection.getChildren().add(addCommentBox);

                card.getChildren().add(commentsSection);
                postsVBox.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // دالة مساعدة لتحديث شكل زر اللايك
    private void updateLikeButtonUI(Button btn, boolean isLiked, int count) {
        String heart = isLiked ? "❤️" : "🤍";
        btn.setText(heart + " Like (" + count + ")");
        if (isLiked) {
            btn.getStyleClass().remove("like-btn");
            btn.getStyleClass().add("like-btn-active");
        } else {
            btn.getStyleClass().remove("like-btn-active");
            btn.getStyleClass().add("like-btn");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToProfile(ActionEvent event) {
        try {
            // تحميل شاشة البروفايل
            Parent profileRoot = FXMLLoader.load(getClass().getResource("/view/profile.fxml"));
            
            // جلب النافذة الحالية وتغيير المشهد (Scene) إليها
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
            
        } catch (Exception e) {
            System.err.println("حدث خطأ أثناء فتح صفحة البروفايل:");
            e.printStackTrace();
        }
    }
    @FXML
    public void goToFriends(ActionEvent event) {
        try {
            Parent friendsRoot = FXMLLoader.load(getClass().getResource("/view/friends.fxml"));
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(friendsRoot));
        } catch (Exception e) {
            System.err.println("حدث خطأ أثناء فتح صفحة الأصدقاء:");
            e.printStackTrace();
        }
    }
    @FXML
    public void goToNotifications(ActionEvent event) {
        try {
            Parent notificationsRoot = FXMLLoader.load(getClass().getResource("/view/notifications.fxml"));
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            stage.setScene(new Scene(notificationsRoot));
        } catch (Exception e) {
            System.err.println("حدث خطأ أثناء فتح صفحة الإشعارات:");
            e.printStackTrace();
        }
    }
}