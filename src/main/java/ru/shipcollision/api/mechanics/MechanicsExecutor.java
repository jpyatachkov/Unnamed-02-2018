package ru.shipcollision.api.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class MechanicsExecutor implements Runnable {

    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(MechanicsExecutor.class);

    private static final long GM_STEP_TIME = 50;

    private final @NotNull GameMechanics gameMechanics;

    private final Executor tickExecutor = Executors.newSingleThreadExecutor();

    public MechanicsExecutor(@NotNull GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    public void initAfterStartup() {
        tickExecutor.execute(this);
    }

    @Override
    public void run() {
        try {
            mainCycle();
        } finally {
            LOGGER.warn("Выполнение механики было прервано");
        }
    }

    private void mainCycle() {
        while (true) {
            try {
                gameMechanics.gmStep();

                try {
                    Thread.sleep(GM_STEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
            } catch (RuntimeException e) {
                LOGGER.error("Исключение", e);
            }
        }
    }
}
