package mekanism.multiblockmachine.client.render.item;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.config.MekanismConfig;
import mekanism.multiblockmachine.client.model.ModelLargeWindGenerator;
import mekanism.multiblockmachine.common.util.MekanismMultiblockMachineUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;


@SideOnly(Side.CLIENT)
public class RenderLargeWindGeneratorItem {

    private static ModelLargeWindGenerator windGenerator = new ModelLargeWindGenerator();
    private static int angle = 0;
    private static float lastTicksUpdated = 0;

    public static void renderStack(@Nonnull ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND || transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.translate(0, 0.4F, 0);
            if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) {
                GlStateManager.rotate(-45, 0, 1, 0);
            } else {
                GlStateManager.rotate(45, 0, 1, 0);
            }
            GlStateManager.rotate(50, 1, 0, 0);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.translate(0, -0.4F, 0);
        } else {
            if (transformType == ItemCameraTransforms.TransformType.GUI) {
                GlStateManager.rotate(90, 0, 1, 0);
            } else if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
                GlStateManager.rotate(180, 0, 1, 0);
            }
            GlStateManager.translate(0, 0.4F, 0);
        }
        MekanismRenderer.bindTexture(MekanismMultiblockMachineUtils.getResource(MekanismMultiblockMachineUtils.ResourceType.RENDER, "LargeWindGenerator.png"));

        if (MekanismConfig.current().generators.windGeneratorItem.val()) {
            if (lastTicksUpdated != Minecraft.getMinecraft().getRenderPartialTicks()) {
                angle = (angle + 2) % 360;
                lastTicksUpdated = Minecraft.getMinecraft().getRenderPartialTicks();
            }
        } else {
            angle = 0;
        }

        windGenerator.render(0.016F, angle);
        GlStateManager.popMatrix();
    }
}
