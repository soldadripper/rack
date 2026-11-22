package dio.budgeting.application.output;

/** Resultado de uma consulta agregada por categoria. */
public record CategoryTotalOutput(String category, double totalValue, long transactionsCount) {
}
