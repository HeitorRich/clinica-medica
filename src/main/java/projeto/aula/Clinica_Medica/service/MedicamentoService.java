package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Medicamento;
import projeto.aula.Clinica_Medica.repository.MedicamentoRepository;

@Service
public class MedicamentoService extends AbstractCrudService<Medicamento> {

    public MedicamentoService(MedicamentoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Medicamento origem, Medicamento destino) {
        destino.nome = origem.nome;
        destino.dose = origem.dose;
        destino.frequencia = origem.frequencia;
        destino.duracao = origem.duracao;
    }
}
