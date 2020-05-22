/**
 * The multiecho server itself
 */
package channelEchoServerCmd;

import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MultiEchoServerNIOCmd {
	private static ServerSocketChannel serverSocketChannel;
	private static final int PORT = 1234;
	private static Selector selector;
	private static Vector<SocketChannel> socketChannelVec;
	private static Vector<ChatUser> allUsers;
	public static final int CAPACITY = 20;
	public static final int BUFFER_SIZE = 2048;
	public static final String NEW_LINE = System.lineSeparator();

 	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			InetSocketAddress netAddress = new InetSocketAddress(PORT);
			serverSocket.bind(netAddress);
			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
			System.exit(1);
		}
		socketChannelVec = new Vector<>(CAPACITY);
		allUsers = new Vector<>(CAPACITY);
		System.out.println("Server is opened ...");
		processConnections();
	}

	private static void processConnections () {
		do {
			try {
				int numKeys = selector.select();
				System.out.println(numKeys + " keys selected.");
				if (numKeys > 0) {
					Set eventKeys = selector.selectedKeys();
					Iterator keyCycler = eventKeys.iterator();
					while (keyCycler.hasNext()) {
						SelectionKey key = (SelectionKey)keyCycler.next();
						int keyOps = key.readyOps();
						if ((keyOps & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
							acceptConnection(key);
							continue;
						}
						if ((keyOps & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
							acceptData(key);
						}
					}
				}
			}
			catch (IOException ioEx) {
				ioEx.printStackTrace();
				System.exit(1);
			}
		} while (true);
	}
	private static void acceptConnection (SelectionKey key) throws IOException {
		SocketChannel socketChannel;
		Socket socket;

		socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socket = socketChannel.socket();
		System.out.println("Connection on " + socket + ".");
		socketChannel.register(selector, SelectionKey.OP_READ);
		socketChannelVec.add(socketChannel);
		selector.selectedKeys().remove(key);
	}
	private static void acceptData (SelectionKey key) throws IOException {
		SocketChannel socketChannel;
		Socket socket;

		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		socketChannel = (SocketChannel) key.channel();
		buffer.clear();
		int numBytes = socketChannel.read(buffer);
		socket = socketChannel.socket();
		if (numBytes == -1) {
			key.cancel();
			closeSocket(socket);
		}
		else {
			String chatName = null;
			byte[] byteArray = buffer.array();
			if (byteArray[0] == '#')
				announceNewUser(socketChannel, buffer);
			else {
				for (ChatUser chatUser : allUsers)
					if (chatUser.getUserSocketChannel().equals(socketChannel))
						chatName = chatUser.getChatName();
				broadcastMessage(chatName, buffer);
			}
		}
	}

	private static void closeSocket (Socket socket) {
		try {
			if (socket != null)
				socket.close();
		}
		catch (IOException ioEx) {
			System.out.println("Unable to close socket!");
		}
	}
	public static void announceNewUser (SocketChannel userSocketChannel, ByteBuffer buffer) {
		ChatUser chatUser;
		byte[] byteArray = buffer.array();
		int messageSize = buffer.position();
		String chatName = new String(byteArray, 1, messageSize);
		if (chatName.indexOf("\n") >= 0)
			chatName = chatName.substring(0, chatName.indexOf("\n"));
		chatUser = new ChatUser(userSocketChannel, chatName);
		allUsers.add(chatUser);
		if (!socketChannelVec.remove(userSocketChannel)) {
			System.out.println("Can't find user!");
			return;
		} // we should save userSocketChannel in a chatUser instance before deleting it.
		chatName = chatUser.getChatName();
		System.out.println(chatName + " entered the chat room at " + new Date() +  "." + NEW_LINE);
		String welcomeMessage = "Welcome " + chatName + "!" + NEW_LINE;
		byte[] bytes = welcomeMessage.getBytes(); 
		buffer.clear();
		for (int i = 0; i < welcomeMessage.length(); i++)
			buffer.put(bytes[i]);
		buffer.flip();
		try {
			chatUser.getUserSocketChannel().write(buffer);
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}
	public static void announceExit (String name) {
		System.out.println(name + " left chat room at " + new Date() + "." + NEW_LINE);
		for (ChatUser chatUser : allUsers) {
			if (chatUser.getChatName().equals(name))
				allUsers.remove(chatUser);
		}
	}
	public static void broadcastMessage (String chatName, ByteBuffer buffer) {
		String messagePrefix = chatName + ": ";
		byte[] messagePrefixBytes = messagePrefix.getBytes();
		final byte[] CR = NEW_LINE.getBytes();

		try {
			int messageSize = buffer.position();
			byte[] messageBytes = buffer.array();
			byte[] messageBytesCopy = new byte[messageSize];

			String userMessage = new String(messageBytes, 0, messageSize);
			if (userMessage.equals("Bye"))
				announceExit(chatName);

			for (int i = 0; i < messageSize; i++) 
				messageBytesCopy[i] = messageBytes[i];
			buffer.clear();
			buffer.put(messagePrefixBytes);
			for (int i = 0; i < messageSize; i++)
				buffer.put(messageBytesCopy[i]);
			buffer.put(CR);
			SocketChannel chatSocketChannel;
			for (ChatUser chatUser : allUsers) {
				chatSocketChannel = chatUser.getUserSocketChannel();
				buffer.flip();
				chatSocketChannel.write(buffer);
			}
		}
		catch (IOException ioEx) {
			ioEx.printStackTrace();
		}
	}
}

class ChatUser {
	private SocketChannel userSocketChannel;
	private String chatName;

	public ChatUser (SocketChannel userSocketChannel, String chatName) {
		this.userSocketChannel = userSocketChannel;
		this.chatName = chatName;
	}

	public SocketChannel getUserSocketChannel () {
		return userSocketChannel;
	}
	public String getChatName () {
		return chatName;
	}
}