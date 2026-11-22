package dio.budgeting.support;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação fake do repositório para testes de unidade.
 * Vantagem: roda em milissegundos e NÃO precisa de banco nem de OPENAI_API_KEY.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public Transaction save(Transaction transaction) {
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactions.stream().filter(t -> t.getCategory() == category).toList();
    }

    @Override
    public long sumAmountByCategory(Category category) {
        return transactions.stream()
                .filter(t -> t.getCategory() == category)
                .mapToLong(Transaction::getAmount)
                .sum();
    }

    @Override
    public long sumAmount() {
        return transactions.stream().mapToLong(Transaction::getAmount).sum();
    }

    @Override
    public long countByCategory(Category category) {
        return transactions.stream().filter(t -> t.getCategory() == category).count();
    }

    @Override
    public long countAll() {
        return transactions.size();
    }

    @Override
    public List<Transaction> findAllByPeriod(LocalDate start, LocalDate end) {
        return transactions.stream()
                .filter(t -> !t.getOccurredAt().isBefore(start) && !t.getOccurredAt().isAfter(end))
                .toList();
    }
}
