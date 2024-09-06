package org.agilesoft.tasks_operations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.dto.TaskDTO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.utils.WorkWithToken;

import java.time.LocalDateTime;

@Path("/api")
@ApplicationScoped
public class AddTask {
    WorkWithToken wwt = new WorkWithToken();
    @Inject
    UserDAO userDAO;

    @Inject
    TaskDAO taskDAO;

    @POST
    @Path("/add-task")
    @RolesAllowed({"userRegistered"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTask(@HeaderParam("Authorization") String token, TaskDTO taskDTO) {
        try {
            String username = wwt.extractUsernameFromToken(token);
            User user = userDAO.findByUsername(username);

            if (user != null) {
                org.agilesoft.entity.Task task = new Task();
                task.setTitle(taskDTO.getTitle());
                task.setStatus(Boolean.valueOf(taskDTO.isStatus() ? "true" : "false"));
                task.setDescription(taskDTO.getDescription());
                task.setUser(user);
                task.setCreationDate(LocalDateTime.now());
                task.setLastUpdatedDate(LocalDateTime.now());

                task = taskDAO.save(task);
                return Response.ok("Saved successfully!").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
        }
    }
}
