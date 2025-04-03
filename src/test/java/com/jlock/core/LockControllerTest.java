package com.jlock.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jlock.core.controllers.LockController;
import com.jlock.core.interfaces.CommandHandler;
import com.jlock.core.models.LockRequest;
import com.jlock.core.models.LockResponse;
import com.jlock.core.models.LockTable;
import com.jlock.core.models.UnlockRequest;
import com.jlock.core.models.UnlockResponse;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@DirtiesContext
public class LockControllerTest {

    @Autowired
    private LockController lockController;

    @SuppressWarnings("unused")
	@Autowired
    private CommandHandler<LockRequest, LockResponse> lockHandler;

    @SuppressWarnings("unused")
	@Autowired
    private CommandHandler<UnlockRequest, UnlockResponse> unlockHandler;

    @Autowired
    private LockTable lockTable;

    @BeforeEach
    public void reset() {
        lockTable.clearLocks();
    }

    @ParameterizedTest
    @ValueSource(strings = "65fb0a8f-63e1-4ea1-8c51-e44d082a160e")
    public void testLockEndpoint(UUID id) throws Exception {
        var lockRequest = new LockRequest("default", id);
        var res = lockController.lock(lockRequest).join(); // Run the CompletableFuture and wait for its completion

        assertEquals(201, res.getStatusCode().value());
    }

    @Test
    public void testUnlockEndpoint() throws Exception {
        var id = UUID.randomUUID();
        testLockEndpoint(id);

        var unlockRequest = new UnlockRequest("default", id);
        var res = lockController.unlock(unlockRequest).join(); // Run the CompletableFuture and wait for its completion

        assertEquals(201, res.getStatusCode().value());
    }
}