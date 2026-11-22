package dio.budgeting.application;

import dio.budgeting.application.output.CategoryTotalOutput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * NOVO TIPO DE CONSULTA FINANCEIRA #1: total gasto por categoria.
 * A soma é feita no banco (SUM), não em memória.
 */
@Service
public class TotalSpentByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public TotalSpentByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "total-spent-by-category",
            description = "Informa quanto foi gasto no total em UMA categoria (somatório dos valores) "
                    + "e quantas transações existem nela. Use para perguntas como "
                    + "'quanto gastei no mercado?', 'qual o total da farmácia?'.")
    public CategoryTotalOutput execute(
            @ToolParam(description = "Categoria: GROCERIES (mercado), PHARMA (farmácia) ou AUTO (veículo)")
            Category category) {
        long totalCents = transactionRepository.sumAmountByCategory(category);
        long count = transactionRepository.countByCategory(category);
        return new CategoryTotalOutput(category.name(), TransactionOutput.toReais(totalCents), count);
    }
}
