package dev.codescreen.service;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.User;
import dev.codescreen.Service.StateRebuilder;
import dev.codescreen.Service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;


class TransactionServiceTests {

    @Mock
    private StateRebuilder stateRebuilder;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.userData.put("user1", new User("user1", "USD", "200"));
        service.eventStore = new ArrayList<>();
        when(stateRebuilder.rebuildStateFromEvents(any(), any())).thenReturn(service.userData);
    }
    @Test
    void testServiceInitialization() {
        service.initialize();
        verify(stateRebuilder, times(1)).rebuildStateFromEvents(any(), any());
    }

    /////Request Validation Unit Tests/////
    @Test
    void requestValidation_EmptyUserId_ReturnsFalse() {
        Amount amount = new Amount("50", "USD", "DEBIT");
        assertFalse(service.requestValidation("", "tx138", amount));
    }

    @Test
    void requestValidation_NullUserId_ReturnsFalse() {
        Amount amount = new Amount("50", "USD", "DEBIT");
        assertFalse(service.requestValidation(null, "tx138", amount));
    }

    @Test
    void requestValidation_EmptyMessageId_ReturnsFalse() {
        Amount amount = new Amount("50", "USD", "DEBIT");
        assertFalse(service.requestValidation("user1", "", amount));
    }

    @Test
    void requestValidation_NullMessageId_ReturnsFalse() {
        Amount amount = new Amount("50", "USD", "DEBIT");
        assertFalse(service.requestValidation("user1", null, amount));
    }


    @Test
    void requestValidation_NullAmount_ReturnsFalse() {
        assertFalse(service.requestValidation("user1", "tx139", null));
    }

    @Test
    void requestValidation_NullAmountValue_ReturnsFalse() {
        Amount amount = new Amount(null, "USD", "DEBIT");
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    @Test
    void requestValidation_EmptyAmountValue_ReturnsFalse() {
        Amount amount = new Amount(" ", "USD", "DEBIT");
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    @Test
    void requestValidation_NullCurrency_ReturnsFalse() {
        Amount amount = new Amount("300", null, "DEBIT");
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    @Test
    void requestValidation_EmptyCurrency_ReturnsFalse() {
        Amount amount = new Amount("300", "", "DEBIT");
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    @Test
    void requestValidation_NullDebitOrCredit_ReturnsFalse() {
        Amount amount = new Amount("300", "", null);
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    @Test
    void requestValidation_EmptyDebitOrCredit_ReturnsFalse() {
        Amount amount = new Amount("300", "", " ");
        assertFalse(service.requestValidation("user1", "tx139", amount));
    }

    ////ProcessLoad Unit Tests////
    @Test
    void processLoad_ValidRequest_Success() {
        Amount amount = new Amount("100", "USD", "CREDIT");
        service.userData.put("user1", new User("user1", "USD", "200"));
        String result = service.processLoad("user1", "tx123", amount);
        assertEquals("300.00", result);
    }

    @Test
    void processLoad_ExtremeDecimalValues_Success() {
        Amount amount = new Amount("100.123456789", "USD", "CREDIT");
        service.processLoad("user1", "tx207", amount);
        assertEquals("300.12", service.getBalance("user1"));
    }

    @Test
    void processLoad_AmountWithCommas_Success() {
        // Some locales use commas to separate thousands
        Amount amount = new Amount("1,000", "USD", "CREDIT");
        service.processLoad("user1", "tx207", amount);
        assertEquals("1200.00", service.getBalance("user1"));
    }

    @Test
    void processLoad_AmountWithTrailingOrLeadingWhiteSpace_Success() {
        Amount amount = new Amount(" 1000  ", "USD", "CREDIT");
        service.processLoad("user1", "tx207", amount);
        assertEquals("1200.00", service.getBalance("user1"));
    }

    @Test
    void processLoad_AmountWithSpaces_ThrowsException() {
        Amount amount = new Amount("1 000  ", "USD", "CREDIT");
        assertThrows(IllegalArgumentException.class, () -> service.processLoad("user1", "tx126", amount));
    }

    @Test
    void processLoad_NegativeAmount_ThrowsException() {
        Amount amount = new Amount("-100", "USD", "CREDIT");
        assertThrows(IllegalArgumentException.class, () -> service.processLoad("user1", "tx126", amount));
    }

    @Test
    void processLoad_Debit_ThrowsException() {
        Amount amount = new Amount("100", "USD", "Debit");
        assertThrows(IllegalArgumentException.class, () -> service.processLoad("user1", "tx127", amount));
    }

    @Test
    void processLoad_CurrencyMismatch_ThrowsException() {
        Amount amount = new Amount("100", "EUR", "CREDIT");
        assertThrows(IllegalArgumentException.class, () -> service.processLoad("user1", "tx127", amount));
    }

    @Test
    void processLoad_MixedCaseCurrency_Success() {
        Amount amount = new Amount("100", "usd", "CREDIT");
        service.processLoad("user1", "tx207", amount);
        assertEquals("300.00", service.getBalance("user1"));
    }

    @Test
    void processLoad_ZeroAmount_Success() {
        Amount amount = new Amount("0.00", "usd", "CREDIT");
        service.processLoad("user1", "tx207", amount);
        assertEquals("200.00", service.getBalance("user1"));
    }



    @Test
    void processLoad_Concurrent_Success() throws InterruptedException {
        Thread t1 = new Thread(() -> service.processLoad("user1", "tx130", new Amount("50", "USD", "CREDIT")));
        Thread t2 = new Thread(() -> service.processLoad("user1", "tx131", new Amount("50", "USD", "CREDIT")));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals("300.00", service.getBalance("user1"));
    }

    //// ProcessAuthorization Unit Tests ////

    @Test
    void processAuthorization_ValidRequest_Success() {
        Amount amount = new Amount("100", "USD", "DEBIT");
        // Execute
        boolean result = service.processAuthorization("user1", "tx124", amount);
        // Verify
        assertTrue(result);
    }

    @Test
    void processAuthorization_AmountWithCommas_Success() {
        // Some locales use commas to separate thousands
        Amount amount = new Amount("1,000", "USD", "DEBIT");
        service.processLoad("user1", "tx203", new Amount("800", "USD", "CREDIT"));
        boolean result = service.processAuthorization("user1", "tx203", amount);
        assertTrue(result);
        assertEquals("0.00", service.getBalance("user1"));
    }

    @Test
    void processAuthorization_AmountWithTrailingOrLeadingWhiteSpace_Success() {
        Amount amount = new Amount(" 100  ", "USD", "DEBIT");
        boolean result = service.processAuthorization("user1", "tx203", amount);
        assertTrue(result);
    }

    @Test
    void processAuthorization_AmountWithSpaces_ThrowsException() {
        Amount amount = new Amount("1 000  ", "USD", "DEBIT");
        assertThrows(IllegalArgumentException.class, () -> service.processAuthorization("user1", "tx203", amount));
    }


    @Test
    void processAuthorization_UserNotFound_ThrowsException() {
        Amount amount = new Amount("100", "USD", "DEBIT");
        assertThrows(IllegalArgumentException.class, () -> service.processAuthorization("nonexistent", "tx128", amount));
    }

    @Test
    void processAuthorization_DebitMismatch_ThrowsException() {
        service.userData.put("user1", new User("user1", "USD", "200"));
        Amount amount = new Amount("-50", "USD", "Credit");
        assertThrows(IllegalArgumentException.class, () -> service.processAuthorization("user1", "tx129", amount));
    }

    @Test
    void processAuthorization_NegativeAmount_ThrowsException() {
        service.userData.put("user1", new User("user1", "USD", "200"));
        Amount amount = new Amount("-50", "USD", "DEBIT");
        assertThrows(IllegalArgumentException.class, () -> service.processAuthorization("user1", "tx129", amount));
    }

    @Test
    void processAuthorization_NotEnoughBalance_Declined() {
        boolean result = service.processAuthorization("user1", "tx136", new Amount("500", "USD", "DEBIT"));
        assertFalse(result);
        assertEquals("200", service.getBalance("user1")); // Balance remains unchanged
    }

    @Test
    void processAuthorization_MixedCaseCurrency_Success() {
        service.processLoad("user1", "tx204", new Amount("500", "usd", "CREDIT"));
        boolean result = service.processAuthorization("user1", "tx205", new Amount("300", "USD", "DEBIT"));
        assertTrue(result);
        assertEquals("400.00", service.getBalance("user1"));
    }

    @Test
    void processAuthorization_CurrencyMismatch_Declined() {
        // Currency mismatch for existing user
        boolean result = service.processAuthorization("user1", "tx205", new Amount("300", "BWT", "DEBIT"));
        assertFalse(result);
        assertEquals("200", service.getBalance("user1"));  // Initial 200 + 500 - 300
    }

    @Test
    void processAuthorization_ZeroAmount_NoChange() {
        boolean result = service.processAuthorization("user1", "tx208", new Amount("0", "USD", "DEBIT"));
        assertTrue(result);
        assertEquals("200.00", service.getBalance("user1"));  // Balance remains unchanged
    }

    @Test
    void processAuthorization_MaxLimitExceeds_Declined() {
        boolean result = service.processAuthorization("user1", "tx136", new Amount("10001", "USD", "DEBIT"));
        assertFalse(result);
        assertEquals("200", service.getBalance("user1")); // Balance remains unchanged
    }
    @Test
    void processAuthorization_SimultaneousTransactions_ConsistencyCheck() {
        Thread t1 = new Thread(() -> service.processAuthorization("user1", "tx209", new Amount("50", "USD", "DEBIT")));
        Thread t2 = new Thread(() -> service.processLoad("user1", "tx210", new Amount("50", "USD", "CREDIT")));
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertEquals("200.00", service.getBalance("user1"));
    }

    @Test
    void processAuthorization_AfterMultipleLoads_CorrectBalance() {
        service.processLoad("user1", "tx133", new Amount("100", "USD", "CREDIT"));
        service.processLoad("user1", "tx134", new Amount("200", "USD", "CREDIT"));
        service.processAuthorization("user1", "tx135", new Amount("150", "USD", "DEBIT"));
        assertEquals("350.00", service.getBalance("user1"));
    }

    @Test
    void processAuthorization_InvalidDebitOrCredit_ThrowsException() {
        Amount amount = new Amount("100", "USD", "UNKNOWN");
        assertThrows(IllegalArgumentException.class, () -> service.processAuthorization("user1", "tx137", amount));
    }

    @Test
    void processAuthorization_ExactAmount_Success() {
        boolean result = service.processAuthorization("user1", "tx132", new Amount("200", "USD", "DEBIT"));
        assertTrue(result);
        assertEquals("0.00", service.getBalance("user1"));
    }

}
