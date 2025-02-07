<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ page import = "java.util.Optional, persist.ChessGameDM, domain.ChessGame, java.lang.StringBuilder, java.util.List, java.lang.String, domain.ChessMove, domain.ChessBoard, main.ChessMain" %><%

    ChessMain.clearCache();
    String gameId = request.getParameter("gameId");
    int gameIdInt = Integer.parseInt(gameId);
    String numberOfMoves = request.getParameter("numberOfMoves");
    String finished = request.getParameter("finished");
    String tieOffer = request.getParameter("tieOffer");
    String square = request.getParameter("square");

    ChessGameDM cgDM = ChessGameDM.getInstance();
    Optional<ChessGame> game = cgDM.find(gameIdInt);

    if(numberOfMoves != null){
        int numberOfMovesInt = Integer.parseInt(numberOfMoves);
        if(numberOfMovesInt != game.get().getMoves().size() || !finished.equals(Boolean.toString(game.get().isFinished())) || ((tieOffer.equals("true") && game.get().getTieOffer() == null) || (tieOffer.equals("false") && game.get().getTieOffer() != null))){
        	out.print("True");
        }
    }

    if(square != null){
        List<ChessMove> jogadas = game.get().getMoves();
        ChessBoard tabuleiro = new ChessBoard(jogadas);
        game.get().setBoard(tabuleiro);

        List<String> possibleMoves = game.get().getPossibleMoves(square);
        StringBuilder sb  = new StringBuilder();
        for(int i = 0; i < possibleMoves.size(); i++){
            sb.append(possibleMoves.get(i) + " ");
        }
        out.print(sb.toString());
    }
%>