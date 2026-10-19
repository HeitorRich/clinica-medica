package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Medicamento;
import projeto.aula.clinica.domain.Prescricao;
import projeto.aula.clinica.dto.MedicamentoRequest;
import projeto.aula.clinica.repository.PrescricaoRepository;

/**
 * Prescrições e os medicamentos de cada uma.
 */
@Service
public class PrescricaoService extends CrudService<Prescricao> {

    private final MedicamentoService medicamentoService;

    public PrescricaoService(PrescricaoRepository repository, MedicamentoService medicamentoService) {
        super(repository);
        this.medicamentoService = medicamentoService;
    }

    /** A lista de medicamentos é preservada na atualização (ela tem endpoint próprio). */
    @Override
    protected void copiarCampos(Prescricao origem, Prescricao destino) {
        destino.setData(origem.getData());
    }

    /** Cadastra o medicamento e o adiciona à receita. */
    public Prescricao adicionarMedicamento(int prescricaoId, MedicamentoRequest dados) {
        Prescricao prescricao = buscarObrigatorio(prescricaoId);
        Medicamento medicamento = medicamentoService.salvar(new Medicamento(
                dados.nome(), dados.dose(), dados.frequencia(), dados.duracao()));
        prescricao.adicionarMedicamento(medicamento);
        return salvar(prescricao);
    }
}
