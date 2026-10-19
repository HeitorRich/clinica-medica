package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Exame solicitado em uma consulta. O resultado (quando existe) fica em
 * {@link ResultadoExame}.
 */
@Entity
@Table(name = "exame")
@Getter
@Setter
@NoArgsConstructor
public class Exame extends EntidadeBase {

    private String nome;
    private String tipo;
    private String dataSolicitacao;

    @Enumerated(EnumType.STRING)
    private SituacaoExame situacao = SituacaoExame.SOLICITADO;

    /**
     * Consulta que originou o exame. É por ela que se sabe a qual paciente o
     * exame pertence ({@code consulta.paciente}). As coleções da consulta são
     * omitidas na serialização para não gerar referência circular
     * (consulta → exames → consulta → ...).
     */
    @ManyToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnoreProperties({"exames", "procedimentos", "pagamentos", "prescricao"})
    private Consulta consulta;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resultado_id")
    @JsonIgnoreProperties({"exame"})
    private ResultadoExame resultado;

    public Exame(String nome, String tipo, String dataSolicitacao) {
        this.nome = nome;
        this.tipo = tipo;
        this.dataSolicitacao = dataSolicitacao;
    }

    /** Usado pelo endpoint {@code POST /api/exames/{id}/resultado}. */
    public void registrarResultado(ResultadoExame resultado) {
        this.resultado = resultado;
        resultado.setExame(this);
        this.situacao = SituacaoExame.CONCLUIDO;
    }

    /** Usado pelo endpoint {@code GET /api/exames/{id}/paciente}. */
    public Paciente pacienteDoExame() {
        return consulta == null ? null : consulta.getPaciente();
    }
}
