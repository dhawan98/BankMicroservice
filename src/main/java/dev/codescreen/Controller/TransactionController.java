package dev.codescreen.Controller;

import dev.codescreen.Model.Amount;
import dev.codescreen.Model.*;
import dev.codescreen.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    //Provides response for Load requests
    @PutMapping("/load/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LoadResponse load(@PathVariable String messageId, @RequestBody LoadRequest request) throws Exception {
        try {
            String balance = transactionService.processLoad(request.getUserId(), messageId, request.getTransactionAmount());
            return new LoadResponse(request.getUserId(), messageId, new Amount(balance, request.getTransactionAmount().getCurrency(), request.getTransactionAmount().getDebitOrCredit()));
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing load", e);

        }
    }


    //Provides response for Authorization requests
    @PutMapping("/authorization/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorizationResponse authorize(@PathVariable String messageId, @RequestBody AuthorizationRequest request) throws Exception {
        try {
            boolean approved = transactionService.processAuthorization(request.getUserId(), messageId, request.getTransactionAmount());
            String responseCode = approved ? "APPROVED" : "DECLINED";
            String balance = transactionService.getBalance(request.getUserId());
            return new AuthorizationResponse(request.getUserId(), messageId, responseCode, new Amount(balance, request.getTransactionAmount().getCurrency(), request.getTransactionAmount().getDebitOrCredit()));
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing authorization", e);

        }
    }


    //Provides response for Ping requests
    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public Ping ping() {
        return new Ping(ZonedDateTime.now());
    }

}
