package cn.ksmcbrigade.IGOA.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerLevel.class)
public interface ServerLevelMixin {
    @Accessor("players")
    @Mutable
    List<ServerPlayer> getPlayers();
}
