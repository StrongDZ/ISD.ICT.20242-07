package com.example.aims.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
/**
 * ✅ This class represents delivery address information used in placing a rush order.
 * ✅ High Cohesion: All fields and methods relate only to delivery address representation.
 * ✅ Follows SRP (Single Responsibility Principle): This class only models data, no logic.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): Focused solely on holding delivery address data.
 * - O (Open/Closed Principle): Can be extended with additional fields without modifying existing ones.
 * - L (Liskov Substitution Principle): As a plain DTO, can be substituted where delivery info is needed.
 * - I (Interface Segregation Principle): Not applicable; no interfaces implemented here.
 * - D (Dependency Inversion Principle): Depends only on basic types; no external dependencies.
 */
@Data
@AllArgsConstructor
public class DeliveryInfoDTO {

    private String city;           // ✅ Part of delivery address
    private String district;       // ✅ Part of delivery address
    private String addressDetail;  // ✅ Additional address detail (e.g., street, number)
    private String recipientName;
    private String mail;
    private String phoneNumber;
    private Boolean isRushOrder;   // ✅ Indicates if this is a rush order
    
    // ✅ Additional rush delivery information
    private String deliveryTime;           // Preferred delivery time for rush orders
    private String specialInstructions;    // Special delivery instructions

    // ✅ Default constructor — required for serialization frameworks like Jackson
    public DeliveryInfoDTO() {}

    // ✅ Parameterized constructor for convenience
    public DeliveryInfoDTO(String city, String district, String addressDetail) {
        this.city = city;
        this.district = district;
        this.addressDetail = addressDetail;
    }

    // ✅ Getter for city
    public String getCity() {
        return city;
    }

    // ✅ Getter for district
    public String getDistrict() {
        return district;
    }

    // ✅ Getter for addressDetail
    public String getAddressDetail() {
        return addressDetail;
    }

    // ✅ Setter for city
    public void setCity(String city) {
        this.city = city;
    }

    // ✅ Setter for district
    public void setDistrict(String district) {
        this.district = district;
    }

    // ✅ Setter for addressDetail
    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public Boolean getIsRushOrder() {
        return isRushOrder;
    }

    public void setIsRushOrder(Boolean isRushOrder) {
        this.isRushOrder = isRushOrder;
    }
}
