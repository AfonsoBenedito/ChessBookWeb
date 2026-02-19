// cmd/seed populates the database with mock players and games for local testing.
// Usage: make seed   (or: DB_PATH=./data/chess.db go run ./cmd/seed)
package main

import (
	"chessbookweb/chess"
	"chessbookweb/db"
	"log"
	"os"
	"time"
)

func must(id int64, err error) int64 {
	if err != nil {
		log.Fatalf("seed: %v", err)
	}
	return id
}

// applyMove looks up the piece at (fr,fc), validates the move against the board,
// inserts it into the DB, and advances the board state.
func applyMove(gameID, playerID int64, board *chess.Board, fr, fc, tr, tc, order int) {
	p := board.Get(fr, fc)
	if p == nil {
		log.Fatalf("no piece at (%d,%d) for move order %d", fr, fc, order)
	}
	mv := &chess.Move{
		From:      chess.Position{Row: fr, Col: fc},
		To:        chess.Position{Row: tr, Col: tc},
		Piece:     *p,
		PlayerID:  playerID,
		TimeMilli: 4000 + order*500,
		MoveOrder: order,
	}
	if err := board.Update(mv); err != nil {
		log.Fatalf("invalid move %d (%d,%d)->(%d,%d): %v", order, fr, fc, tr, tc, err)
	}
	if err := db.InsertMove(gameID, mv); err != nil {
		log.Fatalf("insert move %d: %v", order, err)
	}
}

func finishGame(g *db.Game, winnerID *int64, desc string) {
	g.Finished = true
	g.WinnerPlayerID = winnerID
	g.EndGameDescription = &desc
	g.WhiteNotification = true
	g.BlackNotification = true
	if err := db.UpdateGame(g); err != nil {
		log.Fatalf("update game %d: %v", g.ID, err)
	}
}

func main() {
	dbPath := os.Getenv("DB_PATH")
	if dbPath == "" {
		dbPath = "./data/chess.db"
	}
	if err := db.Init(dbPath); err != nil {
		log.Fatalf("db init: %v", err)
	}

	now := time.Now().UnixMilli()
	day := int64(86_400_000)

	// ── Players ──────────────────────────────────────────────────────────────
	aliceID  := must(db.InsertPlayer("Alice",  "alice@chess.local"))
	bobID    := must(db.InsertPlayer("Bob",    "bob@chess.local"))
	carlosID := must(db.InsertPlayer("Carlos", "carlos@chess.local"))
	log.Printf("Created players: Alice=%d  Bob=%d  Carlos=%d", aliceID, bobID, carlosID)

	// ── Game 1: Alice (white) vs Bob (black) — ongoing, Italian Opening ──────
	// e4 e5 Nf3 Nc6 Bc4 — Bob to move next
	g1 := must(db.InsertGame(aliceID, bobID, now-2*day))
	b1 := chess.NewBoard()
	applyMove(g1, aliceID, b1, 1, 4, 3, 4, 0) // e2-e4
	applyMove(g1, bobID,   b1, 6, 4, 4, 4, 1) // e7-e5
	applyMove(g1, aliceID, b1, 0, 6, 2, 5, 2) // g1-f3  (Nf3)
	applyMove(g1, bobID,   b1, 7, 1, 5, 2, 3) // b8-c6  (Nc6)
	applyMove(g1, aliceID, b1, 0, 5, 3, 2, 4) // f1-c4  (Bc4 — Italian!)
	log.Printf("Game 1 (id=%d): Alice vs Bob — 5 moves, ongoing (Bob to move)", g1)

	// ── Game 2: Bob (white) vs Carlos (black) — finished, Bob resigned ───────
	// d4 d5 Nf3 Nc6 e3 e6 — then Bob resigns
	g2 := must(db.InsertGame(bobID, carlosID, now-5*day))
	b2 := chess.NewBoard()
	applyMove(g2, bobID,    b2, 1, 3, 3, 3, 0) // d2-d4
	applyMove(g2, carlosID, b2, 6, 3, 4, 3, 1) // d7-d5
	applyMove(g2, bobID,    b2, 0, 6, 2, 5, 2) // g1-f3  (Nf3)
	applyMove(g2, carlosID, b2, 7, 1, 5, 2, 3) // b8-c6  (Nc6)
	applyMove(g2, bobID,    b2, 1, 4, 2, 4, 4) // e2-e3
	applyMove(g2, carlosID, b2, 6, 4, 5, 4, 5) // e7-e6
	g2rec, _ := db.FindGameByID(g2)
	finishGame(g2rec, &carlosID, "White Resigned")
	log.Printf("Game 2 (id=%d): Bob vs Carlos — 6 moves, Carlos wins (Bob resigned)", g2)

	// ── Game 3: Carlos (white) vs Alice (black) — draw by agreement ──────────
	// Sicilian-ish: e4 c5 Nf3 Nc6 d3 d6 — then draw
	g3 := must(db.InsertGame(carlosID, aliceID, now-10*day))
	b3 := chess.NewBoard()
	applyMove(g3, carlosID, b3, 1, 4, 3, 4, 0) // e2-e4
	applyMove(g3, aliceID,  b3, 6, 2, 4, 2, 1) // c7-c5  (Sicilian)
	applyMove(g3, carlosID, b3, 0, 6, 2, 5, 2) // g1-f3  (Nf3)
	applyMove(g3, aliceID,  b3, 7, 1, 5, 2, 3) // b8-c6  (Nc6)
	applyMove(g3, carlosID, b3, 1, 3, 2, 3, 4) // d2-d3
	applyMove(g3, aliceID,  b3, 6, 3, 5, 3, 5) // d7-d6
	g3rec, _ := db.FindGameByID(g3)
	finishGame(g3rec, nil, "Players agreed a Draw")
	log.Printf("Game 3 (id=%d): Carlos vs Alice — 6 moves, draw", g3)

	// ── Game 4: Alice (white) vs Carlos (black) — just started ───────────────
	g4 := must(db.InsertGame(aliceID, carlosID, now-1*day))
	log.Printf("Game 4 (id=%d): Alice vs Carlos — no moves yet (Alice to move)", g4)

	log.Println()
	log.Println("Seed complete!  Log in with (name / email):")
	log.Println("  Alice  / alice@chess.local")
	log.Println("  Bob    / bob@chess.local")
	log.Println("  Carlos / carlos@chess.local")
}
