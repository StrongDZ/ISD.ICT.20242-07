package com.example.aims.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.aims.mapper.PaymentError.IPaymentErrorMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory class for creating payment error mappers.
 * Uses Strategy pattern to select the appropriate mapper based on payment type.
 */
@Component
public class PaymentErrorMapperFactory {

    private final Map<String, IPaymentErrorMapper> mapperMap;

    @Autowired
    public PaymentErrorMapperFactory(List<IPaymentErrorMapper> mappers) {
        // Create a map of payment type to mapper for quick lookup
        this.mapperMap = mappers.stream()
                .collect(Collectors.toMap(
                        IPaymentErrorMapper::getPaymentType,
                        Function.identity()));
    }

    /**
     * Gets the appropriate error mapper for the given payment type.
     * 
     * @param paymentType The payment type (e.g., "vnpay", "momo")
     * @return The appropriate PaymentErrorMapper
     * @throws IllegalArgumentException if no mapper is found for the payment type
     */
    public IPaymentErrorMapper getMapper(String paymentType) {
        IPaymentErrorMapper mapper = mapperMap.get(paymentType.toLowerCase());
        if (mapper == null) {
            throw new IllegalArgumentException("No error mapper found for payment type: " + paymentType);
        }
        return mapper;
    }

    /**
     * Gets all available payment types.
     * 
     * @return List of supported payment types
     */
    public List<String> getSupportedPaymentTypes() {
        return List.copyOf(mapperMap.keySet());
    }

    /**
     * Checks if a payment type is supported.
     * 
     * @param paymentType The payment type to check
     * @return true if supported, false otherwise
     */
    public boolean isSupported(String paymentType) {
        return mapperMap.containsKey(paymentType.toLowerCase());
    }
}