package org.agilesoft.crud;

import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.tasks_operations.ResolveTask;
import org.agilesoft.utils.WorkWithToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ResolveTaskTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private WorkWithToken wwt;

    @InjectMocks
    private ResolveTask resolveTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMarkTaskAsResolved_Success() {
        String token = "Bearer validToken";
        String username = "testuser";
        String taskId = "1";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Sample Task");
        task.setStatus(false);
        task.setDescription("Sample Description");
        task.setCreationDate(LocalDateTime.now());
        task.setLastUpdatedDate(LocalDateTime.now());
        task.setUser(user);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(taskId)).thenReturn(task);
        when(taskDAO.save(task)).thenReturn(task);

        Response response = resolveTask.markTaskAsResolved(token, taskId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Estatus cambiado correctamente", response.getEntity());

        assertEquals(true, task.getStatus());
    }

    @Test
    public void testMarkTaskAsResolved_UserNotFound() {
        String token = "Bearer validToken";
        String username = "testuser";
        String taskId = "1";

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(null);

        Response response = resolveTask.markTaskAsResolved(token, taskId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("User not found", response.getEntity());
        verify(taskDAO, never()).findById(anyString());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testMarkTaskAsResolved_TaskNotFound() {
        String token = "Bearer validToken";
        String username = "testuser";
        String taskId = "1";

        User user = new User();
        user.setUsername(username);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(taskId)).thenReturn(null);

        Response response = resolveTask.markTaskAsResolved(token, taskId);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Task not found", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testMarkTaskAsResolved_PermissionDenied() {
        String token = "Bearer validToken";
        String username = "testuser";
        String taskId = "1";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Sample Task");
        task.setStatus(false);
        task.setDescription("Sample Description");
        task.setCreationDate(LocalDateTime.now());
        task.setLastUpdatedDate(LocalDateTime.now());
        task.setUser(new User());

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(taskId)).thenReturn(task);

        Response response = resolveTask.markTaskAsResolved(token, taskId);

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You do not have permission to resolve this task", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testMarkTaskAsResolved_InvalidToken() {
        String token = "Bearer invalidToken";

        when(wwt.extractUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        Response response = resolveTask.markTaskAsResolved(token, "1");

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid token", response.getEntity());
        verify(userDAO, never()).findByUsername(anyString());
        verify(taskDAO, never()).findById(anyString());
        verify(taskDAO, never()).save(any(Task.class));
    }
}
