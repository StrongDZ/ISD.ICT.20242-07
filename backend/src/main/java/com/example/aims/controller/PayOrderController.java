package com.example.aims.controller;

import com.example.aims.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.aims.subsystem.IPaymentSystem;
import com.example.aims.subsystem.VNPay.VNPaySubsystem;


@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class  PayOrderController {

    private PayOrderService payOrderService;

    // Test payment request
    private final IPaymentSystem vnpay = new VNPaySubsystem();


}