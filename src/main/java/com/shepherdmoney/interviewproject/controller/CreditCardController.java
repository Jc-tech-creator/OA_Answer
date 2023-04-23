package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.service.CreditCardService;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CreditCardController {

    @Autowired
    private CreditCardRepository creditCardRepository;
    // TODO: wire in CreditCard repository here (~1 line)(done)
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardService creditCardService;

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        Optional<User> optionalUser = userRepository.findById(payload.getUserId());
        //retrieve the user
        if (optionalUser.isPresent()) {// when do find the user
            User foundUser = optionalUser.get();
            CreditCard newCreditCard = new CreditCard();
            //new an entity
            newCreditCard.setIssuanceBank(payload.getCardIssuanceBank());
            newCreditCard.setNumber(payload.getCardNumber());
            newCreditCard.setOwner(foundUser);

            CreditCard CreditCardToSave = creditCardRepository.save(newCreditCard);
            return ResponseEntity.ok(CreditCardToSave.getId());
        } else {// when user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        // TODO: Create a credit card entity, and then associate that credit card with user with given userId
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        //       return null;
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {// if there is such users
            User userFound = userOptional.get();
            //user.getCreditCards return List<CreditCard>, use Java Stream Stream API to transform the
            // list of CreditCard entities into a list of CreditCardView instances.
            List<CreditCardView> creditCardViews = userFound.getCreditCards().stream()
                    .map(card -> CreditCardView.builder()
                            .issuanceBank(card.getIssuanceBank())
                            .number(card.getNumber())
                            .build())
                    .collect(Collectors.toList());
            // it is an example of the Builder pattern
            return ResponseEntity.ok(creditCardViews);
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
        // TODO: return a list of all credit card associated with the given userId, using CreditCardView class
        //       if the user has no credit card, return empty list, never return null
        //        return null;
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByNumber(creditCardNumber);
        if (creditCardOptional.isPresent()){
            CreditCard creditCard = creditCardOptional.get();
             User userTarget = creditCard.getOwner();
            if(userTarget != null){
                return ResponseEntity.ok(userTarget.getId());
            }else{
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
        // TODO: Given a credit card number, efficiently find whether there is a user associated with the credit card
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
//        return null;
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<Void> updateCreditCardBalance(@RequestBody UpdateBalancePayload[] payload) {
        try {
            for (UpdateBalancePayload transaction : payload) {
                creditCardService.updateBalanceHistory(transaction.getCreditCardNumber(), transaction.getTransactionTime(), transaction.getCurrentBalance());
            }
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        //TODO: Given a list of transactions, update credit cards' balance history.
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a transaction of {date: 4/10, amount: 10}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 110}]
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //       is not associated with a card.

//        return null;
    }

}
