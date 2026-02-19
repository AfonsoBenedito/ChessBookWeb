package db

import (
	"chessbookweb/chess"
	"database/sql"
)

// Game represents a chess game in the DB (flat structure)
type Game struct {
	ID                  int64
	WhitePlayerID       int64
	BlackPlayerID       int64
	WinnerPlayerID      *int64
	Finished            bool
	DateMS              int64
	OpenDateMS          *int64
	EndGameDescription  *string
	TieOfferPlayerID    *int64
	WhiteNotification   bool
	BlackNotification   bool
}

// FindGameByID retrieves a game by ID
func FindGameByID(id int64) (*Game, error) {
	row := DB.QueryRow(`
		SELECT id, white_player_id, black_player_id, winner_player_id,
		       finished, date_ms, open_date_ms, end_game_description,
		       tie_offer_player_id, white_notification, black_notification
		FROM chess_game WHERE id = ?`, id)
	return scanGame(row)
}

func scanGame(row *sql.Row) (*Game, error) {
	g := &Game{}
	var finishedInt, whiteNotifInt, blackNotifInt int
	err := row.Scan(
		&g.ID, &g.WhitePlayerID, &g.BlackPlayerID, &g.WinnerPlayerID,
		&finishedInt, &g.DateMS, &g.OpenDateMS, &g.EndGameDescription,
		&g.TieOfferPlayerID, &whiteNotifInt, &blackNotifInt,
	)
	if err == sql.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	g.Finished = finishedInt != 0
	g.WhiteNotification = whiteNotifInt != 0
	g.BlackNotification = blackNotifInt != 0
	return g, nil
}

// ListAllGames returns all games
func ListAllGames() ([]Game, error) {
	rows, err := DB.Query(`
		SELECT id, white_player_id, black_player_id, winner_player_id,
		       finished, date_ms, open_date_ms, end_game_description,
		       tie_offer_player_id, white_notification, black_notification
		FROM chess_game ORDER BY id`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanGames(rows)
}

// FindFinishedGamesByPlayerID returns finished games for a player
func FindFinishedGamesByPlayerID(playerID int64) ([]Game, error) {
	rows, err := DB.Query(`
		SELECT id, white_player_id, black_player_id, winner_player_id,
		       finished, date_ms, open_date_ms, end_game_description,
		       tie_offer_player_id, white_notification, black_notification
		FROM chess_game
		WHERE finished=1 AND (white_player_id=? OR black_player_id=?)
		ORDER BY id DESC`, playerID, playerID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanGames(rows)
}

// FindUnfinishedGamesByPlayerID returns ongoing games for a player
func FindUnfinishedGamesByPlayerID(playerID int64) ([]Game, error) {
	rows, err := DB.Query(`
		SELECT id, white_player_id, black_player_id, winner_player_id,
		       finished, date_ms, open_date_ms, end_game_description,
		       tie_offer_player_id, white_notification, black_notification
		FROM chess_game
		WHERE finished=0 AND (white_player_id=? OR black_player_id=?)
		ORDER BY id DESC`, playerID, playerID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanGames(rows)
}

// FindGamesByPlayerID returns all games (finished or not) for a player
func FindGamesByPlayerID(playerID int64) ([]Game, error) {
	rows, err := DB.Query(`
		SELECT id, white_player_id, black_player_id, winner_player_id,
		       finished, date_ms, open_date_ms, end_game_description,
		       tie_offer_player_id, white_notification, black_notification
		FROM chess_game
		WHERE white_player_id=? OR black_player_id=?
		ORDER BY id DESC`, playerID, playerID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanGames(rows)
}

func scanGames(rows *sql.Rows) ([]Game, error) {
	var games []Game
	for rows.Next() {
		g := Game{}
		var finishedInt, whiteNotifInt, blackNotifInt int
		err := rows.Scan(
			&g.ID, &g.WhitePlayerID, &g.BlackPlayerID, &g.WinnerPlayerID,
			&finishedInt, &g.DateMS, &g.OpenDateMS, &g.EndGameDescription,
			&g.TieOfferPlayerID, &whiteNotifInt, &blackNotifInt,
		)
		if err != nil {
			return nil, err
		}
		g.Finished = finishedInt != 0
		g.WhiteNotification = whiteNotifInt != 0
		g.BlackNotification = blackNotifInt != 0
		games = append(games, g)
	}
	return games, rows.Err()
}

// InsertGame creates a new game and returns its ID
func InsertGame(whiteID, blackID, dateMS int64) (int64, error) {
	res, err := DB.Exec(`
		INSERT INTO chess_game (white_player_id, black_player_id, finished, date_ms,
		                        white_notification, black_notification)
		VALUES (?, ?, 0, ?, 0, 0)`, whiteID, blackID, dateMS)
	if err != nil {
		return 0, err
	}
	return res.LastInsertId()
}

// UpdateGame saves game state changes
func UpdateGame(g *Game) error {
	finishedInt := 0
	if g.Finished {
		finishedInt = 1
	}
	whiteNotifInt := 0
	if g.WhiteNotification {
		whiteNotifInt = 1
	}
	blackNotifInt := 0
	if g.BlackNotification {
		blackNotifInt = 1
	}
	_, err := DB.Exec(`
		UPDATE chess_game
		SET winner_player_id=?, finished=?, open_date_ms=?, end_game_description=?,
		    tie_offer_player_id=?, white_notification=?, black_notification=?
		WHERE id=?`,
		g.WinnerPlayerID, finishedInt, g.OpenDateMS, g.EndGameDescription,
		g.TieOfferPlayerID, whiteNotifInt, blackNotifInt,
		g.ID)
	return err
}

// DeleteGame removes a game and its moves by ID
func DeleteGame(id int64) error {
	if _, err := DB.Exec("DELETE FROM chess_move WHERE game_id=?", id); err != nil {
		return err
	}
	_, err := DB.Exec("DELETE FROM chess_game WHERE id=?", id)
	return err
}

// InsertMove saves a move record
func InsertMove(gameID int64, mv *chess.Move) error {
	var promotion *int
	if mv.Promotion != nil {
		p := int(*mv.Promotion)
		promotion = &p
	}
	_, err := DB.Exec(`
		INSERT INTO chess_move (game_id, player_id, move_order, from_row, from_col,
		                        to_row, to_col, piece_type, piece_color, promotion, time_milli)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
		gameID, mv.PlayerID, mv.MoveOrder,
		mv.From.Row, mv.From.Col, mv.To.Row, mv.To.Col,
		int(mv.Piece.Kind), int(mv.Piece.Color),
		promotion, mv.TimeMilli)
	return err
}

// LoadMoves retrieves all moves for a game in order
func LoadMoves(gameID int64) ([]chess.Move, error) {
	rows, err := DB.Query(`
		SELECT player_id, move_order, from_row, from_col, to_row, to_col,
		       piece_type, piece_color, promotion, time_milli
		FROM chess_move WHERE game_id=?
		ORDER BY move_order ASC`, gameID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var moves []chess.Move
	for rows.Next() {
		var mv chess.Move
		var pieceType, pieceColor int
		var promotion *int
		err := rows.Scan(
			&mv.PlayerID, &mv.MoveOrder,
			&mv.From.Row, &mv.From.Col, &mv.To.Row, &mv.To.Col,
			&pieceType, &pieceColor, &promotion, &mv.TimeMilli,
		)
		if err != nil {
			return nil, err
		}
		mv.Piece = chess.Piece{Kind: chess.PieceKind(pieceType), Color: chess.Color(pieceColor)}
		if promotion != nil {
			pk := chess.PieceKind(*promotion)
			mv.Promotion = &pk
		}
		moves = append(moves, mv)
	}
	return moves, rows.Err()
}
