package dev.codescreen.Model;

public class User {
    private String userId;
    private String lastCurrency;
    private String balance;

    public User() {
    }

    public User(String userId, String lastCurrency, String balance) {
        this.userId = userId;
        this.lastCurrency = lastCurrency;
        this.balance = balance;
    }

    public User(User user){
        this.userId = user.getUserId();
        this.lastCurrency = user.getLastCurrency();
        this.balance = user.getBalance();
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastCurrency() {
        return lastCurrency;
    }

    public void setLastCurrency(String lastCurrency) {
        this.lastCurrency = lastCurrency;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
