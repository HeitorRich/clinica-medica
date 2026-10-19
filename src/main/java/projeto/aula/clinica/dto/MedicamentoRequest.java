package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/prescricoes/{id}/medicamentos}.
 */
public record MedicamentoRequest(String nome, String dose, String frequencia, String duracao) {
}
