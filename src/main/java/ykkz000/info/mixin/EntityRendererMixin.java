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

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class EntityRendererMixin<T extends Entity> {
    @Unique
    private static final int BLOOD_BAR_WIDTH = 128;
    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;

    @Shadow public abstract Font getFont();

    @Inject(method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    protected void renderBloodBar(T pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if (pEntity instanceof LivingEntity livingEntity) {
            float f = pEntity.getBbHeight() + 0.5F;
            int i = ("deadmau5".equals(pDisplayName.getString()) ? -10 : 0) + getFont().lineHeight;
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.0F, f, 0.0F);
            pMatrixStack.mulPose(entityRenderDispatcher.cameraOrientation());
            pMatrixStack.scale(-0.025F, -0.025F, 0.025F);
            double bloodWidth = BLOOD_BAR_WIDTH * Math.max(Math.min((double)livingEntity.getHealth() / (double)livingEntity.getMaxHealth(), 1), 0);
            GuiComponent.fill(pMatrixStack, -BLOOD_BAR_WIDTH / 2, i, BLOOD_BAR_WIDTH / 2, i + 5, 0xFF8B0000);
            GuiComponent.fill(pMatrixStack, -BLOOD_BAR_WIDTH / 2, i, -BLOOD_BAR_WIDTH / 2 + (int) bloodWidth, i + 5, 0xFFFF0000);
            pMatrixStack.popPose();
        }
    }
}
