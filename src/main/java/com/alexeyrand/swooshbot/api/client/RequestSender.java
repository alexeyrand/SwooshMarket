package com.alexeyrand.swooshbot.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class RequestSender {
    public static void getRegions(URI url) throws JsonProcessingException {

        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJvcmRlcjphbGwiLCJwYXltZW50OmFsbCJdLCJleHAiOjE3MTQ3ODI1NjEsImF1dGhvcml0aWVzIjpbInNoYXJkLWlkOnJ1LTAxIiwiY2xpZW50LWNpdHk60J3QvtCy0L7RgdC40LHQuNGA0YHQuiwg0J3QvtCy0L7RgdC40LHQuNGA0YHQutCw0Y8g0L7QsdC70LDRgdGC0YwiLCJhY2NvdW50LWxhbmc6cnVzIiwiY29udHJhY3Q60JjQnC3QoNCkLdCT0JvQky0yMiIsImFwaS12ZXJzaW9uOjEuMSIsImFjY291bnQtdXVpZDplOTI1YmQwZi0wNWE2LTRjNTYtYjczNy00Yjk5YzE0ZjY2OWEiLCJjbGllbnQtaWQtZWM1OmVkNzVlY2Y0LTMwZWQtNDE1My1hZmU5LWViODBiYjUxMmYyMiIsImNvbnRyYWN0LWlkOmRlNDJjYjcxLTZjOGMtNGNmNS04MjIyLWNmYjY2MDQ0ZThkZiIsImNsaWVudC1pZC1lYzQ6MTQzNDgyMzEiLCJjb250cmFnZW50LXV1aWQ6ZWQ3NWVjZjQtMzBlZC00MTUzLWFmZTktZWI4MGJiNTEyZjIyIiwic29saWQtYWRkcmVzczpmYWxzZSIsImZ1bGwtbmFtZTrQotC10YHRgtC40YDQvtCy0LDQvdC40LUg0JjQvdGC0LXQs9GA0LDRhtC40Lgg0JjQnCJdLCJqdGkiOiJkcDV4eW5LY29kWlhUbE5PZC1BNm1hTjlzRHMiLCJjbGllbnRfaWQiOiJFTXNjZDZyOUpuRmlRM2JMb3lqSlk2ZU03OEpySmNlSSJ9.mzrI3WOG78cwPPyjvhmBexi7_nSXLIxo9zFOKovFvqzAd_35NnuqGY8f6ntVeZxTPwIjfpy2yU0jEstOhrO9KTtllKnd27itIblxt1NPEsSRpIhR3R2OEDST91H-CxHXxeVqKIc_MCsDykpRoNcPL53-6bNx9iBeXyl5zxcA6Rq6kncGN645sJbezRrv2dIjWkdEVSPu_13hZFe4bRbg-LujivLvbXKW3vY68f2b13h60RAg8KJP8vinZO6B7X087e_TSO4BQyCI-ohPoYrFz-fBmNHT-9ImndVvcs-CeVbfgz7F3sH_mGqKvHxKUWoRabTH9Lnjlb6x8GN9pUE0ZA";

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
}
