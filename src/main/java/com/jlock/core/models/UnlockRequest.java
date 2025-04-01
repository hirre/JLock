package com.jlock.core.models;

import java.util.UUID;

import com.jlock.core.interfaces.Command;

public record UnlockRequest(String lockName, UUID lockHolderId) implements Command {
    public UnlockRequest {
        if (lockName == null)
            lockName = "default";
    }
}
