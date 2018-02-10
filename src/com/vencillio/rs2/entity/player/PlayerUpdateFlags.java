package com.vencillio.rs2.entity.player;

import java.util.BitSet;

import com.vencillio.rs2.content.PlayerTitle;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.UpdateFlags;
import com.vencillio.rs2.entity.item.Item;

public final class PlayerUpdateFlags {

	public static byte getTeam(Player p) {
		Item cape = p.getEquipment().getItems()[1];

		if ((cape != null) && (cape.getId() >= 4315) && (cape.getId() <= 4413)) {
			return (byte) (cape.getId() - 4315 + 1);
		}

		return 0;
	}

	private final BitSet set = new BitSet(30);
	private final byte rights;
	private final int chatColor;
	private final int chatEffects;
	private final byte[] chatText;
	private final byte gender;
	private final int[] appearance;
	private final byte[] colors;
	private final short npcAppearanceId;
	private final byte primaryDirection;
	private final byte secondaryDirection;
	private final byte hp;
	private final byte maxHP;
	private final Location forceStart;
	private final Location forceEnd;
	private final short forceSpeed1;
	private final short forceSpeed2;
	private final byte forceDirection;
	private final short x;
	private final short y;
	private final short z;
	private final short regionX;
	private final short regionY;
	private final short regionZ;
	private final byte headicon;
	private final short[] equipment;
	private final short standEmote;
	private final short standTurnEmote;
	private final short walkEmote;
	private final short turn180Emote;
	private final short turn90CWEmote;
	private final short turn90CCWEmote;
	private final short runEmote;
	private final String username;
	private final byte combatLevel;
	private final byte skullIcon;
	private final String forceChatMessage;
	private final short animationId;
	private final byte animationDelay;
	private final int entityFaceIndex;
	private final short faceX;
	private final short faceY;
	private final byte damage;
	private final byte damage2;
	private final byte hitType;
	private final byte hitType2;
	private final short graphicId;
	private final byte graphicHeight;
	private final byte graphicDelay;
	private final byte team;
	private final long usernameToLong;
	private final byte hitUpdateCombatType;
	private final byte hitUpdateCombatType2;
	
	private PlayerTitle playerTitle;

	public PlayerUpdateFlags(Player player) {
		UpdateFlags u = player.getUpdateFlags();

		set.set(0, player.isVisible());
		set.set(1, player.isChatUpdateRequired());
		set.set(2, player.isAppearanceUpdateRequired());
		set.set(3, u.isUpdateRequired());
		set.set(4, u.isForceChatUpdate());
		set.set(5, u.isGraphicsUpdateRequired());
		set.set(6, u.isAnimationUpdateRequired());
		set.set(7, u.isHitUpdate());
		set.set(8, u.isHitUpdate2());
		set.set(9, u.isEntityFaceUpdate());
		set.set(10, u.isFaceToDirection());
		set.set(11, player.needsPlacement());
		set.set(12, player.isResetMovementQueue());
		set.set(13, u.isForceMovement());

		team = getTeam(player);

		x = ((short) player.getLocation().getX());
		y = ((short) player.getLocation().getY());
		z = ((short) player.getLocation().getZ());

		regionX = ((short) player.getCurrentRegion().getX());
		regionY = ((short) player.getCurrentRegion().getY());
		regionZ = ((short) player.getCurrentRegion().getZ());

		usernameToLong = player.getUsernameToLong();
		playerTitle = player.getPlayerTitle();

		set.get(0);

		if (set.get(3)) {
			if (set.get(6)) {
				animationId = ((short) u.getAnimationId());
				animationDelay = ((byte) u.getAnimationDelay());
			} else {
				animationId = 0;
				animationDelay = 0;
			}

			if (set.get(5)) {
				graphicId = ((short) u.getGraphic().getId());
				graphicHeight = ((byte) u.getGraphic().getHeight());
				graphicDelay = ((byte) u.getGraphic().getDelay());
			} else {
				graphicId = 0;
				graphicHeight = 0;
				graphicDelay = 0;
			}

			if (set.get(10)) {
				faceX = ((short) u.getFace().getX());
				faceY = ((short) u.getFace().getY());
			} else {
				faceX = 0;
				faceY = 0;
			}

			if ((set.get(7)) || (set.get(8))) {
				hp = ((byte) player.getLevels()[3]);
				maxHP = ((byte) player.getMaxLevels()[3]);
				damage = ((byte) u.getDamage());
				damage2 = ((byte) u.getDamage2());
				hitType = ((byte) u.getHitType());
				hitType2 = ((byte) u.getHitType2());
				hitUpdateCombatType = (byte) u.getHitUpdateCombatType();
				hitUpdateCombatType2 = (byte) u.getHitUpdateCombatType2();
			} else {
				hp = 0;
				maxHP = 0;
				damage = 0;
				damage2 = 0;
				hitType = 0;
				hitType2 = 0;
				hitUpdateCombatType = 0;
				hitUpdateCombatType2 = 0;
			}

			if (set.get(4))
				forceChatMessage = u.getForceChatMessage();
			else {
				forceChatMessage = null;
			}

			if (set.get(1)) {
				chatText = player.getChatText();
				chatColor = player.getChatColor();
				chatEffects = player.getChatEffects();
			} else {
				chatText = null;
				chatColor = 0;
				chatEffects = 0;
			}

			entityFaceIndex = u.getEntityFaceIndex();

			if (set.get(13)) {
				forceStart = player.getMovementHandler().getForceStart();
				forceEnd = player.getMovementHandler().getForceEnd();
				forceSpeed1 = (short) player.getMovementHandler().getForceSpeed1();
				forceSpeed2 = (short) player.getMovementHandler().getForceSpeed2();
				forceDirection = (byte) player.getMovementHandler().getForceDirection();
			} else {
				forceStart = null;
				forceEnd = null;
				forceSpeed1 = 0;
				forceSpeed2 = 0;
				forceDirection = 0;
			}
		} else {
			animationId = 0;
			animationDelay = 0;
			graphicId = 0;
			graphicHeight = 0;
			graphicDelay = 0;
			forceStart = null;
			forceEnd = null;
			forceSpeed1 = 0;
			forceSpeed2 = 0;
			forceDirection = 0;
			faceX = 0;
			faceY = 0;
			hp = 0;
			maxHP = 0;
			damage = 0;
			damage2 = 0;
			hitType = 0;
			hitType2 = 0;
			forceChatMessage = null;
			entityFaceIndex = 0;
			chatText = null;
			chatColor = 0;
			chatEffects = 0;
			hitUpdateCombatType = 0;
			hitUpdateCombatType2 = 0;
		}

		primaryDirection = ((byte) player.getMovementHandler().getPrimaryDirection());
		secondaryDirection = ((byte) player.getMovementHandler().getSecondaryDirection());

		equipment = new short[14];
		for (int i = 0; i < equipment.length; i++) {
			if (player.getEquipment().getItems()[i] != null) {
				equipment[i] = ((short) player.getEquipment().getItems()[i].getId());
			}
		}

		npcAppearanceId = ((short) player.getNpcAppearanceId());

		rights = ((byte) player.getRights());
		combatLevel = ((byte) player.getSkill().calcCombatLevel());
		headicon = ((byte) player.getPrayer().getHeadicon());

		standEmote = ((short) player.getAnimations().getStandEmote());
		runEmote = ((short) player.getAnimations().getRunEmote());
		standTurnEmote = ((short) player.getAnimations().getStandTurnEmote());
		walkEmote = ((short) player.getAnimations().getWalkEmote());
		turn180Emote = ((short) player.getAnimations().getTurn180Emote());
		turn90CWEmote = ((short) player.getAnimations().getTurn90CWEmote());
		turn90CCWEmote = ((short) player.getAnimations().getTurn90CCWEmote());

		username = player.getDisplay();
		skullIcon = ((byte) player.getSkulling().getSkullIcon());

		gender = player.getGender();

		colors = (player.getColors().clone());
		appearance = (player.getAppearance().clone());
	}

	public byte getAnimationDelay() {
		return animationDelay;
	}

	public short getAnimationId() {
		return animationId;
	}

	public int[] getAppearance() {
		return appearance;
	}

	public int getChatColor() {
		return chatColor;
	}

	public int getChatEffects() {
		return chatEffects;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public byte[] getColors() {
		return colors;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public byte getDamage() {
		return damage;
	}

	public byte getDamage2() {
		return damage2;
	}

	public int getEntityFaceIndex() {
		return entityFaceIndex;
	}

	public short[] getEquipment() {
		return equipment;
	}

	public short getFaceX() {
		return faceX;
	}

	public short getFaceY() {
		return faceY;
	}

	public String getForceChatMessage() {
		return forceChatMessage;
	}

	public int getGender() {
		return gender;
	}

	public byte getGraphicDelay() {
		return graphicDelay;
	}

	public byte getGraphicHeight() {
		return graphicHeight;
	}

	public short getGraphicId() {
		return graphicId;
	}

	public int getHeadicon() {
		return headicon;
	}

	public byte getHitType() {
		return hitType;
	}

	public byte getHitUpdateType() {
		return hitUpdateCombatType;
	}
	
	public byte getHitUpdateType2() {
		return hitUpdateCombatType2;
	}

	public byte getHitType2() {
		return hitType2;
	}

	public byte getHp() {
		return hp;
	}

	public Location getLocation() {
		return new Location(x, y, z);
	}

	public byte getMaxHP() {
		return maxHP;
	}

	public int getNpcAppearanceId() {
		return npcAppearanceId;
	}

	public int getPrimaryDirection() {
		return primaryDirection;
	}

	public Location getRegion() {
		return new Location(regionX, regionY, regionZ);
	}

	public short getRegionX() {
		return regionX;
	}

	public short getRegionY() {
		return regionY;
	}

	public short getRegionZ() {
		return regionZ;
	}

	public int getRights() {
		return rights;
	}

	public int getRunEmote() {
		return runEmote;
	}

	public int getSecondaryDirection() {
		return secondaryDirection;
	}

	public BitSet getSet() {
		return set;
	}

	public int getSkullIcon() {
		return skullIcon;
	}

	public int getStandEmote() {
		return standEmote;
	}

	public int getStandTurnEmote() {
		return standTurnEmote;
	}

	public byte getTeam() {
		return team;
	}

	public int getTurn180Emote() {
		return turn180Emote;
	}

	public int getTurn90CCWEmote() {
		return turn90CCWEmote;
	}

	public int getTurn90CWEmote() {
		return turn90CWEmote;
	}

	public String getUsername() {
		return username;
	}

	public long getUsernameToLong() {
		return usernameToLong;
	}

	public int getWalkEmote() {
		return walkEmote;
	}

	public Location getForceStart() {
		return forceStart;
	}

	public Location getForceEnd() {
		return forceEnd;
	}

	public short getForceSpeed1() {
		return forceSpeed1;
	}

	public short getForceSpeed2() {
		return forceSpeed2;
	}

	public byte getForceDirection() {
		return forceDirection;
	}

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}

	public boolean isActive() {
		return set.get(0);
	}

	public boolean isAnimationUpdateRequired() {
		return set.get(6);
	}

	public boolean isAppearanceUpdateRequired() {
		return set.get(2);
	}

	public boolean isChatUpdateRequired() {
		return set.get(1);
	}

	public boolean isEntityFaceUpdate() {
		return set.get(9);
	}

	public boolean isFaceToDirection() {
		return set.get(10);
	}

	public boolean isForceChatUpdate() {
		return set.get(4);
	}

	public boolean isGraphicsUpdateRequired() {
		return set.get(5);
	}

	public boolean isHitUpdate() {
		return set.get(7);
	}

	public boolean isHitUpdate2() {
		return set.get(8);
	}

	public boolean isPlacement() {
		return set.get(11);
	}

	public boolean isResetMovementQueue() {
		return set.get(12);
	}

	public boolean isUpdateRequired() {
		return set.get(3);
	}

	public boolean isVisible() {
		return set.get(0);
	}

	public PlayerTitle getPlayerTitle() {
		return playerTitle;
	}

	public boolean isForceMoveMask() {
		return set.get(13);
	}

}