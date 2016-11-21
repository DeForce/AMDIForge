/*
    Copyright 2016 Stefan 'Namikon' Thomanek <sthomanek at gmail dot com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.usrv.amdiforge.server;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import eu.usrv.yamcore.auxiliary.PlayerChatHelper;


public class AMDICommand implements ICommand
{
	private List aliases;

	public AMDICommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add( "lbag" );
	}

	@Override
	public int compareTo( Object arg0 )
	{
		return 0;
	}

	@Override
	public String getCommandName()
	{
		return "lootbags";
	}

	@Override
	public String getCommandUsage( ICommandSender p_71518_1_ )
	{
		return "Check the readme for usage";
	}

	@Override
	public List getCommandAliases()
	{
		return this.aliases;
	}

	@Override
	public void processCommand( ICommandSender pCmdSender, String[] pArgs )
	{
		/*
		try
		{
			if( pArgs.length < 1 )
			{
				SendHelpToPlayer( pCmdSender );
				return;
			}
			String tSubCommand = pArgs[0];

			if( tSubCommand.equalsIgnoreCase( "reload" ) )
			{
				if( EnhancedLootBags.LootGroupHandler.reload() )
					PlayerChatHelper.SendInfo( pCmdSender, String.format( "Reload successful" ) );
				else
					PlayerChatHelper.SendError( pCmdSender, String.format( "Reload successful" ) );

				return;
			}

			LootGroupsFactory tLGF = new LootGroupsFactory();

			if( !InGame( pCmdSender ) )
			{
				PlayerChatHelper.SendPlain( pCmdSender, "You have to execute this command ingame" );
				return;
			}

			EntityPlayer tEp = (EntityPlayer) pCmdSender;
			ItemStack inHand = null;
			if( tEp != null )
			{
				inHand = tEp.getCurrentEquippedItem();
				if( inHand == null )
				{
					PlayerChatHelper.SendPlain( pCmdSender, "Pickup an item first" );
					return;
				}
			}

			if( tSubCommand.equalsIgnoreCase( "addloot" ) || tSubCommand.equalsIgnoreCase( "addinventory" ) )
			{
				int tGroupID = Integer.parseInt( pArgs[1] );
				int tAmount = 1;
				int tChance = 100;
				int tLimitedDropCount = 0;
				int tRandomAmount = 0;

				boolean tFlagsOK = true;
				if( tGroupID < 0 || tGroupID > 32767 )
					tFlagsOK = false;
				if( pArgs.length == 6 )
				{
					tAmount = Integer.parseInt( pArgs[2] );
					tChance = Integer.parseInt( pArgs[3] );
					tLimitedDropCount = Integer.parseInt( pArgs[4] );
					tRandomAmount = Integer.parseInt( pArgs[5] );

					if( tAmount < 1 || tAmount > 64 )
						tFlagsOK = false;
					if( tChance < 1 || tChance > 255 )
						tFlagsOK = false;
					if( tLimitedDropCount < 0 || tChance > 255 )
						tFlagsOK = false;
					if( tRandomAmount < 0 || tRandomAmount > 1 )
						tFlagsOK = false;
				}
				if( tFlagsOK )
				{
					LootGroup tGrp = EnhancedLootBags.LootGroupHandler.getGroupByID( tGroupID );
					if( tGrp != null )
					{
						List<ItemStack> tStacksToAdd = new ArrayList<ItemStack>();
						if( tSubCommand.equalsIgnoreCase( "addloot" ) )
							tStacksToAdd.add( inHand.copy() );
						else if( tSubCommand.equalsIgnoreCase( "addinventory" ) )
						{
							for( ItemStack is : tEp.inventory.mainInventory )
								if( is != null )
									tStacksToAdd.add( is.copy() );
						}

						for( ItemStack tis : tStacksToAdd )
						{
							String tItemID = ItemDescriptor.fromStack( tis ).toString();
							if( tItemID.isEmpty() )
								continue; // Something went wrong..

							String tItemNBT = "";
							if( tis.stackTagCompound != null )
								tItemNBT = tis.stackTagCompound.toString();

							Drop dr = tLGF.createDrop( tItemID, UUID.randomUUID().toString(), tItemNBT, tAmount, tRandomAmount == 1 ? true : false, tChance, tLimitedDropCount, "" );
							tGrp.getDrops().add( dr );
							EnhancedLootBags.LootGroupHandler.SaveLootGroups();
							PlayerChatHelper.SendInfo( pCmdSender, String.format( "Item %s added to LootGroup ID %d ", tItemID, tGrp.getGroupID() ) );
						}
					}
					else
						PlayerChatHelper.SendError( pCmdSender, String.format( "LootGroup ID %d is unknown", tGroupID ) );
				}
				else
					PlayerChatHelper.SendError( pCmdSender, String.format( "Some flags are wrong. Make sure to read the readme" ) );
			}
			else if( tSubCommand.equalsIgnoreCase( "addgroup" ) )
			{
				int tGroupID = Integer.parseInt( pArgs[1] );
				String tGroupName = String.format( "Unnamed group %d", tGroupID );
				EnumRarity tRarity = EnumRarity.common;
				int tMinItems = 1;
				int tMaxItems = 1;

				boolean tFlagsOK = true;
				if( tGroupID < 0 || tGroupID > 32767 )
					tFlagsOK = false; // Just to be sure...
				if( pArgs.length == 5 )
				{
					int tIntRarity = Integer.parseInt( pArgs[2] );
					tMinItems = Integer.parseInt( pArgs[3] );
					tMaxItems = Integer.parseInt( pArgs[4] );

					if( tMinItems > tMaxItems )
						tFlagsOK = false;
					if( tMinItems < 1 || tMaxItems < 1 )
						tFlagsOK = false;
					if( tIntRarity < 0 || tIntRarity >= EnumRarity.values().length )
						tFlagsOK = false;
					else
						tRarity = EnumRarity.values()[tIntRarity];

				}
				if( tFlagsOK )
				{
					LootGroup tGrp = EnhancedLootBags.LootGroupHandler.getGroupByID( tGroupID );

					if( tGrp == null )
					{
						LootGroup tNewGroup = tLGF.createLootGroup( tGroupID, tGroupName, tRarity, tMinItems, tMaxItems, true );
						EnhancedLootBags.LootGroupHandler.getLootGroups().getLootTable().add( tNewGroup );
						EnhancedLootBags.LootGroupHandler.SaveLootGroups();
						PlayerChatHelper.SendInfo( pCmdSender, String.format( "New group added (ID: %d Name: %s)", tGroupID, tGroupName ) );
					}
					else
						PlayerChatHelper.SendError( pCmdSender, String.format( "LootGroup ID %d is already in use", tGroupID ) );
				}
				else
					PlayerChatHelper.SendError( pCmdSender, String.format( "Some flags are wrong. Make sure to read the readme" ) );
			}

		}
		catch( Exception e )
		{
			e.printStackTrace();
			PlayerChatHelper.SendError( pCmdSender, "Unknown error occoured" );
		}
		*/
	}

	private boolean InGame( ICommandSender pCmdSender )
	{
		if( !( pCmdSender instanceof EntityPlayer ) )
			return false;
		else
			return true;
	}

	private void SendHelpToPlayer( ICommandSender pCmdSender )
	{
		if( !InGame( pCmdSender ) )
		{
			PlayerChatHelper.SendPlain( pCmdSender, "Command can only be executed ingame" );
		}
		else
		{
			PlayerChatHelper.SendInfo( pCmdSender, "Check the readme for usage" );
		}
	}

	@Override
	public boolean canCommandSenderUseCommand( ICommandSender pCommandSender )
	{
		if( pCommandSender instanceof EntityPlayerMP )
		{
			EntityPlayerMP tEP = (EntityPlayerMP) pCommandSender;
			boolean tPlayerOpped = MinecraftServer.getServer().getConfigurationManager().func_152596_g( tEP.getGameProfile() );
			boolean tIncreative = tEP.capabilities.isCreativeMode;
			return tPlayerOpped && tIncreative;
		}
		else if( pCommandSender instanceof MinecraftServer )
			return true;
		else
			return false;
	}

	@Override
	public List addTabCompletionOptions( ICommandSender p_71516_1_, String[] p_71516_2_ )
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex( String[] p_82358_1_, int p_82358_2_ )
	{
		return false;
	}
}
