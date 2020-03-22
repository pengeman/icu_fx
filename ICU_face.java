package org.peng.icu;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.peng.icu.action.SR;
import org.peng.icu.rabbitmq.tran.Rec;
import org.peng.icu.rabbitmq.tran.RecListener;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ICU_face extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private File[] searchFile(File folder, String keyWord) {

        System.out.println("folder is -> " + folder.getAbsolutePath());
        File[] subFolders = folder.listFiles(new FileFilter() {// 运用内部匿名类获得文件
            @Override
            public boolean accept(File pathname) {// 实现FileFilter类的accept方法

                if (pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().contains(keyWord.toLowerCase()))) {
                    // 目录或文件包含关键字
                    return true;
                }

                return false;
            }
        });

        List<File> result = new ArrayList<File>();// 声明一个集合
        for (int i = 0; i < subFolders.length; i++) {// 循环显示文件夹或文件
            if (subFolders[i].isFile()) {// 如果是文件则将文件添加到结果列表中
                result.add(subFolders[i]);
                break;
            } else {// 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                File[] foldResult = searchFile(subFolders[i], keyWord);
                for (int j = 0; j < foldResult.length; j++) {// 循环显示文件
                    result.add(foldResult[j]);// 文件保存到集合中
                    break;
                }
            }
        }

        File files[] = new File[result.size()];// 声明文件数组，长度为集合的长度
        result.toArray(files);// 集合数组化
        return files;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        String userdir = System.getProperty("user.dir");
        String keyWord = "ICU_face.fxml"; //ICU_face.fxml
        File file = new File(userdir);

        File[] files = searchFile(file, keyWord);
        System.out.println(files[0]);
        FXMLLoader loader = new FXMLLoader(files[0].toURI().toURL());
        Parent root = loader.load(); //FXMLLoader.load(files[0].toURI().toURL());
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 575);
        primaryStage.setScene(scene);
        primaryStage.show();
        ICUFaceController controller = loader.getController();

        String sendQue = org.peng.icu.rabbitmq.utils.RabbitUtil.getsendQueueName();
        String recQue = org.peng.icu.rabbitmq.utils.RabbitUtil.getrecQueueName();

        controller.getTextArea().setText("");
        controller.getTextArea().appendText("启动完成\n");
        controller.getTextArea().appendText("正在"+sendQue+"通道上发送 ，");
        controller.getTextArea().appendText(" 在"+recQue+"通道上接受\n");

        new Thread(new Runnable() {

            @Override
            public void run() {
                Rec.rec(new RecListener() {
                    @Override
                    public void msgRec(byte[] msg) {
                        controller.getTextArea().appendText(new String(msg));
                    }

                    @Override
                    public void docRec(byte[] bytes) {

                    }
                });
            }
        }).start();

// 窗口关闭时触发
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
