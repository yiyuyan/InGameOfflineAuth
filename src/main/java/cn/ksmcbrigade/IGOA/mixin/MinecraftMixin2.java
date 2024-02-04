package cn.ksmcbrigade.IGOA.mixin;

import cn.ksmcbrigade.IGOA.IGOAM;
import cn.ksmcbrigade.IGOA.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin2 {

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(GameConfig p_91084_, CallbackInfo ci){
        while (IGOAM.enableds==null){
            //Wait Mod Init
        }
        User MUser = getUser(IGOAM.enableds.stream().toList().get(0));
        if(MUser!=null){
            User.Set(MUser);
        }
    }

    @Nullable
    private User getUser(String name){
        for(User user:IGOAM.users){
            if(user.getName().equals(name)){
                return user;
            }
        }
        return null;
    }
}
