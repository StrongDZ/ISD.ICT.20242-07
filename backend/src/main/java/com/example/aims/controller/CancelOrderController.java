package com.example.aims.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cancel-order")
@CrossOrigin(origins = "*")
public class CancelOrderController {
    // @Autowired
    // private CancelOrderService cancelOrderService;
}
