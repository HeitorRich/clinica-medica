package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Especialidade médica (ex.: Cardiologia). É um catálogo reutilizado por
 * vários médicos.
 */
@Entity
@Table(name = "especialidade")
@Getter
@Setter
@NoArgsConstructor
public class Especialidade extends EntidadeBase {

    private String nome;
    private String descricao;

    @JsonIgnore
    @ManyToMany(mappedBy = "especialidades")
    private List<Medico> medicos = new ArrayList<>();

    public Especialidade(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    /** Usado pelo endpoint {@code GET /api/especialidades/{id}/medicos}. */
    public List<Medico> listaDeMedicos() {
        return medicos;
    }

    /** Lado inverso do vínculo mantido por {@link Medico#adicionarEspecialidade(Especialidade)}. */
    public void adicionarMedico(Medico medico) {
        medicos.add(medico);
    }
}
