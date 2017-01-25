package com.feed_the_beast.ftbl.lib.config;

import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.config.IGuiEditConfig;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiSelectors;
import com.feed_the_beast.ftbl.lib.util.LMColorUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 26.08.2016.
 */
public class PropertyColor extends PropertyBase
{
    public static final String ID = "color";

    private byte value;

    public PropertyColor()
    {
    }

    public PropertyColor(byte v)
    {
        value = v;
    }

    @Override
    public String getName()
    {
        return ID;
    }

    public byte getColorID()
    {
        return value;
    }

    public void setColorID(byte v)
    {
        value = v;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return getColorID();
    }

    @Override
    public String getString()
    {
        return "#" + Integer.toString(getColorID() & 0xFF);
    }

    @Override
    public boolean getBoolean()
    {
        return getColorID() != 0;
    }

    @Override
    public int getInt()
    {
        return getColorID();
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyColor(getColorID());
    }

    @Override
    public int getColor()
    {
        return LMColorUtils.getColorFromID(getColorID());
    }

    @Override
    public void onClicked(IGuiEditConfig gui, IConfigKey key, IMouseButton button)
    {
        GuiSelectors.selectJson(this, (val, set) ->
        {
            if(set)
            {
                setColorID((byte) val.getInt());
                gui.onChanged(key, getSerializableElement());
            }

            gui.openGui();
        });
    }

    @Override
    public NBTBase serializeNBT()
    {
        return new NBTTagByte(getColorID());
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        setColorID(((NBTPrimitive) nbt).getByte());
    }

    @Override
    public void fromJson(JsonElement json)
    {
        setColorID(json.getAsByte());
    }

    @Override
    public JsonElement getSerializableElement()
    {
        return new JsonPrimitive(getColorID());
    }

    @Override
    public void writeData(ByteBuf data)
    {
        data.writeByte(getColorID());
    }

    @Override
    public void readData(ByteBuf data)
    {
        setColorID(data.readByte());
    }
}