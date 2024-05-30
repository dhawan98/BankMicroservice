package dev.codescreen.service;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.User;
import dev.codescreen.Service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    void integrationTest_ProcessAuthorization() {
        Amount amount = new Amount("100", "USD", "DEBIT");
        transactionService.userData.put("user2", new User("user2", "USD", "300"));
        boolean result = transactionService.processAuthorization("user2", "tx130", amount);
        assertTrue(result);
        assertEquals("200.00", transactionService.getBalance("user2"));
    }

    @Test
    void integrationTest_ProcessLoad_FailureDueToCurrencyMismatch() {
        transactionService.userData.put("user3", new User("user3", "EUR", "200"));
        Amount amount = new Amount("100", "USD", "CREDIT");
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.processLoad("user3", "tx131", amount)
        );
        assertTrue(exception.getMessage().contains("Transaction currency does not match"));
    }

    @Test
    void integrationTest_ProcessLoad_Success() {
        transactionService.userData.put("user4", new User("user4", "USD", "150"));
        Amount amount = new Amount("100", "USD", "CREDIT");
        transactionService.processLoad("user4", "tx132", amount);
        assertEquals("250.00", transactionService.getBalance("user4"));
    }

    @Test
    void integrationTest_MultipleTransactions_Sequencing() {
        transactionService.userData.put("user5", new User("user5", "USD", "500"));
        transactionService.processLoad("user5", "tx133", new Amount("300", "USD", "CREDIT"));
        transactionService.processAuthorization("user5", "tx134", new Amount("200", "USD", "DEBIT"));
        assertEquals("600.00", transactionService.getBalance("user5"));
    }

    @Test
    void integrationTest_ProcessAuthorization_InsufficientFunds() {
        transactionService.userData.put("user6", new User("user6", "USD", "100"));
        Amount amount = new Amount("150", "USD", "DEBIT");
        boolean result = transactionService.processAuthorization("user6", "tx135", amount);
        assertFalse(result);
        assertEquals("100", transactionService.getBalance("user6")); // Balance should remain unchanged
    }

    @Test
    void integrationTest_RepeatedLoadOperations() {
        transactionService.userData.put("user7", new User("user7", "USD", "100"));
        transactionService.processLoad("user7", "tx136", new Amount("100", "USD", "CREDIT"));
        transactionService.processLoad("user7", "tx137", new Amount("200", "USD", "CREDIT"));
        assertEquals("400.00", transactionService.getBalance("user7"));
    }

    @Test
    void integrationTest_AuthorizationToZeroBalance() {
        transactionService.userData.put("user8", new User("user8", "USD", "150"));
        boolean result = transactionService.processAuthorization("user8", "tx138", new Amount("150", "USD", "DEBIT"));
        assertTrue(result);
        assertEquals("0.00", transactionService.getBalance("user8"));
    }

    @Test
    void integrationTest_LoadThenMultipleAuthorizations() {
        transactionService.userData.put("user9", new User("user9", "USD", "100"));
        transactionService.processLoad("user9", "tx139", new Amount("200", "USD", "CREDIT"));
        transactionService.processAuthorization("user9", "tx140", new Amount("100", "USD", "DEBIT"));
        transactionService.processAuthorization("user9", "tx141", new Amount("50", "USD", "DEBIT"));
        assertEquals("150.00", transactionService.getBalance("user9"));
    }

    @Test
    void integrationTest_CurrencyMismatchInMultipleOperations() {
        transactionService.userData.put("user10", new User("user10", "EUR", "200"));
        transactionService.processLoad("user10", "tx142", new Amount("100", "EUR", "CREDIT"));
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.processLoad("user10", "tx143", new Amount("100", "USD", "CREDIT"))
        );
        assertTrue(exception.getMessage().contains("Transaction currency does not match"));
        assertEquals("300.00", transactionService.getBalance("user10")); // Balance should remain correct in EUR
    }
}
