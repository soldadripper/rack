o projeto pega a voz do usuario, traduz para texto e gurda suas dispesas;

foi implementado sistemas de filtragem para pesquisa (mensal, categoria e total), em base nos dados guardados;

foi utilizado:
Java 25 · Spring Boot 4.0.5
Spring AI 2.0.0-M4 — ChatClient, @Tool/@ToolParam, TranscriptionModel, TextToSpeechModel
OpenAI — gpt-4o-mini (chat), whisper-1 (transcrição), gpt-4o-mini-tts (voz)
Spring Data JPA + MySQL 9.6 (Docker Compose)
Lombok, Gradle, JUnit 5 + AssertJ

sendo que eu aprendi a nao confiar em IAs para estudar, ja que quanto mais eu pedia, mais ela me dava a resposta em vez de me ensinar.


testes:
- build
PS> ./gradlew build -x test
BUILD SUCCESSFUL

- consulta por categoria
GET /transactions/reports/total/GROCERIES
{
    "category":  "GROCERIES",
    "totalValue":  40.9,
    "transactionsCount":  2
}

- consulta toatal
GET /transactions/reports/total
{
    "totalValue":  170.4,
    "transactionsCount":  4
}

- resumo mensal
GET /transactions/reports/monthly/2026-11
{
    "period":  "2026-11",
    "totalValue":  130.4,
    "transactionsCount":  3,
    "byCategory":  [
        { "category": "PHARMA",    "totalValue": 89.5, "transactionsCount": 1 },
        { "category": "GROCERIES", "totalValue": 40.9, "transactionsCount": 2 }
    ]
}

GET /transactions/reports/monthly/2026-10
{
    "period":  "2026-10",
    "totalValue":  40.0,
    "transactionsCount":  1,
    "byCategory":  [
        { "category": "AUTO", "totalValue": 40.0, "transactionsCount": 1 }
    ]
}

- Listagem por categoria (bug dos centavos corrigido: 25.9, não 2590.0)
GET /transactions/GROCERIES
[
    { "id": "4275e7c7-...", "category": "GROCERIES", "description": "mercado",
      "amount": 25.9, "occurredAt": "2026-11-10" },
    { "id": "e089c08e-...", "category": "GROCERIES", "description": "padaria",
      "amount": 15.0, "occurredAt": "2026-11-11" }
]

- teste de erro
GET /transactions/reports/total/PIZZA       -> 400  "Categorias válidas: [GROCERIES, PHARMA, AUTO]"
GET /transactions/reports/monthly/novembro  -> 400  "Use o formato AAAA-MM (ex.: 2026-11)"
