package com.vencillio.rs2.content;

import java.util.HashMap;

import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.mob.MobConstants;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;

public class PlayerProperties {

	public static final String ATTRIBUTE_KEY = "PROPERTY_";

	private final Player player;

	public PlayerProperties(Player player) {
		this.player = player;
	}

	public void addProperty(String attributeSuffix, String name, int increment) {
		attributeSuffix = attributeSuffix.trim().toUpperCase().replaceAll(" ", "_");
		name = name.trim().toUpperCase().replaceAll(" ", "_");
		int current = player.getAttributes().getInt(ATTRIBUTE_KEY + attributeSuffix + "_" + name);

		if (!player.getAttributes().isSet(ATTRIBUTE_KEY + attributeSuffix + "_" + name)) {
			current = 0;
		}

		player.getAttributes().set(ATTRIBUTE_KEY + attributeSuffix + "_" + name, current + increment);
	}
	
	public void addProperty(String property, int increment) {
		addProperty("PLAYER", property, increment);
	}

	public void addProperty(Mob mob, int increment) {
		addProperty("MOB", mob.getDefinition().getName(), increment);
		player.send(new SendMessage("Your " + mob.getDefinition().getName() + " kill count is: @red@" + Utility.format(player.getProperties().getPropertyValue("MOB_" + mob.getDefinition().getName())) + "</col>."));
	}

	public void addProperty(Item item, int increment) {
		addProperty("ITEM", item.getDefinition().getName(), increment);
	}
	
	public int getPropertyValue(String property) {
		property = property.trim().toUpperCase().replaceAll(" ", "_");
		
		int value = 0;	
		
		for (Object attribute : player.getAttributes().getAttributes().keySet()) {
			if (String.valueOf(attribute).startsWith(ATTRIBUTE_KEY + property)) {
				value = player.getAttributes().getInt(attribute);
			}
		}
		
		return value;
	}

	public HashMap<String, Integer> getPropertyValues(String property) {
		property = property.trim().toUpperCase().replaceAll(" ", "_");

		HashMap<String, Integer> properties = new HashMap<>();

		for (Object attribute : player.getAttributes().getAttributes().keySet()) {
			if (String.valueOf(attribute).startsWith(ATTRIBUTE_KEY + property + "_")) {
				properties.put(String.valueOf(attribute).replace(ATTRIBUTE_KEY + property + "_", ""), player.getAttributes().getInt(attribute));
			}
		}

		return properties;
	}

	public void setDefaults() {
		for (int npc : MobConstants.LOGGED_NPCS) {
			player.getProperties().addProperty("MOB", GameDefinitionLoader.getNpcDefinition(npc).getName(), 0);
		}
		
		player.getProperties().addProperty("BARROWS", "BARROWS_CHESTS", 0);
	}
}