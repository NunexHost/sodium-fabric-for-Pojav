package me.jellysquid.mods.sodium.client.render.chunk.data;

import me.jellysquid.mods.sodium.client.gl.arena.GlBufferSegment;
import me.jellysquid.mods.sodium.client.gl.util.VertexRange;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;

import java.util.HashMap;
import java.util.Map;

public class SectionRenderDataStorage {
    private final Map<Integer, GlBufferSegment> allocations = new HashMap<>();

    private final long pMeshDataArray;

    public SectionRenderDataStorage() {
        this.pMeshDataArray = SectionRenderDataUnsafe.allocateHeap(RenderRegion.REGION_SIZE);
    }

    public void setMeshes(int localSectionIndex,
                          GlBufferSegment allocation, VertexRange[] ranges) {
        this.allocations.put(localSectionIndex, allocation);

        var pMeshData = this.getDataPointer(localSectionIndex);

        int sliceMask = 0;
        int vertexOffset = allocation.getOffset();

        for (int facingIndex = 0; facingIndex < ModelQuadFacing.COUNT; facingIndex++) {
            VertexRange vertexRange = ranges[facingIndex];
            int vertexCount;

            if (vertexRange != null) {
                vertexCount = vertexRange.vertexCount();
            } else {
                vertexCount = 0;
            }

            SectionRenderDataUnsafe.setVertexOffset(pMeshData, facingIndex, vertexOffset);
            SectionRenderDataUnsafe.setElementCount(pMeshData, facingIndex, (vertexCount >> 2) * 6);

            if (vertexCount > 0) {
                sliceMask |= 1 << facingIndex;
            }

            vertexOffset += vertexCount;
        }

        SectionRenderDataUnsafe.setSliceMask(pMeshData, sliceMask);
    }

    public void removeMeshes(int localSectionIndex) {
        GlBufferSegment allocation = this.allocations.remove(localSectionIndex);

        if (allocation != null) {
            allocation.delete();
        }

        SectionRenderDataUnsafe.clear(this.getDataPointer(localSectionIndex));
    }

    public void onBufferResized() {
        for (int sectionIndex = 0; sectionIndex < RenderRegion.REGION_SIZE; sectionIndex++) {
            this.updateMeshes(sectionIndex);
        }
    }

    private void updateMeshes(int sectionIndex) {
        GlBufferSegment allocation = this.allocations.get(sectionIndex);

        if (allocation == null) {
            return;
        }

        var offset = allocation.getOffset();
        var data = this.getDataPointer(sectionIndex);

        for (int facing = 0; facing < ModelQuadFacing.COUNT; facing++) {
            SectionRenderDataUnsafe.setVertexOffset(data, facing, offset);

            var count = SectionRenderDataUnsafe.getElementCount(data, facing);
            offset += (count / 6) * 4; // convert elements back into vertices
        }
    }

    public long getDataPointer(int sectionIndex) {
        return SectionRenderDataUnsafe.heapPointer(this.pMeshDataArray, sectionIndex);
    }

    public void delete() {
        for (var allocation : this.allocations.values()) {
            if (allocation != null) {
                allocation.delete();
            }
        }

        this.allocations.clear();

        SectionRenderDataUnsafe.freeHeap(this.pMeshDataArray);
    }
}
