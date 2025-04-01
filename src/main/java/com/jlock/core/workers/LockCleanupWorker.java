package com.jlock.core.workers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jlock.core.models.LockTable;

@Component
public class LockCleanupWorker {
    private final LockTable lockTable;

    public LockCleanupWorker(LockTable lockTable) {
        this.lockTable = lockTable;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // once each hour (ms)
    public void cleanupExpiredLocks() {
        lockTable.releaseAllExpiredLocks();
    }
}
