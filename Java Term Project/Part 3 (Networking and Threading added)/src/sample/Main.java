package sample;

import Controllers.*;
import DTO.ClubLoginAuthentication;
import DataModel.Club;
import DataModel.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.CurrentPage;
import util.MyAlert;
import util.NetworkUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public Stage primaryStage, tempStage;
    public Parent RootOfAll;
    public AnchorPane mainPane;
    public ClubDashboardController dashboardController;
    private boolean isFirstTime = true;
    public static double screenHeight, screenWidth;
    public InetAddress LocalAddress;
    public Socket socket;
    public int port = 44444;
    public Club myClub;
    public CurrentPage.Type pageType;
    public List<Player>TransferListedPlayers, latestSearchedPlayers;
    public NetworkUtil myNetworkUtil;

    public void initiateApplication() throws IOException {
        var screen = Screen.getPrimary().getBounds();
        screenHeight = screen.getHeight();
        screenWidth = screen.getWidth();
        LocalAddress = InetAddress.getLocalHost();
        showLoginPage();
    }

    public void ConnectToServer(String username, String password) throws IOException {
        socket = new Socket(LocalAddress, port);
        myNetworkUtil = new NetworkUtil(socket);
        new ReadThreadClient(this);
        myNetworkUtil.write(new ClubLoginAuthentication(username, password));
    }

    public void showAlertMessage(MyAlert myAlert) {
        myAlert.show();
    }

    public void showLoginPage() throws IOException {
        pageType = CurrentPage.Type.LoginPage;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/ClubLoginView.fxml"));
        Parent root = loader.load();
        ClubLoginController controller = loader.getController();
        controller.setClubClient(this);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login Page");
        primaryStage.setResizable(false);
        if (isFirstTime) {
            primaryStage.centerOnScreen();
            isFirstTime = false;
        }
        primaryStage.show();
    }

    public void showClubHomePage(Club c) throws IOException {
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/ClubDashboardView.fxml"));
        RootOfAll = loader.load();
        primaryStage.setScene(new Scene(RootOfAll));
        dashboardController = loader.getController();
        dashboardController.setMain(this);
        dashboardController.initiate(c);
        showMyPlayers();
    }

    public void resetAllButtons() {
        dashboardController.myPlayerButton.setStyle("-fx-background-color: #FED45F; -fx-background-radius: 15 15 15 15");
        dashboardController.marketplaceButton.setStyle("-fx-background-color: #FED45F; -fx-background-radius: 15 15 15 15");
        dashboardController.searchPlayerButton.setStyle("-fx-background-color: #FED45F; -fx-background-radius: 15 15 15 15");
    }

    public void showMyPlayers() throws IOException {
        resetAllButtons();
        mainPane.getChildren().clear();
        dashboardController.myPlayerButton.setStyle("-fx-background-color: #DA3A34; -fx-background-radius: 15 15 15 15");
        pageType = CurrentPage.Type.ShowMyPlayers;
        displayList(myClub.getPlayerList(), PlayerListViewController.PageType.SimpleList);
    }

    public void showSearchPage() throws IOException {
        pageType = CurrentPage.Type.ShowSearchOptions;
        resetAllButtons();
        mainPane.getChildren().clear();
        dashboardController.searchPlayerButton.setStyle("-fx-background-color: #DA3A34; -fx-background-radius: 15 15 15 15");
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/PlayerSearchView.fxml"));
        Parent root = loader.load();
        mainPane.getChildren().add(root);
        PlayerSearchController searchController = loader.getController();
        searchController.setMain(this);
        searchController.initiate(myClub);
        primaryStage.setTitle("Player Search Options");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void showCountryWiseCount(Stage stage, Club club) throws IOException {
        pageType = CurrentPage.Type.ShowCountryWiseCount;
        tempStage = stage;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/CountryListView.fxml"));
        Parent root = loader.load();
        CountryListViewController c = loader.getController();
        c.setStage(stage);
        c.setMain(this);
        c.initiate(club.getCountryWisePlayerCount());
        stage.setScene(new Scene(root));
        stage.setTitle("Country Wise Player Count");
        stage.setResizable(false);
        stage.show();
    }

    public void displayList(List<Player> playerList, PlayerListViewController.PageType pageType) throws IOException {
        mainPane.getChildren().clear();
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/PlayerListView.fxml"));
        Parent root = loader.load();
        mainPane.getChildren().add(root);
        PlayerListViewController playerListViewController = loader.getController();
        playerListViewController.setMain(this);
        playerListViewController.initiate(playerList, pageType);
        primaryStage.setTitle("Player List Display");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void AskForTransferFee(SinglePlayerDetailController singlePlayerDetailController) throws IOException {
        Stage stage = new Stage();
        tempStage = stage;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/AskForTransferFee.fxml"));
        Parent root = loader.load();
        AskForTransferFeeController askForTransferFeeController = loader.getController();
        askForTransferFeeController.setMain(this);
        askForTransferFeeController.setStage(stage);
        askForTransferFeeController.initiate(singlePlayerDetailController);
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Transfer Fee Input");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public void showBuyablePlayers() throws IOException {
        pageType = CurrentPage.Type.ShowMarketPlayers;
        resetAllButtons();
        mainPane.getChildren().clear();
        dashboardController.marketplaceButton.setStyle("-fx-background-color: #DA3A34; -fx-background-radius: 15 15 15 15");
        displayList(TransferListedPlayers, PlayerListViewController.PageType.TransferList);
    }

    public void refreshPage() throws IOException {
        if (pageType == CurrentPage.Type.ShowMyPlayers) showMyPlayers();
        else if (pageType == CurrentPage.Type.ShowMarketPlayers) showBuyablePlayers();
        else if (pageType == CurrentPage.Type.ShowSearchedPlayers){
            //refining the list
            List<Player> newList = new ArrayList<>();
            List<Player> clubPlayerList = myClub.getPlayerList();
            for (var p : latestSearchedPlayers){
                if (clubPlayerList.contains(p)) newList.add(p);
            }
            latestSearchedPlayers = newList;
            displayList(newList, PlayerListViewController.PageType.SimpleList);
        }
        else if (pageType == CurrentPage.Type.ShowCountryWiseCount){
            tempStage.close();
            showCountryWiseCount(tempStage, myClub);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initiateApplication();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
