package com.vencillio.tools;

import com.vencillio.core.cache.map.ObjectDef;
import com.vencillio.core.definitions.ItemDefinition;
import com.vencillio.core.definitions.NpcCombatDefinition;
import com.vencillio.core.definitions.NpcDefinition;
import com.vencillio.core.util.GameDefinitionLoader;
import com.vencillio.rs2.content.io.PlayerSave;
import com.vencillio.rs2.content.io.PlayerSaveUtil;
import com.vencillio.rs2.content.shopping.Shop;
import com.vencillio.rs2.content.skill.Skills;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.mob.Mob;
import com.vencillio.rs2.entity.object.ObjectManager;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendSystemBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

//import com.ew.utils.ObjectExamines;//delete?
//import com.ew.utils.PriceManager;//need?
//import com.ew.utils.ProfanityFilter;//delete?
//import com.ew.utils.WeightManager;//delete?

/**
 * 
 * @author Cody
 *
 */

public class ControlPanel extends JFrame {

	private static final long serialVersionUID = -421120533203117621L;

	private Player player;

	private final JPanel contentPane;

	private static boolean showingOffline;
	private boolean showingObjects = false;
	private boolean showingItems = false;

	private static DefaultListModel<String> playersModel = new DefaultListModel<>();
	private static DefaultListModel<String> loggingModel = new DefaultListModel<>();
	private static DefaultListModel<String> hsPlayersList = new DefaultListModel<>();
	private static DefaultListModel<String> npcListModel = new DefaultListModel<>();

	private final JList<String> playerList = new JList<>(playersModel);
	private final JList<String> logger = new JList<>(loggingModel);
	private final JList<String> hsList = new JList<>(hsPlayersList);
	private final JList<String> npcList = new JList<>(npcListModel);

	private final JTextField usernameField = new JTextField();
	private final JTextField hoursField = new JTextField();

	private static final String IMAGE_PATH = "./data/icons/";
	private JTextField textField;
	private JTextField announcementsTextField;
	public PrintStream SYSTEM_OUT = null;
	public JScrollPane CONSOL_SCROLLER, jScrollPane4;

	public static void init() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final ControlPanel frame = new ControlPanel();
					frame.setVisible(true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static ImageIcon resize(String path) {
		ImageIcon imageIcon = new ImageIcon(path);
		Image image = imageIcon.getImage();
		Image newimg = image.getScaledInstance(36,25, Image.SCALE_SMOOTH);
		return new ImageIcon(newimg);
	}

	public ControlPanel() {
		setResizable(false);
		setTitle("Control Panel");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 625, 472);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		final JPanel mainMenu = new JPanel();
		tabbedPane.addTab("Main menu", null, mainMenu, null);
		mainMenu.setLayout(null);

		final JLabel playersText = new JLabel("Players: ");
		playersText.setHorizontalAlignment(SwingConstants.CENTER);
		playersText.setBounds(24, 5, 94, 14);
		mainMenu.add(playersText);

		final JLabel lblLogger = new JLabel("Logger:");
		lblLogger.setBounds(145, 288, 46, 14);
		mainMenu.add(lblLogger);

		final JScrollPane playerScrollList = new JScrollPane();
		playerScrollList.setBounds(14, 57, 121, 327);
		mainMenu.add(playerScrollList);
		playerList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				usernameField.setText(playerList.getSelectedValue().replaceAll(" - Lobby", ""));
			}
		});
		playerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				usernameField.setText(playerList.getSelectedValue());
			}
		});

		playerScrollList.setViewportView(playerList);

		final JButton ipBan = new JButton("IP Ban");
		ipBan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully Permanently IP-Banned " + usernameField.getText());
					new SendSystemBan().execute(player.getClient());
					PlayerSaveUtil.setIPBanned(player);
					player.logout(true);
				}
			}
		});
		ipBan.setBounds(145, 57, 114, 30);
		mainMenu.add(ipBan);

		final JButton permBan = new JButton("Perm Ban");
		permBan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully permanently banned " + usernameField.getText());
					player.setBanned(true);
					player.setBanLength(-1);
					PlayerSave.save(player);
					player.logout(true);
				}
			}
		});
		permBan.setBounds(145, 112, 114, 30);
		mainMenu.add(permBan);

		final JButton ipMute = new JButton("IP Mute");
		ipMute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully permanently IPMuted "+usernameField.getText());
					PlayerSaveUtil.setIPMuted(player);
					player.setMuted(true);
					PlayerSave.save(player);
				}
			}
		});
		ipMute.setBounds(317, 57, 114, 30);
		mainMenu.add(ipMute);

		final JButton giveAdmin = new JButton("Give Admin");
		giveAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully promoted "+usernameField.getText()+" to administrator.");
					player.setRights(2);
					PlayerSave.save(player);
				}
			}
		});
		giveAdmin.setBounds(480, 57, 114, 30);
		mainMenu.add(giveAdmin);

		final JButton giveMod = new JButton("Give Mod");
		giveMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully promoted "+usernameField.getText()+" to moderator.");
					player.setRights(1);
					PlayerSave.save(player);
				}
			}
		});
		giveMod.setBounds(480, 112, 114, 30);
		mainMenu.add(giveMod);

		final JButton tempBan = new JButton("Temp ban");
		tempBan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(true)) {
					logAction("Punishments", "Successfully banned "+usernameField.getText()+" for "+getGrammar());
					player.setBanLength(System.currentTimeMillis() + Integer.parseInt(hoursField.getText())* 60 * 60 * 1000);
					PlayerSave.save(player);
				}
			}
		});
		tempBan.setBounds(145, 166, 114, 30);
		mainMenu.add(tempBan);

		final JButton demotePlayer = new JButton("Demote");
		demotePlayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully demoted "+usernameField.getText()+" to regular player.");
					player.setRights(0);
					PlayerSave.save(player);
				}
			}
		});
		demotePlayer.setBounds(480, 166, 114, 30);
		mainMenu.add(demotePlayer);

		final JButton permMute = new JButton("Perm Mute");
		permMute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(false)) {
					logAction("Punishments", "Successfully permanently muted "+usernameField.getText());
					player.setMuted(true);
					player.setMuteLength(-1);
					PlayerSave.save(player);
				}
			}
		});
		permMute.setBounds(317, 112, 114, 30);
		mainMenu.add(permMute);

		final JButton tempMute = new JButton("Temp Mute");
		tempMute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkAll(true)) {
					logAction("Punishments", "Successfully muted "+usernameField.getText()+" for "+getGrammar());
					player.setMuteLength(System.currentTimeMillis() + Integer.parseInt(hoursField.getText())* 60 * 60 * 1000);
					PlayerSave.save(player);
				}
			}
		});
		tempMute.setBounds(317, 166, 114, 30);
		mainMenu.add(tempMute);

		for (int i = 0; i < mainMenu.getComponents().length; i++) { //needs to be above textField or they become unclickable.
			mainMenu.getComponents()[i].setFocusable(false);
		}

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(145, 304, 449, 80);
		mainMenu.add(scrollPane);

		scrollPane.setViewportView(logger);
		final JButton btnShowOffline = new JButton("Include offline");

		final JLabel lblControlPanelV = new JLabel("Control Panel V3 by Cody");
		lblControlPanelV.setFont(new Font("Sitka Small", Font.PLAIN, 30));
		lblControlPanelV.setBounds(166, 5, 416, 41);
		mainMenu.add(lblControlPanelV);

		final JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(145, 263, 449, 14);
		mainMenu.add(progressBar);
		progressBar.setForeground(Color.ORANGE);

		final JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(145, 226, 79, 14);
		mainMenu.add(lblUsername);

		usernameField.setBounds(212, 223, 193, 20);
		mainMenu.add(usernameField);
		usernameField.setColumns(10);

		final JLabel lblHoursifApplicable = new JLabel("Hours (if applicable):");
		lblHoursifApplicable.setBounds(415, 226, 130, 14);
		mainMenu.add(lblHoursifApplicable);

		hoursField.setBounds(538, 223, 56, 20);
		mainMenu.add(hoursField);
		hoursField.setColumns(10);

		final JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Highscores", null, panel_2, null);
		panel_2.setLayout(null);


		final JButton button = new JButton("Include offline");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showingOffline = !showingOffline;
				updateList();
				logAction("Player Manager", showingOffline ? "Showing all players, Number of accounts registered: "
						+ new File("./data/characters/details/").list().length:
							"Only showing online players, Total players online: "+World.getActivePlayers());
				btnShowOffline.setText(showingOffline ? "Only online" : "Include offline");
				button.setText(showingOffline ? "Only online" : "Include offline");
			}
		});
		button.setBounds(12, 23, 123, 23);
		panel_2.add(button);

		btnShowOffline.setFocusable(false);
		btnShowOffline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showingOffline = !showingOffline;
				updateList();
				if(World.getPlayers() != null && new File("./data/characters/details/").list() != null)
				logAction("Player Manager", showingOffline ? "Showing all players, Number of accounts registered: "
						+ new File("./data/characters/details/").list().length:
							"Only showing online players, Total players online: "+World.getActivePlayers());
				else
				logAction("Player Manager", "All players are null");
				btnShowOffline.setText(showingOffline ? "Only online" : "Include offline");
				button.setText(showingOffline ? "Only online" : "Include offline");
			}
		});
		btnShowOffline.setBounds(12, 23, 123, 23);
		mainMenu.add(btnShowOffline);

		final JLabel label = new JLabel("Players: ");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(24, 5, 94, 14);
		panel_2.add(label);

		final JLabel attackLabel = new JLabel("1");
		attackLabel.setIcon(resize(IMAGE_PATH+"Attack.png"));
		attackLabel.setBounds(177, 23, 76, 29);
		panel_2.add(attackLabel);

		final JLabel constitutionLabel = new JLabel("10");
		constitutionLabel.setIcon(resize(IMAGE_PATH+"Constitution.png"));
		constitutionLabel.setBounds(241, 23, 76, 29);
		panel_2.add(constitutionLabel);

		final JLabel miningLabel = new JLabel("1");
		miningLabel.setIcon(resize(IMAGE_PATH+"Mining.png"));
		miningLabel.setBounds(307, 23, 76, 29);
		panel_2.add(miningLabel);

		final JLabel strengthLabel = new JLabel("1");
		strengthLabel.setIcon(resize(IMAGE_PATH+"Strength.png"));
		strengthLabel.setBounds(177, 62, 76, 29);
		panel_2.add(strengthLabel);

		final JLabel agilityLabel = new JLabel("1");
		agilityLabel.setIcon(resize(IMAGE_PATH+"Agility.png"));
		agilityLabel.setBounds(241, 62, 76, 29);
		panel_2.add(agilityLabel);

		final JLabel smithingLabel = new JLabel("1");
		smithingLabel.setIcon(resize(IMAGE_PATH+"Smithing.png"));
		smithingLabel.setBounds(307, 62, 76, 29);
		panel_2.add(smithingLabel);

		final JLabel defenceLabel = new JLabel("1");
		defenceLabel.setIcon(resize(IMAGE_PATH+"Defence.png"));
		defenceLabel.setBounds(177, 102, 76, 29);
		panel_2.add(defenceLabel);

		final JLabel herbloreLabel = new JLabel("1");
		herbloreLabel.setIcon(resize(IMAGE_PATH+"Herblore.png"));
		herbloreLabel.setBounds(241, 102, 76, 29);
		panel_2.add(herbloreLabel);

		final JLabel fishingLabel = new JLabel("1");
		fishingLabel.setIcon(resize(IMAGE_PATH+"Fishing.png"));
		fishingLabel.setBounds(307, 102, 76, 29);
		panel_2.add(fishingLabel);

		final JLabel rangedLabel = new JLabel("1");
		rangedLabel.setIcon(resize(IMAGE_PATH+"Ranged.png"));
		rangedLabel.setBounds(177, 142, 76, 29);
		panel_2.add(rangedLabel);

		final JLabel thievingLabel = new JLabel("1");
		thievingLabel.setIcon(resize(IMAGE_PATH+"Thieving.png"));
		thievingLabel.setBounds(241, 142, 76, 29);
		panel_2.add(thievingLabel);

		final JLabel cookingLabel = new JLabel("1");
		cookingLabel.setIcon(resize(IMAGE_PATH+"Cooking.png"));
		cookingLabel.setBounds(307, 142, 76, 29);
		panel_2.add(cookingLabel);

		final JLabel prayerLabel = new JLabel("1");
		prayerLabel.setIcon(resize(IMAGE_PATH+"Prayer.png"));
		prayerLabel.setBounds(177, 182, 76, 29);
		panel_2.add(prayerLabel);

		final JLabel craftingLabel = new JLabel("1");
		craftingLabel.setIcon(resize(IMAGE_PATH+"Crafting.png"));
		craftingLabel.setBounds(241, 182, 76, 29);
		panel_2.add(craftingLabel);

		final JLabel firemakingLabel = new JLabel("1");
		firemakingLabel.setIcon(resize(IMAGE_PATH+"Firemaking.png"));
		firemakingLabel.setBounds(307, 182, 76, 29);
		panel_2.add(firemakingLabel);

		final JLabel magicLabel = new JLabel("1");
		magicLabel.setIcon(resize(IMAGE_PATH+"Magic.png"));
		magicLabel.setBounds(177, 222, 76, 29);
		panel_2.add(magicLabel);

		final JLabel fletchingLabel = new JLabel("1");
		fletchingLabel.setIcon(resize(IMAGE_PATH+"Fletching.png"));
		fletchingLabel.setBounds(241, 222, 76, 29);
		panel_2.add(fletchingLabel);

		final JLabel woodcuttingLabel = new JLabel("1");
		woodcuttingLabel.setIcon(resize(IMAGE_PATH+"Woodcutting.png"));
		woodcuttingLabel.setBounds(307, 222, 76, 29);
		panel_2.add(woodcuttingLabel);

		final JLabel runecraftingLabel = new JLabel("1");
		runecraftingLabel.setIcon(resize(IMAGE_PATH+"Runecrafting.png"));
		runecraftingLabel.setBounds(177, 262, 76, 29);
		panel_2.add(runecraftingLabel);

		final JLabel slayerLabel = new JLabel("1");
		slayerLabel.setIcon(resize(IMAGE_PATH+"Slayer.png"));
		slayerLabel.setBounds(241, 262, 76, 29);
		panel_2.add(slayerLabel);

		final JLabel farmingLabel = new JLabel("1");
		farmingLabel.setIcon(resize(IMAGE_PATH+"Farming.png"));
		farmingLabel.setBounds(307, 262, 76, 29);
		panel_2.add(farmingLabel);

		final JLabel constructionLabel = new JLabel("1");
		constructionLabel.setIcon(resize(IMAGE_PATH+"Construction.png"));
		constructionLabel.setBounds(177, 302, 76, 29);
		panel_2.add(constructionLabel);

		final JLabel hunterLabel = new JLabel("1");
		hunterLabel.setIcon(resize(IMAGE_PATH+"Hunter.png"));
		hunterLabel.setBounds(241, 302, 76, 29);
		panel_2.add(hunterLabel);

		final JLabel summoningLabel = new JLabel("1");
		summoningLabel.setIcon(resize(IMAGE_PATH+"Summoning.png"));
		summoningLabel.setBounds(307, 302, 76, 29);
		panel_2.add(summoningLabel);

		final JLabel dungeoneeringLabel = new JLabel("1");
		dungeoneeringLabel.setIcon(resize(IMAGE_PATH+"Dungeoneering.png"));
		dungeoneeringLabel.setBounds(177, 342, 76, 29);
		panel_2.add(dungeoneeringLabel);

		final JLabel questPointsLabel = new JLabel("0");
		questPointsLabel.setIcon(resize(IMAGE_PATH+"QuestPoints.png"));
		questPointsLabel.setBounds(241, 342, 76, 29);
		panel_2.add(questPointsLabel);

		final JLabel totalLevelLabel = new JLabel("Total Level:");
		totalLevelLabel.setBounds(393, 32, 189, 14);
		panel_2.add(totalLevelLabel);

		final JLabel totalExpLabel = new JLabel("Total Exp:");
		totalExpLabel.setBounds(393, 69, 201, 14);
		panel_2.add(totalExpLabel);

		final JLabel memberLabel = new JLabel("Member:");
		memberLabel.setBounds(393, 149, 165, 14);
		panel_2.add(memberLabel);

		final JLabel rightsLabel = new JLabel("Rights:");
		rightsLabel.setBounds(393, 109, 165, 14);
		panel_2.add(rightsLabel);

		final JLabel bankSizeLabel = new JLabel("Bank size:");
		bankSizeLabel.setBounds(393, 189, 176, 14);
		panel_2.add(bankSizeLabel);

		final JLabel canTradeLabel = new JLabel("Trade banned:");
		canTradeLabel.setBounds(393, 269, 201, 14);
		panel_2.add(canTradeLabel);

		final JLabel bannedLabel = new JLabel("Banned:");
		bannedLabel.setBounds(393, 309, 165, 14);
		panel_2.add(bannedLabel);

		final JLabel moneyPouchLabel = new JLabel("Money pouch:");
		moneyPouchLabel.setBounds(393, 229, 201, 14);
		panel_2.add(moneyPouchLabel);

		final JLabel mutedLabel = new JLabel("Muted:");
		mutedLabel.setBounds(393, 349, 176, 14);
		panel_2.add(mutedLabel);

		final JPanel infoPanel = new JPanel();
		tabbedPane.addTab("Information", null, infoPanel, null);
		infoPanel.setLayout(null);

		final JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(14, 57, 198, 301);
		infoPanel.add(scrollPane_2);

		updateNPCList();
		scrollPane_2.setViewportView(npcList);

		final JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(14, 57, 121, 327);
		panel_2.add(scrollPane_1);

		hsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (loadPlayer()) {
					attackLabel.setText(player.getSkill().getLevels()[Skills.ATTACK]+"");
					constitutionLabel.setText(player.getSkill().getLevels()[Skills.HITPOINTS]+"");
					miningLabel.setText(player.getSkill().getLevels()[Skills.MINING]+"");
					strengthLabel.setText(player.getSkill().getLevels()[Skills.STRENGTH]+"");
					agilityLabel.setText(player.getSkill().getLevels()[Skills.AGILITY]+"");
					smithingLabel.setText(player.getSkill().getLevels()[Skills.SMITHING]+"");
					defenceLabel.setText(player.getSkill().getLevels()[Skills.DEFENCE]+"");
					herbloreLabel.setText(player.getSkill().getLevels()[Skills.HERBLORE]+"");
					fishingLabel.setText(player.getSkill().getLevels()[Skills.FISHING]+"");
					rangedLabel.setText(player.getSkill().getLevels()[Skills.RANGED]+"");
					thievingLabel.setText(player.getSkill().getLevels()[Skills.THIEVING]+"");
					cookingLabel.setText(player.getSkill().getLevels()[Skills.COOKING]+"");
					prayerLabel.setText(player.getSkill().getLevels()[Skills.PRAYER]+"");
					craftingLabel.setText(player.getSkill().getLevels()[Skills.CRAFTING]+"");
					firemakingLabel.setText(player.getSkill().getLevels()[Skills.FIREMAKING]+"");
					magicLabel.setText(player.getSkill().getLevels()[Skills.MAGIC]+"");
					fletchingLabel.setText(player.getSkill().getLevels()[Skills.FLETCHING]+"");
					woodcuttingLabel.setText(player.getSkill().getLevels()[Skills.WOODCUTTING]+"");
					runecraftingLabel.setText(player.getSkill().getLevels()[Skills.RUNECRAFTING]+"");
					slayerLabel.setText(player.getSkill().getLevels()[Skills.SLAYER]+"");
					farmingLabel.setText(player.getSkill().getLevels()[Skills.FARMING]+"");
					constructionLabel.setText(player.getSkill().getLevels()[Skills.CONSTRUCTION]+"");
					hunterLabel.setText(player.getSkill().getLevels()[Skills.HUNTER]+"");
					summoningLabel.setText(player.getSkill().getLevels()[Skills.SUMMONING]+"");
					dungeoneeringLabel.setText(player.getSkill().getLevels()[Skills.DUNGEONEERING]+"");
					questPointsLabel.setText(player.getAchievementsPoints()+"");
					totalLevelLabel.setText("Total Level: "+ player.getSkill().getTotalLevel());
					totalExpLabel.setText("Total Exp: "+ player.getSkill().getTotalExperience());
					rightsLabel.setText("Rights: "+player.getRights());
					memberLabel.setText("Member: True"); //+(player.isMember() ? "true" : "false")); //Set to true, doesn't matter
					bankSizeLabel.setText("Bank size: "+player.getBank().getSize()+"");
					moneyPouchLabel.setText("Money pouch: "+player.getMoneyPouch());
					canTradeLabel.setText("Trade banned: N/A"); //+(player.isTradeBanned() ? "true" : "false")); //Set to true, doesn't matter
					bannedLabel.setText("Banned: "+(player.isBanned() ? "true" : "false"));
					mutedLabel.setText("Muted: "+(player.isMuted() ? "true" : "false"));
				}
			}
		});
		scrollPane_1.setViewportView(hsList);

		final JLabel idLabel = new JLabel("Id :");
		idLabel.setBounds(241, 59, 322, 14);
		infoPanel.add(idLabel);

		final JLabel informationLabel = new JLabel("NPC information");
		informationLabel.setFont(new Font("Sitka Small", Font.PLAIN, 30));
		informationLabel.setBounds(241, 12, 353, 36);
		infoPanel.add(informationLabel);

		final JLabel nameLabel = new JLabel("Name :");
		nameLabel.setBounds(241, 84, 353, 14);
		infoPanel.add(nameLabel);

		final JLabel combatLevelLabel = new JLabel("Combat level:");
		combatLevelLabel.setBounds(241, 109, 322, 14);
		infoPanel.add(combatLevelLabel);

		final JLabel examineLabel = new JLabel("Examine:");
		examineLabel.setBounds(241, 134, 353, 14);
		infoPanel.add(examineLabel);

		final JLabel attackAnimationLabel = new JLabel("Attack animation: ");
		attackAnimationLabel.setBounds(241, 159, 322, 14);
		infoPanel.add(attackAnimationLabel);

		final JLabel defenceAnimationLabel = new JLabel("Defence animation: ");
		defenceAnimationLabel.setBounds(241, 184, 322, 14);
		infoPanel.add(defenceAnimationLabel);

		final JLabel deathAnimationLabel = new JLabel("Death animation: ");
		deathAnimationLabel.setBounds(241, 209, 322, 14);
		infoPanel.add(deathAnimationLabel);

		final JLabel respawnTimeLabel = new JLabel("Respawn time: ");
		respawnTimeLabel.setBounds(241, 234, 322, 14);
		infoPanel.add(respawnTimeLabel);

		final JLabel firstOptionLabel = new JLabel("First option:");
		firstOptionLabel.setBounds(241, 259, 322, 14);
		infoPanel.add(firstOptionLabel);

		final JLabel secondOptionLabel = new JLabel("Second option:");
		secondOptionLabel.setBounds(241, 284, 322, 14);
		infoPanel.add(secondOptionLabel);

		final JLabel thirdOptionLabel = new JLabel("Third option:");
		thirdOptionLabel.setBounds(241, 309, 322, 14);
		infoPanel.add(thirdOptionLabel);

		final JLabel fourthOptionLabel = new JLabel("Fourth option:");
		fourthOptionLabel.setBounds(241, 334, 322, 14);
		infoPanel.add(fourthOptionLabel);

		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				npcListModel.clear();
				if (!showingItems && !showingObjects) {
					for (int i = 0; i <= GameDefinitionLoader.getNpcDefinitions().values().size(); i++) {
						if (GameDefinitionLoader.getNpcDefinition(i).getName().toLowerCase().contains(textField.getText())) {
							npcListModel.addElement(i+" - "+GameDefinitionLoader.getNpcDefinition(i).getName());
						}
					}
				} else if (showingItems && !showingObjects) {
					for (int i = 0; i <= GameDefinitionLoader.getItemSize(); i++) {
						if (GameDefinitionLoader.getItemDef(i).getName().toLowerCase().contains(textField.getText())) {
							npcListModel.addElement(i+" - "+GameDefinitionLoader.getItemDef(i).getName());
						}
					}
				} /*else {
					for (int i = 0; i <= Utils.getObjectDefinitionsSize(); i++) {
						if (ObjectDefinitions.getObjectDefinitions(i).getName().toLowerCase().contains(textField.getText())) {
							npcListModel.addElement(i+" - "+ObjectDefinitions.getObjectDefinitions(i).getName());
						}
					}
					return;
				}*/
			}
		});
		searchButton.setBounds(135, 373, 77, 23);
		infoPanel.add(searchButton);

		final JButton btnShowObjects = new JButton("Show Items");
		btnShowObjects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (showingItems && !showingObjects) {
					showingItems = !showingItems;
					showingObjects = !showingObjects;
					btnShowObjects.setText("Show npcs");
					informationLabel.setText("Object information");
					idLabel.setText("Id : ");
					nameLabel.setText("Name : ");
					combatLevelLabel.setText("Animation : ");
					examineLabel.setText("Examine : ");
					attackAnimationLabel.setText("Size (X) : ");
					defenceAnimationLabel.setText("Size (Y) : ");
					deathAnimationLabel.setText("Projectile clipped : ");
					respawnTimeLabel.setText("Clip type : ");
					firstOptionLabel.setText("First option : ");
					secondOptionLabel.setText("Second option : ");
					thirdOptionLabel.setText("Third option : ");
					fourthOptionLabel.setText("Fourth option : ");
					updateNPCList();
					return;
				} if (showingObjects && !showingItems) {
					showingObjects = !showingObjects;
					btnShowObjects.setText("Show items");
					informationLabel.setText("NPC information");
					idLabel.setText("Id : ");
					nameLabel.setText("Name : ");
					combatLevelLabel.setText("Combat level : ");
					examineLabel.setText("Examine : ");
					attackAnimationLabel.setText("Attack animation : ");
					attackAnimationLabel.setText("Attack animation : ");
					defenceAnimationLabel.setText("Defence animation : ");
					deathAnimationLabel.setText("Death animation : ");
					respawnTimeLabel.setText("Respawn time : ");
					firstOptionLabel.setText("First option : ");
					secondOptionLabel.setText("Second option : ");
					thirdOptionLabel.setText("Third option : ");
					fourthOptionLabel.setText("Fourth option : ");
					updateNPCList();
					return;
				} else {//currently displaying NPC's..
					showingItems = !showingItems;
					btnShowObjects.setText("Show objects");
					informationLabel.setText("Item information");
					idLabel.setText("Id : ");
					nameLabel.setText("Name : ");
					combatLevelLabel.setText("Size : ");
					examineLabel.setText("Examine : ");
					attackAnimationLabel.setText("Price : ");
					attackAnimationLabel.setText("Weight : ");
					defenceAnimationLabel.setText("Tradeable : ");
					deathAnimationLabel.setText("Noted : ");
					respawnTimeLabel.setText("Lended item : ");
					firstOptionLabel.setText("First option : ");
					secondOptionLabel.setText("Second option : ");
					thirdOptionLabel.setText("Third option : ");
					fourthOptionLabel.setText("Fourth option : ");
					updateNPCList();
					return;
				}
			}
		});
		btnShowObjects.setBounds(14, 23, 198, 23);
		infoPanel.add(btnShowObjects);

		for (int i = 0; i < infoPanel.getComponents().length; i++) { //needs to be above textField or they become unclickable.
			infoPanel.getComponents()[i].setFocusable(false);
		}

		textField = new JTextField();
		textField.setBounds(14, 374, 111, 20);
		infoPanel.add(textField);
		textField.setColumns(10);

		JPanel toolsPanel = new JPanel();
		tabbedPane.addTab("Tools", null, toolsPanel, null);
		toolsPanel.setLayout(null);

		JButton btnNewButton = new JButton("Pack item bonuses");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					GameDefinitionLoader.loadItemBonusDefinitions();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(10, 44, 172, 36);
		toolsPanel.add(btnNewButton);

		/*JButton btnUnpackIb = new JButton("Reload prices");
		btnUnpackIb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					PriceManager.init();
					logAction("Price manager", "Successfully reloaded item prices.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnUnpackIb.setBounds(10, 111, 172, 36);
		toolsPanel.add(btnUnpackIb);*/

		JButton btnPackNpcBonuses = new JButton("Pack NPC bonuses");
		btnPackNpcBonuses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GameDefinitionLoader.loadNpcDefinitions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logAction("NPC bonuses", "Successfully packed NPC bonuses");
			}
		});
		btnPackNpcBonuses.setBounds(10, 181, 172, 36);
		toolsPanel.add(btnPackNpcBonuses);

		/*JButton btnPackMusicHints = new JButton("Reload item weights");
		btnPackMusicHints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WeightManager.init();
				logAction("Weight manager", "Successfully reloaded item weights.");
			}
		});
		btnPackMusicHints.setBounds(220, 44, 172, 36);
		toolsPanel.add(btnPackMusicHints);

		JButton btnPackCensoredWords = new JButton("Pack censored words");
		btnPackCensoredWords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProfanityFilter.loadUnpackedCensoredWords();
				logAction("Profanity filter", "Successfully packed censored words.");
			}
		});
		btnPackCensoredWords.setBounds(220, 111, 172, 36);
		toolsPanel.add(btnPackCensoredWords);*/

		JButton btnPackNpcSpawns = new JButton("Pack NPC spawns");
		btnPackNpcSpawns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (Mob i : World.getNpcs()) {
					if (i != null) {
						i.remove();
						World.getNpcs()[i.getIndex()] = null;

						for (Player k : World.getPlayers()) {
							if (k != null) {
								k.getClient().getNpcs().remove(i);
							}
						}
					}
				}

				Mob.spawnBosses();
				try {
					GameDefinitionLoader.loadNpcSpawns();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logAction("NPC Spawns", "Successfully packed NPC Spawns.");
			}
		});
		btnPackNpcSpawns.setBounds(220, 181, 172, 36);
		toolsPanel.add(btnPackNpcSpawns);

		JButton btnPackShops = new JButton("Pack shops");
		btnPackShops.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GameDefinitionLoader.loadShopDefinitions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Shop.declare();
				logAction("Shops handler", "Successfully packed the shops.");
			}
		});
		btnPackShops.setBounds(10, 249, 172, 36);
		toolsPanel.add(btnPackShops);

		JButton btnPackObjectSpawns = new JButton("Pack object spawns");
		btnPackObjectSpawns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectManager.declare();
				logAction("Object spawns", "Successfully packed object spawns.");
			}
		});
		btnPackObjectSpawns.setBounds(220, 249, 172, 36);
		toolsPanel.add(btnPackObjectSpawns);

		JButton btnShutdownServer = new JButton("Shutdown server");
		btnShutdownServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				logAction("Launcher", "Shutting down server...");
			}
		});
		btnShutdownServer.setBounds(422, 249, 172, 36);
		toolsPanel.add(btnShutdownServer);

		final boolean[] yellDisabled = {false};
		JButton btnToggleYell = new JButton("Toggle yell");
		btnToggleYell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!yellDisabled[0]) {
					for (Player p : World.getPlayers()) {
						if(p == null) continue;
						p.setYellMuted(true);
						yellDisabled[0] = true;
					}
				}
				else {
					for (Player p : World.getPlayers()) {
						if(p == null) continue;
						p.setYellMuted(false);
						yellDisabled[0] = false;
					}
				}

				logAction("Yell handler", "The yell system has been "+(!yellDisabled[0] ? "dis" : "en")+"abled.");
			}
		});
		btnToggleYell.setBounds(422, 181, 172, 36);
		toolsPanel.add(btnToggleYell);

		JButton btnPackNpcCombat = new JButton("Pack NPC combat defs");
		btnPackNpcCombat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GameDefinitionLoader.loadNpcCombatDefinitions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logAction("NPC combat definitions", "Successfully packed npc combat definitions.");
			}
		});
		btnPackNpcCombat.setBounds(422, 111, 172, 36);
		toolsPanel.add(btnPackNpcCombat);

		JButton btnPackItemExamine = new JButton("Pack item examines");
		btnPackItemExamine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GameDefinitionLoader.loadItemDefinitions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logAction("Item examines", "Successfully packed item examines.");
			}
		});
		btnPackItemExamine.setBounds(422, 44, 172, 36);
		toolsPanel.add(btnPackItemExamine);



		JButton btnNewButton_1 = new JButton("Send");
		btnNewButton_1.addActionListener(arg0 -> {
			if (announcementsTextField.getText() == null) {
				return;
			}
			System.out.println("Field: " + announcementsTextField.getText());
			for (Player p : World.getPlayers()) {
				if(p == null) continue;
				p.send(new SendMessage("<col=FF0000>[<img=1> Server <img=1>] "+announcementsTextField.getText()+"</col>"));
			}
			announcementsTextField.setText("");
		});
		btnNewButton_1.setBounds(487, 345, 89, 27);
		toolsPanel.add(btnNewButton_1);

		for (int i = 0; i < toolsPanel.getComponents().length; i++) {
			toolsPanel.getComponents()[i].setFocusable(false);
		}

		announcementsTextField = new JTextField();
		announcementsTextField.setBounds(24, 345, 453, 27);
		toolsPanel.add(announcementsTextField);
		announcementsTextField.setColumns(10);

		JLabel lblWorldAnnouncement = new JLabel("World announcement:");
		lblWorldAnnouncement.setBounds(24, 320, 158, 14);
		toolsPanel.add(lblWorldAnnouncement);

		npcList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final String[] info = npcList.getSelectedValue().split(" - ");
				if (!showingItems && !showingObjects) { //if we're displaying NPC's
					NpcDefinition npcDefs = GameDefinitionLoader.getNpcDefinition(Integer.parseInt(info[0]));
					final NpcCombatDefinition defs = GameDefinitionLoader.getNpcCombatDefinition(Integer.parseInt(info[0]));
					idLabel.setText("Id : "+info[0]);
					nameLabel.setText("Name : "+info[1]);
					combatLevelLabel.setText("Combat level : "+npcDefs.getLevel());
					//examineLabel.setText("Examine : "+NPCExamines.getExamine(Integer.parseInt(info[0])));
					//attackAnimationLabel.setText("Attack animation : "+defs.getAttackEmote());
					defenceAnimationLabel.setText("Defence animation : "+defs.getBlock());
					deathAnimationLabel.setText("Death animation : "+defs.getDeath());
					respawnTimeLabel.setText("Respawn time : "+defs.getRespawnTime()+" seconds");
					/*firstOptionLabel.setText("First option : "+npcDefs.getOption(0));
					secondOptionLabel.setText("Second option : "+npcDefs.getOption(1));
					thirdOptionLabel.setText("Third option : "+npcDefs.getOption(2));
					fourthOptionLabel.setText("Fourth option : "+npcDefs.getOption(3));*/
				} else if (showingItems && !showingObjects) { //Items...
					ItemDefinition itemDefs = GameDefinitionLoader.getItemDef(Integer.parseInt(info[0]));
					idLabel.setText("Id : "+info[0]);
					nameLabel.setText("Name : "+info[1]);
					//combatLevelLabel.setText("Oversized : "+itemDefs.isOverSized());
					//examineLabel.setText("Examine : "+ItemExamines.getExamine(new Item(Integer.parseInt(info[0]))));
					//attackAnimationLabel.setText("Price : "+PriceManager.getPrice(Integer.parseInt(info[0])));
					//attackAnimationLabel.setText("Weight : "+WeightManager.getWeight(Integer.parseInt(info[0]))+" KG");
					defenceAnimationLabel.setText("Tradeable : "+itemDefs.isTradable());
					deathAnimationLabel.setText("Noted : "+itemDefs.isNote());
					//respawnTimeLabel.setText("Lended item : "+itemDefs.isLended());
					/*firstOptionLabel.setText("First option : "+itemDefs.inventoryOptions[0]);
					secondOptionLabel.setText("Second option : "+itemDefs.inventoryOptions[1]);
					thirdOptionLabel.setText("Third option : "+itemDefs.inventoryOptions[2]);
					fourthOptionLabel.setText("Fourth option : "+itemDefs.inventoryOptions[3]);*/
				} else { //Objects
					ObjectDef objectDefs = ObjectDef.getObjectDef(Integer.parseInt(info[0]));
					idLabel.setText("Id : "+info[0]);
					nameLabel.setText("Name : "+info[1]);
					/*combatLevelLabel.setText("Animation : "+objectDefs.objectAnimation);
					examineLabel.setText("Examine : "+ObjectExamines.getExamine(info[0]));
					attackAnimationLabel.setText("Size (X) : "+objectDefs.sizeX);
					defenceAnimationLabel.setText("Size (Y) : "+objectDefs.sizeY);
					deathAnimationLabel.setText("Projectile clipped : "+objectDefs.isProjectileCliped());
					respawnTimeLabel.setText("Clip type : "+objectDefs.getClipType());
					firstOptionLabel.setText("First option : "+objectDefs.getOption(0));
					secondOptionLabel.setText("Second option : "+objectDefs.getOption(1));
					thirdOptionLabel.setText("Third option : "+objectDefs.getOption(2));
					fourthOptionLabel.setText("Fourth option : "+objectDefs.getOption(3));*/
				}
			}
		});

		for (int i = 0; i < panel_2.getComponents().length; i++) { //needs to be above textField or they become unclickable.
			panel_2.getComponents()[i].setFocusable(false);
		}

		final JTextArea text = new JTextArea();
		DefaultCaret caret = (DefaultCaret)text.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


		JPanel console = new JPanel();
		tabbedPane.addTab("Console", null, console, null);
		console.setLayout(new BoxLayout(console, BoxLayout.X_AXIS));

		SYSTEM_OUT =  new PrintStream(System.out) {
			public void println(String x) {
				text.append(x + "\n");
			}
		};
		System.setOut(SYSTEM_OUT);
		CONSOL_SCROLLER = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		CONSOL_SCROLLER.setAutoscrolls(true);
		text.setBackground(new Color(0, 0, 0));
		text.setFont(new Font("Verdana", 0, 12));
		text.setForeground(new Color(51, 204, 0));
		text.setWrapStyleWord(true);
		console.add(CONSOL_SCROLLER);


		/*text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					ItemBonusesPacker.main(new String[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		btnNewButton.setBounds(10, 44, 172, 36);
		toolsPanel.add(btnNewButton);

	}

	protected void updateNPCList() {
		npcListModel.clear();
		if (showingItems) {
			for (int i = 0; i <= GameDefinitionLoader.getItemSize(); i++) {
				npcListModel.addElement(i+" - "+GameDefinitionLoader.getItemDef(i).getName());
			}
		}
		/*else if (showingObjects) {
			for (int i = 0; i <= GameDefinitionLoader; i++) {
				npcListModel.addElement(i+" - "+ObjectDef.getObjectDef(i).name);
			}
		}*/ else {
			int i = 0;
			List<NpcDefinition> npcdefs = GameDefinitionLoader.getNpcDefinitions().values().stream().filter(npcdef -> {
				if (npcdef == null || !GameDefinitionLoader.getMobDropDefinitions().containsKey(npcdef.getId())) {
					return false;
				}
				return npcdef.isAttackable();
			}).collect(Collectors.toList());
			for (NpcDefinition def : npcdefs) {
				npcListModel.addElement(i+" - "+def.getName());
				i++;
			}
		}
	}

	protected String getGrammar() {
		return hoursField.getText()+ (hoursField.getText().equalsIgnoreCase("1") ? " hour" : " hours");
	}

	private boolean loadPlayer() {
		player = World.getPlayerByName(hsList.getSelectedValue());
		//System.out.println(player);
		if (player == null) {
			String[] str = hsList.getSelectedValue().split(" - ");
			//player = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(str[0]));
			if(PlayerSaveUtil.exists(str[0])) {
				try {
					//PlayerSave.PlayerDetails.loadDetails(str[0]);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			if (player != null)
				player.setUsername(hsList.getSelectedValue());
			else
				logAction("Error", "User does not exist");
			return true;
		}
		return true;
	}

	protected boolean checkAll(boolean requiresHours) {
		player = World.getPlayerByName(usernameField.getText());
		if (usernameField.getText().equalsIgnoreCase("")) {
			logAction("Error", "Select a player from the left or type their name in the username field.");
			return false;
		} /*else if (!SerializableFilesManager.containsPlayer(Utils.formatPlayerNameForProtocol(usernameField.getText()))) {
			logAction("Error", "It seems that player doesn't exist.");
			return false;
		}*/
		if (requiresHours) {
			try {
				if (hoursField.getText().equalsIgnoreCase("")) {
					logAction("Error", "For this punishment please fill in the hours field.");
					return false;
				}
				Integer.parseInt(hoursField.getText());
				if (hoursField.getText().startsWith("0")) {
					logAction("Error", "The hours field must start with an Integer of 1 - 9");
					return false;
				}
			} catch (final NumberFormatException e) {
				logAction("Error", "You may only use Integers in the hours text box.");
				return false;
			}
		}
		if (player == null) {
			/*player = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(usernameField.getText()));
			player.setUsername(Utils.formatPlayerNameForProtocol(usernameField.getText()));*/
			return true;
		}
		return true;
	}

	public static void updateList() {
		try {
			playersModel.clear();
			hsPlayersList.clear();
			if (showingOffline) {
				init(new File("./data/characters/details/"));
				return;
			}
			for (final Player p : World.getPlayers()) {
				if(p == null) continue;
				playersModel.addElement(p.getUsername());
				hsPlayersList.addElement(p.getUsername());
			}
		/*for (final Player p : World.getLobbyPlayers()) {
				playersModel.addElement(p.getUsername() + " - Lobby");
				hsPlayersList.addElement(p.getUsername() + " - Lobby");
		}*/
		} catch(Exception e) {

		}
	}


	public void logAction(String file, String log) {
		loggingModel.addElement("["+file+"]"+" "+log);
		logger.ensureIndexIsVisible(logger.getModel().getSize()-1);
	}

	public static void init(final File folder) { //shows players that are offline.
		File[] fileArray = folder.listFiles();
		for (int i=0; (fileArray != null) && i< fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				init(fileArray[i]);
			} else {
				playersModel.addElement(fileArray[i].getName().replaceAll(".json", ""));
				hsPlayersList.addElement(fileArray[i].getName().replaceAll(".json", ""));
			}
		}
	}
}

