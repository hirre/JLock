package com.jlock.core.interfaces;

import java.util.concurrent.CompletableFuture;

import com.jlock.core.models.Result;

public interface CommandHandler<TRequest extends Command, TResponse extends CommandResponse> {
    CompletableFuture<Result<TResponse>> handle(TRequest request);
}
