package com.jlock.JLock.handlers;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jlock.JLock.interfaces.RequestHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.LockResponse;
import com.jlock.JLock.models.Result;

@Component
public class LockHandler implements RequestHandler<LockRequest, LockResponse> {

    @Override
    @Async
    public CompletableFuture<Result<LockResponse>> handle(LockRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            var response = new LockResponse();

            try {

            } catch (Exception e) {
                return Result.failure(e.getMessage());
            }

            return Result.success(response);
        });
    }

}
