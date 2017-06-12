package com.feed_the_beast.ftbl.api.events.team;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.google.common.base.Preconditions;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class ForgeTeamEvent extends Event
{
	private final IForgeTeam team;

	public ForgeTeamEvent(IForgeTeam t)
	{
		team = Preconditions.checkNotNull(t, "Null IForgeTeam in ForgeTeamEvent!");
	}

	public IForgeTeam getTeam()
	{
		return team;
	}
}