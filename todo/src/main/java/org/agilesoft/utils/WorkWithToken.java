package org.agilesoft.utils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.util.Base64;

public class WorkWithToken {
    public String extractUsernameFromToken(String token) {
        try {
            if (token == null || !token.contains(".")) {
                throw new WebApplicationException("Invalid token format", Response.Status.UNAUTHORIZED);
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new WebApplicationException("Invalid token format", Response.Status.UNAUTHORIZED);
            }

            String payload = new String(Base64.getDecoder().decode(parts[1]));
            JsonObject jsonObject = Json.createReader(new StringReader(payload)).readObject();

            if (jsonObject.containsKey("username")) {
                return jsonObject.getString("username");
            } else {
                throw new WebApplicationException("Username not found in token", Response.Status.UNAUTHORIZED);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Invalid token", e);
        }
    }
}
