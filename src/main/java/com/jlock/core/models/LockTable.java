package com.jlock.core.models;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Getter;

import org.springframework.stereotype.Repository;

import com.jlock.core.configuration.LockConfig;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.Setter;

@Repository
@Slf4j
public class LockTable {

    private final Duration lockTimeout;

    private final Cache<String, SharedLock> lockStorage;

    private final Object tableLock = new Object();

    public LockTable(LockConfig lockConfig) {
        this.lockTimeout = lockConfig.defaultLockTimeout();
        this.lockStorage = CacheBuilder.newBuilder()
                .expireAfterAccess(lockTimeout.toMinutes(), TimeUnit.MINUTES) // Reset expiration on access
                .build();
    }

    public SharedLock getOrCreateLock(String key) {
        if (lockStorage.getIfPresent(key) == null) {
            synchronized (tableLock) {
                if (lockStorage.getIfPresent(key) == null) // Stampade protection
                    lockStorage.put(key, new SharedLock(key));
            }
        }

        var sharedLock = lockStorage.getIfPresent(key);
        sharedLock.setExpiresAt(ZonedDateTime.now(java.time.ZoneOffset.UTC).plus(lockTimeout));
        return sharedLock;
    }

    // For testing purposes, don't use
    @VisibleForTesting
    public void clearLocks() {
        synchronized (tableLock) {
            lockStorage.asMap().forEach((_, v) -> {
                try {
                    v.getInternalLock().unlock();
                } catch (Exception _) {
                }
            });

            lockStorage.invalidateAll();
            lockStorage.cleanUp();
        }
    }

    public class SharedLock {

        private final ReentrantLock lock;

        @Getter
        private final String lockName;

        @Getter
        private LockState lockState;

        @Getter
        private UUID lockHolderId = null;

        @Getter
        private final ZonedDateTime createdAt = ZonedDateTime.now(java.time.ZoneOffset.UTC);

        @Getter
        private ZonedDateTime updatedAt = createdAt;

        @Getter
        @Setter
        private ZonedDateTime expiresAt;

        public SharedLock(String lockName) {
            this.lock = new ReentrantLock();
            this.lockState = LockState.FREE;
            this.lockName = lockName;
        }

        public ReentrantLock getInternalLock() {
            return this.lock;
        }

        public void setLockState(LockState state, UUID lockHolderId) {
            this.lockState = state;
            this.lockHolderId = lockHolderId;
            this.updatedAt = ZonedDateTime.now(java.time.ZoneOffset.UTC);
        }

    }
}
