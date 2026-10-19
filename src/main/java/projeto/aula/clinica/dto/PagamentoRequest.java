package projeto.aula.clinica.dto;

/**
 * Corpo esperado em {@code POST /api/consultas/{id}/pagamentos}.
 * O pagamento nasce com situação PENDENTE.
 */
public record PagamentoRequest(String data, double valor, String formaPagamento) {
}
