package com.example.testapi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "images111")
public class Image {

    @Id
    @Column(name = "fileName")
    private String fileName;

    @Column(name = "prompt")
    private String prompt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creationDate", nullable = false, updatable = false)
    private Date creationDate;

    public Image() {
        this.creationDate = new Date();
    }

    // Getters and setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
