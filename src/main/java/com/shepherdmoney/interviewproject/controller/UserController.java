package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;
    // TODO: wire in the user repository (~ 1 line)

    @Autowired
    private CreditCardRepository creditCardRepository;

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        User newUser = new User();
        //new an entity
        newUser.setName(payload.getName());
        newUser.setEmail(payload.getEmail());

        User UsertoSave = userRepository.save(newUser);
        return ResponseEntity.ok(UsertoSave.getId());
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
//        return null;
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()){
            User userFound = userOptional.get();
            userRepository.delete(userFound);
            String responseMessage = "We have successfully deleted user with ID " + userFound.getId();
            return ResponseEntity.ok(responseMessage);
        }else{
            return ResponseEntity.badRequest().build();
        }
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        //        return null;
    }
}
