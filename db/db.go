package db

import (
	"database/sql"
	"fmt"
	"os"
	"path/filepath"

	_ "modernc.org/sqlite"
)

var DB *sql.DB

// Init opens the SQLite database and runs migrations
func Init(dbPath string) error {
	if err := os.MkdirAll(filepath.Dir(dbPath), 0755); err != nil {
		return fmt.Errorf("creating db dir: %w", err)
	}

	db, err := sql.Open("sqlite", dbPath)
	if err != nil {
		return fmt.Errorf("opening db: %w", err)
	}
	db.SetMaxOpenConns(1) // SQLite WAL mode: 1 writer at a time

	if _, err := db.Exec("PRAGMA journal_mode=WAL"); err != nil {
		return fmt.Errorf("setting WAL mode: %w", err)
	}
	if _, err := db.Exec("PRAGMA foreign_keys=ON"); err != nil {
		return fmt.Errorf("enabling foreign keys: %w", err)
	}

	DB = db
	return migrate(db)
}

func migrate(db *sql.DB) error {
	schema := `
CREATE TABLE IF NOT EXISTS chess_player (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS chess_game (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    white_player_id      INTEGER NOT NULL REFERENCES chess_player(id),
    black_player_id      INTEGER NOT NULL REFERENCES chess_player(id),
    winner_player_id     INTEGER REFERENCES chess_player(id),
    finished             INTEGER NOT NULL DEFAULT 0,
    date_ms              INTEGER,
    open_date_ms         INTEGER,
    end_game_description TEXT,
    tie_offer_player_id  INTEGER REFERENCES chess_player(id),
    white_notification   INTEGER NOT NULL DEFAULT 0,
    black_notification   INTEGER NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS chess_move (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    game_id      INTEGER NOT NULL REFERENCES chess_game(id),
    player_id    INTEGER NOT NULL REFERENCES chess_player(id),
    move_order   INTEGER NOT NULL,
    from_row     INTEGER NOT NULL,
    from_col     INTEGER NOT NULL,
    to_row       INTEGER NOT NULL,
    to_col       INTEGER NOT NULL,
    piece_type   INTEGER NOT NULL,
    piece_color  INTEGER NOT NULL,
    promotion    INTEGER,
    time_milli   INTEGER NOT NULL
);
`
	_, err := db.Exec(schema)
	return err
}
