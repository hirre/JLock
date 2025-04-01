package com.jlock.JLock.models;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockTable {
    private final ConcurrentHashMap<String, SharedLock> lockTable = new ConcurrentHashMap<>();
    private final Object tableLock = new Object();

    public SharedLock getOrCreateLock(String key) {
        if (!lockTable.containsKey(key)) {
            synchronized (tableLock) {
                if (!lockTable.containsKey(key)) // Stampade protection
                    lockTable.put(key, new SharedLock(key));
            }
        }

        return lockTable.get(key);
    }

    public class SharedLock {
        private final ReentrantLock lock;
        private final String lockName;
        private LockState lockState;
        private UUID lockHolderId = null;

        private final ZonedDateTime createdAt = ZonedDateTime.now(java.time.ZoneOffset.UTC);
        private ZonedDateTime updatedAt = createdAt;

        private ZonedDateTime expiresAt;
        private final Duration lockTimeout = Duration.ofMinutes(10); // Default 10-minute timeout

        public SharedLock(String lockName) {
            this.lock = new ReentrantLock();
            this.lockState = LockState.FREE;
            this.lockName = lockName;
        }

        public boolean isExpired() {
            return expiresAt != null && ZonedDateTime.now(ZoneOffset.UTC).isAfter(expiresAt);
        }

        public ZonedDateTime getExpiresAt() {
            return expiresAt;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public ZonedDateTime getUpdatedAt() {
            return updatedAt;
        }

        public String getLockName() {
            return this.lockName;
        }

        public ReentrantLock getInternalLock() {
            return this.lock;
        }

        public UUID getLockHolderId() {
            return this.lockHolderId;
        }

        public void setLockState(LockState state, UUID lockHolderId) {
            this.lockState = state;
            this.lockHolderId = lockHolderId;
            this.updatedAt = ZonedDateTime.now(java.time.ZoneOffset.UTC);

            if (state != LockState.FREE) {
                this.expiresAt = this.updatedAt.plus(this.lockTimeout);
            } else {
                this.expiresAt = null; // No expiration for free locks
            }
        }

        public LockState getLockState() {
            return this.lockState;
        }
    }
}
