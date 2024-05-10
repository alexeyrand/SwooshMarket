package com.alexeyrand.swooshbot.api.client;

import com.alexeyrand.swooshbot.datamodel.dto.*;
import com.alexeyrand.swooshbot.datamodel.dto.Package;
import com.alexeyrand.swooshbot.datamodel.entity.sdek.SdekOrderInfo;
import com.alexeyrand.swooshbot.datamodel.service.SdekOrderRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@RequiredArgsConstructor
public class RequestSender {

    private final SdekOrderRequestService sdekOrderRequest;

    @SneakyThrows
    public String getPVZ(String PVZCode) throws JsonProcessingException {
        String URL = "https://api.edu.cdek.ru/v2/deliverypoints";
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?code=" + PVZCode.toUpperCase()))
                .timeout(Duration.of(5, SECONDS))
                .GET()
                .header("Authorization", getToken())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            if (!responseBody.isEmpty()) {
                return responseBody;
            }
            return "";
        }
        return "";
    }

    @SneakyThrows
    public void getCityCode(Long chatId) throws JsonProcessingException {
        String URL = "https://api.edu.cdek.ru/v2/location/cities?size=1&page=0";
        SdekOrderInfo sdekOrderInfo = sdekOrderRequest.findSdekOrderRequestByChatId(chatId).orElseThrow();
        String shipmentCity = sdekOrderInfo.getShipmentCity();
        String deliveryCity = sdekOrderInfo.getDeliveryCity();
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "&city=" + shipmentCity))
                .timeout(Duration.of(5, SECONDS))
                .GET()
                .header("Authorization", getToken())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            if (!responseBody.isEmpty()) {
                sdekOrderInfo.setShipmentCity("r");
            }

        }

    }


    @SneakyThrows
    public static void createOrder(URI url) throws JsonProcessingException {

        final ObjectMapper mapper = new ObjectMapper();

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
                .header("Authorization", getToken())
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

//    public String calculateTheCostOrder(Long chatId) {
//        SdekOrderInfo sdekOrderInfo = sdekOrderRequest.findSdekOrderRequestByChatId(chatId).orElseThrow();
//
//    }

    private static String getToken() throws IOException, InterruptedException {
        String params = Map.of(
                        "grant_type", "client_credentials",
                        "client_id", "EMscd6r9JnFiQ3bLoyjJY6eM78JrJceI",
                        "client_secret", "PjLZkKBHEiLK3YsjtNrt3TGNG0ahs3kG")
                .entrySet()
                .stream()
                .map(entry -> Stream.of(
                                URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("="))
                ).collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.edu.cdek.ru/v2/oauth/token?parameters"))
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString("{grant_type: \"client_credentials\", client_id: \"EMscd6r9JnFiQ3bLoyjJY6eM78JrJceI\", client_secret: \"PjLZkKBHEiLK3YsjtNrt3TGNG0ahs3kG\"}"))
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> responseFuture = client.send(request, HttpResponse.BodyHandlers.ofString());
        return "Bearer " + responseFuture.body().split("\"")[3];
    }
}
