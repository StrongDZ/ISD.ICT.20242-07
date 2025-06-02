package com.example.aims.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.OrderDTO;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Order;
import com.example.aims.model.Product;
import com.example.aims.model.ProductOrderEntity;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.ProductOrderRepository;
import com.example.aims.repository.ProductRepository;

//***Cohesion: low to medium
// In the case of the PlaceOrderService class:
//
// It handles several different tasks:
//
// - Creating orders (e.g., saving new order entities with delivery info)
// - Calculating total amount (subtotal + VAT + delivery fee)
// - Saving delivery information
// - Linking products to orders (ProductOrderEntity creation)
// - Supporting rush order checking based on address and product property
//
// These tasks are all related to placing an order, but they represent distinct responsibilities.
// So the class has low to medium cohesion because it blends different concerns (order placement logic, pricing logic, delivery logic, and business rule checking for rush orders).

//***SRP Violation:
// This class violates the Single Responsibility Principle (SRP) by handling multiple responsibilities:
//
// - Business Logic: Checks if rush delivery is supported for a province and product.
// - Delivery Handling: Saves delivery info to the database.
// - Order Management: Creates orders and links them to products.
// - Pricing Calculation: Calculates total amount from VAT, subtotal, and delivery fee.
// 
// These should ideally be separated into smaller services or helpers focused on:
// - RushOrderChecker
// - DeliveryService
// - OrderCreationService
// - InvoiceCalculationService

//***The solution:
// To improve cohesion and follow SRP, refactor the PlaceOrderService into dedicated services:
//
// - OrderCreationService: For creating and saving orders.
// - DeliveryService: For managing and saving delivery information.
// - ProductOrderService: For handling associations between products and orders.
// - RushOrderService: To encapsulate rush order support checking.
// - PricingService: To calculate total amounts including taxes and delivery.
//
// This modular approach simplifies testing, debugging, and code reusability while keeping responsibilities clearly separated.

@Service
public class PlaceOrderService {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    private final ProductOrderRepository productOrderRepository;

    public PlaceOrderService(OrderRepository orderRepository, ProductRepository productRepository,
                             DeliveryInfoRepository deliveryInfoRepository, ProductOrderRepository productOrderRepository) {
        this.productOrderRepository = productOrderRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    public boolean checkAddressForRushOrder(String address) {
        return address.contains("Hà Nội");
    }

    public boolean checkRushOrder(DeliveryProductDTO[] deliveryProduct, DeliveryInfo deliveryInfo) {
        boolean result = false;
        System.out.println(checkAddressForRushOrder(deliveryInfo.getCity()));
        if (!checkAddressForRushOrder(deliveryInfo.getCity())) {
            return false;
        }
        for (DeliveryProductDTO product : deliveryProduct) {
            if (product.isSupportRushOrder()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public Order createOrder(InvoiceDTO invoice) {
        if (invoice == null || invoice.getDeliveryInfo() == null || invoice.getCart() == null) {
            throw new IllegalArgumentException("Invoice, delivery info, or cart is null");
        }
        try {
            int totalAmount = calculateTotalAmount(invoice);
            DeliveryInfo newDeliveryInfo = saveDeliveryInfo(invoice.getDeliveryInfo());
            Order newOrder = saveOrder(newDeliveryInfo, totalAmount);
            saveProductOrders(invoice.getCart(), newOrder);

            return newOrder;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage());
        }
    }

    private int calculateTotalAmount(InvoiceDTO invoice) {
        int totalAmount = 0;
        totalAmount = invoice.getSubtotal() + invoice.getDeliveryFee() + invoice.getVat();
        return totalAmount;
    }

    private DeliveryInfo saveDeliveryInfo(DeliveryInfo deliveryInfo) {
        return deliveryInfoRepository.save(deliveryInfo);
    }

    private Order saveOrder(DeliveryInfo deliveryInfo, int totalAmount) {
        Order order = new Order();
        order.setDeliveryInfo(deliveryInfo);
        order.setTotalAmount((double) totalAmount);
        return orderRepository.save(order);
    }

    private void saveProductOrders(DeliveryProductDTO[] products, Order newOrder) {
        for (DeliveryProductDTO product : products) {
            ProductOrderEntity productOrder = new ProductOrderEntity();
            productOrder.setOrder(newOrder);

            Product productEntity = getProductEntity(product.getId());
            productOrder.setProduct(productEntity);

            productOrder.setQuantity(product.getQuantity());
            productOrderRepository.save(productOrder);
        }
    }

    private Product getProductEntity(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }
}
