package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.support.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthlySummaryUseCaseTest {

    private MonthlySummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        var repository = new InMemoryTransactionRepository();
        repository.save(new Transaction("mercado", 5000, Category.GROCERIES, LocalDate.of(2026, 11, 10)));
        repository.save(new Transaction("remédio", 3000, Category.PHARMA, LocalDate.of(2026, 11, 12)));
        repository.save(new Transaction("lavagem", 8000, Category.AUTO, LocalDate.of(2026, 10, 5)));
        useCase = new MonthlySummaryUseCase(repository);
    }

    @Test
    @DisplayName("deve considerar apenas as transações do mês pedido")
    void should_sumOnlyTransactionsOfGivenMonth() {
        var output = useCase.execute("2026-11");

        assertThat(output.period()).isEqualTo("2026-11");
        assertThat(output.transactionsCount()).isEqualTo(2);
        assertThat(output.totalValue()).isEqualTo(80.00);
        assertThat(output.byCategory()).hasSize(2);
        // ordenado do maior para o menor
        assertThat(output.byCategory().get(0).category()).isEqualTo("GROCERIES");
    }

    @Test
    @DisplayName("deve lançar erro explicativo quando o mês é inválido")
    void should_throw_when_monthIsInvalid() {
        assertThatThrownBy(() -> useCase.execute("novembro"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2026-11");
    }
}
