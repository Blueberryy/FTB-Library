package com.feed_the_beast.ftbl.cmd.team;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.internal.FTBLibPerms;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Created by LatvianModder on 20.06.2016.
 */
public class CmdInvite extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "invite";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
        IForgePlayer p = getForgePlayer(ep);
        IForgeTeam team = p.getTeam();

        if(team == null)
        {
            throw FTBLibLang.TEAM_NO_TEAM.commandError();
        }
        else if(!team.hasPermission(p.getProfile().getId(), FTBLibPerms.TEAM_MANAGE_MEMBERS))
        {
            throw FTBLibLang.COMMAND_PERMISSION.commandError();
        }

        checkArgs(args, 1, "<player>");

        IForgePlayer p1 = getForgePlayer(args[0]);

        if(team.setHasPermission(p1.getProfile().getId(), FTBLibPerms.TEAM_CAN_JOIN, true) && !team.hasStatus(p1, EnumTeamStatus.MEMBER))
        {
            FTBLibLang.TEAM_INVITED.printChat(sender, p1.getProfile().getName(), team.getName());

            if(p1.isOnline())
            {
                FTBLibLang.TEAM_INVITED_YOU.printChat(p1.getPlayer(), team.getName(), ep.getName());
            }
        }
        else
        {
            throw FTBLibLang.TEAM_ALREADY_INVITED.commandError(p1.getProfile().getName());
        }
    }
}
