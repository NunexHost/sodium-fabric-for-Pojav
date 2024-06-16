package me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Arrays;

public class BlockRenderContext {
    private final WorldSlice world;

    private final BlockPos.Mutable[] posPool;
    private final Vector3f[] originPool;
    private int posIndex = 0;
    private int originIndex = 0;

    private BlockState state;
    private BakedModel model;
    private long seed;

    public BlockRenderContext(WorldSlice world, int poolSize) {
        this.world = world;
        posPool = new BlockPos.Mutable[poolSize];
        originPool = new Vector3f[poolSize];
        for (int i = 0; i < poolSize; i++) {
            posPool[i] = new BlockPos.Mutable();
            originPool[i] = new Vector3f();
        }
    }

    public void update(BlockPos pos, BlockPos origin, BlockState state, BakedModel model, long seed) {
        this.state = state;
        this.model = model;
        this.seed = seed;

        BlockPos.Mutable nextPos = posPool[posIndex];
        nextPos.set(pos);
        posIndex = (posIndex + 1) % posPool.length;

        Vector3f nextOrigin = originPool[originIndex];
        nextOrigin.set(origin.getX(), origin.getY(), origin.getZ());
        originIndex = (originIndex + 1) % originPool.length;
    }

    public BlockPos pos() {
        return posPool[posIndex];
    }

    public WorldSlice world() {
        return world;
    }

    public BlockState state() {
        return state;
    }

    public BakedModel model() {
        return model;
    }

    public Vector3fc origin() {
        return originPool[originIndex];
    }

    public long seed() {
        return seed;
    }
}
