package dio.budgeting.infrastructure.http;

import dio.budgeting.application.*;
import dio.budgeting.application.output.CategoryTotalOutput;
import dio.budgeting.application.output.MonthlySummaryOutput;
import dio.budgeting.application.output.TotalSpentOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.infrastructure.http.request.TransactionRequest;
import dio.budgeting.infrastructure.http.response.AiResponse;
import dio.budgeting.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final TotalSpentByCategoryUseCase totalSpentByCategoryUseCase;
    private final TotalSpentUseCase totalSpentUseCase;
    private final MonthlySummaryUseCase monthlySummaryUseCase;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 TotalSpentByCategoryUseCase totalSpentByCategoryUseCase,
                                 TotalSpentUseCase totalSpentUseCase,
                                 MonthlySummaryUseCase monthlySummaryUseCase,
                                 TranscriptionModel transcriptionModel,
                                 @Value("classpath:prompts/system-message.st") Resource systemPrompt,
                                 ChatClient.Builder chatClientBuilder,
                                 TextToSpeechModel textToSpeechModel) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.totalSpentByCategoryUseCase = totalSpentByCategoryUseCase;
        this.totalSpentUseCase = totalSpentUseCase;
        this.monthlySummaryUseCase = monthlySummaryUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;

        // AQUI é o "cérebro" da API: prompt de sistema + catálogo de tools disponíveis para o modelo.
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(
                        persistTransactionUseCase,
                        listTransactionsByCategoryUseCase,
                        totalSpentByCategoryUseCase,
                        totalSpentUseCase,
                        monthlySummaryUseCase)
                .build();
    }

    // ---------------- REST puro (sem IA) ----------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        return TransactionResponse.from(persistTransactionUseCase.execute(request.toInput()));
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // ATENCAO ao caminho: existe tambem @GetMapping("/{category}") mais acima, que casaria com
    // /transactions/totals e faria o Spring tentar converter "totals" no enum Category -> HTTP 400.
    // Caminhos multi-segmento (/reports/...) nao colidem e eliminam essa ambiguidade.
    @GetMapping("/reports/total/{category}")
    public CategoryTotalOutput totalByCategory(@PathVariable Category category) {
        return totalSpentByCategoryUseCase.execute(category);
    }

    @GetMapping("/reports/total")
    public TotalSpentOutput total() {
        return totalSpentUseCase.execute();
    }

    @GetMapping("/reports/monthly/{month}")
    public MonthlySummaryOutput monthlySummary(@PathVariable String month) {
        return monthlySummaryUseCase.execute(month);
    }

    // ---------------- IA ----------------

    /** Fluxo completo: áudio -> texto -> tool calling -> resposta falada (MP3). */
    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        var userMessage = transcriptionModel.transcribe(file.getResource());
        var result = chatClient.prompt().user(userMessage).call().content();

        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("audio.mp3").build().toString())
                .contentType(MediaType.parseMediaType("audio/mp3"))
                .body(resource);
    }

    /**
     * Mesmo fluxo, mas devolve JSON (transcrição + resposta em texto).
     * MUITO mais fácil de testar: você vê o que o Whisper entendeu e o que o modelo respondeu.
     */
    @PostMapping(value = "/ai/text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AiResponse ask(@RequestParam("file") MultipartFile file) {
        var userMessage = transcriptionModel.transcribe(file.getResource());
        var answer = chatClient.prompt().user(userMessage).call().content();
        return new AiResponse(userMessage, answer);
    }
}
