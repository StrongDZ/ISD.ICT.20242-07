package com.example.aims.dto;

import java.util.List;

import com.example.aims.model.Product;

public class PlaceRushOrderResponse {
    private boolean supported;
    private List<Product> rushProducts;
    private List<Product> regularProducts;
    private String promptMessage;
    private double rushFee;
    private double regularFee;
    // Getters and setters

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public List<Product> getRushProducts() {
        return rushProducts;
    }

    public void setRushProducts(List<Product> rushProducts) {
        this.rushProducts = rushProducts;
    }

    public List<Product> getRegularProducts() {
        return regularProducts;
    }

    public void setRegularProducts(List<Product> regularProducts) {
        this.regularProducts = regularProducts;
    }

    public String getPromptMessage() {
        return promptMessage;
    }

    public void setPromptMessage(String promptMessage) {
        this.promptMessage = promptMessage;
    }

    public double getRushFee() {
        return rushFee;
    }

    public void setRushFee(double rushFee) {
        this.rushFee = rushFee;
    }

    public double getRegularFee() {
        return regularFee;
    }

    public void setRegularFee(double regularFee) {
        this.regularFee = regularFee;
    }
}
