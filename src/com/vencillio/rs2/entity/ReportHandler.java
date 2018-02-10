package com.vencillio.rs2.entity;

import com.vencillio.core.util.Utility;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.impl.SendMessage;
import com.vencillio.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReportHandler {
	
	public static String[] savedNames = new String[500];
	public static String[] savedSpeach = new String[500];
	public static String[] savedTimes = new String[500];
	
	public static void addText(String name, byte[] data, int dataLength) {
		for (int i = 499; i > 0; i--) {
			savedNames[i] = savedNames[i - 1];
			savedSpeach[i] = savedSpeach[i - 1];
			savedTimes[i] = savedTimes[i - 1];
		}
		savedNames[0] = name;
		savedSpeach[0] = Utility.textUnpack(data, dataLength, false);
		String minute = new SimpleDateFormat("mm").format(new Date());
		String second = new SimpleDateFormat("ss").format(new Date());
		String hour = new SimpleDateFormat("hh").format(new Date());
		savedTimes[0] = hour + ":" + minute + ":" + second;
	}
	
	public static boolean hasSpoke(String s) {
		for (int i = 0; i < 500; i++) {
			if (savedNames[i] != null) {
				if (savedNames[i].equalsIgnoreCase(s))
					return true;
			}
		}
		return false;
	}

    public enum ReportData {
        RULE_1(163034, "Offensive language"),
        RULE_2(163035, "Item scamming"),
        RULE_3(163036, "Password scamming"),
        RULE_4(163037, "Bug abuse"),
        RULE_5(163038, "Staff impersonation"),
        RULE_6(163039, "Account sharing/trading"),
        RULE_7(163040, "Macroing"),
        RULE_8(163041, "Multiple loggin in"),
        RULE_9(163042, "Advertising"),
        RULE_10(163043, "Real world trading"),
        RULE_11(163044, "Misuse of customer support"),
        RULE_12(163045, "Encouraging others to break rules") ;

        private final int buttonID;
        private final String rule;
        private ReportData(int buttonID, String rule) {
            this.buttonID = buttonID;
            this.rule = rule;
        }

        public int getButton() {
            return buttonID;
        }

        public String getRule() {
            return rule;
        }
        
    	private static final HashMap<Integer, ReportData> reports = new HashMap<Integer, ReportData>();

    	static {
    		for (final ReportData report : ReportData.values()) {
    			ReportData.reports.put(report.buttonID, report);
    		}
    	}
    	
    	public static ReportData get(int id) {
    		return reports.get(id);
    	}
    }



    public static void handleReport(Player player) {
    
    	if (player.getInterfaceManager().main != 41750) {
    		player.send(new SendRemoveInterfaces());
    		return;
    	}
    	
    	if (player.reportName == "") {
    		player.send(new SendMessage("Please enter a name."));
    		return;
    	}

        Player offending = World.getPlayerByName(player.reportName);

        if (offending == null) {
            player.send(new SendMessage("It appears " + player.reportName + " is either offline or does not exist!"));
            return;
        }

        if (offending == player) {
            player.send(new SendMessage("You can not report yourself!"));
            return;
        }

        if (player.lastReported.equalsIgnoreCase(player.reportName) && (System.currentTimeMillis() - player.lastReport) < 60000) {
            player.send(new SendMessage("You can only report a player once every 60 seconds."));
            return;
        }
        
        if (player.reportClicked == 0) {
        	player.send(new SendMessage("Please select the offense " + offending.getUsername() + " has broken."));
        	return;
        }
        
        ReportData data = ReportData.reports.get(player.reportClicked);

		if (data == null) {
			return;
		}
        
		if (hasSpoke(offending.getUsername())) {
			String sendText = "";
			for (int i = 499; i > 0; i--) {
				if (savedNames[i] != null) {
					if (savedNames[i].equalsIgnoreCase(player.getUsername()) || savedNames[i].equalsIgnoreCase(offending.getUsername())) {
						sendText += " -[" + savedTimes[i] + ": " + savedNames[i] + "]: " + savedSpeach[i] + "\r\n";
					}
				}
			}

			sendText = sendText.replaceAll("'", " ");
			String month = getMonth(new SimpleDateFormat("MM").format(new Date()));
			String day = new SimpleDateFormat("dd").format(new Date());
			writeReport(offending.getUsername() + " was reported by " + player.getUsername() + ", " + data.getRule() + ", " + month + ", " + day + "", sendText + ".", offending.getUsername());
			player.send(new SendMessage("Thank you, your report has been received and will be reviewed."));
			player.lastReported = offending.getUsername();
			player.lastReport = System.currentTimeMillis();
			return;
		} else {
			player.send(new SendMessage("You can only report someone who has spoken in the last 60 seconds."));
			return;
		}


    }
    
	public static String getMonth(String s) {
		try {
			int i = Integer.parseInt(s);
			String[] months = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
			return months[i];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown";
	}

    public static void writeReport(String data, String text, String file) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("./data/reports/" + file + ".txt", true));
            bw.write(data);
            bw.newLine();
            bw.write(text);
            bw.newLine();
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException ioe2) {
                    System.out.println("Error writing system log.");
                    ioe2.printStackTrace();
                }
        }
    }

    public static void writeLog(String text, String file, String dir) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(dir + file + ".txt", true));
            bw.write(text);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException ioe2) {
                    System.out.println("Error writing system log.");
                }
        }
    }

}