package com.jlock.JLock.handlers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jlock.JLock.interfaces.RequestHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.LockResponse;
import com.jlock.JLock.models.LockState;
import com.jlock.JLock.models.LockTable;
import com.jlock.JLock.models.Result;
import com.jlock.JLock.models.LockTable.SharedLock;

@Component
public class RequestLockHandler implements RequestHandler<LockRequest, LockResponse> {
    private final LockTable lockTable;

    public RequestLockHandler(LockTable lockTable) {
        this.lockTable = lockTable;
    }

    @Override
    @Async
    public CompletableFuture<Result<LockResponse>> handle(LockRequest request) {
        return CompletableFuture.supplyAsync(() -> {

            if (request.lockHolderId() == null)
                return Result.failure("Lock holder ID must not be null");

            SharedLock sharedLock = null;
            LockState lockState = LockState.ERROR;
            UUID lockHolderId = null;

            try {

                sharedLock = lockTable.getLock(request.lockName());
                sharedLock.getLock().lock();

                if (sharedLock.getLockState() == LockState.FREE) {
                    sharedLock.setLockState(LockState.WAIT, request.lockHolderId());
                } else if (sharedLock.getLockState() == LockState.ACQUIRED) {

                }

                lockState = sharedLock.getLockState();
                lockHolderId = sharedLock.getLockHolderId();

            } catch (Exception e) {
                lockState = LockState.ERROR;
                return Result.failure(String.format("%s \nLOCK STATE: %s (%s %s)",
                        e.getMessage(), lockState, request.lockName(), request.lockHolderId().toString()));
            } finally {
                if (sharedLock != null)
                    sharedLock.getLock().unlock();
            }

            return Result.success(new LockResponse(lockHolderId, lockState));
        });
    }

}
