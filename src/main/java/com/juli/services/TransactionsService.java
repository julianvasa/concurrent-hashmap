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

@Service
public class TransactionsService {

    private final ConcurrentHashMap<Long, Transaction> storage = new ConcurrentHashMap<>();

    public Statistics getStatistics() {
        //storage.entrySet().removeIf((k) -> (Duration.between(k.getValue().getTimestamp(), LocalDateTime.now(Clock.systemUTC())).getSeconds() >= 60));

        final AtomicReference<BigDecimal> tot = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> avg = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.ZERO);
        final AtomicReference<BigDecimal> min = new AtomicReference<>(BigDecimal.ZERO);

        AtomicInteger tmp = new AtomicInteger();
        storage.forEach((k, v) -> {
            // delete transactions older than 60 seconds
            if (Duration.between(v.getTimestamp(), LocalDateTime.now(Clock.systemUTC())).getSeconds() >= 60) {
                // Delete transactions >= 60 seconds
                storage.remove(k);
            }
        });

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

        // calc avg
        if (tot.get().intValue() > 0) {
            avg.set(sum.get().divide(tot.get(), 2, RoundingMode.HALF_UP));
        }

        // return to the controller
        return new Statistics(
            NumberUtils.roundAndScale(sum.get()),
            NumberUtils.roundAndScale(avg.get()),
            NumberUtils.roundAndScale(max.get()),
            NumberUtils.roundAndScale(min.get()),
            tot.get().longValue()
        );
    }

    public HttpStatus postTransactions(String txn) {
        HttpStatus status = HttpStatus.NO_CONTENT;
        try {
            Gson gson = new Gson();
            // create json object from string json
            JsonObject jsonObject = gson.fromJson(txn, JsonObject.class);
            // check if the fields are formatted correctly
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
            LocalDateTime timeStamp = LocalDateTime.parse(jsonObject.get("timestamp").getAsString(), dateTimeFormatter);
            BigDecimal amount = jsonObject.get("amount").getAsBigDecimal();
            /*DecimalFormat f = new DecimalFormat("##0.00");
            f.format(amount.setScale(2, RoundingMode.HALF_UP));*/

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


    public HttpStatus deleteTransactions() {
        storage.clear();
        return HttpStatus.NO_CONTENT;
    }
}
