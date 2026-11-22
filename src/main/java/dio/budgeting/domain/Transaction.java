package dio.budgeting.domain;

import java.time.LocalDate;

/**
 * Entidade de domínio.
 *
 * Mudanças em relação ao projeto base:
 *  1. novo atributo occurredAt (data em que o gasto aconteceu) -> necessário para consultas por período;
 *  2. campos final + construtor explícito com validação (invariantes do domínio);
 *  3. getters EXPLÍCITOS (sem Lombok): se o @Getter não for processado, o projeto quebra
 *     com "cannot find symbol: method getId()". Aqui o código não depende de annotation processing.
 */
public class Transaction {
    private final TransactionId id;
    private final String description;
    private final long amount;          // sempre em CENTAVOS (R$ 25,90 -> 2590)
    private final Category category;
    private final LocalDate occurredAt;

    public Transaction(String description, long amount, Category category, LocalDate occurredAt) {
        this(new TransactionId(), description, amount, category, occurredAt);
    }

    public Transaction(TransactionId id, String description, long amount, Category category, LocalDate occurredAt) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição da transação é obrigatória.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero (informe em centavos).");
        }
        if (category == null) {
            throw new IllegalArgumentException("A categoria da transação é obrigatória.");
        }
        this.id = id == null ? new TransactionId() : id;
        this.description = description.strip();
        this.amount = amount;
        this.category = category;
        this.occurredAt = occurredAt == null ? LocalDate.now() : occurredAt;
    }

    public TransactionId getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getOccurredAt() {
        return occurredAt;
    }
}
