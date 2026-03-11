package com.myfinance.controller.dto;

public class ChatResponseDTO {
    private String message;
    private String status;
    private boolean isTransaction;
    private boolean showRepeat;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public ChatResponseDTO(String message, String status, boolean isTransaction) {
        this.message = message;
        this.status = status;
        this.isTransaction = isTransaction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isTransaction() {
        return isTransaction;
    }

    public void setTransaction(boolean transaction) {
        isTransaction = transaction;
    }

    public boolean isShowRepeat() {
        return showRepeat;
    }

    public void setShowRepeat(boolean showRepeat) {
        this.showRepeat = showRepeat;
    }
}
