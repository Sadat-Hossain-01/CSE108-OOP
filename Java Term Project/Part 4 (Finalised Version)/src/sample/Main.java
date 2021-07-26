package sample;

import Controllers.*;
import DTO.ClubLoginAuthentication;
import DataModel.Club;
import DataModel.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.Scene;
import util.MyAlert;
import util.NetworkUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Main extends Application {
    public Stage primaryStage, tempStage;
    public Parent RootOfAll;
    public AnchorPane mainPane;
    public ClubDashboardController dashboardController;
    public boolean isFirstTime = true;
    public static double screenHeight, screenWidth;
    public InetAddress LocalAddress;
    public Socket socket;
    public int port = 44444;
    public Club myClub, latestCountryWiseCountClub;
    public Image cLogo;
    public Player latestDetailedPlayer;
    public Scene.Type currentPageType, previousPageType;
    public boolean isMainListUpdatePending = false, isTransferListUpdatePending = false;
    public List<Player> transferListedPlayers, latestSearchedPlayers;
    public HashMap<String, String> countryFlagMap = new HashMap<>();
    public HashMap<String, String> clubLogoMap = new HashMap<>();
    public NetworkUtil myNetworkUtil;
    public Image seal = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Assets/Image/Stamp1.png")));

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
        currentPageType = Scene.Type.LoginPage;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/ClubLoginView.fxml"));
        Parent root = loader.load();
        ClubLoginController controller = loader.getController();
        controller.setClubClient(this);
        var scene = new javafx.scene.Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    controller.SignIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
//        primaryStage.setOpacity(0.9);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
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
        primaryStage.setScene(new javafx.scene.Scene(RootOfAll));
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
        currentPageType = Scene.Type.ShowMyPlayers;
        displayList(myClub.getPlayerList(), PlayerListViewController.PageType.SimpleList);
    }

    public void showSearchPage() throws IOException {
        currentPageType = Scene.Type.ShowSearchOptions;
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
        previousPageType = currentPageType;
        currentPageType = Scene.Type.ShowCountryWiseCount;
        latestCountryWiseCountClub = club;
        tempStage = stage;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/CountryListView.fxml"));
        Parent root = loader.load();
        CountryListViewController c = loader.getController();
        c.setStage(stage);
        c.setMain(this);
        c.initiate(club.getCountryWisePlayerCount());
        var scene = new javafx.scene.Scene(root);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            currentPageType = previousPageType;
        });
        stage.setTitle("Country Wise Player Count");
        stage.setResizable(false);
        if (!stage.isShowing()) stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void showPlayerDetail(Stage stage, Player player) throws IOException {
        latestDetailedPlayer = player;
        tempStage = stage;
        previousPageType = currentPageType;
        currentPageType = Scene.Type.ShowAPlayerDetail;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/PlayerCardView.fxml"));
        Parent root = loader.load();
        PlayerCardController p = loader.getController();
        p.setMain(this);
        p.setStage(stage);
        p.initiate(player);
        var scene = new javafx.scene.Scene(root);
        stage.setOnCloseRequest(event -> {
            try {
                if (isTransferListUpdatePending) {
                    refreshPage(Scene.Type.ShowMarketPlayers);
                    isTransferListUpdatePending = false;
                }
                else if (isMainListUpdatePending){
                    refreshPage(Scene.Type.ShowMyPlayers);
                    isMainListUpdatePending = false;
                }
                else currentPageType = previousPageType;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.setTitle("Player Detail");
        stage.setResizable(false);
        if (!stage.isShowing()) stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
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

    public void AskForTransferFee(MinimalPlayerDetailController singlePlayerDetailController) throws IOException {
        Stage stage = new Stage();
        tempStage = stage;
        previousPageType = currentPageType;
        currentPageType = Scene.Type.AskForTransferFee;
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ViewFX/AskForTransferFee.fxml"));
        Parent root = loader.load();
        AskForTransferFeeController askForTransferFeeController = loader.getController();
        askForTransferFeeController.setMain(this);
        askForTransferFeeController.setStage(stage);
        askForTransferFeeController.initiate(singlePlayerDetailController);
        var scene = new javafx.scene.Scene(root);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    askForTransferFeeController.confirmListing();
                    if (isMainListUpdatePending) {
                        refreshPage(Scene.Type.ShowMyPlayers);
                        isMainListUpdatePending = false;
                    }
                    else currentPageType = Scene.Type.ShowMyPlayers;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.setOnCloseRequest(event -> {
            try {
                if (isMainListUpdatePending) {
                    refreshPage(Scene.Type.ShowMyPlayers);
                    isMainListUpdatePending = false;
                }
                else currentPageType = Scene.Type.ShowMyPlayers;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setScene(scene);
        stage.setTitle("Transfer Fee Input");
        stage.setResizable(false);
        stage.centerOnScreen();
        if (!stage.isShowing()) stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void showBuyablePlayers() throws IOException {
        currentPageType = Scene.Type.ShowMarketPlayers;
        resetAllButtons();
        mainPane.getChildren().clear();
        dashboardController.marketplaceButton.setStyle("-fx-background-color: #DA3A34; -fx-background-radius: 15 15 15 15");
        displayList(transferListedPlayers, PlayerListViewController.PageType.TransferList);
    }

    public void refreshPage(Scene.Type pageType) throws IOException {
        if (pageType == Scene.Type.ShowMyPlayers) showMyPlayers();
        else if (pageType == Scene.Type.ShowMarketPlayers) showBuyablePlayers();
        else if (pageType == Scene.Type.ShowSearchedPlayers){
            //refining the list
            List<Player> newList = new ArrayList<>();
            List<Player> clubPlayerList = myClub.getPlayerList();
            for (var p : latestSearchedPlayers){
                if (clubPlayerList.contains(p)) newList.add(p);
            }
            latestSearchedPlayers = newList;
            displayList(newList, PlayerListViewController.PageType.SimpleList);
        }
        else if (pageType == Scene.Type.ShowCountryWiseCount){
            showCountryWiseCount(tempStage, latestCountryWiseCountClub);
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
