package controller;

import dao.LikeDAO;
import model.Like;

public class LikeController {

    private LikeDAO likeDAO = new LikeDAO();

    public void likePost(int userId,int postId){

        try{

            Like like = new Like(userId,postId);

            likeDAO.addLike(like);

        }catch(Exception e){

            e.printStackTrace();

        }

    }

}