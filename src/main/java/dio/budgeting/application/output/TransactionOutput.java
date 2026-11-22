package dio.budgeting.application.output;

import dio.budgeting.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Output do caso de uso.
 *
 * Mudanças em relação ao base:
 *  1. corrigido o bug de conversão de centavos -> reais (o base devolvia 2590.0 em vez de 25.9);
 *  2. incluída a data (occurredAt) como String ISO;
 *  3. centralizado o helper toReais, reaproveitado pelas novas consultas.
 */
public record TransactionOutput(String id, String description, String category, double value, String occurredAt) {

    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().uuid().toString(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                toReais(transaction.getAmount()),
                transaction.getOccurredAt().toString());
    }

    /** centavos -> reais com 2 casas decimais. 2590 -> 25.9 */
    public static double toReais(long cents) {
        return BigDecimal.valueOf(cents).movePointLeft(2).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
