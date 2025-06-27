package com.example.aims.service;

import com.example.aims.dto.CartItemDTO;
import com.example.aims.model.Product;
import com.example.aims.repository.ProductRepository;
import com.example.aims.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * Implementation của InventoryService
 * Chuyên quản lý tồn kho sản phẩm với các tính năng:
 * - Cập nhật tồn kho sau khi đặt hàng
 * - Kiểm tra tính khả dụng của tồn kho
 * - Validation tồn kho
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;

    @Transactional
    public void updateProductStocks(List<CartItemDTO> cartItems) {
        
        for (CartItemDTO cartItem : cartItems) {
            String productId = cartItem.getProductDTO().getProductID();
            int requestedQuantity = cartItem.getQuantity();
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
            
            // Kiểm tra tồn kho trước khi cập nhật
            if (product.getQuantity() < requestedQuantity) {
                throw new RuntimeException("Sản phẩm " + productId + " không đủ tồn kho. Yêu cầu: " + 
                    requestedQuantity + ", Hiện có: " + product.getQuantity());
            }
            
            // Cập nhật số lượng tồn kho
            int newQuantity = product.getQuantity() - requestedQuantity;
            product.setQuantity(newQuantity);
            
            // Lưu lại vào database
            productRepository.save(product);
            
        }
    }

    public List<CartItemDTO> checkInventoryAvailability(List<CartItemDTO> cartItems) {
        
        List<CartItemDTO> insufficientItems = new ArrayList<>();
        
        for (CartItemDTO cartItem : cartItems) {
            String productId = cartItem.getProductDTO().getProductID();
            int requestedQuantity = cartItem.getQuantity();
            
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
            
            // Kiểm tra xem số lượng yêu cầu có vượt quá tồn kho không
            if (requestedQuantity > product.getQuantity()) {
                // Cập nhật thông tin tồn kho thực tế vào productDTO
                cartItem.getProductDTO().setQuantity(product.getQuantity());
                insufficientItems.add(cartItem);
                
            }
        }
        
        return insufficientItems;
    }

    public boolean isInventorySufficient(List<CartItemDTO> cartItems) {
        return checkInventoryAvailability(cartItems).isEmpty();
    }

    public int getCurrentStock(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));
        
        return product.getQuantity();
    }

    public boolean isStockSufficient(String productId, int requestedQuantity) {
        int currentStock = getCurrentStock(productId);
        return currentStock >= requestedQuantity;
    }
} 