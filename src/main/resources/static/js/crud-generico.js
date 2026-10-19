/* =========================================================================
   Tela de API (crud-generico.html): motor generico de abas, tabela e
   formulario. A descricao das entidades fica em js/recursos-crud.js.
   ========================================================================= */

/* ---------- Painel de saida ---------- */

function showOutput(title, status, data) {
  const box = document.getElementById('output');
  document.getElementById('output-title').textContent = `${title} · HTTP ${status}`;
  document.getElementById('output-body').textContent = JSON.stringify(data, null, 2);
  box.classList.add('show');
}

/* ---------- Comunicacao com a API ---------- */

async function api(method, url, body) {
  const { response, data } = await requisicao(method, url, body);
  showOutput(`${method} ${url}`, response.status, data);
  if (!response.ok) throw new Error(`${response.status} ${response.statusText}`);
  return data;
}


/* ---------- construção da interface ---------- */

function buildBody(resource, form) {
  const body = {};
  for (const field of resource.fields) {
    let value = form.elements[field.name].value.trim();
    if (value === '') continue;
    if (field.valueType === 'dinheiro') {
      const numero = Mask.valorNumerico(value);
      value = isNaN(numero) ? 0 : numero;
    } else if (field.type === 'number') {
      value = Number(value);
    }
    setPath(body, field.name, value);
  }
  return body;
}

function renderForm(resource) {
  const inputs = resource.fields.map(field => {
    const money = field.valueType === 'dinheiro';
    const type = money ? 'text' : (field.type === 'number' ? 'number' : 'text');
    return `
    <div>
      <label for="${resource.key}-${field.name}">${escapeHtml(field.label)}</label>
      <input id="${resource.key}-${field.name}" name="${field.name}"
             type="${type}" ${Mask.atributos(field)}
             ${field.type === 'number' && !money ? 'step="any"' : ''}>
    </div>`;
  }).join('');

  return `
    <div class="card">
      <h2>Novo registro</h2>
      <form id="form-${resource.key}">
        <div class="grid">${inputs}</div>
        <div class="form-actions">
          <button class="btn" type="submit">Criar</button>
        </div>
      </form>
    </div>`;
}

function renderTable(resource) {
  const headers = resource.columns.map(c => `<th>${escapeHtml(c.label)}</th>`).join('');
  return `
    <div class="card">
      <h2>${escapeHtml(resource.title)} <span class="hint">(${resource.path})</span></h2>
      <div id="table-${resource.key}">
        <table>
          <thead><tr>${headers}<th>Ações</th></tr></thead>
          <tbody id="tbody-${resource.key}"></tbody>
        </table>
      </div>
    </div>`;
}

function renderPanel(resource) {
  return `<section class="panel" id="panel-${resource.key}">
      ${renderForm(resource)}
      ${renderTable(resource)}
    </section>`;
}

function renderRow(resource, item) {
  const cells = resource.columns.map(column => {
    const raw = getPath(item, column.path);
    const text = column.format ? column.format(raw, item) : raw;
    return `<td>${escapeHtml(text)}</td>`;
  }).join('');

  const actionButtons = (resource.actions || [])
    .map((action, index) => ({ action, index }))
    .filter(({ action }) => !action.when || action.when(item))
    .map(({ action, index }) => `<button type="button" data-action="${index}">${escapeHtml(action.label)}</button>`)
    .join('');

  return `<tr data-id="${item.id}">
    ${cells}
    <td>
      <div class="row-actions">
        ${actionButtons}
        <button type="button" class="danger" data-action="delete">Excluir</button>
      </div>
    </td>
  </tr>`;
}

async function loadResource(resource) {
  const tbody = document.getElementById(`tbody-${resource.key}`);
  try {
    const items = await api('GET', resource.path);
    if (!items || items.length === 0) {
      tbody.innerHTML = `<tr><td colspan="${resource.columns.length + 1}" class="empty">Nenhum registro. Use o formulário acima para criar.</td></tr>`;
      return;
    }
    tbody.innerHTML = items.map(item => renderRow(resource, item)).join('');
    tbody.querySelectorAll('tr[data-id]').forEach(row => {
      const id = Number(row.dataset.id);
      const item = items.find(i => i.id === id);
      row.querySelectorAll('button[data-action]').forEach(button => {
        button.addEventListener('click', async () => {
          const action = button.dataset.action;
          try {
            if (action === 'delete') {
              if (!confirm(`Excluir registro ${id}?`)) return;
              await api('DELETE', `${resource.path}/${id}`);
            } else {
              await resource.actions[Number(action)].run(item, resource);
            }
            await loadResource(resource);
          } catch (error) {
            alert('Erro: ' + error.message);
          }
        });
      });
    });
  } catch (error) {
    tbody.innerHTML = `<tr><td colspan="${resource.columns.length + 1}" class="empty">Erro ao carregar: ${escapeHtml(error.message)}</td></tr>`;
  }
}

function setupResource(resource) {
  const form = document.getElementById(`form-${resource.key}`);
  form.addEventListener('submit', async event => {
    event.preventDefault();
    try {
      await api('POST', resource.path, buildBody(resource, form));
      form.reset();
      await loadResource(resource);
    } catch (error) {
      alert('Erro: ' + error.message);
    }
  });
}

function init() {
  const tabs = document.getElementById('tabs');
  const main = document.getElementById('main');

  main.innerHTML = resources.map(renderPanel).join('');

  resources.forEach((resource, index) => {
    const button = document.createElement('button');
    button.textContent = resource.title;
    if (index === 0) button.classList.add('active');
    button.addEventListener('click', () => activate(resource.key));
    tabs.appendChild(button);

    setupResource(resource);
  });

  activate(resources[0].key);
}

function activate(key) {
  document.querySelectorAll('.tabs button').forEach(button => {
    button.classList.toggle('active', button.textContent === resources.find(r => r.key === key).title);
  });
  document.querySelectorAll('.panel').forEach(panel => {
    panel.classList.toggle('active', panel.id === `panel-${key}`);
  });
  loadResource(resources.find(r => r.key === key));
}

init();
