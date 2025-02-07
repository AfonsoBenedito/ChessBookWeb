package main;

import java.util.Date;
import java.util.Scanner;
import javax.persistence.*;
import domain.ChessGame;
import domain.ChessMove;
import domain.ChessPlayer;
import domain.ChessPosition;
import domain.IllegalMoveException;
import persist.*;

public class ChessMain {
	
	public static EntityManagerFactory emf;
	
	private static Scanner sc = new Scanner(System.in);
	
	static {
		boolean noEmf = true;
		int tries = 0;
		int maxTries = 5;
		while (noEmf && tries < maxTries) {
			try {
				emf = Persistence.createEntityManagerFactory("chessbookweb");
				noEmf = false;
			} catch (Exception e) {
				noEmf = true;
				tries += 1;
				System.out.println(e.getMessage());
			}
		}
		
		if (noEmf) {
			System.out.println("Cant connect to Database");
		}
	}
	
	public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
	
	public static void clearCache() {
        emf.getCache().evictAll();
    }
	
	public static void closeEntityManagerFactory() {
		emf.close();
	}

	public static void main(String[] args) {
		ChessPlayer playerOne = new ChessPlayer("Manuel", "manuel@email.com");

		ChessPlayer playerTwo = new ChessPlayer("Acácio", "acacio@email.com");

		Date d = new Date(System.currentTimeMillis());

		ChessGame Game1 = new ChessGame(playerOne, playerTwo, d);

		ChessPlayer currentPlayer = playerOne;

		boolean goodMove = false;

		while (Game1.isFinished() == false) {

			System.out.println(Game1.getBoard().toString());

			ChessMove move1 = askMove(currentPlayer, Game1);

			try {
				Game1.addMove(move1);
				System.out.println(Game1.getBoard());
				goodMove = true;
			} catch (IllegalMoveException e) {
				System.out.println(e.getMessage());
				goodMove = false;
			}

			if (goodMove == true) {
				if (currentPlayer.equals(playerOne)) {
					currentPlayer = playerTwo;
				} else {
					currentPlayer = playerOne;				}
			}

			goodMove = false;
		}

		sc.close();
	}
	
	public static String convertPosToString(ChessPosition pos) {
		switch(pos.getCol()) {
			case 0:
				return "a"+(pos.getRow() + 1);

			case 1:
				return "b"+(pos.getRow() + 1);

			case 2:
				return "c"+(pos.getRow() + 1);

			case 3:
				return "d"+(pos.getRow() + 1);

			case 4:
				return "e"+(pos.getRow() + 1);

			case 5:
				return "f"+(pos.getRow() + 1);

			case 6:
				return "g"+(pos.getRow() + 1);

			case 7:
				return "h"+(pos.getRow() + 1);
		}
		return null;
	}
	
	private static ChessMove askMove(ChessPlayer player, ChessGame game) {
		while (true) {
			System.out.println(player.getName() + " (colunalinha colunalinha):");
			String inp1 = sc.nextLine();
			System.out.println(inp1);
			try {
				ChessMove move = convertMove(inp1, player, game);
				return move;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private static ChessMove convertMove(String input, ChessPlayer player, ChessGame game) throws Exception {
		ChessMove move = null;
		int timeMilli = 0;
		int colFrom;
		int rowFrom;
		int colTo;
		int rowTo;
		if (input.length() != 5 || input.charAt(2) != ' ' || !Character.isDigit(input.charAt(1)) || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(4)) || !Character.isLetter(input.charAt(3))) {
			throw new Exception("Input is wrongly formmated");
		} else {
			switch (input.charAt(0)) {
				case 'a':
					colFrom = 0;
					break;

				case 'b':
					colFrom = 1;
					break;

				case 'c':
					colFrom = 2;
					break;

				case 'd':
					colFrom = 3;
					break;

				case 'e':
					colFrom = 4;
					break;

				case 'f':
					colFrom = 5;
					break;

				case 'g':
					colFrom = 6;
					break;

				case 'h':
					colFrom = 7;
					break;

				default:
					throw new Exception("Input is wrong, From Column");
			}
			
			switch (input.charAt(3)) {
				case 'a':
					colTo = 0;
					break;

				case 'b':
					colTo = 1;
					break;

				case 'c':
					colTo = 2;
					break;

				case 'd':
					colTo = 3;
					break;

				case 'e':
					colTo = 4;
					break;

				case 'f':
					colTo = 5;
					break;

				case 'g':
					colTo = 6;
					break;

				case 'h':
					colTo = 7;
					break;

				default:
					throw new Exception("Input is wrong, To Column");
			}
			
			rowFrom = Character.getNumericValue(input.charAt(1));
			rowTo = Character.getNumericValue(input.charAt(4));
			
			if (rowFrom < 0 || rowFrom > 8 || rowTo < 0 || rowTo > 8) {
				throw new Exception("Input is wrong, Row's");
			} else {
				rowFrom--;
				rowTo--;
			}
			
			ChessPosition from = new ChessPosition(rowFrom,colFrom);
			ChessPosition to = new ChessPosition(rowTo,colTo);

			move = new ChessMove(player,game.getBoard().get(rowFrom, colFrom),from,to, timeMilli);
		}
		
		return move;
	}


	private static void addPlayer() {
		ChessPlayerDM cpDM = ChessPlayerDM.getInstance();

		ChessPlayer playerTest = new ChessPlayer("Bacano", "teste@email.com");

		cpDM.insert(playerTest);
	}

	private static void addPlayer(ChessPlayer cp) {
		ChessPlayerDM cpDM = ChessPlayerDM.getInstance();
		cpDM.insert(cp);
	}
}
