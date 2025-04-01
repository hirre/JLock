package com.jlock.JLock.models;

import com.jlock.JLock.interfaces.CommandResponse;

public record UnlockResponse(LockState lockState) implements CommandResponse {

}
