package by.aliakseikarpenka.home_ledger.domain.value;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {

    private final BigDecimal amount;
    private final Currency currency;

    public Money(
            BigDecimal amount,
            Currency currency
    ) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("Money fields cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        this.amount = amount.stripTrailingZeros().setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

}
