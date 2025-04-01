package com.jlock.JLock.models;

import java.util.UUID;

import com.jlock.JLock.interfaces.Command;

public record LockRequest(String lockName, UUID lockHolderId) implements Command {
    public LockRequest {
        if (lockName == null)
            lockName = "default";
    }
}
