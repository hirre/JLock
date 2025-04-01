package com.jlock.core.models;

import java.time.ZonedDateTime;

import com.jlock.core.interfaces.CommandResponse;

public record UnlockResponse(String lockName, LockState lockState, ZonedDateTime createdAt,
                ZonedDateTime updatedAt, ZonedDateTime expiresAt) implements CommandResponse {

}
