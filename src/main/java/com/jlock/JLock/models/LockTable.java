package com.jlock.JLock.models;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockTable {
    private final ConcurrentHashMap<String, SharedLock> lockTable = new ConcurrentHashMap<>();

    public SharedLock getLock(String key) {
        if (!lockTable.containsKey(key)) {
            lockTable.put(key, new SharedLock(key));
        }

        return lockTable.get(key);
    }

    public class SharedLock {
        private final ReentrantLock lock;
        private final String lockName;
        private LockState lockState;
        private UUID lockHolderId;

        public SharedLock(String lockName) {
            this.lock = new ReentrantLock();
            this.lockState = LockState.FREE;
            this.lockName = lockName;
        }

        public String getLockName() {
            return this.lockName;
        }

        public ReentrantLock getLock() {
            return this.lock;
        }

        public UUID getLockHolderId() {
            return this.lockHolderId;
        }

        public void setLockState(LockState state, UUID lockHolderId) {
            this.lockState = state;
            this.lockHolderId = lockHolderId;
        }

        public LockState getLockState() {
            return this.lockState;
        }
    }
}
