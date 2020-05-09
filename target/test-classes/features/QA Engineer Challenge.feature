#language: pt
@pesquisa
Funcionalidade: Pesquisa de cursos por professor

  Cenario: Validacao resultados dos cursos professora "Ena Loiola"
    Dado que eu esteja na homepage da Estrategia Concursos
    Quando eu utilizar a busca 'Por professor'
    E acessar os cursos da professora 'Ena Loiola'
    Entao eu valido se o valor do curso na pagina de listagem e igual ao valor na pagina de detalhes
    E verifico que o total do valor parcelado do curso corresponde ao valor total
