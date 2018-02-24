package com.vencillio.rs2.content.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.NameUtil;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.PlayerProperties;
import com.vencillio.rs2.content.PlayerTitle;
import com.vencillio.rs2.content.achievements.AchievementList;
import com.vencillio.rs2.content.bank.Bank;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.membership.CreditPurchase;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSeas;
import com.vencillio.rs2.content.skill.magic.weapons.TridentOfTheSwamp;
import com.vencillio.rs2.content.skill.melee.SerpentineHelmet;
import com.vencillio.rs2.content.skill.ranged.ToxicBlowpipe;
import com.vencillio.rs2.content.skill.slayer.Slayer;
import com.vencillio.rs2.entity.Entity.AttackType;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Equipment;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.item.ItemDegrading;
import com.vencillio.rs2.entity.player.Player;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class PlayerSave {

	public static final class PlayerFarming {

		public static boolean loadDetails(Player player) throws Exception {
			File file = new File("./data/characters/farming/" + player.getUsername() + ".json");

			if (!file.exists()) {
				return false;
			}

			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				PlayerFarming details = PlayerSave.GSON.fromJson(reader, PlayerFarming.class);

				long millis = System.currentTimeMillis();

				if (details.lastFarmingTimer > 0) {
					player.getFarming().setFarmingTimer(details.farmingTimer + TimeUnit.MILLISECONDS.toMinutes(millis - details.lastFarmingTimer));
				} else {
					player.getFarming().setFarmingTimer(details.farmingTimer);
				}

				player.getFarming().getCompost().compostBins = details.compostBins;
				player.getFarming().getCompost().compostBinsTimer = details.compostBinsTimer;
				player.getFarming().getCompost().organicItemAdded = details.organicItemAdded;
				player.getFarming().getBushes().bushesStages = details.bushesStages;
				player.getFarming().getBushes().bushesSeeds = details.bushesSeeds;
				player.getFarming().getBushes().bushesState = details.bushesState;
				player.getFarming().getBushes().bushesTimer = details.bushesTimer;
				player.getFarming().getBushes().diseaseChance = details.bushesDiseaseChance;
				player.getFarming().getBushes().hasFullyGrown = details.bushesHasFullyGrown;
				player.getFarming().getBushes().bushesWatched = details.bushesWatched;
				player.getFarming().getAllotment().allotmentStages = details.allotmentStages;
				player.getFarming().getAllotment().allotmentSeeds = details.allotmentSeeds;
				player.getFarming().getAllotment().allotmentHarvest = details.allotmentHarvest;
				player.getFarming().getAllotment().allotmentState = details.allotmentState;
				player.getFarming().getAllotment().allotmentTimer = details.allotmentTimer;
				player.getFarming().getAllotment().diseaseChance = details.allotmentDiseaseChance;
				player.getFarming().getAllotment().allotmentWatched = details.allotmentWatched;
				player.getFarming().getAllotment().hasFullyGrown = details.allotmentHasFullyGrown;
				player.getFarming().getFlowers().flowerStages = details.flowerStages;
				player.getFarming().getFlowers().flowerSeeds = details.flowerSeeds;
				player.getFarming().getFlowers().flowerState = details.flowerState;
				player.getFarming().getFlowers().flowerTimer = details.flowerTimer;
				player.getFarming().getFlowers().diseaseChance = details.flowerDiseaseChance;
				player.getFarming().getFlowers().hasFullyGrown = details.flowerHasFullyGrown;
				player.getFarming().getFruitTrees().fruitTreeStages = details.fruitTreeStages;
				player.getFarming().getFruitTrees().fruitTreeSaplings = details.fruitTreeSaplings;
				player.getFarming().getFruitTrees().fruitTreeState = details.fruitTreeState;
				player.getFarming().getFruitTrees().fruitTreeTimer = details.fruitTreeTimer;
				player.getFarming().getFruitTrees().diseaseChance = details.fruitDiseaseChance;
				player.getFarming().getFruitTrees().hasFullyGrown = details.fruitHasFullyGrown;
				player.getFarming().getFruitTrees().fruitTreeWatched = details.fruitTreeWatched;
				player.getFarming().getHerbs().herbStages = details.herbStages;
				player.getFarming().getHerbs().herbSeeds = details.herbSeeds;
				player.getFarming().getHerbs().herbHarvest = details.herbHarvest;
				player.getFarming().getHerbs().herbState = details.herbState;
				player.getFarming().getHerbs().herbTimer = details.herbTimer;
				player.getFarming().getHerbs().diseaseChance = details.herbDiseaseChance;
				player.getFarming().getHops().hopsStages = details.hopsStages;
				player.getFarming().getHops().hopsSeeds = details.hopsSeeds;
				player.getFarming().getHops().hopsHarvest = details.hopsHarvest;
				player.getFarming().getHops().hopsState = details.hopsState;
				player.getFarming().getHops().hopsTimer = details.hopsTimer;
				player.getFarming().getHops().diseaseChance = details.hopDiseaseChance;
				player.getFarming().getHops().hasFullyGrown = details.hopHasFullyGrown;
				player.getFarming().getHops().hopsWatched = details.hopsWatched;
				player.getFarming().getSpecialPlantOne().specialPlantStages = details.specialPlantOneStages;
				player.getFarming().getSpecialPlantOne().specialPlantSaplings = details.specialPlantOneSeeds;
				player.getFarming().getSpecialPlantOne().specialPlantState = details.specialPlantOneState;
				player.getFarming().getSpecialPlantOne().specialPlantTimer = details.specialPlantOneTimer;
				player.getFarming().getSpecialPlantOne().diseaseChance = details.specialPlantOneDiseaseChance;
				player.getFarming().getSpecialPlantOne().hasFullyGrown = details.specialPlantOneHasFullyGrown;
				player.getFarming().getSpecialPlantTwo().specialPlantStages = details.specialPlantTwoStages;
				player.getFarming().getSpecialPlantTwo().specialPlantSeeds = details.specialPlantTwoSeeds;
				player.getFarming().getSpecialPlantTwo().specialPlantState = details.specialPlantTwoState;
				player.getFarming().getSpecialPlantTwo().specialPlantTimer = details.specialPlantTwoTimer;
				player.getFarming().getSpecialPlantTwo().diseaseChance = details.specialPlantTwoDiseaseChance;
				player.getFarming().getSpecialPlantTwo().hasFullyGrown = details.specialPlantTwoHasFullyGrown;
				player.getFarming().getTrees().treeStages = details.treeStages;
				player.getFarming().getTrees().treeSaplings = details.treeSaplings;
				player.getFarming().getTrees().treeHarvest = details.treeHarvest;
				player.getFarming().getTrees().treeState = details.treeState;
				player.getFarming().getTrees().treeTimer = details.treeTimer;
				player.getFarming().getTrees().diseaseChance = details.treeDiseaseChance;
				player.getFarming().getTrees().hasFullyGrown = details.treeHasFullyGrown;
				player.getFarming().getTrees().treeWatched = details.treeWatched;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			return true;
		}

		private final long farmingTimer;
		private final long lastFarmingTimer;

		private final int[] compostBins;
		private final long[] compostBinsTimer;
		private final int[] organicItemAdded;

		private final int[] bushesStages;
		private final int[] bushesSeeds;
		private final int[] bushesState;
		private final long[] bushesTimer;
		private final double[] bushesDiseaseChance;
		private final boolean[] bushesHasFullyGrown;
		private final boolean[] bushesWatched;

		private final int[] allotmentStages;
		private final int[] allotmentSeeds;
		private final int[] allotmentHarvest;
		private final int[] allotmentState;
		private final long[] allotmentTimer;
		private final double[] allotmentDiseaseChance;
		private final boolean[] allotmentWatched;
		private final boolean[] allotmentHasFullyGrown;

		private final int[] flowerStages;
		private final int[] flowerSeeds;
		private final int[] flowerState;
		private final long[] flowerTimer;
		private final double[] flowerDiseaseChance;
		private final boolean[] flowerHasFullyGrown;

		private final int[] fruitTreeStages;
		private final int[] fruitTreeSaplings;
		private final int[] fruitTreeState;
		private final long[] fruitTreeTimer;
		private final double[] fruitDiseaseChance;
		private final boolean[] fruitHasFullyGrown;
		private final boolean[] fruitTreeWatched;

		private final int[] herbStages;
		private final int[] herbSeeds;
		private final int[] herbHarvest;
		private final int[] herbState;
		private final long[] herbTimer;
		private final double[] herbDiseaseChance;

		private final int[] hopsStages;
		private final int[] hopsSeeds;
		private final int[] hopsHarvest;
		private final int[] hopsState;
		private final long[] hopsTimer;
		private final double[] hopDiseaseChance;
		private final boolean[] hopHasFullyGrown;
		private final boolean[] hopsWatched;

		private final int[] specialPlantOneStages;
		private final int[] specialPlantOneSeeds;
		private final int[] specialPlantOneState;
		private final long[] specialPlantOneTimer;
		private final double[] specialPlantOneDiseaseChance;
		private final boolean[] specialPlantOneHasFullyGrown;

		private final int[] specialPlantTwoStages;
		private final int[] specialPlantTwoSeeds;
		private final int[] specialPlantTwoState;
		private final long[] specialPlantTwoTimer;
		private final double[] specialPlantTwoDiseaseChance;
		private final boolean[] specialPlantTwoHasFullyGrown;

		private final int[] treeStages;
		private final int[] treeSaplings;
		private final int[] treeHarvest;
		private final int[] treeState;
		private final long[] treeTimer;
		private final double[] treeDiseaseChance;
		private final boolean[] treeHasFullyGrown;
		private final boolean[] treeWatched;

		public PlayerFarming(Player player) {
			this.farmingTimer = player.getFarming().getFarmingTimer();
			this.lastFarmingTimer = System.currentTimeMillis();
			this.compostBins = player.getFarming().getCompost().compostBins;
			this.compostBinsTimer = player.getFarming().getCompost().compostBinsTimer;
			this.organicItemAdded = player.getFarming().getCompost().organicItemAdded;
			this.bushesStages = player.getFarming().getBushes().bushesStages;
			this.bushesSeeds = player.getFarming().getBushes().bushesSeeds;
			this.bushesState = player.getFarming().getBushes().bushesState;
			this.bushesTimer = player.getFarming().getBushes().bushesTimer;
			this.bushesDiseaseChance = player.getFarming().getBushes().diseaseChance;
			this.bushesHasFullyGrown = player.getFarming().getBushes().hasFullyGrown;
			this.bushesWatched = player.getFarming().getBushes().bushesWatched;
			this.allotmentStages = player.getFarming().getAllotment().allotmentStages;
			this.allotmentSeeds = player.getFarming().getAllotment().allotmentSeeds;
			this.allotmentHarvest = player.getFarming().getAllotment().allotmentHarvest;
			this.allotmentState = player.getFarming().getAllotment().allotmentState;
			this.allotmentTimer = player.getFarming().getAllotment().allotmentTimer;
			this.allotmentDiseaseChance = player.getFarming().getAllotment().diseaseChance;
			this.allotmentWatched = player.getFarming().getAllotment().allotmentWatched;
			this.allotmentHasFullyGrown = player.getFarming().getAllotment().hasFullyGrown;
			this.flowerStages = player.getFarming().getFlowers().flowerStages;
			this.flowerSeeds = player.getFarming().getFlowers().flowerSeeds;
			this.flowerState = player.getFarming().getFlowers().flowerState;
			this.flowerTimer = player.getFarming().getFlowers().flowerTimer;
			this.flowerDiseaseChance = player.getFarming().getFlowers().diseaseChance;
			this.flowerHasFullyGrown = player.getFarming().getFlowers().hasFullyGrown;
			this.fruitTreeStages = player.getFarming().getFruitTrees().fruitTreeStages;
			this.fruitTreeSaplings = player.getFarming().getFruitTrees().fruitTreeSaplings;
			this.fruitTreeState = player.getFarming().getFruitTrees().fruitTreeState;
			this.fruitTreeTimer = player.getFarming().getFruitTrees().fruitTreeTimer;
			this.fruitDiseaseChance = player.getFarming().getFruitTrees().diseaseChance;
			this.fruitHasFullyGrown = player.getFarming().getFruitTrees().hasFullyGrown;
			this.fruitTreeWatched = player.getFarming().getFruitTrees().fruitTreeWatched;
			this.herbStages = player.getFarming().getHerbs().herbStages;
			this.herbSeeds = player.getFarming().getHerbs().herbSeeds;
			this.herbHarvest = player.getFarming().getHerbs().herbHarvest;
			this.herbState = player.getFarming().getHerbs().herbState;
			this.herbTimer = player.getFarming().getHerbs().herbTimer;
			this.herbDiseaseChance = player.getFarming().getHerbs().diseaseChance;
			this.hopsStages = player.getFarming().getHops().hopsStages;
			this.hopsSeeds = player.getFarming().getHops().hopsSeeds;
			this.hopsHarvest = player.getFarming().getHops().hopsHarvest;
			this.hopsState = player.getFarming().getHops().hopsState;
			this.hopsTimer = player.getFarming().getHops().hopsTimer;
			this.hopDiseaseChance = player.getFarming().getHops().diseaseChance;
			this.hopHasFullyGrown = player.getFarming().getHops().hasFullyGrown;
			this.hopsWatched = player.getFarming().getHops().hopsWatched;
			this.specialPlantOneStages = player.getFarming().getSpecialPlantOne().specialPlantStages;
			this.specialPlantOneSeeds = player.getFarming().getSpecialPlantOne().specialPlantSaplings;
			this.specialPlantOneState = player.getFarming().getSpecialPlantOne().specialPlantState;
			this.specialPlantOneTimer = player.getFarming().getSpecialPlantOne().specialPlantTimer;
			this.specialPlantOneDiseaseChance = player.getFarming().getSpecialPlantOne().diseaseChance;
			this.specialPlantOneHasFullyGrown = player.getFarming().getSpecialPlantOne().hasFullyGrown;
			this.specialPlantTwoStages = player.getFarming().getSpecialPlantTwo().specialPlantStages;
			this.specialPlantTwoSeeds = player.getFarming().getSpecialPlantTwo().specialPlantSeeds;
			this.specialPlantTwoState = player.getFarming().getSpecialPlantTwo().specialPlantState;
			this.specialPlantTwoTimer = player.getFarming().getSpecialPlantTwo().specialPlantTimer;
			this.specialPlantTwoDiseaseChance = player.getFarming().getSpecialPlantTwo().diseaseChance;
			this.specialPlantTwoHasFullyGrown = player.getFarming().getSpecialPlantTwo().hasFullyGrown;
			this.treeStages = player.getFarming().getTrees().treeStages;
			this.treeSaplings = player.getFarming().getTrees().treeSaplings;
			this.treeHarvest = player.getFarming().getTrees().treeHarvest;
			this.treeState = player.getFarming().getTrees().treeState;
			this.treeTimer = player.getFarming().getTrees().treeTimer;
			this.treeDiseaseChance = player.getFarming().getTrees().diseaseChance;
			this.treeHasFullyGrown = player.getFarming().getTrees().hasFullyGrown;
			this.treeWatched = player.getFarming().getTrees().treeWatched;
		}

		public void parseDetails(Player player) throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/characters/farming/" + player.getUsername() + ".json", false));
			try {
				writer.write(PlayerSave.GSON.toJson(this));
				writer.flush();
			} finally {
				writer.close();
			}
		}
	}

	public static final class PlayerContainer {

		public static boolean loadDetails(Player player) throws Exception {
			File file = new File("./data/characters/containers/" + player.getUsername() + ".json");

			if (!file.exists()) {
				return false;
			}


			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				PlayerContainer details = PlayerSave.GSON.fromJson(reader, PlayerContainer.class);

				if (details.shopItems != null) {
					player.getPlayerShop().setItems(details.shopItems);
				}

				if (details.shopPrices != null) {
					player.getPlayerShop().setPrices(details.shopPrices);
				}

				if (details.tabAmounts != null) {
					player.getBank().setTabAmounts(details.tabAmounts);
				}

				if (details.bank != null) {
					int tabs = Arrays.stream(player.getBank().getTabAmounts()).sum();
					int total = 0;
					for (int i = 0, slot = 0; i < Bank.SIZE; i++) {
						if (i >= Bank.SIZE) {
							break;
						}
						if (i >= details.bank.length) {
							break;
						}
						Item check = ItemCheck.check(player, details.bank[i]);
						player.getBank().getItems()[slot++] = check;
						if (check != null) {
							total++;
						}
					}
					
					if (total != tabs) {
						player.getBank().setTabAmounts(new int[] { total, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
						DialogueManager.sendStatement(player, "@dre@There was an issue loading your bank tabs.", "@dre@Your bank tabs have been collapsed as a safety measure.");
					}
				}

				if (details.equipment != null) {
					for (int i = 0; i < details.equipment.length; i++) {
						player.getEquipment().getItems()[i] = ItemCheck.check(player, details.equipment[i]);
					}
				}

				if (details.inventory != null) {
					for (int i = 0; i < details.inventory.length; i++) {
						player.getInventory().getItems()[i] = ItemCheck.check(player, details.inventory[i]);
					}
				}

				if (details.bobInventory != null) {
					player.getAttributes().set("summoningbobinventory", details.bobInventory);
				}

				if (details.pouches != null) {
					player.setPouches(details.pouches);
				}

				player.setLastLoginDay(Utility.getDayOfYear());
				player.setLastLoginYear(Utility.getYear());

			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			return true;
		}

		private final Item[] bank;
		private final int[] tabAmounts;
		private final Item[] inventory;
		private final Item[] equipment;
		private final Item[] bobInventory;
		private final Item[] shopItems;
		private final int[] shopPrices;
		private final byte[] pouches;

		public PlayerContainer(Player player) {
			bank = player.getBank().getItems();
			tabAmounts = player.getBank().getTabAmounts();
			inventory = player.getInventory().getItems();
			equipment = player.getEquipment().getItems();
			bobInventory = (player.getSummoning().isFamilarBOB() ? player.getSummoning().getContainer().getItems() : null);
			shopItems = player.getPlayerShop().getItems();
			shopPrices = player.getPlayerShop().getPrices();
			pouches = player.getPouches();
		}

		public void parseDetails(Player player) throws IOException {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/characters/containers/" + player.getUsername() + ".json", false));
			try {
				writer.write(PlayerSave.GSON.toJson(this));
				writer.flush();
			} finally {
				writer.close();
			}
		}
	}

	public static final class PlayerDetails {

		public static boolean loadDetails(Player player) throws Exception {
			BufferedReader reader = null;
			try {
				File file = new File("./data/characters/details/" + NameUtil.uppercaseFirstLetter(player.getUsername()) + ".json");

				if (!file.exists()) {
					return false;
				}


				reader = new BufferedReader(new FileReader(file));

				PlayerDetails details = PlayerSave.GSON.fromJson(reader, PlayerDetails.class);
				player.setUsername(details.username);
				player.setPassword(details.password);
				player.getLocation().setAs(new Location(details.x, details.y, details.z));
				player.setRights(details.rights);

				if (details.lastKnownUID != null) {					
					player.setLastKnownUID(details.lastKnownUID);
				}

				player.setPlayerMode(details.mode);
				player.setYellTitle(details.yellTitle);
				player.setPlayerTitle(details.playerTitle);
				
				if (details.unlockedTitles != null && !details.unlockedTitles.isEmpty()) {
					player.unlockedTitles.addAll(details.unlockedTitles);
				}

				player.getBank().setWithdrawalMode(details.withdrawalMode);
				player.setMoneyPouch(details.moneyPouch);
				player.setShopCollection(details.shopCollection);
				player.setWeaponPoints(details.weaponPoints);
				player.setShopMotto(details.shopMotto);
				player.setShopColor(details.shopColor);
				player.setLastLike(details.lastLike);
				player.setLikesGiven(details.likesGiven);
				player.setLikes(details.likes);
				player.setDislikes(details.dislikes);
				player.setProfileViews(details.profileViews);
				player.setRetaliate(details.retaliate);
				player.getSkill().setExpLock(details.expLock);
				player.getSlayer().setAmount(details.slayerAmount);
				player.getSlayer().setTask(details.slayerTask);
				player.setPoisonDamage(details.poisonDmg);
				player.getSpecialAttack().setSpecialAmount(details.spec);
				player.getRunEnergy().setEnergy(details.energy);
				player.getSummoning().setAttack(details.summoningAttack);
				player.getSummoning().setSpecial(details.summoningSpecialAmount);
				player.getSummoning().setTime(details.summoningTime);
				player.setPestPoints(details.pestPoints);
				player.setArenaPoints(details.arenaPoints);
				if (details.summoningFamiliar != -1) {
					player.getAttributes().set("summoningfamsave", details.summoningFamiliar);
				}
				player.getClient().setLogPlayer(details.logPackets);
				player.getRunEnergy().setRunning(details.running);
				player.setTeleblockTime(details.teleblockTime);
				player.getRareDropEP().setEp(details.rareDropEP);
				player.getRareDropEP().setReceived(details.rareDropsReceived);
				player.setDeaths(details.deaths);
				player.setKills(details.kills);
				player.setRogueKills(details.rogueKills);
				player.setRogueRecord(details.rogueRecord);
				player.setHunterKills(details.hunterKills);
				player.setHunterRecord(details.hunterRecord);
				player.setLastKilledPlayers(details.lastKilledPlayers);
				player.setPin(details.pins);
				player.setIP(details.IP);
				player.setEmailAddress(details.emailAddress);
				player.setFullName(details.fullName);
				player.setRecovery(details.recovery);
				player.setCredits(details.credits);
				player.setMoneySpent(details.moneySpent);
				player.setBountyPoints(details.bountyPoints);
				player.setMusicVolume((byte) details.musicVolume);
				player.setSoundVolume((byte) details.soundVolume);
				player.setSlayerPoints(details.slayerPoints);
				player.setBlackMarks(details.blackMarks);
				player.getMagic().setDragonFireShieldCharges(details.dragonFireShieldCharges);
				player.setPlayPoints(details.playPoints);
				if (details.degrading != null) {
					player.getItemDegrading().setDegrading(details.degrading);
				}
				if (details.savedArrows != null) {
					player.getRanged().getSavedArrows().setItems(details.savedArrows);
				}
				player.setVotePoints(details.votePoints);
				player.setWantTrivia(details.wantsTrivia);
				player.setTriviaNotification(details.triviaNotification);
				if (details.attackStyle != null) {
					player.getEquipment().setAttackStyle(details.attackStyle);
				}
				if (details.attackType != null) {
					player.setAttackType(details.attackType);
				}
				if (details.recoilStage != -1) {
					player.getAttributes().set("recoilhits", details.recoilStage);
				}
				
				player.getSkulling().setLeft(details.left);
				if (player.getSkulling().isSkulled()) {
					player.getSkulling().setSkullIcon(player, details.skullIcon);
				}
				if (details.host != null) {
					player.getClient().setHost(details.host);
				}
				if (details.slayerDifficulty != null) {
					player.getSlayer().setCurrent(details.slayerDifficulty);
				}
				player.setUltimateIron(details.isUltimateIron);
				player.setMember(details.isMember);
				player.setIron(details.isIron);
				player.setYearCreated(details.yearCreated);
				player.setDayCreated(details.dayCreated);

				player.setLastLoginDay(details.lastLoginDay);
				player.setLastLoginYear(details.lastLoginYear);
				player.setScreenBrightness(details.bright);
				player.setMultipleMouseButtons(details.multipleMouse);
				player.setChatEffects(details.chatEffects);
				player.setSplitPrivateChat(details.splitPrivate);			
				player.setTransparentPanel(details.transparentPanel);
				player.setTransparentChatbox(details.transparentChatbox);
				player.setSideStones(details.sideStones);			
				player.setAcceptAid(details.acceptAid);
				player.getJadDetails().setStage(details.fightCavesWave);
				if (details.friends != null) {
					for (String i : details.friends) {
						player.getPrivateMessaging().getFriends().add(i);
					}
				}

				if (details.ignores != null) {
					for (String i : details.ignores) {
						player.getPrivateMessaging().getIgnores().add(i);
					}
				}

				if ((details.poisonDmg > 0) && (details.poisoned)) {
					player.poison(details.poisonDmg);
				}
				
				player.setPouchPayment(details.pouchPayment);

				player.setGender(details.gender);

				if (details.appearance != null) {
					for (int i = 0; i < details.appearance.length; i++)
						player.getAppearance()[i] = details.appearance[i];
				}
				if (details.colours != null) {
					for (int i = 0; i < details.colours.length; i++)
						player.getColors()[i] = details.colours[i];
				}
				if (details.experience != null) {
					for (int i = 0; i < details.experience.length; i++) {
						player.getSkill().getExperience()[i] = details.experience[i];
					}
				}
				if (details.skillsLevel != null) {
					for (int i = 0; i < details.skillsLevel.length; i++) {
						player.getLevels()[i] = details.skillsLevel[i];
					}
				}
				if (details.experience != null) {
					for (int i = 0; i < details.experience.length; i++) {
						player.getMaxLevels()[i] = player.getSkill().getLevelForExperience(i, details.experience[i]);
					}
				}

				if (details.gwkc != null) {
					player.getMinigames().setGWKC(details.gwkc);
				}

				boolean banned = details.banned;
				boolean muted = details.muted;
				boolean jailed = details.jailed;

				if ((banned) && (TimeUnit.MILLISECONDS.toSeconds(details.banLength - System.currentTimeMillis()) > 0 || details.banLength == -1)) {
					player.setBanned(true);
					player.setBanLength(details.banLength);
				}
				
				if ((jailed) && (TimeUnit.MILLISECONDS.toSeconds(details.jailLength - System.currentTimeMillis()) > 0 || details.jailLength == -1)) {
					player.setJailed(true);
					player.setJailLength(details.jailLength);
				}

				if ((muted) && (TimeUnit.MILLISECONDS.toSeconds(details.muteLength - System.currentTimeMillis()) > 0 || details.muteLength == -1)) {
					player.setMuted(true);
					player.setMuteLength(details.muteLength);
				}

				if (details.magicBook > 0) {
					player.getMagic().setMagicBook(details.magicBook);
				}

				if (details.prayerBook > 0) {
					player.setPrayerInterface(details.prayerBook);
				}

				if (details.skillGoals != null) {
					player.setSkillGoals(details.skillGoals);
				}

				if (details.playerAchievements != null) {
					player.getPlayerAchievements().putAll(details.playerAchievements);
				}

				if (details.achievementsPoints > 0) {
					player.addAchievementPoints(details.achievementsPoints);
				}

				if (details.expCounter > 0) {
					player.addCounterExp(details.expCounter);
				}
				
				if (details.cluesCompleted != null) {
					player.setCluesCompleted(details.cluesCompleted);
				}
				
				if (details.lastClanChat != null) {
					player.lastClanChat = details.lastClanChat;
				}

				// set prayer book

				player.setPrayerInterface(5608);

				if (details.quickPrayers != null) {
					player.getPrayer().setQuickPrayers(details.quickPrayers);
				}

				player.setPrestigePoints(details.prestigePoints);

				if (details.prestiges != null) {
					player.setSkillPrestiges(details.prestiges);
				}

				if (details.blowpipe != null) {
					player.setToxicBlowpipe(details.blowpipe);
				}

				if (details.seasTrident != null) {
					player.setSeasTrident(details.seasTrident);
				}
				
				if (details.swampTrident != null) {
					player.setSwampTrident(details.swampTrident);
				}
				
				if (details.serpentineHelmet != null) {
					player.setSerpentineHelment(details.serpentineHelmet);
				}
				
				player.getProperties().setDefaults();
				
				if (details.playerProperties != null) {
					for (Object attribute : details.playerProperties.keySet()) {
						player.getAttributes().getAttributes().put(attribute, details.playerProperties.get(attribute));
					}
				}

				if (details.unlockedCredits != null) {
					player.getUnlockedCredits().addAll(details.unlockedCredits);
				}

				if (details.quickPrayers != null) {
					player.getPrayer().setQuickPrayers(details.quickPrayers);
				}

				return true;
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

		private final String username;
		private final String password;
		private final int x;
		private final int y;
		private final int z;
		private final byte rights;
		private final String lastKnownUID;
		private final int[] cluesCompleted;
		private final int mode;
		private final int withdrawalMode;
		private final String yellTitle;
		private final PlayerTitle playerTitle;
		private final List<PlayerTitle> unlockedTitles;
		private final boolean banned;
		private final long banLength;
		private final long moneyPouch;
		private final long jailLength;
		private final long shopCollection;
		private final String shopMotto;
		private final String shopColor;
		private final String lastClanChat;
		private final boolean muted;
		private final boolean isMember;
		private final boolean isIron;
		private final boolean isUltimateIron;
		private final boolean jailed;
		private final long muteLength;
		private final int fightCavesWave;
		private final int magicBook;
		private final int prayerBook;
		private final boolean retaliate;
		private final boolean expLock;
		private final short[] gwkc;
		private final boolean poisoned;
		private final int poisonDmg;
		private final String slayerTask;
		private final byte slayerAmount;
		private final Slayer.SlayerDifficulty slayerDifficulty;
		private final short[] skillsLevel;
		private final double[] experience;
		private final byte gender;
		private final int[] appearance;
		private final byte[] colours;
		private final long left;
		private final int skullIcon;
		private final byte bright;
		private final byte multipleMouse;
		private final byte chatEffects;
		private final byte splitPrivate;
		
		private byte transparentPanel;
		private byte transparentChatbox;
		private byte sideStones;
		
		private final byte acceptAid;
		private final boolean pouchPayment;
		private final String[] friends;
		private final String[] ignores;
		private final int yearCreated;
		private final int dayCreated;
		private final int recoilStage;
		private final int spec;
		private final Equipment.AttackStyles attackStyle;
		private final AttackType attackType;
		private final double energy;
		private final int lastLoginDay;
		private final int lastLoginYear;
		private final String host;
		private final int votePoints;
		private final ItemDegrading.DegradedItem[] degrading;
		private final byte dragonFireShieldCharges;
		private final int slayerPoints;
		private final int musicVolume;
		private final int soundVolume;
		private final Item[] savedArrows;
		private final boolean wantsTrivia;
		private final boolean triviaNotification;
		private final int deaths;
		private final int kills;
		private final int rogueKills;
		private final int rogueRecord;
		private final int hunterKills;
		private final int hunterRecord;
		private final int credits;
		private final int moneySpent;
		private final int bountyPoints;
		private final int playPoints;
		private long lastLike;
		private byte likesGiven;
		private int likes, dislikes, profileViews;
		private final String fullName;
		private final String IP;
		private final String emailAddress;
		private final String recovery;
		private final String pins;
		private final boolean running;
		private final boolean logPackets;
		private final int weaponPoints;
		private final int summoningTime;
		private final int summoningSpecialAmount;
		private final int summoningFamiliar;
		private final boolean summoningAttack;
		private final int pestPoints;
		private final int arenaPoints;
		private final int teleblockTime;
		private final int blackMarks;
		private final double rareDropEP;
		private final int rareDropsReceived;
		private final int[][] skillGoals;
		private final ArrayList<String> lastKilledPlayers;
		private final HashMap<AchievementList, Integer> playerAchievements;
		private final int achievementsPoints;
		private final Set<CreditPurchase> unlockedCredits;
		private final boolean[] quickPrayers;

		private final HashMap<Object, Object> playerProperties;

		private final double expCounter;
		private final int prestigePoints;
		private final int[] prestiges;
		private final ToxicBlowpipe blowpipe;
		private final TridentOfTheSeas seasTrident;
		private final TridentOfTheSwamp swampTrident;
		private final SerpentineHelmet serpentineHelmet;

		public PlayerDetails(Player player) {
			username = player.getUsername();
			password = player.getPassword();
			x = player.getLocation().getX();
			y = player.getLocation().getY();
			z = player.getLocation().getZ();
			rights = ((byte) player.getRights());
			lastKnownUID = player.getUid();
			fullName = player.getFullName();
			IP = player.getIP();
			recovery = player.getRecovery();
			emailAddress = player.getEmailAddress();
			pins = player.getPin();
			credits = player.getCredits();
			moneySpent = player.getMoneySpent();
			host = player.getClient().getHost();
			cluesCompleted = player.getCluesCompleted();
			mode = player.getMode();
			withdrawalMode = player.getBank().getWithdrawalMode();
			yellTitle = player.getYellTitle();
			playerTitle = player.getPlayerTitle();
			unlockedTitles = player.unlockedTitles;
			lastLike = player.getLastLike();
			likesGiven = player.getLikesGiven();
			likes = player.getLikes();
			dislikes = player.getDislikes();
			profileViews = player.getProfileViews();
			banned = player.isBanned();
			banLength = player.getBanLength();
			moneyPouch = player.getMoneyPouch();
			jailLength = player.getJailLength();
			shopCollection = player.getShopCollection();
			shopMotto = player.getShopMotto();
			shopColor = player.getShopColor();
			muted = player.isMuted();
			isMember = player.isMember();
			isIron = player.isIron();
			isUltimateIron = player.isUltimateIron();
			jailed = player.isJailed();
			muteLength = player.getMuteLength();
			weaponPoints = player.getWeaponPoints();
			fightCavesWave = player.getJadDetails().getStage();
			magicBook = player.getMagic().getMagicBook();
			prayerBook = player.getPrayerInterface();
			retaliate = player.isRetaliate();
			expLock = player.getSkill().isExpLocked();
			gwkc = player.getMinigames().getGWKC();
			lastClanChat = player.lastClanChat;

			quickPrayers = player.getPrayer().getQuickPrayers();

			rareDropEP = player.getRareDropEP().getEp();

			rareDropsReceived = player.getRareDropEP().getReceived();

			blackMarks = player.getBlackMarks();

			poisoned = player.isPoisoned();
			pouchPayment = player.isPouchPayment();
			poisonDmg = player.getPoisonDamage();
			slayerTask = player.getSlayer().getTask();
			slayerAmount = player.getSlayer().getAmount();
			experience = player.getSkill().getExperience();
			skillsLevel = player.getSkill().getLevels();
			gender = player.getGender();
			appearance = (player.getAppearance().clone());
			colours = (player.getColors().clone());
			left = player.getSkulling().getLeft();
			skullIcon = player.getSkulling().getSkullIcon();
			spec = player.getSpecialAttack().getAmount();
			attackStyle = player.getEquipment().getAttackStyle();
			attackType = player.getAttackType();
			energy = player.getRunEnergy().getEnergy();
			votePoints = player.getVotePoints();

			teleblockTime = player.getTeleblockTime();

			summoningAttack = player.getSummoning().isAttack();
			summoningTime = player.getSummoning().getTime();
			summoningSpecialAmount = player.getSummoning().getSpecialAmount();
			summoningFamiliar = (player.getSummoning().getFamiliarData() != null ? player.getSummoning().getFamiliarData().mob : -1);

			logPackets = player.getClient().isLogPlayer();

			running = player.getRunEnergy().isRunning();

			pestPoints = player.getPestPoints();
			arenaPoints = player.getArenaPoints();

			soundVolume = player.getSoundVolume();

			deaths = player.getDeaths();
			kills = player.getKills();
			rogueKills = player.getRogueKills();
			rogueRecord = player.getRogueRecord();
			hunterKills = player.getHunterKills();
			hunterRecord = player.getHunterRecord();

			lastKilledPlayers = player.getLastKilledPlayers();

			bountyPoints = player.getBountyPoints();
			playPoints = player.getPlayPoints();


			musicVolume = player.getMusicVolume();

			dragonFireShieldCharges = player.getMagic().getDragonFireShieldCharges();

			degrading = player.getItemDegrading().getDegrading();

			lastLoginDay = player.getLastLoginDay();
			lastLoginYear = player.getLastLoginYear();

			yearCreated = player.getYearCreated();
			dayCreated = player.getDayCreated();

			slayerPoints = player.getSlayerPoints();
			slayerDifficulty = player.getSlayer().getCurrent();

			if (player.getAttributes().get("recoilhits") != null)
				recoilStage = player.getAttributes().getInt("recoilhits");
			else {
				recoilStage = -1;
			}

			bright = player.getScreenBrightness();
			multipleMouse = player.getMultipleMouseButtons();
			chatEffects = player.getChatEffectsEnabled();
			splitPrivate = player.getSplitPrivateChat();
			transparentPanel = player.getTransparentPanel();
			transparentChatbox = player.getTransparentChatbox();
			sideStones = player.getSideStones();	
			acceptAid = player.getAcceptAid();

			savedArrows = player.getRanged().getSavedArrows().getItems();
			skillGoals = player.getSkillGoals();
			expCounter = player.getCounterExp();
			
			wantsTrivia = player.isWantTrivia();
			triviaNotification = player.isTriviaNotification();

			playerAchievements = player.getPlayerAchievements();
			achievementsPoints = player.getAchievementsPoints();
			
			unlockedCredits = player.getUnlockedCredits();

			int k = 0;
			friends = new String[player.getPrivateMessaging().getFriends().size()];
			for (String i : player.getPrivateMessaging().getFriends()) {
				friends[k] = i;
				k++;
			}

			k = 0;
			ignores = new String[player.getPrivateMessaging().getIgnores().size()];
			for (String i : player.getPrivateMessaging().getIgnores()) {
				ignores[k] = i;
				k++;
			}

			prestigePoints = player.getPrestigePoints();
			prestiges = player.getSkillPrestiges();

			blowpipe = player.getToxicBlowpipe();
			seasTrident = player.getSeasTrident();
			swampTrident = player.getSwampTrident();
			serpentineHelmet = player.getSerpentineHelment();
			
			playerProperties = new HashMap<>();
			
			for (Object attribute : player.getAttributes().getAttributes().keySet()) {
				if (String.valueOf(attribute).startsWith(PlayerProperties.ATTRIBUTE_KEY)) {
					playerProperties.put(attribute, player.getAttributes().getAttributes().get(attribute));
				}
			}
		}

		public void parseDetails() throws Exception {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("./data/characters/details/" + username + ".json", false));
				writer.write(PlayerSave.GSON.toJson(this));
				writer.flush();
			} finally {
				if (writer != null)
					writer.close();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		GameDefinitionLoader.declare();
		GameDefinitionLoader.loadNpcDefinitions();
		File[] files = new File("./data/characters/details/").listFiles();
		int searches = 0;
		HashMap<String, String> map = new HashMap<>();
		for (File file : files) {
			Player player = new Player();
			player.setUsername(file.getName().replace(".json", ""));
			try {
				if (PlayerDetails.loadDetails(player)) {
				
					if (player.getLastLoginDay() > 259) {
						if (player.getLastKnownUID() != null && !map.containsKey(player.getLastKnownUID())) {
							map.put(player.getLastKnownUID(), player.getUsername());
						} else {
							System.out.println(player.getUsername() + " " + player.getLastKnownUID());
						}
						searches++;
					}

				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Searched thorugh " + searches + " files.");
	}

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static synchronized boolean load(Player p) throws Exception {
		if (!PlayerDetails.loadDetails(p)) {
			return false;
		}

		if (!PlayerContainer.loadDetails(p)) {
			return false;
		}

		if (!PlayerFarming.loadDetails(p)) {
			return false;
		}

		return true;
	}

	public static synchronized final void save(Player p) {
		try {
			new PlayerDetails(p).parseDetails();
			new PlayerContainer(p).parseDetails(p);
			new PlayerFarming(p).parseDetails(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
