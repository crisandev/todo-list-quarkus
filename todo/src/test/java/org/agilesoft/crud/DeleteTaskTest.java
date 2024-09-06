package org.agilesoft.crud;

import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.tasks_operations.DeleteTask;
import org.agilesoft.utils.WorkWithToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DeleteTaskTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private WorkWithToken wwt;

    @InjectMocks
    private DeleteTask deleteTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteTask_Success() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(id);
        task.setUser(user);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(id)).thenReturn(task);

        Response response = deleteTask.deleteTask(token, id);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Tarea eliminada correctamente", response.getEntity());
        verify(taskDAO, times(1)).delete(task);
    }

    @Test
    public void testDeleteTask_UserNotFound() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(null);

        Response response = deleteTask.deleteTask(token, id);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Usuario no encontrado", response.getEntity());
        verify(taskDAO, never()).delete(any(Task.class));
    }

    @Test
    public void testDeleteTask_TaskNotFound() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(id)).thenReturn(null);

        Response response = deleteTask.deleteTask(token, id);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Tarea no encontrada", response.getEntity());
        verify(taskDAO, never()).delete(any(Task.class));
    }

    @Test
    public void testDeleteTask_NotAuthorized() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setId(id);
        task.setUser(new User());

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(id)).thenReturn(task);

        Response response = deleteTask.deleteTask(token, id);

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("No tienes permiso para eliminar esta tarea", response.getEntity());
        verify(taskDAO, never()).delete(any(Task.class));
    }

    @Test
    public void testDeleteTask_InvalidToken() {
        String token = "Bearer invalidToken";
        String id = "task-id";

        when(wwt.extractUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        Response response = deleteTask.deleteTask(token, id);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Token inválido", response.getEntity());
        verify(taskDAO, never()).delete(any(Task.class));
    }
}
