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

import org.agilesoft.authentication.AuthenticationJWTService;
import org.agilesoft.utils.WorkWithSession;

@Path("/login")
public class Login {

    @Inject
    UserDAO userDAO;

    @Inject
    AuthenticationJWTService jwtService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(WorkWithSession.UserCredentials credentials) {
        System.out.println(credentials.getPassword() + " " + credentials.getUsername());

        User user = userDAO.findByUsername(credentials.getUsername());
        System.out.println(user);

        if (user != null && BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
            String jwt = jwtService.generateJWT(user.getUsername());
            return Response.ok(new WorkWithSession.LoginResponse("Login successful", jwt)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new WorkWithSession.LoginResponse("Invalid credentials", null)).build();
        }
    }
}
