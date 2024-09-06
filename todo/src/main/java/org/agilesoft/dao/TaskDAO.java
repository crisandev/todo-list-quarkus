package org.agilesoft.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.agilesoft.entity.Task;

import java.util.List;

@ApplicationScoped
public class TaskDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Task save(Task task) {
        if (task.getId() == null) {
            em.persist(task);
        } else {
            em.merge(task);
        }
        return task;
    }

    public Task findById(String id) {
        return em.find(Task.class, id);
    }

    public List<Task> findAll() {
        return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
    }

    public List<Task> findByUsername(String username) {
        return em.createQuery("SELECT t FROM Task t WHERE t.user.username = :username", Task.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Transactional
    public void delete(Task task) {
        Task managedTask = em.contains(task) ? task : em.merge(task);
        em.createQuery("delete from Task where id = :id")
                .setParameter("id", task.getId())
                .executeUpdate();
    }
}
