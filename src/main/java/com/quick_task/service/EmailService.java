package com.quick_task.service;


import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> send(String to, String email);
}