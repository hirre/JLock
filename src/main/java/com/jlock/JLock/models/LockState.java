package com.jlock.JLock.models;

public enum LockState {
    ERROR,
    FREE,
    ACQUIRED,
    WAIT,
    INTERNAL_WAIT,
    INTERNAL_INTERRUPTION;
}
