package com.jlock.JLock.handlers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jlock.JLock.interfaces.CommandHandler;
import com.jlock.JLock.models.LockState;
import com.jlock.JLock.models.LockTable;
import com.jlock.JLock.models.LockTable.SharedLock;
import com.jlock.JLock.models.Result;
import com.jlock.JLock.models.UnlockRequest;
import com.jlock.JLock.models.UnlockResponse;

@Component
public class UnlockCommandHandler implements CommandHandler<UnlockRequest, UnlockResponse> {
    private final LockTable lockTable;

    public UnlockCommandHandler(LockTable lockTable) {
        this.lockTable = lockTable;
    }

    @Override
    @Async
    public CompletableFuture<Result<UnlockResponse>> handle(UnlockRequest request) {
        return CompletableFuture.supplyAsync(() -> {

            if (request.lockHolderId() == null)
                return Result.failure("Lock holder ID must not be null");

            SharedLock sharedLock = null;

            try {

                sharedLock = lockTable.getLock(request.lockName());
                sharedLock.getLock().lock();

                // If the lock isn't free and the client request UUID is the same as the stored
                // one, then we can unlock
                if (sharedLock.getLockState() != LockState.FREE
                        && sharedLock.getLockHolderId().equals(request.lockHolderId())) {

                    sharedLock.setLockState(LockState.FREE, new UUID(0L, 0L));

                    return Result.success(new UnlockResponse(sharedLock.getLockName(), LockState.FREE,
                            sharedLock.getCreatedAt(), sharedLock.getUpdatedAt()), LockState.FREE);

                    // Default initialized value, if unlock is called before lock
                } else if (sharedLock.getLockState() == LockState.FREE && sharedLock.getLockHolderId() == null) {

                    sharedLock.setLockState(LockState.FREE, request.lockHolderId());

                    return Result.success(new UnlockResponse(sharedLock.getLockName(), sharedLock.getLockState(),
                            sharedLock.getCreatedAt(), sharedLock.getUpdatedAt()), LockState.FREE);
                }

                return Result.success(new UnlockResponse(sharedLock.getLockName(), sharedLock.getLockState(),
                        sharedLock.getCreatedAt(), sharedLock.getUpdatedAt()));

            } catch (Exception e) {

                return Result.failure(String.format("%s \nLOCK STATE: %s (%s %s)",
                        e.getMessage(), LockState.ERROR, request.lockName(), request.lockHolderId().toString()));
            } finally {

                if (sharedLock != null)
                    sharedLock.getLock().unlock();
            }
        });
    }

}
