package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especialidade")
public class Especialidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nome, descricao;

    @JsonIgnore
    @ManyToMany(mappedBy = "especialidades")
    public List<Medico> medicos = new ArrayList<>();

    public Especialidade() {
    }

    public Especialidade(int id, String nome, String descricao) { this.id = id; this.nome = nome; this.descricao = descricao; }

    public List<Medico> listaDeMedicos() { return medicos; }
}
