package com.github.lunatrius.core.client.renderer;

import com.github.lunatrius.core.client.renderer.vertex.VertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class GeometryTessellator extends Tessellator {
    private static GeometryTessellator instance = null;

    private int mode = -1;
    private double delta = 0;

    public GeometryTessellator() {
        this(0x200000);
    }

    public GeometryTessellator(final int size) {
        super(size);
    }

    public static GeometryTessellator getInstance() {
        if (instance == null) {
            instance = new GeometryTessellator();
        }

        return instance;
    }

    public void setTranslation(final double x, final double y, final double z) {
        getWorldRenderer().setTranslation(x, y, z);
    }

    public void startQuads() {
        start(GL11.GL_QUADS);
    }

    public void startLines() {
        start(GL11.GL_LINES);
    }

    public void start(final int mode) {
        this.mode = mode;
        getWorldRenderer().startDrawing(mode);
        getWorldRenderer().setVertexFormat(VertexFormats.ABSTRACT);
    }

    @Override
    public int draw() {
        this.mode = -1;
        return super.draw();
    }

    public void setDelta(final double delta) {
        this.delta = delta;
    }

    public void drawCuboid(final BlockPos pos, final int sides, final int color) {
        drawCuboid(pos, sides, color, color >>> 24);
    }

    public void drawCuboid(final BlockPos pos, final int sides, final int rgb, int alpha) {
        drawCuboid(pos, pos, sides, rgb, alpha);
    }

    public void drawCuboid(final BlockPos begin, final BlockPos end, final int sides, final int color) {
        drawCuboid(begin, end, sides, color, color >>> 24);
    }

    public void drawCuboid(final BlockPos begin, final BlockPos end, final int sides, final int rgb, final int a) {
        if (this.mode == -1 || sides == 0) {
            return;
        }

        final double x0 = begin.getX() - this.delta;
        final double y0 = begin.getY() - this.delta;
        final double z0 = begin.getZ() - this.delta;
        final double x1 = end.getX() + 1 + this.delta;
        final double y1 = end.getY() + 1 + this.delta;
        final double z1 = end.getZ() + 1 + this.delta;

        if (this.mode == GL11.GL_QUADS) {
            drawQuads(x0, y0, z0, x1, y1, z1, sides, rgb, a);
        } else if (this.mode == GL11.GL_LINES) {
            drawLines(x0, y0, z0, x1, y1, z1, sides, rgb, a);
        } else {
            throw new IllegalStateException("Unsupported mode!");
        }
    }

    private void drawQuads(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1, final int sides, final int rgb, int a) {
        final WorldRenderer worldRenderer = getWorldRenderer();
        worldRenderer.setColorRGBA_I(rgb, a);

        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            worldRenderer.addVertex(x1, y0, z0);
            worldRenderer.addVertex(x1, y0, z1);
            worldRenderer.addVertex(x0, y0, z1);
            worldRenderer.addVertex(x0, y0, z0);
        }

        if ((sides & GeometryMasks.Quad.UP) != 0) {
            worldRenderer.addVertex(x1, y1, z0);
            worldRenderer.addVertex(x0, y1, z0);
            worldRenderer.addVertex(x0, y1, z1);
            worldRenderer.addVertex(x1, y1, z1);
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            worldRenderer.addVertex(x1, y0, z0);
            worldRenderer.addVertex(x0, y0, z0);
            worldRenderer.addVertex(x0, y1, z0);
            worldRenderer.addVertex(x1, y1, z0);
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            worldRenderer.addVertex(x0, y0, z1);
            worldRenderer.addVertex(x1, y0, z1);
            worldRenderer.addVertex(x1, y1, z1);
            worldRenderer.addVertex(x0, y1, z1);
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            worldRenderer.addVertex(x0, y0, z0);
            worldRenderer.addVertex(x0, y0, z1);
            worldRenderer.addVertex(x0, y1, z1);
            worldRenderer.addVertex(x0, y1, z0);
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            worldRenderer.addVertex(x1, y0, z1);
            worldRenderer.addVertex(x1, y0, z0);
            worldRenderer.addVertex(x1, y1, z0);
            worldRenderer.addVertex(x1, y1, z1);
        }
    }

    private void drawLines(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1, final int sides, final int rgb, int a) {
        final WorldRenderer worldRenderer = getWorldRenderer();
        worldRenderer.setColorRGBA_I(rgb, a);

        if ((sides & GeometryMasks.Line.DOWN_WEST) != 0) {
            worldRenderer.addVertex(x0, y0, z0);
            worldRenderer.addVertex(x0, y0, z1);
        }

        if ((sides & GeometryMasks.Line.UP_WEST) != 0) {
            worldRenderer.addVertex(x0, y1, z0);
            worldRenderer.addVertex(x0, y1, z1);
        }

        if ((sides & GeometryMasks.Line.DOWN_EAST) != 0) {
            worldRenderer.addVertex(x1, y0, z0);
            worldRenderer.addVertex(x1, y0, z1);
        }

        if ((sides & GeometryMasks.Line.UP_EAST) != 0) {
            worldRenderer.addVertex(x1, y1, z0);
            worldRenderer.addVertex(x1, y1, z1);
        }

        if ((sides & GeometryMasks.Line.DOWN_NORTH) != 0) {
            worldRenderer.addVertex(x0, y0, z0);
            worldRenderer.addVertex(x1, y0, z0);
        }

        if ((sides & GeometryMasks.Line.UP_NORTH) != 0) {
            worldRenderer.addVertex(x0, y1, z0);
            worldRenderer.addVertex(x1, y1, z0);
        }

        if ((sides & GeometryMasks.Line.DOWN_SOUTH) != 0) {
            worldRenderer.addVertex(x0, y0, z1);
            worldRenderer.addVertex(x1, y0, z1);
        }

        if ((sides & GeometryMasks.Line.UP_SOUTH) != 0) {
            worldRenderer.addVertex(x0, y1, z1);
            worldRenderer.addVertex(x1, y1, z1);
        }

        if ((sides & GeometryMasks.Line.NORTH_WEST) != 0) {
            worldRenderer.addVertex(x0, y0, z0);
            worldRenderer.addVertex(x0, y1, z0);
        }

        if ((sides & GeometryMasks.Line.NORTH_EAST) != 0) {
            worldRenderer.addVertex(x1, y0, z0);
            worldRenderer.addVertex(x1, y1, z0);
        }

        if ((sides & GeometryMasks.Line.SOUTH_WEST) != 0) {
            worldRenderer.addVertex(x0, y0, z1);
            worldRenderer.addVertex(x0, y1, z1);
        }

        if ((sides & GeometryMasks.Line.SOUTH_EAST) != 0) {
            worldRenderer.addVertex(x1, y0, z1);
            worldRenderer.addVertex(x1, y1, z1);
        }
    }
}
