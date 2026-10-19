package projeto.aula.clinica.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Paciente da clínica.
 *
 * A entidade guarda as regras que dependem apenas do próprio paciente (vínculo
 * com operadoras e histórico de consultas). Assim essas decisões não precisam
 * ser repetidas nas camadas de serviço/controller.
 */
@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class Paciente extends EntidadeBase {

    private String cpf;
    private String nome;
    private String dataNasc;
    private String telefone;

    /** Operadoras do paciente; cada vínculo guarda o número da carteirinha. */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PacienteConvenio> convenios = new ArrayList<>();

    /**
     * Histórico de consultas. Não entra no JSON: a consulta é quem aponta para
     * o paciente, e expor a lista aqui geraria repetição e referência circular.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    public Paciente(String cpf, String nome, String dataNasc, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNasc = dataNasc;
        this.telefone = telefone;
    }

    /** Usado pelo endpoint {@code POST /api/pacientes/{id}/cadastrar}. */
    public void cadastrar() {
        log.info("Paciente cadastrado: {}", nome);
    }

    /** Usado pelo endpoint {@code PUT /api/pacientes/{id}/dados}. */
    public void atualizarDados(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
    }

    /** Usado pelo endpoint {@code GET /api/pacientes/{id}/historico}. */
    public List<Consulta> consultarHistorico() {
        return consultas;
    }

    /** Mantém os dois lados do relacionamento em memória antes de gravar. */
    public void adicionarConsulta(Consulta consulta) {
        consultas.add(consulta);
    }

    /**
     * Vincula o paciente a uma operadora guardando o número da carteirinha.
     * Se o convênio já estiver vinculado, apenas atualiza os dados do vínculo,
     * garantindo que o paciente nunca fique com duas carteirinhas da mesma
     * operadora.
     */
    public PacienteConvenio vincularConvenio(Convenio convenio, String numeroCarteirinha, SituacaoCobertura situacao) {
        PacienteConvenio vinculo = buscarVinculo(convenio.getId())
                .orElseGet(() -> {
                    PacienteConvenio novo = new PacienteConvenio(this, convenio, numeroCarteirinha, situacao);
                    convenios.add(novo);
                    return novo;
                });
        vinculo.setNumeroCarteirinha(numeroCarteirinha);
        if (situacao != null) {
            vinculo.setSituacao(situacao);
        }
        return vinculo;
    }

    /** Remove o vínculo com a operadora informada (e a respectiva carteirinha). */
    public boolean removerConvenio(int convenioId) {
        return convenios.removeIf(vinculo -> vinculo.pertenceAOperadora(convenioId));
    }

    /** Busca o vínculo do paciente com a operadora informada. */
    public Optional<PacienteConvenio> buscarVinculo(int convenioId) {
        return convenios.stream()
                .filter(vinculo -> vinculo.pertenceAOperadora(convenioId))
                .findFirst();
    }
}
