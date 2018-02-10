package com.vencillio.rs2.content;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.content.combat.Combat.CombatTypes;
import com.vencillio.rs2.entity.Entity;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.PlayerConstants;
import com.vencillio.rs2.entity.player.controllers.Controller;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSidebarInterface;

public class EasterRing {
	
	public static class EasterRingController extends Controller {
		@Override
		public boolean allowMultiSpells() {
			return false;
		}

		@Override
		public boolean allowPvPCombat() {
			return false;
		}

		@Override
		public boolean canAttackNPC() {
			return false;
		}

		@Override
		public boolean canAttackPlayer(Player p, Player p2) {
			return false;
		}

		@Override
		public boolean canClick() {
			return false;
		}

		@Override
		public boolean canDrink(Player p) {
			return false;
		}

		@Override
		public boolean canEat(Player p) {
			return false;
		}

		@Override
		public boolean canEquip(Player p, int id, int slot) {
			return false;
		}

		@Override
		public boolean canLogOut() {
			return false;
		}

		@Override
		public boolean canMove(Player p) {
			return false;
		}

		@Override
		public boolean canSave() {
			return false;
		}

		@Override
		public boolean canTalk() {
			return true;
		}

		@Override
		public boolean canTeleport() {
			return false;
		}

		@Override
		public boolean canTrade() {
			return false;
		}

		@Override
		public boolean canUseCombatType(Player p, CombatTypes type) {
			return false;
		}

		@Override
		public boolean canUsePrayer(Player p, int id) {
			return false;
		}

		@Override
		public boolean canUseSpecialAttack(Player p) {
			return false;
		}

		@Override
		public Location getRespawnLocation(Player player) {
			return PlayerConstants.HOME;
		}

		@Override
		public boolean isSafe(Player player) {
			return false;
		}

		@Override
		public void onControllerInit(Player p) {
		}

		@Override
		public void onDeath(Player p) {
		}

		@Override
		public void onDisconnect(Player p) {
		}

		@Override
		public void onTeleport(Player p) {
		}

		@Override
		public void tick(Player p) {
		}

		@Override
		public String toString() {
			return "Easter ring";
		}

		@Override
		public boolean transitionOnWalk(Player p) {
			return true;
		}

		@Override
		public void onKill(Player player, Entity killed) {
		}

		@Override
		public boolean canUnequip(Player player) {		
			return true;
		}

		@Override
		public boolean canDrop(Player player) {
			return false;
		}
	}

	public static final int EASTER_RING_ID = 7927;
	public static final int UNMORPH_INTERFACE_ID = 6014;

	public static final Controller EASTER_RING_CONTROLLER = new EasterRingController();

	public static final int[] EGG_IDS = { 3689, 3690, 3691, 3692, 3693, 3694 };

	public static void cancel(Player player) {
		player.setController(ControllerManager.DEFAULT_CONTROLLER);
		player.setNpcAppearanceId((short) -1);
		player.setAppearanceUpdateRequired(true);

		int[] tabs = (int[]) player.getAttributes().get("tabs");

		for (int i = 0; i < tabs.length; i++) {
			player.getClient().queueOutgoingPacket(new SendSidebarInterface(i, tabs[i]));
		}

		player.getClient().queueOutgoingPacket(new SendMessage("You morph back into a human."));
	}

	public static final boolean canEquip(Player player) {
		if (!player.getController().equals(ControllerManager.DEFAULT_CONTROLLER)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot do this here."));
			return false;
		}

		return true;
	}

	public static void init(Player player) {
		player.setNpcAppearanceId((short) EGG_IDS[Utility.randomNumber(EGG_IDS.length)]);
		player.setController(EASTER_RING_CONTROLLER);
		player.getMovementHandler().reset();

		int[] tabs = player.getInterfaceManager().getTabs().clone();

		player.getAttributes().set("tabs", tabs);

		for (int i = 0; i < tabs.length; i++) {
			player.getClient().queueOutgoingPacket(new SendSidebarInterface(i, -1));
		}
		player.getClient().queueOutgoingPacket(new SendSidebarInterface(3, 6014));
		player.getClient().queueOutgoingPacket(new SendMessage("You morph into an Easter egg."));
	}
}
