package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.Category;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findAllByCategory(Category category);

    List<TransactionEntity> findAllByOccurredAtBetween(LocalDate start, LocalDate end);

    long countByCategory(Category category);

    /**
     * SUM devolve NULL quando não há linhas, por isso o retorno é Long (não long).
     * Quem converte NULL -> 0 é o adapter (JpaTransactionRepository).
     */
    @Query("select sum(t.amount) from TransactionEntity t where t.category = :category")
    Long sumAmountByCategory(@Param("category") Category category);

    @Query("select sum(t.amount) from TransactionEntity t")
    Long sumAmount();
}
