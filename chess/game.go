package chess

import (
	"fmt"
	"strings"
)

// Game orchestrates a chess game
type Game struct {
	ID                 int64
	White              Player
	Black              Player
	Winner             *Player
	WinnerPlayerID     *int64
	Moves              []Move
	Finished           bool
	TieOfferPlayerID   *int64
	WhiteNotification  bool
	BlackNotification  bool
	EndGameDescription string
	DateMS             int64
	OpenDateMS         *int64
	Board              *Board
}

// Player is a lightweight player reference used inside the game
type Player struct {
	ID    int64
	Name  string
	Email string
}

// NewGame creates a new game given white and black players
func NewGame(white, black Player, dateMS int64) *Game {
	return &Game{
		White:  white,
		Black:  black,
		DateMS: dateMS,
		Board:  NewBoard(),
	}
}

// LoadBoard reconstructs the board from the stored moves
func (g *Game) LoadBoard() error {
	b, err := NewBoardFromMoves(g.Moves)
	if err != nil {
		return err
	}
	g.Board = b
	return nil
}

// GetPlayerTurn returns the player whose turn it is
func (g *Game) GetPlayerTurn() Player {
	if g.Board == nil {
		return g.White
	}
	if g.Board.Turn == White {
		return g.White
	}
	return g.Black
}

// AddMove validates and applies a move
func (g *Game) AddMove(mv *Move) error {
	if err := g.Board.Update(mv); err != nil {
		return err
	}
	g.Moves = append(g.Moves, *mv)

	if g.Board.Finished {
		g.Finished = true
		if g.Board.Winner != nil {
			if *g.Board.Winner == White {
				g.Winner = &g.White
				g.WinnerPlayerID = &g.White.ID
			} else {
				g.Winner = &g.Black
				g.WinnerPlayerID = &g.Black.ID
			}
		}
		g.EndGameDescription = g.Board.BoardEndDescription
	}
	g.OpenDateMS = nil
	g.TieOfferPlayerID = nil
	return nil
}

// WhiteResign marks white as resigned
func (g *Game) WhiteResign() {
	g.Finished = true
	g.Winner = &g.Black
	g.WinnerPlayerID = &g.Black.ID
	w := Black
	g.Board.FinishGame(&w)
	g.EndGameDescription = "White Resigned"
}

// BlackResign marks black as resigned
func (g *Game) BlackResign() {
	g.Finished = true
	g.Winner = &g.White
	g.WinnerPlayerID = &g.White.ID
	w := White
	g.Board.FinishGame(&w)
	g.EndGameDescription = "Black Resigned"
}

// Stalemate marks the game as a draw
func (g *Game) Stalemate() {
	g.Finished = true
	g.Winner = nil
	g.WinnerPlayerID = nil
	g.Board.FinishGame(nil)
	g.EndGameDescription = "Players agreed a Draw"
}

// OfferDraw records a draw offer from a player
func (g *Game) OfferDraw(playerID int64) {
	g.TieOfferPlayerID = &playerID
}

// AcceptDraw accepts the draw offer
func (g *Game) AcceptDraw() {
	g.Stalemate()
}

// RefuseDraw clears the draw offer
func (g *Game) RefuseDraw() {
	g.TieOfferPlayerID = nil
}

// GetPossibleMoves returns legal destination square strings for the given square
func (g *Game) GetPossibleMoves(square string) []string {
	row, col := SquareToPos(square)
	if row < 0 {
		return nil
	}
	pos := Position{row, col}
	moves := g.Board.GetPossibleMoves(pos)
	var result []string
	for _, m := range moves {
		result = append(result, PosToSquare(m.To.Row, m.To.Col))
	}
	return result
}

// VerifyInput validates a move input string like "a1 b2" or "a7 a8 QUEEN"
func VerifyInput(input string) bool {
	parts := strings.Fields(input)
	if len(parts) < 2 || len(parts) > 3 {
		return false
	}
	if !validSquare(parts[0]) || !validSquare(parts[1]) {
		return false
	}
	if len(parts) == 3 {
		p := parts[2]
		if p != "QUEEN" && p != "ROOK" && p != "BISHOP" && p != "KNIGHT" {
			return false
		}
	}
	return true
}

func validSquare(s string) bool {
	if len(s) != 2 {
		return false
	}
	return s[0] >= 'a' && s[0] <= 'h' && s[1] >= '1' && s[1] <= '8'
}

// ConvertInputToMove parses a move string into a Move struct
func (g *Game) ConvertInputToMove(input string, playerID int64, timeMilli int, moveOrder int) (*Move, error) {
	parts := strings.Fields(input)
	if len(parts) < 2 {
		return nil, fmt.Errorf("invalid input")
	}
	fromRow, fromCol := SquareToPos(parts[0])
	toRow, toCol := SquareToPos(parts[1])
	if fromRow < 0 || toRow < 0 {
		return nil, fmt.Errorf("invalid square")
	}
	piece := g.Board.Get(fromRow, fromCol)
	if piece == nil {
		return nil, fmt.Errorf("no piece at %s", parts[0])
	}

	mv := &Move{
		From:      Position{fromRow, fromCol},
		To:        Position{toRow, toCol},
		Piece:     *piece,
		PlayerID:  playerID,
		TimeMilli: timeMilli,
		MoveOrder: moveOrder,
	}

	if len(parts) == 3 {
		var pk PieceKind
		switch parts[2] {
		case "QUEEN":
			pk = Queen
		case "ROOK":
			pk = Rook
		case "BISHOP":
			pk = Bishop
		case "KNIGHT":
			pk = Knight
		default:
			return nil, fmt.Errorf("invalid promotion piece")
		}
		mv.Promotion = &pk
	}
	return mv, nil
}
