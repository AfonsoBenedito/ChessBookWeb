<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import = "persist.ChessGameDM, persist.ChessPlayerDM, domain.ChessGame, java.util.List, domain.ChessBoard, domain.ChessPlayer, java.util.ArrayList, java.util.Date, main.ChessMain, java.util.Random" %>

<%
String logged = (String)request.getSession(false).getAttribute("logged");
String name = (String)request.getSession(false).getAttribute("name");
String email = (String)request.getSession(false).getAttribute("email");

if (logged != null){
	if (!logged.equals("true")){
		response.sendRedirect("/");
		return;
	}
} else {
	response.sendRedirect("/");
	return;
}

boolean sessionInteratividade = (boolean)request.getSession(false).getAttribute("interatividade");

String formsInter = request.getParameter("formsInter");
String interatividadeString = request.getParameter("boxInteratividade");
boolean interatividade = false;
if (formsInter != null){
	if (interatividadeString != null){
		interatividade = true;
	} else {
		interatividade = false;
	}
} else {
	interatividade = sessionInteratividade;
}

request.getSession().setAttribute("interatividade",interatividade);

ChessGameDM cgDM = ChessGameDM.getInstance();
ChessPlayerDM cpDM = ChessPlayerDM.getInstance();

try {
	cgDM.findByPlayerName("teste");
}catch(Exception e){
	response.sendRedirect("Erro");
	return;
}

List<ChessGame> finishedGames = cgDM.findFinishedGamesByEmail(email);

List<ChessGame> unfinishedGames = cgDM.findUnfinishedGamesByEmail(email);

List<ChessPlayer> playerList = cpDM.chessPlayersList();

List<ChessPlayer> playerListNoSelf = new ArrayList<ChessPlayer>();

for (int i = 0; i < playerList.size(); i++){
	if (!playerList.get(i).equals(name, email)){
		playerListNoSelf.add(playerList.get(i));
	}
}

String startGame = request.getParameter("startGameEmail");
String startCor = request.getParameter("cor");

if (startGame != null){
	
	Date date = new Date(System.currentTimeMillis());
	
	ChessPlayer playerOne = null;
	
	ChessPlayer playerTwo = null;
	
	if (startCor.equals("branco")){
		
		playerOne = cpDM.findByEmail(email).get();
		
		playerTwo = cpDM.findByEmail(startGame).get();
		
	} else if (startCor.equals("preto")){
		
		playerTwo = cpDM.findByEmail(email).get();
		
		playerOne = cpDM.findByEmail(startGame).get();
		
	} else if (startCor.equals("random")){
		Random rd = new Random();
		
		if (rd.nextBoolean()){
			playerOne = cpDM.findByEmail(email).get();
			
			playerTwo = cpDM.findByEmail(startGame).get();
		} else {
			playerTwo = cpDM.findByEmail(email).get();
			
			playerOne = cpDM.findByEmail(startGame).get();
		}
	}

	ChessGame newGame = new ChessGame(playerOne, playerTwo, date);
	
	int newGameId = cgDM.insert(newGame);
	
	response.sendRedirect("Game?Id=" + String.valueOf(newGameId));
}
%>

<!DOCTYPE html>

<html>
<head>
<link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
<meta charset="UTF-8">
<link rel="stylesheet" href="styles/geral.css">
<link rel="stylesheet" href="styles/gameList.css">
<title>Game List</title>
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
        	<a href="Logout">
	            <li class="btn_sair" >
	                <p>Sair</p>
	            </li>
            </a>
        </div>
        <div class="zona_nome">
            <h2>Olá, <span><%= name %></span></h2>
        </div>
    </header>

    <div class="content_total">
        <div class="box">
            <div class="back_blur"></div>
            <div class="front_invisible">
                <div class="zona_titulo_interatividade">
                    <h3>Partidas a decorrer</h3>

                    <div class="zona_interatividade">
                        <div id="nome_mogo_interativo">Modo interativo</div>
                        <!-- Rounded switch -->
                        <form id="formsInteratividade" method="POST" action="GameList">
	                        <label class="switch_interatividade">
	                        	<input type="hidden" name="formsInter" value="formsInter">
	                            <input type="checkbox" id="modo_interativo_btn" name="boxInteratividade" value="interTrue" <% if (interatividade){out.print("checked");} %>>
	                            <span class="slider_interatividade round"></span>
	                        </label>
                        </form>
                    </div>
                </div>
                
                <%
                if (unfinishedGames.size() == 0){
                	%> 
                	<div class="sem_jogos">
                    <h2>Não tens partidas a decorrer</h2>
                    <img src="images/info_3.png" alt="">
                	</div>
                	<% 
                }
                %>
                
                <div class="scroll_blocos">
                
                	<%
                	for (int i = 0; i < unfinishedGames.size(); i++){
                		
                		ChessGame uGTest = unfinishedGames.get(i);
                		
                		uGTest.setBoard(new ChessBoard(uGTest.getMoves()));
                		
                		out.println("<div class=\"sub_bloco_jogos\">");
                		out.println("<div class=\"first_line\">");
                		out.println("<h4>#" + String.valueOf(uGTest.getId()) + "</h4>");
                		out.println("<h4>" + String.valueOf(uGTest.getMoves().size()) + " jogadas</h4>");
                		out.println("<a href=\"Game?Id=" + String.valueOf(uGTest.getId()) +"\">");
                		out.println("<h4 class=\"btn_voltar_jogar\">Jogar</h4>");
                		out.println("</a>");
                		out.println("</div>");
                		out.println("<div class=\"second_line\">");
                		
                		if (uGTest.getPlayerTurn().equals(name, email)){
                			
                			if (uGTest.getWhite().equals(name,email) && uGTest.getTieOffer() != null){
                				
	                			if (uGTest.getTieOffer().equals(uGTest.getBlack().getName(), uGTest.getBlack().getEmail())){
	                				
	                				out.println("<h4>É a tua vez de jogares - <span>Pedido de Empate</span></h4>");
	                				
	                			} else{
	                				
	                				out.println("<h4>É a tua vez de jogares</h4>");
	                			}
	                			
                			}else if (uGTest.getBlack().equals(name,email) && uGTest.getTieOffer() != null){
                				
	                			if (uGTest.getTieOffer().equals(uGTest.getWhite().getName(), uGTest.getWhite().getEmail())){
	                				
	                				out.println("<h4>É a tua vez de jogares - <span>Pedido de Empate</span></h4>");
	                				
	                			} else{
	                				
	                				out.println("<h4>É a tua vez de jogares</h4>");
	                			}
	                			
                			} else{
                				
                				out.println("<h4>É a tua vez de jogares</h4>");
                			}
                			
                		} else {
         
							if (uGTest.getWhite().equals(name,email) && uGTest.getTieOffer() != null){
                				
	                			if (uGTest.getTieOffer().equals(uGTest.getBlack().getName(), uGTest.getBlack().getEmail())){
	                				
	                				out.println("<h4>Aguarde pelo adversário - <span>Pedido de Empate</span></h4>");
	                				
	                			} else{
	                				
	                				out.println("<h4>Aguarde pelo adversário</h4>");
	                			}
	                			
                			}else if (uGTest.getBlack().equals(name,email) && uGTest.getTieOffer() != null){
                				
	                			if (uGTest.getTieOffer().equals(uGTest.getWhite().getName(), uGTest.getWhite().getEmail())){
	                				
	                				out.println("<h4>Aguarde pelo adversário - <span>Pedido de Empate</span></h4>");
	                				
	                			} else{
	                				
	                				out.println("<h4>Aguarde pelo adversário</h4>");
	                			}
	                			
                			} else{
                				
                				out.println("<h4>Aguarde pelo adversário</h4>");
                			}
                		}

                		out.println("</div>");
                		out.println("<div class=\"second_line\">");
                		
                		if (uGTest.getWhite().equals(name, email)){
                			out.println("<h4>Adversário: " + uGTest.getBlack().getName() + "</h4>");
                		} else {
                			out.println("<h4>Adversário: " + uGTest.getWhite().getName() + "</h4>");
                		}

                		out.println("</div>");
                		out.println("<div class=\"image_game\"></div>");
                		out.println("</div>");
                	}
                	%>
                </div>
            </div>
        </div>
        <div class="box">
            <div class="back_blur"></div>
            <div class="front_invisible">
                <h3>Nova partida</h3>
                <div class="nova_partida_bloco_pesquisa">
                    <input type="text" class="input_pesquisa" id="#" name="#" placeholder="Pesquisa" maxlength="20">
                    <div class="icone lupa"></div>
                </div>
                <h5>Principais sugestões</h5>
                <div class="resultado_busca">
                	<%
						int maxVar = 10;
               			
               			if (playerList.size() < 10){
               				maxVar = playerListNoSelf.size();
               			}
               			
               			for (int i = 0; i < maxVar; i++){

              				out.println("<li class=\"liJogador\" id='" + playerListNoSelf.get(i).getEmail() + "'>");
            				out.println("<h3 id='h3" + playerListNoSelf.get(i).getEmail() + "'>" + playerListNoSelf.get(i).getName() + "</h3>");
            				out.println("<h6>" + playerListNoSelf.get(i).getEmail() + "</h6>");
               				out.println("</li>");
               				
               			}
                	%>

                </div>
                <form action="GameList" method="POST">
	                <div class="jogador_escolhido">
	                    <div class="nome_jogador_escolhido"><span>Oponente:</span> </div>
	                </div>
	                <input name="startGameEmail" class="inputStartGame" required>
	                
	                <div class="nome_escolher_pecas">Escolher cor</div>
	                <div class="escolher_cor_pecas">
	                    <label class="container"> Brancas
	                        <input name="cor" type="radio" value="branco" checked="checked">
	                        <span class="checkmark"></span>
	                    </label>
	                      
	                    <label class="container"> Pretas
	                        <input name="cor" type="radio" value="preto">
	                        <span class="checkmark"></span>
	                    </label>
	                    
	                    <label class="container"> Random
	                        <input name="cor" type="radio" value="random">
	                        <span class="checkmark"></span>
	                    </label>
	                </div>
	                <button type="submit" class="btn">Começar jogo</button>
                </form>
                
            </div>
        </div>
        <div class="box">
            <div class="back_blur"></div>
            <div class="front_invisible">
                <h3>Histórico de Jogos</h3>
                <% 
					if (finishedGames.size() == 0){
						%>
						<div class="sem_jogos">
							<h2>Ainda não realizaste nenhuma partida</h2>
							<img src="images/info_1.png" alt="">
						</div>
						<%
					}
					%>
                <div class="scroll_blocos">
                	
                	<%
						for (int i = 0; i < finishedGames.size(); i++){

							ChessGame fGTest = finishedGames.get(i);

							out.println("<div class=\"sub_bloco_jogos_historico\">");
							out.println("<div class=\"third_line\">");

							out.println("<h4>#" + String.valueOf(fGTest.getId()) + "</h4>");

							ChessPlayer winner = fGTest.getWinner();

							if (winner == null){
								out.println("<h4 class=\"empatar_jogo\">Empataram o jogo!</h4>");
							} else {
								if (winner.equals(name, email)){
									out.println("<h4 class=\"vencer_jogo\">Venceste o jogo!</h4>");
								} else {
									out.println("<h4 class=\"perder_jogo\">Perdeste o jogo!</h4>");
								}
							}

							out.println("");

							out.println("</div>");
							out.println("<div class=\"fourth_line\">");

							out.println("<h4>Número de jogadas total - " + String.valueOf(fGTest.getMoves().size()) + " jogadas</h4>");
							out.println("<a href=\"Game?Id=" + String.valueOf(fGTest.getId()) +"\">");
							out.println("<h4 class=\"btn_voltar_reply\">Replay</h4>");
							out.println("</div>");
							out.println("<div class=\"second_line\">");

							if (fGTest.getWhite().equals(name, email)){
								out.println("<h4>Adversário: " + fGTest.getBlack().getName() + "</h4>");
							} else {
								out.println("<h4>Adversário: " + fGTest.getWhite().getName() + "</h4>");
							}

							out.println("</div>");

							out.println("<div class=\"second_line\">");

							out.println("<h4>Motivo: <span class='motivo_jogo'>" + fGTest.getEndGameDescription() + "</h4>");

							out.println("</div>");

							out.println("</div>");
						}
                	%>
                </div>
            </div>
        </div>
    </div>
	<script>
		listaJogadores = []
		<%
			for (int i = 0; i < playerListNoSelf.size(); i++){
				out.println("jogadorAdd = ['" + playerListNoSelf.get(i).getName() + "','" + playerListNoSelf.get(i).getEmail() + "']");
				out.println("listaJogadores.push(jogadorAdd)");
			}
		
		%>
	 </script>
    <script src="scripts/gamelist.js"></script>
</body>
</html>