package dio.budgeting.application.input;

import dio.budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * Input do caso de uso.
 *
 * occurredAt é String (e não LocalDate) de propósito:
 * na fronteira com a IA (Tool Calling) use SEMPRE tipos simples (String, long, enum),
 * porque é o modelo quem "escreve" esse valor em JSON. Convertemos para LocalDate
 * dentro do caso de uso, com mensagem de erro didática para o modelo conseguir se corrigir.
 */
public record PersistTransactionInput(
        @ToolParam(description = "Descrição curta do gasto, ex.: 'compras no mercado'")
        String description,

        @ToolParam(description = "Valor do gasto EM CENTAVOS. Ex.: R$ 25,90 deve ser enviado como 2590")
        long amount,

        @ToolParam(description = "Categoria do gasto. Use GROCERIES para mercado, PHARMA para farmácia, AUTO para combustível/veículo")
        Category category,

        @ToolParam(description = "Data do gasto no formato AAAA-MM-DD. Se o usuário não informar, envie vazio")
        String occurredAt) {
}
