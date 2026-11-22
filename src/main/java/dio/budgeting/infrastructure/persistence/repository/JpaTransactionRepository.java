package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adapter: implementa o contrato do domínio usando Spring Data JPA.
 * Toda conversão entidade <-> domínio acontece aqui.
 */
@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public long sumAmountByCategory(Category category) {
        return Optional.ofNullable(transactionEntityRepository.sumAmountByCategory(category)).orElse(0L);
    }

    @Override
    public long sumAmount() {
        return Optional.ofNullable(transactionEntityRepository.sumAmount()).orElse(0L);
    }

    @Override
    public long countByCategory(Category category) {
        return transactionEntityRepository.countByCategory(category);
    }

    @Override
    public long countAll() {
        return transactionEntityRepository.count();
    }

    @Override
    public List<Transaction> findAllByPeriod(LocalDate start, LocalDate end) {
        return transactionEntityRepository.findAllByOccurredAtBetween(start, end)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
