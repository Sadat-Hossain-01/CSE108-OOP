package util;

import DataModel.Club;
import DataModel.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileOperations {
    public static List<Player> readPlayerDataFromFile(String FILE_NAME) throws Exception {
        List<Player> playerList = new ArrayList<>();
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NAME)));
        while (true) {
            String line = input.readLine();
            if (line == null)
                break;
//            String[] tokens = line.split(",");
//            Player p = new Player(tokens[0], tokens[1], Integer.parseInt(tokens[2]), Double.parseDouble(tokens[3]),
//                    tokens[4], tokens[5], Integer.parseInt(tokens[6]), Double.parseDouble(tokens[7]));
            String[] tokens = line.split(";");
            try {
                Player p = new Player(tokens[1], tokens[5], tokens[2], tokens[10], Integer.parseInt(tokens[3]), tokens[4], Double.parseDouble(tokens[7]), tokens[9], Integer.parseInt(tokens[8]), Double.parseDouble(tokens[6]), 200000000);
                if (tokens[11].equalsIgnoreCase("Left")) p.setPreferredFoot(Player.PreferredFoot.Left);
                else if (tokens[11].equalsIgnoreCase("Right")) p.setPreferredFoot(Player.PreferredFoot.Right);
                else p.setPreferredFoot(Player.PreferredFoot.Both);
                playerList.add(p);
            }
            catch (Exception ignored){}
        }
        input.close();
        return playerList;
    }

    public static HashMap<String, String> readCredentialsOfClubs(String FILE_NAME) throws IOException {
        HashMap<String, String> clubPasswords = new HashMap<>();
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_NAME)));
        while (true){
            String line = input.readLine();
            if (line == null) break;
            String[] tokens = line.split(",");
            String username = tokens[0];
            String password = tokens[1];
            clubPasswords.put(username, password);
        }
        input.close();
        return clubPasswords;
    }

    public static void writePlayerDataToFile(String FILE_NAME, List<Player> playerList) throws Exception {
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME)));
        for (Player p : playerList) {
            output.write(p.getName() + "," + p.getCountry() + "," + p.getAge() + "," + p.getHeight() + ","
                    + p.getClubName() + "," + p.getPosition() + "," + p.getNumber() + "," + p.getWeeklySalary());
            output.write("\n");
        }
        output.close();
    }

    public static void writeClubCredentialsToFile(String FILE_NAME, List<Club> clubList) throws Exception {
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME)));
        for (Club c : clubList) {
            output.write(c.getName() + "," + c.getName().toLowerCase());
            output.write("\n");
        }
        output.close();
    }
}