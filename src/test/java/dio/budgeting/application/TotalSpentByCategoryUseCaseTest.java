package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.support.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TotalSpentByCategoryUseCaseTest {

    private InMemoryTransactionRepository repository;
    private TotalSpentByCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        useCase = new TotalSpentByCategoryUseCase(repository);
        repository.save(new Transaction("mercado", 5000, Category.GROCERIES, LocalDate.of(2026, 11, 10)));
        repository.save(new Transaction("padaria", 1500, Category.GROCERIES, LocalDate.of(2026, 11, 12)));
        repository.save(new Transaction("remédio", 3000, Category.PHARMA, LocalDate.of(2026, 11, 12)));
    }

    @Test
    @DisplayName("deve somar os valores da categoria e devolver em reais")
    void should_sumAmountOfCategory() {
        var output = useCase.execute(Category.GROCERIES);

        assertThat(output.transactionsCount()).isEqualTo(2);
        assertThat(output.totalValue()).isEqualTo(65.00);
        assertThat(output.category()).isEqualTo("GROCERIES");
    }

    @Test
    @DisplayName("deve devolver zero quando a categoria não tem transações")
    void should_returnZero_when_categoryHasNoTransactions() {
        var output = useCase.execute(Category.AUTO);

        assertThat(output.transactionsCount()).isZero();
        assertThat(output.totalValue()).isZero();
    }
}
