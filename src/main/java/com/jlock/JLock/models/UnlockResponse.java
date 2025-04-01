package com.jlock.JLock.models;

import java.time.ZonedDateTime;

import com.jlock.JLock.interfaces.CommandResponse;

public record UnlockResponse(String lockName, LockState lockState, ZonedDateTime createdAt,
                ZonedDateTime updatedAt, ZonedDateTime expiresAt) implements CommandResponse {

}
