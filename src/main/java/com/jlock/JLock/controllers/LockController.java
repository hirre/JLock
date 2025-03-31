package com.jlock.JLock.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/locks")
public class LockController {

    @GetMapping("/get-lock")
    public String getLock() {
        return new String();
    }
}
