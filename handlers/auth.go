package handlers

import (
	"chessbookweb/db"
	"net/http"
)

type registroData struct {
	Output    string
	ShowLogin bool
}

// Registo handles GET/POST /Registo
func Registo(w http.ResponseWriter, r *http.Request) {
	data := registroData{}

	if r.Method == http.MethodPost {
		r.ParseForm()

		nomeRegisto := r.FormValue("nomeRegisto")
		emailRegisto := r.FormValue("emailRegisto")
		nomeLogin := r.FormValue("nomeLogin")
		emailLogin := r.FormValue("emailLogin")

		if nomeRegisto != "" {
			existing, err := db.FindPlayerByEmail(emailRegisto)
			if err != nil {
				http.Redirect(w, r, "/Erro", http.StatusSeeOther)
				return
			}
			if existing != nil {
				data.Output = "Registo erro"
			} else {
				_, err := db.InsertPlayer(nomeRegisto, emailRegisto)
				if err != nil {
					http.Redirect(w, r, "/Erro", http.StatusSeeOther)
					return
				}
				data.Output = "Registo sucesso"
				data.ShowLogin = true
			}
		} else if nomeLogin != "" {
			player, err := db.FindPlayerByEmail(emailLogin)
			if err != nil {
				http.Redirect(w, r, "/Erro", http.StatusSeeOther)
				return
			}
			if player == nil {
				data.Output = "Login erro email"
				data.ShowLogin = true
			} else if player.Name != nomeLogin {
				data.Output = "Login erro nome"
				data.ShowLogin = true
			} else {
				sess := &Session{
					Name:     player.Name,
					Email:    player.Email,
					PlayerID: player.ID,
				}
				if err := SetSession(w, sess); err != nil {
					http.Error(w, "session error", http.StatusInternalServerError)
					return
				}
				http.Redirect(w, r, "/GameList", http.StatusSeeOther)
				return
			}
		}
	}

	renderTemplate(w, "registo.html", data)
}

// Logout clears the session and redirects to /Registo
func Logout(w http.ResponseWriter, r *http.Request) {
	ClearSession(w)
	http.Redirect(w, r, "/Registo", http.StatusSeeOther)
}
