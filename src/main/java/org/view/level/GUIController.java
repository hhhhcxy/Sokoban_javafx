package org.view.level;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GUIController {

    @FXML
    private HBox MoveCount;

    @FXML
    private Button Settings;

    @FXML
    private VBox Stats;

    @FXML
    private Text movecount_num;

    @FXML
    private Text movecount_text;

    private int movecount;

    public void initialize(){
        movecount = 0;
        movecount_num.setText(String.valueOf(movecount));
    }

    public GUIController() {

    }

    public void setMoveCount(int movecount) {
        this.movecount = movecount;
    }
    public void update(){
        movecount_num.setText(String.valueOf(movecount));
    }
    public void update(int movecount) {
        this.movecount = movecount;
        movecount_num.setText(String.valueOf(movecount));
    }

}