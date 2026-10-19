package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Associação entre paciente e convênio (operadora).
 *
 * Regras do modelo:
 * <ul>
 *   <li>uma operadora atende vários pacientes;</li>
 *   <li>cada paciente tem <b>um</b> número de carteirinha por operadora
 *       (garantido pela chave única paciente + convênio);</li>
 *   <li>a situação (ATIVO/INATIVO) é da cobertura do paciente naquela
 *       operadora, e não da operadora em si.</li>
 * </ul>
 */
@Entity
@Table(
        name = "paciente_convenio",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_paciente_convenio",
                columnNames = {"paciente_id", "convenio_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PacienteConvenio extends EntidadeBase {

    /**
     * Paciente dono da carteirinha. Ao serializar o vínculo, a coleção
     * {@code convenios} do paciente é omitida para não repetir os mesmos dados
     * nem gerar referência circular (paciente → convênios → paciente → ...).
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonIgnoreProperties({"convenios"})
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    /** Número de carteirinha deste paciente na operadora. */
    private String numeroCarteirinha;

    /** ATIVO/INATIVO gravado como texto, igual ao que já existia no banco. */
    @Enumerated(EnumType.STRING)
    private SituacaoCobertura situacao = SituacaoCobertura.ATIVO;

    public PacienteConvenio(Paciente paciente, Convenio convenio, String numeroCarteirinha, SituacaoCobertura situacao) {
        this.paciente = paciente;
        this.convenio = convenio;
        this.numeroCarteirinha = numeroCarteirinha;
        if (situacao != null) {
            this.situacao = situacao;
        }
    }

    /** Indica se o vínculo pertence à operadora informada. */
    public boolean pertenceAOperadora(int convenioId) {
        return convenio != null && convenio.getId() == convenioId;
    }

    /** Usado pelo endpoint {@code GET /api/pacientes/{id}/convenios/{convenioId}/cobertura}. */
    public boolean coberturaAtiva() {
        return situacao == SituacaoCobertura.ATIVO;
    }
}
