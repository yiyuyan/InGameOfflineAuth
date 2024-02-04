package cn.ksmcbrigade.IGOA.mixin;

import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(User.class)
public interface UserMixin {
    @Accessor("type")
    User.Type getType();

    @Accessor("accessToken")
    String getAT();
}
