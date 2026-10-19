/* =========================================================================
   Cliente HTTP compartilhado.
   Faz a requisicao e devolve status + corpo ja interpretado; cada tela decide
   como mostrar o resultado (toast na tela principal, painel de saida na tela
   de API).
   ========================================================================= */

async function requisicao(method, url, body) {
  const options = { method, headers: {} };
  if (body !== undefined) {
    options.headers['Content-Type'] = 'application/json';
    options.body = JSON.stringify(body);
  }
  const response = await fetch(url, options);
  const texto = await response.text();
  let data = null;
  try { data = texto ? JSON.parse(texto) : null; } catch (e) { data = texto; }
  return { response, data };
}

/** Extrai a mensagem de erro (o ProblemDetail do Spring usa "title"/"detail"). */
function mensagemDeErro(data, response) {
  return (data && (data.message || data.detail || data.title)) || `${response.status} ${response.statusText}`;
}
