package com.jlock.JLock.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class LockController {

    @GetMapping("/api/lock/get-lock")
    public String getLock(@RequestParam String param) {
        return new String();
    }
}
