package com.alexeyrand.swooshbot.api.client;

import com.alexeyrand.swooshbot.datamodel.entity.*;
import com.alexeyrand.swooshbot.datamodel.entity.Package;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@RequiredArgsConstructor
public class RequestSender {



    public static void getRegions(URI url) throws JsonProcessingException {
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3MTQ4MzMxMDksImF1dGhvcml0aWVzIjpbInNoYXJkLWlkOnJ1LTAxIiwiY2xpZW50LWNpdHk60J3QvtCy0L7RgdC40LHQuNGA0YHQuiwg0J3QvtCy0L7RgdC40LHQuNGA0YHQutCw0Y8g0L7QsdC70LDRgdGC0YwiLCJjb250cmFjdDrQmNCcLdCg0KQt0JPQm9CTLTIyIiwiYWNjb3VudC1sYW5nOnJ1cyIsImFjY291bnQtdXVpZDplOTI1YmQwZi0wNWE2LTRjNTYtYjczNy00Yjk5YzE0ZjY2OWEiLCJhcGktdmVyc2lvbjoxLjEiLCJjbGllbnQtaWQtZWM1OmVkNzVlY2Y0LTMwZWQtNDE1My1hZmU5LWViODBiYjUxMmYyMiIsImNvbnRyYWN0LWlkOmRlNDJjYjcxLTZjOGMtNGNmNS04MjIyLWNmYjY2MDQ0ZThkZiIsImNsaWVudC1pZC1lYzQ6MTQzNDgyMzEiLCJjb250cmFnZW50LXV1aWQ6ZWQ3NWVjZjQtMzBlZC00MTUzLWFmZTktZWI4MGJiNTEyZjIyIiwic29saWQtYWRkcmVzczpmYWxzZSIsImZ1bGwtbmFtZTrQotC10YHRgtC40YDQvtCy0LDQvdC40LUg0JjQvdGC0LXQs9GA0LDRhtC40Lgg0JjQnCJdLCJqdGkiOiI3Y3hVUS04a1pOd0xNa0tfMm5ZZVg5MGNNem8iLCJjbGllbnRfaWQiOiJFTXNjZDZyOUpuRmlRM2JMb3lqSlk2ZU03OEpySmNlSSJ9.Xds81xy2oihnPPb675i9pO0CkcHU6tiLW0vWZ2HPazJudnl5ohl8GqCN0j0Yri-b-DeZcxuTfubSKDKfAvbvZBPKCfYFez1sVrH1H9CirhXUinZAq8ETDDOVMkVDNVg9r50LjRU1o8J2cfqDX5j_SyzpgWFEyqEopgnqtiJf3c3UtawDUEhbtM_XKJKt6TYR2_9OM_JB8B1dOOvi4Qpw-wb2gI-WG1JrLimEHFeKFU_Kfx2bGWaY6jeZ1Hh2YAb0pJsib48MbBhJ7FtLH0sVweCPcjjSkTl6USHp_wR1PwYzmm6Y3DGcwMMts5mOSz2m1wyDgQ_7DwvqpXE2izP9LQ";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                //.authenticator()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .GET()
                .header("Authorization", token)
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            if (responseFuture.get().statusCode() == 200) {
                System.out.println(responseFuture.get().body());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createOrder(URI url) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3MTQ5NDAwNjMsImF1dGhvcml0aWVzIjpbInNoYXJkLWlkOnJ1LTAxIiwiY2xpZW50LWNpdHk60J3QvtCy0L7RgdC40LHQuNGA0YHQuiwg0J3QvtCy0L7RgdC40LHQuNGA0YHQutCw0Y8g0L7QsdC70LDRgdGC0YwiLCJjb250cmFjdDrQmNCcLdCg0KQt0JPQm9CTLTIyIiwiYWNjb3VudC1sYW5nOnJ1cyIsImFwaS12ZXJzaW9uOjEuMSIsImFjY291bnQtdXVpZDplOTI1YmQwZi0wNWE2LTRjNTYtYjczNy00Yjk5YzE0ZjY2OWEiLCJjbGllbnQtaWQtZWM1OmVkNzVlY2Y0LTMwZWQtNDE1My1hZmU5LWViODBiYjUxMmYyMiIsImNvbnRyYWN0LWlkOmRlNDJjYjcxLTZjOGMtNGNmNS04MjIyLWNmYjY2MDQ0ZThkZiIsImNsaWVudC1pZC1lYzQ6MTQzNDgyMzEiLCJjb250cmFnZW50LXV1aWQ6ZWQ3NWVjZjQtMzBlZC00MTUzLWFmZTktZWI4MGJiNTEyZjIyIiwic29saWQtYWRkcmVzczpmYWxzZSIsImZ1bGwtbmFtZTrQotC10YHRgtC40YDQvtCy0LDQvdC40LUg0JjQvdGC0LXQs9GA0LDRhtC40Lgg0JjQnCJdLCJqdGkiOiJxLWFKQzYzQ3U4Vnl2ZmM3UkVXN0RDcVVKMEUiLCJjbGllbnRfaWQiOiJFTXNjZDZyOUpuRmlRM2JMb3lqSlk2ZU03OEpySmNlSSJ9.a2lnhttvAba1_8wDgD2lT0TbD8kfpzQEVVXXL1y1fKuPfP9ojkOZH6qvp23UfRQYWcz72GHxvq2am8CD7uGLv47fGsDrKGKsftI6hY8y5dXcZj4VHjQfZPnUqLc6VdQ8MKnX9qlfSiYROb4cXEb9y5KYw2qz2vZb8Y0-f7jndvJkPM_cBqHnHDECZrC6aOJ8rIEWx4sFQGuEUdzyrCrXHDCVlFTlktWG9j38f-enkyLS45q9U__odnMqecRz0Kgjj1sp6ujo1j5-SzdeURY1nI5q-ftnxa8IJgXQImfdGvS246LWVaV_yjCbVbwgJ1qFAiyb-otNqphXRJzHxF06pg";
        Phone phone = new Phone("+79150187948");
        Item item = new Item("Товар", "123213", new Money(213f), 213f, 100f, 123);
        Package pckage = new Package("1", 100, 100, 100, 100, List.of(item));
        SdekOrderRequest orderRequest = SdekOrderRequest.builder()
                .type(1)
                .tariff_code(136)
                .comment("Тестовый заказ")
                .shipment_point("ABK1")
                .delivery_point("AST7")
                //.date_invoice(new Date())
                //.shipper_name()
                //.shipper_address()
                .delivery_recipient_cost(new Money(155.0f))
                //.seller()
                .recipient(new Contact("ContactName", List.of(phone)))
                //.from_location()
                //.to_location()
                .packages(List.of(pckage))
                .build();
        String jsonOrderRequest = mapper.writeValueAsString(orderRequest);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                //.authenticator()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonOrderRequest))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            //if (responseFuture.get().statusCode() == 200) {
                System.out.println(responseFuture.get().body());
            //}
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
