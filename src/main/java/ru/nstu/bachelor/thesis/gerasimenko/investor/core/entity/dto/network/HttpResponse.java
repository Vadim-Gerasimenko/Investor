package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

public record HttpResponse<T>(String status, String message, T body) {

    public HttpResponse(String status, String message) {
        this(status, message, null);
    }
}