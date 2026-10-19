package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/exames/{id}/resultado}.
 */
public record ResultadoExameRequest(String data, String descricao, String conclusao) {
}
