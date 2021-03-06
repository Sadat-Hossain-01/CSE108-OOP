package DataModel;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private String country;
    private String ClubName;
    private final String position;
    private final int age;
    private int number;
    private final double height;
    private final double WeeklySalary;
    private boolean isTransferListed = false;
    private double TransferFee = 0;

    public Player(String name, String country, int age, double height, String ClubName, String position, int number, double WeeklySalary) {
        setName(name);
        setCountry(country);
        this.age = age;
        this.height = height;
        setClubName(ClubName);
        this.position = position;
        this.number = number;
        this.WeeklySalary = WeeklySalary;
    }

    public Player(Player player) {
        this.name = player.name;
        this.country = player.country;
        this.ClubName = player.ClubName;
        this.position = player.position;
        this.age = player.age;
        this.number = player.number;
        this.height = player.height;
        this.WeeklySalary = player.WeeklySalary;
        this.isTransferListed = player.isTransferListed;
        this.TransferFee = player.TransferFee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = League.FormatName(name);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = League.FormatName(country);
    }

    public String getClubName() {
        return ClubName;
    }

    public void setClubName(String ClubName) {
        this.ClubName = League.FormatName(ClubName);
    }

    public String getPosition() {
        return position;
    }

    public int getAge() {
        return age;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public double getHeight() {
        return height;
    }

    public double getWeeklySalary() {
        return WeeklySalary;
    }

    public double getTransferFee() {
        return TransferFee;
    }

    public void setTransferFee(double transferFee) {
        TransferFee = transferFee;
    }

    public boolean isTransferListed() {
        return isTransferListed;
    }

    public void setTransferListed(boolean transferListed) {
        isTransferListed = transferListed;
    }

    public synchronized int isTransferPossible(Club buyer) { //returns 0 on success, 1 on already bought, 2 on insufficient budget
        if (!isTransferListed) return 1;
        if (buyer.getTransferBudget() < TransferFee) return 2;
        return 0;
    }

    public void showDetails() {
        System.out.println("Name: " + name);
        System.out.println("Country: " + country);
        System.out.println("Age: " + age + " years");
        System.out.println("Height: " + height + "m");
        System.out.println("Club: " + ClubName);
        System.out.println("Position: " + position);
        System.out.println("Number: " + number);
        System.out.println("Weekly Salary: " + WeeklySalary);
    }
}
