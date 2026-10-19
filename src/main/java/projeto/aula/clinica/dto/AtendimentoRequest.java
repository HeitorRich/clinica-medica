package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/consultas/{id}/atendimento}.
 */
public record AtendimentoRequest(String queixaPrincipal, String diagnostico, String observacoes) {
}
