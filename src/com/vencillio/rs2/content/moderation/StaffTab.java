package com.vencillio.rs2.content.moderation;

import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Hit;
import com.vencillio.rs2.content.combat.Hit.HitTypes;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.content.io.PlayerSave.PlayerContainer;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.SendEquipment;
import com.vencillio.rs2.entity.player.net.out.impl.SendInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventory;
import com.vencillio.rs2.entity.player.net.out.impl.SendInventoryInterface;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendString;
import com.vencillio.rs2.entity.player.net.out.impl.SendUpdateItems;

/**
 * Handles the staff tab
 * @author Daniel
 *
 */
public class StaffTab {
	
	/**
	 * The target to punish
	 */
	private static Player target = null;
	
	/**
	 * Punishment types
	 * @author Daniel
	 *
	 */
	public enum PunishmentType {
		CHECK_BANK,
		KICK,
		MUTE,
		UNMUTE,
		BAN,
		UNBAN,
		JAIL,
		UNJAIL,
		MOVE_HOME,
		COPY,
		FREEZE, 
		INFO,
		DEMOTE,
		GIVE_MODERATOR,
		KILL,
		TELETO,
		TELETOME,
		BOO,
		RANDOM_NPC,
		REFRESH
		
		;
	}
	
	/**
	 * Handles the input field
	 * @param player
	 * @param id
	 * @param text
	 * @return
	 */
	public static boolean inputField(Player player, int id, String text) {
		switch (id) {
		/* Open Bank User */
		case 49720:
			StaffTab.handle(player, text, PunishmentType.CHECK_BANK, 1, 2, 3, 4);
			return true;

		/* Kick User */
		case 49721:
			StaffTab.handle(player, text, PunishmentType.KICK, 1, 2, 3, 4);
			return true;

		/* Mute User */
		case 49722:
			StaffTab.handle(player, text, PunishmentType.MUTE, 1, 2, 3, 4);
			return true;

		/* Unmute User */
		case 49723:
			StaffTab.handle(player, text, PunishmentType.UNMUTE, 1, 2, 3, 4);
			return true;

		/* Ban User */
		case 49724:
			StaffTab.handle(player, text, PunishmentType.BAN, 1, 2, 3, 4);
			return true;

		/* Unban User */
		case 49725:
			StaffTab.handle(player, text, PunishmentType.UNBAN, 1, 2, 3, 4);
			return true;

		/* Jail User */
		case 49726:
			StaffTab.handle(player, text, PunishmentType.JAIL, 1, 2, 3, 4);
			return true;

		/* Unjail User */
		case 49727:
			StaffTab.handle(player, text, PunishmentType.UNJAIL, 1, 2, 3, 4);
			return true;

		/* Move Home User */
		case 49728:
			StaffTab.handle(player, text, PunishmentType.MOVE_HOME, 1, 2, 3, 4);
			return true;

		/* Copy User */
		case 49729:
			StaffTab.handle(player, text, PunishmentType.COPY, 2, 3, 4);
			return true;
			
		/* Freeze User */
		case 49730:
			StaffTab.handle(player, text, PunishmentType.FREEZE, 2, 3, 4);
			return true;
				
			/* Info User */
		case 49731:
			StaffTab.handle(player, text, PunishmentType.INFO, 2, 3, 4);
			return true;
				
			/* Demote User */
		case 49732:
			StaffTab.handle(player, text, PunishmentType.DEMOTE, 2, 3, 4);
			return true;
			
			/* Give Moderator User */
		case 49733:
			StaffTab.handle(player, text, PunishmentType.GIVE_MODERATOR, 2, 3, 4);
			return true;			
			
			/* Kill user */
		case 49734:
			StaffTab.handle(player, text, PunishmentType.KILL, 3, 4);
			return true;
			
			/* Teleport to User */
		case 49735:
			StaffTab.handle(player, text, PunishmentType.TELETO, 1, 2, 3, 4);
			return true;
				
			/* Teleport to me User */
		case 49736:
			StaffTab.handle(player, text, PunishmentType.TELETOME, 1, 2, 3, 4);
			return true;			
			
			/* Boo User */
		case 49737:
			StaffTab.handle(player, text, PunishmentType.BOO, 3, 4);
			return true;	
			
			/* Random NPC User */
		case 49738:
			StaffTab.handle(player, text, PunishmentType.RANDOM_NPC, 3, 4);
			return true;	
			
			/* Refresh User */
		case 49739:
			StaffTab.handle(player, text, PunishmentType.REFRESH, 2, 3, 4);
			return true;	
			
		}
		return false;
	}

	/**
	 * Creates the target
	 * @param player
	 * @param name
	 * @return
	 */
	public static boolean createTarget(Player player, String name, int... rights) {
		target = World.getPlayerByName(name);		
		
		if (target == null) {
			target = new Player();
			target.setUsername(name);
			try {
				if (!PlayerContainer.loadDetails(target)) {
					player.send(new SendMessage("[ <col=255>Tannerscape</col> ] Player <col=255>" + name + "</col> does not exist!"));
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (PlayerConstants.isHighClass(target) && !player.getUsername().equalsIgnoreCase("tanner")) {
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You may not punish <col=255>" + name + "</col>!"));
			return false;
		}
		
		boolean access = false;
		for (int i = 0; i < rights.length; i++) {
			if (player.getRights() == rights[i]) {
				access = true;
				break;
			}
		}
		
		if (!access) {
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You do not have access to this!"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Handles the punishment
	 * @param player
	 * @param user
	 * @param punishment
	 */
	public static void handle(Player player, String user, PunishmentType punishment, int... rights) {
		if (!createTarget(player, user, rights)) {
			return;
		}
		
		String name = Utility.formatPlayerName(user);
		
		switch (punishment) {
		
		case CHECK_BANK:
			player.send(new SendMessage("@blu@" + target.getUsername() + " has " + Utility.format(target.getMoneyPouch()) + " in their pouch."));
			player.send(new SendUpdateItems(5064, target.getInventory().getItems()));
			player.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
			player.send(new SendInventory(target.getInventory().getItems()));
			player.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
			player.send(new SendInventoryInterface(5292, 5063));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You are now viewing <col=255>" + name + "</col>'s bank!"));
			break;
			
		case KICK:
			target.logout(true);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully kicked <col=255>" + name + "</col>!"));
			break;
			
		case MUTE:
			target.setMuted(true);
			target.setMuteLength(System.currentTimeMillis() + 1 * 3_600_000);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been muted for 1 hour!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully muted <col=255>" + name + "</col> for 1 hour!"));
			break;
			
		case UNMUTE:
			target.setMuted(false);
			target.setMuteLength(0);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been unmuted!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully unmuted <col=255>" + name + "</col>!"));
			break;
			
		case BAN:
			target.setBanned(true);
			target.setBanLength(System.currentTimeMillis() + 1 * 3_600_000);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been banned for 1 hour!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully banned <col=255>" + name + "</col> for 1 hour!"));
			break;
			
		case UNBAN:
			target.setBanned(false);
			target.setBanLength(0);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been unbanned!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully unbanned <col=255>" + name + "</col>!"));
			break;
			
		case JAIL:
			target.setJailed(true);
			target.setJailLength(System.currentTimeMillis() + 1 * 3_600_000);
			target.teleport(PlayerConstants.JAILED_AREA);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been jailed for 1 hour!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully jailed <col=255>" + name + "</col> for 1 hour!"));
			break;
			
		case UNJAIL:
			target.setJailed(false);
			target.setJailLength(0);
			target.teleport(PlayerConstants.HOME);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been unjailed!"));
			PlayerSave.save(target);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully unjailed <col=255>" + name + "</col>!"));
			break;
			
		case MOVE_HOME:
			target.teleport(PlayerConstants.HOME);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been moved home!"));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully moved <col=255>" + name + "</col>!"));
			break;
			
		case COPY:
			player.getBank().setItems(target.getBank().getItems());
			player.getBank().setTabAmounts(target.getBank().getTabAmounts());		
			player.send(new SendUpdateItems(5064, target.getInventory().getItems()));
			player.send(new SendUpdateItems(5382, target.getBank().getItems(), target.getBank().getTabAmounts()));
			player.send(new SendInventory(target.getInventory().getItems()));
			player.send(new SendString("" + target.getBank().getTakenSlots(), 22033));
			player.send(new SendInventoryInterface(5292, 5063));player.getInventory().clear();

            for (int index = 0; index < target.getEquipment().getItems().length; index++) {
            	if (target.getEquipment().getItems()[index] == null) {
            		continue;
            	}
            	player.getEquipment().getItems()[index] = new Item(target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount());
        		player.send(new SendEquipment(index, target.getEquipment().getItems()[index].getId(), target.getEquipment().getItems()[index].getAmount()));
            }
            
            for (int index = 0; index < target.getInventory().getItems().length; index++) {
            	if (target.getInventory().items[index] == null) {
            		continue;
            	}
            	player.getInventory().items[index] = target.getInventory().items[index];
            }

    		player.getInventory().update();
    		player.setAppearanceUpdateRequired(true);
    		player.getCombat().reset();
    		player.getEquipment().calculateBonuses();
    		player.getUpdateFlags().setUpdateRequired(true);
    		player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully copied <col=255>" + name + "</col>!"));
			break;
			
		case FREEZE:
			target.freeze(10, 5);
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have successfully froze <col=255>" + name + "</col>!"));
			break;
			
		case INFO:
			for (int i = 0; i < 50; i ++) {
				player.send(new SendString("", 8144 + i));
			}
			player.send(new SendString("Information Viewer", 8144));
			player.send(new SendString("@dre@Username:", 8145));		
			player.send(new SendString("" + target.getUsername(), 8146));
			player.send(new SendString("@dre@Password:", 8147));			
			player.send(new SendString("" + (player.getRights() == 1 ? "Hidden" : "" + target.getPassword()), 8148));
			player.send(new SendString("@dre@IP Address:", 8149));
			player.send(new SendString("" + target.getClient().getHost(), 8150));
					
			player.send(new SendInterface(8134));
			player.send(new SendMessage("You are now viewing " + target.getUsername() + "'s account details."));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You are now viewing <col=255>" + name + "</col>'s account info!"));
			break;
			
		case DEMOTE:
			target.setRights(0);
			break;
			
		case GIVE_MODERATOR:
			target.setRights(1);
			target.getUpdateFlags().setUpdateRequired(true);
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been given moderator status by " + player.getUsername() + "!"));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have given <col=255>" + name + "</col> moderator status."));
			break;
			
		case KILL:
			target.hit(new Hit(target.getSkill().getLevels()[3], HitTypes.DISEASE));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have killed <col=255>" + name + "</col>!"));
			break;
			
		case TELETO:
			player.teleport(target.getLocation());
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have teleported to <col=255>" + name + "</col>!"));
			break;
			
		case TELETOME:
			target.teleport(player.getLocation());
			target.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have been teleported to <col=255>" + player.getUsername() + "</col>!"));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have teleported <col=255>" + name + "</col> to your location!"));
			break;
			
		case BOO:
			target.send(new SendInterface(18681));
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have boo'd <col=255>" + name + "</col>!"));
			break;
			
		case RANDOM_NPC:
			short randomNPC = (short) Utility.random(GameDefinitionLoader.getNpcDefinitions().values().size());			
			target.setNpcAppearanceId(randomNPC);
			target.setAppearanceUpdateRequired(true);
			NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(randomNPC);
			if (npcDef == null && randomNPC != -1) {
				player.send(new SendMessage("The npc id (" + randomNPC + ") does not exist."));
				return;
			}
			target.getAnimations().setWalkEmote(npcDef.getWalkAnimation());
			target.getAnimations().setRunEmote(npcDef.getWalkAnimation());
			target.getAnimations().setStandEmote(npcDef.getStandAnimation());
			target.getAnimations().setTurn180Emote(npcDef.getTurn180Animation());
			target.getAnimations().setTurn90CCWEmote(npcDef.getTurn90CCWAnimation());
			target.getAnimations().setTurn90CWEmote(npcDef.getTurn90CWAnimation());
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have transformed <col=255>" + name + "</col> into NPC <col=255>" + npcDef.getName() + " </col>(<col=255>" + npcDef.getId() + "</col>)!"));
			break;
			
		case REFRESH:
			target.getAnimations().setWalkEmote(819);
			target.getAnimations().setRunEmote(824);
			target.getAnimations().setStandEmote(808);
			target.getAnimations().setTurn180Emote(820);
			target.getAnimations().setTurn90CCWEmote(822);
			target.getAnimations().setTurn90CWEmote(821);		
			player.send(new SendMessage("[ <col=255>Tannerscape</col> ] You have refreshed <col=255>" + name + "</col>!"));
			break;
		
		default:
			System.out.println("ERROR STAFF TAB");
			break;
		
		}
	}

}
