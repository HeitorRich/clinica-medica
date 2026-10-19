package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Convenio;
import projeto.aula.Clinica_Medica.Paciente;
import projeto.aula.Clinica_Medica.PacienteConvenio;
import projeto.aula.Clinica_Medica.repository.PacienteRepository;

import java.util.Optional;

@Service
public class PacienteService extends AbstractCrudService<Paciente> {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository repository) {
        super(repository);
        this.pacienteRepository = repository;
    }

    /**
     * Insere o paciente no banco de dados validando que o CPF informado ainda
     * não pertence a outro cadastro.
     */
    @Override
    public Paciente salvar(Paciente paciente) {
        paciente.cpf = padronizarCpf(paciente.cpf);
        if (paciente.id <= 0) {
            validarCpfDisponivel(paciente.cpf, 0);
        }
        return super.salvar(paciente);
    }

    /**
     * Atualiza o paciente mantendo a mesma regra: o CPF não pode repetir o de
     * outro registro (a validação ignora o próprio paciente, pois a entidade é
     * identificada pelo id informado).
     */
    @Override
    public Optional<Paciente> atualizar(int id, Paciente paciente) {
        paciente.cpf = padronizarCpf(paciente.cpf);
        if (buscar(id).isPresent()) {
            validarCpfDisponivel(paciente.cpf, id);
        }
        return super.atualizar(id, paciente);
    }

    @Override
    protected void copiarCampos(Paciente origem, Paciente destino) {
        destino.cpf = origem.cpf;
        destino.nome = origem.nome;
        destino.dataNasc = origem.dataNasc;
        destino.telefone = origem.telefone;
    }

    /**
     * Vincula o paciente à operadora guardando o número de carteirinha.
     * Cada paciente fica com um único vínculo por convênio (se já existir,
     * os dados da carteirinha são atualizados).
     * O número é obrigatório ao criar o vínculo; numa atualização em que ele
     * não é informado, a carteirinha já cadastrada é mantida.
     */
    public Paciente vincularConvenio(Paciente paciente, Convenio convenio, String numeroCarteirinha, String situacao) {
        String carteirinha = (numeroCarteirinha == null || numeroCarteirinha.isBlank())
                ? paciente.buscarVinculo(convenio.id).map(vinculo -> vinculo.numeroCarteirinha).orElse(null)
                : numeroCarteirinha.trim();
        if (carteirinha == null || carteirinha.isBlank()) {
            throw new CarteirinhaObrigatoriaException(convenio.nome);
        }
        paciente.vincularConvenio(convenio, carteirinha, situacao);
        return salvar(paciente);
    }

    /** Desvincula a operadora do paciente (remove o vínculo e a carteirinha). */
    public Paciente desvincularConvenio(Paciente paciente, int convenioId) {
        paciente.removerConvenio(convenioId);
        return salvar(paciente);
    }

    /**
     * Rejeita o cadastro quando já existe outro paciente com o mesmo CPF.
     *
     * @param cpf          CPF informado (com ou sem máscara)
     * @param idIgnorado   id do próprio paciente na edição (0 ao criar)
     */
    private void validarCpfDisponivel(String cpf, int idIgnorado) {
        String documento = padronizarCpf(cpf);
        if (documento == null || documento.isBlank()) {
            return; // CPF é opcional no cadastro
        }

        boolean jaCadastrado = pacienteRepository.findByCpf(documento).stream()
                .anyMatch(encontrado -> encontrado.id != idIgnorado);

        if (jaCadastrado) {
            throw new CpfJaCadastradoException(documento);
        }
    }

    /**
     * Deixa o CPF sempre no padrão 000.000.000-00, independentemente de o
     * cliente enviar "12345678900" ou "123.456.789-00". Valores com quantidade
     * de dígitos diferente de 11 são mantidos como recebidos (apenas sem espaços).
     */
    private static String padronizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return cpf.trim();
        }
        return digitos.substring(0, 3) + "."
                + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-"
                + digitos.substring(9);
    }
}
