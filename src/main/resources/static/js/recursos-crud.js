/* =========================================================================
   Descricao das entidades exibidas na tela de API (crud-generico.html).
   Campos, colunas e acoes de cada recurso ficam aqui; o motor da tela esta em
   js/crud-generico.js.
   ========================================================================= */

const resources = [
  {
    key: 'pacientes',
    title: 'Pacientes',
    path: '/api/pacientes',
    fields: [
      { name: 'cpf', label: 'CPF', mask: 'cpf' },
      { name: 'nome', label: 'Nome', mask: 'letras' },
      { name: 'dataNasc', label: 'Data de nascimento', mask: 'data' },
      { name: 'telefone', label: 'Telefone', mask: 'telefone' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'cpf', label: 'CPF' },
      { path: 'dataNasc', label: 'Nascimento' },
      { path: 'telefone', label: 'Telefone' },
      { path: 'convenios', label: 'Convênios', format: v => (v || []).map(c => (getPath(c, 'convenio.nome') || '?') + ': ' + (c.numeroCarteirinha || '—')).join(', ') || '—' }
    ],
    actions: [
      { label: 'Histórico', run: async item => api('GET', `/api/pacientes/${item.id}/historico`) },
      { label: 'Cadastrar', run: async item => api('POST', `/api/pacientes/${item.id}/cadastrar`) },
      {
        label: 'Editar dados',
        run: async item => {
          const nome = prompt('Novo nome:', item.nome);
          if (nome === null) return;
          const telefone = prompt('Novo telefone:', item.telefone);
          if (telefone === null) return;
          await api('PUT', `/api/pacientes/${item.id}/dados?nome=${encodeURIComponent(nome)}&telefone=${encodeURIComponent(telefone)}`);
        }
      },
      {
        label: 'Vincular convênio',
        run: async item => {
          const id = prompt('ID do convênio (operadora):');
          if (!id) return;
          const carteirinha = prompt('Nº da carteirinha deste paciente:');
          if (!carteirinha) return;
          await api('POST', `/api/pacientes/${item.id}/convenios/${id}?numeroCarteirinha=${encodeURIComponent(carteirinha)}`);
        }
      }
    ]
  },
  {
    key: 'medicos',
    title: 'Médicos',
    path: '/api/medicos',
    fields: [
      { name: 'nome', label: 'Nome', mask: 'letras' },
      { name: 'crm', label: 'CRM', mask: 'crm' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'crm', label: 'CRM' },
      { path: 'especialidades', label: 'Especialidades', format: v => (v || []).map(e => e.nome).join(', ') || '-' }
    ],
    actions: [
      { label: 'Agenda', run: async item => api('GET', `/api/medicos/${item.id}/agenda`) },
      {
        label: 'Add especialidade',
        run: async item => {
          const id = prompt('ID da especialidade:');
          if (!id) return;
          await api('POST', `/api/medicos/${item.id}/especialidades/${id}`);
        }
      }
    ]
  },
  {
    key: 'especialidades',
    title: 'Especialidades',
    path: '/api/especialidades',
    fields: [
      { name: 'nome', label: 'Nome', mask: 'letras' },
      { name: 'descricao', label: 'Descrição', mask: 'texto' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'descricao', label: 'Descrição' }
    ],
    actions: [
      { label: 'Médicos', run: async item => api('GET', `/api/especialidades/${item.id}/medicos`) }
    ]
  },
  {
    key: 'convenios',
    title: 'Convênios',
    path: '/api/convenios',
    fields: [
      { name: 'nome', label: 'Nome da operadora', mask: 'alfanumerico' },
      { name: 'cnpj', label: 'CNPJ da operadora', mask: 'cnpj' },
      { name: 'codigoAns', label: 'Código ANS', mask: '000000' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Operadora' },
      { path: 'cnpj', label: 'CNPJ' },
      { path: 'codigoAns', label: 'Código ANS' }
    ],
    actions: [
      { label: 'Pacientes', run: async item => api('GET', `/api/convenios/${item.id}/vinculos`) }
    ]
  },
  {
    key: 'consultas',
    title: 'Consultas',
    path: '/api/consultas',
    fields: [
      { name: 'data', label: 'Data (dd/mm/aaaa)', mask: 'data' },
      { name: 'horario', label: 'Horário (hh:mm)', mask: 'hora' },
      { name: 'paciente.id', label: 'ID do paciente', type: 'number' },
      { name: 'medico.id', label: 'ID do médico', type: 'number' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'data', label: 'Data' },
      { path: 'horario', label: 'Hora' },
      { path: 'paciente.nome', label: 'Paciente' },
      { path: 'medico.nome', label: 'Médico' },
      { path: 'diagnostico', label: 'Diagnóstico' }
    ],
    actions: [
      {
        label: 'Registrar atendimento',
        run: async item => {
          const queixaPrincipal = prompt('Queixa principal:', item.queixaPrincipal || '');
          if (queixaPrincipal === null) return;
          const diagnostico = prompt('Diagnóstico:', item.diagnostico || '');
          if (diagnostico === null) return;
          const observacoes = prompt('Observações:', item.observacoes || '');
          if (observacoes === null) return;
          await api('POST', `/api/consultas/${item.id}/atendimento`, { queixaPrincipal, diagnostico, observacoes });
        }
      },
      {
        label: 'Solicitar exame',
        run: async item => {
          const nome = prompt('Nome do exame:');
          if (!nome) return;
          const tipo = prompt('Tipo do exame:') || '';
          await api('POST', `/api/consultas/${item.id}/exames`, { nome, tipo });
        }
      },
      { label: 'Gerar prescrição', run: async item => api('POST', `/api/consultas/${item.id}/prescricao`) },
      {
        label: 'Add pagamento',
        run: async item => {
          const valor = prompt('Valor:');
          if (!valor) return;
          const formaPagamento = prompt('Forma de pagamento (PIX, DINHEIRO...):') || '';
          await api('POST', `/api/consultas/${item.id}/pagamentos`, { data: item.data, valor: Number(valor), formaPagamento });
        }
      },
      {
        label: 'Add procedimento',
        run: async item => {
          const nome = prompt('Nome do procedimento:');
          if (!nome) return;
          const valor = Number(prompt('Valor:') || 0);
          await api('POST', `/api/consultas/${item.id}/procedimentos`, { nome, valorPadrao: valor, valorCobrado: valor });
        }
      }
    ]
  },
  {
    key: 'exames',
    title: 'Exames',
    path: '/api/exames',
    fields: [
      { name: 'nome', label: 'Nome', mask: 'alfanumerico' },
      { name: 'tipo', label: 'Tipo', mask: 'alfanumerico' },
      { name: 'consulta.id', label: 'ID da consulta de origem', type: 'number' },
      { name: 'dataSolicitacao', label: 'Data de solicitação', mask: 'data' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'tipo', label: 'Tipo' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'situacao', label: 'Situação' }
    ],
    actions: [
      {
        label: 'Registrar resultado',
        /* Só aparece enquanto o exame não tem resultado (situação CONCLUIDO). */
        when: item => !item.resultado,
        run: async item => {
          const data = prompt('Data do resultado:', new Date().toLocaleDateString('pt-BR'));
          if (data === null) return;
          const descricao = prompt('Descrição:') || '';
          const conclusao = prompt('Conclusão:') || '';
          await api('POST', `/api/exames/${item.id}/resultado`, { data, descricao, conclusao });
        }
      }
    ]
  },
  {
    key: 'resultados-exame',
    title: 'Resultados de Exame',
    path: '/api/resultados-exame',
    fields: [
      { name: 'data', label: 'Data', mask: 'data' },
      { name: 'descricao', label: 'Descrição', mask: 'texto' },
      { name: 'conclusao', label: 'Conclusão', mask: 'texto' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'data', label: 'Data' },
      { path: 'exame.id', label: 'Exame', format: (v, item) => referencia(v, getPath(item, 'exame.nome')) },
      { path: 'exame.consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'descricao', label: 'Descrição' },
      { path: 'conclusao', label: 'Conclusão' }
    ]
  },
  {
    key: 'prescricoes',
    title: 'Prescrições',
    path: '/api/prescricoes',
    fields: [{ name: 'data', label: 'Data', mask: 'data' }],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'data', label: 'Data' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'medicamentos', label: 'Medicamentos', format: v => (v || []).map(m => m.nome).join(', ') || '-' }
    ],
    actions: [
      {
        label: 'Add medicamento',
        run: async item => {
          const nome = prompt('Nome do medicamento:');
          if (!nome) return;
          const dose = prompt('Dose:') || '';
          const frequencia = prompt('Frequência:') || '';
          const duracao = prompt('Duração:') || '';
          await api('POST', `/api/prescricoes/${item.id}/medicamentos`, { nome, dose, frequencia, duracao });
        }
      }
    ]
  },
  {
    key: 'medicamentos',
    title: 'Medicamentos',
    path: '/api/medicamentos',
    fields: [
      { name: 'nome', label: 'Nome', mask: 'alfanumerico' },
      { name: 'dose', label: 'Dose', mask: 'alfanumerico' },
      { name: 'frequencia', label: 'Frequência', mask: 'alfanumerico' },
      { name: 'duracao', label: 'Duração', mask: 'alfanumerico' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'dose', label: 'Dose' },
      { path: 'frequencia', label: 'Frequência' },
      { path: 'duracao', label: 'Duração' }
    ]
  },
  {
    key: 'pagamentos',
    title: 'Pagamentos',
    path: '/api/pagamentos',
    fields: [
      { name: 'consulta.id', label: 'ID da consulta', type: 'number' },
      { name: 'data', label: 'Data', mask: 'data' },
      { name: 'valor', label: 'Valor', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' },
      { name: 'formaPagamento', label: 'Forma de pagamento', mask: 'alfanumerico' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'consulta.id', label: 'Consulta', format: (v, item) => referencia(v, getPath(item, 'consulta.data')) },
      { path: 'consulta.paciente.nome', label: 'Paciente', format: v => v || '—' },
      { path: 'data', label: 'Data' },
      { path: 'valor', label: 'Valor' },
      { path: 'formaPagamento', label: 'Forma' },
      { path: 'situacao', label: 'Situação' }
    ],
    actions: [
      { label: 'Pagar', when: item => item.situacao !== 'PAGO', run: async item => api('POST', `/api/pagamentos/${item.id}/pagar`) },
      { label: 'Estornar', when: item => item.situacao !== 'ESTORNADO', run: async item => api('POST', `/api/pagamentos/${item.id}/estornar`) }
    ]
  },
  {
    key: 'procedimentos',
    title: 'Procedimentos',
    path: '/api/procedimentos',
    fields: [
      { name: 'nome', label: 'Nome', mask: 'alfanumerico' },
      { name: 'valorPadrao', label: 'Valor padrão', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' },
      { name: 'valorCobrado', label: 'Valor cobrado', type: 'number', mask: 'dinheiro', valueType: 'dinheiro' }
    ],
    columns: [
      { path: 'id', label: 'ID' },
      { path: 'nome', label: 'Nome' },
      { path: 'valorPadrao', label: 'Valor padrão' },
      { path: 'valorCobrado', label: 'Valor cobrado' }
    ],
    actions: [
      { label: 'Realizar', run: async item => api('POST', `/api/procedimentos/${item.id}/realizar`) }
    ]
  }
];
