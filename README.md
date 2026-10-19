# Clínica Médica

Sistema de gestão de clínica médica com **API REST em Spring Boot** e duas telas web
estáticas (uma tela de uso diário e uma tela para explorar a API).

Cobre pacientes, convênios (operadoras), médicos, especialidades, consultas,
atendimento, exames e resultados, prescrições, procedimentos e pagamentos.

## Tecnologias

- **Java 27** + **Spring Boot 4.1.1** (Web MVC, Data JPA)
- **PostgreSQL** (as tabelas são criadas/atualizadas automaticamente)
- **Lombok** (getters/setters)
- **springdoc-openapi** (Swagger UI em `/swagger-ui.html`)
- Frontend em **HTML + CSS + JavaScript puro** (sem framework e sem etapa de build)

## Como executar

1. Tenha o **JDK 27** e um **PostgreSQL** em `localhost:5432`.
2. Crie o banco `clinica_medica` (usuário/senha configurados em
   `src/main/resources/application.yaml`).
3. Suba a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Acesse:
   - <http://localhost:8080/> — tela principal
   - <http://localhost:8080/crud-generico.html> — tela de exploração da API
   - <http://localhost:8080/swagger-ui.html> — documentação dos endpoints

Na primeira execução, o `DataSeeder` popula o banco com um cenário de exemplo
(dois pacientes, um médico, convênios, uma consulta completa etc.). Se já existir
algum paciente cadastrado, nada é inserido.

> A senha do banco está no `application.yaml` como estava no projeto original.
> Para publicar em outro ambiente, prefira ler de uma variável de ambiente
> (ex.: `${DB_PASSWORD}`).

## Estrutura do projeto

```
src/main/java/projeto/aula/clinica/
├── ClinicaMedicaApplication.java
├── domain/          # entidades JPA + regras que dependem de uma única entidade
│   ├── EntidadeBase.java        (id gerado pelo banco)
│   ├── Paciente.java, Medico.java, Consulta.java, ...
│   └── Situacao*.java           (situações como enum, gravadas como texto)
├── repository/      # interfaces Spring Data JPA
├── service/         # regras de negócio e orquestração
│   ├── CrudService.java         (CRUD genérico para todas as entidades)
│   └── *Service.java            (só o que é específico de cada entidade)
├── controller/      # API REST
│   ├── CrudController.java      (GET/POST/PUT/DELETE genéricos)
│   └── *Controller.java         (rotas e endpoints extras)
├── dto/             # corpos de entrada dos endpoints de ação (records)
├── exception/       # erros de domínio (404, 409, 400)
├── config/          # serialização JSON, dados de exemplo
└── util/            # formatação de CPF

src/main/resources/static/
├── index.html                    # tela principal (só marcação)
├── crud-generico.html            # tela de API (só marcação)
├── css/
│   ├── comum.css                 # regras mínimas compartilhadas
│   ├── index.css                 # estilo da tela principal
│   └── crud-generico.css         # estilo da tela de API
└── js/
    ├── comum.js                  # utilitários (escapeHtml, getPath, máscaras)
    ├── api.js                    # cliente HTTP compartilhado
    ├── masks.js                  # máscaras de digitação
    ├── recursos.js               # descrição das entidades da tela principal
    ├── index.js                  # motor da tela principal (tabelas, formulários, modais)
    ├── recursos-crud.js          # descrição das entidades da tela de API
    └── crud-generico.js          # motor da tela de API
```

### Camadas do backend

| Camada       | Responsabilidade                                                                 |
|--------------|----------------------------------------------------------------------------------|
| `domain`     | Entidades JPA e regras que dependem de um único objeto (ex.: vincular convênio). |
| `repository` | Acesso ao banco; consultas são geradas pelo Spring Data.                         |
| `service`    | Regras de negócio, validações e orquestração entre entidades.                    |
| `controller` | Só traduz HTTP ↔ chamadas de serviço (sem regra de negócio).                     |
| `dto`        | Corpos de entrada enxutos para os endpoints que não são CRUD puro.               |
| `exception`  | Erros esperados, convertidos em `ProblemDetail` pelo `ApiExceptionHandler`.       |

`CrudService` e `CrudController` concentram o que é igual em todas as entidades.
Cada serviço concreto declara apenas:

- **quais campos podem ser alterados** na atualização (`copiarCampos`), evitando
  que uma atualização parcial apague dados como situação de pagamento;
- **as operações próprias da entidade** (ex.: `ConsultaService.registrarAtendimento`,
  `ExameService.registrarResultado`).

## Principais decisões do refactor

- **Nada de funcionalidade mudou**: rotas, códigos HTTP, formato do JSON, regras de
  validação e dados iniciais foram preservados (ver "Compatibilidade verificada").
- **Campos encapsulados**: as entidades passaram a usar campos privados com
  getters/setters (Lombok). O JSON continua com os mesmos nomes de propriedade.
- **`id` em `EntidadeBase`**: evita repetir `@Id`/`@GeneratedValue` nas 12 entidades.
- **Situações como `enum` + `@Enumerated(STRING)`**: elimina strings soltas
  (`"PAGO"`, `"SOLICITADO"`) mantendo os mesmos valores no banco e no JSON.
- **Regras saíram dos controllers**: histórico, convênios, agenda, especialidades,
  atendimento, exames, prescrição, pagamentos e procedimentos agora são métodos de
  serviço/domínio; os controllers só chamam.
- **Exceções de domínio**: `CpfJaCadastradoException` (409), `CarteirinhaObrigatoriaException`
  (400) e `RecursoNaoEncontradoException` (404), todas convertidas em `ProblemDetail`.
- **DTOs de entrada** para os endpoints de ação (ex.: `AtendimentoRequest`,
  `ResultadoExameRequest`), em vez de reaproveitar a entidade inteira.
- **Frontend separado por responsabilidade**: CSS, utilitários, cliente HTTP,
  configuração das entidades e motor de cada tela em arquivos próprios.

## Compatibilidade verificada

Antes de publicar a refatoração, o comportamento foi comparado com a versão anterior:

- **API**: 61 requisições (CRUD completo + todos os endpoints de ação, incluindo
  casos de erro 400/404/409) executadas nas duas versões, cada uma em um banco
  PostgreSQL criado do zero. Status e JSON foram idênticos em todas — a única
  diferença é o `timestamp` do corpo de erro 404, que é o horário da chamada.
- **Frontend**: as duas telas foram abertas em navegador headless e o DOM
  renderizado foi comparado (11 telas, formulários e modais na tela principal;
  11 painéis, tabelas e saída na tela de API). Resultado idêntico.
- **CSS**: os arquivos extraídos são iguais ao bloco original, exceto a regra
  `* { box-sizing: border-box; }`, que passou para `css/comum.css`.

## Endpoints

CRUD padrão em todos os recursos: `GET`, `GET /{id}`, `POST`, `PUT /{id}` e `DELETE /{id}`.

| Recurso              | Rota                      | Endpoints adicionais |
|----------------------|---------------------------|----------------------|
| Pacientes            | `/api/pacientes`          | `GET /{id}/historico`, `POST /{id}/cadastrar`, `PUT /{id}/dados`, `GET /{id}/convenios`, `POST/PUT /{id}/convenios/{convenioId}`, `DELETE /{id}/convenios/{convenioId}`, `GET /{id}/convenios/{convenioId}/cobertura` |
| Médicos              | `/api/medicos`            | `GET /{id}/agenda`, `POST/DELETE /{id}/especialidades/{especialidadeId}` |
| Especialidades       | `/api/especialidades`     | `GET /{id}/medicos` |
| Convênios            | `/api/convenios`          | `GET /{id}/pacientes`, `GET /{id}/vinculos` |
| Consultas            | `/api/consultas`          | `POST /{id}/atendimento`, `POST /{id}/exames`, `POST /{id}/prescricao`, `POST /{id}/procedimentos`, `POST /{id}/pagamentos` |
| Exames               | `/api/exames`             | `POST /{id}/resultado`, `GET /{id}/paciente` |
| Resultados de exame  | `/api/resultados-exame`   | — |
| Prescrições          | `/api/prescricoes`        | `POST /{id}/medicamentos` |
| Medicamentos         | `/api/medicamentos`       | — |
| Procedimentos        | `/api/procedimentos`      | `POST /{id}/realizar` |
| Pagamentos           | `/api/pagamentos`         | `POST /{id}/pagar`, `POST /{id}/estornar` |

Exemplo:

```bash
curl -X POST http://localhost:8080/api/consultas/1/atendimento \
  -H "Content-Type: application/json" \
  -d '{"queixaPrincipal":"Dor no peito","diagnostico":"Ansiedade","observacoes":"Retorno em 30 dias"}'
```
