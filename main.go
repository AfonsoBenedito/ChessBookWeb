package main

import (
	"chessbookweb/db"
	"chessbookweb/handlers"
	"embed"
	"html/template"
	"log"
	"net/http"
	"os"
)

//go:embed templates
var templatesFS embed.FS

//go:embed static
var staticFS embed.FS

func main() {
	// Init DB
	dbPath := os.Getenv("DB_PATH")
	if dbPath == "" {
		dbPath = "./data/chess.db"
	}
	if err := db.Init(dbPath); err != nil {
		log.Fatalf("db init: %v", err)
	}

	// Init templates with custom functions
	tmpl := template.Must(
		template.New("").
			Funcs(template.FuncMap{
				"add": func(a, b int) int { return a + b },
				"deref": func(s *string) string {
					if s == nil {
						return ""
					}
					return *s
				},
			}).
			ParseFS(templatesFS, "templates/*.html"),
	)
	handlers.SetTemplate(tmpl)

	// Static files
	staticSub := http.FS(staticFS)
	http.Handle("/static/", http.FileServer(staticSub))

	// Routes
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		http.Redirect(w, r, "/Registo", http.StatusSeeOther)
	})
	http.HandleFunc("/Registo", handlers.Registo)
	http.HandleFunc("/Logout", handlers.Logout)
	http.HandleFunc("/GameList", handlers.GameList)
	http.HandleFunc("/Game", handlers.Game)
	http.HandleFunc("/events", handlers.SSE)
	http.HandleFunc("/ajax", handlers.PossibleMoves)
	http.HandleFunc("/ManageDB", handlers.ManageDB)
	http.HandleFunc("/Erro", func(w http.ResponseWriter, r *http.Request) {
		handlers.RenderError(w)
	})

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	log.Printf("Listening on :%s", port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
