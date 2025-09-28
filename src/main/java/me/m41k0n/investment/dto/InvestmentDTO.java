package me.m41k0n.investment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para representar um investimento.
 * Simplifica a estrutura de dados, mantendo-a separada da lógica da UI e da API.
 * Usamos 'record' (Java 17+) para concisão e imutabilidade.
 */
public record InvestmentDTO(
        String id,
        String name,
        String type,
        String broker,
        BigDecimal investmentValue,
        BigDecimal purchaseRate,
        LocalDate purchaseDate
) {
    // Construtor principal implícito do record.
    // Construtor auxiliar para facilitar a criação de novos investimentos (sem ID).
    public InvestmentDTO(String name, String type, String broker, BigDecimal investmentValue, BigDecimal purchaseRate, LocalDate purchaseDate) {
        this(null, name, type, broker, investmentValue, purchaseRate, purchaseDate);
    }
}
