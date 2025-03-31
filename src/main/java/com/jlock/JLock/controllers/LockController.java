package com.jlock.JLock.controllers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jlock.JLock.handlers.LockHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.LockResponse;

@RestController
@RequestMapping("/api/locks")
public class LockController {

    private final LockHandler lockHandler;

    public LockController(LockHandler lockHandler) {
        this.lockHandler = lockHandler;
    }

    @GetMapping("/get-lock")
    public ResponseEntity<LockResponse> getLock(@RequestParam(required = false) LockRequest request) {
        var result = lockHandler.handle(request);

        if (!result.isSuccess())
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));

        return new ResponseEntity<>(result.getValue(), HttpStatusCode.valueOf(200));
    }
}
