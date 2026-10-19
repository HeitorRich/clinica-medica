package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pagamento de uma consulta. Nasce PENDENTE e pode ser confirmado (PAGO) ou
 * estornado.
 */
@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento extends EntidadeBase {

    private String data;
    private String formaPagamento;
    private double valor;

    @Enumerated(EnumType.STRING)
    private SituacaoPagamento situacao = SituacaoPagamento.PENDENTE;

    /**
     * Consulta a que este pagamento se refere. As coleções da consulta são
     * omitidas na serialização para não repetir dados nem gerar referência
     * circular (consulta → pagamentos → consulta → ...).
     */
    @ManyToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    private Consulta consulta;

    public Pagamento(String data, double valor, String formaPagamento) {
        this.data = data;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
    }

    /** Usado pelo endpoint {@code POST /api/pagamentos/{id}/pagar}. */
    public void registrarPagamento() {
        situacao = SituacaoPagamento.PAGO;
    }

    /** Usado pelo endpoint {@code POST /api/pagamentos/{id}/estornar}. */
    public void estornar() {
        situacao = SituacaoPagamento.ESTORNADO;
    }
}
