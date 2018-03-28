package com.vencillio.core.util;

import com.vencillio.rs2.entity.player.Player;

import java.awt.*;

/**
 * Created by Tanner on 3/27/2018.
 */
public class Notifications {

	public static void displayTray(Player p) {
		//Obtain only one instance of the SystemTray object
		SystemTray tray = SystemTray.getSystemTray();

		//If the icon is a file
		Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
		//Alternative (if the icon is on the classpath):
		//Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));
		TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
		//Let the system resizes the image if needed
		trayIcon.setImageAutoSize(true);
		//Set tooltip text for the tray icon
		//trayIcon.setToolTip("System tray icon demo");
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		trayIcon.displayMessage("Player Online: " + p.getUsername(), "", TrayIcon.MessageType.INFO);
	}
}
