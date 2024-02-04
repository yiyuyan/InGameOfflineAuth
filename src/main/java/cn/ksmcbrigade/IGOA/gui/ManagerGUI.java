package cn.ksmcbrigade.IGOA.gui;

import cn.ksmcbrigade.IGOA.IGOAM;
import cn.ksmcbrigade.IGOA.User;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;

public class ManagerGUI extends Screen {

    private Screen lastGUI;
    private OptionsList optionsList;
    private boolean shift = false;
    private GameProfile gameProfile = Minecraft.getInstance().getUser().getGameProfile();
    private User SetUser = new User(gameProfile.getName(),gameProfile.getId().toString().replace("-",""),true);

    public ManagerGUI(Screen last) {
        super(new TranslatableComponent("gui.igoa.title"));
        this.lastGUI = last;
    }

    @Override
    public void onClose(){
        try {
            User.Set(SetUser);
            User.save();
            if(this.minecraft!=null){
                this.minecraft.setScreen(lastGUI);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void init() {
        if (this.minecraft != null) {
            this.optionsList = new OptionsList(this.minecraft,this.width,this.height,24,this.height - 32,25);
        }

        ArrayList<Option> options = new ArrayList<>();
        for(User user: IGOAM.users){
            options.add(CycleOption.create(user.getName(),ButtonArg.values(),(c)-> new TranslatableComponent(c.getKey()),(cc)->{
                if(user.isEnabled()){
                    SetUser = user;
                    return ButtonArg.ON;
                }
                else{
                    return ButtonArg.OFF;
                }
                },(c1,c2,c3)->{
                if(c3.equals(ButtonArg.OFF)){
                    user.setEnabled(false);
                    try {
                        User.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(c3.equals(ButtonArg.ON)){
                    user.setEnabled(true);
                    SetUser = user;
                    for(User user1:IGOAM.users){
                        if(user1!=user){
                            user1.setEnabled(false);
                        }
                    }
                    try {
                        User.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Minecraft.getInstance().setScreen(new ManagerGUI(lastGUI));
                }
                if(c3.equals(ButtonArg.EDIT) & shift){
                    this.minecraft.setScreen(new EditUserGUI(this,true,user));
                }
            }));
        }

        this.optionsList.addSmall(options.toArray(new Option[0]));
        this.addRenderableWidget(this.optionsList);
        this.addRenderableWidget(new Button(this.width / 2 - 105,this.height - 25,98,20, CommonComponents.GUI_DONE,(context)->{
            this.onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2,this.height - 25,98,20, new TranslatableComponent("gui.igoa.add"),(context)->{
            this.minecraft.setScreen(new EditUserGUI(this,false,null));
        }));
        super.init();
        if(IGOAM.users.isEmpty() | IGOAM.users.size()==1){
            this.minecraft.setScreen(new RandomUserGUI(this));
        }
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        this.optionsList.render(p_96562_,p_96563_,p_96564_,p_96565_);
        drawCenteredString(p_96562_, font,this.title.getString(),this.width / 2, 8, 16777215);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    @Override
    public void tick() {
        shift=false;
        boolean[] enableds = new boolean[IGOAM.users.size()];
        for(int i=0;i<IGOAM.users.size();i++){
            enableds[i] = IGOAM.users.get(i).isEnabled();
        }
        boolean it = false;
        for(boolean is:enableds){
            if(is){
                it = true;
                break;
            }
        }
        if(!it){
            for(User user:IGOAM.users){
                if(user.getName().equals(Minecraft.getInstance().getUser().getName())){
                    SetUser = user;
                    user.setEnabled(true);
                    Minecraft.getInstance().setScreen(new ManagerGUI(lastGUI));
                    break;
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if(p_96552_ == GLFW.GLFW_KEY_LEFT_SHIFT || p_96552_== GLFW.GLFW_KEY_RIGHT_SHIFT){
            shift=true;
        }
        else{
            shift=false;
        }
        return super.keyPressed(p_96552_, p_96553_, p_96554_);
    }
}
