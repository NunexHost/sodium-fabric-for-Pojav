package me.jellysquid.mods.sodium.client.render.chunk.compile.executor;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderTask;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChunkBuilder {
    static final Logger LOGGER = LogManager.getLogger("ChunkBuilder");

    private final ChunkJobQueue queue = new ChunkJobQueue();

    private final ExecutorService executor;

    private final ChunkBuildContext localContext;

    public ChunkBuilder(ClientWorld world, ChunkVertexType vertexType) {
        int threadCount = getThreadCount();
        this.executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            ChunkBuildContext context = new ChunkBuildContext(world, vertexType);
            WorkerRunnable worker = new WorkerRunnable(context);
            this.executor.submit(worker);
        }

        LOGGER.info("Started {} worker threads", threadCount);

        this.localContext = new ChunkBuildContext(world, vertexType);
    }

    /**
     * Returns the remaining number of build tasks which should be scheduled this frame. If an attempt is made to
     * spawn more tasks than the budget allows, it will block until resources become available.
     */
    public int getSchedulingBudget() {
        return Math.max(0, this.executor instanceof ThreadPoolExecutor ?
                ((ThreadPoolExecutor) this.executor).getQueue().remainingCapacity() : Integer.MAX_VALUE);
    }

    /**
     * Notifies all worker threads to stop and blocks until all workers terminate. After the workers have been shut
     * down, all tasks are cancelled and the pending queues are cleared. If the builder is already stopped, this
     * method does nothing and exits.
     */
    public void shutdown() {
        if (this.executor.isShutdown()) {
            return;
        }

        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOGGER.error("ChunkBuilder executor did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            this.executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Clear any remaining jobs in the queue
        var jobs = this.queue.shutdown();
        jobs.forEach(ChunkJob::setCancelled);

        LOGGER.info("Stopped worker threads");
    }

    public <TASK extends ChunkBuilderTask<OUTPUT>, OUTPUT> ChunkJobTyped<TASK, OUTPUT> scheduleTask(TASK task, boolean important,
                                                                                                //Consumer<ChunkJobResult<OUTPUT>> consumer)
{
    Validate.notNull(task, "Task must be non-null");

    if (this.executor.isShutdown()) {
        throw new IllegalStateException("Executor is stopped");
    }

    var job = new ChunkJobTyped<>(task, consumer);

    this.queue.add(job, important);

    return job;
}
    
    /**
     * Returns the "optimal" number of threads to be used for chunk build tasks. This will always return at least one
     * thread.
     */
    private static int getOptimalThreadCount() {
        return MathHelper.clamp(Math.max(getMaxThreadCount() / 3, getMaxThreadCount() - 6), 1, 10);
    }

    private static int getThreadCount() {
        int requested = SodiumClientMod.options().performance.chunkBuilderThreads;
        return requested == 0 ? getOptimalThreadCount() : Math.min(requested, getMaxThreadCount());
    }

    private static int getMaxThreadCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public void tryStealTask(ChunkJob job) {
        if (!this.queue.stealJob(job)) {
            return;
        }

        var localContext = this.localContext;

        try {
            job.execute(localContext);
        } finally {
            localContext.cleanup();
        }
    }

    public boolean isBuildQueueEmpty() {
        return this.queue.isEmpty();
    }

    public int getScheduledJobCount() {
        return this.queue.size();
    }

    public int getBusyThreadCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) this.executor).getActiveCount();
        } else {
            return 0;
        }
    }

    public int getTotalThreadCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) this.executor).getMaximumPoolSize();
        } else {
            return 0;
        }
    }

    private class WorkerRunnable implements Runnable {
        private final ChunkBuildContext context;

        public WorkerRunnable(ChunkBuildContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !executor.isShutdown()) {
                ChunkJob job;

                try {
                    job = queue.waitForNextJob();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    continue;
                }

                if (job == null) {
                    continue;
                }

                try {
                    job.execute(this.context);
                } finally {
                    this.context.cleanup();
                }
            }
        }
    }
}
