package cn.ksmcbrigade.IGOA.gui;

import cn.ksmcbrigade.IGOA.User;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

import java.io.IOException;

public class RandomUserGUI extends Screen {

    private Screen lastGUI;
    private Button button;

    public RandomUserGUI(Screen last) {
        super(new TranslatableComponent("gui.igoa.title2"));
        this.lastGUI = last;
    }

    @Override
    public void onClose(){
        this.minecraft.setScreen(lastGUI);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 100,this.height - 100,98,20, CommonComponents.GUI_YES,(context)->{
            try {
                User.random(10);
                this.onClose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        button = new Button(this.width / 2 +2,this.height - 100,98,20, CommonComponents.GUI_NO,(cn)-> Minecraft.getInstance().setScreen(new ManagerGUI(new TitleScreen())));
        this.addWidget(button);
        super.init();
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        drawCenteredString(p_96562_, font,this.title.getString(),this.width / 2, 100, 16777215);
        this.button.render(p_96562_,p_96563_,p_96564_,p_96565_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }
}
