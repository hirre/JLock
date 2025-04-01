package com.jlock.JLock.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jlock.JLock.interfaces.CommandHandler;
import com.jlock.JLock.models.LockRequest;
import com.jlock.JLock.models.LockResponse;
import com.jlock.JLock.models.LockState;
import com.jlock.JLock.models.UnlockRequest;
import com.jlock.JLock.models.UnlockResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/locks")
public class LockController {

    private final CommandHandler<LockRequest, LockResponse> lockHandler;
    private final CommandHandler<UnlockRequest, UnlockResponse> unlockHandler;

    public LockController(CommandHandler<LockRequest, LockResponse> lockHandler,
            CommandHandler<UnlockRequest, UnlockResponse> unlockHandler) {
        this.lockHandler = lockHandler;
        this.unlockHandler = unlockHandler;
    }

    @PostMapping("/lock")
    @Operation(summary = "Acquire a lock", description = "Acquire a named lock resource with the provided holder ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lock status retreived successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LockResponse.class))),
            @ApiResponse(responseCode = "201", description = "Lock acquired (state change) successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LockResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompletableFuture<ResponseEntity<?>> lock(
            @RequestBody LockRequest request) {
        return lockHandler.handle(request).thenApply(result -> {
            if (!result.isSuccess()) {
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problem.setTitle(result.getErrorMessage());

                if (result.getExtraParameter() != null && result.getExtraParameter() instanceof LockState)
                    problem.setDetail(((LockState) result.getExtraParameter()).toString());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
            }

            if (result.getValue().lockState() == LockState.ACQUIRED &&
                    result.getExtraParameter() != null &&
                    result.getExtraParameter() instanceof LockState &&
                    ((LockState) result.getExtraParameter()) == LockState.ACQUIRED)
                return ResponseEntity.status(201).body(result.getValue());
            else
                return ResponseEntity.ok(result.getValue());
        });
    }

    @PostMapping("/unlock")
    @Operation(summary = "Release a lock", description = "Release a previously acquired lock using the lock name and holder ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lock status retreived successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnlockResponse.class))),
            @ApiResponse(responseCode = "201", description = "Lock released (state change) successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnlockResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    public CompletableFuture<ResponseEntity<?>> unlock(
            @RequestBody UnlockRequest request) {
        return unlockHandler.handle(request).thenApply(result -> {
            if (!result.isSuccess()) {
                ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problem.setTitle(result.getErrorMessage());

                if (result.getExtraParameter() != null && result.getExtraParameter() instanceof LockState)
                    problem.setDetail(((LockState) result.getExtraParameter()).toString());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
            }

            if (result.getValue().lockState() == LockState.FREE &&
                    result.getExtraParameter() != null &&
                    result.getExtraParameter() instanceof LockState &&
                    ((LockState) result.getExtraParameter()) == LockState.FREE)
                return ResponseEntity.status(201).body(result.getValue());
            else
                return ResponseEntity.ok(result.getValue());
        });
    }
}
