package bg.sofia.uni.fmi.mjt.dungeon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import bg.sofia.uni.fmi.mjt.dungeon.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.dungeon.command.GameRepository;
import bg.sofia.uni.fmi.mjt.dungeon.command.UserRecipient;

public class GameServer {
	
	private static final String SERVER_HOST = "localhost";
	public static final int SERVER_PORT = 8080;
	private static final int BUFFER_SIZE = 1024;
	private static final int SLEEP_MILLIS = 300;
	
	private static final int ZERO = 0;
	
	private static final String PROBLEM_OPENING_RESOURCES_MESSAGE = "Problem with opening resources.";
	private static final String PROBLEM_INTERRUPTED_THREAD_MESSAGE = "Server thread was interrupted.";
	private static final String PROBLEM_ACCEPT_MESSAGE = "Problem occured while accepting a connection.";
	private static final String NOTHING_TO_READ_CLOSING_CHANNEL_MESSAGE = "Nothing to read, closing channel.";
	private static final String PROBLEM_SELECTING_KEYS_MESSAGE = "Problem occurred while selecting keys.";
	private static final String PROBLEM_READING_FROM_CHANNEL_MESSAGE = "Problem occurred while reading from channel.";
	private static final String PROBLEM_CLOSING_CHANNEL_MESSAGE = "Problem occurred while closing channel.";
	private static final String PROBLEM_WRITING_TO_CHANNEL_MESSAGE = "Problem occurred while writing to channel.";
	
	private CommandExecutor commandExecutor;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private ByteBuffer buffer;	
	

	private GameServer(GameRepository gameRepository) {
		commandExecutor = new CommandExecutor(gameRepository);
		openResources();
	}
	
	private void openResources() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
			serverSocketChannel.configureBlocking(false);

			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			buffer = ByteBuffer.allocate(BUFFER_SIZE);
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_OPENING_RESOURCES_MESSAGE, e);
		}
	}
	
	public void startGameServer() {
		boolean running = true;
		while (running) {
			int readyChannels;
			try {
				readyChannels = selector.select();
			} catch (IOException e) {
				throw new RuntimeException(PROBLEM_SELECTING_KEYS_MESSAGE, e);
			}
			if (readyChannels == ZERO) {
				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch (InterruptedException e) {
					throw new RuntimeException(PROBLEM_INTERRUPTED_THREAD_MESSAGE, e);
				}
				continue;
			}

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				if (key.isReadable()) {
					readFromKey(key);
				} else if (key.isAcceptable()) {
					acceptFromKey(key);
				}
				keyIterator.remove();
			}
		}
		
	}
	
	private void acceptFromKey(SelectionKey key) {
		ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
		try {
			SocketChannel accept = socketChannel.accept();
			accept.configureBlocking(false);
			accept.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_ACCEPT_MESSAGE, e);
		}
	}
	
	private void readFromKey(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		buffer.clear();
		int readBytes = ZERO;
		try {
			readBytes = socketChannel.read(buffer);
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_READING_FROM_CHANNEL_MESSAGE, e);
		}
		if (readBytes <= ZERO) {
			System.out.println(NOTHING_TO_READ_CLOSING_CHANNEL_MESSAGE);
			try {
				socketChannel.close();
			} catch (IOException e) {
				throw new RuntimeException(PROBLEM_CLOSING_CHANNEL_MESSAGE, e);
			}
			return;
		}

		buffer.flip();
		String command = new String(buffer.array(), ZERO, buffer.limit());
		answerForCommand(command, socketChannel);
	}
	
	private void answerForCommand(String command, SocketChannel socketChannel) {
		UserRecipient userRecipient = new UserRecipient(null, null);
		String commandResult = commandExecutor.executeCommand(command, socketChannel, userRecipient);
		String messageForOtherUser = userRecipient.getMessage();
		if (messageForOtherUser != null) {
			sendMessageToChannel(messageForOtherUser, userRecipient.getSocketChannel());
		}
		
		sendMessageToChannel(commandResult, socketChannel);
		String updatedDungeonMap = commandExecutor.getDungeonMapFromRepository();
		Collection<SocketChannel> socketChannels = commandExecutor.getSocketChannelsFromRepository();
		for (SocketChannel socketChannelRecipient : socketChannels) {
			sendMessageToChannel(updatedDungeonMap, socketChannelRecipient);
		}
	}
	
	private void sendMessageToChannel(String message, SocketChannel socketChannel) {
		buffer.clear();
		buffer.put(message.getBytes());
		buffer.flip();
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_WRITING_TO_CHANNEL_MESSAGE, e);
		}
	}	
	
	public static GameServer createGameServer(GameRepository gameRepository) {
		return new GameServer(gameRepository);
	}
	
	


}
