package dev.codescreen.Model;

public class Amount {
    private String amount;
    private String currency;
    private String debitOrCredit;

    public Amount(String amount, String usd, String credit) {
        this.amount = amount;
        this.currency = usd;
        this.debitOrCredit = credit;
    }

    // Getters and setters
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

    public void setDebitOrCredit(String debitOrCredit) {
        this.debitOrCredit = debitOrCredit;
    }
}
