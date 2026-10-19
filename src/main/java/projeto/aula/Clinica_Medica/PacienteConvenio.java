package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Tabela de associação entre paciente e convênio.
 *
 * Regras do modelo:
 * <ul>
 *   <li>um convênio (operadora) pode ser usado por vários pacientes;</li>
 *   <li>cada paciente tem <b>um</b> número de carteirinha por convênio
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
public class PacienteConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    /**
     * O paciente é omitido ao serializar o vínculo dentro do paciente (evita
     * referência circular); ao listar os pacientes de um convênio o
     * {@code convenios} do paciente também é omitido pelo mesmo motivo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonIgnoreProperties({"convenios"})
    public Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    public Convenio convenio;

    /** Número de carteirinha deste paciente na operadora. */
    public String numeroCarteirinha;

    public String situacao = "ATIVO";

    public PacienteConvenio() {
    }

    public PacienteConvenio(Paciente paciente, Convenio convenio, String numeroCarteirinha, String situacao) {
        this.paciente = paciente;
        this.convenio = convenio;
        this.numeroCarteirinha = numeroCarteirinha;
        if (situacao != null && !situacao.isBlank()) {
            this.situacao = situacao;
        }
    }

    /** Executa PacienteConvenio.validacaoCobertura(). */
    public boolean validacaoCobertura() {
        return "ATIVO".equalsIgnoreCase(situacao);
    }
}
