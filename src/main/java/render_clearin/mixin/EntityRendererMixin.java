
package render_clearin.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(Entity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null || entity == mc.cameraEntity) return;

        Camera cam = mc.gameRenderer.getMainCamera();
        double dx = entity.getX() - cam.getPosition().x;
        double dz = entity.getZ() - cam.getPosition().z;
        double dy = entity.getY() - cam.getPosition().y;

        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double angle = Math.toDegrees(Math.atan2(dz, dx)) - cam.getYRot();

        float fov = mc.options.fov().get();

        if (Math.abs(angle) > fov / 2 + 10) {
            ci.cancel(); // Cancel render if outside FOV + margin
        }
    }
}
