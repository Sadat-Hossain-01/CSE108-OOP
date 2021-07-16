package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.Main;
import util.MyAlert;

import java.util.Objects;

public class AskForTransferFeeController {
    private Main main;
    private SinglePlayerDetailController singlePlayerDetailController;
    private Stage stage;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField AskedTransferFee;

    public void initiate(SinglePlayerDetailController singlePlayerDetailController){
        this.singlePlayerDetailController = singlePlayerDetailController;
    }

    @FXML
    void confirmTransferListing(ActionEvent event) {
        double transferFee;
        try {
            transferFee = Double.parseDouble(AskedTransferFee.getText()) * 1e6;
            if (transferFee <= 0) throw new Exception();
        } catch (Exception e){
            new MyAlert(Alert.AlertType.ERROR, MyAlert.MessageType.InvalidSalaryInput).show();
            return;
        }
        var player = singlePlayerDetailController.getPlayer();
        var transferTag = singlePlayerDetailController.getTransferTag();
        var transferButton = singlePlayerDetailController.getTransferStatusButton();
        player.setAskingPrice(transferFee);
        player.setTransferListed(true);
        transferTag.setImage(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/Assets/Image/Rotated Seal.png"))));
        transferButton.setText("Remove from Transfer List");
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
