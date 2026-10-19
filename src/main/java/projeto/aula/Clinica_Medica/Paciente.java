package projeto.aula.Clinica_Medica;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String cpf, nome, dataNasc, telefone;

    /** Convênios do paciente, cada um com o seu número de carteirinha. */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<PacienteConvenio> convenios = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "paciente")
    public List<Consulta> consultas = new ArrayList<>();

    public Paciente() {
    }

    public Paciente(int id, String cpf, String nome, String dataNasc, String telefone) {
        this.id = id; this.cpf = cpf; this.nome = nome; this.dataNasc = dataNasc; this.telefone = telefone;
    }

    public void cadastrar() { System.out.println("Paciente cadastrado: " + nome); }

    public void atualizarDados(String nome, String telefone) { this.nome = nome; this.telefone = telefone; }

    public List<Consulta> consultarHistorico() { return consultas; }

    /**
     * Vincula o paciente a uma operadora guardando o seu número de carteirinha.
     * Se o convênio já estiver vinculado, apenas atualiza os dados do vínculo
     * (assim o paciente nunca fica com duas carteirinhas do mesmo convênio).
     */
    public PacienteConvenio vincularConvenio(Convenio convenio, String numeroCarteirinha, String situacao) {
        PacienteConvenio vinculo = buscarVinculo(convenio.id)
                .orElseGet(() -> {
                    PacienteConvenio novo = new PacienteConvenio(this, convenio, numeroCarteirinha, situacao);
                    convenios.add(novo);
                    return novo;
                });
        vinculo.numeroCarteirinha = numeroCarteirinha;
        if (situacao != null && !situacao.isBlank()) {
            vinculo.situacao = situacao;
        }
        return vinculo;
    }

    /** Remove o vínculo com a operadora informada (e a respectiva carteirinha). */
    public boolean removerConvenio(int convenioId) {
        return convenios.removeIf(vinculo -> vinculo.convenio != null && vinculo.convenio.id == convenioId);
    }

    /** Busca o vínculo do paciente com a operadora informada. */
    public Optional<PacienteConvenio> buscarVinculo(int convenioId) {
        return convenios.stream()
                .filter(vinculo -> vinculo.convenio != null && vinculo.convenio.id == convenioId)
                .findFirst();
    }
}
