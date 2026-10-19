/*!
 * masks.js - Mascaras de digitacao padrao do sistema Clinica Medica
 * -----------------------------------------------------------------
 * Biblioteca propria, sem dependencia externa, usada pelas telas
 * (index.html e crud-generico.html).
 *
 * Como usar (declarativo, funciona tambem em HTML criado dinamicamente):
 *   <input data-mask="cpf">        -> 000.000.000-00
 *   <input data-mask="cnpj">       -> 00.000.000/0000-00
 *   <input data-mask="data">       -> dd/mm/aaaa (com validacao de calendario)
 *   <input data-mask="hora">       -> hh:mm      (com validacao de faixa)
 *   <input data-mask="telefone">   -> 99999-0000 ou (11) 91234-5678
 *   <input data-mask="dinheiro">   -> 1.234,56
 *   <input data-mask="letras">     -> somente letras
 *   <input data-mask="alfanumerico">, data-mask="texto", data-mask="crm",
 *   data-mask="carteirinha", data-mask="numero"
 *
 *   <input data-pattern="000-000"> -> template livre (0=digito, A=letra, X=letra/digito)
 *
 * Aplicacao automatica: um MutationObserver observa o DOM, portanto qualquer
 * input que entre na pagina (inclusive modais montados por innerHTML) recebe
 * a mascara sem precisar de chamada manual.
 *
 * API publica em window.Mascaras:
 *   Mascaras.atributos({ mask: 'cpf' })  -> 'data-mask="cpf"' (para os templates)
 *   Mascaras.aplicar(input)              -> aplica a mascara em um elemento
 *   Mascaras.aplicarTudo(raiz)           -> varre um sub-arvore
 *   Mascaras.valorNumerico('1.234,56')   -> 1234.56
 *   Mascaras.formatarMoeda(1234.56)      -> '1.234,56'
 */
(function (global) {
  'use strict';

  /* ----------------------------------------------------------------------
     Classes de caracteres
     ---------------------------------------------------------------------- */

  var SIG = /[0-9A-Za-z\u00C0-\u00FF]/;        // "significante" para o posicionamento do cursor
  var RE_DIGITO = /[0-9]/;
  var RE_LETRA = /[A-Za-z\u00C0-\u00FF]/;
  var RE_ALNUM = /[A-Za-z0-9\u00C0-\u00FF]/;

  /* Regras de "somente" (filtro sem template) */
  var SOMENTE = {
    letras: {
      permitido: /[A-Za-z\u00C0-\u00FF' .-]/,
      title: 'Este campo aceita apenas letras.'
    },
    alfanumerico: {
      permitido: /[A-Za-z0-9\u00C0-\u00FF .,/\u00BA\u00B0\u00AA%()+-]/,
      title: 'Use apenas letras, numeros e separadores simples.'
    },
    codigo: {
      permitido: /[A-Za-z0-9\u00C0-\u00FF ./:-]/,
      title: 'Use apenas letras, numeros, ponto, barra, dois-pontos e hifen.'
    },
    numero: {
      permitido: /[0-9]/,
      title: 'Use apenas numeros.'
    },
    texto: {
      permitido: /[\p{L}\p{N}\s\p{P}]/u,
      title: 'Campo de texto livre.'
    }
  };

  /* ----------------------------------------------------------------------
     Mascaras prontas
     pattern  -> template (0 = digito, A = letra, X = letra ou digito)
     permitido -> filtro simples (usado por SOMENTE.*)
     formatar -> formatador especial (telefone)
     dinheiro -> formatacao monetaria pt-BR
     validar  -> funcao extra (data/hora) chamada quando o campo esta completo
     ---------------------------------------------------------------------- */

  var MASCARAS = {
    cpf: {
      pattern: '000.000.000-00',
      placeholder: '000.000.000-00',
      patternHtml: '\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}',
      title: 'CPF no formato 000.000.000-00',
      numerico: true,
      compativel: function (v) { return apenasDigitos(v).length === 11; }
    },
    cnpj: {
      pattern: '00.000.000/0000-00',
      placeholder: '00.000.000/0000-00',
      patternHtml: '\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}',
      title: 'CNPJ no formato 00.000.000/0000-00',
      numerico: true,
      compativel: function (v) { return apenasDigitos(v).length === 14; }
    },
    data: {
      pattern: '00/00/0000',
      placeholder: 'dd/mm/aaaa',
      patternHtml: '\\d{2}/\\d{2}/\\d{4}',
      title: 'Data no formato dd/mm/aaaa',
      numerico: true,
      validar: validarData,
      // aceita o que ja esta no padrao (ou ainda incompleto) e converte ISO (aaaa-mm-dd)
      compativel: function (v) { return /^(\d{1,8}|\d{2}\/\d{2}\/\d{4}|\d{4}-\d{2}-\d{2})$/.test(v); },
      converter: function (v) {
        var iso = /^(\d{4})-(\d{2})-(\d{2})$/.exec(v);
        return iso ? iso[3] + '/' + iso[2] + '/' + iso[1] : v;
      }
    },
    hora: {
      pattern: '00:00',
      placeholder: 'hh:mm',
      patternHtml: '([01]\\d|2[0-3]):[0-5]\\d',
      title: 'Horario no formato hh:mm',
      numerico: true,
      validar: validarHora,
      compativel: function (v) { return /^(\d{1,4}|\d{2}:\d{2})$/.test(v); }
    },
    telefone: {
      formatar: formatarTelefone,
      placeholder: '(00) 00000-0000',
      patternHtml: '(\\d{4,5}-\\d{4}|\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4})',
      title: 'Telefone: 99999-0000 (fixo/celular) ou (11) 91234-5678 (com DDD)',
      numerico: true,
      compativel: function (v) { var d = apenasDigitos(v).length; return d >= 8 && d <= 11; }
    },
    dinheiro: {
      dinheiro: true,
      placeholder: '0,00',
      patternHtml: '\\d{1,3}(\\.\\d{3})*(,\\d{1,2})?',
      title: 'Valor em reais, ex.: 1.234,56',
      numerico: true
    },
    letras: { somente: 'letras', placeholder: 'Só letras' },
    alfanumerico: { somente: 'alfanumerico' },
    texto: { somente: 'texto' },
    numero: { somente: 'numero', patternHtml: '\\d+', numerico: true },
    crm: { somente: 'codigo', upper: true, placeholder: 'CRM-12345', title: 'CRM: letras, numeros e hifen.' },
    carteirinha: { somente: 'codigo', upper: true, placeholder: 'CARD-0001', title: 'Numero da carteirinha: letras, numeros e hifen.' }
  };

  var SELETOR = '[data-mask], [data-only], [data-pattern]';

  /* ----------------------------------------------------------------------
     Utilidades
     ---------------------------------------------------------------------- */

  function apenasDigitos(texto) {
    return String(texto == null ? '' : texto).replace(/\D+/g, '');
  }

  function classeDoToken(token) {
    if (token === '0') return RE_DIGITO;
    if (token === 'A') return RE_LETRA;
    if (token === 'X') return RE_ALNUM;
    return null;
  }

  /** Mantem, na ordem digitada, apenas os caracteres aceitos pelo template. */
  function significantes(texto, pattern) {
    var classes = [];
    var i;
    for (i = 0; i < pattern.length; i++) {
      var classe = classeDoToken(pattern.charAt(i));
      if (classe) classes.push(classe);
    }
    var aceitos = [];
    for (i = 0; i < texto.length; i++) {
      var ch = texto.charAt(i);
      for (var c = 0; c < classes.length; c++) {
        if (classes[c].test(ch)) { aceitos.push(ch); break; }
      }
    }
    return aceitos;
  }

  /** Insere os separadores do template conforme o usuario digita. */
  function montar(chars, pattern) {
    var out = '';
    var i = 0;
    for (var p = 0; p < pattern.length; p++) {
      if (i >= chars.length) break;
      var token = pattern.charAt(p);
      if (classeDoToken(token)) out += chars[i++];
      else out += token;
    }
    return out;
  }

  /** Telefone: aceita sem DDD (99999-0000) ou com DDD ((11) 91234-5678). */
  function formatarTelefone(digitos) {
    var d = digitos.slice(0, 11);
    if (d.length <= 4) return d;
    if (d.length <= 8) return d.slice(0, 4) + '-' + d.slice(4);
    if (d.length <= 9) return d.slice(0, 5) + '-' + d.slice(5);
    if (d.length <= 10) return '(' + d.slice(0, 2) + ') ' + d.slice(2, 6) + '-' + d.slice(6);
    return '(' + d.slice(0, 2) + ') ' + d.slice(2, 7) + '-' + d.slice(7);
  }

  /** Dinheiro pt-BR: separador de milhar "." e decimal "," (maximo 2 casas). */
  function formatarDinheiro(texto) {
    var temVirgula = String(texto).indexOf(',') !== -1;
    var limpo = String(texto).replace(/\./g, '').replace(/[^\d,]/g, '');
    var partes = limpo.split(',');
    var inteiro = (partes.shift() || '').replace(/\D+/g, '');
    var decimal = partes.join('').replace(/\D+/g, '').slice(0, 2);
    var inteiroFmt = inteiro.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    return (temVirgula || decimal !== '') ? inteiroFmt + ',' + decimal : inteiroFmt;
  }

  /** Converte um valor formatado ("1.234,56") em numero (1234.56). */
  function valorNumerico(texto) {
    if (texto == null) return NaN;
    var limpo = String(texto).replace(/[^\d,.-]/g, '');
    if (limpo === '' || limpo === '-') return NaN;
    if (limpo.indexOf(',') !== -1) limpo = limpo.replace(/\./g, '').replace(',', '.');
    var numero = Number(limpo);
    return isNaN(numero) ? NaN : numero;
  }

  /** Converte um numero em texto pt-BR ("1234.56" -> "1.234,56"). */
  function formatarMoeda(valor) {
    if (valor === null || valor === undefined || valor === '') return '';
    var numero = Number(valor);
    if (!isFinite(numero)) return '';
    return numero.toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  }

  function validarData(texto) {
    var m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(texto);
    if (!m) return 'Data incompleta. Use dd/mm/aaaa.';
    var dia = Number(m[1]), mes = Number(m[2]), ano = Number(m[3]);
    if (mes < 1 || mes > 12) return 'Mês inválido.';
    var ultimoDia = new Date(ano, mes, 0).getDate();
    if (dia < 1 || dia > ultimoDia) return 'Dia inválido para o mês informado.';
    if (ano < 1900 || ano > 2200) return 'Ano fora do intervalo esperado (1900 a 2200).';
    return null;
  }

  function validarHora(texto) {
    var m = /^(\d{2}):(\d{2})$/.exec(texto);
    if (!m) return 'Horário incompleto. Use hh:mm.';
    if (Number(m[1]) > 23) return 'Hora inválida (00 a 23).';
    if (Number(m[2]) > 59) return 'Minutos inválidos (00 a 59).';
    return null;
  }

  function contarSignificantes(texto) {
    var total = 0;
    for (var i = 0; i < texto.length; i++) {
      if (SIG.test(texto.charAt(i))) total++;
    }
    return total;
  }

  function posicaoDoCaret(texto, quantidade) {
    var vistos = 0;
    for (var i = 0; i < texto.length; i++) {
      if (SIG.test(texto.charAt(i))) {
        vistos++;
        if (vistos >= quantidade) return i + 1;
      }
    }
    return texto.length;
  }

  /* ----------------------------------------------------------------------
     Motor
     ---------------------------------------------------------------------- */

  /** Monta a definicao final (com filtros/formatadores) a partir do atributo. */
  function normalizar(def) {
    if (!def) return null;
    if (def.somente) {
      var base = SOMENTE[def.somente] || SOMENTE.texto;
      return {
        permitido: base.permitido,
        title: def.title || base.title,
        placeholder: def.placeholder,
        upper: def.upper,
        pattern: def.pattern,
        patternHtml: def.patternHtml,
        validar: def.validar,
        numerico: def.numerico
      };
    }
    return def;
  }

  function formatarValor(texto, def) {
    var out;
    if (def.permitido) {
      var aceitos = [];
      for (var i = 0; i < texto.length; i++) {
        var ch = texto.charAt(i);
        if (def.permitido.test(ch)) aceitos.push(ch);
      }
      out = aceitos.join('');
    } else if (def.dinheiro) {
      out = formatarDinheiro(texto);
    } else if (def.formatar) {
      out = def.formatar(apenasDigitos(texto));
    } else if (def.pattern) {
      out = montar(significantes(texto, def.pattern), def.pattern);
    } else {
      out = texto;
    }
    return def.upper ? out.toUpperCase() : out;
  }

  function validarCampo(input, def) {
    if (!def.validar) return;
    var valor = input.value;
    var mensagem = '';
    if (valor !== '' && (!def.pattern || valor.length === def.pattern.length)) {
      mensagem = def.validar(valor) || '';
    }
    input.setCustomValidity(mensagem);
  }

  function tratar(input, def) {
    var atual = input.value;
    var caret = typeof input.selectionStart === 'number' ? input.selectionStart : atual.length;
    var noFim = caret >= atual.length;
    var novo = formatarValor(atual, def);
    if (novo !== atual) {
      input.value = novo;
      var pos = noFim
        ? novo.length
        : posicaoDoCaret(novo, contarSignificantes(atual.slice(0, caret)));
      try { input.setSelectionRange(pos, pos); } catch (e) { /* tipo sem selecao */ }
    }
    validarCampo(input, def);
  }

  function prepararAtributos(input, def) {
    var placeholder = def.placeholder || def.pattern;
    if (placeholder && !input.getAttribute('placeholder')) input.setAttribute('placeholder', placeholder);
    if (def.title && !input.getAttribute('title')) input.setAttribute('title', def.title);
    if (def.patternHtml && !input.getAttribute('pattern')) input.setAttribute('pattern', def.patternHtml);
    if (def.pattern && !input.getAttribute('maxlength')) input.setAttribute('maxlength', def.pattern.length);
    if (def.numerico) input.setAttribute('inputmode', 'numeric');
  }

  function definir(input) {
    var nome = input.getAttribute('data-mask');
    if (nome) {
      if (MASCARAS[nome]) return MASCARAS[nome];
      // template livre: data-mask="000-000"
      if (/^[0AX0-9./:() \-+]+$/.test(nome)) return { pattern: nome };
      return null;
    }
    var pattern = input.getAttribute('data-pattern');
    if (pattern) return { pattern: pattern };
    var only = input.getAttribute('data-only');
    if (only) return { somente: only };
    return null;
  }

  /** Aplica a mascara em um elemento (idempotente). */
  function aplicar(input) {
    if (!input || typeof input.getAttribute !== 'function') return;
    if (input.getAttribute('data-mask-aplicada') === '1') return;
    var def = normalizar(definir(input));
    if (!def) return;
    input.setAttribute('data-mask-aplicada', '1');
    prepararAtributos(input, def);
    input.addEventListener('input', function () { tratar(input, def); });
    input.addEventListener('blur', function () { tratar(input, def); });

    // Normalizacao inicial (modo edicao). Valores que nao seguem o padrao
    // conhecido sao preservados para nao corromper dados antigos.
    if (typeof def.converter === 'function') input.value = def.converter(input.value);
    if (input.value !== '' && typeof def.compativel === 'function' && !def.compativel(input.value)) return;
    tratar(input, def);
  }

  /** Varre a raiz (inclusive) em busca de campos com mascara. */
  function aplicarTudo(raiz) {
    raiz = raiz || global.document;
    if (!raiz) return;
    if (raiz.nodeType === 1 && raiz.matches && raiz.matches(SELETOR)) aplicar(raiz);
    if (raiz.querySelectorAll) {
      var lista = raiz.querySelectorAll(SELETOR);
      for (var i = 0; i < lista.length; i++) aplicar(lista[i]);
    }
  }

  function iniciar() {
    aplicarTudo(global.document);
    if (typeof global.MutationObserver !== 'function') return;
    var observer = new global.MutationObserver(function (mutations) {
      for (var i = 0; i < mutations.length; i++) {
        var adicionados = mutations[i].addedNodes;
        for (var j = 0; j < adicionados.length; j++) {
          if (adicionados[j].nodeType === 1) aplicarTudo(adicionados[j]);
        }
      }
    });
    observer.observe(global.document.body || global.document.documentElement, { childList: true, subtree: true });
  }

  /* ----------------------------------------------------------------------
     API publica
     ---------------------------------------------------------------------- */

  global.Mascaras = {
    MASCARAS: MASCARAS,
    aplicar: aplicar,
    aplicarTudo: aplicarTudo,
    valorNumerico: valorNumerico,
    formatarMoeda: formatarMoeda,
    formatarDinheiro: formatarDinheiro,
    /** Retorna os atributos que ativam a mascara em um campo de formulario. */
    atributos: function (campo) {
      if (!campo || !campo.mask) return '';
      return MASCARAS[campo.mask]
        ? 'data-mask="' + campo.mask + '"'
        : 'data-pattern="' + campo.mask + '"';
    }
  };

  if (global.document) {
    if (global.document.readyState === 'loading') {
      global.document.addEventListener('DOMContentLoaded', iniciar);
    } else {
      iniciar();
    }
  }
})(window);
