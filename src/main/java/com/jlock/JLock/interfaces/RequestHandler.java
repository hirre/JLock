package com.jlock.JLock.interfaces;

import java.util.concurrent.CompletableFuture;

import com.jlock.JLock.models.Result;

public interface RequestHandler<TRequest extends RequestDto, TResponse extends ResponseDto> {
    CompletableFuture<Result<TResponse>> handle(TRequest request);
}
