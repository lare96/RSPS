package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.dialogue.DialogueManager;
import com.vencillio.rs2.content.dialogue.impl.ConfirmDialogue;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

/**
 * Handles item interactions
 * 
 * @author Daniel
 *
 */
public enum ItemCreation {

	/* Abyssal Tentacle */
	ABYSSAL_TENTACLE(new int[] { 12004, 4151 }, 12006, new CreationHandle() {
		@Override
		public void handle(Player player, ItemCreation data) {
			if (meetsRequirements(player, data)) {
				player.start(new ConfirmDialogue(player) {
					@Override
					public void onConfirm() {
						if (meetsRequirements(player, data)) {
							ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
							DialogueManager.sendItem1(player, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
							player.getInventory().remove(new Item(data.getItem()[0]));
							player.getInventory().remove(new Item(data.getItem()[1]));
							player.getInventory().add(new Item(data.getNewItem(), 1));
						}
					}
				});
			}
		}
	}),
	/* Blowpipe */
	BLOWPIPE(new int[] { 12922, 1755 }, 12924, new CreationHandle() {
		@Override
		public void handle(Player player, ItemCreation data) {
			if (meetsRequirements(player, data)) {
				player.start(new ConfirmDialogue(player) {
					@Override
					public void onConfirm() {
						if (meetsRequirements(player, data)) {
							if (!skillRequired(player, Skills.FLETCHING, 33)) {
								return;
							}
							ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
							DialogueManager.sendItem1(player, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
							player.getInventory().remove(new Item(data.getItem()[0]));
							player.getInventory().add(new Item(data.getNewItem(), 1));
						}
					}
				});
			}
		}
	}),
	/* Saradomin Blessed Sword */
	SARADOMIN_BLESSED_SWORD(new int[] { 12804, 11838 }, 12809, new CreationHandle() {
		@Override
		public void handle(Player player, ItemCreation data) {
			if (meetsRequirements(player, data)) {
				player.start(new ConfirmDialogue(player) {
					@Override
					public void onConfirm() {
						if (meetsRequirements(player, data)) {
							ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
							DialogueManager.sendItem1(player, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
							player.getInventory().remove(new Item(data.getItem()[0]));
							player.getInventory().remove(new Item(data.getItem()[1]));
							player.getInventory().add(new Item(data.getNewItem(), 1));
						}
					}
				});
			}
		}
	}),
	/* Dragon fire shield */
	DRAGONFIRE_SHIELD(new int[] { 1540, 11286 }, 11283, new CreationHandle() {
		@Override
		public void handle(Player player, ItemCreation data) {
			if (meetsRequirements(player, data)) {
				player.start(new ConfirmDialogue(player) {
					@Override
					public void onConfirm() {
						if (meetsRequirements(player, data)) {
							if (!skillRequired(player, Skills.SMITHING, 90)) {
								return;
							}
							ItemDefinition item = GameDefinitionLoader.getItemDef(data.getNewItem());
							DialogueManager.sendItem1(player, "You have created a @dre@" + item.getName() + "</col>.", data.getNewItem());
							player.getInventory().remove(new Item(data.getItem()[0]));
							player.getInventory().remove(new Item(data.getItem()[1]));
							player.getInventory().add(new Item(data.getNewItem(), 1));
						}
					}
				});
			}
		}
	}), ;

	private int[] itemID;
	int newItem;
	private CreationHandle handle;

	private ItemCreation(int[] itemId, int newItem, CreationHandle handle) {
		this.itemID = itemId;
		this.handle = handle;
		this.newItem = newItem;
	}

	public int[] getItem() {
		return itemID;
	}

	public int getNewItem() {
		return newItem;
	}

	public CreationHandle getHandle() {
		return handle;
	}

	public static HashMap<Integer, ItemCreation> creation = new HashMap<>();

	static {
		for (final ItemCreation creation : ItemCreation.values()) {
			if (ItemCreation.creation.put(creation.itemID[0] << 16 | creation.itemID[1], creation) != null) {
				throw new AssertionError("Conflicting keys. Items: [" + creation.itemID[0] + ", " + creation.itemID[1] + "]");
			}

			if (ItemCreation.creation.put(creation.itemID[1] << 16 | creation.itemID[0], creation) != null) {
				throw new AssertionError("Conflicting keys. Items: [" + creation.itemID[0] + ", " + creation.itemID[1] + "]");
			}
		}
	}

	/**
	 * Check if player needs a required skill level
	 * @param player
	 * @param skill
	 * @param level
	 * @return
	 */
	public static boolean skillRequired(Player player, int skill, int level) {
		if (player.getSkill().getLevels()[skill] < level) {
			player.send(new SendRemoveInterfaces());
			DialogueManager.sendStatement(player, "You need a " + Prestige.getSkillName(skill) + " level of " + level + " to do this!");
			return false;
		}
		return true;
	}

	/**
	 * Checks if player meets requirements
	 * @param player
	 * @param data
	 * @return
	 */
	public static boolean meetsRequirements(Player player, ItemCreation data) {
		for (int item : data.getItem()) {
			if (!player.getInventory().hasItemId(item)) {
				player.send(new SendMessage("Nothing interesting happens"));
				return false;
			}
		}
		return true;
	}

	/**
	 * Handles 
	 * @param player
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static boolean handle(Player player, int item1, int item2) {
		ItemCreation data = ItemCreation.creation.get((Integer) (item1 << 16 | item2));

		if (data == null) {
			if (handleGodsword(player, item1, item2)) {
				return true;
			}
			return false;
		}

		data.getHandle().handle(player, data);
		return true;
	}
	
	/**
	 * Handles Godsword creations
	 * @param player
	 * @param use
	 * @param with
	 * @return
	 */
	public static boolean handleGodsword(Player player, int use, int with) {
		Item product = null;
		boolean smithing = false;
		if (isUsedWith(use, with, 11820, 11818)) {
			product = new Item(11794);
		} else if (isUsedWith(use, with, 11822, 11818)) {
			product = new Item(11796);
		} else if (isUsedWith(use, with, 11822, 11820)) {
			product = new Item(11800);
		} else if (isUsedWith(use, with, 11794, 11822)) {
			smithing = true;
			product = new Item(11798);
		} else if (isUsedWith(use, with, 11796, 11820)) {
			smithing = true;
			product = new Item(11798);
		} else if (isUsedWith(use, with, 11800, 11818)) {
			smithing = true;
			product = new Item(11798);
		} else if (isUsedWith(use, with, 11798, 11810)) {
			product = new Item(11802);
		} else if (isUsedWith(use, with, 11798, 11812)) {
			product = new Item(11804);
		} else if (isUsedWith(use, with, 11798, 11814)) {
			product = new Item(11806);
		} else if (isUsedWith(use, with, 11798, 11816)) {
			product = new Item(11808);
		}
		if (smithing) {
			if (player.getLevels()[Skills.SMITHING] < 80) {
				DialogueManager.sendStatement(player, "<col=369>You need a smithing level of @dre@80<col=369> to create godsword blades.");
				return true;
			}
			player.getSkill().addExperience(Skills.SMITHING, 100.0);
		}
		if (product != null) {
			player.getInventory().remove(use, 1);
			player.getInventory().remove(with, 1);
			player.getInventory().add(product);
			DialogueManager.sendItem1(player, "You have created " + Utility.getAOrAn(product.getDefinition().getName().toLowerCase()) + " " + product.getDefinition().getName() + ".", product.getId());
		}
		return product != null;
	}
	
	/**
	 * Items used with
	 * @param use
	 * @param with
	 * @param item1
	 * @param item2
	 * @return
	 */
	private static final boolean isUsedWith(int use, int with, int item1, int item2) {
		return (use == item1 && with == item2) || (use == item2 && with == item1);
	}
	
}