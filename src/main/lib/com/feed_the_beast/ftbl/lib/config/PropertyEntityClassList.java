package com.feed_the_beast.ftbl.lib.config;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 24.09.2016.
 */
public class PropertyEntityClassList extends PropertyBase
{
    public static final String ID = "entity_class_list";

    private List<Class<?>> list;

    public PropertyEntityClassList()
    {
        this(Collections.emptyList());
    }

    public PropertyEntityClassList(Collection<Class<?>> c)
    {
        list = new ArrayList<>(c);
    }

    @Override
    public String getName()
    {
        return ID;
    }

    public List<Class<?>> getEntityList()
    {
        return list;
    }

    public void setEntityList(List<Class<?>> l)
    {
        list = l;
    }

    @Nullable
    @Override
    public Object getValue()
    {
        return getEntityList();
    }

    public boolean containsEntity(Class<?> c, boolean assignable)
    {
        if(assignable)
        {
            for(Class<?> c1 : getEntityList())
            {
                if(c1.isAssignableFrom(c))
                {
                    return true;
                }
            }

            return false;
        }

        return getEntityList().contains(c);
    }

    @Override
    public String getString()
    {
        return getEntityList().toString();
    }

    @Override
    public boolean getBoolean()
    {
        return !getEntityList().isEmpty();
    }

    @Override
    public int getInt()
    {
        return getEntityList().size();
    }

    @Override
    public IConfigValue copy()
    {
        return new PropertyEntityClassList(getEntityList());
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagList tagList = new NBTTagList();

        getEntityList().forEach(c ->
        {
            String s = EntityList.CLASS_TO_NAME.get(c);

            if(s != null)
            {
                tagList.appendTag(new NBTTagString(s));
            }
        });

        return tagList;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        NBTTagList tagList = (NBTTagList) nbt;

        list.clear();

        for(int i = 0; i < tagList.tagCount(); i++)
        {
            Class<?> c = EntityList.NAME_TO_CLASS.get(tagList.getStringTagAt(i));

            if(c != null && Entity.class.isAssignableFrom(c))
            {
                list.add(c);
            }
        }

        setEntityList(list);
    }

    @Override
    public void fromJson(JsonElement o)
    {
        list.clear();

        if(o.isJsonArray())
        {
            for(JsonElement e : o.getAsJsonArray())
            {
                Class<?> c = EntityList.NAME_TO_CLASS.get(e.getAsString());
                if(c != null && Entity.class.isAssignableFrom(c))
                {
                    list.add(c);
                }
            }
        }
    }

    @Override
    public JsonElement getSerializableElement()
    {
        JsonArray a = new JsonArray();

        getEntityList().forEach(c ->
        {
            String s = EntityList.CLASS_TO_NAME.get(c);

            if(s != null)
            {
                a.add(new JsonPrimitive(s));
            }
        });

        return a;
    }

    @Override
    public void writeData(ByteBuf data)
    {
        list = getEntityList();
        data.writeShort(list.size());
        list.forEach(c -> ByteBufUtils.writeUTF8String(data, EntityList.CLASS_TO_NAME.get(c)));
    }

    @Override
    public void readData(ByteBuf data)
    {
        list.clear();

        int s = data.readUnsignedShort();

        while(--s >= 0)
        {
            Class<?> c = EntityList.NAME_TO_CLASS.get(ByteBufUtils.readUTF8String(data));

            if(c != null && Entity.class.isAssignableFrom(c))
            {
                list.add(c);
            }
        }

        setEntityList(list);
    }
}