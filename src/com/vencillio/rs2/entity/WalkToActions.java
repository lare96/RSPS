package com.vencillio.rs2.entity;

import com.vencillio.VencillioConstants;
import com.vencillio.core.cache.map.*;
import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.task.Task;
import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.*;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.CrystalChest;
import com.vencillio.rs2.content.DropTable;
import com.vencillio.rs2.content.FountainOfRune;
import com.vencillio.rs2.content.Prestige;
import com.vencillio.rs2.content.achievements.AchievementHandler;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.Emotion;
import com.vencillio.rs2.content.dialogue.OneLineDialogue;
import com.vencillio.rs2.content.dialogue.OptionDialogue;
import com.vencillio.rs2.content.dialogue.impl.*;
import com.vencillio.rs2.content.dialogue.impl.teleport.SpiritTree;
import com.vencillio.rs2.content.dialogue.impl.teleport.WildernessLever;
import com.vencillio.rs2.content.dwarfcannon.DwarfMultiCannon;
import com.vencillio.rs2.content.membership.MysteryBoxMinigame;
import com.vencillio.rs2.content.minigames.barrows.Barrows;
import com.vencillio.rs2.content.minigames.clanwars.ClanWarsFFA;
import com.vencillio.rs2.content.minigames.fightcave.TzharrGame;
import com.vencillio.rs2.content.minigames.fightpits.FightPits;
import com.vencillio.rs2.content.minigames.godwars.GodWars;
import com.vencillio.rs2.content.minigames.pestcontrol.PestControl;
import com.vencillio.rs2.content.minigames.plunder.PyramidPlunder;
import com.vencillio.rs2.content.minigames.warriorsguild.ArmourAnimator;
import com.vencillio.rs2.content.minigames.warriorsguild.CyclopsRoom;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGame;
import com.vencillio.rs2.content.minigames.weapongame.WeaponGameStore;
import com.vencillio.rs2.content.pets.BossPets;
import com.vencillio.rs2.content.pets.BossPets.PetData;
import com.vencillio.rs2.content.shopping.impl.BountyShop;
import com.vencillio.rs2.content.shopping.impl.PestShop;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.agility.Agility;
import com.vencillio.rs2.content.skill.agility.obstacle.interaction.WalkInteraction;
import com.vencillio.rs2.content.skill.cooking.CookingTask;
import com.vencillio.rs2.content.skill.crafting.*;
import com.vencillio.rs2.content.skill.farming.Farming;
import com.vencillio.rs2.content.skill.firemaking.FireColor;
import com.vencillio.rs2.content.skill.herblore.PotionDecanting;
import com.vencillio.rs2.content.skill.hunter.Impling.ImplingRewards.Implings;
import com.vencillio.rs2.content.skill.magic.MagicSkill;
import com.vencillio.rs2.content.skill.magic.MagicSkill.SpellBookTypes;
import com.vencillio.rs2.content.skill.mining.Mining;
import com.vencillio.rs2.content.skill.prayer.BoneBurying;
import com.vencillio.rs2.content.skill.runecrafting.AbyssObjects;
import com.vencillio.rs2.content.skill.runecrafting.RunecraftingTask;
import com.vencillio.rs2.content.skill.smithing.SmithingConstants;
import com.vencillio.rs2.content.skill.thieving.HomeStalls;
import com.vencillio.rs2.content.skill.thieving.ThievingNpcTask;
import com.vencillio.rs2.content.skill.thieving.ThievingStallTask;
import com.vencillio.rs2.content.skill.thieving.WallSafes;
import com.vencillio.rs2.content.skill.woodcutting.WoodcuttingTask;
import com.vencillio.rs2.content.wilderness.Lockpick;
import com.vencillio.rs2.content.wilderness.TargetSystem;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemCheck;
import com.vencillio.rs2.entity.item.impl.GroundItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.object.GameObject;
import com.vencillio.rs2.entity.object.ObjectConstants;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.net.out.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WalkToActions {

	/**
	 * Npcs that open a store
	 */
	public static final int[][] NPC_STORE_DATA = {
			{519, 0},
			{518, 0}, // General
			{225, 15}, // Consumables store
			{527, 16}, // Weapon store
			{505, 17}, // Skilling store
			{528, 18}, // Armour store
			{536, 25}, // Range store
			{5314, 26}, // Magic store
			{603, 27}, // Accessories store
			{4306, 20}, // Skillcape store
			{5919, 3}, // Graceful store
			{22, 29}, // Merchant store
			{1758, 5}, // Pestcontrol store
			{3984, 31}, // Packs
	};

	/**
	 * Default dialogues for npcs
	 */
	public final static String[] DEFAULT_DIALOGUES = {"When you feel sad, just sing Roar - by Katy Perry!", "Thank the Tannerscape gods for this beautiful day!", "Break free of your cage and be happy!", "Porn is the key to a happy marriage.", "I'm in the mood to groove it out!", "Don't let fear hold you down.", "Just let it out, let it out.", "Make your dreams a reality.", "Be your own creator.", "Tanner blesses your soul."};

	public static void clickNpc(final Player player, final int option, int slot) {
		if (player.getMagic().isTeleporting()) {
			return;
		}

		if (player.getTrade().trading()) {
			player.getTrade().end(false);
		}

		if (player.getDueling().isDueling()) {
			player.getDueling().decline();
		}

		if ((slot > World.getNpcs().length) || (slot < 0)) {
			return;
		}

		player.getMovementHandler().reset();

		final Mob mob = World.getNpcs()[slot];

		if (mob == null) {
			player.getMovementHandler().reset();
			return;
		}

		if (VencillioConstants.DEV_MODE) {
			player.getClient().queueOutgoingPacket(new SendMessage("option: " + option));
		}

		TaskQueue.queue(new FollowToEntityTask(player, mob) {
			@Override
			public void onDestination() {
				if (mob.face()) {
					mob.face(player);
				}

				player.face(mob);

				if (mob.getSize() > 1) {
					player.getMovementHandler().reset();
				}

				WalkToActions.finishClickNpc(player, option, mob);
			}
		});
	}

	public static void clickObject(final Player player, final int option, final int id, final int x, final int y) {
		if (player.getMagic().isTeleporting()) {
			return;
		}

		if (player.getTrade().trading()) {
			player.getTrade().end(false);
		}

		if (player.getDueling().isDueling()) {
			player.getDueling().decline();
		}

		int z = player.getLocation().getZ();

		RSObject object = Region.getObject(x, y, z);

		if ((object == null) && (!PlayerConstants.isOverrideObjectExistance(player, id, x, y, z))) {
			return;
		}

		final int[] length = ObjectConstants.getObjectLength(id, object == null ? 0 : object.getFace());

		if ((id == 1815) && x == 3098 && y == 3499 && player.getX() == x && player.getY() == y) {
			player.start(new WildernessLever(player));
			return;
		}
		if ((id == 1815)) {
			TaskQueue.queue(new PullLeverTask(player, x, y, length[0], length[1]) {
				@Override
				public void onDestination() {
					player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);
					player.getMagic().teleportNoWildernessRequirement(2561, 3311, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
				}
			});
			return;
		} else if ((id == 1814)) {
			TaskQueue.queue(new PullLeverTask(player, x, y, length[0], length[1]) {
				@Override
				public void onDestination() {
					player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);
					player.getMagic().teleportNoWildernessRequirement(3153, 3923, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
				}
			});
			return;
		} else if ((id == 5960) || (id == 5959)) {
			TaskQueue.queue(new PullLeverTask(player, x, y, length[0], length[1]) {
				@Override
				public void onDestination() {
					player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);
					player.getMagic().teleportNoWildernessRequirement(id == 5960 ? 3090 : 2539, id == 5960 ? 3956 : 4712, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
				}
			});
			return;
		} else if ((id == 9706)) {
			TaskQueue.queue(new PullLeverTask(player, x, y, length[0], length[1]) {
				@Override
				public void onDestination() {
					player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);
					player.getMagic().teleportNoWildernessRequirement(3105, 3951, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
				}
			});
			return;
		} else if ((id == 9707)) {
			TaskQueue.queue(new PullLeverTask(player, x, y, length[0], length[1]) {
				@Override
				public void onDestination() {
					player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);
					player.getMagic().teleportNoWildernessRequirement(3105, 3956, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
				}
			});
			return;
		}

		TaskQueue.queue(new WalkToTask(player, x, y, length[0], length[1]) {
			@Override
			public void onDestination() {
				player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);

				WalkToActions.finishObjectClick(player, id, option, x, y);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static final void finishClickNpc(Player player, int option, Mob mob) {
		int id = mob.getId();

		if (player.getSummoning().interact(mob, option)) {
			return;
		}

		if (Implings.implings.containsKey(mob.getId())) {
			Implings.catchImp(player, mob);
		}
		else {
			switch (option) {

				case 1:// NPC FIRST CLICK
					if (player.getFishing().clickNpc(mob, id, 1)) {
						return;
					}

					for (int i = 0; i < NPC_STORE_DATA.length; i++) {
						if (NPC_STORE_DATA[i][0] == id) {
							player.getShopping().open(NPC_STORE_DATA[i][1]);
							return;
						}
					}

					if (BossPets.pickupPet(player, mob)) {
						return;
					}

					switch (id) {
						case 3936://Sailor
							player.start(new SailorDialogue(player));
							break;
						case 2914://Otto
							player.start(new OttoGodblessed(player));
							break;
						case 2801://Sheep
							if (!player.getInventory().hasItemId(1735)) {
								DialogueManager.sendItem1(player, "A shear is required to do this!", 1735);
								return;
							}
							if (player.getInventory().getFreeSlots() == 0) {
								DialogueManager.sendStatement(player, "You do not have any free inventory spaces to do this!");
								return;
							}
							if (player.getDelay().elapsed() < 5000) {
								return;
							}
							player.send(new SendMessage("You manage to get some wool."));
							player.getUpdateFlags().sendAnimation(new Animation(893));
							player.getInventory().add(1737, 1);
							mob.transform(2697);
							player.getDelay().reset();
							TaskQueue.queue(new ShearingTask(mob, 15));
							AchievementHandler.activateAchievement(player, AchievementList.SHEAR_10_SHEEPS, 1);
							AchievementHandler.activateAchievement(player, AchievementList.SHEAR_150_SHEEPS, 1);
							break;
						case 1011://Gambler
							player.start(new GamblerDialogue(player));
							break;
						case 1103://Weapon Game
							player.start(new WeaponGameDialogue(player));
							break;
						case 534://Clothing store
							player.start(new OptionDialogue("Clothing shop 1", p -> {
								player.getShopping().open(28);
							}, "Clothing shop 2", p -> {
								player.getShopping().open(40);
							}));
							break;
						case 6749://Dunce
							if (player.inMemberZone()) {
								player.start(new DunceDialogue(player));
							} else {
								player.start(new StaffTitleDialogue(player));
							}
							break;
						case 311://Adam
							player.start(new AdamDialogue(player));
							break;
						case 2181://Firecape pet
							player.start(new TzhaarMejKahDialogue(player));
							break;
						case 2195://William
							DialogueManager.sendNpcChat(player, 2195, Emotion.VERY_SAD, "Help me...");
							break;
						case 490://Boss tasks
							player.start(new NeiveDialogue(player));
							break;
						case 1603://Mage Arena
							player.start(new KolodionDialogue(player));
							break;
						case 118://Fire color
							player.start(new FiremakingDialogue(player));
							break;
						case 5811://master crafter
							player.getShopping().open(36);
							break;
						case 822:// Oziach
							player.start(new OziachDialogue(player));
							break;
						case 954:// Barrows
							DialogueManager.sendNpcChat(player, 954, Emotion.HAPPY, "Oy! Need your items repaired?", "I can fix them for 250,000 coins each.", "Simply use your item on me.");
							break;
						case 5787:// Agility
							player.send(new SendString("Agility Ticket Exchange", 8383));
							player.send(new SendInterface(8292));
							break;
						case 732:// Farming Store
							player.start(new OptionDialogue("Farming Store", p -> {
								player.getShopping().open(32);
							}, "Herblore Store", p -> {
								player.getShopping().open(33);
							}));
							break;
						case 5523:// Swagster
							player.start(new MembershipDialogue(player));
							break;
						case 3231:// Leater tanner
							HideTanning.sendTanningInterface(player);
							break;
						case 394:// Banker
						case 395:// Banker
						case 2182:
							player.getBank().openBank();
							break;
						case 5860:// Big Mo
							player.start(new BigMoDialogue(player));
							break;
						case 13:// Piles
							player.start(new PilesDialogue(player));
							break;
						case 403:// Slayer
							player.start(new VannakaDialogue(player));
							break;
						case 1306:// Make over mage
							player.start(new MakeoverMage(player));
							break;
						case 315:// Emblem trader
							player.start(new EmblemDialogue(player));
							break;
						case 4936:// Mage of zamorak
							player.start(new RunecraftingTeleport(player, mob));
							break;
						case 5419:// Exchange agent
							player.start(new ShopExchangeDialogue(player));
							break;
						case 326:// Genie reset
							player.start(new GenieResetDialogue(player));
							break;
						case 3951:// Bank Pin
							player.start(new SecurityDialogue(player));
							break;
						case 1325:// Npc guide
							player.start(new HariDialogue(player));
							break;
						case 606:// Prestige
							player.start(new PrestigeDialogue(player));
							break;
						case 2461:// Kamfree
							player.start(new KamfreeDialogue());
							break;
						case 1756:// Void knight in game
							DialogueManager.sendNpcChat(player, 1756, Emotion.ANGRY_4, "Don't distract me! Go kill them!");
							player.send(new SendMessage("Maybe I should focus on defeating the portals..."));
							break;
						case 1771:// Novice squire
							DialogueManager.sendNpcChat(player, 1771, Emotion.HAPPY, "Welcome to Pest Control " + player.getUsername() + "!");
							break;
						case 5527:// Achievement
							player.start(new AchievementDialogue(player));
							break;
						case 6524:// Decanting
							player.start(new DecantingDialogue(player));
							break;
						case 3257:
						case 3258:
							ThievingNpcTask.attemptThieving(player, mob);
							break;


						default:
							if (player.getUsername().equalsIgnoreCase(PlayerConstants.OWNER_USERNAME[0])) {
								player.getClient().queueOutgoingPacket(new SendMessage("Mob id: " + mob.getId()));
							}

							if (OneLineDialogue.doOneLineChat(player, id)) {
								return;
							}
							DialogueManager.sendNpcChat(player, id, Emotion.DEFAULT, Utility.randomElement(DEFAULT_DIALOGUES));

							break;
					}
					break;

				case 2:// NPC SECOND CLICK
					for (int i = 0; i < NPC_STORE_DATA.length; i++) {
						if (NPC_STORE_DATA[i][0] == id) {
							player.getShopping().open(NPC_STORE_DATA[i][1]);
							return;
						}
					}

					if (player.getFishing().clickNpc(mob, id, 2)) {
						return;
					}

					if (ThievingNpcTask.attemptThieving(player, mob)) {
						return;
					}

					switch (id) {
						case 2195://William
							player.getShopping().open(37);
							break;
						case 5527:// Achievement
							player.getShopping().open(89);
							break;
						case 1603://Mage Arena
							player.getShopping().open(95);
							break;
						case 2130://Snakelings
						case 2131:
						case 2132:
							if (player.getBossPet() == null || player.getBossPet().isDead()) {
								return;
							}
							if (player.getBossPet() != mob || mob.getOwner() != player) {
								player.getClient().queueOutgoingPacket(new SendMessage("This is not your pet."));
								return;
							}
							List<Object[]> dialogues = new ArrayList<>();
							if (id != 2130) {
								dialogues.add(new Object[]{"Green", (Consumer<Player>) p -> {
									player.getBossPet().transform(2130);
									player.send(new SendRemoveInterfaces());
								}});
							}
							if (id != 2131) {
								dialogues.add(new Object[]{"Red", (Consumer<Player>) p -> {
									player.getBossPet().transform(2131);
									player.send(new SendRemoveInterfaces());
								}});
							}
							if (id != 2132) {
								dialogues.add(new Object[]{"Blue", (Consumer<Player>) p -> {
									player.getBossPet().transform(2132);
									player.send(new SendRemoveInterfaces());
								}});
							}
							player.start(new OptionDialogue((String) dialogues.get(0)[0], (Consumer<Player>) (dialogues.get(0)[1]), (String) dialogues.get(1)[0], (Consumer<Player>) (dialogues.get(1)[1])));
							break;
						case 822:// Oziach
							player.getShopping().open(34);
							break;
						case 5787:// Agility
							player.send(new SendString("Agility Ticket Exchange", 8383));
							player.send(new SendInterface(8292));
							break;
						case 5523:// Swagster
							player.start(new OptionDialogue("Credit Shop 1", p -> {
								player.getShopping().open(94);
							}, "Credit Store 2", p -> {
								player.getShopping().open(90);
							}, "Credit Store 3", p -> {
								player.getShopping().open(87);
							}));

							break;
						case 606:// Prestige
							player.getShopping().open(93);
							break;
						case 395:// Banker
							player.getBank().openBank();
							break;
						case 5860:// Big Mo
							player.send(new SendInterface(55000));
							break;
						case 403:// Slayer
						case 490://Boss tasks
							player.getShopping().open(6);
							break;
						case 315:// Emblem trader
							player.getShopping().open(BountyShop.SHOP_ID);
							break;
						case 2183:
						case 2184:
							player.getShopping().open(4); //Tokkul shop
							break;
						case 5419:// Exchange agent
							player.start(new OptionDialogue("Edit shop description (100k).", p -> {
								player.setEnterXInterfaceId(55776);
								player.getClient().queueOutgoingPacket(new SendEnterString());
							}, "Edit shop.", p -> {
								player.getShopping().open(player);
							}));
							break;
						case 326:// Genie reset
							player.send(new SendInterface(59500));
							break;
						case 3789:// Pest Store
						case 3788:
							player.getShopping().open(PestShop.SHOP_ID);
							break;
						case 494:// Banker
						case 902:
						case 6538:
						case 2271:
							player.getBank().openBank();
							break;
						case 6524:
							DialogueManager.sendNpcChat(player, 6524, Emotion.CALM, "I can decant your potions for 300 gp each.");
							break;
						case 4936:// Runecrafter
							player.getShopping().open(30);
							break;
						case 1635:
						case 1636:
						case 1637:
						case 1638:
						case 1639:
						case 1640:
						case 1641:
						case 1642:
						case 1643:
						case 1644:
							break;

						default:

							if (VencillioConstants.DEV_MODE) {
								player.getClient().queueOutgoingPacket(new SendMessage("Mob id: " + mob.getId()));
							}
							DialogueManager.sendNpcChat(player, id, Emotion.DEFAULT, Utility.randomElement(DEFAULT_DIALOGUES));
							break;
					}
					break;

				case 3:// NPC THIRD CLICK
					for (int i = 0; i < NPC_STORE_DATA.length; i++) {
						if (NPC_STORE_DATA[i][0] == id) {
							player.getShopping().open(NPC_STORE_DATA[i][1]);
							return;
						}
					}
					switch (id) {
						case 1103://Weapon Game
							WeaponGameStore.open(player);
							break;
						case 1306:// Make over mage
							if (!player.getInventory().hasItemAmount(new Item(995, 10000))) {
								DialogueManager.sendNpcChat(player, 1306, Emotion.ANNOYED, "You don't have 10,000 coins!");
								return;
							}
							player.getInventory().remove(new Item(995, 10000));
							player.send(new SendInterface(3559));
							break;
						case 5523:// Swagster
							if (PlayerConstants.isPlayer(player)) {
								DialogueManager.sendNpcChat(player, 5523, Emotion.DEFAULT, "You need to be a <img=4>@red@member </col>to do this!");
								return;
							}
							TaskQueue.queue(new TeleOtherTask(mob, player, PlayerConstants.MEMEBER_AREA));
							break;
						case 4936:// Mage of zamorak
							player.start(new OptionDialogue("Abyss", p -> {
								player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
								TaskQueue.queue(new TeleOtherTask(mob, player, new Location(3039, 4834)));
							}, "Essence mine", p -> {
								player.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
								TaskQueue.queue(new TeleOtherTask(mob, player, new Location(2923, 4819)));
							}));
							break;
						case 1325:// Npc guide
							DropTable.open(player);
							break;
						case 606:// Prestige
							Prestige.update(player);
							player.send(new SendInterface(51000));
							break;
						case 315:// Emblem trader
							if (player.getSkulling().isSkulled()) {
								DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You already have a wilderness skull!");
								return;
							}
							player.getSkulling().skull(player, player);
							DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You have been skulled.");
							break;
						case 6524:// potion decanting
							PotionDecanting.decantAll(player);
							break;
						case 5419:// Exchange agent
							player.getClient().queueOutgoingPacket(new SendInterface(53500));
							player.getClient().queueOutgoingPacket(new SendString("Shops Exchange | @red@0</col> Active Shops", 53505));
							List<Player> available = Arrays.stream(World.getPlayers()).filter(p -> p != null && p.isActive() && p.getPlayerShop().hasAnyItems()).collect(Collectors.toList());
							for (int i = 53516; i < 53716; i++) {
								Player p = null;
								if (i - 53516 < available.size()) {
									p = available.get(i - 53516);
									player.getClient().queueOutgoingPacket(new SendString("Shops Exchange | @gre@" + available.size() + "</col> Active " + (available.size() == 1 ? "Shop" : "Shops"), 53505));
									player.getClient().queueOutgoingPacket(new SendString(p.getUsername(), i));
									if (player.getShopMotto() != null) {
										player.getClient().queueOutgoingPacket(new SendString(p.getShopMotto(), i + 200));
									} else {
										player.getClient().queueOutgoingPacket(new SendString("No shop description set.", i + 200));
									}
								} else {
									player.getClient().queueOutgoingPacket(new SendString("", i));
									player.getClient().queueOutgoingPacket(new SendString("", i + 200));
								}
							}
							break;

						default:
							DialogueManager.sendNpcChat(player, id, Emotion.DEFAULT, Utility.randomElement(DEFAULT_DIALOGUES));
					}
					break;

				case 4:// NPC FOURTH CLICK
					switch (id) {
						case 315:// Emblem trader
							if (player.getSkulling().isSkulled()) {
								DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You already have a wilderness skull!");
								return;
							}

							player.getSkulling().skull(player, player);
							DialogueManager.sendNpcChat(player, 315, Emotion.DEFAULT, "You have been skulled.");
							break;
					}

				default:
					DialogueManager.sendNpcChat(player, id, Emotion.DEFAULT, Utility.randomElement(DEFAULT_DIALOGUES));
					break;
			}
		}
	}

	public static final void finishItemOnNpc(Player player, int item, Mob mob) {
		switch (mob.getId()) {

			case 2181://Firecape exchange
				if (!PlayerConstants.isOwner(player)) {
					player.send(new SendMessage("Coming soon!"));
					player.send(new SendRemoveInterfaces());
					return;
				}
				if (!player.getInventory().hasItemId(6570)) {
					DialogueManager.sendItem1(player, "You don't have a Firecape to do this!", 6570);
					return;
				}
				player.getInventory().remove(6570, 1);
				if (Utility.random(200) == 0) {
					PetData petDrop = PetData.forItem(13178);

					if (petDrop != null) {
						if (player.getBossPet() == null) {
							BossPets.spawnPet(player, petDrop.getItem(), true);
							player.send(new SendMessage("You feel a pressence following you; " + Utility.formatPlayerName(GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName()) + " starts to follow you."));
						} else {
							player.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
							player.send(new SendMessage("You feel a pressence added to your bank."));
						}
					} else {
						GroundItemHandler.add(new Item(13178, 1), player.getLocation(), player, player.ironPlayer() ? player : null);
					}
				} else {
					player.send(new SendMessage("@red@You have sacrificed a Fire cape... Nothing happens."));
				}
				player.send(new SendRemoveInterfaces());
				break;

			case 954://Barrows repair
				for (int[] id : Barrows.BROKEN_BARROWS) {
					if (item == id[1]) {
						if (player.getInventory().contains(new Item(995, 250_000))) {
							player.getInventory().remove(item, 1);
							player.getInventory().remove(new Item(995, 250_000));
							player.getInventory().add(new Item(id[0]));
							DialogueManager.sendNpcChat(player, 954, Emotion.DEFAULT, "Your " + GameDefinitionLoader.getItemDef(id[0]).getName() + " has been repaired.");
						} else {
							DialogueManager.sendNpcChat(player, 954, Emotion.EVIL_LAUGH_SHORT, "You need 250k to repair your barrows piece!");
						}
						break;
					}
				}
				break;

		}
	}

	public static void finishObjectClick(Player player, int id, int option, int x, int y) {
		int z = player.getLocation().getZ();

		if (player.getMagic().isTeleporting()) {
			return;
		}

		if (PlayerConstants.isOwner(player)) {
			RSObject o = Region.getObject(x, y, player.getLocation().getZ());
			if (o != null) {
				player.getClient().queueOutgoingPacket(new SendMessage("Object option: " + option + " id: " + id + " x: " + x + " y: " + y + " face: " + o.getFace() + " type: " + o.getType()));
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("clicking id with no reference: " + id));
			}
		}

		ObjectDef def = ObjectDef.getObjectDef(id);

		if (def != null && def.name != null && def.name.toLowerCase().contains("vines")) {
			RSObject o = Region.getObject(x, y, player.getLocation().getZ());
			if (o != null) {
				if (o.getFace() == 1 || o.getFace() == 3) {
					if (player.getX() > x) {
						player.teleport(new Location(player.getX() - 2, player.getY()));
					} else {
						player.teleport(new Location(player.getX() + 2, player.getY()));
					}
				} else {
					if (player.getY() > y) {
						player.teleport(new Location(player.getX(), player.getY() - 2));
					} else {
						player.teleport(new Location(player.getX(), player.getY() + 2));
					}
				}
			}
		}

		if (SmithingConstants.clickAnvil(player, id)) {
			return;
		}

		if (option == 1) {
			if (Farming.harvest(player, x, y)) {
				return;
			}

			RSObject o = Region.getObject(x, y, player.getLocation().getZ());

			if (o == null) {
				o = new RSObject(x, y, z, id, 10, 0);
			}

			if (PyramidPlunder.SINGLETON.clickObject(player, o)) {
				return;
			}
		}

		if (Farming.inspectObject(player, x, y)) {
			return;
		}

		if (Doors.isDoorJammed(player, x, y, z)) {
			return;
		}

		if ((id == 5581) && (x == 3232) && (y == 3296)) {
			player.getInventory().add(new Item(1351));
			ObjectManager.deleteWithObject(3232, 3296, player.getZ());

			TaskQueue.queue(new Task(100) {
				@Override
				public void execute() {
					ObjectManager.spawnWithObject(id, new Location(3232, 3296), 10, 0);
					stop();
				}

				@Override
				public void onStop() {
				}

			});
			return;
		}

		if (id == 1161) {
			player.getInventory().add(new Item(1965));
			ObjectManager.deleteWithObject(x, y, player.getZ());

			TaskQueue.queue(new Task(100) {
				@Override
				public void execute() {
					ObjectManager.spawnWithObject(id, new Location(x, y), 10, 0);
					stop();
				}

				@Override
				public void onStop() {
				}

			});
			return;
		}

		if (id == 312) {
			player.getInventory().add(new Item(1942));
			ObjectManager.deleteWithObject(x, y, player.getZ());

			TaskQueue.queue(new Task(100) {
				@Override
				public void execute() {
					ObjectManager.spawnWithObject(id, new Location(x, y), 10, 0);
					stop();
				}

				@Override
				public void onStop() {
				}

			});
			return;
		}

		if ((id == 1738) && (x == 2839) && (y == 3537)) {
			player.teleport(new Location(2839, 3537, 2));
			return;
		}
		if ((id == 1742) && (x == 2445) && (y == 3434)) {
			player.teleport(new Location(2445, 3433, 1));
			return;
		}
		if ((id == 1742) && (x == 2444) && (y == 3414)) {
			player.teleport(new Location(2445, 3416, 1));
			return;
		}
		if ((id == 1744) && (x == 2445) && (y == 3434)) {
			player.teleport(new Location(2446, 3436));
			return;
		}
		if ((id == 1744) && (x == 2445) && (y == 3415)) {
			player.teleport(new Location(2444, 3413));
			return;
		}

		if ((id == 15638) && (x == 2840) && (y == 3538)) {
			player.teleport(new Location(2840, 3538));
			return;
		}
		if ((id == 1738) && (x == 2853) && (y == 3535)) {
			player.teleport(new Location(2840, 3539, 2));
			return;
		}

		if (id == 11834) {
			TzharrGame.finish(player, false);
			return;
		}

		if (PestControl.clickObject(player, id)) {
			return;
		}

		if (WeaponGame.objectClick(player, id, x, y, z)) {
			return;
		}

		switch (option) {
			case 1:
				if (player.getDueling().clickForfeitTrapDoor(id)) {
					return;
				}

				if (GodWars.clickObject(player, id, x, y, z)) {
					return;
				}

				if (FightPits.clickObject(player, id)) {
					return;
				}

				if (Barrows.clickObject(player, id, x, y, z)) {
					return;
				}

				if (PestControl.clickObject(player, id)) {
					return;
				}

				if ((id >= 7) && (id <= 9)) {
					if (DwarfMultiCannon.hasCannon(player)) {
						DwarfMultiCannon.getCannon(player).pickup(player, x, y);
					}
					return;
				}

				if ((id == 5264) && (x == 3161) && (y == 4236)) {
					player.teleport(new Location(3085, 3493));
					return;
				}
				if (ObeliskTick.clickObelisk(id)) {
					return;
				}
				if (Doors.clickDoor(player, id, x, y, player.getLocation().getZ())) {
					return;
				}

				if (Agility.SINGLETON.fireObjectClick(player, player.getLocation(), Region.getObject(x, y, player.getLocation().getZ()))) {
					return;
				}

				if (AbyssObjects.clickObject(player, id)) {
					return;
				}

				if (ClanWarsFFA.clickObject(player, id)) {
					return;
				}

				if (id == 8720 || id == 26820 || id == 26813) {
					player.send(new SendString("http://www.rennatscape.proboards.com/page/voting", 12000));
					player.send(new SendMessage("Loading voting page..."));
					return;
				}

				if (ObjectDef.getObjectDef(id) != null && ObjectDef.getObjectDef(id).name != null && ObjectDef.getObjectDef(id).name.toLowerCase().contains("bank booth")) {
					player.getBank().openBank();
					return;
				}

				if (ObjectConstants.isSummoningObelisk(id)) {
					if (player.getLevels()[21] == player.getMaxLevels()[21]) {
						player.getClient().queueOutgoingPacket(new SendMessage("You already have full Summoning points."));
					} else {
						player.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
						player.getClient().queueOutgoingPacket(new SendMessage("You recharge your Summoning points at the obelisk."));
						player.getUpdateFlags().sendAnimation(8502, 0);
						player.getUpdateFlags().sendGraphic(new Graphic(1308, 0, false));
						player.getLevels()[21] = player.getMaxLevels()[21];
						player.getSkill().update(21);
					}
					return;
				}

				ThievingStallTask.attemptStealFromStall(player, id, new Location(x, y, z));
				RunecraftingTask.attemptRunecrafting(player, new GameObject(id, x, y, z, 10, 0));

				if (Mining.clickRock(player, Region.getObject(x, y, player.getLocation().getZ()))) {
					return;
				}
				WoodcuttingTask.attemptWoodcutting(player, id, x, y);
				switch (id) {

					case 22472://Tab Creation
						player.send(new SendString("Please click on a tab to create it", 26707));
						player.send(new SendString("Click on 'info' to get requirements.", 26708));
						int[] tabs = {8007, 8008, 8009, 8010, 8011, 8012, 8013, 8014, 8015};
						for (int i = 0; i < tabs.length; i++) {
							player.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(26706, tabs[i], 1, i));
						}
						player.send(new SendInterface(26700));
						break;
					case 3193://Bank chest
					case 26707:
						player.getBank().openBank();
						break;

					case 26762:// Scorpion Pit
						player.getUpdateFlags().sendFaceToDirection(x, y);
						player.getUpdateFlags().sendAnimation(new Animation(844));
						TaskQueue.queue(new Task(player, 1, false) {
							@Override
							public void execute() {
								stop();
							}

							@Override
							public void onStop() {
								if (player.getLocation().equals(new Location(3233, 3938)) || player.getLocation().equals(new Location(3232, 3938))) {
									player.teleport(new Location(3233, 10332));
								} else if (player.getLocation().equals(new Location(3233, 3950)) || player.getLocation().equals(new Location(3232, 3950))) {
									player.teleport(new Location(3232, 10351));
								} else if (player.getLocation().equals(new Location(3242, 3948)) || player.getLocation().equals(new Location(3243, 3948))) {
									player.teleport(new Location(3243, 10351));
								}
							}
						});
						break;

					case 26763:// Scorpion Pit
						player.getUpdateFlags().sendFaceToDirection(x, y);
						player.getUpdateFlags().sendAnimation(new Animation(844));
						TaskQueue.queue(new Task(player, 1, false) {
							@Override
							public void execute() {
								stop();
							}

							@Override
							public void onStop() {
								if (player.getLocation().equals(new Location(3233, 10332))) {
									player.teleport(new Location(3233, 3938));
								} else if (player.getLocation().equals(new Location(3232, 10351))) {
									player.teleport(new Location(3233, 3950));
								} else if (player.getLocation().equals(new Location(3243, 10351))) {
									player.teleport(new Location(3242, 3948));
								}
							}
						});
						break;


					case 2878://Entering god cape place
						TaskQueue.queue(new Task(player, 1) {
							@Override
							public void execute() {
								player.getUpdateFlags().sendAnimation(new Animation(7133));
								stop();
							}

							@Override
							public void onStop() {
								player.teleport(new Location(2509, 4689, 0));
							}
						});
						break;
					case 2879://Leaving god cape place
						TaskQueue.queue(new Task(player, 1) {
							@Override
							public void execute() {
								player.getUpdateFlags().sendAnimation(new Animation(7133));
								stop();
							}

							@Override
							public void onStop() {
								player.teleport(new Location(2542, 4718, 0));
							}
						});
						break;
					case 2873://Saradomin god cape
						if (ItemCheck.hasGodCape(player)) {
							DialogueManager.sendStatement(player, "It appears you already have a god cape!");
							return;
						}
						if (player.getSkill().getLevels()[Skills.MAGIC] < 60) {
							DialogueManager.sendStatement(player, "You need a Magic level of 60 to do this!");
							return;
						}
						player.getUpdateFlags().sendAnimation(new Animation(645));
						player.getInventory().add(new Item(2412));
						player.send(new SendMessage("@dre@You have obtained a Saradomin cape."));
						break;
					case 2875://Guthix god cape
						if (ItemCheck.hasGodCape(player)) {
							DialogueManager.sendStatement(player, "It appears you already have a god cape!");
							return;
						}
						if (player.getSkill().getLevels()[Skills.MAGIC] < 60) {
							DialogueManager.sendStatement(player, "You need a Magic level of 60 to do this!");
							return;
						}
						player.getUpdateFlags().sendAnimation(new Animation(645));
						player.getInventory().add(new Item(2413));
						player.send(new SendMessage("@dre@You have obtained a Guthix cape."));
						break;
					case 2874://Zamorak god cape
						if (ItemCheck.hasGodCape(player)) {
							DialogueManager.sendStatement(player, "It appears you already have a god cape!");
							return;
						}
						if (player.getSkill().getLevels()[Skills.MAGIC] < 60) {
							DialogueManager.sendStatement(player, "You need a Magic level of 60 to do this!");
							return;
						}
						player.getUpdateFlags().sendAnimation(new Animation(645));
						player.getInventory().add(new Item(2414));
						player.send(new SendMessage("@dre@You have obtained a Zamorak cape."));
						break;
					case 13618:// Wyverns teleport
						player.start(new OptionDialogue("Teleport to Wyverns", p -> {
							player.teleport(new Location(3056, 9555, 0));
							player.send(new SendMessage("You have been teleported to the Skeletal Wyverns."));
						}, "Nevermind", p -> {
							player.send(new SendRemoveInterfaces());
						}));
						break;
					case 13619://Fountain of Rune
						player.start(new OptionDialogue("Teleport (30+ Wild)", p -> {
							player.getMagic().teleport(3372, 3894, 0, MagicSkill.TeleportTypes.FOUNTAIN_OF_RUNE);
							player.send(new SendMessage("You have been teleported to the Fountain of Rune."));
						}, "Nevermind", p -> {
							player.send(new SendRemoveInterfaces());
						}));
						break;


					case 10596:// Wyverns teleport
						player.teleport(new Location(3056, 9555, 0));
						break;
					case 12941:// Membership Fountain

						break;
					case 26760:// Wilderness resource arena
						if (player.getLocation().getX() == 3184 && player.getLocation().getY() == 3945) {
							if (player.isPouchPayment()) {
								if (player.getMoneyPouch() < 7500) {
									DialogueManager.sendStatement(player, "You need 7,500 coins to enter the arena.");
									return;
								}
							} else {
								if (!player.getInventory().hasItemAmount(995, 7500)) {
									DialogueManager.sendStatement(player, "You need 7,500 coins to enter the arena.");
									return;
								}
							}
							if (player.isPouchPayment()) {
								player.setMoneyPouch(player.getMoneyPouch() - 7500);
								player.send(new SendString(player.getMoneyPouch() + "", 8135));
							} else {
								player.getInventory().remove(995, 7500);
							}
							player.send(new SendMessage("You have paid 7,500 coins and entered the resource arena."));
							TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y - 1, z)));
						} else {
							player.getAttributes().set("stopMovement", Boolean.valueOf(true));
							TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(x, y + 1, z)));
						}
						break;
					case 10230:// Dagganoth Kings
						player.teleport(new Location(2900, 4449, 0));
						break;
					case 9472:// Exchange
					case 9741:
					case 9371:
						player.start(new ShopExchangeDialogue(player));
						break;
					case 24306:// Warrior Guild door
					case 24309:
						CyclopsRoom.handleDoor(player, x, y);
						break;
					case 16671: // Warrior guild climb up
						player.teleport(new Location(2841, 3538, 2));
						player.send(new SendMessage("You climb up the stairs to the next floor."));
						break;
					case 24303: // Warrior guild climb down
						player.teleport(new Location(2841, 3538, 0));
						player.send(new SendMessage("You climb down the stairs to the previous floor."));
						break;
					case 7236: // Wall safes
						WallSafes.crack(player);
						break;
					case 2794:
						FireColor.open(player);// fire color
						break;
					case 18987:// KBD ladder
						player.send(new SendMessage("You have climbed down the ladder."));
						player.teleport(new Location(2271, 4680));
						break;
					case 677:// Corporeal cave
						if (player.getX() == 2974) {
							player.teleport(new Location(player.getX() - 3, player.getY(), player.getZ()));
						} else {
							player.teleport(new Location(player.getX() + 3, player.getY(), player.getZ()));
						}
						break;
					case 11183:
					case 11185:
					case 11184:
						if (player.getInventory().hasSpaceFor(new Item(444))) {
							player.getInventory().add(new Item(444));
							player.getClient().queueOutgoingPacket(new SendMessage("You get some gold ore."));
						}
						break;
					case 11188:
					case 11186:
					case 11187:
						if (player.getInventory().hasSpaceFor(new Item(442))) {
							player.getInventory().add(new Item(442));
							player.getClient().queueOutgoingPacket(new SendMessage("You get some silver ore."));
						}
						break;
					case 11191:
					case 11189:
					case 11190:
						if (player.getInventory().hasSpaceFor(new Item(434))) {
							player.getInventory().add(new Item(434));
							player.getClient().queueOutgoingPacket(new SendMessage("You get some clay."));
						}
						break;
					case 2073:
						if (player.getInventory().hasSpaceFor(new Item(1963))) {
							player.getInventory().add(new Item(1963));
							player.getClient().queueOutgoingPacket(new SendMessage("You pick the banana from the tree."));
						}
						break;
					case 2557:
					case 2558:
						if (player.getX() == 3038 && player.getY() == 3956 || player.getX() == 3044 && player.getY() == 3956 || player.getX() == 3041 && player.getY() == 3959 || player.getX() == 3190 && player.getY() == 3957 || player.getX() == 3191 && player.getY() == 3963) {
							player.getUpdateFlags().sendAnimation(new Animation(2246));
							Task task = new Lockpick(player, (byte) 2, id, x, y, z);
							player.getAttributes().set("lockPick", task);
							TaskQueue.queue(task);
						} else {
							player.getClient().queueOutgoingPacket(new SendMessage("This door is locked.."));
						}
						break;
					case 2147:
						if (x == 2324 && y == 3809) {
							player.teleport(new Location(3577, 9927));
						}
						if (x == 2317 && y == 3807) {
							player.teleport(new Location(2884, 9798));
						}
						if (x == 2538 && y == 3895) {
							player.teleport(new Location(1764, 5365, 1));
						}
						break;
					case 1568:
						if (x == 3097 && y == 3468) {
							player.teleport(new Location(3096, 9867));
						}
						break;
					case 29370:
						if (x == 3150 && y == 9906) {
							player.teleport(new Location(3155, 9906));
						}
						if (x == 3153 && y == 9906) {
							player.teleport(new Location(3149, 9906));
						}
						break;
					case 1755:
						if (x == 2884 && y == 9797) {
							player.teleport(new Location(2318, 3807));
						}
						if (x == 3008 && y == 9550) {
							player.teleport(new Location(2364, 3893));
						}
						if (x == 3097 && y == 9867) {
							player.teleport(new Location(3096, 3468));
						}
						break;
					case 1757:
						if (x == 3578 && y == 9927) {
							player.teleport(new Location(2324, 3808));
						}
						break;
					case 23555:
						if (player.getSkill().locked()) {
							return;
						}
						player.getSkill().lock(4);

						if (player.getLocation().getY() < 3917) {
							TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(2998, 3931, z)) {
								DoubleDoor normalDoor = Region.getDoubleDoor(2998, 3931, 0);
								boolean stopBalance = false;
								WalkInteraction balance;

								@Override
								public void execute() {
									if (stage == 0) {
										p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
										door.append();
										ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
										stage++;
									} else if (stage == 1) {
										p.getMovementHandler().walkTo(xMod, yMod);
										stage++;
									} else if (stage == 2) {
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
										door.append();
										ObjectManager.send(new GameObject(door.getCurrentId(), door.getX(), door.getY(), door.getZ(), door.getType(), door.getCurrentFace()));
										stage++;
									} else if (stage == 3) {
										stage++;
									} else if (stage == 4 && balance == null) {
										balance = new WalkInteraction() {
											@Override
											public int getAnimation() {
												return 762;
											}

											@Override
											public String getPreMessage() {
												return null;
											}

											@Override
											public String getPostMessage() {
												return null;
											}

											@Override
											public void onCancellation(Player player) {
												WalkInteraction.super.onCancellation(player);
												stopBalance = true;
											}
										};

										balance.execute(player, null, player.getLocation(), new Location(2998, 3930), 0, 0, -1);
									} else if (stage == 5) {
										p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX1(), normalDoor.getY1(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace1()));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX2(), normalDoor.getY2(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace2()));
										normalDoor.append();
										ObjectManager.send(new GameObject(normalDoor.getCurrentId1(), normalDoor.getX1(), normalDoor.getY1(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace1()));
										ObjectManager.send(new GameObject(normalDoor.getCurrentId2(), normalDoor.getX2(), normalDoor.getY2(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace2()));
										stage++;
									} else if (stage == 6) {
										p.getMovementHandler().walkTo(0, 1);
										stage++;
									} else if (stage == 7) {
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX1(), normalDoor.getY1(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace1()));
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX2(), normalDoor.getY2(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace2()));
										normalDoor.append();
										ObjectManager.send(new GameObject(normalDoor.getCurrentId1(), normalDoor.getX1(), normalDoor.getY1(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace1()));
										ObjectManager.send(new GameObject(normalDoor.getCurrentId2(), normalDoor.getX2(), normalDoor.getY2(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace2()));
										stop();
										return;
									}

									if (stopBalance) {
										stage++;
										stopBalance = false;
									}
								}
							});
						}
						break;
					case 23552:
					case 23554:
						if (player.getSkill().locked()) {
							return;
						}

						player.getSkill().lock(4);

						if (player.getLocation().getY() >= 3931) {
							TaskQueue.queue(new WalkThroughDoubleDoorTask(player, x, y, z, new Location(2998, 3931, z)) {
								Door normalDoor = Region.getDoor(2998, 3917, 0);
								boolean stopBalance = false;
								WalkInteraction balance;

								@Override
								public void execute() {
									if (stage == 0) {
										p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
										door.append();
										ObjectManager.send(new GameObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
										ObjectManager.send(new GameObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
										stage++;
									} else if (stage == 1) {
										p.getMovementHandler().walkTo(xMod, yMod);
										stage++;
									} else if (stage == 2) {
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
										door.append();
										ObjectManager.send(new GameObject(door.getCurrentId1(), door.getX1(), door.getY1(), door.getZ(), door.getType(), door.getCurrentFace1()));
										ObjectManager.send(new GameObject(door.getCurrentId2(), door.getX2(), door.getY2(), door.getZ(), door.getType(), door.getCurrentFace2()));
										stage++;
									} else if (stage == 3) {
										stage++;
									} else if (stage == 4 && balance == null) {
										balance = new WalkInteraction() {
											@Override
											public int getAnimation() {
												return 762;
											}

											@Override
											public String getPreMessage() {
												return null;
											}

											@Override
											public String getPostMessage() {
												return null;
											}

											@Override
											public void onCancellation(Player player) {
												WalkInteraction.super.onCancellation(player);
												stopBalance = true;
											}
										};

										balance.execute(player, null, player.getLocation(), new Location(2998, 3916), 0, 0, -1);
									} else if (stage == 5) {
										p.getClient().queueOutgoingPacket(new SendSound(326, 0, 0));
										ObjectManager.remove2(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX(), normalDoor.getY(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace()));
										normalDoor.append();
										ObjectManager.send(new GameObject(normalDoor.getCurrentId(), normalDoor.getX(), normalDoor.getY(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace()));
										stage++;
									} else if (stage == 6) {
										p.getMovementHandler().walkTo(0, -1);
										stage++;
									} else if (stage == 7) {
										ObjectManager.send(new GameObject(ObjectManager.BLANK_OBJECT_ID, normalDoor.getX(), normalDoor.getY(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace()));
										normalDoor.append();
										ObjectManager.send(new GameObject(normalDoor.getCurrentId(), normalDoor.getX(), normalDoor.getY(), normalDoor.getZ(), normalDoor.getType(), normalDoor.getCurrentFace()));
										stop();
										return;
									}

									if (stopBalance) {
										stage++;
										stopBalance = false;
									}
								}
							});
						}
						break;
					case 26933:
						player.teleport(new Location(3096, 9867));
						break;
					case 5084:
						player.teleport(new Location(2401, 3888));
						break;
					case 21584:
						player.teleport(new Location(2712, 9564));
						break;
					case 10595:
						player.teleport(new Location(3056, 9562));
						break;
					case 1733:
						player.teleport(new Location(player.getLocation().getX(), 10322, 0));
						break;
					case 1734:
						player.teleport(new Location(player.getLocation().getX(), 3927, 0));
						break;
					case 25338:// Ancient Cavern down
						player.teleport(new Location(1772, 5366, 0));
						break;
					case 25336:// Ancient Cavern Up
						player.teleport(new Location(1768, 5366, 1));
						break;
					case 21311:
						player.teleport(new Location(2314, 3839));
						break;
					case 21310:
						player.teleport(new Location(2314, 3848));
						break;
					case 21585:
						player.teleport(new Location(2882, 5310, 2));
						break;
					case 21586:
						player.teleport(new Location(3007, 9550));
						break;
					case 5083:
						player.teleport(new Location(2712, 9564));
						break;
					case 10041:
						DialogueManager.sendNpcChat(player, 496, Emotion.ANGRY_1, "Woah Woah! Watch out!");
						break;
					case 21309:
						player.getMovementHandler().addToPath(new Location(2343, 3820));
						player.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
						break;
					case 21308:
						player.getMovementHandler().addToPath(new Location(2343, 3829));
						player.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
						break;
					case 21306:
						player.getMovementHandler().addToPath(new Location(2317, 3832));
						player.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
						break;
					case 21307:
						player.getMovementHandler().addToPath(new Location(2317, 3823));
						player.getClient().queueOutgoingPacket(new SendMessage("You run across the bridge."));
						break;
					case 272:
						player.getUpdateFlags().sendAnimation(new Animation(828));
						player.teleport(new Location(3018, 3956, 1));
						break;
					case 273:
						player.getUpdateFlags().sendAnimation(new Animation(828));
						player.teleport(new Location(3018, 3958, 0));
						break;

					case 245:
						if (x == 3017 && y == 3959) {
							player.teleport(new Location(3017, 3960, 2));
						} else if (x == 3019 && y == 3959) {
							player.teleport(new Location(3019, 3960, 2));
						}
						break;

					case 246:
						player.teleport(new Location(player.getX(), player.getY() - 2, 1));
						break;
					case 9358:
						player.teleport(new Location(2480, 5175));
						break;
					case 492:
						player.teleport(new Location(2856, 9570));
						break;

					case 25339:
						player.teleport(new Location(1778, 5343, 1));
						break;

					case 25340:// from mith drag
						player.teleport(new Location(1778, 5346, 0));
						break;

					case 37928:// from corp
						player.teleport(new Location(3404, 3090));
						break;

					case 10229:// from dag kings to dag cave
						player.teleport(new Location(2488, 10151));
						break;

					case 8929:// to dags
						player.teleport(new Location(2442, 10146));
						break;

					case 8966:// from dags to waterbirth
						player.teleport(new Location(2523, 3739));
						break;

					case 41077:// from tormented demons
						player.teleport(new Location(3106, 3955));
						break;

					case 3832:// kq hive to lair
						player.teleport(new Location(3509, 9499, 2));
						break;
					case 3829:// kq lair to desert
						player.teleport(new Location(3228, 3110));
						break;

					case 412://Chaos altar
						player.start(new OptionDialogue("Normal Magiks", p -> {
							player.getMagic().setSpellBookType(SpellBookTypes.MODERN);
							player.getMagic().setMagicBook(1151);
							player.getUpdateFlags().sendFaceToDirection(x, y);
							player.getUpdateFlags().sendAnimation(new Animation(645));
							player.send(new SendMessage("You have converted to normal magiks."));
							player.send(new SendRemoveInterfaces());
						}, "Ancient Magiks", p -> {
							player.getMagic().setSpellBookType(SpellBookTypes.ANCIENT);
							player.getMagic().setMagicBook(12855);
							player.getUpdateFlags().sendAnimation(new Animation(645));
							player.send(new SendMessage("You have converted to ancient magiks."));
							player.send(new SendRemoveInterfaces());
						}, "Lunar Magiks", p -> {
							player.getMagic().setSpellBookType(SpellBookTypes.LUNAR);
							player.getMagic().setMagicBook(29999);
							player.getUpdateFlags().sendAnimation(new Animation(645));
							player.send(new SendMessage("You have converted to lunar magiks."));
							player.send(new SendRemoveInterfaces());
						}));
						break;

					case 6552:// ancient altar
						player.getMagic().setSpellBookType(player.getMagic().getSpellBookType() == SpellBookTypes.MODERN ? SpellBookTypes.ANCIENT : SpellBookTypes.MODERN);
						player.getMagic().setMagicBook(player.getMagic().getSpellBookType() == SpellBookTypes.MODERN ? 1151 : 12855);
						player.getUpdateFlags().sendAnimation(new Animation(645));
						break;
					case 410:// lunar altar
						player.getMagic().setSpellBookType(player.getMagic().getSpellBookType() == SpellBookTypes.MODERN ? SpellBookTypes.LUNAR : SpellBookTypes.MODERN);
						player.getMagic().setMagicBook(player.getMagic().getSpellBookType() == SpellBookTypes.MODERN ? 1151 : 29999);
						player.getUpdateFlags().sendAnimation(new Animation(645));
						break;
					case 409:
					case 10638:
					case 19145:
						// regular altar
						if (player.getSkill().getLevels()[Skills.PRAYER] < player.getMaxLevels()[Skills.PRAYER]) {
							player.getSkill().setLevel(Skills.PRAYER, player.getMaxLevels()[Skills.PRAYER]);
							player.getClient().queueOutgoingPacket(new SendMessage("You recharge your prayer points."));
							player.getUpdateFlags().sendAnimation(new Animation(645));
						} else {
							player.getClient().queueOutgoingPacket(new SendMessage("Your prayer is already full."));
						}
						break;
					case 18772://Mystery box
						MysteryBoxMinigame.open(player);
						break;


					case 2113:// to fally mine
						player.teleport(new Location(3021, 9739));
						break;
					case 30941:// from fally mine
						player.teleport(new Location(3019, 3337));
						break;

					case 7257:// to rogue den
						player.teleport(new Location(3061, 4985, 1));
						break;
					case 7258:// from rogue den
						player.teleport(new Location(2906, 3537));
						break;

					case 5008:// to rellekka slayer dungeon
						player.teleport(new Location(3206, 9379, 0));
						break;
					case 4499:// to rellekka slayer dungeon alt
						player.teleport(new Location(3206, 9379, 0));
						player.getClient().queueOutgoingPacket(new SendMessage("This is an alternative entrance, use the rope to find the shorter cave entrance."));
						break;
					case 6439:
						player.teleport(new Location(2730, 3713, 0));
						break;
					case 1765:// ladder down
						if (x == 3017 && y == 3849) {
							player.teleport(new Location(3069, 10255));
						}
						break;
					case 1766:// ladder up
						if (x == 3069 && y == 10256) {
							player.teleport(new Location(3017, 3850));
						}
						break;
					case 1817:// lever teleport
						player.teleport(new Location(3017, 3848));
						break;
					case 36687:
						player.teleport(new Location(3210, 9616, 0));
						break;
					case 29355:
						player.teleport(new Location(3210, 3216, 0));
						break;
					case 2492:
						player.getMagic().teleport(2809, 3436, 0, MagicSkill.TeleportTypes.SPELL_BOOK);
						break;
					case 2191:
						if (player.getInventory().contains(new Item(989))) {
							CrystalChest.searchChest(player, x, y);
						} else {
							player.send(new SendMessage("You need a key to open this chest."));
						}
						break;
					case 9319:
						player.changeZ(1);
						break;
					case 9320:
						player.changeZ(0);
						break;
					case 11601:// Jewlery Making
						JewelryCreationTask.sendInterface(player);
						break;
					case 2806:
						player.teleport(new Location(2885, 4372));
						break;
					case 37929:
						if (player.getLocation().getX() < 2926) {
							if (player.getLocation().getX() <= 2918)
								player.teleport(new Location(2921, 4384));
							else {
								player.teleport(new Location(2917, 4384));
							}
						} else if (player.getLocation().getX() <= 2971)
							player.teleport(new Location(2974, 3484));
						else {
							player.teleport(new Location(2970, 4384));
						}

						break;
					case 1293:
						player.start(new SpiritTree(player));
						break;
					case 2640:
					case 4859:
					case 27661:
						if (player.getLevels()[5] == player.getMaxLevels()[5]) {
							player.getClient().queueOutgoingPacket(new SendMessage("You already have full Prayer."));
						} else {
							player.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
							player.getClient().queueOutgoingPacket(new SendMessage("You recharge your Prayer points at the altar."));
							player.getUpdateFlags().sendAnimation(645, 5);
							player.getLevels()[5] = player.getMaxLevels()[5];
							player.getSkill().update(5);
						}
						break;
					case 23271:
						TaskQueue.queue(new HopDitchTask(player));
						if (TargetSystem.getInstance().playerHasTarget(player)) {
							TargetSystem.getInstance().resetTarget(player, false);
						}
						player.send(new SendString("---", 25351));
						player.send(new SendString("---", 25353));
						player.send(new SendString("---", 25355));
						break;
					case 11833:// Fightcaves
						TzharrGame.init(player, false);
						break;
					case 2114: // Slayer Tower stairs
						player.teleport(new Location(3433, 3537, 1));
						break;
					case 2118:
						player.teleport(new Location(3438, 3538, 0));
						break;
					case 2119:
						player.teleport(new Location(3417, 3541, 2));
						break;
					case 2120:
						player.teleport(new Location(3412, 3541, 1));
						break;
					case 2102:
					case 2104:
						if (player.getSkill().locked()) {
							return;
						}
						player.getSkill().lock(4);
						if (player.getLocation().getY() >= 3556) {
							TaskQueue.queue(new WalkThroughDoubleDoorTask(player, x, y, z, new Location(x, y - 1, z)));
						} else {
							TaskQueue.queue(new WalkThroughDoubleDoorTask(player, x, y, z, new Location(x, y + 1, z)));
						}

						break;
					case 2100:
						if (player.getSkill().locked()) {
							return;
						}
						player.getSkill().lock(4);
						if (player.getLocation().getY() <= 3554) {
							TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(3445, 3555, z)));
						} else {
							TaskQueue.queue(new WalkThroughDoorTask(player, x, y, z, new Location(3445, 3554, z)));
						}
						break;
					case 1596:
					case 1597:
						if (player.getLocation().getY() <= 9917)
							player.teleport(new Location(3131, 9918, 0));
						else {
							player.teleport(new Location(3131, 9917, 0));
						}
						break;
					case 1557:
					case 1558:
						if (player.getLocation().getY() == 9944)
							player.teleport(new Location(3105, 9945, 0));
						else if (player.getLocation().getY() == 9945)
							player.teleport(new Location(3105, 9944, 0));
						else if (player.getLocation().getX() == 3146)
							player.teleport(new Location(3145, 9871, 0));
						else if (player.getLocation().getX() == 3145) {
							player.teleport(new Location(3146, 9871, 0));
						}
						break;
					case 2623:
						if (player.getLocation().getX() >= 2924)
							player.teleport(new Location(2923, 9803, 0));
						else {
							player.teleport(new Location(2924, 9803, 0));
						}
						break;
					case 8960:
						if (player.getLocation().getX() <= 2490)
							player.teleport(new Location(2491, 10131, 0));
						else {
							player.teleport(new Location(2490, 10131, 0));
						}
						break;
					case 8959:
						if (player.getLocation().getX() <= 2490)
							player.teleport(new Location(2491, 10146, 0));
						else {
							player.teleport(new Location(2490, 10146, 0));
						}
						break;
					case 8958:
						if (player.getLocation().getX() <= 2490)
							player.teleport(new Location(2491, 10163, 0));
						else {
							player.teleport(new Location(2490, 10163, 0));
						}
						break;
					case 5103:
						if (player.getLocation().getX() >= 2691)
							player.teleport(new Location(2689, 9564, 0));
						else {
							player.teleport(new Location(2691, 9564, 0));
						}
						break;
					case 5104:
						if (player.getLocation().getY() <= 9568)
							player.teleport(new Location(2683, 9570, 0));
						else {
							player.teleport(new Location(2683, 9568, 0));
						}
						break;
					case 5110:
						player.teleport(new Location(2647, 9557, 0));
						break;
					case 5111:
						player.teleport(new Location(2649, 9562, 0));
						break;
					case 5106:
						if (player.getLocation().getX() <= 2674)
							player.teleport(new Location(2676, 9479, 0));
						else {
							player.teleport(new Location(2674, 9479, 0));
						}
						break;
					case 5107:
						if (player.getLocation().getX() <= 2693)
							player.teleport(new Location(2695, 9482, 0));
						else {
							player.teleport(new Location(2693, 9482, 0));
						}
						break;
					case 5105:
						if (player.getLocation().getX() <= 2672)
							player.teleport(new Location(2674, 9499, 0));
						else {
							player.teleport(new Location(2672, 9499, 0));
						}
						break;
					case 5088:
						player.teleport(new Location(2687, 9506, 0));
						break;
					case 5090:
						player.teleport(new Location(2682, 9506, 0));
						break;
					case 5097:
						player.teleport(new Location(2636, 9510, 2));
						break;
					case 5098:
						player.teleport(new Location(2636, 9517, 0));
						break;
					case 5096:
						player.teleport(new Location(2649, 9591, 0));
						break;
					case 5094:
						player.teleport(new Location(2643, 9594, 2));
						break;
					case 4309:// Crafting
						for (Item i : player.getInventory().getItems()) {
							if (i != null && Spinnable.forId(i.getId()) != null) {
								Spinnable spinnable = Spinnable.forId(i.getId());
								player.getAttributes().set("craftingType", CraftingType.WHEEL_SPINNING);
								player.getAttributes().set("spinnable", spinnable);
								player.getClient().queueOutgoingPacket(new SendString("\\n \\n \\n \\n" + spinnable.getOutcome().getDefinition().getName(), 2799));
								player.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
								player.getClient().queueOutgoingPacket(new SendItemOnInterface(1746, 170, spinnable.getOutcome().getId()));
								return;
							}
						}
						player.getClient().queueOutgoingPacket(new SendMessage("You do not have anything to spin!"));
						break;
					default:

						break;
				}

				break;
			case 2:
				if (id == 12309) {
					player.getShopping().open(10);
					return;
				}

				Location location = new Location(x, y, z);
				ThievingStallTask.attemptStealFromStall(player, id, location);
				HomeStalls.attempt(player, id, location);
				if (DwarfMultiCannon.hasCannon(player)) {
					DwarfMultiCannon.getCannon(player).pickup(player, x, y);
					return;
				}

				if (ObjectDef.getObjectDef(id) != null && ObjectDef.getObjectDef(id).name != null && ObjectDef.getObjectDef(id).name.toLowerCase().contains("bank booth")) {
					player.getBank().openBank();
					return;
				}

				switch (id) {
					case 8720:// Vote boot
					case 26820:
					case 26813:
						player.getShopping().open(92);
						break;
					case 7134:// Flax
						Flax.pickFlax(player, x, y);
						break;
					case 9472:// Exchange
					case 9741:
					case 9371:
						player.start(new ShopExchangeDialogue2(player));
						break;
					case 2030:
					case 21303:
					case 24009:
					case 26814:
						SmithingConstants.sendSmeltSelectionInterface(player);
						break;
					case 2557:
					case 2558:
						if (player.getSkill().getLevels()[17] < 52) {
							player.getClient().queueOutgoingPacket(new SendMessage("You need a thieving level of 52 to pick the lock on this door."));
							return;
						}
						if (!player.getInventory().hasItemId(1523)) {
							DialogueManager.sendStatement(player, "You need a lockpick to be able to open this door.");
							return;
						}
						player.getClient().queueOutgoingPacket(new SendMessage("You attempt to pick the lock..."));
						Task task = new Lockpick(player, (byte) 2, id, x, y, z);
						player.getAttributes().set("lockPick", task);
						TaskQueue.queue(task);
						break;

					case 2732: // fires
					case 11404:
					case 11405:
					case 11406:
						FireColor.open(player);
						break;
					// end newest

					case 4309:
						for (Item i : player.getInventory().getItems()) {
							if (i != null && Spinnable.forId(i.getId()) != null) {
								Spinnable spinnable = Spinnable.forId(i.getId());
								player.getAttributes().set("craftingType", CraftingType.WHEEL_SPINNING);
								player.getAttributes().set("spinnable", spinnable);
								player.getClient().queueOutgoingPacket(new SendString("\\n \\n \\n \\n" + spinnable.getOutcome().getDefinition().getName(), 2799));
								player.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
								player.getClient().queueOutgoingPacket(new SendItemOnInterface(1746, 170, spinnable.getOutcome().getId()));
								return;
							}
						}

						player.getClient().queueOutgoingPacket(new SendMessage("You do not have anything to spin!"));
						break;

					case 3044:
					case 11666:
					case 45310:
						SmithingConstants.sendSmeltSelectionInterface(player);
						break;
					case 2646:
						TaskQueue.queue(new HarvestTask(player, id, 1779, x, y, z));
						break;
					case 1293:
						player.teleport(new Location(2461, 3434, 0));
						break;

					default:

						break;
				}

				break;
			case 3:
				switch (id) {
					case 8720:// Vote booth
					case 26820:
					case 26813:
						DialogueManager.sendStatement(player, "You have @blu@" + Utility.format(player.getVotePoints()) + " </col>voting points.");
						break;
					case 9371:
					case 9472:// Exchange
					case 9741:
						player.getClient().queueOutgoingPacket(new SendInterface(53500));
						player.getClient().queueOutgoingPacket(new SendString("Shops Exchange | @red@0</col> Active Shops", 53505));
						List<Player> available = Arrays.stream(World.getPlayers()).filter(p -> p != null && p.isActive() && p.getPlayerShop().hasAnyItems()).collect(Collectors.toList());
						for (int i = 53516; i < 53716; i++) {
							Player p = null;
							if (i - 53516 < available.size()) {
								p = available.get(i - 53516);
								player.getClient().queueOutgoingPacket(new SendString("Shops Exchange | @gre@" + available.size() + "</col> Active " + (available.size() == 1 ? "Shop" : "Shops"), 53505));

								String color = "";
								if (p.getShopColor() == null) {
									color = "</col>" + p.determineIcon(p);
								} else {
									color = p.getShopColor();
								}

								player.getClient().queueOutgoingPacket(new SendString(p.determineIcon(p) + p.getUsername(), i));

								if (player.getShopMotto() != null) {
									player.getClient().queueOutgoingPacket(new SendString(color + p.getShopMotto(), i + 200));
								} else {
									player.getClient().queueOutgoingPacket(new SendString(color + "No shop description set.", i + 200));
								}
							} else {
								player.getClient().queueOutgoingPacket(new SendString("", i));
								player.getClient().queueOutgoingPacket(new SendString("", i + 200));
							}
						}
						break;

					default:
						return;
				}
				break;
			case 4:
				switch (id) {

				}
				break;
		}
	}

	public static void itemOnObject(final Player player, final int itemId, final int objectId, final int x, final int y) {
		if (VencillioConstants.DEV_MODE) {
			player.getClient().queueOutgoingPacket(new SendMessage("Using item " + itemId + " on object " + objectId));
		}

		if (player.getMagic().isTeleporting()) {
			return;
		}

		RSObject object = Region.getObject(x, y, player.getLocation().getZ());

		int z = player.getLocation().getZ();

		if ((object == null) && (!PlayerConstants.isOverrideObjectExistance(player, objectId, x, y, z))) {
			return;
		}

		final int[] length = ObjectConstants.getObjectLength(objectId, object != null ? object.getFace() : 0);

		TaskQueue.queue(new WalkToTask(player, x, y, length[0], length[1]) {
			@Override
			public void onDestination() {
				player.getUpdateFlags().sendFaceToDirection(length[0] >= 2 ? x + length[0] / 2 : x, length[1] >= 2 ? y + length[1] / 2 : y);

				if (objectId == 884 && itemId == 5331) {
					int slot = player.getInventory().getItemSlot(itemId);
					player.getInventory().get(slot).setId(5340);
					player.send(new SendUpdateItemsAlt(3214, 5340, 1, slot));
					player.send(new SendMessage("You fill your Watering can."));
					return;
				}

				if (FountainOfRune.itemOnObject(player, objectId, itemId)) {
					return;
				}

				if (SmithingConstants.useBarOnAnvil(player, objectId, itemId)) {
					return;
				}

				if (Farming.prepareCrop(player, itemId, objectId, x, y)) {
					return;
				}

				if (GodWars.useItemOnObject(player, itemId, objectId)) {
					return;
				}

				if (objectId == 3044 || objectId == 45310) {
					SmithingConstants.sendSmeltSelectionInterface(player);
					return;
				}

				if (BoneBurying.useBonesOnAltar(player, itemId, objectId)) {
					return;
				}

				if (objectId == 11744) {

					ItemDefinition def = GameDefinitionLoader.getItemDef(itemId);

					if (def == null || !player.isUltimateIron()) {
						return;
					}

					if (!def.isTradable()) {
						player.send(new SendMessage(def.getName() + " cannot be noted!"));
						return;
					}

					if (def.getNoteId() == -1) {
						player.send(new SendMessage(def.getName() + " cannot be " + (def.isNote() ? "un-noted" : "noted") + "."));
						return;
					}

					int space = player.getInventory().getFreeSlots();

					int amount = player.getInventory().getItemAmount(def.getId());

					if (def.isNote()) {

						if (space == 0) {
							player.send(new SendMessage("You have no free inventory spaces to do this!"));
							return;
						}

						if (amount > space) {
							amount = space;
							player.send(new SendMessage("You can't un-note that many items!"));
						}
						player.getInventory().remove(def.getId(), amount);
						player.getInventory().add(def.getNoteId(), amount);
						player.send(new SendMessage("You have un-noted " + amount + " of " + def.getName() + "."));
						return;
					}

					player.getInventory().remove(def.getId(), amount);
					player.getInventory().add(def.getNoteId(), amount);
					player.send(new SendMessage("You have noted " + amount + " of " + def.getName() + "."));
					return;

				}

				if (objectId == 48802 && itemId == 954) {
					player.teleport(new Location(3484, 9510, 2));
					return;
				}

				if (objectId == 48803 && itemId == 954) {
					player.teleport(new Location(3507, 9494));
					return;
				}

				if (objectId == 3044 || objectId == 45310) {
					SmithingConstants.sendSmeltSelectionInterface(player);
					return;
				}

				if (objectId == 4309) {
					if (Spinnable.forId(itemId) != null) {
						Spinnable spinnable = Spinnable.forId(itemId);
						player.getAttributes().set("craftingType", CraftingType.WHEEL_SPINNING);
						player.getAttributes().set("spinnable", spinnable);
						player.getClient().queueOutgoingPacket(new SendString("\\n \\n \\n \\n" + spinnable.getOutcome().getDefinition().getName(), 2799));
						player.getClient().queueOutgoingPacket(new SendChatBoxInterface(4429));
						player.getClient().queueOutgoingPacket(new SendItemOnInterface(1746, 170, spinnable.getOutcome().getId()));
					} else {
						player.getClient().queueOutgoingPacket(new SendMessage("You cant spin this!"));
					}

					return;
				}

				GameObject object = new GameObject(objectId, x, y, player.getLocation().getZ(), 10, 0);

				if (CookingTask.isCookableObject(object)) {
					CookingTask.showInterface(player, object, new Item(itemId, 1));
				}

				ArmourAnimator.armorOnAnimator(player, itemId, object, x, y);

				if ((DwarfMultiCannon.hasCannon(player)) && (!DwarfMultiCannon.getCannon(player).construct(itemId)))
					DwarfMultiCannon.getCannon(player).load(player, itemId, objectId);
			}
		});
	}

	public static void useItemOnNpc(final Player player, final int item, int slot) {
		if (player.getMagic().isTeleporting()) {
			return;
		}

		if ((slot > World.getNpcs().length) || (slot < 0)) {
			return;
		}

		final Mob mob = World.getNpcs()[slot];

		if (mob == null) {
			player.getMovementHandler().reset();
			return;
		}

		TaskQueue.queue(new FollowToEntityTask(player, mob) {
			@Override
			public void onDestination() {
				if (mob.face()) {
					mob.face(player);
				}

				player.face(mob);

				if (mob.getSize() > 1) {
					player.getMovementHandler().reset();
				}

				WalkToActions.finishItemOnNpc(player, item, mob);
			}
		});
	}
}
