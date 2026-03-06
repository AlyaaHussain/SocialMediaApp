package social_media; // قم بتغيير اسم الحزمة (Package) إذا كانت مختلفة لديك

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. تحميل واجهة تسجيل الدخول كأول شاشة تظهر للمستخدم
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);

            // 2. إعداد عنوان النافذة
            primaryStage.setTitle("Social Media App");

            // 3. إضافة أيقونة التطبيق (اللوجو)
            try {
                // نبحث عن اللوجو في مجلد الموارد
                Image icon = new Image(getClass().getResourceAsStream("/resources/logo.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("تنبيه: لم يتم العثور على ملف logo.png. تأكد من وضعه في المسار الصحيح.");
            }

            // 4. إعدادات النافذة النهائية وعرضها
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // منع تغيير حجم النافذة للحفاظ على جمالية التصميم
            primaryStage.show();

        } catch(Exception e) {
            System.err.println("حدث خطأ أثناء تشغيل التطبيق:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // تشغيل تطبيق JavaFX
        launch(args);
    }
}