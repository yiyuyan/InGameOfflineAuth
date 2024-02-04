package cn.ksmcbrigade.IGOA.mixin;

import cn.ksmcbrigade.IGOA.gui.ManagerGUI;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Inject(method = "init",at = @At("TAIL"))
    public void Init(CallbackInfo ci){
        this.addRenderableWidget(new Button(this.width / 2 - 100,this.height / 4 + 24, 200, 20, new TranslatableComponent("gui.igoa.title"), (p_96781_) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new ManagerGUI(this));
            }
        }));
    }
}
