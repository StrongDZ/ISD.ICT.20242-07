package com.example.aims.service.rush.eligibility;

/**
 * ✅ High Cohesion:
 *     - This interface defines a single, focused responsibility:
 *       to check if a given input of type T is eligible for rush processing.
 *
 * ✅ S - Single Responsibility Principle (SRP):
 *     - The interface declares one method with a clear responsibility.
 *     - It does not mix different concerns.
 *
 * ✅ O - Open/Closed Principle (OCP):
 *     - The interface is open for extension by implementing classes.
 *     - New eligibility rules can be introduced by new implementations without changing this interface.
 *
 * ✅ L - Liskov Substitution Principle (LSP):
 *     - All implementations can be substituted wherever this interface is expected,
 *       ensuring polymorphism and consistent behavior.
 *
 * ✅ I - Interface Segregation Principle (ISP):
 *     - The interface is minimal and focused, preventing clients from depending on methods they don't use.
 *
 * ✅ D - Dependency Inversion Principle (DIP):
 *     - Higher-level modules depend on this abstraction, not on concrete implementations.
 */
public interface RushEligibility<T> {
    /**
     * Checks if the input entity is eligible for rush processing.
     *
     * @param input an entity of generic type T to check eligibility for
     * @return true if eligible; false otherwise
     */
    boolean isRushAllowed(T input);
}
