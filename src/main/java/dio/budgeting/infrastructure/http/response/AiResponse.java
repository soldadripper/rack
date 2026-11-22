package dio.budgeting.infrastructure.http.response;

/** Resposta em JSON do endpoint de IA (mais fácil de testar/depurar que o MP3). */
public record AiResponse(String transcription, String answer) {
}
