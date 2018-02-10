package com.vencillio.rs2.content.skill.agility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vencillio.core.cache.map.RSObject;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.content.skill.agility.obstacle.Obstacle;
import com.vencillio.rs2.content.skill.agility.obstacle.ObstacleType;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public enum Agility {

	SINGLETON;

	public static final int GNOME_FLAGS = 0b0111_1111;
	public static final int BARBARIAN_FLAGS = 0b0111_1111;
	public static final int WILDERNESS_FLAGS = 0b0001_1111;

	private static final HashMap<Location, Obstacle> obstacles = new HashMap<>();

	public static void declare() {
		try {
			Obstacle[] loaded = new Gson().fromJson(new BufferedReader(new FileReader("./data/def/skills/agility.json")), Obstacle[].class);
			for (Obstacle obstacle : loaded) {
				obstacles.put(obstacle.getStart(), obstacle);
			}
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// Write the obstacles so we don't have to deal with ugly as fuck wall
		// of code.

//		 obstacles.put(new Location(2551, 3554), new Obstacle.ObstacleBuilder(ObstacleType.ROPE_SWING, new Location(2551, 3554), new Location(2551, 3549))
//		 .setExperience(22f)
//		 .setLevel(35)
//		 .setOrdinal(0)
//		 .setNext(new Obstacle.ObstacleBuilder(ObstacleType.ROPE_SWING, new Location(2551, 3554), new Location(2551, 3549)).build())
//		 .build());

		try (FileWriter writer = new FileWriter(new File("./data/def/skills/agility1.json"))) {
			Gson builder = new GsonBuilder().setPrettyPrinting().create();

			writer.write(builder.toJson(obstacles.values()).replaceAll("\\{\n      \"x\"", "\\{ \"x\"").replaceAll(",\n      \"y\"", ", \"y\"").replaceAll(",\n      \"z\"", ", \"z\"").replaceAll("\n    \\},", " \\},"));
		} catch (Exception e) {
		}
	}

	public boolean fireObjectClick(Player player, Location location, RSObject obj) {
		Obstacle obstacle = obstacles.get(player.getLocation());

		if (obstacle == null) {
			return false;
		}

		if (player.getAttributes().get("AGILITY_FLAGS") == null) {
			player.getAttributes().set("AGILITY_FLAGS", 0);
		}

		if (obstacle.getType() == ObstacleType.ROPE_SWING) {
			player.getAttributes().set("AGILITY_OBJ", obj);
		}

		obstacle.execute(player);

		return false;
	}
	
	public static double TICKET_EXPERIENCE = 2.5;

	public static boolean clickButton(Player player, int buttonId) {
		int amount = -1;
		Consumer<Player> onClick = null;
		switch (buttonId) {
		case 32195:
			amount = 1;
			onClick = p -> p.getSkill().addExperience(Skills.AGILITY, TICKET_EXPERIENCE * 1);
			break;
		case 32197:
			amount = 10;
			onClick = p -> p.getSkill().addExperience(Skills.AGILITY, TICKET_EXPERIENCE * 10);
			break;
		case 32198:
			amount = 25;
			onClick = p -> p.getSkill().addExperience(Skills.AGILITY, TICKET_EXPERIENCE * 25);
			break;
		case 32199:
			amount = 100;
			onClick = p -> p.getSkill().addExperience(Skills.AGILITY, TICKET_EXPERIENCE * 100);
			break;
		case 32200:
			amount = 1_000;
			onClick = p -> p.getSkill().addExperience(Skills.AGILITY, TICKET_EXPERIENCE * 1000);
			break;
		case 32190:
			break;
		case 32201:
			break;
		case 32189:
			amount = 800;
			onClick = p -> p.getInventory().addOrCreateGroundItem(2997, 1, true);
			break;
		}
		if (amount > -1 && onClick != null) {
			if (player.getInventory().hasItemAmount(2996, amount)) {
				player.getInventory().remove(2996, amount);
				onClick.accept(player);
				player.send(new SendRemoveInterfaces());
				return true;
			} else {
				player.send(new SendMessage("You do not have enough agility tickets to purchase that."));
				return true;
			}
		}
		return false;
	}
}