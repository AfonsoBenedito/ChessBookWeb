# ChessBook WEB
A aplicação web, ChessBook Web, é a 2ª iteração do projeto da cadeira de Construção de Sistemas de *Software*.
Construida com base em Java, o *software* acrescenta uma interface gráfica ao projeto entregue na 1ª iteração.\
Optamos por escolher fazer os *servlets* nas páginas `.jsp`, como tal decidimos adicionar mais métodos aos nossos ficheiros `.java`. \
Para além da interface visual, foram também acrescentadas as seguintes funcionalidades:
* Interatividade com as peças e o tabuleiro
* Jogabilidade com respostas no momento
* Interface administrativa de gestão de jogos
* Possibilidade de rever as suas jogadas
* Possibilidade de escolher a cor da peça com que deseja jogar
* Cronômetros com a duração de jogadas, tempo total e tempo gasto por cada jogador

### Páginas
* Registo/Login `(/Registo)`
* Game List `(/GameList)`
* Game `(/Game)`
* ManageDB `(/ManageDB)`
* Erro `(/Erro)`

Para se aceder ao Game List é necessário ter o login feito. O Game necessita de vir acompanhado por um id, e.g., */Game?Id=0*, não sendo necessário tratar-se de um jogo do mesmo.

## Authors

- [Afonso Benedito | 54937](https://git.alunos.di.fc.ul.pt/fc54937)
- [Afonso Telles | 54945](https://git.alunos.di.fc.ul.pt/fc54945)
- [Tomás Ndlate | 54970](https://git.alunos.di.fc.ul.pt/fc54970)

## Login/Registo
Para aceder ao jogo, primeiro tem que se passar pela autenticação.\
Para tal, encontramos na parte direita da página a secção de Registo, onde após o realizar poderá também encontrar o *Login*.\
Este endereço utiliza o `DataMapper` do Jogador para aceder aos registos da base de dados e fazendo assim a autenticação.\
A parte esquerda da página reserva-se a uma pequena secção com curiosidades sobre o Xadrez.
É através de esta página que será redirecionado para a sua sessão.

## Game List
Nesta página encontramos a central de jogos.
- Na Parte Esquerda, encontramos os jogos em andamento, devidamente identificados com o seu *id*, número de jogadas já realizadas, adversário e a opção de Jogar.
    -  Caso deseje, pode selecionar a opção de utilizar o modo interativo. Este, ativa o modo `Ajax`, que permite ao jogador ter as jogadas a acontecerem em tempo real assim como as casas para onde a peça se pode deslocar, melhorando a experiência e a jogabilidade.
* Ao Centro, encontramos principais sugestões de adversários. Basta selecionar um - pode ver quem selecionou mais abaixo - e escolher a cor das peças com que deseja jogar (pode tambem selecionar a opção que o torna aleatório). Carregando em "Começar Jogo", será redirecionado para a página de jogo.
* Na Parte Direita, encontramos o histórico de jogos onde podemos ver o *id*, número de jogadas totais, o adversário, o motivo da finalização do jogo e a opção de o rever (*Replay*)

## Game
Nesta, que é a página principal do projeto, temos acesso ao jogo, onde o jogador terá o tabuleiro virado conforme a cor das suas peças.
- Na Parte Esquerda, encontramos em cima o painel informativo referente ao jogador das peças pretas; em baixo o painel do jogador das peças brancas.
    - Podemos ver as peças capturadas por cada um dos jogadores, assim como o tempo total gasto por si

- No centro, encontramos o tabuleiro de xadrez, atualizado com a jogada mais recente, por defeito. Neste podemos efetuar jogadas `selecionando a peça que deseja movimentar`, seguida da seleção da casa para onde pretende ir. Através do recurso a `Ajax`, podemos ver que são sugeridas as jogadas possiveis para essa peça. Caso queira desselecionar a peça, basta carregar novamente nela. Caso esteja jogue um peão para posição de **promoção**, um *pop-up* irá aparecer para que decida qual peça escolher. 

- Na Parte Direita, encontramos a Área Pessoal do jogador.
    - Caso seja a sua vez de jogar, pode ver que o `cronômetro` terá iniciado na primeira vez que abriu a página, após o seu adversário ter feito a sua jogada.
    - Tem a opção de rodar o tabuleiro caso deseje, carregando no botão
    - Pode, caso prefira, fazer a sua jogada através de texto e carregando em Introduzir Jogada.
    - Tem ainda a opção de desistir do jogo, ou fazer um pedido de empate (que terá que ser aceite pelo adversário).
    - Por fim, tem a opção de rever jogadas carregado nos botões com as setas representativas, assim como um botão que retorna à jogada mais recente

## Manage DB
Nesta página encontramos o *dashboard* do *admin*, onde o mesmo poderá gerir as Partidas e os Jogadores.
- Em `Partidas` temos acesso a uma tabela que mostra todos os jogos, tanto `A Decorrer` como `Terminado`'s. Conseguimos ver que Jogadores estão envolvidos. Podemos ver o vencedor (caso tenha terminado). Pode também expandir o jogo para ver as jogadas.
- Em `Jogadores` temos acesso a uma tabela que mostra todos os jogadores inscritos. Podemos ver quantos jogos têm, quantas vitórias, empates e derrotas têm. É possivel também remover jogadores individualmente.


## Exemplos
#### Opte por entrar com o nome de utilizador `tl` e o e-mail `tl@fcul`
Para testar o maior número de funcionalidades disponiveis, observe os seguintes jogos e faça o indicado:
Estes jogos demonstrarão as seguintes situações:
- Jogo `ganho` por Player 1 (`tl`) (ID: #52)
- Jogo `perdido` por Player 1 (`tl`) (ID: #53)
- Jogo a `1 CheckMate de distância` (ID: #55)
    - Fazer jogada **h5 f7**
- Jogo `Empatado` (ID: #56)
- Jogo com `empate por decidir durante jogada` (ID: #57)
- Jogo com `empate por decidir` (ID: #58)
    - Poderá decidir se **aceita** ou **não** o empate
- Jogo a `1 move de promoção` (ID: #59)
    - Fazer jogada **f7 f8**
- Jogo do `início` (ID: #60)
- Jogo `à espera de jogada` (ID: #61)
- Jogo com `Enpassant` (ID: #62)
    - Fazer jogada **e5 d6**
- Jogo com `Castle Short` (ID: #63)
    -   Fazer jogada **e1 g1**
- Jogo com `Castle Long` (ID: #64)
    - Fazer jogada **e1 c1**

  
### Deploy

Para fazer *deploy* deste projeto, é necessario aceder ao Eclipse (ou à sua IDE de eleição) e correr o programa em `Run As: -> Maven Build`
Pode também utilizar o terminal:

```bash
  mvn clean
  mvn package
```

Após ser extraido o ficheiro `.war`, o mesmo deve ser colocado no servidor (TomCat). Ao ser dado *deploy*, o mesmo fica disponível para ser acedido.

<details>
<summary>Caso deseje, para facilitar a tarefa de *deploy* pode criar um *alias* que tratará do acima referido por si:</summary>

```bash
alias deploy='mvn package; cp target/*.war /var/lib/tomcat9/webapps/'
```
</details>

### Docker Compose

Para correr a aplicação utilizando Docker Compose, certifique-se que tem o Docker instalado e corra o seguinte comando na raiz do projeto:

```bash
docker compose up
```

Para correr em *background* (detached mode):

```bash
docker compose up -d
```

> **Nota:** A aplicação corre na porta `8080`. Se encontrar um erro `Bind for 0.0.0.0:8080 failed: port is already allocated`, significa que a porta já está a ser utilizada por outro serviço. Pare o serviço conflituoso ou altere a porta no ficheiro `docker-compose.yml`.

### Run JUnits

Para rodar os testes, basta no Eclipse (ou à sua IDE de eleição) selecionar o diretório `'test/'` e `Run As: -> JUnit Test`

```bash
├───src
│   ├───main
│   └───test
│       └───java
│           ├───domain
│           └───persist
```

### Used Stack

**Front-end:** HTML, CSS, JavaScript, Java

**Back-end:** Java, MySQL
