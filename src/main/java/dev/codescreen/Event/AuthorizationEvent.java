package dev.codescreen.Event;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.User;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationEvent extends Event {
    private boolean approved;

    public AuthorizationEvent(String userId, String messageId, Amount amount, boolean approved) {
        super(userId, messageId, amount);
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }

    //Deducting the balance from user after authorization is approved
    @Override
    public void apply(ConcurrentHashMap<String, User> userData) {
        if (isApproved()) {
            userData.computeIfPresent(getUserId(), (key, curUser) -> {
                double newBalance = Double.parseDouble(curUser.getBalance()) - Double.parseDouble(getAmount().getAmount());
                curUser.setBalance(String.format("%.2f", newBalance));
                return curUser;
            });
        }
    }
}
