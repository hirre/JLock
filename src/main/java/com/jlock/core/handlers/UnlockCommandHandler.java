package com.jlock.core.handlers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jlock.core.interfaces.CommandHandler;
import com.jlock.core.models.LockState;
import com.jlock.core.models.LockTable;
import com.jlock.core.models.Result;
import com.jlock.core.models.UnlockRequest;
import com.jlock.core.models.UnlockResponse;

import lombok.extern.slf4j.Slf4j;

import com.jlock.core.models.LockTable.SharedLock;

@Service
@Slf4j
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

                sharedLock = lockTable.getOrCreateLock(request.lockName());

                if (!sharedLock.getInternalLock().tryLock(5, TimeUnit.SECONDS)) {
                    sharedLock = null;
                    return Result.failure("Failed getting lock", LockState.INTERNAL_WAIT);
                }

                // If the lock isn't free and the client request UUID is the same as the stored
                // one, then we can unlock
                if (sharedLock.getLockState() != LockState.FREE
                        && sharedLock.getLockHolderId().equals(request.lockHolderId())) {

                    sharedLock.setLockState(LockState.FREE, new UUID(0L, 0L));

                    return Result.success(new UnlockResponse(sharedLock.getLockName(), LockState.FREE,
                            sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(), sharedLock.getExpiresAt()),
                            LockState.FREE);

                    // Default initialized value, if unlock is called before lock
                } else if (sharedLock.getLockState() == LockState.FREE && sharedLock.getLockHolderId() == null) {

                    sharedLock.setLockState(LockState.FREE, request.lockHolderId());

                    return Result.success(new UnlockResponse(sharedLock.getLockName(), sharedLock.getLockState(),
                            sharedLock.getCreatedAt(), sharedLock.getUpdatedAt(),
                            sharedLock.getExpiresAt()), LockState.FREE);
                }

                return Result.success(new UnlockResponse(sharedLock.getLockName(), sharedLock.getLockState(),
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
