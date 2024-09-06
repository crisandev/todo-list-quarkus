package org.agilesoft.login;

import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.User;
import org.agilesoft.session.Login;
import org.agilesoft.utils.WorkWithSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.agilesoft.authentication.AuthenticationJWTService;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private AuthenticationJWTService jwtService;

    @InjectMocks
    private Login loginService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("$2a$10$hashedpassword");

        when(userDAO.findByUsername("testuser")).thenReturn(mockUser);

        when(jwtService.generateJWT("testuser")).thenReturn("fake-jwt-token");

        WorkWithSession.UserCredentials credentials = new WorkWithSession.UserCredentials();
        credentials.setUsername("testuser");
        credentials.setPassword("password");

        Response response = loginService.login(credentials);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(((WorkWithSession.LoginResponse) response.getEntity()).token);
    }

    @Test
    public void testLoginFailure() {
        when(userDAO.findByUsername("unknownUser")).thenReturn(null);

        WorkWithSession.UserCredentials credentials = new WorkWithSession.UserCredentials();
        credentials.setUsername("unknownUser");
        credentials.setPassword("password");

        Response response = loginService.login(credentials);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
}
