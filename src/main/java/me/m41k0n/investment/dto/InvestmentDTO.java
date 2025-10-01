package me.m41k0n.investment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentDTO(
        String id,
        String name,
        String type,
        String broker,
        BigDecimal investmentValue,
        BigDecimal purchaseRate,
        LocalDate purchaseDate,
        String operationType
) {
    public InvestmentDTO(String name, String type, String broker, BigDecimal investmentValue, BigDecimal purchaseRate, LocalDate purchaseDate, String operationType) {
        this(null, name, type, broker, investmentValue, purchaseRate, purchaseDate, operationType);
    }
}
