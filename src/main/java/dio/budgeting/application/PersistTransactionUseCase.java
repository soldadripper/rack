package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction",
            description = "Registra (persiste) uma nova transação financeira de gasto. "
                    + "Use quando o usuário informar que gastou, pagou ou comprou algo.")
    public TransactionOutput execute(PersistTransactionInput input) {
        var transaction = transactionRepository.save(new Transaction(
                input.description(),
                input.amount(),
                input.category(),
                parseDate(input.occurredAt())));

        return TransactionOutput.from(transaction);
    }

    /**
     * Aceita "AAAA-MM-DD", "DD/MM/AAAA" ou vazio (= hoje).
     * A mensagem de erro é "conversa" com o modelo: ele recebe o texto e normalmente tenta de novo correto.
     */
    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return LocalDate.now();
        }
        var value = raw.strip();
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
            // tenta o formato brasileiro antes de desistir
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Data inválida: '" + raw + "'. Use o formato AAAA-MM-DD (ex.: 2026-11-22) ou envie vazio para hoje.");
        }
    }
}
