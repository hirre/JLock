package com.jlock.core.handlers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jlock.core.interfaces.CommandHandler;
import com.jlock.core.models.LockRequest;
import com.jlock.core.models.LockResponse;
import com.jlock.core.models.LockState;
import com.jlock.core.models.LockTable;
import com.jlock.core.models.Result;

import lombok.extern.slf4j.Slf4j;

import com.jlock.core.models.LockTable.SharedLock;

@Component
@Slf4j
public class LockCommandHandler implements CommandHandler<LockRequest, LockResponse> {
    private final LockTable lockTable;

    public LockCommandHandler(LockTable lockTable) {
        this.lockTable = lockTable;
    }

    @Override
    @Async
    public CompletableFuture<Result<LockResponse>> handle(LockRequest request) {
        return CompletableFuture.supplyAsync(() -> {

            if (request.lockHolderId() == null)
                return Result.failure("Lock holder ID must not be null");

            SharedLock sharedLock = null;

            try {

                sharedLock = lockTable.getOrCreateLock(request.lockName());

                if (!sharedLock.getInternalLock().tryLock(5, TimeUnit.SECONDS)) {
                    sharedLock = null;
                    return Result.failure("Failed getting lock", LockState.INTERNAL_WAIT);
                }

                // Check and reset if expired
                if (sharedLock.isExpired() && sharedLock.getLockState() != LockState.FREE) {
                    // Log that we're auto-resetting an expired lock
                    sharedLock.setLockState(LockState.FREE, new UUID(0L, 0L));
                }

                // If the lock is free we can set it to WAIT globally to indicate this client
                // has it, we return ACQUIRED for this specific response though
                if (sharedLock.getLockState() == LockState.FREE) {
                    sharedLock.setLockState(LockState.WAIT, request.lockHolderId());
                    return Result.success(new LockResponse(sharedLock.getLockName(),
                            LockState.ACQUIRED, sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(), sharedLock
                                    .getExpiresAt()),
                            LockState.ACQUIRED);
                }

                // If the client request has the same UUID as the lock in the storage we can set
                // the returned response state to ACQUIRED, indicating to the client that it has
                // the lock, otherwise return the stored state
                if (request.lockHolderId().equals(sharedLock.getLockHolderId()))
                    return Result.success(new LockResponse(sharedLock.getLockName(),
                            LockState.ACQUIRED, sharedLock.getCreatedAt(),
                            sharedLock.getUpdatedAt(), sharedLock.getExpiresAt()));
                else
                    return Result.success(
                            new LockResponse(sharedLock.getLockName(), sharedLock.getLockState(),
                                    sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(), sharedLock.getExpiresAt()));

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
                if (sharedLock != null && sharedLock.getInternalLock().isHeldByCurrentThread())
                    sharedLock.getInternalLock().unlock();
            }
        });
    }
}
