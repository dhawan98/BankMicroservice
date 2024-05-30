package dev.codescreen.Event;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.User;
import java.util.concurrent.ConcurrentHashMap;

public class LoadEvent extends Event {
    public LoadEvent(String userId, String messageId, Amount amount) {
        super(userId, messageId, amount);
    }

    @Override
    public void apply(ConcurrentHashMap<String, User> userData) {
        userData.merge(getUserId(), new User(getUserId(), getAmount().getCurrency(), getAmount().getAmount()),
                (oldUser, newUser)-> {

            double sum = Double.parseDouble(oldUser.getBalance()) +
                    Double.parseDouble(newUser.getBalance());
           newUser.setBalance(String.format("%.2f", sum));
           return newUser;
        });
        }
}

