package com.vencillio.rs2.entity.player.net.in.impl;

import com.vencillio.core.network.StreamBuffer;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.*;
import com.vencillio.rs2.content.achievements.AchievementButtons;
import com.vencillio.rs2.content.combat.formula.MagicFormulas;
import com.vencillio.rs2.content.combat.formula.MeleeFormulas;
import com.vencillio.rs2.content.combat.formula.RangeFormulas;
import com.vencillio.rs2.content.combat.impl.PlayerDrops;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.content.dialogue.impl.Tutorial;
import com.vencillio.rs2.content.interfaces.InterfaceHandler;
import com.vencillio.rs2.content.interfaces.impl.*;
import com.vencillio.rs2.content.membership.CreditHandler;
import com.vencillio.rs2.content.membership.MysteryBoxMinigame;
import com.vencillio.rs2.content.minigames.duelarena.DuelingConstants;
import com.vencillio.rs2.content.profiles.PlayerProfiler;
import com.vencillio.rs2.content.profiles.ProfileLeaderboard;
import com.vencillio.rs2.content.skill.SkillGoal;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.agility.Agility;
import com.vencillio.rs2.content.skill.cooking.CookingTask;
import com.vencillio.rs2.content.skill.crafting.Crafting;
import com.vencillio.rs2.content.skill.crafting.HideTanning;
import com.vencillio.rs2.content.skill.firemaking.FireColor;
import com.vencillio.rs2.content.skill.fletching.Fletching;
import com.vencillio.rs2.content.skill.herblore.HerbloreFinishedPotionTask;
import com.vencillio.rs2.content.skill.herblore.HerbloreUnfinishedPotionTask;
import com.vencillio.rs2.content.skill.magic.Autocast;
import com.vencillio.rs2.content.skill.magic.MagicSkill.TeleportTypes;
import com.vencillio.rs2.content.skill.magic.spells.BoltEnchanting;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSeas;
import com.vencillio.rs2.content.skill.prayer.PrayerBook.Prayer;
import com.vencillio.rs2.content.skill.ranged.ToxicBlowpipe;
import com.vencillio.rs2.content.skill.smithing.SmithingConstants;
import com.vencillio.rs2.content.skill.summoning.SummoningCreation;
import com.vencillio.rs2.entity.Animation;
import com.vencillio.rs2.entity.Graphic;
import com.vencillio.rs2.entity.ReportHandler;
import com.vencillio.rs2.entity.ReportHandler.ReportData;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.Client;
import com.vencillio.rs2.entity.player.net.in.IncomingPacket;
import com.vencillio.rs2.entity.player.net.out.impl.*;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.*;

public class ClickButtonPacket extends IncomingPacket {
	
	@Override
	public int getMaxDuplicates() {
		return 5;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		int buttonId = in.readShort();
		in.reset();
		buttonId = Utility.hexToInt(in.readBytes(2));
		
		if (player.isStunned()) {
			return;
		}
		
		if (player.getPrayer().clickButton(buttonId)) {
			return;
		}

		if(buttonId == 9154)
			System.out.println("Logging out: " + player);
		
		if (PlayerConstants.isOwner(player) && buttonId != 9154) {
			player.getClient().queueOutgoingPacket(new SendMessage("@red@Developer - button: " + buttonId));
			System.out.println("button: " + buttonId);
		}
		
		if (player.getAttributes().get("DROPTABLE_SEARCH") != null) {
			@SuppressWarnings("unchecked")
			HashMap<Integer, Integer> searchButtons = (HashMap<Integer, Integer>) player.getAttributes().get("DROPTABLE_SEARCH");
			if (searchButtons.containsKey(buttonId)) {
				DropTable.displayNpc(player, searchButtons.get(buttonId));
			}
		}

		if (player.getInterfaceManager().getMain() == 53500) {
			if (buttonId <= 209161 && buttonId >= 209012) {
				int c = 0;
				for (Player p : World.getPlayers()) {
					if (p != null && p.isActive() && p.getPlayerShop().hasAnyItems()) {
						if (209012 + c == buttonId) {
							player.getShopping().open(p);
							break;
						}
						c++;
					}
				}
				return;
			}
		}
		
		if (ReportData.get(buttonId) != null) {
			player.reportClicked = buttonId;
			return;
		}

		if ((player.getController().equals(Tutorial.TUTORIAL_CONTROLLER)) && (player.getDialogue() != null)) {
			player.getDialogue().clickButton(buttonId);
			if (player.getInterfaceManager().getMain() != 51750) {
				return;
			}
		}

		if ((player.getController().equals(EasterRing.EASTER_RING_CONTROLLER)) && (buttonId == 23132)) {
			EasterRing.cancel(player);
			return;
		}
		
		if (Agility.clickButton(player, buttonId)) {
			return;
		}
		
		if (LoyaltyShop.handleButtons(player, buttonId)) {
			return;
		}

		if (PlayerConstants.isSettingAppearance(player)) {
			return;
		}
		
		if (StarterKit.handle(player, buttonId)) {
			return;
		}

		if ((player.isDead()) || (!player.getController().canClick())) {
			return;
		}

		if (TeleportHandler.selection(player, buttonId)) {
			return;
		}

		if (SkillGoal.handle(player, buttonId)) {
			return;
		}

		if (Prestige.handleActionButtons(player, buttonId)) {
			return;
		}
		
		switch (buttonId) {
		
		/** Staff Tab */
		case 114229:
			if (PlayerConstants.isStaff(player)) {		
				String accessibility = "";
				if (PlayerConstants.isModerator(player)) {
					accessibility = "You have access to a few commands!";
				} else if (PlayerConstants.isAdministrator(player)) {
					accessibility = "You have access to most commands!";
				} else if (PlayerConstants.isOwner(player)) {
					accessibility = "You have acess to all commands!";
				}
				player.send(new SendString(accessibility, 49704));
				player.send(new SendString("</col>Rank: " + player.determineIcon(player) + " " + player.determineRank(player), 49705));	
				player.send(new SendSidebarInterface(2, 49700));
				player.send(new SendOpenTab(2));
				player.send(new SendMessage("<col=25236>Use your powers responsibly."));
			}
			break;			
		case 194042:
			if (PlayerConstants.isStaff(player)) {
				InterfaceHandler.writeText(new QuestTab(player));
				player.send(new SendSidebarInterface(2, 29400));
				player.send(new SendOpenTab(2));
			}
			break;
		
		/** Mystery Box */
		case 66108:
			MysteryBoxMinigame.play(player);
			break;
		case 66115:
			player.send(new SendString("http://www.rennatscape.proboards.com/", 12000));
			break;
		
		
		/** Drop Table */
		case 233110:
			DropTable.displayNpc(player, player.monsterSelected);
			break;
		
		/** Magic Spells teleport */
		case 50235:
		case 4140:
			InterfaceHandler.writeText(new TrainingInterface(player));
			player.send(new SendInterface(61000));
			break;
		case 50245:
		case 4143:
			InterfaceHandler.writeText(new SkillingInterface(player));
			player.send(new SendInterface(62000));
			break;
		case 50253:
		case 4146:
			InterfaceHandler.writeText(new PvPInterface(player));
			player.send(new SendInterface(63000));
			break;
		case 51005:
		case 4150:
			InterfaceHandler.writeText(new BossInterface(player));
			player.send(new SendInterface(64000));
			break;
		case 51013:
		case 6004:
			InterfaceHandler.writeText(new MinigameInterface(player));
			player.send(new SendInterface(65000));
			break;
		case 51023:
		case 6005:
			InterfaceHandler.writeText(new OtherInterface(player));
			player.send(new SendInterface(61500));
			break;		
			
		/** Report abuse */
		case 163046:
			ReportHandler.handleReport(player);
			break;
		case 2094:
			player.send(new SendRemoveInterfaces());
			player.reportClicked = 0;
			player.reportName = "";
			player.send(new SendInterface(41750));
			break;
			
		/* Bolt enchanting */
		case 75007:
			BoltEnchanting.open(player);
			break;
		
		/** Home teleport */
		case 117048:
		case 75010:
		case 84237:
			if (player.getMagic().isTeleporting()) {
				return;
			}
			if (player.inJailed()) {
				return;
			}
			if (player.inWilderness() && player.getWildernessLevel() > 20) {
				player.send(new SendMessage("You can't teleport over 20 wilderness!"));
				return;
			}
			player.getMagic().teleport(PlayerConstants.HOME.getX(), PlayerConstants.HOME.getY(), PlayerConstants.HOME.getZ(), TeleportTypes.SPELL_BOOK);
			player.send(new SendMessage("Welcome home " + player.determineIcon(player) + player.getUsername() + "!"));
			break;
		
		/** Blow Pipe */
		case 55095:
			if (player.getAttributes().getInt("ASK_KEY") == 0) {
				ToxicBlowpipe.unload(player);
			} else if (player.getAttributes().getInt("ASK_KEY") == 1) {
				TridentOfTheSeas.unload(player);
				player.getInventory().remove(11908, 1);
				player.getGroundItems().drop(new Item(11908), player.getLocation());
			}
			player.send(new SendRemoveInterfaces());
			return;
			
		/** Money Pouch */
		case 2202:
			DialogueManager.sendItem1(player, "You have <col=255>" + Utility.format(player.getMoneyPouch()) + " </col>coins stored.", 995);
			break;
		case 2203:
			player.start(new OptionDialogue("Pay by inventory", p -> {
				player.setPouchPayment(false);
				player.send(new SendRemoveInterfaces());
				player.send(new SendMessage("You will now be paying with your inventory."));
			} , "Pay by pouch", p -> {
				player.setPouchPayment(true);
				player.send(new SendRemoveInterfaces());
				player.send(new SendMessage("You will now be paying with your pouch."));
			}));
			break;

		/** Close buttons */
		case 15062:
		case 55096:
		case 190116:
		case 184163:
			player.send(new SendRemoveInterfaces());
			break;
			
		/** Equipment screen */
		case 59097:
			player.send(new SendString("</col>Melee Max Hit: @gre@" + MeleeFormulas.calculateBaseDamage(player), 15116));
			player.send(new SendString("</col>Range Max Hit: @gre@" + RangeFormulas.getRangedMaxHit(player) + ".0", 15117));
			player.send(new SendString("</col>Magic Max Hit: @gre@" + MagicFormulas.magicMaxHit(player) + ".0", 15118));
			player.send(new SendInterface(15106));
			break;
			
		/** Settings */
		case 140186:
			player.send(new SendMessage(":updateSettings:"));
			player.send(new SendSidebarInterface(11, 28400));
			player.send(new SendOpenTab(11));				
			break;
		case 110245:
			player.send(new SendMessage(":saveSettings:"));
			player.send(new SendSidebarInterface(11, 904));
			player.send(new SendOpenTab(11));
			player.send(new SendMessage("@red@Your settings have been saved!"));
			break;
		case 110248:
			player.send(new SendMessage(":defaultSettings:"));
			player.send(new SendMessage("@red@Your settings have been reset!"));
			break;
		case 140185:
			player.send(new SendInterface(28200));
			break;
		case 140189:
			player.send(new SendInterface(37500));
			player.send(new SendString("Color chosen: @or2@-", 37506));
			break;		
		case 110046:
			player.send(new SendMessage(":transparentTab:"));
			break;			
		case 110047:
			player.send(new SendMessage(":transparentChatbox:"));
			break;		
		case 110048:
			player.send(new SendMessage(":sideStones:"));
			break;
		case 111024:
			if (player.getDelay().elapsed() < 3_000) {
				player.send(new SendMessage("Please wait before doing this again!"));
				return;
			}
			if (player.isPrestigeColors()) {
				player.setPrestigeColors(false);
				player.send(new SendMessage(":prestigeColorsFalse:"));
				player.getSkill().resetColors();
				player.send(new SendMessage("Prestige colors will now not display in skill tab."));
			} else {
				player.setPrestigeColors(true);
				player.send(new SendMessage(":prestigeColorsTrue:"));
				player.getSkill().resetColors();
				player.send(new SendMessage("Prestige colors will now display in skill tab."));
			}
			player.getDelay().reset();
			break;
	
		/** Player Profiler */
		case 201051:
		case 201053:
			player.send(new SendConfig(1032, 1));
			player.setProfilePrivacy(true);
			player.send(new SendMessage("@dre@You have enabled your privacy settings."));
			break;
		case 201052:
		case 201054:
			player.send(new SendConfig(1032, 2));
			player.setProfilePrivacy(false);
			player.send(new SendMessage("@dre@You have disabled your privacy settings."));
			break;
		case 201055:
			PlayerProfiler.myProfile(player);
			break;
		case 203022:
		case 203025:
			PlayerProfiler.manageReputation(player, player.viewing, buttonId);
			break;
		case 201059:
		case 185046:
			ProfileLeaderboard.open(player, "Views");
			break;
		case 185049:
			ProfileLeaderboard.open(player, "Likes");
			break;
		case 185052:
			ProfileLeaderboard.open(player, "Dislikes");
			break;
		case 185055:
			ProfileLeaderboard.open(player, "Ratio");
			break;

		/** Pricer checker */
		case 59103:
			player.getPriceChecker().open();
			break;
		case 189121:
			player.getPriceChecker().depositeAll();
			break;
		case 189194:
			player.getPriceChecker().withdrawAll();
			break;
		case 189124:
			player.send(new SendMessage("Coming soon!"));
			break;
			
		/** Experience lock */	
		case 59206:
			player.start(new OptionDialogue("Lock experience", p -> {
				player.getSkill().setExpLock(true);
				player.send(new SendMessage("You have @blu@locked</col> your experience."));
				player.send(new SendRemoveInterfaces());
			}, "Unlock experience", p -> {
				player.getSkill().setExpLock(false);
				player.send(new SendMessage("You have @blu@unlocked</col> your experience."));
				player.send(new SendRemoveInterfaces());
			}));
			break;
			
		/** Bank */
		case 195087: // option
			player.send(new SendInterface(32500));
			break;
		case 127000:
			player.send(new SendInterface(5292));
			break;

		/** Shop exchange */
		case 209002:
			player.start(new OptionDialogue("Search name", p -> {
				player.setEnterXInterfaceId(55777);
				player.getClient().queueOutgoingPacket(new SendEnterString());
			}, "Search item", p -> {
				player.setEnterXInterfaceId(55778);
				player.getClient().queueOutgoingPacket(new SendEnterString());
			}));
			break;

		/** Fire color */
		case 194098:
		case 194101:
		case 194104:
		case 194107:
			FireColor.main(player, buttonId);
			break;

		/** Achievement & Quest Tab */
		case 114220:
			InterfaceHandler.writeText(new AchievementTab(player));
			player.send(new SendSidebarInterface(2, 31000));
			break;
		case 121028:
			InterfaceHandler.writeText(new QuestTab(player));
			player.send(new SendSidebarInterface(2, 29400));
			break;
		case 114226:
			player.send(new SendMessage("@red@You have refreshed the Quest Tab."));
			InterfaceHandler.writeText(new QuestTab(player));
			break;
		case 115077:
			player.send(new SendString("@blu@" + player.getUsername() + "'s tracked points.", 8144));
			InterfaceHandler.writeText(new PointsInterface(player));
			player.send(new SendInterface(8134));
			break;
		case 115078:
			int linePosition = 8145;
			HashMap<String, Integer> map = player.getProperties().getPropertyValues("MOB");
			
			List<String> alphabetical = new ArrayList<>();
			alphabetical.addAll(map.keySet());			
			alphabetical.sort(String.CASE_INSENSITIVE_ORDER);
			
			for (String key : alphabetical) {
				String line = Utility.formatPlayerName(key.toLowerCase().replaceAll("_", " ")) + ": @dre@" + map.get(key);
				player.send(new SendString("@dre@Player Log Panel | " + alphabetical.size() + " Logs", 8144));
				player.send(new SendString("</col>" + line, linePosition++));
			}
			
			map = player.getProperties().getPropertyValues("BARROWS");
			for (String key : map.keySet()) {
				String line = Utility.formatPlayerName(key.toLowerCase().replaceAll("_", " ")) + ": @dre@" + map.get(key);
				player.send(new SendString("</col>" + line, linePosition++));
			}
			
			while (linePosition < 8193) {
				player.send(new SendString("", linePosition++));
			}
			
			player.send(new SendInterface(8134));
			break;

		/** Players online && Quest tab */
		case 115062:
		case 154052:
			PlayersOnline.showPlayers(player, p -> {
				return true;
			});
			break;
		case 115070:
			/*player.send(new SendString("@dre@Vencillio's Latest Updates (</col> " + GameSettings.LATEST_UPDATE.length + " @dre@)", 8144));
			for (int i = 0; i < 30; i++) {
				player.send(new SendString("", 8145 + i));
			}
			for (int i = 0; i < GameSettings.LATEST_UPDATE.length; i++) {
				player.send(new SendString("</col>" + (i + 1) + ") @dre@" + GameSettings.LATEST_UPDATE[i], 8145 + i));
			}
			player.send(new SendInterface(8134));*/
			break;

		/** Special attack button */
		case 29124:
		case 29049:
		case 29199:
		case 29138:
		case 48034:
		case 155:
		case 30108:
		case 29238:
			player.getSpecialAttack().clickSpecialButton(buttonId);
			break;
		case 29074:
			if (player.getSpecialAttack().getAmount() != 100) {
				player.send(new SendMessage("You do not have enough special attack to do this!"));
				return;
			}
			player.getUpdateFlags().sendAnimation(new Animation(1056));
			player.getUpdateFlags().sendGraphic(new Graphic(246));
			player.getSpecialAttack().deduct(100);
			player.getSpecialAttack().update();
			player.getSpecialAttack().setInitialized(false);
			player.getLevels()[Skills.ATTACK] = (short) (player.getMaxLevels()[Skills.ATTACK] * 0.9);
			player.getLevels()[Skills.DEFENCE] = (short) (player.getMaxLevels()[Skills.DEFENCE] * 0.9);
			player.getLevels()[Skills.RANGED] = (short) (player.getMaxLevels()[Skills.RANGED] * 0.9);
			player.getLevels()[Skills.MAGIC] = (short) (player.getMaxLevels()[Skills.MAGIC] * 0.9);
			player.getLevels()[Skills.STRENGTH] = (short) (player.getMaxLevels()[Skills.STRENGTH] * 1.2);
			player.getSkill().update(Skills.ATTACK);
			player.getSkill().update(Skills.DEFENCE);
			player.getSkill().update(Skills.RANGED);
			player.getSkill().update(Skills.MAGIC);
			player.getSkill().update(Skills.STRENGTH);
			player.getUpdateFlags().sendForceMessage("Raarrrrrgggggghhhhhhh!");
			break;
			

		case 155026:
			player.getClient().queueOutgoingPacket(new SendInterface(38700));
			break;
		case 151045:
			player.getClient().queueOutgoingPacket(new SendInterface(39700));
			break;
		case 9118:
		case 83051:
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			break;
		case 83093:
			player.getClient().queueOutgoingPacket(new SendInterface(15106));
			break;
		case 3162:
			player.setMusicVolume((byte) 4);
			player.getClient().queueOutgoingPacket(new SendConfig(168, 4));
			break;
		case 70209:
			player.setEnterXInterfaceId(6969);
			break;
		case 3163:
		case 3164:
		case 3165:
		case 3166:
			player.setMusicVolume((byte) (3166 - buttonId));
			player.getClient().queueOutgoingPacket(new SendConfig(168, player.getMusicVolume()));
			break;
		case 3173:
			player.setSoundVolume((byte) 4);
			player.getClient().queueOutgoingPacket(new SendConfig(169, 4));
			break;
		case 3174:
		case 3175:
		case 3176:
		case 3177:
			player.setSoundVolume((byte) (3177 - buttonId));
			player.getClient().queueOutgoingPacket(new SendConfig(169, player.getSoundVolume()));
			break;
		case 24125:
			player.getAttributes().remove("manual");
			break;
		case 24126:
			player.getAttributes().set("manual", (byte) 1);
			break;
		case 108005:
			player.getClient().queueOutgoingPacket(new SendInterface(19148));
			break;
		case 14067:
			player.setAppearanceUpdateRequired(true);
			player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
			break;
		case 9154:
			if (player.getCombat().inCombat()) {
				player.getClient().queueOutgoingPacket(new SendMessage("You can't logout while in combat!"));
			} else {
				if (player.getClient().getStage() == Client.Stages.LOGGED_IN) {
					player.logout(false);
				}
			}
			break;

		case 74:
		case 152:
		case 33230:
		case 74214:
			player.getRunEnergy().setRunning(!player.getRunEnergy().isRunning());
			player.getClient().queueOutgoingPacket(new SendConfig(173, player.getRunEnergy().isRunning() ? 1 : 0));
			break;
		case 211172:
			player.getRunEnergy().toggleResting();
			break;
			
		case 3138:
			player.setScreenBrightness((byte) 1);
			break;
		case 3140:
			player.setScreenBrightness((byte) 2);
			break;
		case 3142:
			player.setScreenBrightness((byte) 3);
			break;
		case 3144:
			player.setScreenBrightness((byte) 4);
			break;
		case 100228:
			player.setMultipleMouseButtons((byte) (player.getMultipleMouseButtons() == 0 ? 1 : 0));
			break;
		case 100231:
			player.setChatEffectsEnabled((byte) (player.getChatEffectsEnabled() == 0 ? 1 : 0));
			break;
		case 3189:
			player.setSplitPrivateChat((byte) (player.getSplitPrivateChat() == 0 ? 1 : 0));
			player.getClient().queueOutgoingPacket(new SendConfig(287, player.getSplitPrivateChat()));
			break;
		case 100237:
			player.setAcceptAid((byte) (player.getAcceptAid() == 0 ? 1 : 0));
			break;
		case 150:
		case 89061:
		case 93202:
		case 93209:
		case 93217:
		case 93225:
		case 94051:
			player.setRetaliate(!player.isRetaliate());
			break;

		/** Items kept on death */
		case 59100:
			int kept = 3;

			if (player.getSkulling().isSkulled()) {
				kept = 0;
			}

			if (player.getPrayer().active(Prayer.PROTECT_ITEM)) {
				kept++;
			}

			Queue<Item> dropItems = new PriorityQueue<Item>(42, PlayerDrops.ITEM_VALUE_COMPARATOR);

			for (Item i : player.getInventory().getItems()) {
				if (i != null) {
					dropItems.add(new Item(i.getId(), i.getAmount()));
				}
			}

			for (Item i : player.getEquipment().getItems()) {
				if (i != null) {
					dropItems.add(new Item(i.getId(), i.getAmount()));
				}
			}

			Item dropItem = null;
			Item[] toKeep = new Item[kept];
			int keepIndex = 0;

			for (int i = 0; i < kept; i++) {
				Item keep = dropItems.poll();

				if (keep != null) {
					if (keep.getAmount() == 1) {
						toKeep[keepIndex++] = keep;
					} else {
						keep.remove(1);
						toKeep[keepIndex++] = new Item(keep.getId(), 1);
					}
				}
			}

			Item[] toDrop = new Item[dropItems.size()];
			int dropIndex = 0;

			while ((dropItem = dropItems.poll()) != null) {
				if (dropItem.getDefinition().isTradable() || !dropItem.getDefinition().isTradable() ||ItemCheck.isItemDyedWhip(dropItem)) {
					toDrop[dropIndex++] = dropItem;
				}
			}

			for (int i = 17109; i < 17131; i++) {
				player.send(new SendString("", i));
			}

			player.send(new SendString("Items you will keep on death:", 17104));
			player.send(new SendString("Items you will lose on death:", 17105));
			player.send(new SendString("Player Information", 17106));
			player.send(new SendString("Max items kept on death:", 17107));
			player.send(new SendString("~ " + kept + " ~", 17108));
			player.send(new SendString("The normal amount of", 17111));
			player.send(new SendString("items kept is three.", 17112));
			switch (kept) {
			case 0:
			default:
				player.send(new SendString("Items you will keep on death:", 17104));
				player.send(new SendString("Items you will lose on death:", 17105));
				player.send(new SendString("You're marked with a", 17111));
				player.send(new SendString("@red@skull. @lre@This reduces the", 17112));
				player.send(new SendString("items you keep from", 17113));
				player.send(new SendString("three to zero!", 17114));
				break;
			case 1:
				player.send(new SendString("Items you will keep on death:", 17104));
				player.send(new SendString("Items you will lose on death:", 17105));
				player.send(new SendString("You're marked with a", 17111));
				player.send(new SendString("@red@skull. @lre@This reduces the", 17112));
				player.send(new SendString("items you keep from", 17113));
				player.send(new SendString("three to zero!", 17114));
				player.send(new SendString("However, you also have", 17115));
				player.send(new SendString("the @red@Protect @lre@Items prayer", 17116));
				player.send(new SendString("active, which saves you", 17117));
				player.send(new SendString("one extra item!", 17118));
				break;
			case 3:
				player.send(new SendString("Items you will keep on death(if not skulled):", 17104));
				player.send(new SendString("Items you will lose on death(if not skulled):", 17105));
				player.send(new SendString("You have no factors", 17111));
				player.send(new SendString("affecting the items you", 17112));
				player.send(new SendString("keep.", 17113));
				break;
			case 4:
				player.send(new SendString("Items you will keep on death(if not skulled):", 17104));
				player.send(new SendString("Items you will lose on death(if not skulled):", 17105));
				player.send(new SendString("You have the @red@Protect", 17111));
				player.send(new SendString("@red@Item @lre@prayer active,", 17112));
				player.send(new SendString("which saves you one", 17113));
				player.send(new SendString("extra item!", 17114));
				break;
			}
			player.send(new SendString("Carried wealth:", 17121));
			BigInteger carrying = player.getInventory().getContainerNet().add(player.getEquipment().getContainerNet());
			if (carrying.equals(BigInteger.ZERO)) {
				player.send(new SendString("@red@Nothing!", 17122));
			} else {
				player.send(new SendString("@red@" + NumberFormat.getNumberInstance(Locale.US).format(carrying) + "</col> coins.", 17122));
			}


			BigInteger risked = BigInteger.ZERO;
			for (Item dropping : toDrop) {
				if (dropping == null || dropping.getDefinition() == null) {
					continue;
				}
				
				risked = risked.add(new BigInteger(String.valueOf(dropping.getDefinition().getGeneralPrice())).multiply(new BigInteger(String.valueOf(dropping.getAmount()))));
			}

			player.send(new SendString("Risked wealth:", 17124));

			if (risked.equals(BigInteger.ZERO)) {
				player.send(new SendString("@red@Nothing!", 17125));
			} else {
				player.send(new SendString("@red@" + NumberFormat.getNumberInstance(Locale.US).format(risked) + "</col> coins.", 17125));
			}

			player.send(new SendUpdateItems(10494, toKeep));
			player.send(new SendUpdateItems(10600, toDrop));
			player.send(new SendInterface(17100));
			break;

		/** Teleport */
		/** Teleports */
		case 238107:
		case 242083:
		case 246059:
		case 250035:
		case 254011:
		case 240095:
			TeleportHandler.teleport(player);
			break;

		case 5227:
		case 238077:// Training
		case 242053:
		case 246029:
		case 253237:
		case 240065:
		case 250005:
			InterfaceHandler.writeText(new TrainingInterface(player));
			player.send(new SendInterface(61000));
			player.send(new SendString("Selected: @red@None", 61031));
			player.send(new SendString("Cost: @red@Free", 61032));
			player.send(new SendString("Requirement: @red@None", 61033));
			player.send(new SendString("Other: @red@None", 61034));
			break;
		case 238080:// Skilling
		case 242056:
		case 246032:
		case 253240:
		case 240068:
		case 250008:
			InterfaceHandler.writeText(new SkillingInterface(player));
			player.send(new SendInterface(62000));
			player.send(new SendString("Selected: @red@None", 62031));
			player.send(new SendString("Cost: @red@Free", 62032));
			player.send(new SendString("Requirement: @red@None", 62033));
			player.send(new SendString("Other: @red@None", 62034));
			break;
		case 238083:// PvP
		case 242059:
		case 246035:
		case 253243:
		case 240071:
		case 250011:
			InterfaceHandler.writeText(new PvPInterface(player));
			player.send(new SendInterface(63000));
			player.send(new SendString("Selected: @red@None", 63031));
			player.send(new SendString("Cost: @red@Free", 63032));
			player.send(new SendString("Requirement: @red@None", 63033));
			player.send(new SendString("Other: @red@None", 63034));
			break;
		case 238086:// Boss
		case 246038:
		case 253246:
		case 240074:
		case 250014:
		case 242062:
			InterfaceHandler.writeText(new BossInterface(player));
			player.send(new SendInterface(64000));
			player.send(new SendString("Selected: @red@None", 64031));
			player.send(new SendString("Cost: @red@Free", 64032));
			player.send(new SendString("Requirement: @red@None", 64033));
			player.send(new SendString("Other: @red@None", 64034));
			break;
		case 238089:// Minigame
		case 253249:
		case 246041:
		case 240077:
		case 250017:
		case 242065:
			Utility.writeBuffer(player.getUsername());
			InterfaceHandler.writeText(new MinigameInterface(player));
			player.send(new SendInterface(65000));
			player.send(new SendString("Selected: @red@None", 65031));
			player.send(new SendString("Cost: @red@Free", 65032));
			player.send(new SendString("Requirement: @red@None", 65033));
			player.send(new SendString("Other: @red@None", 65034));
			break;
		case 238092:// Other
		case 253252:
		case 240080:
		case 250020:
		case 242068:
		case 246044:
			InterfaceHandler.writeText(new OtherInterface(player));
			player.send(new SendInterface(61500));
			player.send(new SendString("Selected: @red@None", 61531));
			player.send(new SendString("Cost: @red@Free", 61532));
			player.send(new SendString("Requirement: @red@None", 61533));
			player.send(new SendString("Other: @red@None", 61534));
			break;

		default:
			if (CreditHandler.handleClicking(player, buttonId)) {
				return;
			}
			if (GenieLamp.handle(player, buttonId)) {
				return;
			}
			if (GenieReset.handle(player, buttonId))
				return;
			if (AchievementButtons.handleButtons(player, buttonId))
				return;
			if (SkillsChat.handle(player, buttonId))
				break;
			if (player.getSummoning().click(buttonId))
				break;
			if (SummoningCreation.create(player, buttonId))
				break;
			if (Fletching.SINGLETON.clickButton(player, buttonId))
				break;
			if (com.vencillio.rs2.content.skill.craftingnew.Crafting.SINGLETON.clickButton(player, buttonId))
				break;
			if (Crafting.handleCraftingByButtons(player, buttonId))
				break;
			if (HideTanning.clickButton(player, buttonId))
				break;
			if (CookingTask.handleCookingByAmount(player, buttonId))
				break;
			if ((player.getDialogue() != null) && (player.getDialogue().clickButton(buttonId)))
				break;
			if (Autocast.clickButton(player, buttonId))
				break;
			if (Emotes.clickButton(player, buttonId))
				break;
			if (DuelingConstants.clickDuelButton(player, buttonId))
				break;
			if (player.getTrade().clickTradeButton(buttonId))
				break;
			if (player.getBank().clickButton(buttonId))
				break;
			if (player.getMagic().clickMagicButtons(buttonId))
				break;
			if (EquipmentConstants.clickAttackStyleButtons(player, buttonId))
				break;
			if (SmithingConstants.clickSmeltSelection(player, buttonId))
				break;
			if ((player.getAttributes().get("herbloreitem1") != null) && ((((Item) player.getAttributes().get("herbloreitem1")).getId() == 227) || (((Item) player.getAttributes().get("herbloreitem2")).getId() == 227) ? !HerbloreUnfinishedPotionTask.handleHerbloreButtons(player, buttonId) : !HerbloreFinishedPotionTask.handleHerbloreButtons(player, buttonId)))
				break;
			break;
		}
	}
}
