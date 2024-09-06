package org.agilesoft.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Task {

    @Id
    private String id;

    private String title;

    @Column(name = "status")
    private Boolean status;

    private String description;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {
        this.id = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.lastUpdatedDate = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
