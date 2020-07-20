package com.hextech.smarttime.util;

import java.util.Date;

public class ToDoItem {

    int recordID = 0;
    String title, description, category;
    Date createdDate, dueDate;

    public ToDoItem(String title, String description, String category, Date createdDate, Date dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "itemNumber=" + recordID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", createdDate=" + createdDate +
                ", dueDate=" + dueDate +
                '}';
    }
}
