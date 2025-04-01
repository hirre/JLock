package com.jlock.JLock.models;

import java.util.UUID;

import com.jlock.JLock.interfaces.Command;

public record UnlockRequest(String lockName, UUID lockHolderId) implements Command {
    public UnlockRequest {
        if (lockName == null)
            lockName = "default";
    }
}
