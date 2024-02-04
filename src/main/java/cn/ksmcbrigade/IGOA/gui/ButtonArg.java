package cn.ksmcbrigade.IGOA.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ButtonArg {
    OFF(0, "options.off"),
    ON(1,"options.on"),
    EDIT(2,"options.igoa.edit");

    private final int id;
    private final String key;

    private ButtonArg(int p_90484_, String p_90485_) {
        this.id = p_90484_;
        this.key = p_90485_;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
}
