package dio.budgeting.application;

import dio.budgeting.application.output.TotalSpentOutput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * NOVO TIPO DE CONSULTA FINANCEIRA #2: total gasto geral.
 * Repare que não há parâmetros: o modelo chama a tool "vazia".
 */
@Service
public class TotalSpentUseCase {
    private final TransactionRepository transactionRepository;

    public TotalSpentUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "total-spent",
            description = "Informa quanto foi gasto no TOTAL, considerando todas as transações registradas, "
                    + "e quantas transações existem. Use para 'quanto eu gastei no total?', 'qual meu gasto geral?'.")
    public TotalSpentOutput execute() {
        long totalCents = transactionRepository.sumAmount();
        long count = transactionRepository.countAll();
        return new TotalSpentOutput(TransactionOutput.toReais(totalCents), count);
    }
}
