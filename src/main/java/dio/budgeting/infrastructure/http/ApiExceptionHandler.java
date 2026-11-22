package dio.budgeting.infrastructure.http;

import dio.budgeting.domain.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

/**
 * Traduz erros de negócio/infra em respostas HTTP claras (RFC 9457 - ProblemDetail).
 * Sem isso, um valor inválido vira 500 com stack trace.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Requisição inválida");
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var detail = "Valor inválido para o parâmetro '%s'".formatted(ex.getName());
        if (Category.class.equals(ex.getRequiredType())) {
            detail += ". Categorias válidas: " + Arrays.toString(Category.values());
        }
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Parâmetro inválido");
        return problem;
    }
}
