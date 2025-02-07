<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "persist.ChessPlayerDM, persist.ChessGameDM, main.ChessMain, java.util.List, domain.ChessGame, domain.ChessPlayer" %>
<%
    ChessPlayerDM cpDM = ChessPlayerDM.getInstance();

    ChessGameDM cgDM = ChessGameDM.getInstance();

    List<ChessGame> listOfGames = cgDM.findGamesList();

    List<ChessPlayer> listOfPlayers = cpDM.chessPlayersList();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles/manager.css">
    <title>Geral</title>
</head>
<body>

<header>
    <h1>ManageDB</h1>
    <div class="header_options">
        <a href="logout.jsp">
            <li class="btn_sair" >
                <p>Sair</p>
            </li>
        </a>
    </div>
    <div class="zona_nome">
        <h2>Gestão do <span>Admin</span></h2>
    </div>
</header>

<form action="Manager" method="POST" id="apagarJogo">
    <input type="hidden" name="apagarJogo">
</form>
<form action="Manager" method="POST" id="apagarJogadas">
    <input type="hidden" name="apagarJogadas">
</form>
<form action="Manager" method="POST" id="apagarJogador">
    <input type="hidden" name="apagarJogador">
</form>

<div class="zona_btns_principais">
    <div id="btn_partidas">Partidas</div>
    <div id="btn_jogadores">Jogadores</div>
</div>

<div id="content_total_partidas">

    <div class="back_blur"></div>

    <div class="header_table">
        <div class="arg_btn"></div>
        <div class="arg_id">Id</div>
        <div class="arg_meio">Data de início</div>
        <div class="arg_meio">Jogador branco</div>
        <div class="arg_meio">Jogador preto</div>
        <div class="arg_meio">Estado</div>
        <div class="arg_meio">Vencedor</div>
        <div class="arg_meio">Motivo</div>
        <div class="arg_eliminar"></div>
    </div>

    <div class="conteudo_dinamico">
        <%
            for (int i = 0; i < listOfGames.size(); i++){
        %>
        <div class="display_game">

            <div class="linha_table">
                <div class="arg_btn"><div class="arg_btn_expandir">&#8250;</div></div>
                <div class="arg_id"><%= listOfGames.get(i).getId() %></div>
                <div class="arg_meio"><%= listOfGames.get(i).getDate().toString() %></div>
                <div class="arg_meio"><%= listOfGames.get(i).getWhite().getName() %></div>
                <div class="arg_meio"><%= listOfGames.get(i).getBlack().getName() %></div>
                <div class="arg_meio"><% if(listOfGames.get(i).isFinished()){out.print("Terminado");} else {out.print("A Decorrer");} %></div>
                <div class="arg_meio"><% if(listOfGames.get(i).getWinner() != null){out.print(listOfGames.get(i).getWinner().getName());} else {out.print("null");} %></div>
                <div class="arg_meio"><%= listOfGames.get(i).getEndGameDescription() %></div>
                <div class="arg_eliminar"><div class="arg_btn_eliminar" id = "jogo<%= listOfGames.get(i).getId() %>"></div></div>
            </div>
            <div class="display_jogadas">
                <div class="sub_linha_table">
                    <div class="sub_arg">Jogada</div>
                    <div class="sub_arg">Jogador</div>
                    <div class="sub_arg">Peça</div>
                    <div class="sub_arg">Posição inicial</div>
                    <div class="sub_arg">Posição final</div>
                    <div class="sub_arg">Promoção</div>
                    <div class="sub_arg">Tempo</div>
                    <div class="sub_arg_reset" id = "jogadas<%=listOfGames.get(i).getId() %>"></div>
                </div>
                <%
                    for (int j = 0; j < listOfGames.get(i).getMoves().size(); j++){
                %>
                <div class="sub_linha_table">
                    <div class="sub_arg"><%= listOfGames.get(i).getMoves().get(j).getId() %></div>
                    <div class="sub_arg"><%= listOfGames.get(i).getMoves().get(j).getPlayer().getName() %></div>
                    <div class="sub_arg"><%= listOfGames.get(i).getMoves().get(j).getPiece().getChessPieceKind().toString() %></div>
                    <div class="sub_arg"><%= ChessMain.convertPosToString(listOfGames.get(i).getMoves().get(j).getFrom()) %></div>
                    <div class="sub_arg"><%= ChessMain.convertPosToString(listOfGames.get(i).getMoves().get(j).getTo())%></div>
                    <div class="sub_arg"><%= listOfGames.get(i).getMoves().get(j).getPromotion() %></div>
                    <div class="sub_arg penultimo_sub"><%= listOfGames.get(i).getMoves().get(j).getTimeMilli() %></div>
                    <div class="sub_arg_reset_vazio"></div>
                </div>
                <%
                    }
                %>
            </div> <!-- /display jogadas -->
        </div> <!-- /display game -->
        <%
            }
        %>

    </div> <!-- /conteudo dinamico -->
</div> <!-- /conteudo total -->

<div id="content_total_jogadores">
    <div class="back_blur"></div>

    <div class="header_table_jogadores">
        <div class="arg_id_jogador">Id</div>
        <div class="arg_meio_jogador">Nome</div>
        <div class="arg_meio_jogador">Email</div>
        <div class="arg_meio_jogador">Jogos</div>
        <div class="arg_meio_jogador">Vitóras</div>
        <div class="arg_meio_jogador">Empates</div>
        <div class="arg_meio_jogador">Derrotas</div>
        <div class="arg_eliminar_jogador"></div>
    </div>

    <div class="conteudo_dinamico">

        <%
            for (int i = 0; i < listOfPlayers.size(); i++){
        %>

        <div class="display_jogador">
            <%
                List<ChessGame> gamesTest = cgDM.findFinishedGamesByEmail(listOfPlayers.get(i).getEmail());
                int empates = 0;
                int derrotas = 0;
                int vitorias = 0;
                for (int j = 0; j < gamesTest.size(); j++){

                    if (gamesTest.get(j).getWinner() != null){
                        if (!gamesTest.get(j).getWinner().equals(listOfPlayers.get(i).getName(), listOfPlayers.get(i).getEmail())){
                            derrotas++;
                        } else {
                            vitorias++;
                        }
                    } else {
                        empates++;
                    }

                }
            %>

            <div class="linha_table_jogador">
                <div class="arg_id_jogador"><%= listOfPlayers.get(i).getId() %></div>
                <div class="arg_meio_jogador"><%= listOfPlayers.get(i).getName() %></div>
                <div class="arg_meio_jogador"><%= listOfPlayers.get(i).getEmail() %></div>
                <div class="arg_meio_jogador"><%= cgDM.findByPlayerEmail(listOfPlayers.get(i).getEmail()).size() %></div>
                <div class="arg_meio_jogador"><%= vitorias %></div>
                <div class="arg_meio_jogador"><%= empates %></div>
                <div class="arg_meio_jogador"><%= derrotas %></div>
                <div class="arg_eliminar_jogador"><div class="arg_btn_eliminar_jogador" id="jogador<%= listOfPlayers.get(i).getId() %>"></div></div>
            </div>
        </div> <!-- /display jogador -->
        <% } %>
    </div> <!-- /conteudo dinamico -->
</div> <!-- /conteudo total -->
<script src="scripts/manager.js"></script>
</body>
</html>
