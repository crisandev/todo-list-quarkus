package org.agilesoft.utils;

public class WorkWithSession {
    public static class UserCredentials {
        public String username;
        public String password;

        // Getters y setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String message;
        public String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }
    }
}
