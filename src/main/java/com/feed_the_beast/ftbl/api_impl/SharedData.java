package com.feed_the_beast.ftbl.api_impl;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IPackMode;
import com.feed_the_beast.ftbl.api.ISharedData;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public abstract class SharedData implements ISharedData
{
	public IPackMode currentMode;
	public UUID universeID;
	public final Collection<String> optionalServerMods = new HashSet<>();

	SharedData()
	{
	}

	public void reset()
	{
		currentMode = null;
		universeID = null;
	}

	@Override
	public IPackMode getPackMode()
	{
		if (currentMode == null)
		{
			currentMode = getSide().isClient() ? new PackMode("default") : FTBLibAPI.API.getPackModes().getDefault();
		}

		return currentMode;
	}

	@Override
	public UUID getUniverseID()
	{
		if (universeID == null || (universeID.getLeastSignificantBits() == 0L && universeID.getMostSignificantBits() == 0L))
		{
			universeID = UUID.randomUUID();
		}

		return universeID;
	}

	@Override
	public Collection<String> optionalServerMods()
	{
		return optionalServerMods;
	}
}