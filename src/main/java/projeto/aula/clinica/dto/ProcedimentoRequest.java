package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/consultas/{id}/procedimentos}.
 */
public record ProcedimentoRequest(String nome, double valorPadrao, double valorCobrado) {
}
