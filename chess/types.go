package chess

// Color represents piece color
type Color int

const (
	White Color = 0
	Black Color = 1
)

// PieceKind represents the type of chess piece
type PieceKind int

const (
	King   PieceKind = 0
	Queen  PieceKind = 1
	Rook   PieceKind = 2
	Bishop PieceKind = 3
	Knight PieceKind = 4
	Pawn   PieceKind = 5
)

func (p PieceKind) String() string {
	switch p {
	case King:
		return "KING"
	case Queen:
		return "QUEEN"
	case Rook:
		return "ROOK"
	case Bishop:
		return "BISHOP"
	case Knight:
		return "KNIGHT"
	case Pawn:
		return "PAWN"
	}
	return ""
}

// Piece represents a chess piece on the board
type Piece struct {
	Color    Color
	Kind     PieceKind
	NumMoves int // transient: not stored in DB, tracked per board reconstruction
}

func (p *Piece) ToHTML() string {
	if p.Color == White {
		switch p.Kind {
		case Pawn:
			return "\u2659" // ♙
		case Rook:
			return "\u2656" // ♖
		case Knight:
			return "\u2658" // ♘
		case Bishop:
			return "\u2657" // ♗
		case Queen:
			return "\u2655" // ♕
		case King:
			return "\u2654" // ♔
		}
	} else {
		switch p.Kind {
		case Pawn:
			return "\u265F" // ♟
		case Rook:
			return "\u265C" // ♜
		case Knight:
			return "\u265E" // ♞
		case Bishop:
			return "\u265D" // ♝
		case Queen:
			return "\u265B" // ♛
		case King:
			return "\u265A" // ♚
		}
	}
	return ""
}

// Position represents a board position
type Position struct {
	Row int
	Col int
}

// ColToFile converts a column index to chess file letter (0='a', 7='h')
func ColToFile(col int) byte {
	return byte('a' + col)
}

// FileToCol converts a chess file letter to column index
func FileToCol(file byte) int {
	return int(file - 'a')
}

// Move represents a chess move
type Move struct {
	From      Position
	To        Position
	Piece     Piece     // piece being moved (color+kind+numMoves at time of move)
	Promotion *PieceKind // nil unless pawn promotion
	PlayerID  int64
	TimeMilli int
	MoveOrder int
}

// PosToSquare converts row, col to chess square notation (e.g., 0,0 -> "a1")
func PosToSquare(row, col int) string {
	return string([]byte{byte('a' + col), byte('1' + row)})
}

// SquareToPos converts chess square notation to row, col (returns -1,-1 on error)
func SquareToPos(square string) (int, int) {
	if len(square) != 2 {
		return -1, -1
	}
	col := int(square[0] - 'a')
	row := int(square[1] - '1')
	if col < 0 || col > 7 || row < 0 || row > 7 {
		return -1, -1
	}
	return row, col
}
