package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Crud repository to store credit cards
 */
@Repository("CreditCardRepo")
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {
    Optional<CreditCard> findByNumber(String creditCardNumber);
    // we need to add this methods, since it is not default
    // don't need to provide an implementation for the query methods in the repository interfaces.
    // Spring Data JPA automatically generates the implementation for you based on the method signature.
}
