package dio.budgeting.application.output;

import java.util.List;

/** Resumo financeiro de um mês (AAAA-MM). */
public record MonthlySummaryOutput(String period,
                                   double totalValue,
                                   long transactionsCount,
                                   List<CategoryTotalOutput> byCategory) {
}
