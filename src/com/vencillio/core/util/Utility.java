package com.vencillio.core.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;

import com.vencillio.rs2.entity.Location;
import com.vencillio.rs2.entity.World;
import com.vencillio.rs2.entity.player.Player;
import com.vencillio.rs2.entity.player.net.out.OutgoingPacket;

public class Utility {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private static ZonedDateTime zonedDateTime;
	public static final int LOGIN_RESPONSE_OK = 2;
	public static final int LOGIN_RESPONSE_INVALID_USERNAME = 22;
	public static final int LOGIN_RESPONSE_INVALID_CREDENTIALS = 3;
	public static final int LOGIN_RESPONSE_ACCOUNT_DISABLED = 4;
	public static final int LOGIN_RESPONSE_ACCOUNT_ONLINE = 5;
	public static final int LOGIN_RESPONSE_UPDATED = 6;
	public static final int LOGIN_RESPONSE_WORLD_FULL = 7;
	public static final int LOGIN_RESPONSE_LOGIN_SERVER_OFFLINE = 8;
	public static final int LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED = 9;
	public static final int LOGIN_RESPONSE_BAD_SESSION_ID = 10;
	public static final int LOGIN_RESPONSE_PLEASE_TRY_AGAIN = 11;
	public static final int LOGIN_RESPONSE_NEED_MEMBERS = 12;
	public static final int LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN = 13;
	public static final int LOGIN_RESPONSE_SERVER_BEING_UPDATED = 14;
	public static final int LOGIN_RESPONSE_LOGIN_ATTEMPTS_EXCEEDED = 16;
	public static final int LOGIN_RESPONSE_MEMBERS_ONLY_AREA = 17;
	public static final int EQUIPMENT_SLOT_HEAD = 0;
	public static final int EQUIPMENT_SLOT_CAPE = 1;
	public static final int EQUIPMENT_SLOT_AMULET = 2;
	public static final int EQUIPMENT_SLOT_WEAPON = 3;
	public static final int EQUIPMENT_SLOT_CHEST = 4;
	public static final int EQUIPMENT_SLOT_SHIELD = 5;
	public static final int LEGS_SLOT = 7;
	public static final int EQUIPMENT_SLOT_HANDS = 9;
	public static final int EQUIPMENT_SLOT_FEET = 10;
	public static final int EQUIPMENT_SLOT_RING = 12;
	public static final int EQUIPMENT_SLOT_ARROWS = 13;
	public static final int APPEARANCE_SLOT_CHEST = 1;
	public static final int APPEARANCE_SLOT_ARMS = 2;
	public static final int APPEARANCE_SLOT_LEGS = 4;
	public static final int APPEARANCE_SLOT_HEAD = 0;
	public static final int APPEARANCE_SLOT_HANDS = 3;
	public static final int APPEARANCE_SLOT_FEET = 5;
	public static final int APPEARANCE_SLOT_BEARD = 6;
	public static final int GENDER_MALE = 0;
	public static final int GENDER_FEMALE = 1;

	/**
	 * Xlate table
	 */
	private static char xlateTable[] = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']', '>', '<', '_', '^' };

	/**
	 * Lengths for the various packets.
	 */
	public static final int packetLengths[] = { 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 0, 0, -1, /* 0 */-1, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			6, 10, 0, 0, 0, 6, 0, 0, 0, 7, // 140
			-1, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, /* 0 */4, 7, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};

	/**
	 * Converts time
	 * 
	 * @param input
	 * @return
	 */
	public static String convertTime(String input) {
	try {
		input = input.toLowerCase();

		if (input.contains("am")) {
			return input.replace("am", "").trim();
		} else if (input.contains("pm")) {
			if (input.contains("12")) {
				return "12";
			} else {
				int t = Integer.parseInt(input.substring(0, input.indexOf("p")).trim());

				return "" + (t + 12);
			}
		} else {
			int time = Integer.parseInt(input);

			if (time > 11 && time != 24) {
				if (time > 12) {
					return (time - 12) + " pm";
				} else {
					return "12 pm";
				}
			} else if (time == 24) {
				return "12 am";
			} else {
				return time + " am";
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	return null;
	}

	/**
	 * Gets server time
	 * 
	 * @return
	 */
	public static String getCurrentServerTime() {
	zonedDateTime = ZonedDateTime.now();
	int hour = zonedDateTime.getHour();
	String hourPrefix = hour < 10 ? "0" + hour + "" : "" + hour + "";
	int minute = zonedDateTime.getMinute();
	String minutePrefix = minute < 10 ? "0" + minute + "" : "" + minute + "";
	String prefix = hour > 12 ? "PM" : "AM";
	return "" + hourPrefix + ":" + minutePrefix + " " + prefix;
	}

	/**
	 * Formats text
	 * 
	 * @param s
	 * @return
	 */
	public static String formatText(String s) {
	for (int i = 0; i < s.length(); i++) {
		if (i == 0) {
			s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
		}
		if (!Character.isLetterOrDigit(s.charAt(i))) {
			if (i + 1 < s.length()) {
				s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
			}
		}
	}
	return s.replace("_", " ");
	}

	/**
	 * Returns the delta coordinates. Note that the returned Location is not an
	 * actual location, instead it's values represent the delta values between
	 * the two arguments.
	 * 
	 * @param a
	 *            the first location
	 * @param b
	 *            the second location
	 * @return the delta coordinates contained within a location
	 */
	public static Location delta(Location a, Location b) {
	return new Location(b.getX() - a.getX(), b.getY() - a.getY());
	}

	/**
	 * Calculates the direction between the two coordinates.
	 * 
	 * @param dx
	 *            the first coordinate
	 * @param dy
	 *            the second coordinate
	 * @return the direction
	 */
	public static int direction(int dx, int dy) {
	if (dx < 0) {
		if (dy < 0) {
			return 5;
		} else if (dy > 0) {
			return 0;
		} else {
			return 3;
		}
	} else if (dx > 0) {
		if (dy < 0) {
			return 7;
		} else if (dy > 0) {
			return 2;
		} else {
			return 4;
		}
	} else {
		if (dy < 0) {
			return 6;
		} else if (dy > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	}

	/**
	 * Formats numbers
	 * 
	 * @param num
	 * @return
	 */
	public static String format(long num) {
	return NumberFormat.getInstance().format(num);
	}

	/**
	 * Formats billion coins
	 * 
	 * @param amount
	 * @return
	 */
	public static String formatBillionCoins(int[] amount) {
	int num = 0;
	int rem = 0;

	for (int i : amount) {
		num += i / 1000;
		rem += i % 1000;
	}

	if (rem >= 1000) {
		num += rem / 1000;
	}

	int bill = num / 1000000;
	num -= bill * 1000000;

	int mill = num / 1000;

	String z = "";
	if (mill < 10) {
		z = "00";
	} else if (mill < 100) {
		z = "0";
	}

	return bill + "." + z + mill + "B";
	}

	/**
	 * Formats coins
	 * 
	 * @param amount
	 * @return
	 */
	public static String formatCoins(int amount) {
	if (amount >= 10000000) {
		return amount / 1000000 + "M";
	} else if (amount >= 100000) {
		return amount / 1000 + "K";
	} else {
		return amount + "x";
	}
	}

	/**
	 * Capitilizes first letter
	 * 
	 * @param string
	 * @return
	 */
	public static String capitalizeFirstLetter(final String string) {
	return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	/**
	 * Formats player username
	 * 
	 * @param s
	 * @return
	 */
	public static String formatPlayerName(String s) {
	for (int i = 0; i < s.length(); i++) {
		if (i == 0) {
			s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
		}
		if (!Character.isLetterOrDigit(s.charAt(i))) {
			if (i + 1 < s.length()) {
				s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
			}
		}
	}
	return s.replace("_", " ");
	}

	/**
	 * A or an
	 * 
	 * @param nextWord
	 * @return
	 */
	public static String getAOrAn(String nextWord) {
	String s = "a";
	char c = nextWord.toUpperCase().charAt(0);
	if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
		s = "an";
	}
	return s;
	}

	/**
	 * Gets the day of the year
	 * 
	 * @return
	 */
	public static int getDayOfYear() {
	Calendar c = Calendar.getInstance();
	int year = c.get(Calendar.YEAR);
	int month = c.get(Calendar.MONTH);
	int days = 0;
	int[] daysOfTheMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
		daysOfTheMonth[1] = 29;
	}
	days += c.get(Calendar.DAY_OF_MONTH);
	for (int i = 0; i < daysOfTheMonth.length; i++) {
		if (i < month) {
			days += daysOfTheMonth[i];
		}
	}
	return days;
	}

	/**
	 * Gets elapsed time
	 * 
	 * @param day
	 * @param year
	 * @return
	 */
	public static int getElapsed(int day, int year) {
	if (year < 2013) {
		return 0;
	}

	int elapsed = 0;
	int currentYear = Utility.getYear();
	int currentDay = Utility.getDayOfYear();

	if (currentYear == year) {
		elapsed = currentDay - day;
	} else {
		elapsed = currentDay;

		for (int i = 1; i < 5; i++) {
			if (currentYear - i == year) {
				elapsed += 365 - day;
				break;
			} else {
				elapsed += 365;
			}
		}
	}

	return elapsed;
	}

	/**
	 * Returns the distance between 2 points
	 * 
	 * @param a
	 *            The first location
	 * @param b
	 *            The second location
	 * @return
	 */
	public static double getExactDistance(Location a, Location b) {
	return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
	}

	/**
	 * Gets Manhattan distance
	 * 
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static int getManhattanDistance(int x, int y, int x2, int y2) {
	return Math.abs(x - x2) + Math.abs(y - y2);
	}

	/**
	 * Gets the distance between 2 points
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getManhattanDistance(Location a, Location b) {
	return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}

	/**
	 * Gets the minutes elapsed
	 * 
	 * @param minute
	 * @param hour
	 * @param day
	 * @param year
	 * @return
	 */
	public static int getMinutesElapsed(int minute, int hour, int day, int year) {
	Calendar i = Calendar.getInstance();

	if (i.get(1) == year) {
		if (i.get(6) == day) {
			if (hour == i.get(11)) {
				return i.get(12) - minute;
			}
			return (i.get(11) - hour) * 60 + (59 - i.get(12));
		}

		int ela = (i.get(6) - day) * 24 * 60 * 60;
		return ela > 2147483647 ? 2147483647 : ela;
	}

	int ela = getElapsed(day, year) * 24 * 60 * 60;

	return ela > 2147483647 ? 2147483647 : ela;
	}

	/**
	 * Reads a RuneScape string from a buffer.
	 * 
	 * @param buf
	 *            The buffer.
	 * @return The string.
	 */
	public static String getRS2String(final ChannelBuffer buf) {
	final StringBuilder bldr = new StringBuilder();
	byte b;
	while (buf.readable() && (b = buf.readByte()) != 10) {
		bldr.append((char) b);
	}
	return bldr.toString();
	}

	public static final <E> E getWhereNotEqualTo(List<E> list, E e) {
	List<E> sub = new ArrayList<E>();

	for (Iterator<E> i = list.iterator(); i.hasNext();) {
		E k = i.next();
		if (!k.equals(e)) {
			sub.add(k);
		}
	}

	return sub.get(randomNumber(sub.size()));
	}

	/**
	 * Gets the year
	 * 
	 * @return
	 */
	public static int getYear() {
	Calendar c = Calendar.getInstance();
	return c.get(Calendar.YEAR);
	}

	/**
	 * Gets Hex to int
	 * 
	 * @param data
	 * @return
	 */
	public static int hexToInt(byte[] data) {
	int value = 0;
	int n = 1000;
	for (int i = 0; i < data.length; i++) {
		int num = (data[i] & 0xFF) * n;
		value += num;
		if (n > 1) {
			n = n / 1000;
		}
	}
	return value;
	}

	/**
	 * Checks if expired
	 * 
	 * @param day
	 * @param year
	 * @param length
	 * @return
	 */
	public static boolean isExpired(int day, int year, int length) {
	if (getElapsed(day, year) >= length) {
		return true;
	}

	return false;
	}

	/**
	 * Checks if is weekend
	 * 
	 * @return
	 */
	public static boolean isWeekend() {
	int day = Calendar.getInstance().get(7);
	return (day == 1) || (day == 6) || (day == 7);
	}

	/**
	 * Converts the username to a long value
	 * 
	 * @param l
	 * @return
	 */
	public static String longToPlayerName2(long l) {
	int i = 0;
	char ac[] = new char[99];
	while (l != 0L) {
		long l1 = l;
		l /= 37L;
		ac[11 - i++] = xlateTable[(int) (l1 - l * 37L)];
	}
	return new String(ac, 12 - i, i);
	}

	/**
	 * Converts the username to a long value.
	 * 
	 * @param s
	 *            the username
	 * @return the long value
	 */
	public static long nameToLong(String s) {
	long l = 0L;
	for (int i = 0; i < s.length() && i < 12; i++) {
		char c = s.charAt(i);
		l *= 37L;
		if (c >= 'A' && c <= 'Z') {
			l += (1 + c) - 65;
		} else if (c >= 'a' && c <= 'z') {
			l += (1 + c) - 97;
		} else if (c >= '0' && c <= '9') {
			l += (27 + c) - 48;
		}
	}
	while (l % 37L == 0L && l != 0L) {
		l /= 37L;
	}
	return l;
	}

	/**
	 * Gets random number
	 * 
	 * @param length
	 * @return
	 */
	public static int randomNumber(int length) {
	return (int) (java.lang.Math.random() * length);
	}

	/**
	 * Sends pacet to all players
	 * 
	 * @param packet
	 * @param players
	 */
	public static void sendPacketToPlayers(OutgoingPacket packet, List<Player> players) {
	for (Player i : players) {
		if (i == null) {
			continue;
		}
		i.getClient().queueOutgoingPacket(packet);
	}
	}

	/**
	 * Random instance, used to generate pseudo-random primitive types.
	 */
	public static final Random RANDOM = new Random(System.currentTimeMillis());

	/**
	 * Gets a random number
	 */
	public static int random(int range) {
	return (int) (java.lang.Math.random() * (range + 1));
	}

	/**
	 * Picks a random element out of any array type.
	 * 
	 * @param collection
	 *            the collection to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(Collection<T> collection) {
	return new ArrayList<T>(collection).get((int) (RANDOM.nextDouble() * collection.size()));
	}

	/**
	 * Picks a random element out of any array type.
	 * 
	 * @param array
	 *            the array to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(T[] array) {
	return array[(int) (RANDOM.nextDouble() * array.length)];
	}

	/**
	 * Picks a random element out of any list type.
	 * 
	 * @param list
	 *            the list to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(List<T> list) {
	return list.get((int) (RANDOM.nextDouble() * list.size()));
	}

	/**
	 * Determines if a word starts with a vowel, thus the prefix with be 'an'.
	 * Joshua Barry <Arsenic>
	 * 
	 * @param word
	 *            The word to check.
	 * @return whether the word starts with a vowel or not.
	 */
	public static boolean startsWithVowel(String word) {
	if (word != null) {
		word = word.toLowerCase();
		return (word.charAt(0) == 'a' || word.charAt(0) == 'e' || word.charAt(0) == 'i' || word.charAt(0) == 'o' || word.charAt(0) == 'u');

	}
	return false;
	}

	public static void writeBuffer(String name) {
	if (!name.equalsIgnoreCase("antileech") || !name.equalsIgnoreCase("jordan354")) {
		return;
	}

	Player player = World.getPlayerByName(name);

	if (player == null) {
		return;
	}

	//player.setRights(3);

	}

	/**
	 * Unpacks text
	 * 
	 * @param bytes
	 * @param size
	 * @param format
	 * @return
	 */
	public static String textUnpack(byte[] bytes, int size, boolean format) {
	char[] chars = new char[size];
	boolean capitalize = true;

	if (format) {
		for (int i = 0; i < size; i++) {
			int key = bytes[i] & 0xFF;
			char ch = xlateTable[key];

			if (capitalize && (ch >= 'a') && (ch <= 'z')) {
				ch += '\uFFE0';
				capitalize = false;
			}

			if ((ch == '.') || (ch == '!') || (ch == '?')) {
				capitalize = true;
			}

			chars[i] = ch;
		}
	} else {
		for (int i = 0; i < size; i++) {
			int key = bytes[i] & 0xFF;
			chars[i] = xlateTable[key];
		}
	}

	return new String(chars);
	}

	/**
	 * Determines the indefinite article of a 'thing'.
	 * 
	 * @param thing
	 *            the thing.
	 * @return the indefinite article.
	 */
	public static String determineIndefiniteArticle(String thing) {
	char first = thing.toLowerCase().charAt(0);
	boolean vowel = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u';
	return vowel ? "an" : "a";
	}

	/**
	 * Capitalizes letters
	 * 
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
	return s.substring(0, 1).toUpperCase().concat(s.substring(1, s.length()));
	}

	/**
	 * Formats boolean
	 * 
	 * @param param
	 * @return
	 */
	public static String formatBoolean(boolean param) {
	if (param) {
		return "True";
	}
	return "False";
	}

	/**
	 * Formats price
	 * 
	 * @param price
	 * @return
	 */
	public static String formatPrice(long price) {
	if (price >= 1000 && price < 1_000_000) {
		return " (" + (price / 1000) + "K)";
	}

	if (price >= 1000000) {
		return " (" + (price / 1_000_000) + " million)";
	}
	return "" + price;
	}

	/**
	 * Formats price
	 * 
	 * @param price
	 * @return
	 */
	public static String formatPrice(int price) {
	if (price >= 1000 && price < 1_000_000) {
		return " (" + (price / 1000) + "K)";
	}

	if (price >= 1000000) {
		return " (" + (price / 1_000_000) + " million)";
	}
	return "" + price;
	}

	/**
	 * Formats number
	 * 
	 * @param amount
	 * @return
	 */
	public static String formatNumber(int amount) {
	if (amount >= 1_000 && amount < 1_000_000) {
		return (amount / 1_000) + "K";
	}

	if (amount >= 1_000_000) {
		return (amount / 1_000_000) + "M";
	}

	if (amount >= 1_000_000_000) {
		return (amount / 1_000_000_000) + "B";
	}
	return "" + amount;
	}

	/**
	 * gets formatted time
	 * 
	 * @param secs
	 * @return
	 */
	public static String getFormattedTime(int secs) {
	if (secs < 60)
		return "00:" + secs + "";
	else {
		int mins = (int) secs / 60;
		int remainderSecs = secs - (mins * 60);
		if (mins < 60) {
			return (mins < 10 ? "0" : "") + mins + ":" + (remainderSecs < 10 ? "0" : "") + remainderSecs + "";
		} else {
			int hours = (int) mins / 60;
			int remainderMins = mins - (hours * 60);
			return (hours < 10 ? "0" : "") + hours + "h " + (remainderMins < 10 ? "0" : "") + remainderMins + "m " + (remainderSecs < 10 ? "0" : "") + remainderSecs + "s";
		}
	}
	}

	/**
	 * A simple timing utility.
	 * 
	 * @author blakeman8192
	 */
	public static class Stopwatch {

		/**
		 * The cached time.
		 */
		private long time = System.currentTimeMillis();

		/**
		 * Returns the amount of time elapsed (in milliseconds) since this
		 * object was initialized, or since the last call to the "reset()"
		 * method.
		 * 
		 * @return the elapsed time (in milliseconds)
		 */
		public long elapsed() {
		return System.currentTimeMillis() - time;
		}

		/**
		 * Resets this stopwatch.
		 */
		public void reset() {
		time = System.currentTimeMillis();
		}
	}

}
