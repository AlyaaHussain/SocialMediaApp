package controller;

import dao.PostDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Post;

public class PostController {

    @FXML
    private TextArea contentField;

    @FXML
    private ChoiceBox<String> privacyBox;

    private PostDAO postDAO = new PostDAO();

    public void initialize(){

        privacyBox.getItems().addAll(
                "public",
                "friends",
                "private"
        );

    }

    @FXML
    private void createPost(){

        try{

            Post post = new Post(
                    1,
                    contentField.getText(),
                    privacyBox.getValue()
            );

            postDAO.createPost(post);

            contentField.clear();

        }catch(Exception e){

            e.printStackTrace();

        }

    }

}