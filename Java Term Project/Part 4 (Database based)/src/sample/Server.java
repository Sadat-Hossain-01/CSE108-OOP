package sample;

import DataModel.League;
import DataModel.Player;
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

    public Server(int port) throws Exception {
        FiveASideLeague = new League();
        clubPasswordList = FileOperations.readCredentialsOfClubs("src/Assets/Text/UpdatedClubAuthentications.txt");
//        var loaded = FileOperations.readPlayerDataFromFile("src/Assets/Text/players.txt"); //file name path tree starts from one step back of src, but others all start from src
        var loaded = FileOperations.readPlayerDataFromFile("src/Assets/Text/database1.txt"); //file name path tree starts from one step back of src, but others all start from src
        for (var p : loaded) FiveASideLeague.addPlayerToLeague(p);
//        FileOperations.writeClubCredentialsToFile("src/Assets/Text/UpdatedClubAuthentications.txt", FiveASideLeague.getClubList());
        for (var c : FiveASideLeague.getCountryList()){
            System.out.println(c.getName());
        }
        System.out.println("Server up and running");
        clubNetworkUtilMap = new HashMap<>();
        transferListedPlayers = new ArrayList<>();
        serverSocket = new ServerSocket(port);
    }

    public void serve(Socket clientSocket) throws IOException {
        System.out.println("Server accepts a new socket");
        var networkUtil = new NetworkUtil(clientSocket);
        new ReadThreadServer(networkUtil, FiveASideLeague, transferListedPlayers, clubPasswordList, clubNetworkUtilMap);
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