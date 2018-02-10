package com.vencillio.rs2.entity.item.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.vencillio.core.task.TaskQueue;
import com.vencillio.core.task.impl.WalkToTask;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.item.Item;
import com.vencillio.rs2.entity.pathfinding.StraightPathFinder;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.controllers.ControllerManager;
import com.vencillio.rs2.entity.player.net.out.impl.SendGroundItem;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveGroundItem;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class LocalGroundItems {
	private final Player player;
	private Deque<GroundItem> loaded = new ArrayDeque<GroundItem>();
	private Deque<GroundItem> adding = new ArrayDeque<GroundItem>();
	private Deque<GroundItem> removing = new ArrayDeque<GroundItem>();
	private boolean hasLoaded = true;

	public LocalGroundItems(Player player) {
		this.player = player;
	}

	public void add(GroundItem groundItem) {
		synchronized (adding) {
			adding.add(groundItem);
		}
	}
	
	public void dropFull(int id, int slot) {
		Item drop = player.getInventory().get(slot);
		GroundItemHandler.add(drop, new Location(player.getLocation()), player, player.ironPlayer() ? player : null);
		player.getInventory().clear(slot);
	}

	public void drop(int id, int slot) {
		if ((player.getController().equals(ControllerManager.WILDERNESS_CONTROLLER)) && (player.getCombat().inCombat())) {
			if (GameDefinitionLoader.getHighAlchemyValue(id) > 1000) {
				player.getClient().queueOutgoingPacket(new SendMessage("You cannot drop this item while in combat here."));
			}
		} else if ((player.getController().equals(ControllerManager.DUELING_CONTROLLER))) {
			return;
		}

		Item drop = player.getInventory().get(slot);

		GroundItemHandler.add(drop, new Location(player.getLocation()), player, player.ironPlayer() ? player : null);
		
		player.getInventory().clear(slot);
		player.getCombat().reset();
		if (player.getInterfaceManager().getMain() != -1) {
			player.send(new SendRemoveInterfaces());
		}
	}

	public boolean drop(Item item, Location location) {
		return GroundItemHandler.add(item, location, player, player.ironPlayer() ? player : null);
	}

	private void load() {
		synchronized (GroundItemHandler.getActive()) {
			for (Iterator<?> g = GroundItemHandler.getActive().iterator(); g.hasNext();) {
				GroundItem i = (GroundItem) g.next();

				if (GroundItemHandler.visible(player, i)) {
					synchronized (adding) {
						adding.add(i);
					}
				}
			}
		}

		hasLoaded = true;
	}

	public void onRegionChange() {
		hasLoaded = false;

		synchronized (adding) {
			adding.clear();
		}

		synchronized (loaded) {
			GroundItem g;
			while ((g = loaded.poll()) != null) {
				player.getClient().queueOutgoingPacket(new SendRemoveGroundItem(player, g));
			}
		}
	}

	public void pickup(final int x, final int y, final int id) {
		GroundItem g = GroundItemHandler.getGroundItem(id, x, y, player.getLocation().getZ(), player.getUsername(), false);
		if ((g == null) || (!GroundItemHandler.exists(g))) {
			player.getMovementHandler().reset();
			return;
		}
		TaskQueue.queue(new WalkToTask(player, g) {
			@Override
			public void onDestination() {
				GroundItem g = GroundItemHandler.getGroundItem(id, x, y, player.getLocation().getZ(), player.getUsername(), false);

				if ((g == null) || (!g.exists())) {
					player.getMovementHandler().reset();
					stop();
					return;
				}

				if (player.ironPlayer() && !player.getUsername().equals(g.include())) {
					player.send(new SendMessage("You cannot pick up this item as an iron man."));
					player.getMovementHandler().reset();
					stop();
					return;
				}

				if (!StraightPathFinder.isInteractionPathClear(player.getLocation(), g.getLocation())) {
					player.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
					stop();
					return;
				}
				
				if ((player.getInventory().hasSpaceFor(g.getItem())) && (GroundItemHandler.remove(g))) {
					player.getInventory().add(g.getItem());
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to pick that up."));
				}
			}
		});
	}

	public void process() {
		if (!hasLoaded) {
			load();
		}

		GroundItem g = null;

		synchronized (adding) {
			while ((g = adding.poll()) != null) {
				player.getClient().queueOutgoingPacket(new SendGroundItem(player, g));
				loaded.add(g);
			}
		}

		synchronized (removing) {
			while ((g = removing.poll()) != null)
				player.getClient().queueOutgoingPacket(new SendRemoveGroundItem(player, g));
		}
	}

	public void remove(GroundItem groundItem) {
		synchronized (removing) {
			removing.add(groundItem);
		}
	}

	public boolean stack(GroundItem g) {
		if (!g.getItem().getDefinition().isStackable()) {
			return false;
		}

		GroundItem onGround = GroundItemHandler.getNonGlobalGroundItem(g.getItem().getId(), g.getLocation().getX(), g.getLocation().getY(), g.getLocation().getZ(), g.getLongOwnerName());

		if (onGround == null) {
			return false;
		}

		if (onGround.isGlobal()) {
			return false;
		}
		player.getClient().queueOutgoingPacket(new SendRemoveGroundItem(player, onGround));
		onGround.getItem().add(g.getItem().getAmount());
		player.getClient().queueOutgoingPacket(new SendGroundItem(player, onGround));
		return true;
	}
}
