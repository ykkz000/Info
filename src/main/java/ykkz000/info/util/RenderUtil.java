/*
 * Info
 * Copyright (C) 2023  ykkz000
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ykkz000.info.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public class RenderUtil {
    public static void drawBar(PoseStack poseStack, MultiBufferSource bufferSource, float minX, float minY, float maxX, float maxY, float Z, int color) {
        Matrix4f matrix4f = poseStack.last().pose();
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }

        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(ExtendedRenderType.filledBox());
        vertexConsumer.vertex(matrix4f, minX, minY, Z).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, minX, maxY, Z).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, maxY, Z).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, minY, Z).color(color).endVertex();
        RenderSystem.disableBlend();
    }
}
