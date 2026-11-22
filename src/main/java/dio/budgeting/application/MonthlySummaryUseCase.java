package dio.budgeting.application;

import dio.budgeting.application.output.CategoryTotalOutput;
import dio.budgeting.application.output.MonthlySummaryOutput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NOVO TIPO DE CONSULTA FINANCEIRA #3: resumo do mês.
 * Aqui o banco devolve as transações do período e a agregação é feita em Java (Streams),
 * porque precisamos de vários números diferentes de uma vez.
 */
@Service
public class MonthlySummaryUseCase {
    private final TransactionRepository transactionRepository;

    public MonthlySummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "monthly-summary",
            description = "Gera o resumo financeiro de um mês: total gasto, quantidade de transações "
                    + "e o total separado por categoria. Use para 'como foi meu mês?', "
                    + "'quanto gastei em outubro?'.")
    public MonthlySummaryOutput execute(
            @ToolParam(description = "Mês no formato AAAA-MM (ex.: 2026-11). Se o usuário não informar, envie vazio para usar o mês atual")
            String month) {

        YearMonth reference = parseMonth(month);
        var transactions = transactionRepository.findAllByPeriod(reference.atDay(1), reference.atEndOfMonth());

        long totalCents = transactions.stream().mapToLong(Transaction::getAmount).sum();

        Map<Category, List<Transaction>> grouped = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory));

        List<CategoryTotalOutput> byCategory = grouped.entrySet().stream()
                .map(entry -> new CategoryTotalOutput(
                        entry.getKey().name(),
                        TransactionOutput.toReais(entry.getValue().stream().mapToLong(Transaction::getAmount).sum()),
                        entry.getValue().size()))
                .sorted(Comparator.comparingDouble(CategoryTotalOutput::totalValue).reversed())
                .toList();

        return new MonthlySummaryOutput(
                reference.toString(),
                TransactionOutput.toReais(totalCents),
                transactions.size(),
                byCategory);
    }

    private YearMonth parseMonth(String raw) {
        if (raw == null || raw.isBlank()) {
            return YearMonth.now();
        }
        var value = raw.strip();
        try {
            return YearMonth.parse(value); // AAAA-MM
        } catch (DateTimeParseException ignored) {
            // tenta MM/AAAA
        }
        try {
            var parts = value.split("/");
            return YearMonth.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Mês inválido: '" + raw + "'. Use o formato AAAA-MM (ex.: 2026-11).");
        }
    }
}
