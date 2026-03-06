package controller;

import dao.CommentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.Comment;

public class CommentController {

    @FXML
    private TextField commentField;

    private CommentDAO commentDAO = new CommentDAO();

    @FXML
    private void addComment(){

        try{

            Comment comment = new Comment(
                    1,
                    1,
                    commentField.getText()
            );

            commentDAO.addComment(comment);

            commentField.clear();

        }catch(Exception e){

            e.printStackTrace();

        }

    }

}