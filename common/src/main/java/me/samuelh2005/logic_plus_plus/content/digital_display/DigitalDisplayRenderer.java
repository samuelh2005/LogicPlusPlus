package me.samuelh2005.logic_plus_plus.content.digital_display;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalDisplayRenderer implements BlockEntityRenderer<DigitalDisplayBlockEntity> {
    // The font's digits are 5px wide with 1px spacing, so "99" is 11px of visible ink.
    private static final float INK_WIDTH_TWO_DIGITS = 11f;
    // Fraction of the block face the two-digit text should span (screen area is 14/16 wide).
    private static final float TARGET_WIDTH = 0.80f;
    private static final float SCALE = TARGET_WIDTH / INK_WIDTH_TWO_DIGITS;
    // Digit glyphs are 7px tall, drawn from the top of the 9px line.
    private static final float GLYPH_HEIGHT = 7f;

    private final Font font;

    public DigitalDisplayRenderer(BlockEntityRendererProvider.Context ctx) {
        this.font = ctx.getFont();
    }

    @Override
    public void render(DigitalDisplayBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        int value = be.getValue();
        String text = Integer.toString(value);
        if (text.isEmpty()) return;
        // If bigger then 2 digits show "?"
        if (text.length() > 2) {
            text = "?";
        }

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);

        poseStack.pushPose();

        // 1. Move to block center and rotate so local +Z points out of the front face
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        // 2. Move out to the front surface (slightly in front to avoid z-fighting)
        poseStack.translate(0, 0, 0.5 + 0.005);

        // 3. Scale down to world units; negative Y because font Y points down
        poseStack.scale(SCALE, -SCALE, SCALE);

        // 4. Light: sample the air in front of the face, since the block itself is dark
        Level level = be.getLevel();
        int light = level != null
                ? LevelRenderer.getLightColor(level, be.getBlockPos().relative(facing))
                : packedLight;

        // 5. Center horizontally and vertically
        Component component = Component.literal(text);
        // width() includes the 1px spacing after the last glyph, which is not visible ink
        float x = -(font.width(component) - 1) / 2f;
        // centre on the actual glyph height, not the full line height
        float y = -GLYPH_HEIGHT / 2f;

        font.drawInBatch(component, x, y, 0xFFFFFFFF, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

        poseStack.popPose();
    }
}