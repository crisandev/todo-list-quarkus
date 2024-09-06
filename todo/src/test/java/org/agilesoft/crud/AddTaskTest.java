package org.agilesoft.crud;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.dto.TaskDTO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.tasks_operations.AddTask;
import org.agilesoft.utils.WorkWithToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AddTaskTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private WorkWithToken wwt;

    @InjectMocks
    private AddTask addTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddTask_Success() {
        String token = "Bearer token";
        String username = "testuser";
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setStatus(true);
        taskDTO.setDescription("Test Description");

        User user = new User();
        user.setUsername(username);

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setStatus(Boolean.valueOf(taskDTO.isStatus() ? "true" : "false"));
        task.setDescription(taskDTO.getDescription());
        task.setUser(user);
        task.setCreationDate(LocalDateTime.now());
        task.setLastUpdatedDate(LocalDateTime.now());

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.save(any(Task.class))).thenReturn(task);

        Response response = addTask.addTask(token, taskDTO);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Guardado exitosamente", response.getEntity());
    }

    @Test
    public void testAddTask_UserNotFound() {
        String token = "Bearer token";
        String username = "testuser";
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setStatus(true);
        taskDTO.setDescription("Test Description");

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(null);

        Response response = addTask.addTask(token, taskDTO);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("User not found", response.getEntity());
    }

    @Test
    public void testAddTask_InvalidToken() {
        String token = "Invalid token";
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setStatus(true);
        taskDTO.setDescription("Test Description");

        when(wwt.extractUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        Response response = addTask.addTask(token, taskDTO);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid token", response.getEntity());
    }
}
