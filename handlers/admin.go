package handlers

import (
	"chessbookweb/db"
	"net/http"
	"strconv"
	"time"
)

type gameDisplay struct {
	ID                 int64
	Date               string
	WhitePlayer        string
	BlackPlayer        string
	Finished           bool
	Winner             string
	EndGameDescription string
}

type adminData struct {
	Session *Session
	Players []db.Player
	Games   []gameDisplay
	Error   string
}

// ManageDB handles GET/POST /ManageDB (no login required)
func ManageDB(w http.ResponseWriter, r *http.Request) {
	sess, _ := GetSession(r) // optional – may be nil for unauthenticated visitors
	data := adminData{Session: sess}

	if r.Method == http.MethodPost {
		r.ParseForm()

		// Delete player
		if delID := r.FormValue("deletePlayer"); delID != "" {
			pid, err := strconv.ParseInt(delID, 10, 64)
			if err == nil {
				if err := db.DeletePlayer(pid); err != nil {
					data.Error = "Error deleting player: " + err.Error()
				}
			}
		}

		// Delete game
		if delID := r.FormValue("deleteGame"); delID != "" {
			gid, err := strconv.ParseInt(delID, 10, 64)
			if err == nil {
				if err := db.DeleteGame(gid); err != nil {
					data.Error = "Error deleting game: " + err.Error()
				}
			}
		}
	}

	players, err := db.ListPlayers()
	if err != nil {
		data.Error = "Error loading players: " + err.Error()
	} else {
		data.Players = players
	}

	// Build player name lookup map
	nameMap := make(map[int64]string, len(players))
	for _, p := range players {
		nameMap[p.ID] = p.Name
	}

	games, err := db.ListAllGames()
	if err != nil {
		data.Error = "Error loading games: " + err.Error()
	} else {
		displays := make([]gameDisplay, 0, len(games))
		for _, g := range games {
			d := gameDisplay{
				ID:          g.ID,
				WhitePlayer: nameMap[g.WhitePlayerID],
				BlackPlayer: nameMap[g.BlackPlayerID],
				Finished:    g.Finished,
			}
			if g.DateMS != 0 {
				d.Date = time.UnixMilli(g.DateMS).Format("02/01/2006")
			}
			if g.WinnerPlayerID != nil {
				d.Winner = nameMap[*g.WinnerPlayerID]
			} else {
				d.Winner = "—"
			}
			if g.EndGameDescription != nil {
				d.EndGameDescription = *g.EndGameDescription
			}
			displays = append(displays, d)
		}
		data.Games = displays
	}

	renderTemplate(w, "manager.html", data)
}
