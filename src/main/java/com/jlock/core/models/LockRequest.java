package com.jlock.core.models;

import java.util.UUID;

import com.jlock.core.interfaces.Command;

public record LockRequest(String lockName, UUID lockHolderId) implements Command {
    public LockRequest {
        if (lockName == null)
            lockName = "default";
    }
}
