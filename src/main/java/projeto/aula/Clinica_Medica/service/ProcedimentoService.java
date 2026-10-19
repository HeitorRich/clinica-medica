package projeto.aula.Clinica_Medica.service;

import org.springframework.stereotype.Service;
import projeto.aula.Clinica_Medica.Procedimento;
import projeto.aula.Clinica_Medica.repository.ProcedimentoRepository;

@Service
public class ProcedimentoService extends AbstractCrudService<Procedimento> {

    public ProcedimentoService(ProcedimentoRepository repository) {
        super(repository);
    }

    @Override
    protected void copiarCampos(Procedimento origem, Procedimento destino) {
        destino.nome = origem.nome;
        destino.valorPadrao = origem.valorPadrao;
        destino.valorCobrado = origem.valorCobrado;
    }
}
