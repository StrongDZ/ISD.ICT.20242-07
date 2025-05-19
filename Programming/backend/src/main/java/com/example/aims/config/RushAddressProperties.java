package com.example.aims.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ✅ This class represents configuration properties for rush-eligible addresses.
 * ✅ High Cohesion: All fields and methods relate to a single purpose—external configuration binding.
 * ✅ Follows SRP: It only handles binding config properties, nothing more.
 *
 * SOLID Evaluation:
 * - S (Single Responsibility Principle): Focused solely on binding rush address config properties.
 * - O (Open/Closed Principle): Can be extended with more properties without changing existing code.
 * - L (Liskov Substitution Principle): Not applicable directly; no inheritance here.
 * - I (Interface Segregation Principle): Not applicable; no interfaces.
 * - D (Dependency Inversion Principle): Adheres to DIP by externalizing config and letting Spring inject it.
 */
@Component
@ConfigurationProperties(prefix = "rush.address") // ✅ Adheres to Dependency Inversion by externalizing configuration.
public class RushAddressProperties {

    // ✅ Single Responsibility: Stores the name of the city eligible for rush orders.
    private String city;

    // ✅ Single Responsibility: Stores the list of districts eligible for rush orders.
    private List<String> districts;

    // ✅ Basic POJO getter — cohesive with class responsibility.
    public String getCity() {
        return city;
    }

    // ✅ Setter used by Spring Boot's configuration binding mechanism.
    public void setCity(String city) {
        this.city = city;
    }

    // ✅ Basic POJO getter — remains cohesive to the purpose of holding rush-related config.
    public List<String> getDistricts() {
        return districts;
    }

    // ✅ Setter used by Spring Boot configuration binding.
    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }
}
