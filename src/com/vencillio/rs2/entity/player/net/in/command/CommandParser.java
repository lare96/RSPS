package com.vencillio.rs2.entity.player.net.in.command;


/**
 * Parses a command.
 *
 * @author Michael | Chex
 */
public class CommandParser {

	/**
	 * The arguments of the command.
	 */
	private String[] arguments;

	/**
	 * The argument index.
	 */
	private int argumentIndex = 0;

	/**
	 * Creates a new command parser.
	 *
	 * @param command
	 *            - The command to parse.
	 */
	public static CommandParser create(String command) {
		CommandParser parser = new CommandParser();
		parser.arguments = command.split(" ");
		return parser;
	}

	/**
	 * Reads the next integer in the arguments.
	 *
	 * @return The next integer.
	 * @throws NumberFormatException
	 *             The argument is an invalid integer.
	 * @see #nextShort()
	 * @see #nextLong()
	 * @see #nextString()
	 * @see #nextDouble()
	 * @see #nextByte()
	 */
	public final int nextInt() throws NumberFormatException {
		return Integer.parseInt(nextString());
	}

	/**
	 * Reads the next double in the arguments.
	 *
	 * @return The next double.
	 * @throws NumberFormatException
	 *             The argument is an invalid integer.
	 * @see #nextShort()
	 * @see #nextLong()
	 * @see #nextString()
	 * @see #nextDouble()
	 * @see #nextByte()
	 */
	public final double nextDouble() throws NumberFormatException {
		return Double.parseDouble(nextString());
	}

	/**
	 * Reads the next long in the arguments.
	 *
	 * @return The next long.
	 * @throws NumberFormatException
	 *             The argument is an invalid long.
	 * @see #nextShort()
	 * @see #nextString()
	 * @see #nextInt()
	 * @see #nextDouble()
	 * @see #nextByte()
	 */
	public final long nextLong() throws NumberFormatException {
		return Long.parseLong(nextString());
	}

	/**
	 * Reads the next byte in the arguments.
	 *
	 * @return The next byte.
	 * @throws NumberFormatException
	 *             The argument is an invalid byte.
	 * @see #nextShort()
	 * @see #nextLong()
	 * @see #nextInt()
	 * @see #nextDouble()
	 * @see #nextString()
	 */
	public final byte nextByte() throws NumberFormatException {
		return Byte.parseByte(nextString());
	}

	/**
	 * Reads the next short in the arguments.
	 *
	 * @return The next short.
	 * @throws NumberFormatException
	 *             The argument is an invalid short.
	 * @see #nextString()
	 * @see #nextLong()
	 * @see #nextInt()
	 * @see #nextDouble()
	 * @see #nextByte()
	 */
	public final short nextShort() throws NumberFormatException {
		return Short.parseShort(nextString());
	}

	/**
	 * Reads the next argument in the command.
	 *
	 * @return The next argument.
	 * @throws ArrayIndexOutOfBoundsException
	 *             The next argument does not exist.
	 * @see #nextShort()
	 * @see #nextLong()
	 * @see #nextInt()
	 * @see #nextDouble()
	 * @see #nextByte()
	 */
	public final String nextString() throws ArrayIndexOutOfBoundsException {
		if (argumentIndex + 1 >= arguments.length)
			throw new ArrayIndexOutOfBoundsException("The next argument does not exist. [Size: " + arguments.length + ", Attempted: " + (argumentIndex + 1) + "]");

		return arguments[++argumentIndex];
	}

	/**
	 * Checks if the command parser has an unread argument.
	 *
	 * @return <code>True</code> if there is an unread argument left.
	 */
	public final boolean hasNext() {
		return hasNext(1);
	}

	/**
	 * Checks if the command parser has n unread arguments.
	 *
	 * @param length
	 *            - The length of arguments to check.
	 * @return <code>True</code> if there is n unread arguments left.
	 */
	public final boolean hasNext(int length) {
		return argumentIndex + length < arguments.length;
	}

	/**
	 * Gets the command name.
	 *
	 * @return The command name.
	 */
	public final String getCommand() {
		return arguments[0];
	}
	
	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < arguments.length; i++) {
			string += arguments[i] + " ";
		}
		return string.trim();
	}
}