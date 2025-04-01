package com.jlock.JLock.models;

import java.util.UUID;

import com.jlock.JLock.interfaces.CommandResponse;

public record LockResponse(UUID lockHolderId, LockState state) implements CommandResponse {

}
