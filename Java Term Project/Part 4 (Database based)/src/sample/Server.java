package sample;

import DataModel.League;
import DataModel.Player;
import javafx.util.Pair;
import util.FileOperations;
import util.NetworkUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private ServerSocket serverSocket;
    private League FiveASideLeague;
    private HashMap<String, String> clubPasswordList;
    private HashMap<String, NetworkUtil> clubNetworkUtilMap;
    private ArrayList<Player> transferListedPlayers;
    private ArrayList<Pair<String, String>> countryFlagList;
    private ArrayList<Pair<String, String>> clubLogoList;

    public Server(int port) throws Exception {
        FiveASideLeague = new League();
        clubNetworkUtilMap = new HashMap<>();
        transferListedPlayers = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        //Reading all the club passwords
        clubPasswordList = FileOperations.readCredentialsOfClubs("src/Assets/Text/ClubUsername_Password.txt");
        System.out.println("Loaded credentials of " + clubPasswordList.size() + " clubs");
        //Reading player data

        var loaded = FileOperations.readPlayerDataFromFile("src/Assets/Text/fifacm/AllPlayerDatabaseFIFA.txt"); //file name path tree starts from one step back of src, but others all start from src
        for (var p : loaded) {
            FiveASideLeague.addPlayerToLeague(p);
            if (p.isTransferListed()) transferListedPlayers.add(p);
            else if (p.getClubName().equalsIgnoreCase("Free Agent")){
                p.setTransferListed(true);
                p.setTransferFee(0);
            }
        }
//        FileOperations.writeClubCredentialsToFile("src/Assets/Text/ClubUsername_Password.txt", FiveASideLeague.getClubList());
        System.out.println("Loaded data of " + FiveASideLeague.CentralPlayerDatabase.size() + " players");
        //Reading Country Data
        countryFlagList = FileOperations.readFlagLinkOfCountries("src/Assets/Text/fifacm/Country_Flag.txt");
        //Reading Club Data
        clubLogoList = FileOperations.readInformationOfClubs("src/Assets/Text/fifacm/Club_Data.txt", FiveASideLeague);
        System.out.println("Server up and running");
    }

    public void serve(Socket clientSocket) throws IOException {
        System.out.println("Server accepts a new socket");
        var networkUtil = new NetworkUtil(clientSocket);
        new ReadThreadServer(networkUtil, FiveASideLeague, transferListedPlayers, clubPasswordList, clubNetworkUtilMap, countryFlagList, clubLogoList);
    }

    public static void main(String[] args) throws Exception {
        int port = 44444;
        Server server = new Server(port);
        while (true){
            var cs = server.serverSocket.accept();
            server.serve(cs);
        }
    }
}
