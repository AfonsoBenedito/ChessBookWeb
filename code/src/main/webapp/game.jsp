<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import = "persist.ChessPlayerDM, domain.ChessPlayer, java.util.Optional, persist.ChessGameDM, domain.ChessGame, domain.ChessBoard, java.util.List, domain.ChessMove, java.util.Date, domain.IllegalMoveException, main.ChessMain  " %>

<%
	String logged = (String)request.getSession(false).getAttribute("logged");
	String name = (String)request.getSession(false).getAttribute("name");
	String email = (String)request.getSession(false).getAttribute("email");
	String gameIdGet = request.getParameter("Id");
	String respMoveNumber = request.getParameter("MoveNumber");

	boolean interatividade = false;
	interatividade = (boolean)request.getSession(false).getAttribute("interatividade");

	if (logged != "true"){
		response.sendRedirect("Registo");
		return;
	}

	ChessGameDM cgDM = ChessGameDM.getInstance();
	ChessPlayerDM cpDM = ChessPlayerDM.getInstance();

	try {
		cgDM.findByPlayerName("teste");
	}catch(Exception e){
		response.sendRedirect("Erro");
		return;
	}

	int gameId = 0;

	if (gameIdGet != null){
		gameId = Integer.parseInt(gameIdGet);
		ChessMain.clearCache();
	} else {
		response.sendRedirect("GameList");
		return;
	}

	ChessPlayer whitePlayer = null;
	ChessPlayer blackPlayer = null;

	Optional<ChessGame> game = cgDM.find(gameId);
	ChessBoard tabuleiro = null;
	String peca = null;
	Date openDate = null;
	String dataDeAbertura = null;
	int whitePlayerTime = 0;
	int blackPlayerTime = 0;

	if (game.isPresent()){
		whitePlayer = game.get().getWhite();
		blackPlayer = game.get().getBlack();

		List<ChessMove> jogadas = game.get().getMoves();
		tabuleiro = new ChessBoard(jogadas);
		game.get().setBoard(tabuleiro);
		openDate = game.get().getOpenDate();
		if(openDate == null){
			if(game.get().getPlayerTurn().equals(name, email)){
				openDate = new Date();
				game.get().setOpenDate(openDate);
				cgDM.update(game.get());
				dataDeAbertura = String.valueOf(openDate.getTime());
			}

		} else{
			dataDeAbertura = String.valueOf(openDate.getTime());
		}

		for(int i = 0; i < jogadas.size(); i++){
			if(i % 2 == 0){
				whitePlayerTime += jogadas.get(i).getTimeMilli();
			} else{
				blackPlayerTime += jogadas.get(i).getTimeMilli();
			}
		}
	} else {
		response.sendRedirect("GameList");
		return;
	}


	String desistir = request.getParameter("desistir");
	if (desistir != null){
		if (whitePlayer.equals(name, email)){
			game.get().whiteResign();
		} else if (blackPlayer.equals(name, email)){
			game.get().blackResign();
		}

		cgDM.update(game.get());
	}

	String pedirEmpate = request.getParameter("pedirEmpate");
	if (pedirEmpate != null){
		if (whitePlayer.equals(name, email)){
			game.get().offerDraw(whitePlayer);
		} else if (blackPlayer.equals(name, email)){
			game.get().offerDraw(blackPlayer);
		}
		cgDM.update(game.get());
	}

	String aceitarEmpate = request.getParameter("aceitarEmpate");
	if (aceitarEmpate != null && game.get().getTieOffer() != null){
		if (whitePlayer.equals(name, email) || blackPlayer.equals(name, email)){
			game.get().acceptDraw();
		}
		cgDM.update(game.get());
	}

	String recusarEmpate = request.getParameter("recusarEmpate");
	if (recusarEmpate != null && game.get().getTieOffer() != null){
		if (whitePlayer.equals(name, email) || blackPlayer.equals(name, email)){
			game.get().refuseDraw();
		}
		cgDM.update(game.get());
	}


	String move = request.getParameter("move");
	String tempoMove = request.getParameter("tempoMove");
	boolean invalidMove = true;
	String outputMove = null;
	if (move != null){
		if (game.get().verifyInput(move)){
			invalidMove = false;

			Optional<ChessPlayer> thisPlayer = cpDM.findByEmail(email);
			int intTempoMove = Integer.parseInt(tempoMove);
			ChessMove moveReady = game.get().convertInputToMove(move, thisPlayer.get(), game.get(), intTempoMove);

			try {
				game.get().addMove(moveReady);
				tabuleiro = game.get().getBoard();

				cgDM.update(game.get());
				dataDeAbertura = null;
			} catch (IllegalMoveException e){
				outputMove = e.getMessage();
			}
		}
	}


	boolean replayMode = false;
	int moveIndex;
	if (respMoveNumber != null){
		moveIndex = Integer.parseInt(respMoveNumber) - 1;

		if (moveIndex < game.get().getMoves().size() - 1){
			replayMode = true;
			tabuleiro = new ChessBoard(game.get().getMoves().subList(0, moveIndex + 1));
		} else {
			response.sendRedirect("Game?Id=" +  gameId);
			return;
		}
	} else {
		moveIndex = game.get().getMoves().size() - 1;
	}
%>

<!DOCTYPE html>

<html>
<head>
	<link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
	<meta charset="UTF-8">
	<link rel="stylesheet" href="styles/geral.css">
	<link rel="stylesheet" href="styles/game.css">
	<title>Game</title>
</head>

<body>

<header>
	<h1>ChessBookWEB</h1>
	<div class="header_options">
		<a href="ManageDB">
			<li class="btn_sair" >
				<p>ManageDB</p>
			</li>
		</a>
		<a href="GameList">
			<li class="btn_sair" >
				<p>Painel</p>
			</li>
		</a>
		<a href="logout.jsp">
			<li class="btn_sair" >
				<p>Sair</p>
			</li>
		</a>
	</div>
	<div class="zona_nome">
		<h2>Olá, <span><%= name %></span></h2>
	</div>
</header>


<div id="painel_vitoria">
	<div class="blur_game"></div>
	<canvas id="Canvas"></canvas>
	<div id="fechar_pop_up_background"></div>
	<div class="bloco_resultado">
		<div class="back_blur"></div>
		<h5 id="btn_fechar_pop_up">&#215;</h5>
		<h1 class="ganhar_cor">Ganhaste a partida!</h1>
		<img src="images/info_4.png" alt="">
		<h3>Parabéns! Continua a jogar para melhorares as tuas
			habilidades. Torna-te o Rei do Xadrez!
		</h3>
		<div class="zona_btn_vitoria">
			<a class="btn_a" href="GameList">
				<button type="button" class="btn_ir_painel">Ir para painel</button>
			</a>
			<button type="button" id="btn_voltar_jogo">Voltar ao jogo</button>
		</div>
	</div>
</div>

<div id="painel_derrota">
	<div class="blur_game"></div>
	<div id="fechar_pop_up_background_derrota"></div>
	<div class="bloco_resultado">
		<div class="back_blur"></div>
		<h5 id="btn_fechar_pop_up_derrota">&#215;</h5>
		<h1 class="perder_cor">Perdeste a partida!</h1>
		<img src="images/info_3.png" alt="">
		<h3>Que pena! Continua a jogar para melhorares as tuas
			habilidades. Torna-te o Rei do Xadrez!
		</h3>
		<div class="zona_btn_vitoria">
			<a class="btn_a" href="GameList">
				<button type="button" class="btn_ir_painel">Ir para painel</button>
			</a>
			<button type="button" id="btn_voltar_jogo_derrota">Voltar ao jogo</button>
		</div>
	</div>
</div>

<div id="painel_empate">
	<div class="blur_game"></div>
	<div id="fechar_pop_up_background_empate"></div>
	<div class="bloco_resultado">
		<div class="back_blur"></div>
		<h5 id="btn_fechar_pop_up_empate">&#215;</h5>
		<h1 class="empatar_cor">Empataste a partida!</h1>
		<img src="images/info_5.png" alt="">
		<h3>Ora bolas! Continua a jogar para melhorares as tuas
			habilidades. Torna-te o Rei do Xadrez!
		</h3>
		<div class="zona_btn_vitoria">
			<a class="btn_a" href="GameList">
				<button type="button" class="btn_ir_painel">Ir para painel</button>
			</a>
			<button type="button" id="btn_voltar_jogo_empate">Voltar ao jogo</button>
		</div>
	</div>
</div>

<div class="content_superior">

	<div class="top_nome_jogadas">
		<div class="back_blur"></div>
		<div class="top_id_jogo">#<%= gameId %></div>
		<div class="top_numero_jogadas">Total de jogadas - <span><%= game.get().getMoves().size() %></span></div>
		<div class="top_data_inicio"><%= game.get().getDate() %></div>
	</div>
	<%
		if (outputMove != null){
			out.println("<div class=\"top_erros\">");
			out.println("<div class=\"back_blur\"></div>");
			out.println("<div class=\"aviso_erro\">" + outputMove + "</div>");
			out.println("</div>");
		}
	%>

</div>

<div class="content_total">
	<div class="bloco_lateral_esquerdo">
		<div class="bloco_pecas">
			<div class="back_blur"></div>
			<h3 class="id_nome"><%= blackPlayer.getName() %></h3>
			<div class="id_pecas">
				<div class="bloco_cor preto"></div>
				<h4 class="nome_cor preto">Peças pretas</h4>
			</div>
			<div class="bloco_pecas_capturadas">
				<h3 class="titulo_pecas_capturadas">Peças capturadas</h3>
				<div class="show_pecas_capturadas">
					<div class="capturadas_cima">
						<!-- Rainha -->
						<li>
							<h1 class="brancas">&#9813;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(4) %></div>
						</li>
						<!-- Torre -->
						<li>
							<h1 class="brancas">&#9814;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(1) %></div>
						</li>
						<!-- Bispo -->
						<li>
							<h1 class="brancas">&#9815;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(3) %></div>
						</li>
					</div>
					<div class="capturadas_baixo">
						<!-- Peao -->
						<li>
							<h1 class="brancas"> &#9817;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(0) %></div>
						</li>
						<!-- Cavalo -->
						<li>
							<h1 class="brancas">&#9816;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(2) %></div>
						</li>
					</div>
				</div>
			</div>
			<div class="zona_timer">
				<div class="bloco_timer">
					<div class="timer_dia">
						<div class="timer_number" id="blackDia0">0</div>
						<div class="timer_number" id="blackDia1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_hora">
						<div class="timer_number" id="blackHora0">0</div>
						<div class="timer_number" id="blackHora1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_min">
						<div class="timer_number" id="blackMinuto0">0</div>
						<div class="timer_number" id="blackMinuto1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_seg">
						<div class="timer_number" id="blackSegundo0">0</div>
						<div class="timer_number" id="blackSegundo1">0</div>
					</div>
				</div>
				<div class="timer_descricao">
					<div class="timer_desc">Dias</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Horas</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Minutos</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Segundos</div>
				</div>
			</div>
		</div>
		<div class="bloco_pecas">
			<div class="back_blur"></div>
			<h3 class="id_nome"><%= whitePlayer.getName() %></h3>
			<div class="id_pecas">
				<div class="bloco_cor branco"></div>
				<h4 class="nome_cor branco">Peças brancas</h4>
			</div>
			<div class="bloco_pecas_capturadas">
				<h3 class="titulo_pecas_capturadas">Peças capturadas</h3>
				<div class="show_pecas_capturadas">
					<div class="capturadas_cima">
						<!-- Rainha -->
						<li>
							<h1 class="pretas">&#9819;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(9) %></div>
						</li>
						<!-- Torre -->
						<li>
							<h1 class="pretas">&#9820;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(6) %></div>
						</li>
						<!-- Bispo -->
						<li>
							<h1 class="pretas">&#9821;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(8) %></div>
						</li>
					</div>
					<div class="capturadas_baixo">
						<!-- Peao -->
						<li>
							<h1 class="pretas"> &#9823;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(5) %></div>
						</li>
						<!-- Cavalo -->
						<li>
							<h1 class="pretas">&#9822;</h1>
							<div class="num_peca_comida"><%= tabuleiro.getNumberEatenPieces().get(7) %></div>
						</li>
					</div>
				</div>
			</div>
			<div class="zona_timer">
				<div class="bloco_timer">
					<div class="timer_dia">
						<div class="timer_number" id="whiteDia0">0</div>
						<div class="timer_number" id="whiteDia1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_hora">
						<div class="timer_number" id="whiteHora0">0</div>
						<div class="timer_number" id="whiteHora1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_min">
						<div class="timer_number" id="whiteMinuto0">0</div>
						<div class="timer_number" id="whiteMinuto1">0</div>
					</div>
					<div class="timer_sep">:</div>
					<div class="timer_seg">
						<div class="timer_number" id="whiteSegundo0">0</div>
						<div class="timer_number" id="whiteSegundo1">0</div>
					</div>
				</div>
				<div class="timer_descricao">
					<div class="timer_desc">Dias</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Horas</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Minutos</div>
					<div class="timer_sep"></div>
					<div class="timer_desc">Segundos</div>
				</div>
			</div>
		</div>
	</div>
	<div class="bloco_jogo">
		<div class="back_blur"></div>
		<div class="zona_pop_up_promocao">
			<div class="back_promocao"></div>
			<div class="pop_up_promocao">
				<div class="back_blur"></div>
				<div class="pecas_promocao_linha">
					<!-- Rainha -->
					<div class="peca_promocao <% if (game.get().getWhite().equals(name, email)){
                        		out.print("branca");
                        	} else if (game.get().getBlack().equals(name, email)){
                        		out.print("preta");
                        	}
                        	%>" id ="QUEEN">
						<div class="back_blur"></div>
						<div class="codigo_peca">&#9819;</div>
						<div class="nome_peca">Rainha</div>
					</div>
					<!-- Torre -->
					<div class="peca_promocao <% if (game.get().getWhite().equals(name, email)){
                        		out.print("branca");
                        	} else if (game.get().getBlack().equals(name, email)){
                        		out.print("preta");
                        	}
                        	%>" id ="ROOK">
						<div class="back_blur"></div>
						<div class="codigo_peca">&#9820;</div>
						<div class="nome_peca">Torre</div>
					</div>
				</div>
				<div class="pecas_promocao_linha">
					<!-- Cavalo -->
					<div class="peca_promocao <% if (game.get().getWhite().equals(name, email)){
                        		out.print("branca");
                        	} else if (game.get().getBlack().equals(name, email)){
                        		out.print("preta");
                        	}
                        	%>" id ="KNIGHT">
						<div class="back_blur"></div>
						<div class="codigo_peca">&#9822;</div>
						<div class="nome_peca">Cavalo</div>
					</div>
					<!-- Bispo -->
					<div class="peca_promocao <% if (game.get().getWhite().equals(name, email)){
                        		out.print("branca");
                        	} else if (game.get().getBlack().equals(name, email)){
                        		out.print("preta");
                        	}
                        	%>" id ="BISHOP">
						<div class="back_blur"></div>
						<div class="codigo_peca">&#9821;</div>
						<div class="nome_peca">Bispo</div>
					</div>
				</div>
			</div>
		</div>
		<table class="chess-board">
			<tbody>
			<tr>
				<th class="numero_linha"></th>
				<th>a</th>
				<th>b</th>
				<th>c</th>
				<th>d</th>
				<th>e</th>
				<th>f</th>
				<th>g</th>
				<th>h</th>
			</tr>
			<tr>
				<th class="numero_linha">8</th>
				<td class="light borda_cima_esq" id="a8"><%= tabuleiro.getPieceHtml(7,0) %></td>
				<td class="dark" id="b8"><%= tabuleiro.getPieceHtml(7,1) %></td>
				<td class="light" id="c8"><%= tabuleiro.getPieceHtml(7,2) %></td>
				<td class="dark" id="d8"><%= tabuleiro.getPieceHtml(7,3) %></td>
				<td class="light" id="e8"><%= tabuleiro.getPieceHtml(7,4) %></td>
				<td class="dark" id="f8"><%= tabuleiro.getPieceHtml(7,5) %></td>
				<td class="light" id="g8"><%= tabuleiro.getPieceHtml(7,6) %></td>
				<td class="dark borda_cima_dir" id="h8"><%= tabuleiro.getPieceHtml(7,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">7</th>
				<td class="dark" id="a7"><%= tabuleiro.getPieceHtml(6,0) %></td>
				<td class="light" id="b7"><%= tabuleiro.getPieceHtml(6,1) %></td>
				<td class="dark" id="c7"><%= tabuleiro.getPieceHtml(6,2) %></td>
				<td class="light" id="d7"><%= tabuleiro.getPieceHtml(6,3) %></td>
				<td class="dark" id="e7"><%= tabuleiro.getPieceHtml(6,4) %></td>
				<td class="light" id="f7"><%= tabuleiro.getPieceHtml(6,5) %></td>
				<td class="dark" id="g7"><%= tabuleiro.getPieceHtml(6,6) %></td>
				<td class="light" id="h7"><%= tabuleiro.getPieceHtml(6,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">6</th>
				<td class="light" id="a6"><%= tabuleiro.getPieceHtml(5,0) %></td>
				<td class="dark" id="b6"><%= tabuleiro.getPieceHtml(5,1) %></td>
				<td class="light" id="c6"><%= tabuleiro.getPieceHtml(5,2) %></td>
				<td class="dark" id="d6"><%= tabuleiro.getPieceHtml(5,3) %></td>
				<td class="light" id="e6"><%= tabuleiro.getPieceHtml(5,4) %></td>
				<td class="dark" id="f6"><%= tabuleiro.getPieceHtml(5,5) %></td>
				<td class="light" id="g6"><%= tabuleiro.getPieceHtml(5,6) %></td>
				<td class="dark" id="h6"><%= tabuleiro.getPieceHtml(5,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">5</th>
				<td class="dark" id="a5"><%= tabuleiro.getPieceHtml(4,0) %></td>
				<td class="light" id="b5"><%= tabuleiro.getPieceHtml(4,1) %></td>
				<td class="dark" id="c5"><%= tabuleiro.getPieceHtml(4,2) %></td>
				<td class="light" id="d5"><%= tabuleiro.getPieceHtml(4,3) %></td>
				<td class="dark" id="e5"><%= tabuleiro.getPieceHtml(4,4) %></td>
				<td class="light" id="f5"><%= tabuleiro.getPieceHtml(4, 5) %></td>
				<td class="dark" id="g5"><%= tabuleiro.getPieceHtml(4, 6) %></td>
				<td class="light" id="h5"><%= tabuleiro.getPieceHtml(4,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">4</th>
				<td class="light" id="a4"><%= tabuleiro.getPieceHtml(3,0) %></td>
				<td class="dark" id="b4"><%= tabuleiro.getPieceHtml(3,1) %></td>
				<td class="light" id="c4"><%= tabuleiro.getPieceHtml(3,2) %></td>
				<td class="dark" id="d4"><%= tabuleiro.getPieceHtml(3,3) %></td>
				<td class="light" id="e4"><%= tabuleiro.getPieceHtml(3,4) %></td>
				<td class="dark" id="f4"><%= tabuleiro.getPieceHtml(3,5) %></td>
				<td class="light" id="g4"><%= tabuleiro.getPieceHtml(3,6) %></td>
				<td class="dark" id="h4"><%= tabuleiro.getPieceHtml(3,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">3</th>
				<td class="dark" id="a3"><%= tabuleiro.getPieceHtml(2,0) %></td>
				<td class="light" id="b3"><%= tabuleiro.getPieceHtml(2,1) %></td>
				<td class="dark" id="c3"><%= tabuleiro.getPieceHtml(2,2) %></td>
				<td class="light" id="d3"><%= tabuleiro.getPieceHtml(2,3) %></td>
				<td class="dark" id="e3"><%= tabuleiro.getPieceHtml(2,4) %></td>
				<td class="light" id="f3"><%= tabuleiro.getPieceHtml(2,5) %></td>
				<td class="dark" id="g3"><%= tabuleiro.getPieceHtml(2,6) %></td>
				<td class="light" id="h3"><%= tabuleiro.getPieceHtml(2,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">2</th>
				<td class="light" id="a2"><%= tabuleiro.getPieceHtml(1,0) %></td>
				<td class="dark" id="b2"><%= tabuleiro.getPieceHtml(1,1) %></td>
				<td class="light" id="c2"><%= tabuleiro.getPieceHtml(1,2) %></td>
				<td class="dark" id="d2"><%= tabuleiro.getPieceHtml(1,3) %></td>
				<td class="light" id="e2"><%= tabuleiro.getPieceHtml(1,4) %></td>
				<td class="dark" id="f2"><%= tabuleiro.getPieceHtml(1,5) %></td>
				<td class="light" id="g2"><%= tabuleiro.getPieceHtml(1,6) %></td>
				<td class="dark" id="h2"><%= tabuleiro.getPieceHtml(1,7) %></td>
			</tr>
			<tr>
				<th class="numero_linha">1</th>
				<td class="dark borda_baixo_esq" id="a1"><%= tabuleiro.getPieceHtml(0,0) %></td>
				<td class="light" id="b1"><%= tabuleiro.getPieceHtml(0,1) %></td>
				<td class="dark" id="c1"><%= tabuleiro.getPieceHtml(0,2) %></td>
				<td class="light" id="d1"><%= tabuleiro.getPieceHtml(0,3) %></td>
				<td class="dark" id="e1"><%= tabuleiro.getPieceHtml(0,4) %></td>
				<td class="light" id="f1"><%= tabuleiro.getPieceHtml(0,5) %></td>
				<td class="dark" id="g1"><%= tabuleiro.getPieceHtml(0,6) %></td>
				<td class="light borda_baixo_dir" id="h1"><%= tabuleiro.getPieceHtml(0,7) %></td>
			</tr>
			</tbody>
		</table>

	</div>
	<div class="bloco_pessoal">
		<div class="back_blur"></div>
		<%
			if(game.get().isFinished() && game.get().getWinner() != null){
		%>
		<h3 class="id_area_pessoal">Rever Partida</h3>
		<div class="descricao_dados_jogo">Vencedor</div>
		<div class="descricao_dados_principais_jogo"><%= game.get().getWinner().getName() %></div>
		<div class="caixa_cor_pecas_vencedor <%
	            	if(game.get().getWinner().equals(game.get().getWhite().getName() , game.get().getWhite().getEmail())){
	            		out.print("branco");
	            	} else if (game.get().getWinner().equals(game.get().getBlack().getName() , game.get().getBlack().getEmail())){
	            		out.print("preto");
	            	}%>"></div>
		<div class="descricao_dados_vencedor"><%= game.get().getEndGameDescription() %></div>
		<div class="descricao_dados_jogo">Tempo da Partida</div>
		<div class="zona_timer">
			<div class="bloco_timer">
				<div class="timer_dia">
					<div class="timer_number" id="totalDia0">0</div>
					<div class="timer_number" id="totalDia1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_hora">
					<div class="timer_number" id="totalHora0">0</div>
					<div class="timer_number" id="totalHora1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_min">
					<div class="timer_number" id="totalMinuto0">0</div>
					<div class="timer_number" id="totalMinuto1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_seg">
					<div class="timer_number" id="totalSegundo0">0</div>
					<div class="timer_number" id="totalSegundo1">0</div>
				</div>
			</div>
			<div class="timer_descricao">
				<div class="timer_desc">Dias</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Horas</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Minutos</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Segundos</div>
			</div>
		</div>

		<div class="descricao_dados_jogo">Jogadas totais</div>
		<div class="descricao_dados_principais_jogo"><%= game.get().getMoves().size() %></div>
		<%
		} else if(game.get().isFinished() && game.get().getWinner() == null){
		%>
		<h3 class="id_area_pessoal">Rever Partida</h3>

		<div class="descricao_dados_jogo">Empate</div>

		<div class="descricao_dados_principais_jogo"></div>

		<div class="descricao_dados_vencedor"><%= game.get().getEndGameDescription() %></div>

		<div class="descricao_dados_jogo">Tempo da Partida</div>
		<div class="zona_timer">
			<div class="bloco_timer">
				<div class="timer_dia">
					<div class="timer_number" id="totalDia0">0</div>
					<div class="timer_number" id="totalDia1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_hora">
					<div class="timer_number" id="totalHora0">0</div>
					<div class="timer_number" id="totalHora1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_min">
					<div class="timer_number" id="totalMinuto0">0</div>
					<div class="timer_number" id="totalMinuto1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_seg">
					<div class="timer_number" id="totalSegundo0">0</div>
					<div class="timer_number" id="totalSegundo1">0</div>
				</div>
			</div>
			<div class="timer_descricao">
				<div class="timer_desc">Dias</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Horas</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Minutos</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Segundos</div>
			</div>
		</div>

		<div class="descricao_dados_jogo">Jogadas totais</div>
		<div class="descricao_dados_principais_jogo"><%= game.get().getMoves().size() %></div>
		<%
		} else {
		%>

		<h3 class="id_area_pessoal">Area Pessoal</h3>

		<div class="tempo_jogada">Tempo da jogada</div>
		<div class="zona_timer">
			<div class="bloco_timer">
				<div class="timer_dia">
					<div class="timer_number" id="dia0">0</div>
					<div class="timer_number" id="dia1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_hora">
					<div class="timer_number" id="hora0">0</div>
					<div class="timer_number" id="hora1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_min">
					<div class="timer_number" id="minuto0">0</div>
					<div class="timer_number" id="minuto1">0</div>
				</div>
				<div class="timer_sep">:</div>
				<div class="timer_seg">
					<div class="timer_number" id="segundo0">0</div>
					<div class="timer_number" id="segundo1">0</div>
				</div>
			</div>
			<div class="timer_descricao">
				<div class="timer_desc">Dias</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Horas</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Minutos</div>
				<div class="timer_sep"></div>
				<div class="timer_desc">Segundos</div>
			</div>
		</div>
		<%
			}
		%>
		<%
			if (game.get().getPlayerTurn().equals(name, email) && !game.get().isFinished() && replayMode == false){
		%>
		<form id="formsMove" action="Game?Id=<%= gameId %>"  method="POST">
			<h3 class="id_jogada">Introduzir jogada</h3>
			<div class="bloco_input">
				<input type="text" class="input_pesquisa" id="move" name="move" placeholder="a1 b1" maxlength="12">
				<input type="text" class="input_invisivel" id="tempo" name="tempoMove">
			</div>
			<h4 class="id_info_jogada">Introduz as posições ou clica no tabuleiro para mover a peça</h4>
			<button type="submit" class="btn jogar">Introduzir jogada</button>
		</form>
		<div class="btn_options_lado" >
			<form id="formsDesistir" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="desistir" value="desistir">
				<button type="submit" class="btn_lado_lado terminar">Desistir</button>
			</form>
			<form id="formsPedirEmpate" action="Game?Id=<%= gameId %>"  method="POST">
				<%
					if (game.get().getTieOffer() == null){
				%>
				<input type="text" class="input_invisivel" name="pedirEmpate" value="pedirEmpate">
				<button type="submit" class="btn_lado_lado empatar">Pedir empate</button>
				<%
				} else {
				%>
				<input type="text" class="input_invisivel" name="pedirEmpate" value="pedirEmpate">
				<button type="submit" class="btn_lado_lado empatar disable" disabled>Pedir empate</button>
				<%
					}
				%>
			</form>
		</div>
		<%
			if (game.get().getTieOffer() != null){
				if (!game.get().getTieOffer().equals(name, email)){
		%>
		<h3 class="id_decidir_empate">O adversário pediu empate. Pretende aceitar empate?</h3>
		<div class="zona_decidir_empate">
			<form id="formsAceitarEmpate" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="aceitarEmpate" value="aceitarEmpate">
				<button type="submit" class="btn_aceitar_empate">&#10003;</button>
			</form>
			<form id="formsRecusarEmpate" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="recusarEmpate" value="recusarEmpate">
				<button type="submit" class="btn_recusar_empate">&#10007;</button>
			</form>
		</div>

		<%
				}
			}
		%>

		<div class="fixar_rever">
			<h3 class="id_rever">Rever jogadas</h3>
			<div class="zona_btn_rever">
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex %>" method="POST" id="formJogadaAnterior">
					<button class="seta_dir <% if(moveIndex + 1 == 0){ out.print("disabled_seta");} %>" type="submit" name="jogadaAnterior" value="jogadaAnterior" <% if(moveIndex + 1 == 0){ out.print("disabled");} %>>&#8592;</button>
				</form>
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex + 2 %>" method="POST" id="formJogadaSeguinte">
					<button class="seta_esq <% if(!replayMode){ out.print("disabled_seta");} %>" type="submit" name="jogadaSeguinte" value="jogadaSeguinte" <% if(!replayMode){ out.print("disabled");} %>>&#8592;</button>
				</form>
			</div>
			<h4 class="id_info_rever">Revê as jogadas realizadas pelos jogadores deste jogo</h4>
		</div>

		<%
		} else if (((whitePlayer.equals(name, email) || blackPlayer.equals(name, email))  && !game.get().isFinished()) && replayMode == false) {
		%>

		<div class="btn_options_lado" >
			<form id="formsDesistir" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="desistir" value="desistir">
				<button type="submit" class="btn_lado_lado terminar">Desistir</button>
			</form>
			<form id="formsPedirEmpate" action="Game?Id=<%= gameId %>"  method="POST">

				<%
					if (game.get().getTieOffer() == null){
				%>

				<input type="text" class="input_invisivel" name="pedirEmpate" value="pedirEmpate">
				<button type="submit" class="btn_lado_lado empatar">Pedir empate</button>

				<%
				} else {
				%>

				<input type="text" class="input_invisivel" name="pedirEmpate" value="pedirEmpate">
				<button type="submit" class="btn_lado_lado empatar disable" disabled>Pedir empate</button>
				<%
					}
				%>
			</form>
		</div>

		<%
			if (game.get().getTieOffer() != null){
				if (!game.get().getTieOffer().equals(name, email)){
		%>

		<h3 class="id_decidir_empate">O adversário pediu empate. Pretende aceitar empate?</h3>
		<div class="zona_decidir_empate">
			<form id="formsAceitarEmpate" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="aceitarEmpate" value="aceitarEmpate">
				<button type="submit" class="btn_aceitar_empate">&#10003;</button>
			</form>
			<form id="formsRecusarEmpate" action="Game?Id=<%= gameId %>"  method="POST">
				<input type="text" class="input_invisivel" name="recusarEmpate" value="recusarEmpate">
				<button type="submit" class="btn_recusar_empate">&#10007;</button>
			</form>
		</div>

		<%
				}
			}
		%>

		<div class="fixar_rever">
			<h3 class="id_rever">Rever jogadas</h3>
			<div class="zona_btn_rever">
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex %>" method="POST" id="formJogadaAnterior">
					<button class="seta_dir <% if(moveIndex + 1 == 0){ out.print("disabled_seta");} %>" type="submit" name="jogadaAnterior" value="jogadaAnterior" <% if(moveIndex + 1 == 0){ out.print("disabled");} %>>&#8592;</button>
				</form>
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex + 2 %>" method="POST" id="formJogadaSeguinte">
					<button class="seta_esq <% if(!replayMode){ out.print("disabled_seta");} %>" type="submit" name="jogadaSeguinte" value="jogadaSeguinte" <% if(!replayMode){ out.print("disabled");} %>>&#8592;</button>
				</form>
			</div>
			<h4 class="id_info_rever">Revê as jogadas realizadas pelos jogadores deste jogo</h4>
		</div>

		<%
		} else {
		%>

		<div class="fixar_rever_in_game">
			<div class="sair_replay">
				<div class="descricao_replay">Estás em modo replay. Clica para poderes jogar ou voltar à jogada atual</div>
				<a class="btn_voltar_jogada_atual" href="Game?Id=<%= gameId %>">Voltar à jogada atual</a>
				<div class="numero_da_jogada">Jogada - <span><%= moveIndex + 1 %></span></div>
			</div>
			<h3 class="id_rever_in_game">Rever jogadas</h3>
			<div class="zona_btn_rever">
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex %>" method="POST" id="formJogadaAnterior">
					<button class="seta_dir <% if(moveIndex + 1 == 0){ out.print("disabled_seta");} %>" type="submit" name="jogadaAnterior" value="jogadaAnterior" <% if(moveIndex + 1 == 0){ out.print("disabled");} %>>&#8592;</button>
				</form>
				<form action="Game?Id=<%= gameId %>&MoveNumber=<%= moveIndex + 2 %>" method="POST" id="formJogadaSeguinte">
					<button class="seta_esq <% if(!replayMode){ out.print("disabled_seta");} %>" type="submit" name="jogadaSeguinte" value="jogadaSeguinte" <% if(!replayMode){ out.print("disabled");} %>>&#8592;</button>
				</form>
			</div>
			<h4 class="id_info_rever">Revê as jogadas realizadas pelos jogadores deste jogo</h4>
		</div>

		<%
			}
		%>

	</div>
</div>

<button type="button" id="flipBoard">Rodar Tabuleiro</button>
<script src="scripts/game.js"></script>
<script src="scripts/count.js"></script>
<script>

	counterBlackDisplay(<%=blackPlayerTime%>);
	counterWhiteDisplay(<%=whitePlayerTime%>);

	<%
    if (game.get().getBlack().equals(name,email)){
        %> blackView(); <%
        	%> corPlayer = 'preto'; <%
        } else {
        	%> corPlayer = 'branco'; <%
        }

        if(dataDeAbertura != null  && !(game.get().isFinished())){
            %>counterDisplay(<%= dataDeAbertura%>);<%
        } else if (game.get().isFinished()){
        	%>counterTotalDisplay(<%=blackPlayerTime%>, <%=whitePlayerTime%>);<%
        }

        if (game.get().isFinished() && replayMode == false){

        	if (game.get().getWinner() == null){

        		if (game.get().getWhite().equals(name, email) && game.get().getWhiteNotification() == false ){

        			%> ativar_empate() <%
        			game.get().setWhiteNotification(true);
        			cgDM.update(game.get());

        		} else if (game.get().getBlack().equals(name, email) && game.get().getBlackNotification() == false){
        			%> ativar_empate() <%
        			game.get().setBlackNotification(true);
        			cgDM.update(game.get());
        		}

        	}else if (game.get().getWinner().equals(name, email)){

				if (game.get().getWhite().equals(name, email) && game.get().getWhiteNotification() == false){

					%> ativar_vitoria() <%
					game.get().setWhiteNotification(true);
        			cgDM.update(game.get());
        		}else if (game.get().getBlack().equals(name, email) && game.get().getBlackNotification() == false){

        			%> ativar_vitoria() <%
        			game.get().setBlackNotification(true);
        			cgDM.update(game.get());
        		}

        	} else if (game.get().getWhite().equals(name, email) || game.get().getBlack().equals(name, email)){
				if (game.get().getWhite().equals(name, email) && game.get().getWhiteNotification() == false){
					%> ativar_derrota() <%
					game.get().setWhiteNotification(true);
        			cgDM.update(game.get());

        		}else if (game.get().getBlack().equals(name, email) && game.get().getBlackNotification() == false){
        			%> ativar_derrota() <%
        			game.get().setBlackNotification(true);
        			cgDM.update(game.get());
        		}
        	}
        }
        %>

	<% if (interatividade == true){ %>
	setInterval(() => {
		var xhttp = new XMLHttpRequest()
		xhttp.onreadystatechange = function() {
			if (this.readyState === 4 && this.status === 200) {
				if(this.responseText === "True"){
					window.location.href = window.location.href;
				}
			}
		}
		xhttp.open("GET", "ajax?gameId=" + <%= game.get().getId()%> + "&numberOfMoves=" + <%= game.get().getMoves().size() %> + "&finished=" + <%= Boolean.toString(game.get().isFinished()) %> + "&tieOffer=" + <% if(game.get().getTieOffer() != null){out.print("true");} else {out.print("false");} %>, true);
		xhttp.send();
	}, 300);
	<% } %>

	<% if (game.get().getPlayerTurn().equals(name, email) && interatividade == true){
        %>
	function previewJogadas(square){
		var xhttp = new XMLHttpRequest()
		xhttp.onreadystatechange = function() {
			if (this.readyState === 4 && this.status === 200) {
				adicionarPossibles(this.responseText);
			}
		}
		xhttp.open("GET", "ajax?gameId=" + <%= game.get().getId()%> + "&square=" + square, true);
		xhttp.send();
	}

	<%
    }
    if (game.get().getPlayerTurn().equals(name, email)){
        %>interatividadeTabuleiro(); <%
        	}
			%>

</script>
</body>
</html>