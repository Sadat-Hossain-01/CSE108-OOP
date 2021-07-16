package sample;

import DataModel.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class PlayerListViewController {
    Main main;
    public enum PageType{
        SimpleList, TransferList
    }

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private ListView<VBox> listView;

    void initiate(List<Player> players, PageType pageType) throws IOException {
        for (var p : players) {
            var newLoader = new FXMLLoader();
            newLoader.setLocation(getClass().getResource("/ViewFX/SinglePlayerDetailView.fxml"));
            newLoader.load();
            SinglePlayerDetailController pDetail = newLoader.getController();
            listView.getItems().add(pDetail.initiate(p, pageType));
        }
    }

}
