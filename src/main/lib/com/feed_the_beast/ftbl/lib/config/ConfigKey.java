package com.feed_the_beast.ftbl.lib.config;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 26.08.2016.
 */
public class ConfigKey extends SimpleConfigKey
{
    private final IConfigValue defValue;
    private ITextComponent displayName;
    private int flags;
    private String info = "";

    public ConfigKey(String id, IConfigValue def, @Nullable ITextComponent dn)
    {
        super(id);
        defValue = def;
        displayName = dn;
    }

    public ConfigKey(String id, IConfigValue def)
    {
        this(id, def, null);
    }

    @Override
    public IConfigValue getDefValue()
    {
        return defValue;
    }

    @Override
    @Nullable
    public ITextComponent getRawDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(@Nullable ITextComponent c)
    {
        displayName = c;
    }

    @Override
    public int getFlags()
    {
        return flags;
    }

    public ConfigKey setFlags(int f)
    {
        flags = f;
        return this;
    }

    public ConfigKey addFlags(int f)
    {
        flags |= f;
        return this;
    }

    @Override
    public String getInfo()
    {
        return info;
    }

    public void setInfo(String... s)
    {
        info = s.length == 0 ? "" : (s.length == 1 ? s[0] : String.join("\n", s));
    }
}