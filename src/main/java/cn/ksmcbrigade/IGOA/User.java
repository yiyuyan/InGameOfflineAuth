package cn.ksmcbrigade.IGOA;

import cn.ksmcbrigade.IGOA.mixin.MinecraftMixin;
import cn.ksmcbrigade.IGOA.mixin.UserMixin;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class User {

    private String name;
    private String uuid;
    private boolean enabled;

    public User(String name,String uuid,boolean enabled){
        this.name = name;
        this.uuid = uuid;
        this.enabled = enabled;
    }

    public static void Set(User user) {
        Minecraft MC = Minecraft.getInstance();
        MinecraftMixin MCX = (MinecraftMixin) MC;
        GameProfile gameProfile = MC.getUser().getGameProfile();
        UserMixin USX = (UserMixin)MC.getUser();
        net.minecraft.client.User.Type type = net.minecraft.client.User.Type.LEGACY;
        String at = "0";
        if(gameProfile.getName().equals(user.getName()) && USX.getType().equals(net.minecraft.client.User.Type.MSA)){
            at = USX.getAT();
            type = net.minecraft.client.User.Type.MSA;
        }
        MCX.setUser(new net.minecraft.client.User(user.name,user.getUuid(),at, Optional.empty(),Optional.empty(), type));
        IGOAM.LOGGER.info("Setting user:"+user.getName());
        IGOAM.LOGGER.info("User uuid:"+user.getUuid());
        IGOAM.LOGGER.info("User AccessToken:"+at);
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static void save() throws IOException{
        JsonObject json = new JsonObject();
        JsonObject json2 = new JsonObject();
        for(User user:IGOAM.users){
            json.addProperty(user.name,user.uuid);
            if(user.isEnabled()){
                json2.add(user.name,null);
            }
        }
        Files.write(Paths.get("config/users.json"),json.toString().getBytes());
        Files.write(Paths.get("config/enabled-users.json"),json2.toString().getBytes());
    }

    public static void random(int size) throws IOException {
        Random random = new Random();
        for(int i=0;i<size;i++){
            IGOAM.users.add(new User(getRandomString(random.nextInt(4,16)), UUID.randomUUID().toString().replace("-",""),false));
        }
        save();
    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
