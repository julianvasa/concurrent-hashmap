package com.juli.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.juli.models.Statistics;
import com.juli.models.Transaction;
import com.juli.utils.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * LastMinuteTransactionService is a service used to store, retrieve and delete transactions performed the last 60 seconds
 */
@Service
public class LastMinuteTransactionsService implements LastMinuteService {

    private final ConcurrentHashMap<Long, Transaction> storage = new ConcurrentHashMap<>();

    /**
     * Get Avg, Min, Max, Tot and Count statistics for the stored transactions in the last 60 seconds
     * Also delete transactions older than 60 seconds when the GET request is performed
     * @return Statistics object
     */
    public Statistics getStatistics() {
        final AtomicReference<BigDecimal> tot = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> avg = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> min = new AtomicReference<>(BigDecimal.ZERO);

        AtomicInteger tmp = new AtomicInteger();
        deleteTransactionsOlderThan60seconds();

        calculateMinMaxSumTot(tot, sum, max, min, tmp);
        calcAvg(tot, sum, avg);

        // return to the controller
        return new Statistics(
            NumberUtils.roundAndScale(sum.get()),
            NumberUtils.roundAndScale(avg.get()),
            NumberUtils.roundAndScale(max.get()),
            NumberUtils.roundAndScale(min.get()),
            tot.get().longValue()
        );
    }

    /** Calculate Avg for the transactions performed in the last 60 seconds **/
    private void calcAvg(AtomicReference<BigDecimal> tot, AtomicReference<BigDecimal> sum, AtomicReference<BigDecimal> avg) {
        if (tot.get().intValue() > 0) {
            avg.set(sum.get().divide(tot.get(), 2, RoundingMode.HALF_UP));
        }
    }

    /** Calculate Min, Max, Tot and Sum for the transactions performed in the last 60 seconds **/
    private void calculateMinMaxSumTot(AtomicReference<BigDecimal> tot, AtomicReference<BigDecimal> sum, AtomicReference<BigDecimal> max, AtomicReference<BigDecimal> min, AtomicInteger tmp) {
        storage.forEach((k, v) -> {
            // init min with first value
            if (tmp.get() == 0) {
                min.set(v.getAmount());
                tmp.getAndIncrement();
            }
            // calculate min, max, sum, tot
            if (max.get().compareTo(v.getAmount()) < 0) max.set(v.getAmount());
            if (min.get().compareTo(v.getAmount()) > 0) min.set(v.getAmount());
            sum.accumulateAndGet(v.getAmount(), BigDecimal::add);
            tot.accumulateAndGet(BigDecimal.ONE, BigDecimal::add);
        });
    }

    /** Delete transactions older than 60 seconds **/
    private void deleteTransactionsOlderThan60seconds() {
        storage.forEach((k, v) -> {
            // delete transactions older than 60 seconds
            if (Duration.between(v.getTimestamp(), LocalDateTime.now(Clock.systemUTC())).getSeconds() >= 60) {
                // Delete transactions >= 60 seconds
                storage.remove(k);
            }
        });
    }

    /**
     * Insert a new transaction
     * @param txn Input Transaction as a string
     * @return HttpStatus
     */
    public HttpStatus insert(String txn) {
        HttpStatus status = HttpStatus.NO_CONTENT;
        try {
            Gson gson = new Gson();
            // create json object from string json
            JsonObject jsonObject = gson.fromJson(txn, JsonObject.class);
            // check if the fields are formatted correctly
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
            LocalDateTime timeStamp = LocalDateTime.parse(jsonObject.get("timestamp").getAsString(), dateTimeFormatter);
            BigDecimal amount = jsonObject.get("amount").getAsBigDecimal();
            // create the transaction object with the well formatted fields
            Transaction transaction = new Transaction(amount, timeStamp);
            // create datetime of current instant in UTC
            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
            // calc duration between transaction timestamp (UTC) and now (UTC)
            Duration duration = Duration.between(transaction.getTimestamp(), now);
            // if duration seconds < 60 insert
            if (!duration.isNegative() && duration.getSeconds() < 60) {
                storage.put(System.nanoTime(), new Transaction(transaction.getAmount(), transaction.getTimestamp()));
                status = HttpStatus.CREATED;
            }
            else if (duration.isNegative()) {
                status = HttpStatus.UNPROCESSABLE_ENTITY;
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } catch (JsonSyntaxException e) {
            return HttpStatus.BAD_REQUEST;
        }
        return status;
    }

    /**
     * Delete all transactions
     * @return HttpStatus
     */
    public HttpStatus deleteAllData() {
        storage.clear();
        return HttpStatus.NO_CONTENT;
    }
}
