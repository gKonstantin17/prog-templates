package gk17.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.dynomake.yookassa.Yookassa;
import me.dynomake.yookassa.exception.BadRequestException;
import me.dynomake.yookassa.exception.UnspecifiedShopInformation;
import me.dynomake.yookassa.model.Amount;
import me.dynomake.yookassa.model.Confirmation;
import me.dynomake.yookassa.model.Payment;
import me.dynomake.yookassa.model.Refund;
import me.dynomake.yookassa.model.request.PaymentRequest;
import me.dynomake.yookassa.model.request.RefundRequest;
import me.dynomake.yookassa.model.request.receipt.Receipt;
import me.dynomake.yookassa.model.request.receipt.ReceiptCustomer;
import me.dynomake.yookassa.model.request.receipt.ReceiptItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${YOU_SHOP_TOKEN}")
    String YOU_SHOP_TOKEN;
    @Value("${YOU_SHOP_IDENTIFIER}")
    String YOU_SHOP_IDENTIFIER;


    // РЕШЕНИЕ ЗАДАЧИ ИДЕМПОТЕНТНОСТИ
    /*
    * при создании оплаты, появляется object.id например в payment будет:
    * "object" : {
        "id" : "31544fb6-000f-5001-9000-15a535aa7aac"
    * }
    *
    * в order в бд хранить этот id как payment_id
    *
    * 1. проверить у order наличие payment_id и status
    * 2. если заказ всё ещё status="в процессе оплаты", то проверить его в юкассе
    * метод getPayment реализован, но должен использовать в сервисе, а не здесь
    * 3. поменять статус на оплачен или отменен
    *
    * а когда проверять?
    * допустим заказ создан и через полчаса оплата, оплата отменилась
    * да просто хранить дату оплаты и каждые 10 минут проверять, если за 10 мин не оплачено, то заказ отменен
    * */

    @PostMapping("/buy")
    public Payment getDashboard(@RequestBody PaymentRequestDto requestDto) throws UnspecifiedShopInformation, BadRequestException, IOException {
        int shopId  = Integer.parseInt(YOU_SHOP_IDENTIFIER);
        Yookassa yookassa = Yookassa
                .initialize(shopId , YOU_SHOP_TOKEN);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("order_id", requestDto.orderId());
        metadata.put("user_id", requestDto.userId());

        Payment payment = yookassa.createPayment(PaymentRequest.builder()
                .amount(new Amount("2.00", "RUB"))
                .description("This is a test payment!")
                .receipt(Receipt.builder()
                        .customer(ReceiptCustomer.builder().email("dynomake@gmail.com").build())
                        .items(Arrays.asList(ReceiptItem.builder()
                                .amount(new Amount("2.00", "RUB"))
                                .quantity(1)
                                .subject("service")
                                .paymentMode("full_payment")
                                .vat(1)
                                .description("Test product").build()))
                        .build())
                .savePaymentMethod(true)
                .metadata(metadata)
                .confirmation(Confirmation.builder()
                        .type("redirect")
                        .returnUrl("https://wew.cloudpub.ru/")
                        .build())
                .build());

        System.out.println("bill link: " + payment.getConfirmation().getConfirmationUrl());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String paymentJson = mapper.writeValueAsString(payment);

        System.out.println("=== Payment Full Response ===");
        System.out.println(paymentJson);
        return payment;
    }

    @PostMapping("/get")
    public Payment getPayment(@RequestBody PaymentId payment_id) throws UnspecifiedShopInformation, BadRequestException, IOException {
        System.out.println("Ищем платеж");
        System.out.println(payment_id.paymentid());

        int shopId  = Integer.parseInt(YOU_SHOP_IDENTIFIER);
        Yookassa yookassa = Yookassa
                .initialize(shopId , YOU_SHOP_TOKEN);


        Payment payment = yookassa.getPayment(payment_id.paymentid());

        System.out.println("=== Получение готового платежа ===");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String paymentJson = mapper.writeValueAsString(payment);

        System.out.println("=== Payment Full Response ===");
        System.out.println(paymentJson);
        return payment;
    }

    @PostMapping("/refund")
    public Refund getPayment(@RequestBody RefundRequest refund) throws UnspecifiedShopInformation, BadRequestException, IOException {

        int shopId  = Integer.parseInt(YOU_SHOP_IDENTIFIER);
        Yookassa yookassa = Yookassa
                .initialize(shopId , YOU_SHOP_TOKEN);


        Refund payment = yookassa.createRefund(refund);

        System.out.println("=== Получение готового платежа ===");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String paymentJson = mapper.writeValueAsString(payment);

        System.out.println("=== Payment Full Response ===");
        System.out.println(paymentJson);
        return payment;
    }
}
record PaymentRequestDto (
        Integer orderId,
        String userId
) {}

record PaymentId (
        UUID paymentid
) {}

record RefundDto (UUID paymentid, int amount) {}