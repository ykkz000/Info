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

package ykkz000.info.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ykkz000.info.Info;
import ykkz000.info.util.RenderUtil;

@Mod.EventBusSubscriber(modid = Info.MODID, value = Dist.CLIENT)
public class ClientForgeEventHandlers {
    private static final int BLOOD_BAR_WIDTH = 64;
    @SubscribeEvent
    public static void onRenderNameTag(RenderNameTagEvent event) {
        // Always display name tags
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static <T extends LivingEntity, M extends EntityModel<T>> void postRenderLivingEntity(RenderLivingEvent<T, M> event) {
        LivingEntity livingEntity = event.getEntity();
        PoseStack matrixStack = event.getPoseStack();
        float dY = livingEntity.getBbHeight() + 0.5F;
        Component displayName = livingEntity.getCustomName();
        float minY = (displayName != null && "deadmau5".equals(displayName.getString()) ? -10 : 0) + event.getRenderer().getFont().lineHeight;
        matrixStack.pushPose();
        matrixStack.translate(0.0F, dY, 0.0F);
        matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrixStack.scale(-0.025F, -0.025F, 0.025F);
        double bloodWidth = BLOOD_BAR_WIDTH * Math.max(Math.min((double) livingEntity.getHealth() / (double) livingEntity.getMaxHealth(), 1), 0);
        RenderUtil.drawBar(matrixStack, event.getMultiBufferSource(), -BLOOD_BAR_WIDTH / 2f, minY, BLOOD_BAR_WIDTH / 2f, minY + 5, 0, 0xFF8B0000);
        RenderUtil.drawBar(matrixStack, event.getMultiBufferSource(), -BLOOD_BAR_WIDTH / 2f, minY, -BLOOD_BAR_WIDTH / 2f + (int) bloodWidth, minY + 5, 0, 0xFFFF0000);
        matrixStack.popPose();
    }
}
