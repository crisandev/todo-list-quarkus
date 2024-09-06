package org.agilesoft.crud;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.TaskDAO;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.Task;
import org.agilesoft.entity.User;
import org.agilesoft.tasks_operations.GetTasks;
import org.agilesoft.utils.WorkWithToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GetTasksTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private WorkWithToken wwt;

    @InjectMocks
    private GetTasks getTasks;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTasks_Success() {
        String token = "Bearer validToken";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        Task task1 = new Task();
        task1.setId("1");
        task1.setTitle("Task 1");
        task1.setStatus(true);
        task1.setDescription("Description 1");
        task1.setCreationDate(LocalDateTime.now());
        task1.setLastUpdatedDate(LocalDateTime.now());

        Task task2 = new Task();
        task2.setId("2");
        task2.setTitle("Task 2");
        task2.setStatus(false);
        task2.setDescription("Description 2");
        task2.setCreationDate(LocalDateTime.now());
        task2.setLastUpdatedDate(LocalDateTime.now());

        List<Task> tasks = Arrays.asList(task1, task2);

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(user);
        when(taskDAO.findByUsername(username)).thenReturn(tasks);

        Response response = getTasks.getTasks(token);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        JsonArray jsonArray = (JsonArray) response.getEntity();
        assertEquals(2, jsonArray.size());

        JsonObject jsonObject1 = jsonArray.getJsonObject(0);
        assertEquals("1", jsonObject1.getString("id"));
        assertEquals("Task 1", jsonObject1.getString("title"));
        assertEquals(true, jsonObject1.getBoolean("status"));
        assertEquals("Description 1", jsonObject1.getString("description"));

        JsonObject jsonObject2 = jsonArray.getJsonObject(1);
        assertEquals("2", jsonObject2.getString("id"));
        assertEquals("Task 2", jsonObject2.getString("title"));
        assertEquals(false, jsonObject2.getBoolean("status"));
        assertEquals("Description 2", jsonObject2.getString("description"));
    }

    @Test
    public void testGetTasks_UserNotFound() {
        String token = "Bearer validToken";
        String username = "testuser";

        when(wwt.extractUsernameFromToken(token)).thenReturn(username);
        when(userDAO.findByUsername(username)).thenReturn(null);

        Response response = getTasks.getTasks(token);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("User not found", response.getEntity());
        verify(taskDAO, never()).findByUsername(anyString());
    }

    @Test
    public void testGetTasks_InvalidToken() {
        String token = "Bearer invalidToken";

        when(wwt.extractUsernameFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        Response response = getTasks.getTasks(token);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals("Invalid token", response.getEntity());
        verify(userDAO, never()).findByUsername(anyString());
        verify(taskDAO, never()).findByUsername(anyString());
    }
}
