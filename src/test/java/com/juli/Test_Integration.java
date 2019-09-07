package com.juli;

import com.juli.models.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Test_Integration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
    private LocalDateTime now;
    private String request;
    private HttpStatus expectedStatus;
    private HttpStatus actualStatus;

    @Before
    public void cleanUp(){
        restTemplate.delete("http://localhost:" + port + "/transactions", String.class);
    }

    @Test
    public void whenPostTransactionWithin60seconds_thenCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        expectedStatus = HttpStatus.CREATED;
        assertThat(restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class).getStatusCode()).isEqualTo(expectedStatus);
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenCalculateSumAndCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getSum()).isEqualTo("1508.30");

    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenCalculateAvgAndCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getAvg()).isEqualTo("502.77");
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenCalculateMaxAndCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getMax()).isEqualTo("1363.60");

    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenCalculateMinAndCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getMin()).isEqualTo("11.50");

    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenCalculateCountAndCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getCount()).isEqualTo(3);
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenWait1Min_thenSumShouldBeZero() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getSum()).isEqualTo("0.00");
       }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenWait1Min_thenAvgShouldBeZero() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getAvg()).isEqualTo("0.00");
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenWait1Min_thenMaxShouldBeZero() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getMax()).isEqualTo("0.00");
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenWait1Min_thenMinShouldBeZero() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getMin()).isEqualTo("0.00");
    }

    @Test
    public void whenPosted3TransactionsWithin60seconds_thenWait1Min_thenCountShouldBeZero() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusSeconds(10);

        request = "{" +
            "\"amount\": \"133.20\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";
        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(5);
        request = "{" +
            "\"amount\": \"11.50\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        now = now.minusSeconds(2);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \"" + now.format(dateTimeFormatter) + "\"" +
            "}";

        restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class);

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getCount()).isEqualTo(0);
    }


    @Test
    public void whenPostTransactionMoreThan60seconds_thenHttpStatusIsNoContentAndNotCreated() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusMinutes(20);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \""+now.format(dateTimeFormatter)+"\"" +
            "}";

        expectedStatus = HttpStatus.NO_CONTENT;
        assertThat(restTemplate.postForEntity("http://localhost:" + port + "/transactions", request, String.class).getStatusCode()).isEqualTo(expectedStatus);
    }

    @Test
    public void whenPostTransactionMoreThan60seconds_thenTransactionIsNotCreatedAndGetStatisticsReturnsEmptyList() {
        now = LocalDateTime.now(Clock.systemUTC());
        now = now.minusMinutes(20);
        request = "{" +
            "\"amount\": \"1363.60\"," +
            "\"timestamp\": \""+now.format(dateTimeFormatter)+"\"" +
            "}";
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/statistics", Statistics.class).getCount()).isEqualTo(0);
    }
}
