package org.agilesoft.login;

import jakarta.ws.rs.core.Response;
import org.agilesoft.dao.UserDAO;
import org.agilesoft.entity.User;
import org.agilesoft.session.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class SignupTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private Signup signup;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup_Success() {
        Signup.UserCredentials credentials = new Signup.UserCredentials();
        credentials.setUsername("newuser");
        credentials.setPassword("password123");
        credentials.setName("John Doe");

        when(userDAO.findByUsername("newuser")).thenReturn(null);

        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
        savedUser.setName("John Doe");

        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        Response response = signup.signup(credentials);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        User responseUser = (User) response.getEntity();
        assertEquals("newuser", responseUser.getUsername());
        assertEquals("John Doe", responseUser.getName());
        assertTrue(BCrypt.checkpw("password123", responseUser.getPassword()));
    }
}
