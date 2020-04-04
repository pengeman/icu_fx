package org.peng.icu;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.peng.icu.videorecorder.CaptureScreen;

import java.io.*;
import org.apache.log4j.Logger;

public class ICUFaceController {
Logger log = Logger.getLogger(ICUFaceController.class);
    @FXML  private Button fileB;
    @FXML  private ListView listView2;
    @FXML  private Button capB;

    @FXML  private AnchorPane anchorPane;
    //@FXML  private TextArea textArea;
    @FXML  private TextField textField;
    org.peng.icu.action.SR rs = new org.peng.icu.action.SR();

    public void QuitMenu_clicked(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void textField_enterPressed(KeyEvent keyEvent) {
        KeyCode keyCode= keyEvent.getCode();
        if (keyCode == KeyCode.ENTER){
            String msg = this.textField.getText();
            sendMSG(msg);
        }

    }

    public void sendButton_clicked(ActionEvent actionEvent) {
        // 发送按钮
        String msg = this.textField.getText();
        sendMSG(msg);
    }
    // 发送文本消息
private void sendMSG(String msg){
        String msg2 = "error";
    try {
        msg2 = new String(msg.getBytes(),"utf-8");
    } catch (UnsupportedEncodingException e) {
        log.error(e);
    }
    rs.sendMSG(msg2);
    byte[] bytes = msg2.getBytes();

    int byteslen = bytes.length;
    if (byteslen > 20) byteslen = 20;
    for (int i = 0 ; i < byteslen ; i ++){
        log.debug(bytes[i]);
    }

    this.textField.clear();
    this.getListView2().getItems().add(">"+msg2);
}
    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public ListView getListView2() {
        return listView2;
    }

    public void capB_clicked(ActionEvent actionEvent) {
        // 截屏按钮

        getAnchorPane().getScene().getWindow().hide();

        try {
            Thread.sleep(1000L);
            java.io.OutputStream os = CaptureScreen.captureScreen();
            ByteArrayOutputStream bos = ((ByteArrayOutputStream)os);
            byte[] bytes ;
            bytes = bos.toByteArray();
            int byteslen = bytes.length;
            if (byteslen > 20) byteslen = 20;
            for (int i = 0 ; i < byteslen ; i ++){
                log.debug(bytes[i]);
            }
            rs.sendDOC(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) getAnchorPane().getScene().getWindow();
        stage.show();
    }

    public void listViewmouseClicked(MouseEvent mouseEvent) {

        Object item = listView2.getSelectionModel().getSelectedItem();
        System.out.println(item);
        if (item == null) return;
        String filename;
        try {
            if (item.getClass().getTypeName().contains("String")) {
                filename = (String) item;
                if (filename.length() < 4) return;
                if (filename.substring(0, 3).equals("-->")) {
                    filename = filename.substring(3);
                    ImageView imageView = new ImageView();

                    InputStream inputStream = new FileInputStream("out/"+filename);
                    imageView.setImage(new Image(inputStream));
                    imageView.setVisible(true);
                    showPicForm(imageView);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showPicForm(ImageView imageView){
        Button  btnscene2;
        Label  lblscene2;
        ScrollPane  pane2;
        Scene  scene2;
        Stage  newStage;

        btnscene2=new Button("Click to go back to First Scene");
       // btnscene2.setOnAction(e-> ButtonClicked(e));
        lblscene2=new Label("Scene 2");
        lblscene2.setGraphic(imageView);
        //创建面板
        pane2= new ScrollPane();// new FlowPane();

        //set background color of each Pane
        pane2.setStyle("-fx-background-green:#00ff00;-fx-padding:10px;");
        //组件加入面板
        //pane2.getChildren().addAll(lblscene2, btnscene2);
        pane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane2.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane2.setPannable(true);
        pane2.setContent(lblscene2);

        //make 2 scenes from 2 panes
        scene2 = new Scene(pane2, 200, 100);
        //创建另一个stage
        newStage = new Stage();
        newStage.setScene(scene2);
        //指定 stage 的模式
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Pop up window");
        newStage.showAndWait();

    }
    private byte[] getFileBytes(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream in = new FileInputStream(file);
            int fileSize = in.available();
            byte[] data = new byte[fileSize];
            in.read(data);
            in.close();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendFile(ActionEvent actionEvent) {
        // 发送文件按钮被点击
        Stage stage = (Stage) getAnchorPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        log.info("选择一个:" + file.getAbsolutePath());
        if  (file == null){
            return;
        }
        String filename = file.getAbsolutePath();
        rs.sendDOC(filename);
    }
}
