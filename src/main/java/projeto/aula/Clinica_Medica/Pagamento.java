package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String data, formaPagamento, situacao = "PENDENTE";
    public double valor;

    /**
     * Consulta a que este pagamento se refere. As coleções da consulta são
     * omitidas na serialização para não repetir dados nem gerar referência
     * circular (consulta -> pagamentos -> consulta -> ...).
     */
    @ManyToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    public Consulta consulta;

    public Pagamento() {
    }

    public Pagamento(int id, String data, double valor, String formaPagamento) {
        this.id = id; this.data = data; this.valor = valor; this.formaPagamento = formaPagamento;
    }

    public void registrarPag() { situacao = "PAGO"; }

    public void estornarPag() { situacao = "ESTORNADO"; }
}
