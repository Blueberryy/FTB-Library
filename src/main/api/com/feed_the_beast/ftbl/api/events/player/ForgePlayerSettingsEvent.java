package com.feed_the_beast.ftbl.api.events.player;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.ConfigKey;

/**
 * Created by LatvianModder on 11.08.2016.
 */
public class ForgePlayerSettingsEvent extends ForgePlayerEvent
{
    private final IConfigTree settings;

    public ForgePlayerSettingsEvent(IForgePlayer player, IConfigTree tree)
    {
        super(player);
        settings = tree;
    }

    public IConfigKey add(String group, String id, IConfigValue value)
    {
        ConfigKey key = new ConfigKey(id, value, group, "player_config");
        settings.add(key, value);
        return key;
    }
}