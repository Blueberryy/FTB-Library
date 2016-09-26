package com.feed_the_beast.ftbl.api_impl;

import com.feed_the_beast.ftbl.api.config.ConfigFileProvider;
import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.ConfigValueProvider;
import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.api.config.IConfigFile;
import com.feed_the_beast.ftbl.api.config.IConfigFileProvider;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigValueProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.config.ConfigFile;
import com.latmod.lib.config.ConfigKey;
import com.latmod.lib.config.ConfigTree;
import com.latmod.lib.reg.StringIDRegistry;
import com.latmod.lib.reg.SyncedRegistry;
import com.latmod.lib.util.LMJsonUtils;
import com.latmod.lib.util.LMStringUtils;
import com.latmod.lib.util.LMUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 17.09.2016.
 */
public enum ConfigManager
{
    INSTANCE;

    public final SyncedRegistry<String, IConfigValueProvider> CONFIG_VALUES = new SyncedRegistry<>(new StringIDRegistry(), false);
    public final Map<String, IConfigFile> CONFIG_FILES = new HashMap<>();
    public final IConfigTree CLIENT_CONFIG = new ConfigTree();
    public final Map<UUID, IConfigContainer> TEMP_SERVER_CONFIG = new HashMap<>();
    public IConfigFile CLIENT_CONFIG_FILE;

    public void init(ASMDataTable table)
    {
        LMUtils.findAnnotatedObjects(table, IConfigValueProvider.class, ConfigValueProvider.class, (obj, data) ->
        {
            String s = (String) data.getAnnotationInfo().get("value");

            if(s != null && !s.isEmpty())
            {
                CONFIG_VALUES.register(s.toLowerCase(Locale.ENGLISH), obj);
            }

            return null;
        });

        LMUtils.findAnnotatedObjects(table, IConfigFileProvider.class, ConfigFileProvider.class, (obj, data) ->
        {
            String s = (String) data.getAnnotationInfo().get("value");

            if(s != null && !s.isEmpty() && s.charAt(0) != '-')
            {
                s = s.toLowerCase(Locale.ENGLISH);
                ITextComponent n = new TextComponentString(s);
                ConfigFile configFile = new ConfigFile(n, obj);
                CONFIG_FILES.put(s, configFile);
            }

            return null;
        });

        CLIENT_CONFIG_FILE = new ConfigFile(new TextComponentString("Client Config"), () -> new File(LMUtils.folderLocal, "client/config.json")); //TODO: Lang
        CONFIG_FILES.put("client_config", CLIENT_CONFIG_FILE);

        int[] configValuesCount = {0};

        LMUtils.findAnnotatedObjects(table, IConfigValue.class, ConfigValue.class, (obj, data) ->
        {
            String id = (String) data.getAnnotationInfo().get("id");
            String file = (String) data.getAnnotationInfo().get("file");

            if(id != null && file != null && !id.isEmpty() && !file.isEmpty())
            {
                boolean client = data.getAnnotationInfo().containsKey("client") && (boolean) data.getAnnotationInfo().get("client");
                String displayName = data.getAnnotationInfo().containsKey("displayName") ? (String) data.getAnnotationInfo().get("displayName") : null;

                byte flags = 0;

                if(data.getAnnotationInfo().containsKey("isExcluded") && (boolean) data.getAnnotationInfo().get("isExcluded"))
                {
                    flags |= IConfigKey.EXCLUDED;
                }

                if(data.getAnnotationInfo().containsKey("isHidden") && (boolean) data.getAnnotationInfo().get("isHidden"))
                {
                    flags |= IConfigKey.HIDDEN;
                }

                if(data.getAnnotationInfo().containsKey("canEdit") && !(boolean) data.getAnnotationInfo().get("canEdit"))
                {
                    flags |= IConfigKey.CANT_EDIT;
                }

                if(data.getAnnotationInfo().containsKey("useScrollBar") && (boolean) data.getAnnotationInfo().get("useScrollBar"))
                {
                    flags |= IConfigKey.USE_SCROLL_BAR;
                }

                if(data.getAnnotationInfo().containsKey("translateDisplayName") && (boolean) data.getAnnotationInfo().get("translateDisplayName"))
                {
                    flags |= IConfigKey.TRANSLATE_DISPLAY_NAME;
                }

                ConfigKey key = new ConfigKey(client ? (file + "." + id) : id, obj.copy(), displayName, false);
                key.setFlags(flags);

                if(data.getAnnotationInfo().containsKey("info"))
                {
                    key.setInfo(LMStringUtils.unsplit(((List<String>) data.getAnnotationInfo().get("info")).toArray(new String[0]), "\n"));
                }

                if(client)
                {
                    CLIENT_CONFIG.add(key, obj);
                }
                else
                {
                    IConfigFile configFile = CONFIG_FILES.get(file);

                    if(configFile == null)
                    {
                        configFile = new ConfigFile(new TextComponentString(file), () -> new File(LMUtils.folderConfig, file + ".json"));
                        CONFIG_FILES.put(file, configFile);
                    }

                    configFile.add(key, obj);
                    configValuesCount[0]++;
                }
            }

            return null;
        });

        CONFIG_VALUES.getIDs().generateIDs(CONFIG_VALUES.getKeys());

        LMUtils.DEV_LOGGER.info("Found " + CONFIG_VALUES.size() + " IConfigValueProviders: " + CONFIG_VALUES.getKeys());
        LMUtils.DEV_LOGGER.info("Found " + CONFIG_FILES.size() + " IConfigFiles: " + CONFIG_FILES.keySet());
        LMUtils.DEV_LOGGER.info("Found " + configValuesCount[0] + " IConfigValues, " + CLIENT_CONFIG.getTree().size() + " Client IConfigValues");
    }

    //new ResourceLocation(FTBLibFinals.MOD_ID, "client_config")
    public final IConfigContainer CLIENT_CONFIG_CONTAINER = new IConfigContainer()
    {
        @Override
        public IConfigTree getConfigTree()
        {
            return CLIENT_CONFIG_FILE;
        }

        @Override
        public ITextComponent getTitle()
        {
            return new TextComponentString("client_settings"); //TODO: Lang
        }

        @Override
        public void saveConfig(ICommandSender sender, @Nullable NBTTagCompound nbt, JsonObject json)
        {
            CLIENT_CONFIG_FILE.fromJson(json);

        }
    };

    public void loadAllFiles()
    {
        CONFIG_FILES.values().forEach(IConfigFile::load);
    }

    public void saveAllFiles()
    {
        CONFIG_FILES.values().forEach(IConfigFile::save);
    }

    public void reloadConfig()
    {
        loadAllFiles();

        LMUtils.DEV_LOGGER.info("Loading override configs");
        JsonElement overridesE = LMJsonUtils.fromJson(new File(LMUtils.folderModpack, "overrides.json"));

        if(overridesE.isJsonObject())
        {
            overridesE.getAsJsonObject().entrySet().forEach(entry ->
            {
                if(entry.getValue().isJsonObject() && CONFIG_FILES.containsKey(entry.getKey()))
                {
                    CONFIG_FILES.get(entry.getKey()).fromJson(entry.getValue());
                }
            });
        }

        saveAllFiles();
    }
}