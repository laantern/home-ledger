package by.aliakseikarpenka.home_ledger.domain.model;

import by.aliakseikarpenka.home_ledger.domain.value.Currency;
import by.aliakseikarpenka.home_ledger.domain.value.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Expense {
    private final Long id;
    private final LocalDate date;
    private final String description;
    private final Money amount;
    private final BigDecimal rateToBynOnDate;

    public Expense(Long id, LocalDate date, String description, Money amount, BigDecimal rateToBynOnDate) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.rateToBynOnDate = rateToBynOnDate;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getBYNAmount() {
        return switch (getAmount().currency()) {
            case BYN -> getAmount();
            case USD -> new Money(getAmount().amount().multiply(getRateToBynOnDate()), Currency.BYN);
        };
    }

    public Money getUSDAmount() {
        return switch (getAmount().currency()) {
            case BYN -> new Money(getAmount().amount().divide(getRateToBynOnDate(), 2, RoundingMode.HALF_UP), Currency.USD);
            case USD -> getAmount();
        };
    }

    public BigDecimal getRateToBynOnDate() {
        return rateToBynOnDate;
    }
}
