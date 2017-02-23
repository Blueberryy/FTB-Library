package com.feed_the_beast.ftbl.cmd.team;

import com.feed_the_beast.ftbl.FTBLibModCommon;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.internal.FTBLibPerms;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by LatvianModder on 10.11.2016.
 */
public class CmdSetPermission extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "set_has_permission";
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
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, FTBLibModCommon.VISIBLE_TEAM_PLAYER_PERMISSIONS);
        }
        else if(args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
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
        else if(!team.hasPermission(p.getProfile().getId(), FTBLibPerms.TEAM_EDIT_PERMISSIONS))
        {
            throw FTBLibLang.COMMAND_PERMISSION.commandError();
        }

        checkArgs(args, 3, "<player> <id> <true|false>");
        IForgePlayer p1 = getForgePlayer(args[0]);
        boolean val = parseBoolean(args[2]);

        if(p1.equals(team.getOwner()))
        {
            throw FTBLibLang.TEAM_PERMISSION_OWNER.commandError();
        }

        if(team.setHasPermission(p1.getProfile().getId(), args[1], val))
        {
            FTBLibLang.TEAM_PERMISSION_SET.printChat(sender, args[1], args[0], val);
        }
        else
        {
            FTBLibLang.TEAM_PERMISSION_ALREADY_SET.printChat(sender, args[1], args[0], val);
        }
    }
}