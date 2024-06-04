package com.alexeyrand.swooshbot.model.entity.sdek;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "sdek_order_info")
@AllArgsConstructor
@RequiredArgsConstructor
public class SdekOrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    Long chatId;
    Integer tariffCode;   ///
    //    String comment;         ///
    String shipmentPoint;  ///
    String deliveryPoint;  ///
    String shipmentCity;
    String deliveryCity;
    Date dateInvoice;      ///
    String shipperName;    ///
    String shipperAddress; ///

    // Отправитель
    String senderName;
    String senderNumber;


    // List<Package> packages;
    String packageNumber;
    Integer packageWeight;
    Integer packageLength;
    Integer packageWidth;
    Integer packageHeight;

    // Получатель
    String 	recipientName;
    String 	recipientNumber;
    String itemName;
    String itemWare_key;
    Float moneyPayment;
    Float itemCost;
    Float itemWeight;
    Integer itemWeight_gross;
    Integer itemAmount;

    @Column(name = "info", length = 1024)
    String info;
}
