package cn.ksmcbrigade.IGOA.gui;

import cn.ksmcbrigade.IGOA.IGOAM;
import cn.ksmcbrigade.IGOA.User;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.UUID;

public class EditUserGUI extends Screen {

    private Screen lastGUI;
    private User user = new User("", UUID.randomUUID().toString().replace("-",""),false);
    private boolean edit;
    private boolean del = false;
    private EditBox editBox;
    private EditBox editBox2;

    public EditUserGUI(Screen last,boolean edit,@Nullable User user) {
        super(edit ? Component.nullToEmpty(I18n.get("gui.igoa.title3").replace("{user}",user.getName())) : new TranslatableComponent("gui.igoa.title4"));
        this.lastGUI = last;
        if(user!=null){
            this.user = user;
        }
        this.edit = edit;
    }

    @Override
    public void onClose(){
        if(edit){
            if(del){
                User user2 = user;
                for(User user1:IGOAM.users){
                    if(user1.equals(user)){
                        user2 = user1;
                    }
                }
                IGOAM.users.remove(user2);
                try {
                    User.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                for(User user1:IGOAM.users){
                    if(user1.equals(user)){
                        user1.setUuid(user.getUuid());
                        user1.setName(user.getName());
                    }
                }
                try {
                    User.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else{
            if(user.getName()!=""){
                IGOAM.users.add(new User(user.getName(),user.getUuid(),false));
                try {
                    User.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(this.minecraft!=null){
            this.minecraft.setScreen(lastGUI);
        }
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addRenderableWidget(new Button(this.width / 2 - 100, 132, 200, 20, CommonComponents.GUI_DONE, (p_95981_) -> {
            this.onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 100, 132 + 22, 200, 20, new TranslatableComponent("gui.igoa.del"), (p_95981_) -> {
            del=true;
            this.onClose();
        }));
        editBox = new EditBox(this.font, this.width / 2 - 100, 84, 200, 20, new TranslatableComponent("gui.igoa.name"));
        editBox.setMaxLength(20);
        if(!edit){
            editBox.setFocus(true);
        }
        editBox.setCanLoseFocus(true);
        editBox.setValue(user.getName());
        editBox.setResponder((p_95983_) -> {
            user.setName(p_95983_);
        });
        editBox2 = new EditBox(this.font, this.width / 2 - 100, 108, 200, 20, new TranslatableComponent("gui.igoa.uuid"));
        editBox2.setMaxLength(128);
        editBox2.setFocus(false);
        editBox2.setCanLoseFocus(true);
        editBox2.setValue(user.getUuid());
        editBox2.setResponder((p_959832_) -> {
            user.setUuid(p_959832_);
        });
        this.addWidget(editBox);
        this.setInitialFocus(editBox);
        super.init();
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        drawCenteredString(p_96562_, font,this.title.getString(),this.width / 2, 60, 16777215);
        this.editBox.render(p_96562_,p_96563_,p_96564_,p_96565_);
        this.editBox2.render(p_96562_,p_96563_,p_96564_,p_96565_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }
}
