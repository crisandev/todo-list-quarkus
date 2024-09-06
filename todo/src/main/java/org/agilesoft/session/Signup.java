package org.agilesoft.session;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.User;
import org.mindrot.jbcrypt.BCrypt;

@Path("/signup")
public class Signup {

    @Inject
    UserDAO userDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signup(UserCredentials credentials) {
        if (credentials.getUsername() == null || credentials.getPassword() == null || credentials.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username, password, and name are required").build();
        }

        User existingUser = userDAO.findByUsername(credentials.getUsername());
        if (existingUser != null) {
            return Response.status(Response.Status.CONFLICT).entity("User already exists").build();
        }

        User newUser = new User();
        newUser.setUsername(credentials.getUsername());

        String hashedPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
        newUser.setPassword(hashedPassword);

        newUser.setName(credentials.getName());

        userDAO.save(newUser);

        return Response.status(Response.Status.CREATED).entity(newUser).build();
    }

    public static class UserCredentials {
        public String username;
        public String password;
        public String name;

        // Getters and setters
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
