package com.vencillio.rs2.content.dialogue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class OptionDialogue extends Dialogue {

	private String[] lines;

	private List<Consumer<Player>> consumers = new ArrayList<>();

	public OptionDialogue(String option1, Consumer<Player> consumer1, String option2, Consumer<Player> consumer2) {
		lines = new String[] { option1, option2 };
		consumers.add(consumer1);
		consumers.add(consumer2);
	}

	public OptionDialogue(String option1, Consumer<Player> consumer1, String option2, Consumer<Player> consumer2, String option3, Consumer<Player> consumer3) {
		lines = new String[] { option1, option2, option3 };
		consumers.add(consumer1);
		consumers.add(consumer2);
		consumers.add(consumer3);
	}

	public OptionDialogue(String option1, Consumer<Player> consumer1, String option2, Consumer<Player> consumer2, String option3, Consumer<Player> consumer3, String option4, Consumer<Player> consumer4) {
		lines = new String[] { option1, option2, option3, option4 };
		consumers.add(consumer1);
		consumers.add(consumer2);
		consumers.add(consumer3);
		consumers.add(consumer4);
	}

	public OptionDialogue(String option1, Consumer<Player> consumer1, String option2, Consumer<Player> consumer2, String option3, Consumer<Player> consumer3, String option4, Consumer<Player> consumer4, String option5, Consumer<Player> consumer5) {
		lines = new String[] { option1, option2, option3, option4, option5 };
		consumers.add(consumer1);
		consumers.add(consumer2);
		consumers.add(consumer3);
		consumers.add(consumer4);
		consumers.add(consumer5);
	}

	@Override
	public void execute() {
		switch (getNext()) {
		case 0:
			getPlayer().send(new SendRemoveInterfaces());
			DialogueManager.sendOption(getPlayer(), lines);
			break;
		default:
			consumers.get(getNext() - 1).accept(getPlayer());
			setNext(-1);
			break;
		}
	}

	@Override
	public boolean clickButton(int id) {
		switch (getOption()) {
		case 0:
			switch (id) {
			case 9157:
			case 9167:
			case 9178:
			case 9190:
				setNext(1);
				execute();
				return true;
			case 9158:
			case 9168:
			case 9179:
			case 9191:
				setNext(2);
				execute();
				return true;
			case 9169:
			case 9180:
			case 9192:
				setNext(3);
				execute();
				return true;
			case 9181:
			case 9193:
				setNext(4);
				execute();
				return true;
			case 9194:
				setNext(5);
				execute();
				return true;
			}
			return false;
		}
		return true;
	}
}