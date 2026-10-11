package me.samuelh2005.logic_plus_plus.content.value_source;

import org.jetbrains.annotations.Nullable;

import dev.architectury.networking.NetworkManager;
import me.samuelh2005.logic_plus_plus.network.SubmitValueSourceScreenC2S;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ValueSourceScreen extends Screen {
    private static final int BOX_WIDTH = 200;
    private static final int BOX_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 98;
    private static final int GAP = 4;

    // "-2147483648" is 11 characters
    private static final int MAX_LENGTH = 11;

    private static final int COLOR_TEXT = 0xE0E0E0;
    private static final int COLOR_ERROR = 0xFF5555;
    private static final int COLOR_HINT = 0xA0A0A0;

    private final BlockPos pos;

    private EditBox valueBox;
    private Button doneButton;
    private final int initialValue;

    public ValueSourceScreen(int initialValue, BlockPos pos, Component component) {
        super(component);
        this.pos = pos;
        this.initialValue = initialValue;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int boxY = this.height / 2 - 20;

        this.valueBox = new EditBox(
                this.font,
                centerX - BOX_WIDTH / 2, boxY,
                BOX_WIDTH, BOX_HEIGHT,
                Component.literal("Value"));
        this.valueBox.setMaxLength(MAX_LENGTH);
        // Only ever let the box contain something that looks like an integer: an optional
        // leading '-' followed by digits. This also covers pasted text. Range (overflow) is
        // checked separately in parse() since it needs the whole number.
        this.valueBox.setFilter(s -> s.isEmpty() || s.matches("-?\\d*"));
        this.valueBox.setResponder(s -> this.updateValidation());
        this.valueBox.setValue(Integer.toString(this.initialValue));
        this.addRenderableWidget(this.valueBox);
        this.setInitialFocus(this.valueBox);

        int buttonY = boxY + BOX_HEIGHT + 40;
        this.doneButton = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, b -> this.submit())
                        .bounds(centerX - BUTTON_WIDTH - GAP / 2, buttonY, BUTTON_WIDTH, 20)
                        .build());
        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose())
                        .bounds(centerX + GAP / 2, buttonY, BUTTON_WIDTH, 20)
                        .build());

        this.updateValidation();
    }

    /** Returns the entered integer, or null if the box doesn't hold a valid one. */
    @Nullable
    private Integer parse() {
        String text = this.valueBox.getValue();
        if (text.isEmpty() || text.equals("-")) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Digits only at this point, so the only way to fail is overflow
            return null;
        }
    }

    @Nullable
    private Component getErrorMessage() {
        String text = this.valueBox.getValue();
        if (text.isEmpty() || text.equals("-")) return null; // still typing, no scolding
        if (parse() == null) {
            return Component.literal("Number is out of range");
        }
        return null;
    }

    private void updateValidation() {
        if (this.valueBox == null || this.doneButton == null) return;
        boolean valid = parse() != null;
        this.doneButton.active = valid;
        boolean overflow = !valid && getErrorMessage() != null;
        this.valueBox.setTextColor(overflow ? COLOR_ERROR : COLOR_TEXT);
    }

    private void submit() {
        Integer value = parse();
        if (value == null) return;
        NetworkManager.sendToServer(new SubmitValueSourceScreenC2S(this.pos, value));
        this.onClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.submit();
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Screen.render already draws the (blurred) menu background in 1.21.x
        super.render(graphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int boxY = this.height / 2 - 20;

        graphics.drawCenteredString(this.font, this.title, centerX, boxY - 30, 0xFFFFFF);
        graphics.drawString(this.font, Component.literal("Value"), centerX - BOX_WIDTH / 2, boxY - 12, COLOR_HINT);

        Component error = getErrorMessage();
        if (error != null) {
            graphics.drawCenteredString(this.font, error, centerX, boxY + BOX_HEIGHT + 6, COLOR_ERROR);
        } else {
            graphics.drawCenteredString(this.font, Component.literal("Any whole number from -2147483648 to 2147483647"),
                    centerX, boxY + BOX_HEIGHT + 6, COLOR_HINT);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}