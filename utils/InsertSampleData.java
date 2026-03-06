package utils;

import dao.PostDAO;
import dao.PostImageDAO;
import dao.ProfileDAO;
import dao.UserDAO;
import model.Post;
import model.PostImage;
import model.Profile;
import model.User;

public class InsertSampleData {

    // يمكنك تشغيل هذا الملف كبرنامج مستقل (Run As Java Application) لمرة واحدة فقط
    public static void main(String[] args) {
        insertData();
    }

    public static void insertData() {
        try {
            UserDAO userDAO = new UserDAO();
            ProfileDAO profileDAO = new ProfileDAO();
            PostDAO postDAO = new PostDAO();
            PostImageDAO postImageDAO = new PostImageDAO();

            System.out.println("Starting to insert sample data...");

            // 1. إضافة المستخدمين
            User u1 = new User("Ahmed Ali", "ahmed@test.com", "123456");
            User u2 = new User("Sara Khaled", "sara@test.com", "123456");
            User u3 = new User("Omar Zaid", "omar@test.com", "123456");
            
            userDAO.register(u1);
            userDAO.register(u2);
            userDAO.register(u3);

            // جلب المستخدمين بعد إنشائهم للحصول على الـ ID الخاص بهم
            User createdU1 = userDAO.login("ahmed@test.com", "123456");
            User createdU2 = userDAO.login("sara@test.com", "123456");
            User createdU3 = userDAO.login("omar@test.com", "123456");

            if (createdU1 != null && createdU2 != null && createdU3 != null) {
                
                // 2. إضافة الملفات الشخصية (البروفايل) مع الصورة الافتراضية
                // تأكد أن الصورة موجودة فعلياً في مجلد المشروع الخارجي أو داخل مجلد resources
                String defaultProfileImage = "resources/profile.png"; 
                
                profileDAO.createProfile(new Profile(createdU1.getId(), "Software Developer from Egypt.", defaultProfileImage));
                profileDAO.createProfile(new Profile(createdU2.getId(), "Graphic Designer & Artist.", defaultProfileImage));
                profileDAO.createProfile(new Profile(createdU3.getId(), "Coffee Lover & Tech Enthusiast.", defaultProfileImage));

                // 3. إضافة المنشورات (Posts) لاختبار التمرير (Scrolling)
                int post1 = postDAO.createPost(new Post(createdU1.getId(), "Hello World! This is my first post on this awesome app.", "Public"));
                int post2 = postDAO.createPost(new Post(createdU2.getId(), "ماذا لو أن ما تسعى إليه يعلم أنك تسعى إليه ولكن لا يريدك", "Public"));
                int post3 = postDAO.createPost(new Post(createdU3.getId(), "Does anyone know a good tutorial for JavaFX?", "Friends"));
                int post4 = postDAO.createPost(new Post(createdU1.getId(), "Here is another post to test the scrolling feature. Scroll down!", "Public"));

                // 4. إضافة صور للمنشورات (روابط عشوائية كأمثلة، يمكنك استبدالها بصور حقيقية موجودة على جهازك)
                if (post2 != -1) {
                    // يمكنك وضع مسار صورة حقيقي من جهازك هنا لتجربة ظهورها
                    postImageDAO.addPostImage(new PostImage(post2, "resources/1.png"));
                }
                
                if (post4 != -1) {
                    postImageDAO.addPostImage(new PostImage(post4, "resources/2.png"));
                }

                System.out.println("Sample Data Inserted Successfully!");
            }

        } catch (Exception e) {
            System.err.println("An error occurred. Maybe the data already exists?");
            e.printStackTrace();
        }
    }
}