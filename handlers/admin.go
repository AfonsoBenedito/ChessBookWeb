package handlers

import (
	"chessbookweb/db"
	"net/http"
	"strconv"
)

type adminData struct {
	Session *Session
	Players []db.Player
	Games   []db.Game
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

	games, err := db.ListAllGames()
	if err != nil {
		data.Error = "Error loading games: " + err.Error()
	} else {
		data.Games = games
	}

	renderTemplate(w, "manager.html", data)
}
