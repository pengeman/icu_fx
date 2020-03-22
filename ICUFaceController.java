package org.peng.icu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ICUFaceController {

    @FXML  private TextArea textArea;
    @FXML  private TextField textField;
    org.peng.icu.action.SR rs = new org.peng.icu.action.SR();

    public void QuitMenu_clicked(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void textField_enterPressed(KeyEvent keyEvent) {

    }

    public void sendButton_clicked(ActionEvent actionEvent) {
        String msg = this.textField.getText();
        rs.sendMSG(msg);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }
}
