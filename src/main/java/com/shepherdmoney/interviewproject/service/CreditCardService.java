package com.shepherdmoney.interviewproject.service;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;
    //dependency injection
    public void updateBalanceHistory(String creditCardNumber, Instant transactionTime, double transactionAmount) {
        Optional<CreditCard> creditCardOptional = creditCardRepository.findByNumber(creditCardNumber);
        if (!creditCardOptional.isPresent()) {
            throw new IllegalArgumentException("Credit card number not associated with a card.");
        }
        CreditCard creditCard = creditCardOptional.get();
        LocalDate transactionDate = transactionTime.atZone(ZoneId.systemDefault()).toLocalDate();

        List<BalanceHistory> updatedBalanceHistory = new ArrayList<>();
        boolean inserted = false;
//        //previousBalance
//        double previousBalance = 0;


        for (BalanceHistory entry : creditCard.getBalanceHistory()) {
            LocalDate entryDate = entry.getDate().atZone(ZoneId.systemDefault()).toLocalDate();

            if (transactionDate.isBefore(entryDate) || transactionDate.isEqual(entryDate)) {
                BalanceHistory updatedEntry = new BalanceHistory();
                updatedEntry.setBalance(entry.getBalance() + transactionAmount);
                updatedEntry.setDate(entry.getDate());
                updatedBalanceHistory.add(updatedEntry);
                if(transactionDate.isEqual(entryDate)){
                    inserted = true;
                }
            } else { // if the transaction date is after entryDate
                updatedBalanceHistory.add(entry);
            }
        }

        if (!inserted) { // it is a new date
            //previous balance
            LocalDate previousDate = transactionDate.minusDays(1);
            Optional<BalanceHistory> previousEntryOptional = creditCard.getBalanceHistory().stream()
                    .filter(e -> e.getDate().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(previousDate))
                    .findFirst();

            double previousBalance = previousEntryOptional.isPresent() ? previousEntryOptional.get().getBalance() : 0;
            //end
            BalanceHistory newBalanceHistory = new BalanceHistory();
//          newBalanceHistory.setBalance(transactionAmount);
            newBalanceHistory.setBalance(transactionAmount + previousBalance);
            newBalanceHistory.setDate(transactionTime);
            updatedBalanceHistory.add(newBalanceHistory);
        }

        creditCard.getBalanceHistory().clear();
        creditCard.getBalanceHistory().addAll(updatedBalanceHistory);
        creditCardRepository.save(creditCard);
    }


}
