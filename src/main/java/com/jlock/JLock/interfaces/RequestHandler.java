package com.jlock.JLock.interfaces;

import com.jlock.JLock.models.Result;

public interface RequestHandler<TRequest extends RequestDto, TResponse extends ResponseDto> {
    Result<TResponse> handle(TRequest request);
}
