package org.agilesoft.tasks_operations;

import jakarta.json.bind.annotation.JsonbDateFormat;

import java.time.LocalDate;

public class Task {

    String id;
    String name;
    String state;
    String description;
    @JsonbDateFormat("yyyy-MM-dd")
    LocalDate createdDate;
    @JsonbDateFormat("yyyy-MM-dd")
    LocalDate  lastUpdatedDate;

    public Task() {
        this.setId("1");
        this.setLastUpdatedDate(LocalDate.now());
        this.setCreatedDate(LocalDate.now());
    }

    public Task(String id, String state){
        this.setState(state);
        this.setLastUpdatedDate(LocalDate.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

}
