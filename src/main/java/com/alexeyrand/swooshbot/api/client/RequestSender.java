package com.alexeyrand.swooshbot.api.client;

import com.alexeyrand.swooshbot.datamodel.dto.*;
import com.alexeyrand.swooshbot.datamodel.dto.Package;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@RequiredArgsConstructor
public class RequestSender {

    public String getPVZ(URI url, String PVZCode) throws JsonProcessingException {
        Map<Boolean, String> resultRequest = new HashMap<>();
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3MTUyMTAxMDQsImF1dGhvcml0aWVzIjpbImZ1bGwtbmFtZTrQk9GD0YDQsdC40Ycg0JTQvNC40YLRgNC40Lkg0JDQu9C10LrRgdCw0L3QtNGA0L7QstC40YciLCJjbGllbnQtY2l0eTrQnNC-0YHQutCy0LAsINCc0L7RgdC60LLQsCIsInNoYXJkLWlkOnJ1LTA0IiwiY2xpZW50LWVtYWlsczp5by15by0xMjNAbWFpbC5ydSx5by15by0xMjNAbWFpbC5ydSIsImNvbnRyYWdlbnQtdXVpZDpmZjU4Y2MxNC04MjdlLTQ2MzItOTRkZS02MGI1NzA0NjVhZjEiLCJhY2NvdW50LWxhbmc6cnVzIiwiY2xpZW50LWlkLWVjNTpmZjU4Y2MxNC04MjdlLTQ2MzItOTRkZS02MGI1NzA0NjVhZjEiLCJjb250cmFjdC1pZDpjODBkZDlkNy0xOGM0LTRlNDUtYTVlZi1mNTlmMjBlMGE3NWUiLCJhcGktdmVyc2lvbjoxLjEiLCJhY2NvdW50LXV1aWQ6MmUxOGU0ZGQtNGMxMC00ZGZmLWI2ZjEtNWYyZDRkNWMwYjg2IiwiY29udHJhY3Q6U1otVFlVTTE0Ni0xMCIsInNvbGlkLWFkZHJlc3M6ZmFsc2UiLCJjbGllbnQtaWQtZWM0Om51bGwiXSwianRpIjoiajZLQjNMajhwdlp2VWlZeG5mOWJOY25zSElRIiwiY2xpZW50X2lkIjoiM1BnWDVhZWNQUjI5UDFHbHpWemRXalZva0FpYnJ1VWUifQ.Ge_rcPWxsie0b_bywBk1inT57kelAfH2cuZy1ljodfinTFr9x9fyBomxhPtJ0hL9WvyRlh1N_EDyqH6nYytXn7O2Zw38gGwsE9aQ1pi5KSEBxNjl3DUnha6FBwCm5nbyKA5DAMpoZfbec438nyL8OJ_5kEdyGmtXzAUaGboLFgCQ32S1fvJqlkzSpHDLrwAH7buKpow6xbH-J3RhgGYyE1pgETesvL4r7WjcRXdnEWHBOhjKlIRRLy4ei_MtxvAd4sJID2BwxxVvThsBldcWLQrLrT-6WNn9geUa5WITle562kbwohBQ4hbm4inF1dNzhsyrwnRciCBs4YCiDNEgyQ";

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
                String response = responseFuture.get().body();
                if (!response.isEmpty()) {
                    return response;
                }
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return "";
    }

    public void createOrder(URI url) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3MTQ5NDAwNjMsImF1dGhvcml0aWVzIjpbInNoYXJkLWlkOnJ1LTAxIiwiY2xpZW50LWNpdHk60J3QvtCy0L7RgdC40LHQuNGA0YHQuiwg0J3QvtCy0L7RgdC40LHQuNGA0YHQutCw0Y8g0L7QsdC70LDRgdGC0YwiLCJjb250cmFjdDrQmNCcLdCg0KQt0JPQm9CTLTIyIiwiYWNjb3VudC1sYW5nOnJ1cyIsImFwaS12ZXJzaW9uOjEuMSIsImFjY291bnQtdXVpZDplOTI1YmQwZi0wNWE2LTRjNTYtYjczNy00Yjk5YzE0ZjY2OWEiLCJjbGllbnQtaWQtZWM1OmVkNzVlY2Y0LTMwZWQtNDE1My1hZmU5LWViODBiYjUxMmYyMiIsImNvbnRyYWN0LWlkOmRlNDJjYjcxLTZjOGMtNGNmNS04MjIyLWNmYjY2MDQ0ZThkZiIsImNsaWVudC1pZC1lYzQ6MTQzNDgyMzEiLCJjb250cmFnZW50LXV1aWQ6ZWQ3NWVjZjQtMzBlZC00MTUzLWFmZTktZWI4MGJiNTEyZjIyIiwic29saWQtYWRkcmVzczpmYWxzZSIsImZ1bGwtbmFtZTrQotC10YHRgtC40YDQvtCy0LDQvdC40LUg0JjQvdGC0LXQs9GA0LDRhtC40Lgg0JjQnCJdLCJqdGkiOiJxLWFKQzYzQ3U4Vnl2ZmM3UkVXN0RDcVVKMEUiLCJjbGllbnRfaWQiOiJFTXNjZDZyOUpuRmlRM2JMb3lqSlk2ZU03OEpySmNlSSJ9.a2lnhttvAba1_8wDgD2lT0TbD8kfpzQEVVXXL1y1fKuPfP9ojkOZH6qvp23UfRQYWcz72GHxvq2am8CD7uGLv47fGsDrKGKsftI6hY8y5dXcZj4VHjQfZPnUqLc6VdQ8MKnX9qlfSiYROb4cXEb9y5KYw2qz2vZb8Y0-f7jndvJkPM_cBqHnHDECZrC6aOJ8rIEWx4sFQGuEUdzyrCrXHDCVlFTlktWG9j38f-enkyLS45q9U__odnMqecRz0Kgjj1sp6ujo1j5-SzdeURY1nI5q-ftnxa8IJgXQImfdGvS246LWVaV_yjCbVbwgJ1qFAiyb-otNqphXRJzHxF06pg";
        Phone phone = new Phone("+79150187948");
        Item item = new Item("Товар", "123213", new Money(213f), 213f, 100f, 123);
        Package pckage = new Package("1", 100, 100, 100, 100, List.of(item));
        SdekOrderRequest orderRequest = SdekOrderRequest.builder()
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
