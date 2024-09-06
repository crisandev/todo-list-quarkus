package org.agilesoft.tasks_operations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.utils.WorkWithToken;

import java.util.List;
@Path("/api")
@ApplicationScoped
public class GetTasks {
    WorkWithToken wwt = new WorkWithToken();
    @Inject
    UserDAO userDAO;

    @Inject
    TaskDAO taskDAO;

    @GET
    @Path("get-tasks")
    @RolesAllowed({"userRegistered"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasks(@HeaderParam("Authorization") String token) {
        try {
            String username = wwt.extractUsernameFromToken(token);
            User user = userDAO.findByUsername(username);


            if (user != null) {
                List<org.agilesoft.entity.Task> tasks = taskDAO.findByUsername(username);
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Task task : tasks) {
                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                            .add("id", task.getId())
                            .add("title", task.getTitle())
                            .add("status", task.getStatus())
                            .add("description", task.getDescription())
                            .add("creationDate", task.getCreationDate().toString())
                            .add("lastUpdatedDate", task.getLastUpdatedDate().toString());
                    arrayBuilder.add(objectBuilder);
                }

                JsonArray jsonArray = arrayBuilder.build();
                return Response.ok(jsonArray).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token" + e.getMessage()).build();
        }
    }
}
