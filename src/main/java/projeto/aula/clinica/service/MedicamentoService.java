package projeto.aula.clinica.service;

import org.springframework.stereotype.Service;
import projeto.aula.clinica.domain.Medicamento;
import projeto.aula.clinica.repository.MedicamentoRepository;

/**
 * Catálogo de medicamentos (cada item pertence a uma receita).
 */
@Service
public class MedicamentoService extends CrudService<Medicamento> {

    public MedicamentoService(MedicamentoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Medicamento origem, Medicamento destino) {
        destino.setNome(origem.getNome());
        destino.setDose(origem.getDose());
        destino.setFrequencia(origem.getFrequencia());
        destino.setDuracao(origem.getDuracao());
    }
}
