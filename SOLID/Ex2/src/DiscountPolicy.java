
public interface DiscountPolicy {
    double discount(String customerType, double subtotal, int distinctLines);
}