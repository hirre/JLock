package com.jlock.core.handlers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jlock.core.interfaces.CommandHandler;
import com.jlock.core.models.LockRequest;
import com.jlock.core.models.LockResponse;
import com.jlock.core.models.LockState;
import com.jlock.core.models.LockTable;
import com.jlock.core.models.Result;

import lombok.extern.slf4j.Slf4j;

import com.jlock.core.models.LockTable.SharedLock;

@Service
@Slf4j
public class LockCommandHandler implements CommandHandler<LockRequest, LockResponse> {
    private final LockTable lockTable;

    public LockCommandHandler(LockTable lockTable) {
        this.lockTable = lockTable;
    }

    @Override
    @Async
    public CompletableFuture<Result<LockResponse>> handle(LockRequest request) {
        CompletableFuture<Result<LockResponse>> future = CompletableFuture.supplyAsync(() -> {

            if (request.lockHolderId() == null || request.lockName() == null || request.lockName().isEmpty()) {
                return Result.failure("Invalid lock request: lockHolderId and lockName must not be null or empty");
            }

            SharedLock sharedLock = null;

            try {

                sharedLock = lockTable.getOrCreateLock(request.lockName());

                if (!sharedLock.getInternalLock().tryLock(5, TimeUnit.SECONDS)) {
                    return Result.failure("Failed getting lock", LockState.INTERNAL_WAIT);
                }

                return handleLockAcquisition(sharedLock, request);

            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);

                return Result.failure(String.format("%s \nLOCK STATE: %s (%s %s)",
                        e.getMessage(), LockState.ERROR, request.lockName(), request.lockHolderId().toString()),
                        LockState.INTERNAL_INTERRUPTION);
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                return Result.failure(String.format("%s \nLOCK STATE: %s (%s %s)",
                        e.getMessage(), LockState.ERROR, request.lockName(), request.lockHolderId().toString()),
                        LockState.ERROR);
            } finally {
                if (sharedLock != null && sharedLock.getInternalLock().isHeldByCurrentThread()) {
                    try {
                        sharedLock.getInternalLock().unlock();
                    } catch (IllegalMonitorStateException e) {
                        log.warn("Attempted to unlock a lock not held by the current thread: {}", e.getMessage());
                    }
                }
            }
        });

        future.exceptionally(e -> {
            log.error("Error in async lock handling", e);
            return Result.failure("Internal error occurred");
        });

        return future;
    }

    private Result<LockResponse> handleLockAcquisition(SharedLock sharedLock, LockRequest request) {
        if (sharedLock.getLockState() == LockState.FREE) {
            sharedLock.setLockState(LockState.WAIT, request.lockHolderId());
            return Result.success(new LockResponse(sharedLock.getLockName(),
                    LockState.ACQUIRED, sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(),
                    sharedLock.getExpiresAt()),
                    LockState.ACQUIRED);
        }

        if (request.lockHolderId().equals(sharedLock.getLockHolderId())) {
            return Result.success(new LockResponse(sharedLock.getLockName(),
                    LockState.ACQUIRED, sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(),
                    sharedLock.getExpiresAt()));
        }

        return Result.success(new LockResponse(sharedLock.getLockName(),
                sharedLock.getLockState(), sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(),
                sharedLock.getExpiresAt()));
    }
}
