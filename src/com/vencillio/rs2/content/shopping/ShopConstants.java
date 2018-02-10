package com.vencillio.rs2.content.shopping;

/**
 * Holds the Shop Constants
 * @author Daniel
 *
 */
public class ShopConstants {
	
	/**
	 * Color of send messages
	 */
	public static final String COLOUR = "<col=7A007A>";
	
	/**
	 * Shops that are available to Iron players
	 */
	public static int[] IRON_SHOPS = { 3, 5, 6, 7, 20, 26, 29, 32, 33, 37, 38, 39, 41, 89, 91, 92, 93, 94, 95 };
	
	/**
	 * Shops that players may view/sell but not purchase
	 */
	public static int[] IRON_NO_BUY_SHOPS = { 29 };
	
	/**
	 * Items that can't be sold to shop
	 */
	public static int[] NO_SELL = { 1623, 1624, 1621, 1622, 1620, 1619, 1618, 1617, 1626, 1625, 1627, 1628, 1629, 1630 };

}
