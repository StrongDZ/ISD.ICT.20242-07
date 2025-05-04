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
    private final IPaymentSystem vnpay = new VNPaySubsystem();


}