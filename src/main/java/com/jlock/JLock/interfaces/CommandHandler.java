package com.jlock.JLock.interfaces;

import java.util.concurrent.CompletableFuture;

import com.jlock.JLock.models.Result;

public interface CommandHandler<TRequest extends Command, TResponse extends CommandResponse> {
    CompletableFuture<Result<TResponse>> handle(TRequest request);
}
