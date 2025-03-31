package com.jlock.JLock.models;

import java.util.UUID;

import com.jlock.JLock.interfaces.RequestDto;

public record LockRequest(String lockName, UUID lockHolderId) implements RequestDto {
    public LockRequest {
        if (lockName == null)
            lockName = "default";
    }
}
