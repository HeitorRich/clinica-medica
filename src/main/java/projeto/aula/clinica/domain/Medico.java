package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Médico da clínica, suas especialidades e a agenda de consultas.
 */
@Entity
@Table(name = "medico")
@Getter
@Setter
@NoArgsConstructor
public class Medico extends EntidadeBase {

    private String nome;
    private String crm;

    /** Especialidades do médico (relação N:N registrada na tabela medico_especialidade). */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "medico_especialidade",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidade_id"))
    private List<Especialidade> especialidades = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas = new ArrayList<>();

    public Medico(String nome, String crm) {
        this.nome = nome;
        this.crm = crm;
    }

    /** Mantém os dois lados do relacionamento N:N ao vincular a especialidade. */
    public void adicionarEspecialidade(Especialidade especialidade) {
        especialidades.add(especialidade);
        especialidade.adicionarMedico(this);
    }

    /** Desvincula a especialidade sem apagar o cadastro dela. */
    public boolean removerEspecialidade(int especialidadeId) {
        return especialidades.removeIf(especialidade -> especialidade.getId() == especialidadeId);
    }

    /** Usado pelo endpoint {@code GET /api/medicos/{id}/agenda}. */
    public List<Consulta> consultarAgenda() {
        return consultas;
    }

    /** Mantém os dois lados do relacionamento em memória antes de gravar. */
    public void adicionarConsulta(Consulta consulta) {
        consultas.add(consulta);
    }
}
