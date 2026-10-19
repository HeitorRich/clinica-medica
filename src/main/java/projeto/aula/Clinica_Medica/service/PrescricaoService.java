package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Prescricao;
import projeto.aula.Clinica_Medica.repository.PrescricaoRepository;

@Service
public class PrescricaoService extends AbstractCrudService<Prescricao> {

    public PrescricaoService(PrescricaoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Prescricao origem, Prescricao destino) {
        destino.data = origem.data;
    }
}
