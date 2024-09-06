package org.agilesoft.crud;

import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.dto.TaskDTO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.tasks_operations.EditTask;
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

public class EditTaskTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private WorkWithToken wwt;

    @InjectMocks
    private EditTask editTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEditTask_Success() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Title");
        taskDTO.setStatus(true);
        taskDTO.setDescription("Updated Description");

        Task task = new Task();
        task.setId(id);
        task.setUser(user);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(id)).thenReturn(task);

        Response response = editTask.editTask(token, id, taskDTO);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Editada correctamente", response.getEntity());
        verify(taskDAO, times(1)).save(task);
        assertEquals("Updated Title", task.getTitle());
        assertEquals(true, task.getStatus());
        assertEquals("Updated Description", task.getDescription());
        assertEquals(LocalDateTime.now().getDayOfYear(), task.getLastUpdatedDate().getDayOfYear());
    }

    @Test
    public void testEditTask_UserNotFound() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(null);

        Response response = editTask.editTask(token, id, new TaskDTO());

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("User not found", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testEditTask_TaskNotFound() {
        String token = "Bearer validToken";
        String id = "task-id";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findById(id)).thenReturn(null);
        Response response = editTask.editTask(token, id, new TaskDTO());

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Task not found", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testEditTask_NotAuthorized() {
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

        Response response = editTask.editTask(token, id, new TaskDTO());

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertEquals("You do not have permission to edit this task", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }

    @Test
    public void testEditTask_InvalidToken() {
        String token = "Bearer invalidToken";
        String id = "task-id";

        when(wwt.extractUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        Response response = editTask.editTask(token, id, new TaskDTO());

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid token", response.getEntity());
        verify(taskDAO, never()).save(any(Task.class));
    }
}
