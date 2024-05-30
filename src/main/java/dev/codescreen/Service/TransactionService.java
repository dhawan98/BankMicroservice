package dev.codescreen.Service;

import dev.codescreen.Event.*;
import dev.codescreen.Event.LoadEvent;
import dev.codescreen.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import dev.codescreen.Model.User;
import dev.codescreen.Model.Amount;

@Service
public class TransactionService {

    private static final int SNAPSHOT_INTERVAL = 100; //Snapshot every 100 events
    public ConcurrentHashMap<String, User> userData = new ConcurrentHashMap<>();
    public List<Event> eventStore = new ArrayList<>();
    private UserSnapshot lastSnapshot;

    @Autowired
    private StateRebuilder stateRebuilder;

    @PostConstruct
    public void initialize() {
        // Rebuilding the state from last snapshot
        this.userData = stateRebuilder.rebuildStateFromEvents(eventStore, lastSnapshot);
    }

    private void takeSnapshot() {
        lastSnapshot = new UserSnapshot(new ConcurrentHashMap<>(userData), eventStore.size() - 1);
    }
    //Validation requests for transaction
    public Boolean requestValidation(String userId, String messageId, Amount amount){
        if (userId == null || userId.trim().isEmpty()){
            return false;
        }
        if (messageId == null || messageId.trim().isEmpty()){
            return false;
        }
        if (amount == null){
            return false;
        }
        if (amount.getAmount() == null || amount.getAmount().trim().isEmpty()){
            return false;
        }
        if (amount.getCurrency() == null || amount.getCurrency().trim().isEmpty()){
            return false;
        }
        if (amount.getDebitOrCredit() == null || amount.getDebitOrCredit().trim().isEmpty()){
            return false;
        }
        return true;
    }
    //helper function to allow commas in amount
    public String formatAmount(String amount){
        String amountWithoutCommas = amount.replaceAll(",","").trim();
        return amountWithoutCommas.trim();
    }

    //Function to process load transactions
    public String processLoad(String userId, String messageId, Amount amount) {

        if (!requestValidation(userId, messageId, amount)){
            throw new IllegalArgumentException("Invalid request");
        }
        amount.setAmount(formatAmount(amount.getAmount()));
        if (amount.getAmount().contains(" ")){
            throw new IllegalArgumentException("Amount contains spaces");
        }
        User user = userData.get(userId);
        // Check for currency mismatch
        if (user!=null && !user.getLastCurrency().equalsIgnoreCase(amount.getCurrency())) {
            throw new IllegalArgumentException("Transaction currency does not match the last transaction currency for this user.");
        }

        if (!amount.getDebitOrCredit().equalsIgnoreCase("CREDIT")){
            throw new IllegalArgumentException("Load can only handle CREDIT");
        }
        if(Double.parseDouble(amount.getAmount()) < 0) {
            throw new IllegalArgumentException("Negative amounts cannot be credited.");
        }

        LoadEvent event = new LoadEvent(userId, messageId, amount);
        eventStore.add(event);
        if (eventStore.size() % SNAPSHOT_INTERVAL == 0) {
            takeSnapshot();
        }
        event.apply(userData);
        return userData.get(userId).getBalance();
    }

    //Function to process Authorization transactions
    public boolean processAuthorization(String userId, String messageId, Amount amount) {
        if (!requestValidation(userId, messageId, amount)){
            throw new IllegalArgumentException("Invalid request");
        }
        amount.setAmount(formatAmount(amount.getAmount()));
        if (amount.getAmount().contains(" ")){
            throw new IllegalArgumentException("Amount contains spaces");
        }
        User user = userData.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("USER NOT FOUND");
        }
        if (!amount.getDebitOrCredit().equalsIgnoreCase("DEBIT")){
            throw new IllegalArgumentException("Only Debit allowed");
        }
        if(Double.parseDouble(amount.getAmount()) < 0)
        {
            throw new IllegalArgumentException("Negative amounts cannot be Debited.");
        }

        String currentBalance = userData.get(userId).getBalance();
        double amountDouble = Double.parseDouble(amount.getAmount());
        double balanceDouble = Double.parseDouble(currentBalance);
        boolean isApproved = false;

        if (balanceDouble >= amountDouble) {
            isApproved = true;
        }

        if (!user.getLastCurrency().equalsIgnoreCase(amount.getCurrency())) {
            isApproved = false;
        }
        if(amountDouble > 10000){
            isApproved = false;
        }

        AuthorizationEvent event = new AuthorizationEvent(userId, messageId, amount, isApproved);
        eventStore.add(event);
        if (eventStore.size() % SNAPSHOT_INTERVAL == 0) {
            takeSnapshot();
        }
        if (isApproved){
            event.apply(userData);
        }
        return isApproved;
    }
    //helper function
    public String getBalance(String userId) {
        return userData.get(userId).getBalance();
    }
}
