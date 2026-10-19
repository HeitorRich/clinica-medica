package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Convênio = operadora do plano de saúde (catálogo único).
 * O vínculo com cada paciente (e o respectivo número de carteirinha) fica em
 * {@link PacienteConvenio}, pois uma mesma operadora atende vários pacientes.
 */
@Entity
@Table(name = "convenio")
public class Convenio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    /** CNPJ da operadora. */
    public String cnpj;

    /** Nome da operadora (ex.: Unimed). */
    public String nome;

    /** Código ANS da operadora. */
    public String codigoAns;

    @JsonIgnore
    @OneToMany(mappedBy = "convenio")
    public List<PacienteConvenio> vinculos = new ArrayList<>();

    public Convenio() {
    }

    public Convenio(int id, String cnpj, String nome, String codigoAns) {
        this.id = id;
        this.cnpj = cnpj;
        this.nome = nome;
        this.codigoAns = codigoAns;
    }

    /** Pacientes que utilizam esta operadora. */
    public List<Paciente> consultarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        for (PacienteConvenio vinculo : vinculos) {
            pacientes.add(vinculo.paciente);
        }
        return pacientes;
    }
}
