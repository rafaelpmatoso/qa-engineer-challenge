#language: pt
@BuscaCursos
Funcionalidade: Busca de cursos, filtragem de cursos e exibicao de detalhe dos cursos na homepage

  Contexto: Acessar a pesquisa do cursos da professora Ena Loiola
    Dado que eu esteja na homepage da Estrategia Concursos

  @BuscaPorProfessor
  Cenario: Validar detalhes dos cursos encontrados Por Professsor
    Quando eu utilizar a busca 'Por professor'
    E acessar os cursos da professora 'Ena Loiola'
    E listar os cursos exibidos
    Entao eu valido se o valor do curso na pagina de listagem e igual ao valor na pagina de detalhes
    E verifico que o total do valor parcelado do curso corresponde ao valor total
    E que a quantidade de cursos exibidos na pagina de listagem e igual a quantidade de cursos na pagina de detalhes

  @FiltroResultadoBusca
  Cenario: Validar filtros do resultado de busca
    Quando eu realizar a pesquisa na barra de busca
      | pesquisa        |
      | Policia Militar |
    E selecionar o filtro 'Assinaturas'
    Entao os resultados serao exibidos de acordo com o filtro selecionado

  @OrdenacaoPorValor
  Cenario: Validar ordenacao dos valores totais dos cursos
    Quando eu realizar a pesquisa na barra de busca
      | pesquisa |
      | OAB      |
    E selecionar o filtro 'Cursos'
    E ordernar os cursos em order crescente de valor
