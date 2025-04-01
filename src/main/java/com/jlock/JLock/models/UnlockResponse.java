package com.jlock.JLock.models;

import com.jlock.JLock.interfaces.CommandResponse;

public record UnlockResponse(String lockName, LockState lockState) implements CommandResponse {

}
