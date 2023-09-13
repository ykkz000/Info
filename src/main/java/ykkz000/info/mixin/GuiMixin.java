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

package ykkz000.info.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(HitResult pRayTrace);

    @Shadow public abstract Font getFont();

    @Shadow protected int screenWidth;

    @Shadow protected int screenHeight;

    @Inject(method = "renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("RETURN"))
    public void renderCrosshairHitResult(PoseStack pPoseStack, CallbackInfo ci) {
        if (minecraft.options.getCameraType().isFirstPerson()
                && ((minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) || canRenderCrosshairForSpectator(minecraft.hitResult))
                && minecraft.hitResult != null
                && minecraft.player != null) {
            Component component = switch (minecraft.hitResult.getType()) {
                case MISS -> MutableComponent.create(ComponentContents.EMPTY);
                case BLOCK -> minecraft.player.level.getBlockState(((BlockHitResult) minecraft.hitResult).getBlockPos()).getBlock().getName();
                case ENTITY -> ((EntityHitResult) minecraft.hitResult).getEntity().getDisplayName();
            };
            if (component.getString().isEmpty()) {
                return;
            }
            Font font = getFont();
            float x = screenWidth / 2f - font.width(component) / 2f;
            float y = screenHeight / 2f + 20f;
            float opacity = minecraft.options.getBackgroundOpacity(0.25F);
            int backgroundColor = (int)(opacity * 255.0F) << 24;
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(component, x, y, -1, false, pPoseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundColor, 15728880);
            bufferSource.endBatch();
        }
    }
}
