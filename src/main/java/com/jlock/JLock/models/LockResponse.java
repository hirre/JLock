package com.jlock.JLock.models;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.jlock.JLock.interfaces.CommandResponse;

public record LockResponse(String lockName, UUID lockHolderId, LockState lockState, ZonedDateTime createdAt,
        ZonedDateTime updatedAt) implements CommandResponse {

}
