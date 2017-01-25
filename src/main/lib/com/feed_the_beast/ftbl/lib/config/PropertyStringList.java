package com.feed_the_beast.ftbl.lib.config;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by LatvianModder on 26.08.2016.
 */
public class PropertyStringList extends PropertyBase
{
    public static final String ID = "string_list";

    private Collection<String> value;

    public PropertyStringList()
    {
        this(Collections.emptyList());
    }

    public PropertyStringList(Collection<String> v)
    {
        value = new ArrayList<>(v);
    }

    public PropertyStringList(String... s)
    {
        this(Arrays.asList(s));
    }

    @Override
    public String getName()
    {
        return ID;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return getStringList();
    }

    public void setStringList(Collection<String> v)
    {
        value = v;
    }

    public Collection<String> getStringList()
    {
        return value;
    }

    @Override
    public void writeData(ByteBuf data)
    {
        Collection<String> list = getStringList();
        data.writeShort(list.size());

        for(String s : list)
        {
            ByteBufUtils.writeUTF8String(data, s);
        }
    }

    @Override
    public void readData(ByteBuf data)
    {
        int s = data.readUnsignedShort();

        if(s <= 0)
        {
            value.clear();
            setStringList(value);
        }
        else
        {
            value.clear();

            while(--s >= 0)
            {
                value.add(ByteBufUtils.readUTF8String(data));
            }

            setStringList(value);
        }
    }

    @Override
    public String getString()
    {
        return getStringList().toString();
    }

    @Override
    public boolean getBoolean()
    {
        return !getStringList().isEmpty();
    }

    @Override
    public int getInt()
    {
        return getStringList().size();
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyStringList(getStringList());
    }

    @Override
    public int getColor()
    {
        return 0xFFAA49;
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagList tagList = new NBTTagList();
        getStringList().forEach(s -> tagList.appendTag(new NBTTagString(s)));
        return tagList;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        value.clear();
        NBTTagList tagList = (NBTTagList) nbt;
        int s = tagList.tagCount();

        for(int i = 0; i < s; i++)
        {
            value.add(tagList.getStringTagAt(i));
        }

        setStringList(value);
    }

    @Override
    public void fromJson(JsonElement json)
    {
        value.clear();
        JsonArray a = json.getAsJsonArray();

        if(a.size() > 0)
        {
            a.forEach(e -> value.add(e.getAsString()));
        }

        setStringList(value);
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonArray a = new JsonArray();
        getStringList().forEach(s -> a.add(new JsonPrimitive(s)));
        return a;
    }
}