package dev.codescreen.Model;


public class AuthorizationResponse {
    private String userId;
    private String messageId;
    private String responseCode;
    private Amount balance;

    public AuthorizationResponse(String userId, String messageId, String responseCode, Amount balanceAmount) {
        this.userId = userId;
        this.messageId = messageId;
        this.responseCode = responseCode;
        this.balance = balanceAmount;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Amount getBalance() {
        return balance;
    }

    public void setBalance(Amount balance) {
        this.balance = balance;
    }
}
