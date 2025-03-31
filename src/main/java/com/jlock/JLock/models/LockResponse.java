package com.jlock.JLock.models;

import java.util.UUID;

import com.jlock.JLock.interfaces.ResponseDto;

public record LockResponse(UUID lockHolderId, LockState state) implements ResponseDto {

}
