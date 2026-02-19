package handlers

import (
	"chessbookweb/chess"
	"chessbookweb/db"
	"math/rand"
	"net/http"
	"strconv"
	"time"
)

// GameSummary holds display data for one game in the list
type GameSummary struct {
	ID            int64
	MoveCount     int
	Opponent      string
	IsMyTurn      bool
	TieOfferFrom  bool // opponent offered tie
	WinResult     string // "win", "loss", "draw"
	EndDesc       string
	WhitePlayerID int64
	BlackPlayerID int64
}

// PlayerOption holds player data for the new game search
type PlayerOption struct {
	Name  string
	Email string
}

type gameListData struct {
	Name       string
	Email      string
	Unfinished []GameSummary
	Finished   []GameSummary
	Players    []PlayerOption
}

// GameList handles GET/POST /GameList
func GameList(w http.ResponseWriter, r *http.Request) {
	sess := RequireSession(w, r)
	if sess == nil {
		return
	}

	if r.Method == http.MethodPost {
		r.ParseForm()

		// Handle new game
		startGameEmail := r.FormValue("startGameEmail")
		cor := r.FormValue("cor")
		if startGameEmail != "" {
			opponent, err := db.FindPlayerByEmail(startGameEmail)
			if err != nil || opponent == nil {
				http.Redirect(w, r, "/GameList", http.StatusSeeOther)
				return
			}
			me, err := db.FindPlayerByEmail(sess.Email)
			if err != nil || me == nil {
				http.Redirect(w, r, "/GameList", http.StatusSeeOther)
				return
			}

			var whiteID, blackID int64
			switch cor {
			case "branco":
				whiteID, blackID = me.ID, opponent.ID
			case "preto":
				whiteID, blackID = opponent.ID, me.ID
			default: // random
				if rand.Intn(2) == 0 {
					whiteID, blackID = me.ID, opponent.ID
				} else {
					whiteID, blackID = opponent.ID, me.ID
				}
			}

			gameID, err := db.InsertGame(whiteID, blackID, time.Now().UnixMilli())
			if err != nil {
				http.Redirect(w, r, "/Erro", http.StatusSeeOther)
				return
			}

			http.Redirect(w, r, "/Game?Id="+itoa(gameID), http.StatusSeeOther)
			return
		}
	}

	// Load player data
	me, err := db.FindPlayerByEmail(sess.Email)
	if err != nil || me == nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}

	unfinishedGames, err := db.FindUnfinishedGamesByPlayerID(me.ID)
	if err != nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}
	finishedGames, err := db.FindFinishedGamesByPlayerID(me.ID)
	if err != nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}
	allPlayers, err := db.ListPlayers()
	if err != nil {
		http.Redirect(w, r, "/Erro", http.StatusSeeOther)
		return
	}

	// Build player options (exclude self)
	var playerOptions []PlayerOption
	for _, p := range allPlayers {
		if p.ID != me.ID {
			playerOptions = append(playerOptions, PlayerOption{Name: p.Name, Email: p.Email})
		}
	}

	// Build unfinished summaries
	unfinished := make([]GameSummary, 0, len(unfinishedGames))
	for _, g := range unfinishedGames {
		moves, _ := db.LoadMoves(g.ID)
		summary := buildActiveSummary(g, moves, me.ID)
		unfinished = append(unfinished, summary)
	}

	// Build finished summaries
	finished := make([]GameSummary, 0, len(finishedGames))
	for _, g := range finishedGames {
		moves, _ := db.LoadMoves(g.ID)
		summary := buildFinishedSummary(g, moves, me.ID)
		finished = append(finished, summary)
	}

	// Resolve opponent names
	playerCache := map[int64]string{}
	getPlayerName := func(id int64) string {
		if n, ok := playerCache[id]; ok {
			return n
		}
		p, _ := db.FindPlayerByID(id)
		if p != nil {
			playerCache[id] = p.Name
			return p.Name
		}
		return "Unknown"
	}
	for i := range unfinished {
		if unfinished[i].WhitePlayerID == me.ID {
			unfinished[i].Opponent = getPlayerName(unfinished[i].BlackPlayerID)
		} else {
			unfinished[i].Opponent = getPlayerName(unfinished[i].WhitePlayerID)
		}
	}
	for i := range finished {
		if finished[i].WhitePlayerID == me.ID {
			finished[i].Opponent = getPlayerName(finished[i].BlackPlayerID)
		} else {
			finished[i].Opponent = getPlayerName(finished[i].WhitePlayerID)
		}
	}

	data := gameListData{
		Name:       sess.Name,
		Email:      sess.Email,
		Unfinished: unfinished,
		Finished:   finished,
		Players:    playerOptions,
	}
	renderTemplate(w, "gameList.html", data)
}

func buildActiveSummary(g db.Game, moves []chess.Move, myID int64) GameSummary {
	s := GameSummary{
		ID:            g.ID,
		MoveCount:     len(moves),
		WhitePlayerID: g.WhitePlayerID,
		BlackPlayerID: g.BlackPlayerID,
	}
	// Determine whose turn it is based on move count
	if len(moves)%2 == 0 {
		s.IsMyTurn = g.WhitePlayerID == myID
	} else {
		s.IsMyTurn = g.BlackPlayerID == myID
	}
	// Tie offer: did opponent offer?
	if g.TieOfferPlayerID != nil && *g.TieOfferPlayerID != myID {
		s.TieOfferFrom = true
	}
	return s
}

func buildFinishedSummary(g db.Game, moves []chess.Move, myID int64) GameSummary {
	s := GameSummary{
		ID:            g.ID,
		MoveCount:     len(moves),
		WhitePlayerID: g.WhitePlayerID,
		BlackPlayerID: g.BlackPlayerID,
	}
	if g.WinnerPlayerID == nil {
		s.WinResult = "draw"
	} else if *g.WinnerPlayerID == myID {
		s.WinResult = "win"
	} else {
		s.WinResult = "loss"
	}
	if g.EndGameDescription != nil {
		s.EndDesc = *g.EndGameDescription
	}
	return s
}

// itoa converts int64 to string
func itoa(n int64) string {
	return strconv.FormatInt(n, 10)
}
