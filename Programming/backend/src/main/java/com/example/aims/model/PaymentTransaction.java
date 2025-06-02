package com.example.aims.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "PaymentTransaction")

// Functional Cohesion – All fields serve the single purpose of representing a
// payment transaction.
// SRP respected – The class contains only transaction-related data and does not
// mix responsibilities.

// 🔧 Improvement suggestion:
// - Currently, this class only stores data (anemic model). If future business
// logic related to payment arises
// (e.g., verifying payment status, calculating transaction fees, or formatting
// content),
// consider moving those responsibilities into a separate service class like
// PaymentService.
// - Alternatively, if adding small behaviors, add utility methods such as:
// - isExpired(): check if the transaction is outdated.
// - getFormattedDatetime(): return a human-readable string for display.
// - summarizeContent(): return a shortened or categorized version of content.

// ✅ SRP (Single Responsibility Principle)
// The class is focused solely on representing a payment transaction entity.
// It encapsulates transaction-related fields only – no mixed responsibilities.

// ✅ OCP (Open-Closed Principle)
// While the class doesn’t include logic, it's open to extension by adding new fields or utility methods without modifying existing structure.

// ✅ LSP (Liskov Substitution Principle)
// The class can be safely extended or used as a base entity. No overridden behavior that could violate substitutability.

// ✅ ISP (Interface Segregation Principle)
// Not applicable here – the class does not implement any interface, and it doesn't force unwanted methods on clients.

// ⚠️ DIP (Dependency Inversion Principle)
// Currently, this class depends directly on the `Order` entity through `@OneToOne`, which is normal in JPA for entity relationships.
// However, to respect DIP better in the service layer, avoid relying on full `PaymentTransaction` objects when only parts (e.g., `content`, `datetime`) are needed.
// Instead, services can depend on abstraction (e.g., DTOs or interfaces) or pass only necessary fields.

public class PaymentTransaction {

    // Stamp Coupling – This class depends on the entire Order object,
    // even though only specific fields (e.g., id, content) may be relevant for
    // payment tracking.
    // This is acceptable in entity relationships (JPA), but in service logic,
    // prefer passing only needed data to reduce coupling to Data level.

    @Id
    private String orderID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "orderID")
    private Order order;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
}