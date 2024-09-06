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
import java.util.Optional;

@Path("/api")
@ApplicationScoped
public class EditTask {

    WorkWithToken wwt = new WorkWithToken();
    @Inject
    UserDAO userDAO;

    @Inject
    TaskDAO taskDAO;

    @PUT
    @RolesAllowed({"userRegistered"})
    @Path("/edit-task/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editTask(@HeaderParam("Authorization") String token, @PathParam("id") String id, TaskDTO taskDTO) {
        try {
            String username = wwt.extractUsernameFromToken(token);
            User user = userDAO.findByUsername(username);

            if (user != null) {
                Optional<org.agilesoft.entity.Task> taskOptional = Optional.ofNullable(taskDAO.findById(id));
                if (taskOptional.isPresent()) {
                    Task task = taskOptional.get();
                    if (!task.getUser().getUsername().equals(user.getUsername())) {
                        return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to edit this task").build();
                    }
                    task.setTitle(taskDTO.getTitle());
                    task.setStatus(Boolean.valueOf(taskDTO.isStatus() ? "true" : "false"));
                    task.setDescription(taskDTO.getDescription());
                    task.setLastUpdatedDate(LocalDateTime.now());

                    task = taskDAO.save(task);
                    return Response.ok("Edited Correctly!").build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
        }
    }
}
