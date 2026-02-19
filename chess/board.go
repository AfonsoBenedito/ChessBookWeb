package chess

import "fmt"

type direction int

const (
	dirN  direction = 0
	dirS  direction = 1
	dirE  direction = 2
	dirW  direction = 3
	dirNE direction = 4
	dirNW direction = 5
	dirSE direction = 6
	dirSW direction = 7
)

// Board represents the full chess board state
type Board struct {
	pieces              [8][8]*Piece
	Turn                Color
	lastMove            *Move
	doEnpassant         bool
	numberPiecesCreated [10]int // [wPawns,wRooks,wKnights,wBishops,wQueens,bPawns,bRooks,bKnights,bBishops,bQueens]
	Finished            bool
	Winner              *Color
	BoardEndDescription string
}

// NewBoard creates a new board in starting position
func NewBoard() *Board {
	b := &Board{}
	b.Turn = White

	// White pieces at row 0 (rank 1)
	b.pieces[0][0] = &Piece{White, Rook, 0}
	b.pieces[0][1] = &Piece{White, Knight, 0}
	b.pieces[0][2] = &Piece{White, Bishop, 0}
	b.pieces[0][3] = &Piece{White, Queen, 0}
	b.pieces[0][4] = &Piece{White, King, 0}
	b.pieces[0][5] = &Piece{White, Bishop, 0}
	b.pieces[0][6] = &Piece{White, Knight, 0}
	b.pieces[0][7] = &Piece{White, Rook, 0}
	for j := 0; j < 8; j++ {
		b.pieces[1][j] = &Piece{White, Pawn, 0}
	}

	// Black pieces at row 7 (rank 8)
	b.pieces[7][0] = &Piece{Black, Rook, 0}
	b.pieces[7][1] = &Piece{Black, Knight, 0}
	b.pieces[7][2] = &Piece{Black, Bishop, 0}
	b.pieces[7][3] = &Piece{Black, Queen, 0}
	b.pieces[7][4] = &Piece{Black, King, 0}
	b.pieces[7][5] = &Piece{Black, Bishop, 0}
	b.pieces[7][6] = &Piece{Black, Knight, 0}
	b.pieces[7][7] = &Piece{Black, Rook, 0}
	for j := 0; j < 8; j++ {
		b.pieces[6][j] = &Piece{Black, Pawn, 0}
	}

	b.numberPiecesCreated = [10]int{8, 2, 2, 2, 1, 8, 2, 2, 2, 1}
	return b
}

// NewBoardFromMoves replays moves to reconstruct board state
func NewBoardFromMoves(moves []Move) (*Board, error) {
	b := NewBoard()
	for i := range moves {
		if err := b.Update(&moves[i]); err != nil {
			return nil, fmt.Errorf("error replaying move %d: %w", i, err)
		}
	}
	return b, nil
}

// Get returns the piece at (row, col), or nil
func (b *Board) Get(row, col int) *Piece {
	return b.pieces[row][col]
}

// GetPieceHTML returns the HTML entity for the piece at (row, col), or " "
func (b *Board) GetPieceHTML(row, col int) string {
	p := b.pieces[row][col]
	if p == nil {
		return " "
	}
	return p.ToHTML()
}

// GetEatenPieces returns [wPawns,wRooks,wKnights,wBishops,wQueens,bPawns,bRooks,bKnights,bBishops,bQueens] eaten
func (b *Board) GetEatenPieces() [10]int {
	var onBoard [10]int
	for i := 0; i < 8; i++ {
		for j := 0; j < 8; j++ {
			p := b.pieces[i][j]
			if p == nil {
				continue
			}
			if p.Color == White {
				switch p.Kind {
				case Pawn:
					onBoard[0]++
				case Rook:
					onBoard[1]++
				case Knight:
					onBoard[2]++
				case Bishop:
					onBoard[3]++
				case Queen:
					onBoard[4]++
				}
			} else {
				switch p.Kind {
				case Pawn:
					onBoard[5]++
				case Rook:
					onBoard[6]++
				case Knight:
					onBoard[7]++
				case Bishop:
					onBoard[8]++
				case Queen:
					onBoard[9]++
				}
			}
		}
	}
	var eaten [10]int
	for i := 0; i < 10; i++ {
		eaten[i] = b.numberPiecesCreated[i] - onBoard[i]
	}
	return eaten
}

func (b *Board) verifyPlayerPiece(piece *Piece) bool {
	return piece != nil && piece.Color == b.Turn
}

func (b *Board) verifyToPosition(from, to Position) bool {
	if from.Row == to.Row && from.Col == to.Col {
		return false
	}
	if from.Row >= 0 && from.Row <= 7 && from.Col >= 0 && from.Col <= 7 {
		if to.Row >= 0 && to.Row <= 7 && to.Col >= 0 && to.Col <= 7 {
			return true
		}
	}
	return false
}

func (b *Board) findKingPosition(pieces [8][8]*Piece, color Color) *Position {
	for i := 0; i <= 7; i++ {
		for j := 0; j <= 7; j++ {
			p := pieces[i][j]
			if p != nil && p.Color == color && p.Kind == King {
				pos := Position{i, j}
				return &pos
			}
		}
	}
	return nil
}

func (b *Board) kingInCheck(pieces [8][8]*Piece) bool {
	kp := b.findKingPosition(pieces, b.Turn)
	if kp == nil {
		return false
	}
	i, j := kp.Row, kp.Col

	// Straight lines: rook/queen
	for _, dir := range []direction{dirN, dirS, dirE, dirW} {
		pos := b.nextPiece(pieces, i, j, dir)
		if pos[0] != -1 {
			p := pieces[pos[0]][pos[1]]
			if p.Color != b.Turn && (p.Kind == Rook || p.Kind == Queen) {
				return true
			}
		}
	}
	// Diagonals: bishop/queen
	for _, dir := range []direction{dirNE, dirNW, dirSE, dirSW} {
		pos := b.nextPiece(pieces, i, j, dir)
		if pos[0] != -1 {
			p := pieces[pos[0]][pos[1]]
			if p.Color != b.Turn && (p.Kind == Bishop || p.Kind == Queen) {
				return true
			}
		}
	}
	// Knights
	for _, km := range [][2]int{{-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {-2, -1}, {-2, 1}, {2, -1}, {2, 1}} {
		ni, nj := i+km[0], j+km[1]
		if ni >= 0 && ni <= 7 && nj >= 0 && nj <= 7 {
			p := pieces[ni][nj]
			if p != nil && p.Kind == Knight && p.Color != b.Turn {
				return true
			}
		}
	}
	// Pawns
	if b.Turn == White {
		if i < 7 {
			if j < 7 && pieces[i+1][j+1] != nil && pieces[i+1][j+1].Kind == Pawn && pieces[i+1][j+1].Color == Black {
				return true
			}
			if j > 0 && pieces[i+1][j-1] != nil && pieces[i+1][j-1].Kind == Pawn && pieces[i+1][j-1].Color == Black {
				return true
			}
		}
	} else {
		if i > 0 {
			if j < 7 && pieces[i-1][j+1] != nil && pieces[i-1][j+1].Kind == Pawn && pieces[i-1][j+1].Color == White {
				return true
			}
			if j > 0 && pieces[i-1][j-1] != nil && pieces[i-1][j-1].Kind == Pawn && pieces[i-1][j-1].Color == White {
				return true
			}
		}
	}
	return false
}

func (b *Board) nextPiece(pieces [8][8]*Piece, row, col int, dir direction) [2]int {
	switch dir {
	case dirN:
		for i := row + 1; i <= 7; i++ {
			if pieces[i][col] != nil {
				return [2]int{i, col}
			}
		}
	case dirS:
		for i := row - 1; i >= 0; i-- {
			if pieces[i][col] != nil {
				return [2]int{i, col}
			}
		}
	case dirE:
		for j := col + 1; j <= 7; j++ {
			if pieces[row][j] != nil {
				return [2]int{row, j}
			}
		}
	case dirW:
		for j := col - 1; j >= 0; j-- {
			if pieces[row][j] != nil {
				return [2]int{row, j}
			}
		}
	case dirNE:
		for di := 1; row+di <= 7 && col+di <= 7; di++ {
			if pieces[row+di][col+di] != nil {
				return [2]int{row + di, col + di}
			}
		}
	case dirSW:
		for di := 1; row-di >= 0 && col-di >= 0; di++ {
			if pieces[row-di][col-di] != nil {
				return [2]int{row - di, col - di}
			}
		}
	case dirNW:
		for di := 1; row+di <= 7 && col-di >= 0; di++ {
			if pieces[row+di][col-di] != nil {
				return [2]int{row + di, col - di}
			}
		}
	case dirSE:
		for di := 1; row-di >= 0 && col+di <= 7; di++ {
			if pieces[row-di][col+di] != nil {
				return [2]int{row - di, col + di}
			}
		}
	}
	return [2]int{-1, -1}
}

// findMoveDirection returns the direction of a move, or error if same spot, or -1 if non-cardinal/diagonal
func (b *Board) findMoveDirection(from, to Position) (direction, error) {
	dr := to.Row - from.Row
	dc := to.Col - from.Col
	if dr == 0 && dc == 0 {
		return -1, fmt.Errorf("Can't stay in the same spot")
	}
	if dr == 0 {
		if dc > 0 {
			return dirE, nil
		}
		return dirW, nil
	}
	if dc == 0 {
		if dr > 0 {
			return dirN, nil
		}
		return dirS, nil
	}
	// diagonal?
	ratio := float64(dc) / float64(dr)
	if dr > 0 {
		if ratio == 1.0 {
			return dirNE, nil
		}
		if ratio == -1.0 {
			return dirNW, nil
		}
		return -1, nil
	}
	// dr < 0
	if ratio == 1.0 {
		return dirSW, nil
	}
	if ratio == -1.0 {
		return dirSE, nil
	}
	return -1, nil
}

// testApplyMove simulates a move and checks if own king would be in check
// Replicates Java behavior: for en passant, doEnpassant must already be set
func (b *Board) testApplyMove(from, to Position, piece *Piece, promotion *PieceKind) bool {
	var testBoard [8][8]*Piece
	for i := 0; i < 8; i++ {
		for j := 0; j < 8; j++ {
			testBoard[i][j] = b.pieces[i][j]
		}
	}
	if b.Turn == White {
		if piece.Kind == Pawn && to.Row == 7 && promotion != nil {
			testBoard[to.Row][to.Col] = &Piece{White, *promotion, 0}
			testBoard[from.Row][from.Col] = nil
		} else if piece.Kind == Pawn && b.doEnpassant {
			testBoard[to.Row][to.Col] = testBoard[from.Row][from.Col]
			testBoard[from.Row][from.Col] = nil
			testBoard[to.Row-1][to.Col] = nil
		} else {
			testBoard[to.Row][to.Col] = testBoard[from.Row][from.Col]
			testBoard[from.Row][from.Col] = nil
		}
	} else {
		if piece.Kind == Pawn && to.Row == 0 && promotion != nil {
			testBoard[to.Row][to.Col] = &Piece{Black, *promotion, 0}
			testBoard[from.Row][from.Col] = nil
		} else if piece.Kind == Pawn && b.doEnpassant {
			testBoard[to.Row][to.Col] = testBoard[from.Row][from.Col]
			testBoard[from.Row][from.Col] = nil
			testBoard[to.Row+1][to.Col] = nil
		} else {
			testBoard[to.Row][to.Col] = testBoard[from.Row][from.Col]
			testBoard[from.Row][from.Col] = nil
		}
	}
	return !b.kingInCheck(testBoard)
}

// Update validates and applies a move
func (b *Board) Update(move *Move) error {
	if b.Finished {
		if b.Winner != nil {
			if *b.Winner == White {
				return fmt.Errorf("Game is already over, White won")
			}
			return fmt.Errorf("Game is already over, Black won")
		}
		return fmt.Errorf("Game is already over, Stalemate")
	}

	piece := b.pieces[move.From.Row][move.From.Col]
	if !b.verifyPlayerPiece(piece) {
		return fmt.Errorf("You don't have access to that square")
	}
	// Verify piece matches stored info
	move.Piece = *piece

	if !b.verifyToPosition(move.From, move.To) {
		return fmt.Errorf("Move out of bounds")
	}

	var testResult bool
	var err error

	switch piece.Kind {
	case Rook:
		testResult, err = b.testRookMove(move)
	case Bishop:
		testResult, err = b.testBishopMove(move)
	case Queen:
		testResult, err = b.testQueenMove(move)
	case King:
		testResult, err = b.testKingMove(move)
	case Pawn:
		testResult, err = b.testPawnMove(move)
	case Knight:
		testResult, err = b.testKnightMove(move)
	}

	if err != nil {
		return err
	}
	if !testResult {
		return fmt.Errorf("KING will be in 'CHECK'")
	}

	if err := b.applyMove(move); err != nil {
		return err
	}
	b.lastMove = move
	if b.Turn == White {
		b.Turn = Black
	} else {
		b.Turn = White
	}

	if b.noMoves(b.pieces) {
		b.Finished = true
		if b.kingInCheck(b.pieces) {
			if b.Turn == White {
				w := Black
				b.Winner = &w
			} else {
				w := White
				b.Winner = &w
			}
			b.BoardEndDescription = "Checkmate"
		} else {
			b.BoardEndDescription = "There was a Stalemate"
		}
	}
	return nil
}

func (b *Board) testRookMove(move *Move) (bool, error) {
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	if dir != dirN && dir != dirS && dir != dirE && dir != dirW {
		return false, fmt.Errorf("ROOK doesn't move that way")
	}
	np := b.nextPiece(b.pieces, move.From.Row, move.From.Col, dir)
	if np[0] == -1 {
		return b.testApplyMove(move.From, move.To, &move.Piece, move.Promotion), nil
	}
	return b.testSlidingMove(move, dir, np)
}

func (b *Board) testBishopMove(move *Move) (bool, error) {
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	if dir != dirNE && dir != dirSW && dir != dirNW && dir != dirSE {
		return false, fmt.Errorf("BISHOP doesn't move that way")
	}
	np := b.nextPiece(b.pieces, move.From.Row, move.From.Col, dir)
	if np[0] == -1 {
		return b.testApplyMove(move.From, move.To, &move.Piece, move.Promotion), nil
	}
	return b.testSlidingMove(move, dir, np)
}

func (b *Board) testQueenMove(move *Move) (bool, error) {
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	if dir == -1 {
		return false, fmt.Errorf("QUEEN doesn't move that way")
	}
	np := b.nextPiece(b.pieces, move.From.Row, move.From.Col, dir)
	if np[0] == -1 {
		return b.testApplyMove(move.From, move.To, &move.Piece, move.Promotion), nil
	}
	return b.testSlidingMove(move, dir, np)
}

// testSlidingMove handles the common logic for rook/bishop/queen moves
func (b *Board) testSlidingMove(move *Move, dir direction, np [2]int) (bool, error) {
	to := move.To
	// For straight moves, compare rows or cols
	var toCoord, npCoord int
	switch dir {
	case dirN:
		toCoord, npCoord = to.Row, np[0]
		if toCoord < npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord > npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirS:
		toCoord, npCoord = to.Row, np[0]
		if toCoord > npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord < npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirE:
		toCoord, npCoord = to.Col, np[1]
		if toCoord < npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord > npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirW:
		toCoord, npCoord = to.Col, np[1]
		if toCoord > npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord < npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirNE:
		toCoord, npCoord = to.Row, np[0]
		if toCoord < npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord > npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirSW:
		toCoord, npCoord = to.Row, np[0]
		if toCoord > npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord < npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirNW:
		toCoord, npCoord = to.Col, np[1]
		if toCoord > npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord < npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	case dirSE:
		toCoord, npCoord = to.Col, np[1]
		if toCoord < npCoord {
			return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
		} else if toCoord > npCoord {
			return false, fmt.Errorf("You have a piece in your way")
		} else if b.pieces[np[0]][np[1]].Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(move.From, to, &move.Piece, move.Promotion), nil
	}
	return false, nil
}

func (b *Board) testKingMove(move *Move) (bool, error) {
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	if dir == -1 {
		return false, fmt.Errorf("KING doesn't move that way")
	}
	from, to := move.From, move.To
	dr := to.Row - from.Row
	dc := to.Col - from.Col

	switch dir {
	case dirN:
		if dr != 1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	case dirS:
		if dr != -1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	case dirE:
		if dc == 2 {
			return b.testCastle(move, dirE)
		}
		if dc != 1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	case dirW:
		if dc == -2 {
			return b.testCastle(move, dirW)
		}
		if dc != -1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	case dirNE, dirNW:
		if dr != 1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	case dirSE, dirSW:
		if dr != -1 {
			return false, fmt.Errorf("Can't move more than one square")
		}
	}

	target := b.pieces[to.Row][to.Col]
	if target != nil {
		if target.Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
	}
	return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
}

func (b *Board) testPawnMove(move *Move) (bool, error) {
	b.doEnpassant = false
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	from, to := move.From, move.To

	if b.Turn == White {
		if dir != dirN && dir != dirNW && dir != dirNE {
			return false, fmt.Errorf("PAWN doesn't move that way")
		}
		switch dir {
		case dirN:
			dr := to.Row - from.Row
			if dr == 1 {
				if b.pieces[to.Row][to.Col] != nil {
					return false, fmt.Errorf("You have a piece in your way")
				}
				return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
			} else if dr == 2 {
				if from.Row != 1 {
					return false, fmt.Errorf("PAWN can't move that way")
				}
				if b.pieces[to.Row-1][to.Col] == nil && b.pieces[to.Row][to.Col] == nil {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("You have a piece in your way")
			}
			return false, fmt.Errorf("PAWN can't move that way")
		case dirNW:
			if to.Row-from.Row != 1 {
				return false, fmt.Errorf("Can't capture more than one piece")
			}
			if b.pieces[to.Row][to.Col] != nil {
				if b.pieces[to.Row][to.Col].Color != b.Turn {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("Can't capture your own pieces")
			}
			// En passant
			if b.lastMove != nil && b.pieces[to.Row-1][to.Col] != nil &&
				b.pieces[to.Row-1][to.Col].Kind == Pawn && b.pieces[to.Row-1][to.Col].Color == Black &&
				b.lastMove.To.Row == to.Row-1 &&
				abs(b.lastMove.From.Row-b.lastMove.To.Row) == 2 {
				if b.testApplyMove(from, to, &move.Piece, move.Promotion) {
					b.doEnpassant = true
					return true, nil
				}
			}
			return false, fmt.Errorf("Can't capture an empty square")
		case dirNE:
			if to.Row-from.Row != 1 {
				return false, fmt.Errorf("Can't capture more than one piece")
			}
			if b.pieces[to.Row][to.Col] != nil {
				if b.pieces[to.Row][to.Col].Color != b.Turn {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("Can't capture your own pieces")
			}
			// En passant
			if b.lastMove != nil && b.pieces[to.Row-1][to.Col] != nil &&
				b.pieces[to.Row-1][to.Col].Kind == Pawn && b.pieces[to.Row-1][to.Col].Color == Black &&
				b.lastMove.To.Row == to.Row-1 &&
				abs(b.lastMove.From.Row-b.lastMove.To.Row) == 2 {
				if b.testApplyMove(from, to, &move.Piece, move.Promotion) {
					b.doEnpassant = true
					return true, nil
				}
			}
			return false, fmt.Errorf("Can't capture an empty square")
		}
	} else { // Black
		if dir != dirS && dir != dirSW && dir != dirSE {
			return false, fmt.Errorf("PAWN doesn't move that way")
		}
		switch dir {
		case dirS:
			dr := to.Row - from.Row
			if dr == -1 {
				if b.pieces[to.Row][to.Col] != nil {
					return false, fmt.Errorf("You have a piece in your way")
				}
				return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
			} else if dr == -2 {
				if from.Row != 6 {
					return false, fmt.Errorf("PAWN can't move that way")
				}
				if b.pieces[to.Row+1][to.Col] == nil && b.pieces[to.Row][to.Col] == nil {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("You have a piece in your way")
			}
			return false, fmt.Errorf("PAWN can't move that way")
		case dirSW:
			if from.Row-to.Row != 1 {
				return false, fmt.Errorf("Can't capture more than one piece")
			}
			if b.pieces[to.Row][to.Col] != nil {
				if b.pieces[to.Row][to.Col].Color != b.Turn {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("Can't capture your own pieces")
			}
			// En passant
			if b.lastMove != nil && b.pieces[to.Row+1][to.Col] != nil &&
				b.pieces[to.Row+1][to.Col].Kind == Pawn && b.pieces[to.Row+1][to.Col].Color == White &&
				b.lastMove.To.Row == to.Row+1 &&
				abs(b.lastMove.From.Row-b.lastMove.To.Row) == 2 {
				if b.testApplyMove(from, to, &move.Piece, move.Promotion) {
					b.doEnpassant = true
					return true, nil
				}
			}
			return false, fmt.Errorf("Can't capture an empty square")
		case dirSE:
			if from.Row-to.Row != 1 {
				return false, fmt.Errorf("Can't capture more than one piece")
			}
			if b.pieces[to.Row][to.Col] != nil {
				if b.pieces[to.Row][to.Col].Color != b.Turn {
					return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
				}
				return false, fmt.Errorf("Can't capture your own pieces")
			}
			// En passant
			if b.lastMove != nil && b.pieces[to.Row+1][to.Col] != nil &&
				b.pieces[to.Row+1][to.Col].Kind == Pawn && b.pieces[to.Row+1][to.Col].Color == White &&
				b.lastMove.To.Row == to.Row+1 &&
				abs(b.lastMove.From.Row-b.lastMove.To.Row) == 2 {
				if b.testApplyMove(from, to, &move.Piece, move.Promotion) {
					b.doEnpassant = true
					return true, nil
				}
			}
			return false, fmt.Errorf("Can't capture an empty square")
		}
	}
	return false, nil
}

func (b *Board) testKnightMove(move *Move) (bool, error) {
	dir, err := b.findMoveDirection(move.From, move.To)
	if err != nil {
		return false, err
	}
	if dir != -1 {
		return false, fmt.Errorf("KNIGHT doesn't move that way")
	}
	from, to := move.From, move.To
	dr := to.Row - from.Row
	dc := to.Col - from.Col
	if (abs(dr) == 1 && abs(dc) == 2) || (abs(dr) == 2 && abs(dc) == 1) {
		target := b.pieces[to.Row][to.Col]
		if target != nil && target.Color == b.Turn {
			return false, fmt.Errorf("Can't capture your own pieces")
		}
		return b.testApplyMove(from, to, &move.Piece, move.Promotion), nil
	}
	return false, fmt.Errorf("KNIGHT doesn't move that way")
}

func (b *Board) testCastle(move *Move, dir direction) (bool, error) {
	piece := &move.Piece
	if piece.NumMoves != 0 {
		return false, fmt.Errorf("Can't Castle if KING already moved")
	}
	if b.kingInCheck(b.pieces) {
		return false, fmt.Errorf("Can't Castle if KING in CHECK")
	}

	var rookRow, rookCol int
	var passPos1, passPos2 Position

	if piece.Color == White {
		rookRow = 0
		if dir == dirE {
			rookCol = 7
			passPos1 = Position{0, 5}
			passPos2 = Position{0, 6}
		} else {
			rookCol = 0
			passPos1 = Position{0, 2}
			passPos2 = Position{0, 3}
		}
	} else {
		rookRow = 7
		if dir == dirE {
			rookCol = 7
			passPos1 = Position{7, 5}
			passPos2 = Position{7, 6}
		} else {
			rookCol = 0
			passPos1 = Position{7, 2}
			passPos2 = Position{7, 3}
		}
	}

	rook := b.pieces[rookRow][rookCol]
	if rook == nil || rook.Kind != Rook {
		return false, fmt.Errorf("Can't Castle without a ROOK")
	}
	if rook.Color != piece.Color {
		return false, fmt.Errorf("Can't Castle with a ROOK of a different color")
	}
	if rook.NumMoves != 0 {
		return false, fmt.Errorf("Can't Castle if ROOK already moved")
	}

	// Check path is clear
	np := b.nextPiece(b.pieces, move.From.Row, move.From.Col, dir)
	if np[1] != rookCol {
		return false, fmt.Errorf("There is a piece in the way")
	}

	// Test king passes through safe squares
	tm1 := &Move{From: move.From, To: passPos1, Piece: *piece}
	tm2 := &Move{From: move.From, To: passPos2, Piece: *piece}
	if !b.testApplyMove(tm1.From, tm1.To, piece, nil) || !b.testApplyMove(tm2.From, tm2.To, piece, nil) {
		return false, fmt.Errorf("Can't Castle, KING will pass a CHECK square or will be in CHECK")
	}
	return true, nil
}

func (b *Board) applyMove(move *Move) error {
	from, to := move.From, move.To
	piece := b.pieces[from.Row][from.Col]

	if b.Turn == White {
		if piece.Kind == Pawn && to.Row == 7 {
			if move.Promotion == nil {
				return fmt.Errorf("Pawn promotion requires a piece type")
			}
			b.pieces[to.Row][to.Col] = &Piece{White, *move.Promotion, 1}
			b.pieces[from.Row][from.Col] = nil
			switch *move.Promotion {
			case Rook:
				b.numberPiecesCreated[1]++
			case Knight:
				b.numberPiecesCreated[2]++
			case Bishop:
				b.numberPiecesCreated[3]++
			case Queen:
				b.numberPiecesCreated[4]++
			}
		} else if piece.Kind == Pawn && b.doEnpassant {
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			b.pieces[to.Row-1][to.Col] = nil
			b.doEnpassant = false
			b.pieces[to.Row][to.Col].NumMoves++
		} else if piece.Kind == King && abs(to.Col-from.Col) == 2 {
			dir, _ := b.findMoveDirection(from, to)
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			if dir == dirE {
				b.pieces[0][5] = b.pieces[0][7]
				b.pieces[0][7] = nil
				if b.pieces[0][5] != nil {
					b.pieces[0][5].NumMoves++
				}
			} else {
				b.pieces[0][3] = b.pieces[0][0]
				b.pieces[0][0] = nil
				if b.pieces[0][3] != nil {
					b.pieces[0][3].NumMoves++
				}
			}
			b.pieces[to.Row][to.Col].NumMoves++
		} else {
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			b.pieces[to.Row][to.Col].NumMoves++
		}
	} else { // Black
		if piece.Kind == Pawn && to.Row == 0 {
			if move.Promotion == nil {
				return fmt.Errorf("Pawn promotion requires a piece type")
			}
			b.pieces[to.Row][to.Col] = &Piece{Black, *move.Promotion, 1}
			b.pieces[from.Row][from.Col] = nil
			switch *move.Promotion {
			case Rook:
				b.numberPiecesCreated[6]++
			case Knight:
				b.numberPiecesCreated[7]++
			case Bishop:
				b.numberPiecesCreated[8]++
			case Queen:
				b.numberPiecesCreated[9]++
			}
		} else if piece.Kind == Pawn && b.doEnpassant {
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			b.pieces[to.Row+1][to.Col] = nil
			b.doEnpassant = false
			b.pieces[to.Row][to.Col].NumMoves++
		} else if piece.Kind == King && abs(to.Col-from.Col) == 2 {
			dir, _ := b.findMoveDirection(from, to)
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			if dir == dirE {
				b.pieces[7][5] = b.pieces[7][7]
				b.pieces[7][7] = nil
				if b.pieces[7][5] != nil {
					b.pieces[7][5].NumMoves++
				}
			} else {
				b.pieces[7][3] = b.pieces[7][0]
				b.pieces[7][0] = nil
				if b.pieces[7][3] != nil {
					b.pieces[7][3].NumMoves++
				}
			}
			b.pieces[to.Row][to.Col].NumMoves++
		} else {
			b.pieces[to.Row][to.Col] = piece
			b.pieces[from.Row][from.Col] = nil
			b.pieces[to.Row][to.Col].NumMoves++
		}
	}
	return nil
}

// FinishGame marks the game as finished with an optional winner
func (b *Board) FinishGame(winner *Color) {
	b.Finished = true
	if winner != nil {
		b.Winner = winner
	}
}

// noMoves returns true if the current player has no legal moves
func (b *Board) noMoves(pieces [8][8]*Piece) bool {
	for i := 0; i < 8; i++ {
		for j := 0; j < 8; j++ {
			p := pieces[i][j]
			if p == nil || p.Color != b.Turn {
				continue
			}
			pos := [2]int{i, j}
			var candidates []Move
			switch p.Kind {
			case King:
				candidates = b.testAllKingMoves(pieces, pos)
			case Rook:
				candidates = b.testAllRookMoves(pieces, pos)
			case Bishop:
				candidates = b.testAllBishopMoves(pieces, pos)
			case Queen:
				candidates = b.testAllQueenMoves(pieces, pos)
			case Knight:
				candidates = b.testAllKnightMoves(pieces, pos)
			case Pawn:
				candidates = b.testAllPawnMoves(pieces, pos)
			}
			if !b.testPieceMove(candidates, p.Kind) {
				return false // found a valid move
			}
		}
	}
	return true
}

func (b *Board) testPieceMove(candidates []Move, kind PieceKind) bool {
	for i := range candidates {
		m := &candidates[i]
		var result bool
		var err error
		switch kind {
		case King:
			result, err = b.testKingMove(m)
		case Rook:
			result, err = b.testRookMove(m)
		case Bishop:
			result, err = b.testBishopMove(m)
		case Queen:
			result, err = b.testQueenMove(m)
		case Knight:
			result, err = b.testKnightMove(m)
		case Pawn:
			result, err = b.testPawnMove(m)
		}
		if err == nil && result {
			return false // found a valid move
		}
	}
	return true // no valid moves
}

func (b *Board) testAllKingMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	p := pieces[pos[0]][pos[1]]
	from := Position{pos[0], pos[1]}
	var moves []Move
	diffs := [][2]int{{1, 0}, {1, 1}, {1, -1}, {-1, 0}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}}
	for _, d := range diffs {
		to := Position{pos[0] + d[0], pos[1] + d[1]}
		if to.Row >= 0 && to.Row <= 7 && to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
			moves = append(moves, Move{From: from, To: to, Piece: *p})
		}
	}
	// Castling candidates (only if king at col 4)
	if pos[1] == 4 {
		to1 := Position{pos[0], pos[1] + 2}
		to2 := Position{pos[0], pos[1] - 2}
		if b.verifyToPosition(from, to1) {
			moves = append(moves, Move{From: from, To: to1, Piece: *p})
		}
		if b.verifyToPosition(from, to2) {
			moves = append(moves, Move{From: from, To: to2, Piece: *p})
		}
	}
	return moves
}

func (b *Board) testAllRookMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	p := pieces[pos[0]][pos[1]]
	from := Position{pos[0], pos[1]}
	var moves []Move
	for i := 0; i < 8; i++ {
		to := Position{pos[0], i}
		if b.verifyToPosition(from, to) {
			moves = append(moves, Move{From: from, To: to, Piece: *p})
		}
		to = Position{i, pos[1]}
		if b.verifyToPosition(from, to) {
			moves = append(moves, Move{From: from, To: to, Piece: *p})
		}
	}
	return moves
}

func (b *Board) testAllBishopMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	p := pieces[pos[0]][pos[1]]
	from := Position{pos[0], pos[1]}
	var moves []Move
	for di := 1; di < 8; di++ {
		for _, d := range [][2]int{{di, di}, {di, -di}, {-di, di}, {-di, -di}} {
			to := Position{pos[0] + d[0], pos[1] + d[1]}
			if to.Row >= 0 && to.Row <= 7 && to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
				moves = append(moves, Move{From: from, To: to, Piece: *p})
			}
		}
	}
	return moves
}

func (b *Board) testAllQueenMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	moves := b.testAllRookMoves(pieces, pos)
	moves = append(moves, b.testAllBishopMoves(pieces, pos)...)
	return moves
}

func (b *Board) testAllKnightMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	p := pieces[pos[0]][pos[1]]
	from := Position{pos[0], pos[1]}
	var moves []Move
	for _, d := range [][2]int{{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}} {
		to := Position{pos[0] + d[0], pos[1] + d[1]}
		if to.Row >= 0 && to.Row <= 7 && to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
			moves = append(moves, Move{From: from, To: to, Piece: *p})
		}
	}
	return moves
}

func (b *Board) testAllPawnMoves(pieces [8][8]*Piece, pos [2]int) []Move {
	p := pieces[pos[0]][pos[1]]
	from := Position{pos[0], pos[1]}
	var moves []Move
	if p.Color == White {
		if pos[0] < 6 {
			if pos[0] == 1 {
				to := Position{pos[0] + 2, pos[1]}
				if b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p})
				}
			}
			for _, dc := range []int{0, 1, -1} {
				to := Position{pos[0] + 1, pos[1] + dc}
				if to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p})
				}
			}
		} else {
			// Promotion
			q := Queen
			for _, dc := range []int{0, 1, -1} {
				to := Position{pos[0] + 1, pos[1] + dc}
				if to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p, Promotion: &q})
				}
			}
		}
	} else { // Black
		if pos[0] > 1 {
			if pos[0] == 6 {
				to := Position{pos[0] - 2, pos[1]}
				if b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p})
				}
			}
			for _, dc := range []int{0, 1, -1} {
				to := Position{pos[0] - 1, pos[1] + dc}
				if to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p})
				}
			}
		} else {
			// Promotion
			q := Queen
			for _, dc := range []int{0, 1, -1} {
				to := Position{pos[0] - 1, pos[1] + dc}
				if to.Col >= 0 && to.Col <= 7 && b.verifyToPosition(from, to) {
					moves = append(moves, Move{From: from, To: to, Piece: *p, Promotion: &q})
				}
			}
		}
	}
	return moves
}

// GetPossibleMoves returns all legal destination squares for the piece at pos
func (b *Board) GetPossibleMoves(pos Position) []Move {
	p := b.pieces[pos.Row][pos.Col]
	if p == nil {
		return nil
	}

	var candidates []Move
	posArr := [2]int{pos.Row, pos.Col}
	switch p.Kind {
	case King:
		candidates = b.testAllKingMoves(b.pieces, posArr)
	case Rook:
		candidates = b.testAllRookMoves(b.pieces, posArr)
	case Bishop:
		candidates = b.testAllBishopMoves(b.pieces, posArr)
	case Queen:
		candidates = b.testAllQueenMoves(b.pieces, posArr)
	case Knight:
		candidates = b.testAllKnightMoves(b.pieces, posArr)
	case Pawn:
		candidates = b.testAllPawnMoves(b.pieces, posArr)
	}

	var result []Move
	for i := range candidates {
		m := &candidates[i]
		var valid bool
		var err error
		switch p.Kind {
		case King:
			valid, err = b.testKingMove(m)
		case Rook:
			valid, err = b.testRookMove(m)
		case Bishop:
			valid, err = b.testBishopMove(m)
		case Queen:
			valid, err = b.testQueenMove(m)
		case Knight:
			valid, err = b.testKnightMove(m)
		case Pawn:
			valid, err = b.testPawnMove(m)
		}
		if err == nil && valid {
			result = append(result, *m)
		}
	}
	return result
}

func abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}
