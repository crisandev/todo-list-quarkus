package org.agilesoft.tasks_operations;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.utils.WorkWithToken;

@Path("/api")
@ApplicationScoped
public class DeleteTask {

    WorkWithToken wwt = new WorkWithToken();
    @Inject
    UserDAO userDAO;

    @Inject
    TaskDAO taskDAO;

    @DELETE
    @RolesAllowed({"userRegistered"})
    @Path("/delete/{id}")
    public Response deleteTask(@HeaderParam("Authorization") String token, @PathParam("id") String id) {
        try {
            String username = wwt.extractUsernameFromToken(token);
            User user = userDAO.findByUsername(username);

            if (user != null) {
                Task taskToDelete = taskDAO.findById(id);

                if (taskToDelete != null) {
                    if (taskToDelete.getUser() != null && taskToDelete.getUser().getUsername().equals(user.getUsername())) {
                        taskDAO.delete(taskToDelete);

                        return Response.ok("Task deleted correctly!").build();
                    } else {
                        return Response.status(Response.Status.FORBIDDEN).entity("You don't have access to delete this task!").build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("404 - Task not found").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("404 - User not found").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Token").build();
        }
    }


}
