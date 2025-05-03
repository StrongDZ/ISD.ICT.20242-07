package com.example.aims.controller;
import com.example.aims.dto.*;
import com.example.aims.model.*;
import com.example.aims.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class  PayOrderController {

    private PayOrderService payOrderService;

    // Test payment request
    private final IPaymentSystem vnpay = new IPaymentSystem();

    //@GetMapping("payment_test")
//    public String test() {
//        // New delivery info
//        DeliveryInfoEntity deliveryInfoEntity = new DeliveryInfoEntity();
//        deliveryInfoEntity.setAddress("Hanoi");
//        deliveryInfoEntity.setPhone("0123456789");
//        deliveryInfoEntity.setEmail("dontunderstandyou12345@gmail.com");
//        // Test payment
//        OrderEntity orderEntity = new OrderEntity();
//        orderEntity.setId(1);
//        orderEntity.setOrderDate("2021-09-01");
//        orderEntity.setShippingFee(100000.0);
//        orderEntity.setDeliveryInfo(deliveryInfoEntity);
//        return "redirect:" + vnpay.getPaymentUrl(orderEntity);
//    }

    @GetMapping("hello")
    public ResponseEntity<ResponseObject> hello() {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Hello")
                .responseCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("payment")
    public ResponseEntity<ResponseObject> callVNPAY(@RequestParam Integer id) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get payment url success")
                .responseCode(HttpStatus.OK.value())
                .data(payOrderService.getPaymentUrl(id))
                .build());
    }

    // Test payment response
//    @GetMapping("vnpay-return")
//    public ResponseEntity<ResponseObject> vnpayReturn(@RequestParam Map<String,String> allRequestParams) {
//        payOrderService.processPaymentResponse(allRequestParams);
//        return ResponseEntity.ok(ResponseObject.builder()
//                .message("Payment success")
//                .responseCode(HttpStatus.OK.value())
//                .data(allRequestParams)
//                .build());
//    }

    //Return from VNPay
    @GetMapping("vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> allRequestParams) {
        return payOrderService.processPaymentResponse(allRequestParams);
    }

    // Transaction history (test)
    @GetMapping("transaction_history")
    public ResponseEntity<ResponseObject> getTransactionHistory(@RequestParam Integer transaction_id) {
        TransactionDto transactionDto = payOrderService.getTransactionHistory(transaction_id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get transaction history success")
                .responseCode(HttpStatus.OK.value())
                .data(transactionDto)
                .build());
    }

    // Get order info
    @GetMapping("order_info")
    public ResponseEntity<ResponseObject> getOrderInfo(@RequestParam Integer id) {
        OrderInfoDto orderInfoDto = payOrderService.getOrderInfo(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get order info success")
                .responseCode(HttpStatus.OK.value())
                .data(orderInfoDto)
                .build());
    }

    // Get order product
    @GetMapping("order_product")
    public ResponseEntity<ResponseObject> getProductInfo(@RequestParam Integer id) {
        List<ProductDto> productDtos = payOrderService.getProductInfo(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get order product success")
                .responseCode(HttpStatus.OK.value())
                .data(productDtos)
                .build());
    }

    // Cancel order
    @GetMapping("cancel_order")
    public ResponseEntity<ResponseObject> cancelOrder(@RequestParam Integer id) {
        String response = payOrderService.cancelOrder(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Cancel order success")
                .responseCode(HttpStatus.OK.value())
                .data(response)
                .build());
    }

    // Test send mail
    @GetMapping("send_mail")
    public ResponseEntity<ResponseObject> sendMail() {
        payOrderService.sendMail(1);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Send mail success")
                .responseCode(HttpStatus.OK.value())
                .build());
    }
}