package com.jlock.core.models;

public enum LockState {
    ERROR,
    FREE,
    ACQUIRED,
    WAIT,
    INTERNAL_WAIT,
    INTERNAL_INTERRUPTION;
}
