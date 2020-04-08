package org.peng.icu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import org.peng.icu.action.SR;
import org.peng.icu.rabbitmq.tran.Rec;
import org.peng.icu.rabbitmq.tran.RecListener;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ICU_face extends Application {
    Logger log = Logger.getLogger(ICU_face.class);

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
        //文件没有找到
        if (files[0] == null){
            log.error("ICU_face.fxml" + "文件没有找到");
            return;
        }
        FXMLLoader loader = new FXMLLoader(files[0].toURI().toURL());
        Parent root = loader.load();        //Parent root = FXMLLoader.load(files[0].toURI().toURL());
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 575);
        primaryStage.setScene(scene);
        primaryStage.show();
        ICUFaceController controller = loader.getController();

        String sendQue = org.peng.icu.rabbitmq.utils.RabbitUtil.getsendQueueName();
        String recQue = org.peng.icu.rabbitmq.utils.RabbitUtil.getrecQueueName();

//        controller.getTextArea().setText("");
//        controller.getTextArea().appendText("启动完成\n");
//        controller.getTextArea().appendText("正在"+sendQue+"通道上发送 ，");
//        controller.getTextArea().appendText(" 在"+recQue+"通道上接受\n");
        String txt = "正在" + sendQue + "通道上发送  在" + recQue + "通道上接受";
        ListView lv = controller.getListView2();
        ObservableList obl = lv.getItems();
        //obl.addAll("程序启动完成", txt);
        obl.add("程序启动完成");
        obl.add(txt);

        Rec.rec(new RecListener() {
            @Override
            public void msgRec(byte[] msg) {
                String curcharacterSet = System.getProperty("file.encoding");
                String newstr = new String(msg,Charset.forName("utf-8"));
                Charset charset = Charset.forName(curcharacterSet);
                ByteBuffer bytebuffer = charset.encode(newstr);
                CharBuffer charBuffer = charset.decode(bytebuffer);
                newstr = charBuffer.toString();

                ICU_face.this.showMSG(newstr,controller);
            }

            @Override
            public void docRec(byte[] bytes, String filename) {
                ICU_face.this.showDOC(bytes,filename,controller);
            }
        });


// 窗口关闭时触发
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    // 显示那些接收到的文本信息
    private void showMSG(String msg, ICUFaceController controller){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ListView lv = controller.getListView2();
                ObservableList obl = lv.getItems();

                obl.add(msg);
                lv.getSelectionModel().selectLast();
                int lineindex = lv.getSelectionModel().getSelectedIndex();
                lv.scrollTo(lineindex);
            }
        });
    }
    private void showDOC(byte[] bytes,String filename ,ICUFaceController controller ){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (filename == null || filename.trim().getBytes().length < 16) {
                    String tmpfilename = System.currentTimeMillis()+"noName";
                    InputStream is = new ByteArrayInputStream(bytes);
                    javafx.scene.image.Image image = new Image(is, 80, 80, true, true);
                    ListView lv = controller.getListView2();
                    ObservableList obl = lv.getItems();
                    ImageView imageView = new ImageView(image);

                    obl.add(imageView);

                    saveFile(bytes,"out",tmpfilename);
                    String msg = "收到一个图片，单击下方名称，查看全图";
                    obl.add(new String(msg));
                    msg = "-->" + tmpfilename;
                    obl.add(msg);
                } else {
                    String filename2 = filename.trim();
                    String msg = "收到一个文件，保存在: " + filename2;
                    saveFile(bytes, "out", filename2);
                    ListView lv = controller.getListView2();
                    ObservableList obl = lv.getItems();
                    obl.add(new String(msg));
                }
            }
        });

    }

    private void imageView_mouse_clicked(ListChangeListener.Change event, Image image) {
        // 图片被单
        System.out.println("imageView_mouse_clicked");
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        Label imageLable = new Label();
        imageLable.setPrefHeight(imageHeight);
        imageLable.setPrefWidth(imageWidth);
        imageLable.setGraphic(new ImageView(image));
    }

    private void saveFile(byte[] fileByte, String savePath, String fileName) {
        try {
            byte[] fileData = fileByte;
            log.info("file saved in " + savePath + "/" + fileName);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(savePath + "/" + fileName));

            out.write(fileData);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] ToGBK(String str){
        Charset charset_gbk = Charset.forName("gbk");
        ByteBuffer bytebuffer = charset_gbk.encode(str);
        byte[] bs = bytebuffer.array();
        return bs;
    }
    private byte[] ToUTF8(String str){
        Charset charset_gbk = Charset.forName("utf-8");
        ByteBuffer bytebuffer = charset_gbk.encode(str);
        byte[] bs = bytebuffer.array();
        return bs;
    }
}
