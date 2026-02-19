package db

import (
	"database/sql"
	"fmt"
)

// Player represents a chess player in the DB
type Player struct {
	ID    int64
	Name  string
	Email string
}

// FindPlayerByEmail finds a player by email
func FindPlayerByEmail(email string) (*Player, error) {
	row := DB.QueryRow("SELECT id, name, email FROM chess_player WHERE email = ?", email)
	p := &Player{}
	err := row.Scan(&p.ID, &p.Name, &p.Email)
	if err == sql.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	return p, nil
}

// FindPlayerByID finds a player by ID
func FindPlayerByID(id int64) (*Player, error) {
	row := DB.QueryRow("SELECT id, name, email FROM chess_player WHERE id = ?", id)
	p := &Player{}
	err := row.Scan(&p.ID, &p.Name, &p.Email)
	if err == sql.ErrNoRows {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	return p, nil
}

// ListPlayers returns all players
func ListPlayers() ([]Player, error) {
	rows, err := DB.Query("SELECT id, name, email FROM chess_player ORDER BY id")
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var players []Player
	for rows.Next() {
		var p Player
		if err := rows.Scan(&p.ID, &p.Name, &p.Email); err != nil {
			return nil, err
		}
		players = append(players, p)
	}
	return players, rows.Err()
}

// InsertPlayer creates a new player and returns their ID
func InsertPlayer(name, email string) (int64, error) {
	res, err := DB.Exec("INSERT INTO chess_player (name, email) VALUES (?, ?)", name, email)
	if err != nil {
		return 0, err
	}
	return res.LastInsertId()
}

// DeletePlayer removes a player by ID
func DeletePlayer(id int64) error {
	_, err := DB.Exec("DELETE FROM chess_player WHERE id = ?", id)
	return err
}

// FindPlayersLikeName finds players whose name contains the given string
func FindPlayersLikeName(name string) ([]Player, error) {
	rows, err := DB.Query("SELECT id, name, email FROM chess_player WHERE name LIKE ? ORDER BY id", "%"+name+"%")
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var players []Player
	for rows.Next() {
		var p Player
		if err := rows.Scan(&p.ID, &p.Name, &p.Email); err != nil {
			return nil, err
		}
		players = append(players, p)
	}
	return players, rows.Err()
}

// PlayerStats holds win/loss/draw counts for a player
type PlayerStats struct {
	Wins   int
	Draws  int
	Losses int
	Total  int
}

// GetPlayerStats computes win/loss/draw for a player
func GetPlayerStats(playerID int64) (PlayerStats, error) {
	var s PlayerStats
	// Wins: winner_player_id = playerID AND finished = 1
	err := DB.QueryRow(
		"SELECT COUNT(*) FROM chess_game WHERE finished=1 AND winner_player_id=?", playerID,
	).Scan(&s.Wins)
	if err != nil {
		return s, fmt.Errorf("counting wins: %w", err)
	}
	// Draws: finished=1 AND winner_player_id IS NULL AND (white_player_id=? OR black_player_id=?)
	err = DB.QueryRow(
		"SELECT COUNT(*) FROM chess_game WHERE finished=1 AND winner_player_id IS NULL AND (white_player_id=? OR black_player_id=?)",
		playerID, playerID,
	).Scan(&s.Draws)
	if err != nil {
		return s, fmt.Errorf("counting draws: %w", err)
	}
	// Total finished
	err = DB.QueryRow(
		"SELECT COUNT(*) FROM chess_game WHERE finished=1 AND (white_player_id=? OR black_player_id=?)",
		playerID, playerID,
	).Scan(&s.Total)
	if err != nil {
		return s, fmt.Errorf("counting total: %w", err)
	}
	s.Losses = s.Total - s.Wins - s.Draws
	return s, nil
}
