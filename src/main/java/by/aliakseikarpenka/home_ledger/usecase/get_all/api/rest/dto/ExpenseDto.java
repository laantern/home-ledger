package by.aliakseikarpenka.home_ledger.usecase.get_all.api.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDto(
        Long id,
        LocalDate date,
        String description,
        BigDecimal amount,
        String currency,
        BigDecimal amountByn,
        BigDecimal amountUsd,
        BigDecimal rateToBynOnDate
) {}
