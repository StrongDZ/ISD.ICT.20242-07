package com.example.aims.factory;

import com.example.aims.subsystem.IPaymentSystem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory class for creating payment systems.
 * Uses dependency injection and auto-discovery to manage payment system
 * implementations.
 * Follows Factory pattern best practices with Spring integration.
 */
@Component
public class PaymentSystemFactory {

    private final Map<String, IPaymentSystem> paymentSystems;

    /**
     * Constructor that automatically discovers and maps all IPaymentSystem
     * implementations.
     * 
     * @param paymentSystems List of all IPaymentSystem beans injected by Spring
     */
    public PaymentSystemFactory(List<IPaymentSystem> paymentSystems) {
        this.paymentSystems = paymentSystems.stream()
                .collect(Collectors.toMap(
                        IPaymentSystem::getPaymentType,
                        Function.identity()));
    }

    /**
     * Gets a payment system by type.
     * 
     * @param type The payment type (e.g., "vnpay", "momo")
     * @return The payment system implementation
     * @throws IllegalArgumentException if payment type is null or not supported
     */
    public IPaymentSystem getPaymentSystem(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }

        String normalizedType = type.toLowerCase();
        IPaymentSystem paymentSystem = paymentSystems.get(normalizedType);

        if (paymentSystem == null) {
            throw new IllegalArgumentException("Unsupported payment type: " + type +
                    ". Supported types are: " + String.join(", ", paymentSystems.keySet()));
        }

        return paymentSystem;
    }

    /**
     * Gets all supported payment types.
     * 
     * @return List of supported payment types
     */
    public List<String> getSupportedPaymentTypes() {
        return paymentSystems.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Checks if a payment type is supported.
     * 
     * @param type The payment type to check
     * @return true if supported, false otherwise
     */
    public boolean isSupported(String type) {
        return type != null && paymentSystems.containsKey(type.toLowerCase());
    }
}