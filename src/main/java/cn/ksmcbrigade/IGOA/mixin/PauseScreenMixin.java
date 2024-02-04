package cn.ksmcbrigade.IGOA.mixin;

import cn.ksmcbrigade.IGOA.gui.ManagerGUI;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "createPauseMenu",at = @At("TAIL"))
    public void addButtonToPauseMenu(CallbackInfo ci){
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 126, 98, 20, new TranslatableComponent("gui.igoa.to_main"), (p_96315_) -> {
            if (this.minecraft != null && this.minecraft.level != null) {
                this.minecraft.level.disconnect();
                this.minecraft.clearLevel();
                this.minecraft.setScreen(new TitleScreen());
            }
        }));
        this.addRenderableWidget(new Button(this.width / 2+2, this.height / 4 + 126, 98, 20, new TranslatableComponent("gui.igoa.title"), (p_96315_) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new ManagerGUI(this));
            }
        }));
    }
}
