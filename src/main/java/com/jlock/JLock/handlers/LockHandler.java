package com.jlock.JLock.handlers;

import org.springframework.stereotype.Component;

import com.jlock.JLock.interfaces.RequestHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.LockResponse;
import com.jlock.JLock.models.Result;

@Component
public class LockHandler implements RequestHandler<LockRequest, LockResponse> {

    @Override
    public Result<LockResponse> handle(LockRequest request) {
        var response = new LockResponse();

        try {

        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }

        return Result.success(response);
    }

}
