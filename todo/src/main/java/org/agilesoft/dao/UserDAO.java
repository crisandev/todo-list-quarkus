package org.agilesoft.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.agilesoft.entity.User;

@ApplicationScoped
public class UserDAO {


    @PersistenceContext
    private EntityManager em;

    @Transactional
    public User save(User user) {

        try {
            em.persist(user);
            em.flush();

            User savedUser = em.find(User.class, user.getId());
            if (savedUser == null) {
                throw new RuntimeException("Failed to save user");
            }

            return savedUser;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the user: " + e.getMessage(), e);
        }
    }


    @Transactional
    public User findByUsernameAndPassword(String username, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class).setParameter("username", username).setParameter("password", password).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    @Transactional
    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class).setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


}
