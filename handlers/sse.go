package handlers

import (
	"chessbookweb/chess"
	"chessbookweb/db"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"sync"
)

// gameHub manages SSE subscribers per game
var (
	gameMu   sync.Mutex
	gameHubs = map[int64][]chan struct{}{}
)

// BroadcastGame notifies all SSE subscribers for a game
func BroadcastGame(gameID int64) {
	gameMu.Lock()
	defer gameMu.Unlock()
	subs := gameHubs[gameID]
	for _, ch := range subs {
		select {
		case ch <- struct{}{}:
		default:
		}
	}
}

func subscribe(gameID int64) chan struct{} {
	ch := make(chan struct{}, 1)
	gameMu.Lock()
	gameHubs[gameID] = append(gameHubs[gameID], ch)
	gameMu.Unlock()
	return ch
}

func unsubscribe(gameID int64, ch chan struct{}) {
	gameMu.Lock()
	defer gameMu.Unlock()
	subs := gameHubs[gameID]
	for i, s := range subs {
		if s == ch {
			gameHubs[gameID] = append(subs[:i], subs[i+1:]...)
			return
		}
	}
}

// SSE handles GET /events?gameId=X (Server-Sent Events)
func SSE(w http.ResponseWriter, r *http.Request) {
	gameIDStr := r.URL.Query().Get("gameId")
	gameID, err := strconv.ParseInt(gameIDStr, 10, 64)
	if err != nil {
		http.Error(w, "invalid gameId", http.StatusBadRequest)
		return
	}

	flusher, ok := w.(http.Flusher)
	if !ok {
		http.Error(w, "streaming unsupported", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "text/event-stream")
	w.Header().Set("Cache-Control", "no-cache")
	w.Header().Set("Connection", "keep-alive")
	w.Header().Set("X-Accel-Buffering", "no")

	ch := subscribe(gameID)
	defer unsubscribe(gameID, ch)

	ctx := r.Context()
	for {
		select {
		case <-ctx.Done():
			return
		case <-ch:
			fmt.Fprintf(w, "data: update\n\n")
			flusher.Flush()
		}
	}
}

// PossibleMoves handles GET /ajax?gameId=X&square=Y or ?numberOfMoves=N&finished=...
func PossibleMoves(w http.ResponseWriter, r *http.Request) {
	gameIDStr := r.URL.Query().Get("gameId")
	gameID, err := strconv.ParseInt(gameIDStr, 10, 64)
	if err != nil {
		http.Error(w, "invalid gameId", http.StatusBadRequest)
		return
	}

	g, err := db.FindGameByID(gameID)
	if err != nil || g == nil {
		http.Error(w, "game not found", http.StatusNotFound)
		return
	}

	// Polling check: numberOfMoves + finished + tieOffer
	numberOfMovesStr := r.URL.Query().Get("numberOfMoves")
	if numberOfMovesStr != "" {
		moves, _ := db.LoadMoves(gameID)
		clientMoveCount, _ := strconv.Atoi(numberOfMovesStr)
		clientFinished := r.URL.Query().Get("finished")
		clientTieOffer := r.URL.Query().Get("tieOffer")

		serverFinished := "false"
		if g.Finished {
			serverFinished = "true"
		}
		serverTieOffer := "false"
		if g.TieOfferPlayerID != nil {
			serverTieOffer = "true"
		}

		if clientMoveCount != len(moves) ||
			clientFinished != serverFinished ||
			clientTieOffer != serverTieOffer {
			w.Write([]byte("True"))
			return
		}
		return
	}

	// Possible moves for a square
	square := r.URL.Query().Get("square")
	if square != "" {
		moves, err := db.LoadMoves(gameID)
		if err != nil {
			http.Error(w, "error loading moves", http.StatusInternalServerError)
			return
		}
		board, err := chess.NewBoardFromMoves(moves)
		if err != nil {
			http.Error(w, "error building board", http.StatusInternalServerError)
			return
		}
		row, col := chess.SquareToPos(square)
		if row < 0 {
			return
		}
		possibleMoves := board.GetPossibleMoves(chess.Position{Row: row, Col: col})
		var sb strings.Builder
		for _, mv := range possibleMoves {
			sb.WriteString(chess.PosToSquare(mv.To.Row, mv.To.Col))
			sb.WriteString(" ")
		}
		w.Write([]byte(sb.String()))
	}
}
