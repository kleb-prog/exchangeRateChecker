package com.lebedev.exchangeRate.dto;

import java.util.Date;

public class ChatDTO {

    private Long chatId;
    private String firstName;
    private String lastName;
    private Date createdAt;

    public ChatDTO() {
    }

    public ChatDTO(Long chatId, String firstName, String lastName, Date createdAt) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
