package com.test.notification.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String recipient;
    private boolean seen;

    public Notification() {
    }

    public Notification(String message, String recipient, boolean seen) {
        this.message = message;
        this.recipient = recipient;
        this.seen = seen;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
