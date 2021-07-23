package DataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static DataModel.League.FormatName;

public class Club implements Serializable {
    private String name;
    private String logoLink;
    private double TransferBudget, Worth;
    private List<Player> PlayerList;
    public List<Integer> NumberTaken;

    public Club(String name) {
        setName(name);
        PlayerList = new ArrayList<>();
        NumberTaken = new ArrayList<>();
    }

    public Club(Club club){
        this.name = club.name;
        this.logoLink = club.logoLink;
        this.TransferBudget = club.TransferBudget;
        this.PlayerList = new ArrayList<>(PlayerList);
        this.NumberTaken = new ArrayList<>(NumberTaken);
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public void setName(String name) {
        this.name = FormatClubName(name);
    }

    public String getName() {
        return name;
    }

    public double getTransferBudget() {
        return TransferBudget;
    }

    public void setTransferBudget(double transferBudget) {
        TransferBudget = transferBudget;
    }

    public double getWorth() {
        return Worth;
    }

    public void setWorth(double worth) {
        Worth = worth;
    }

    public void increseTransferBudget(double increment){
        TransferBudget += increment;
    }

    public void decreaseTransferBudget(double decrement){
        TransferBudget -= decrement;
    }

    public List<Player> getPlayerList(){return PlayerList;}

    public List<Player> SearchMaximumSalary() {
        List<Player> wantedPlayers = new ArrayList<>();
        double maxSalary = 0;
        for (var player : PlayerList) maxSalary = Math.max(maxSalary, player.getWeeklySalary());
        for (var player : PlayerList) {
            if (player.getWeeklySalary() == maxSalary) {
                wantedPlayers.add(player);
            }
        }
        return wantedPlayers;
    }

    public List<Player> SearchMaximumAge() {
        List<Player> wantedPlayers = new ArrayList<>();
        double maxAge = 0;
        for (var player : PlayerList) maxAge = Math.max(maxAge, player.getAge());
        for (var player : PlayerList) {
            if (player.getAge() == maxAge) {
                wantedPlayers.add(player);
            }
        }
        return wantedPlayers;
    }

    public List<Player> SearchMaximumHeight() {
        List<Player> wantedPlayers = new ArrayList<>();
        double maxHeight = 0;
        for (var player : PlayerList) maxHeight = Math.max(maxHeight, player.getHeight());
        for (var player : PlayerList) {
            if (player.getHeight() == maxHeight) {
                wantedPlayers.add(player);
            }
        }
        return wantedPlayers;
    }

    public double TotalYearlySalary() {
        double total_salary = 0;
        for (var player : PlayerList) total_salary += player.getWeeklySalary();
        return total_salary * 52;
    }

    public void addPlayerToClub(Player p) {
        //assuming the club size check is done in main
        PlayerList.add(p);
        NumberTaken.add(p.getNumber());
    }

    public HashMap<String, Integer> getCountryWisePlayerCount() {
        HashMap<String, Integer> countryWiseCount = new HashMap<>();
        for (var p : PlayerList){
            var c = p.getCountry();
            if (countryWiseCount.containsKey(c)){
                countryWiseCount.put(p.getCountry(), countryWiseCount.get(p.getCountry()) + 1);
            }
            else{
                countryWiseCount.put(p.getCountry(), 1);
            }
        }
        return countryWiseCount;
    }

    public List<Player> SearchByNameInClub(String name) {
        List<Player> ans = new ArrayList<>();
        String FormattedName = FormatName(name);
        for (var p : PlayerList) {
            if (p.getName().equalsIgnoreCase(FormattedName)) {
                ans.add(p);
                return ans;
            }
        }
        return null;
    }

    public List<Player> SearchPlayerByCountryInClub(String country) {
        String FormattedCountryName = FormatName(country);
        List<Player> wantedPlayers = new ArrayList<>();
        for (var p : PlayerList) {
            if ((country.equalsIgnoreCase("ANY") || p.getCountry().equalsIgnoreCase(FormattedCountryName))) {
                wantedPlayers.add(p);
            }
        }
        return wantedPlayers;
    }

    public List<Player> SearchPlayerBySalaryInClub(double lowRange, double highRange) {
        List<Player> wantedPlayers = new ArrayList<>();
        for (var p : PlayerList) {
            double salary = p.getWeeklySalary();
            if ((lowRange == -1 || lowRange <= salary) && (highRange == -1 || salary <= highRange)) {
                wantedPlayers.add(p);
            }
        }
        return wantedPlayers;
    }

    public List<Player> SearchPlayerByPositionInClub(String position) {
        List<Player> wantedPlayers = new ArrayList<>();
        for (var p : PlayerList) {
            if (p.getPosition().equalsIgnoreCase(position)) {
                wantedPlayers.add(p);
            }
        }
        return wantedPlayers;
    }

    public static String showSalary(double salaryNumber){
        String salary = "";
        if (salaryNumber >= 1e9) salary = "€" + salaryNumber/(1e9) + "B";
        else if (salaryNumber >= 1e6) salary = "€" + salaryNumber/(1e6) + "M";
        else if (salaryNumber >= 1e3) salary = "€" + salaryNumber/(1e3) + "K";
        else salary = "€" + salaryNumber;
        return salary;
    }

    public static double decodeSalaryString(String salary){
        int len = salary.length();
        double v = Double.parseDouble(salary.substring(1, len - 1));
        if (salary.endsWith("B")) return 1e9 * v;
        else if (salary.endsWith("M")) return 1e6 * v;
        else if (salary.endsWith("K")) return 1e3 * v;
        else return Double.parseDouble(salary.substring(1, len));
    }

    public Player FindPlayerInList(String PlayerName, List<Player> list) { //will return null if club is not registered yet
        Player wanted = null;
        String FormattedPlayerName = FormatName(PlayerName);
        for (var p : list) {
            if (p.getName().equalsIgnoreCase(FormattedPlayerName)) {
                wanted = p;
                break;
            }
        }
        return wanted;
    }

    public String FormatClubName(String name) {
        return FormatName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
