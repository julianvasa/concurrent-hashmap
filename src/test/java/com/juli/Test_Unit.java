package com.juli;

import com.juli.models.Statistics;
import com.juli.services.TransactionsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class Test_Unit {

    private final TransactionsService service = new TransactionsService();
    private HttpStatus expectedStatus;
    private HttpStatus actualStatus;
    private LocalDateTime now;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    private String request;

    @Test
    public void getEmptyStatistics() {
        Statistics expected = new Statistics("0.00", "0.00", "0.00", "0.00", 0);
        Statistics actual = service.getStatistics();
        assertEquals(expected, actual);
    }

    @Test
    public void whenPostTransactionWithin60seconds_thenCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(30);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        actualStatus = service.postTransactions(request);
        expectedStatus = HttpStatus.CREATED;
        assertEquals(actualStatus, expectedStatus);
    }

    @Test
    public void whenPostTransactionMoreThan60seconds_thenNotCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(61);
        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        actualStatus = service.postTransactions(request);
        expectedStatus = HttpStatus.NO_CONTENT;
        assertEquals(actualStatus, expectedStatus);

    }


    @Test
    public void whenDateTimeNotFormattedCorrectly_thenHttpStatusUnprocessableEntityAndNotCreated() {
        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"4/23/2018 11:32 PM\"" +
            "}";
        actualStatus = service.postTransactions(request);
        expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        assertEquals(actualStatus, expectedStatus);
    }

    @Test
    public void whenAmountNotFormattedCorrectly_thenHttpStatusUnprocessableEntityAndNotCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(1);
        request = "{" +
            "\"amount\": \"One hundred\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        actualStatus = service.postTransactions(request);
        expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        assertEquals(actualStatus, expectedStatus);
    }

    @Test
    public void whenRequestIsNotJSON_thenHttpStatusBadRequestAndNotCreated() {
        request = "Hello World!";
        actualStatus = service.postTransactions(request);
        expectedStatus = HttpStatus.BAD_REQUEST;
        assertEquals(actualStatus, expectedStatus);
    }

    @Test
    public void deleteTransactions() {
        HttpStatus actualStatus = service.deleteTransactions();
        HttpStatus expectedStatus = HttpStatus.NO_CONTENT;
        assertEquals(actualStatus, expectedStatus);
    }

}
