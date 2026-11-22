package dio.budgeting.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato do repositório (domain). Nenhuma dependência de JPA/Spring aqui.
 *
 * Métodos novos foram adicionados para as novas consultas financeiras.
 * Regra: o domínio pede a INTENÇÃO da consulta; quem otimiza é a infraestrutura.
 */
public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    // ---- consultas novas ----
    long sumAmountByCategory(Category category);   // SUM(amount) WHERE category = ?
    long sumAmount();                             // SUM(amount) de tudo
    long countByCategory(Category category);       // COUNT WHERE category = ?
    long countAll();                              // COUNT de tudo
    List<Transaction> findAllByPeriod(LocalDate start, LocalDate end); // BETWEEN
}
