package com.jlock.JLock.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jlock.JLock.handlers.LockCommandHandler;
import com.jlock.JLock.handlers.UnlockCommandHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.UnlockRequest;

@RestController
@RequestMapping("/api/locks")
public class LockController {

    private final LockCommandHandler lockHandler;
    private final UnlockCommandHandler unlockHandler;

    public LockController(LockCommandHandler lockHandler, UnlockCommandHandler unlockHandler) {
        this.lockHandler = lockHandler;
        this.unlockHandler = unlockHandler;
    }

    @PostMapping("/lock")
    public CompletableFuture<ResponseEntity<?>> lock(
            @RequestBody LockRequest request) {
        return lockHandler.handle(request).thenApply(result -> {
            if (!result.isSuccess()) {
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problem.setTitle(result.getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
            }

            return ResponseEntity.ok(result.getValue());
        });
    }

    @PostMapping("/unlock")
    public CompletableFuture<ResponseEntity<?>> unlock(
            @RequestBody UnlockRequest request) {
        return unlockHandler.handle(request).thenApply(result -> {
            if (!result.isSuccess()) {
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problem.setTitle(result.getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
            }

            return ResponseEntity.ok(result.getValue());
        });
    }
}
