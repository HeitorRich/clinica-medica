package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Convênio = operadora do plano de saúde (catálogo único).
 *
 * O vínculo com cada paciente (e o respectivo número de carteirinha) fica em
 * {@link PacienteConvenio}, pois uma mesma operadora atende vários pacientes.
 */
@Entity
@Table(name = "convenio")
@Getter
@Setter
@NoArgsConstructor
public class Convenio extends EntidadeBase {

    /** CNPJ da operadora. */
    private String cnpj;

    /** Nome da operadora (ex.: Unimed). */
    private String nome;

    /** Código ANS da operadora. */
    private String codigoAns;

    @JsonIgnore
    @OneToMany(mappedBy = "convenio")
    private List<PacienteConvenio> vinculos = new ArrayList<>();

    public Convenio(String cnpj, String nome, String codigoAns) {
        this.cnpj = cnpj;
        this.nome = nome;
        this.codigoAns = codigoAns;
    }

    /** Usado pelo endpoint {@code GET /api/convenios/{id}/pacientes}. */
    public List<Paciente> consultarPacientes() {
        return vinculos.stream()
                .map(PacienteConvenio::getPaciente)
                .toList();
    }
}
