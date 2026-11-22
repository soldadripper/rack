package dio.budgeting.application.output;

/** Total gasto considerando todas as transações. */
public record TotalSpentOutput(double totalValue, long transactionsCount) {
}
