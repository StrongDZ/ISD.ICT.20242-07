package com.example.aims.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aims.common.OrderStatus;
import com.example.aims.dto.DeliveryInfoDTO;
import com.example.aims.dto.DeliveryProductDTO;
import com.example.aims.dto.InvoiceDTO;
import com.example.aims.dto.order.OrderDTO;
import com.example.aims.dto.order.OrderItemDTO;
import com.example.aims.model.CartItem;
import com.example.aims.model.DeliveryInfo;
import com.example.aims.model.Invoice;
import com.example.aims.model.Order;
import com.example.aims.model.OrderItem;
import com.example.aims.model.PaymentTransaction;
import com.example.aims.model.Product;
import com.example.aims.model.ProductOrderEntity;
import com.example.aims.model.Users;
import com.example.aims.repository.CartItemRepository;
import com.example.aims.repository.DeliveryInfoRepository;
import com.example.aims.repository.InvoiceRepository;
import com.example.aims.repository.OrderItemRepository;
import com.example.aims.repository.OrderRepository;
import com.example.aims.repository.PaymentTransactionRepository;
import com.example.aims.repository.ProductOrderRepository;
import com.example.aims.repository.ProductRepository;
import com.example.aims.repository.UsersRepository;
import com.example.aims.service.rush.PlaceRushOrderService;

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

    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsersRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductOrderRepository productOrderRepository;
    private final PlaceRushOrderService placeRushOrderService;

    public PlaceOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            DeliveryInfoRepository deliveryInfoRepository, PaymentTransactionRepository paymentTransactionRepository,
            InvoiceRepository invoiceRepository, UsersRepository userRepository,
            ProductRepository productRepository, CartItemRepository cartItemRepository, ProductOrderRepository productOrderRepository,
            PlaceRushOrderService placeRushOrderService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.productOrderRepository = productOrderRepository;
        this.placeRushOrderService = placeRushOrderService;
    }

    public boolean isRushOrderSupported(DeliveryInfoDTO deliveryInfo, List<Product> products) {
        return placeRushOrderService.placeRushOrder(deliveryInfo, products).isSupported();
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

    
    private Users validateAndGetCustomer(Integer customerId) {
        return userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Users not found with id: " + customerId));
    }
    
    private List<CartItem> getCartItemsForCustomer(Users customer) {
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        return cartItems;
    }
    
    private Order createOrder(Users customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        return order;
    }
    
    private double createOrderItemsAndUpdateProducts(Order order, List<CartItem> cartItems) {
        double totalPrice = 0.0;
    
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
    
            OrderItem orderItem = new OrderItem();
            orderItem.setId(new OrderItem.OrderItemId(product.getProductID(), order.getOrderID()));
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
    
            orderItemRepository.save(orderItem);
    
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
    
            totalPrice += product.getPrice() * cartItem.getQuantity();
        }
    
        return totalPrice;
    }
    
    private void createDeliveryInfo(String orderId, DeliveryInfoDTO dto) {
        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setOrderID(orderId);
        deliveryInfo.setCity(dto.getCity());
        deliveryInfo.setDistrict(dto.getDistrict());
        deliveryInfo.setAddressDetail(dto.getAddressDetail());
        deliveryInfo.setPhoneNumber(dto.getPhoneNumber());
        deliveryInfo.setRecipientName(dto.getRecipientName());
        deliveryInfo.setMail(dto.getMail());
    
        deliveryInfoRepository.save(deliveryInfo);
    }
    
    private void createPaymentTransaction(Order order, double totalPrice) {
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setOrder(order);
        paymentTransaction.setDatetime(new Date());
        paymentTransaction.setAmount(totalPrice);
    
        paymentTransactionRepository.save(paymentTransaction);
    }
    
    private void createInvoice(Order order, double totalPrice) {
        double vat = 0.1;
        double deliveryFee = 5.0;
    
        Invoice invoice = new Invoice();
        invoice.setOrderID(order.getOrderID());
        invoice.setOrder(order);
        invoice.setProductPriceExcludingVAT(totalPrice);
        invoice.setProductPriceIncludingVAT(totalPrice * (1 + vat));
        invoice.setDeliveryFee(deliveryFee);
    
        invoiceRepository.save(invoice);
    }
    
    private void clearCart(Users customer) {
        cartItemRepository.deleteByCustomer(customer);
    }

    @Transactional
    public OrderDTO createOrderWithAccount(List<CartItem> cartItems, DeliveryInfoDTO deliveryInfoDTO, Integer customerId) {

        Users customer = validateAndGetCustomer(customerId);
        Order order = createOrder(customer);
        order = orderRepository.save(order);
        
        double totalPrice = createOrderItemsAndUpdateProducts(order, cartItems);
        
        createDeliveryInfo(order.getOrderID(), deliveryInfoDTO);
        createPaymentTransaction(order, totalPrice);
        createInvoice(order, totalPrice);
        
        clearCart(customer);

        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO createOrderNoAccount(List<CartItem> cartItems, DeliveryInfoDTO deliveryInfoDTO) {

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);
        
        double totalPrice = createOrderItemsAndUpdateProducts(order, cartItems);
        
        createDeliveryInfo(order.getOrderID(), deliveryInfoDTO);
        createPaymentTransaction(order, totalPrice);
        createInvoice(order, totalPrice);
        
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getOrderID());
        if (order.getCustomer() != null) {
            dto.setCustomerID(order.getCustomer().getId());
        }
        dto.setStatus(order.getStatus());

        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductID(item.getProduct().getProductID());
            itemDTO.setProductTitle(item.getProduct().getTitle());
            itemDTO.setProductPrice(item.getProduct().getPrice());
            itemDTO.setQuantity(item.getQuantity());
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(orderItemDTOs);

        // Get delivery info
        deliveryInfoRepository.findById(order.getOrderID()).ifPresent(deliveryInfo -> {
            DeliveryInfoDTO deliveryInfoDTO = new DeliveryInfoDTO();
            deliveryInfoDTO.setCity(deliveryInfo.getCity());
            deliveryInfoDTO.setDistrict(deliveryInfo.getDistrict());
            deliveryInfoDTO.setAddressDetail(deliveryInfo.getAddressDetail());
            deliveryInfoDTO.setPhoneNumber(deliveryInfo.getPhoneNumber());
            deliveryInfoDTO.setRecipientName(deliveryInfo.getRecipientName());
            deliveryInfoDTO.setMail(deliveryInfo.getMail());

            dto.setDeliveryInfo(deliveryInfoDTO);
        });

        // Get total price from invoice
        invoiceRepository.findById(order.getOrderID()).ifPresent(invoice -> {
            dto.setTotalPrice(invoice.getProductPriceIncludingVAT() + invoice.getDeliveryFee());
        });

        return dto;
    }
}
