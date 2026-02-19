package handlers

import (
	"chessbookweb/chess"
	"chessbookweb/db"
	"net/http"
	"strconv"
	"time"
)

type eatenPieces struct {
	WPawns   int
	WRooks   int
	WKnights int
	WBishops int
	WQueens  int
	BPawns   int
	BRooks   int
	BKnights int
	BBishops int
	BQueens  int
}

// boardCell holds template data for a single board square
type boardCell struct {
	ID        string // "a1".."h8"
	Class     string // "light" or "dark" + border classes
	PieceHTML string // Unicode chess character (e.g. "♙") or " "
}

// boardRowDisplay is one rank of the board in display order (rank 8 first)
type boardRowDisplay struct {
	Rank  int
	Cells [8]boardCell
}

// gamePageData holds all template data for the game page
type gamePageData struct {
	Session            *Session
	GameID             int64
	WhiteName          string
	BlackName          string
	MoveCount          int
	DateStr            string
	OutputMove         string
	Eaten              eatenPieces
	BoardRows          [8]boardRowDisplay // display order: rank 8 first
	IsFinished         bool
	WinnerName         string
	WinnerIsWhite      bool
	EndDesc            string
	IsMyTurn           bool
	IsParticipant      bool
	TieOfferByMe       bool
	TieOfferByOpponent bool
	ReplayMode         bool
	MoveIndex          int
	BlackPlayerTime    int
	WhitePlayerTime    int
	OpenDateMS         int64 // 0 if not applicable
	IsWhitePlayer      bool
	ShowVitoria        bool
	ShowDerrota        bool
	ShowEmpate         bool
	IsDraw             bool
}

// Game handles GET/POST /Game
func Game(w http.ResponseWriter, r *http.Request) {
	sess := RequireSession(w, r)
	if sess == nil {
		return
	}

	gameIDStr := r.URL.Query().Get("Id")
	if gameIDStr == "" {
		http.Redirect(w, r, "/GameList", http.StatusSeeOther)
		return
	}
	gameID, err := strconv.ParseInt(gameIDStr, 10, 64)
	if err != nil {
		http.Redirect(w, r, "/GameList", http.StatusSeeOther)
		return
	}

	g, err := db.FindGameByID(gameID)
	if err != nil || g == nil {
		http.Redirect(w, r, "/GameList", http.StatusSeeOther)
		return
	}

	moves, err := db.LoadMoves(gameID)
	if err != nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}

	whitep, _ := db.FindPlayerByID(g.WhitePlayerID)
	blackp, _ := db.FindPlayerByID(g.BlackPlayerID)
	if whitep == nil || blackp == nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}

	board, err := chess.NewBoardFromMoves(moves)
	if err != nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}

	if r.Method == http.MethodPost {
		r.ParseForm()

		// Resign
		if r.FormValue("desistir") != "" {
			if !g.Finished {
				if sess.PlayerID == g.WhitePlayerID {
					bid := g.BlackPlayerID
					g.WinnerPlayerID = &bid
					g.Finished = true
					desc := "White Resigned"
					g.EndGameDescription = &desc
				} else if sess.PlayerID == g.BlackPlayerID {
					wid := g.WhitePlayerID
					g.WinnerPlayerID = &wid
					g.Finished = true
					desc := "Black Resigned"
					g.EndGameDescription = &desc
				}
				db.UpdateGame(g)
				BroadcastGame(gameID)
			}
			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}

		// Offer draw
		if r.FormValue("pedirEmpate") != "" {
			if !g.Finished {
				g.TieOfferPlayerID = &sess.PlayerID
				db.UpdateGame(g)
				BroadcastGame(gameID)
			}
			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}

		// Accept draw
		if r.FormValue("aceitarEmpate") != "" {
			if !g.Finished && g.TieOfferPlayerID != nil {
				g.Finished = true
				g.WinnerPlayerID = nil
				desc := "Players agreed a Draw"
				g.EndGameDescription = &desc
				g.TieOfferPlayerID = nil
				db.UpdateGame(g)
				BroadcastGame(gameID)
			}
			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}

		// Refuse draw
		if r.FormValue("recusarEmpate") != "" {
			if g.TieOfferPlayerID != nil {
				g.TieOfferPlayerID = nil
				db.UpdateGame(g)
				BroadcastGame(gameID)
			}
			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}

		// Make a move
		moveStr := r.FormValue("move")
		tempoStr := r.FormValue("tempoMove")
		outputMove := ""
		if moveStr != "" && chess.VerifyInput(moveStr) && !g.Finished {
			// Only current player can move
			turnPlayerID := g.WhitePlayerID
			if len(moves)%2 != 0 {
				turnPlayerID = g.BlackPlayerID
			}
			if sess.PlayerID == turnPlayerID {
				timeMilli := 0
				if tempoStr != "" {
					timeMilli, _ = strconv.Atoi(tempoStr)
				}
				moveOrder := len(moves)
				mv, err := (&chess.Game{
					White: chess.Player{ID: g.WhitePlayerID},
					Black: chess.Player{ID: g.BlackPlayerID},
					Board: board,
					Moves: moves,
				}).ConvertInputToMove(moveStr, sess.PlayerID, timeMilli, moveOrder)
				if err != nil {
					outputMove = err.Error()
				} else {
					if err := board.Update(mv); err != nil {
						outputMove = err.Error()
					} else {
						mv.MoveOrder = moveOrder
						if err := db.InsertMove(gameID, mv); err != nil {
							outputMove = "Database error"
						} else {
							moves = append(moves, *mv)
							// Update game state
							if board.Finished {
								g.Finished = true
								if board.Winner != nil {
									if *board.Winner == chess.White {
										g.WinnerPlayerID = &g.WhitePlayerID
									} else {
										g.WinnerPlayerID = &g.BlackPlayerID
									}
								}
								desc := board.BoardEndDescription
								g.EndGameDescription = &desc
							}
							g.OpenDateMS = nil
							g.TieOfferPlayerID = nil
							db.UpdateGame(g)
							BroadcastGame(gameID)
						}
					}
				}
			}
		}

		if outputMove != "" {
			// Re-render with error
		} else {
			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}
	}

	// Replay mode
	replayMode := false
	moveIndex := len(moves) - 1
	respMoveNumber := r.URL.Query().Get("MoveNumber")
	if respMoveNumber != "" {
		idx, err := strconv.Atoi(respMoveNumber)
		if err == nil {
			idx-- // convert 1-based to 0-based
			if idx < len(moves)-1 {
				replayMode = true
				moveIndex = idx
				// Rebuild board up to that move
				board, _ = chess.NewBoardFromMoves(moves[:idx+1])
			} else {
				http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
				return
			}
		}
	}

	// Determine current turn player ID
	turnPlayerID := g.WhitePlayerID
	if len(moves)%2 != 0 {
		turnPlayerID = g.BlackPlayerID
	}

	isMyTurn := sess.PlayerID == turnPlayerID && !g.Finished && !replayMode
	isParticipant := sess.PlayerID == g.WhitePlayerID || sess.PlayerID == g.BlackPlayerID
	isWhitePlayer := sess.PlayerID == g.WhitePlayerID

	// Set openDate if it's my turn and no openDate yet
	var openDateMS int64
	if isMyTurn && !g.Finished && !replayMode {
		if g.OpenDateMS == nil {
			now := time.Now().UnixMilli()
			g.OpenDateMS = &now
			db.UpdateGame(g)
		}
		openDateMS = *g.OpenDateMS
	}

	// Calculate player times
	var whiteTime, blackTime int
	for i, mv := range moves {
		if i%2 == 0 {
			whiteTime += mv.TimeMilli
		} else {
			blackTime += mv.TimeMilli
		}
	}

	// Eaten pieces
	ep := board.GetEatenPieces()
	eaten := eatenPieces{
		WPawns: ep[0], WRooks: ep[1], WKnights: ep[2], WBishops: ep[3], WQueens: ep[4],
		BPawns: ep[5], BRooks: ep[6], BKnights: ep[7], BBishops: ep[8], BQueens: ep[9],
	}

	// Build board rows in display order (rank 8 first → rank 1 last)
	files := []string{"a", "b", "c", "d", "e", "f", "g", "h"}
	var boardRows [8]boardRowDisplay
	for displayIdx := 0; displayIdx < 8; displayIdx++ {
		row := 7 - displayIdx // rank 8 → row 7, rank 1 → row 0
		rank := row + 1
		var cells [8]boardCell
		for col := 0; col < 8; col++ {
			file := files[col]
			id := file + strconv.Itoa(rank)
			class := "light"
			if (row+col)%2 == 0 {
				class = "dark"
			}
			// Border classes (on the visual corners of the board)
			if displayIdx == 0 && col == 0 {
				class += " borda_cima_esq"
			} else if displayIdx == 0 && col == 7 {
				class += " borda_cima_dir"
			} else if displayIdx == 7 && col == 0 {
				class += " borda_baixo_esq"
			} else if displayIdx == 7 && col == 7 {
				class += " borda_baixo_dir"
			}
			cells[col] = boardCell{
				ID:        id,
				Class:     class,
				PieceHTML: board.GetPieceHTML(row, col),
			}
		}
		boardRows[displayIdx] = boardRowDisplay{Rank: rank, Cells: cells}
	}

	// Notifications for end-of-game modals
	var showVitoria, showDerrota, showEmpate bool
	if g.Finished && !replayMode {
		if g.WinnerPlayerID == nil {
			// Draw
			if isWhitePlayer && !g.WhiteNotification {
				showEmpate = true
				g.WhiteNotification = true
				db.UpdateGame(g)
			} else if !isWhitePlayer && isParticipant && !g.BlackNotification {
				showEmpate = true
				g.BlackNotification = true
				db.UpdateGame(g)
			}
		} else if *g.WinnerPlayerID == sess.PlayerID {
			// Win
			if isWhitePlayer && !g.WhiteNotification {
				showVitoria = true
				g.WhiteNotification = true
				db.UpdateGame(g)
			} else if !isWhitePlayer && isParticipant && !g.BlackNotification {
				showVitoria = true
				g.BlackNotification = true
				db.UpdateGame(g)
			}
		} else if isParticipant {
			// Loss
			if isWhitePlayer && !g.WhiteNotification {
				showDerrota = true
				g.WhiteNotification = true
				db.UpdateGame(g)
			} else if !isWhitePlayer && isParticipant && !g.BlackNotification {
				showDerrota = true
				g.BlackNotification = true
				db.UpdateGame(g)
			}
		}
	}

	winnerName := ""
	if g.WinnerPlayerID != nil {
		if *g.WinnerPlayerID == g.WhitePlayerID {
			winnerName = whitep.Name
		} else {
			winnerName = blackp.Name
		}
	}

	endDesc := ""
	if g.EndGameDescription != nil {
		endDesc = *g.EndGameDescription
	}

	dateStr := time.UnixMilli(g.DateMS).Format("2006-01-02 15:04:05")

	tieOfferByMe := g.TieOfferPlayerID != nil && *g.TieOfferPlayerID == sess.PlayerID
	tieOfferByOpponent := g.TieOfferPlayerID != nil && *g.TieOfferPlayerID != sess.PlayerID

	data := gamePageData{
		Session:            sess,
		GameID:             gameID,
		WhiteName:          whitep.Name,
		BlackName:          blackp.Name,
		MoveCount:          len(moves),
		DateStr:            dateStr,
		Eaten:              eaten,
		BoardRows:          boardRows,
		IsFinished:         g.Finished,
		WinnerName:         winnerName,
		WinnerIsWhite:      g.WinnerPlayerID != nil && *g.WinnerPlayerID == g.WhitePlayerID,
		EndDesc:            endDesc,
		IsMyTurn:           isMyTurn,
		IsParticipant:      isParticipant,
		TieOfferByMe:       tieOfferByMe,
		TieOfferByOpponent: tieOfferByOpponent,
		ReplayMode:         replayMode,
		MoveIndex:          moveIndex,
		BlackPlayerTime:    blackTime,
		WhitePlayerTime:    whiteTime,
		OpenDateMS:         openDateMS,
		IsWhitePlayer:      isWhitePlayer,
		ShowVitoria:        showVitoria,
		ShowDerrota:        showDerrota,
		ShowEmpate:         showEmpate,
		IsDraw:             g.WinnerPlayerID == nil && g.Finished,
	}

	// Handle outputMove from POST re-render
	if r.Method == http.MethodPost && r.FormValue("move") != "" {
		data.OutputMove = r.FormValue("_outputMove")
	}

	renderTemplate(w, "game.html", data)
}
