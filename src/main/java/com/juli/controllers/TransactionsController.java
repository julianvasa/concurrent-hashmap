package com.juli.controllers;

import com.juli.services.LastMinuteService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "Controller to store, delete and get statistics for transactions performed in the last 60 seconds")
public class TransactionsController {

    @Autowired
    LastMinuteService service;

    @GetMapping("/statistics")
    @ApiOperation(value = "Get Min, Max, Avg, Tot and Sum for transactions performed in the last 60 seconds")
    /**
     * Get Avg, Min, Max, Tot and Count statistics for the stored transactions in the last 60 seconds
     * Also delete transactions older than 60 seconds when the GET request is performed
     */
    public ResponseEntity getStatistics() {
        return new ResponseEntity<>(service.getStatistics(), HttpStatus.OK);
    }

    @PostMapping("/transactions")
    @ApiOperation(value = "Insert a new transaction")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully inserted the transaction in the local concurrent hashmap"),
        @ApiResponse(code = 422, message = "If duration is negative or there are formatting exceptions"),
        @ApiResponse(code = 400, message = "If the JSON is not formatted correctly")
    })
    /** it would be more clean if i could declare the requestbody as Transaction type so that the json request could be
    * converted directly to a Transaction type, it works indeed but it has one small issue.
    * in case of invalid json or fields not parsable it throws and error 400 (bad request) and not 422 as specified in the exercise
    * the workaround is manually parse the json string and create the Transaction object. I could not find another (nicer) workaround*  public ResponseEntity postTransactions(@RequestBody Transaction transaction) {
    */
    public ResponseEntity postTransactions(@ApiParam(value = "Transaction to be inserted in the local storage (concurrent hashmap)", required = true)
    @RequestBody String transaction) {
        HttpStatus httpStatus = service.insert(transaction);
        return new ResponseEntity<>(httpStatus);
    }

    @DeleteMapping("/transactions")
    @ApiOperation(value = "Delete all transactions")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Successfully delete all transactions")
    })
    /**
     * Delete all transactions
     */
    public ResponseEntity deleteTransactions() {
        HttpStatus httpStatus = service.deleteAllData();
        return new ResponseEntity<>(httpStatus);
    }
}
