package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Consulta;
import projeto.aula.clinica.domain.Convenio;
import projeto.aula.clinica.domain.Paciente;
import projeto.aula.clinica.domain.PacienteConvenio;
import projeto.aula.clinica.domain.SituacaoCobertura;
import projeto.aula.clinica.exception.CarteirinhaObrigatoriaException;
import projeto.aula.clinica.exception.CpfJaCadastradoException;
import projeto.aula.clinica.exception.RecursoNaoEncontradoException;
import projeto.aula.clinica.repository.PacienteRepository;
import projeto.aula.clinica.util.Cpf;

import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio do paciente: unicidade de CPF e vínculo com operadoras.
 */
@Service
public class PacienteService extends CrudService<Paciente> {

    private final PacienteRepository pacienteRepository;
    private final ConvenioService convenioService;

    public PacienteService(PacienteRepository repository, ConvenioService convenioService) {
        super(repository);
        this.pacienteRepository = repository;
        this.convenioService = convenioService;
    }

    /** Insere o paciente validando que o CPF informado ainda não pertence a outro cadastro. */
    @Override
    public Paciente salvar(Paciente paciente) {
        paciente.setCpf(Cpf.formatar(paciente.getCpf()));
        if (paciente.isNovo()) {
            validarCpfDisponivel(paciente.getCpf(), 0);
        }
        return super.salvar(paciente);
    }

    @Override
    public Optional<Paciente> atualizar(int id, Paciente dados) {
        dados.setCpf(Cpf.formatar(dados.getCpf()));
        return buscar(id).map(atual -> {
            validarCpfDisponivel(dados.getCpf(), id);
            copiarCampos(dados, atual);
            return repository.save(atual);
        });
    }

    /** Na alteração, só os dados cadastrais mudam: convênios e histórico são preservados. */
    @Override
    protected void copiarCampos(Paciente origem, Paciente destino) {
        destino.setCpf(origem.getCpf());
        destino.setNome(origem.getNome());
        destino.setDataNasc(origem.getDataNasc());
        destino.setTelefone(origem.getTelefone());
    }

    /**
     * Vincula o paciente à operadora guardando o número da carteirinha.
     * Se o vínculo já existir, atualiza a carteirinha/situação; se o número não
     * for informado, mantém o que já estava cadastrado.
     */
    public Paciente vincularConvenio(int pacienteId, int convenioId, String numeroCarteirinha, String situacao) {
        Paciente paciente = buscarObrigatorio(pacienteId);
        Convenio convenio = convenioService.buscarObrigatorio(convenioId);
        String carteirinha = definirCarteirinha(paciente, convenio, numeroCarteirinha);
        paciente.vincularConvenio(convenio, carteirinha, SituacaoCobertura.deTexto(situacao));
        return salvar(paciente);
    }

    /** Desvincula a operadora do paciente (remove o vínculo e a carteirinha). */
    public Paciente desvincularConvenio(int pacienteId, int convenioId) {
        Paciente paciente = buscarObrigatorio(pacienteId);
        paciente.removerConvenio(convenioId);
        return salvar(paciente);
    }

    /** Convênios (operadora + carteirinha) do paciente. */
    public List<PacienteConvenio> listarConvenios(int pacienteId) {
        return buscarObrigatorio(pacienteId).getConvenios();
    }

    /** Histórico de consultas do paciente. */
    public List<Consulta> historico(int pacienteId) {
        return buscarObrigatorio(pacienteId).consultarHistorico();
    }

    /** Executa a rotina de cadastro do paciente (registro em log). */
    public Paciente cadastrar(int pacienteId) {
        Paciente paciente = buscarObrigatorio(pacienteId);
        paciente.cadastrar();
        return salvar(paciente);
    }

    /** Atualiza apenas nome e telefone, sem passar pela validação de CPF. */
    public Paciente atualizarDados(int pacienteId, String nome, String telefone) {
        Paciente paciente = buscarObrigatorio(pacienteId);
        paciente.atualizarDados(nome, telefone);
        return salvar(paciente);
    }

    /** Informa se o vínculo do paciente com a operadora está com cobertura ativa. */
    public boolean validarCobertura(int pacienteId, int convenioId) {
        return buscarObrigatorio(pacienteId)
                .buscarVinculo(convenioId)
                .orElseThrow(() -> RecursoNaoEncontradoException.paraVinculo(pacienteId, convenioId))
                .coberturaAtiva();
    }

    /**
     * Decide qual número de carteirinha usar: o informado agora ou, quando o
     * cliente não envia nada, o que já estava gravado no vínculo.
     */
    private String definirCarteirinha(Paciente paciente, Convenio convenio, String numeroCarteirinha) {
        String carteirinha = (numeroCarteirinha == null || numeroCarteirinha.isBlank())
                ? paciente.buscarVinculo(convenio.getId())
                        .map(PacienteConvenio::getNumeroCarteirinha)
                        .orElse(null)
                : numeroCarteirinha.trim();

        if (carteirinha == null || carteirinha.isBlank()) {
            throw new CarteirinhaObrigatoriaException(convenio.getNome());
        }
        return carteirinha;
    }

    /**
     * Rejeita o cadastro quando já existe outro paciente com o mesmo CPF.
     *
     * @param cpf        CPF já padronizado
     * @param idIgnorado id do próprio paciente na edição (0 ao criar)
     */
    private void validarCpfDisponivel(String cpf, int idIgnorado) {
        if (cpf == null || cpf.isBlank()) {
            return; // CPF é opcional no cadastro
        }
        boolean jaCadastrado = pacienteRepository.findByCpf(cpf).stream()
                .anyMatch(encontrado -> encontrado.getId() != idIgnorado);
        if (jaCadastrado) {
            throw new CpfJaCadastradoException(cpf);
        }
    }
}
