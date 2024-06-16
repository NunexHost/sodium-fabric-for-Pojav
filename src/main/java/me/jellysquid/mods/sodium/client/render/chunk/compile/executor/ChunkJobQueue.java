package me.jellysquid.mods.sodium.client.render.chunk.compile.executor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class ChunkJobQueue {
    private final ArrayDeque<ChunkJob> jobs = new ArrayDeque<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public boolean isRunning() {
        return this.isRunning.get();
    }

    public void add(ChunkJob job, boolean important) {
        Validate.isTrue(this.isRunning(), "Queue is no longer running");

        if (important) {
            this.jobs.addFirst(job);
        } else {
            this.jobs.addLast(job);
        }

        executorService.submit(() -> processNextJob());
    }

    private void processNextJob() {
        if (!isRunning()) {
            return;
        }

        ChunkJob job = jobs.poll();
        if (job != null) {
            // Execute the job on the current thread
            job.run();
        }
    }

    public Collection<ChunkJob> shutdown() {
        isRunning.set(false);

        // Wait for the running job to finish
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public int size() {
        return jobs.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
