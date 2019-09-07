package com.juli.controllers;

import com.juli.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionsController {

    @Autowired
    TransactionsService service;

    @GetMapping("/statistics")
    public ResponseEntity getStatistics() {
        return new ResponseEntity<>(service.getStatistics(), HttpStatus.OK);
    }

    @PostMapping("/transactions")
    // it would be more clean if i could declare the requestbody as Transaction type so that the json request could be
    // converted directly to a Transaction type, it works indeed but it has one small issue.
    // in case of invalid json or fields not parsable it throws and error 400 (bad request) and not 422 as specified in the exercise
    // the workaround is manually parse the json string and create the Transaction object. I could not find another (nicer) workaround
    //
    //  public ResponseEntity postTransactions(@RequestBody Transaction transaction) {
    public ResponseEntity postTransactions(@RequestBody String transaction) {
        HttpStatus httpStatus = service.postTransactions(transaction);
        return new ResponseEntity<>(httpStatus);
    }

    @DeleteMapping("/transactions")
    public ResponseEntity deleteTransactions() {
        HttpStatus httpStatus = service.deleteTransactions();
        return new ResponseEntity<>(httpStatus);
    }
}
