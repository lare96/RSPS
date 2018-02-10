package com.vencillio;

import com.vencillio.core.cache.map.MapLoading;
import com.vencillio.core.cache.map.ObjectDef;
import com.vencillio.core.cache.map.RSInterface;
import com.vencillio.core.cache.map.Region;
import com.vencillio.core.util.FileHandler;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.Emotes;
import com.vencillio.rs2.content.FountainOfRune;
import com.vencillio.rs2.content.GlobalMessages;
import com.vencillio.rs2.content.cluescroll.ClueScrollManager;
import com.vencillio.rs2.content.combat.impl.PoisonWeapons;
import com.vencillio.rs2.content.combat.special.SpecialAttackHandler;
import com.vencillio.rs2.content.dialogue.OneLineDialogue;
import com.vencillio.rs2.content.minigames.duelarena.DuelingConstants;
import com.vencillio.rs2.content.minigames.godwars.GodWarsData;
import com.vencillio.rs2.content.minigames.plunder.PlunderConstants;
import com.vencillio.rs2.content.minigames.plunder.PyramidPlunder;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.agility.Agility;
import com.vencillio.rs2.content.skill.cooking.CookingData;
import com.vencillio.rs2.content.skill.crafting.*;
import com.vencillio.rs2.content.skill.craftingnew.craftable.impl.Hide;
import com.vencillio.rs2.content.skill.farming.Farming;
import com.vencillio.rs2.content.skill.firemaking.LogData;
import com.vencillio.rs2.content.skill.fishing.FishableData;
import com.vencillio.rs2.content.skill.fishing.Fishing;
import com.vencillio.rs2.content.skill.fishing.ToolData;
import com.vencillio.rs2.content.skill.fletching.fletchable.impl.*;
import com.vencillio.rs2.content.skill.herblore.FinishedPotionData;
import com.vencillio.rs2.content.skill.herblore.GrimyHerbData;
import com.vencillio.rs2.content.skill.herblore.GrindingData;
import com.vencillio.rs2.content.skill.herblore.UnfinishedPotionData;
import com.vencillio.rs2.content.skill.magic.MagicConstants;
import com.vencillio.rs2.content.skill.magic.MagicEffects;
import com.vencillio.rs2.content.skill.mining.Mining;
import com.vencillio.rs2.content.skill.prayer.BoneBurying;
import com.vencillio.rs2.content.skill.ranged.AmmoData;
import com.vencillio.rs2.content.skill.runecrafting.RunecraftingData;
import com.vencillio.rs2.content.skill.slayer.SlayerMonsters;
import com.vencillio.rs2.content.skill.smithing.SmeltingData;
import com.vencillio.rs2.content.skill.thieving.ThievingNpcData;
import com.vencillio.rs2.content.skill.thieving.ThievingStallData;
import com.vencillio.rs2.content.skill.woodcutting.WoodcuttingAxeData;
import com.vencillio.rs2.content.vencilliobot.VencillioBot;
import com.vencillio.rs2.entity.item.EquipmentConstants;
import com.vencillio.rs2.entity.item.impl.GlobalItemHandler;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobAbilities;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.object.ObjectConstants;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.net.in.PacketHandler;

import java.util.Timer;

/**
 * Loads all of the necessary game data
 * 
 * @author Michael Sasse
 * 
 */
public class GameDataLoader {

	/**
	 * The stage of the game server
	 */
	private static int stage = 0;
	public static Timer time;

	/**
	 * Loads all of the game data
	 */
	public static void load() {
		try {
			GameDefinitionLoader.declare();
			GlobalMessages.declare();
			VencillioBot.declare();
			time = new Timer("myTimer");
			new Thread(() -> {
				try {
					ObjectDef.loadConfig();
					ObjectConstants.declare();
					MapLoading.load();
					Region.sort();
					GameDefinitionLoader.loadAlternateIds();
					MapLoading.processDoors();
					GameDefinitionLoader.clearAlternates();
					ObjectManager.declare();
					GlobalItemHandler.spawnGroundItems();
					Mob.spawnBosses();
					GameDefinitionLoader.loadNpcSpawns();
					GlobalMessages.initialize();
					VencillioBot.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}

				GameDataLoader.stage += 1;
			}).start();

			RSInterface.unpack();
			GameDefinitionLoader.loadNpcDefinitions();
			GameDefinitionLoader.loadItemDefinitions();
			GameDefinitionLoader.loadRareDropChances();	
			GameDefinitionLoader.loadEquipmentDefinitions();
			GameDefinitionLoader.loadShopDefinitions();
			GameDefinitionLoader.setRequirements();
			GameDefinitionLoader.loadWeaponDefinitions();
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadRangedStrengthDefinitions();
			GameDefinitionLoader.loadSpecialAttackDefinitions();
			GameDefinitionLoader.loadCombatSpellDefinitions();
			GameDefinitionLoader.loadFoodDefinitions();
			GameDefinitionLoader.loadPotionDefinitions();
			GameDefinitionLoader.loadRangedWeaponDefinitions();
			GameDefinitionLoader.loadNpcCombatDefinitions();
			GameDefinitionLoader.loadNpcDropDefinitions();
			GameDefinitionLoader.loadItemBonusDefinitions();			
			GodWarsData.declare();			
			Mining.declare();			
			PyramidPlunder.declare();
			PlunderConstants.UrnBitPosition.declare();
			PlunderConstants.DoorBitPosition.declare();		
			ClueScrollManager.declare();
			FountainOfRune.declare();
			Agility.declare();			
			Arrow.declare();
			Bolt.declare();
			Carvable.declare();
			Crossbow.declare();
			Featherable.declare();
			Stringable.declare();
			Craftable.declare();
			HideTanData.declare();
			Jewelry.declare();
			Spinnable.declare();		
			com.vencillio.rs2.content.skill.craftingnew.craftable.impl.Gem.declare();
			Hide.declare();		
			Farming.declare();
			Shop.declare();
			MagicConstants.declare();
			SlayerMonsters.declare();
			DuelingConstants.declare();
			MobConstants.declare();
			Emotes.declare();
			PoisonWeapons.declare();
			SpecialAttackHandler.declare();
			CookingData.declare();
			Glass.declare();
			LogData.declare();
			FishableData.Fishable.declare();
			Fishing.FishingSpots.declare();
			ToolData.Tools.declare();
			FinishedPotionData.declare();
			GrimyHerbData.declare();
			GrindingData.declare();
			UnfinishedPotionData.declare();
			MagicEffects.declare();
			BoneBurying.Bones.declare();
			AmmoData.Ammo.declare();
			RunecraftingData.declare();
			Skills.declare();
			ThievingNpcData.declare();
			ThievingStallData.declare();
			WoodcuttingAxeData.declare();
			EquipmentConstants.declare();
			PacketHandler.declare();
			MobConstants.MobDissapearDelay.declare();
			MobAbilities.declare();
			SmeltingData.declare();
			OneLineDialogue.declare();
			FileHandler.load();
			stage += 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets if the server has been successfully loaded
	 * 
	 * @return boolean
	 */
	public static boolean loaded() {
		return stage == 2;
	}
}
