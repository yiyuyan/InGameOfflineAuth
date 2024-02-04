package cn.ksmcbrigade.IGOA;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Mod("igoa")
@Mod.EventBusSubscriber
public class IGOAM {

    public static Logger LOGGER = LogManager.getLogger();
    public static ArrayList<User> users = new ArrayList<>();
    public static Set<String> enableds;

    public IGOAM() throws IOException {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("In-game offline auth mod loading...");
        init();
        LOGGER.info("In-game offline auth mod loaded.");
    }

    public static void init() throws IOException{
        File file = new File("config/users.json");
        File file2 = new File("config/enabled-users.json");
        if(!file.exists()){
            Files.write(file.toPath(),new JsonObject().toString().getBytes());
        }
        if(!file2.exists()){
            JsonObject object = new JsonObject();
            object.add(Minecraft.getInstance().getUser().getName(),null);
            Files.write(file2.toPath(),object.toString().getBytes());
        }
        JsonObject json = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonObject();
        JsonObject json2 = JsonParser.parseString(Files.readString(file2.toPath())).getAsJsonObject();
        enableds = json2.keySet();
        for(String key:json.keySet()){
            users.add(new User(key,json.get(key).getAsString(),IsEnabled(key)));
        }
        if(users.isEmpty()){
            GameProfile gameProfile = Minecraft.getInstance().getUser().getGameProfile();
            users.add(new User(gameProfile.getName(),gameProfile.getId().toString().replace("-",""),true));
            User.save();
        }
    }

    @SubscribeEvent
    public static void OnRegisterCommands(RegisterClientCommandsEvent event){
        event.getDispatcher().register(Commands.literal("reload-igoa").executes((context)->{
            LOGGER.info("Users are reloading...");
            try {
                users.clear();
                init();
                LOGGER.info("Users are reloaded.");
                context.getSource().getEntity().sendMessage(CommonComponents.GUI_DONE,context.getSource().getEntity().getUUID());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }));

        event.getDispatcher().register(Commands.literal("users").then(Commands.argument("type", StringArgumentType.string()).then(Commands.argument("user",StringArgumentType.string()).executes(context -> {
            String type = StringArgumentType.getString(context, "type");
            String user = StringArgumentType.getString(context, "user");
            if(type.equalsIgnoreCase("add")){
                boolean add = true;
                for(User user1:users){
                    if(user1.getName().equals(user)){
                        add = false;
                        break;
                    }
                }
                if(add){
                    int n = 0;
                    if(!user.equalsIgnoreCase("all")){
                        Player player = null;
                        for(Player serverPlayer:getPlayers(Minecraft.getInstance().player,Minecraft.getInstance().player.getLevel())){
                            if (Minecraft.getInstance().player != null) {
                                if(serverPlayer.getName().equals(Minecraft.getInstance().player.getName())){
                                    player = serverPlayer;
                                    break;
                                }
                            }
                        }
                        if(player!=null){
                            users.add(new User(user,player.getUUID().toString().replace("-",""),false));
                        }
                        else {
                            users.add(new User(user,UUID.randomUUID().toString().replace("-",""),false));
                        }
                        n++;
                    }
                    else{
                        for(Player player:getPlayers(Minecraft.getInstance().player,Minecraft.getInstance().player.getLevel())){
                            if (Minecraft.getInstance().player != null) {
                                if(!player.getName().equals(Minecraft.getInstance().player.getName())){
                                    users.add(new User(user,player.getUUID().toString().replace("-",""),false));
                                    n++;
                                }
                            }
                        }
                    }
                    try {
                        User.save();
                        Objects.requireNonNull(context.getSource().getEntity()).sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.add").replace("{n}",String.valueOf(n))),context.getSource().getEntity().getUUID());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if(type.equalsIgnoreCase("del")){
                int n = 0;
                if(user.equalsIgnoreCase("all")){
                    LocalPlayer player = Minecraft.getInstance().player;
                    int size = users.size();
                    users.clear();
                    if (player != null) {
                        users.add(new User(player.getName().getString(),player.getUUID().toString().replace("-",""),true));
                        n = size-1;
                    }
                }
                else{
                    User user1 = null;
                    for(User user2:users){
                        if(user2.getName().equals(user)){
                            user1 = user2;
                        }
                    }
                    if(user1!=null){
                        users.remove(user1);
                        n++;
                    }
                }
                try {
                    User.save();
                    Objects.requireNonNull(context.getSource().getEntity()).sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.del").replace("{n}",String.valueOf(n))),context.getSource().getEntity().getUUID());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(type.equalsIgnoreCase("list")){
                Entity player = context.getSource().getEntity();
                UUID uuid = player.getUUID();
                if(user.equalsIgnoreCase("all")){
                    player.sendMessage(new TranslatableComponent("commands.igoa.list"),uuid);
                    for(User user1:users){
                        player.sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.list.name")+user1.getName()),uuid);
                        player.sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.list.uuid")+user1.getUuid()),uuid);
                        player.sendMessage(Component.nullToEmpty(""),uuid);
                    }
                }
                else{
                    boolean has = false;
                    User user2 = null;
                    for(User user1:users){
                        if(user1.getName().equals(user)){
                            has = true;
                            user2 = user1;
                            break;
                        }
                    }
                    if(has){
                        player.sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.list.name")+user2.getName()),uuid);
                        player.sendMessage(Component.nullToEmpty(I18n.get("commands.igoa.list.uuid")+user2.getUuid()),uuid);
                    }
                    else{
                        player.sendMessage(new TranslatableComponent("commands.igoa.list.none"),uuid);
                    }
                }
            }
            return 0;
        }))));
    }

    public static boolean IsEnabled(String name){
        return enableds.contains(name);
    }

    public static List<Player> getPlayers(Player player, Level level){
        Vec3 vec3 = new Vec3(player.getX(),player.getY(),player.getZ());
        return level.getEntitiesOfClass(Player.class,new AABB(vec3,vec3).inflate(10000));
    }
}
