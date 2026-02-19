package handlers

import (
	"html/template"
	"net/http"
)

var tmpl *template.Template

// SetTemplate sets the parsed template set. Called once from main.
func SetTemplate(t *template.Template) {
	tmpl = t
}

func renderTemplate(w http.ResponseWriter, name string, data any) {
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	if err := tmpl.ExecuteTemplate(w, name, data); err != nil {
		http.Error(w, "template error: "+err.Error(), http.StatusInternalServerError)
	}
}

// RenderError renders the error page (no data needed).
func RenderError(w http.ResponseWriter) {
	renderTemplate(w, "error.html", nil)
}
