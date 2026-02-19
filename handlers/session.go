package handlers

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strings"
)

const sessionCookieName = "chesssession"

// Session holds the authenticated player data
type Session struct {
	Name     string `json:"name"`
	Email    string `json:"email"`
	PlayerID int64  `json:"player_id"`
}

var sessionSecret []byte

func init() {
	secret := os.Getenv("SESSION_SECRET")
	if secret == "" {
		secret = "dev-secret-change-in-prod"
	}
	sessionSecret = []byte(secret)
}

func sign(payload string) string {
	mac := hmac.New(sha256.New, sessionSecret)
	mac.Write([]byte(payload))
	return base64.RawURLEncoding.EncodeToString(mac.Sum(nil))
}

// SetSession serializes and signs the session, then sets the cookie
func SetSession(w http.ResponseWriter, s *Session) error {
	data, err := json.Marshal(s)
	if err != nil {
		return err
	}
	payload := base64.RawURLEncoding.EncodeToString(data)
	sig := sign(payload)
	cookie := &http.Cookie{
		Name:     sessionCookieName,
		Value:    payload + "." + sig,
		Path:     "/",
		HttpOnly: true,
		SameSite: http.SameSiteLaxMode,
	}
	http.SetCookie(w, cookie)
	return nil
}

// GetSession retrieves and verifies the session from the request
func GetSession(r *http.Request) (*Session, error) {
	c, err := r.Cookie(sessionCookieName)
	if err != nil {
		return nil, nil // no session
	}
	parts := strings.SplitN(c.Value, ".", 2)
	if len(parts) != 2 {
		return nil, fmt.Errorf("invalid cookie format")
	}
	payload, sig := parts[0], parts[1]
	expected := sign(payload)
	if !hmac.Equal([]byte(sig), []byte(expected)) {
		return nil, fmt.Errorf("invalid session signature")
	}
	data, err := base64.RawURLEncoding.DecodeString(payload)
	if err != nil {
		return nil, err
	}
	var s Session
	if err := json.Unmarshal(data, &s); err != nil {
		return nil, err
	}
	return &s, nil
}

// ClearSession removes the session cookie
func ClearSession(w http.ResponseWriter) {
	http.SetCookie(w, &http.Cookie{
		Name:   sessionCookieName,
		Value:  "",
		Path:   "/",
		MaxAge: -1,
	})
}

// RequireSession redirects to /Registo if not logged in, otherwise returns session
func RequireSession(w http.ResponseWriter, r *http.Request) *Session {
	s, err := GetSession(r)
	if err != nil || s == nil {
		http.Redirect(w, r, "/Registo", http.StatusSeeOther)
		return nil
	}
	return s
}
