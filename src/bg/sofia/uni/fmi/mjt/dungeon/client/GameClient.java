package bg.sofia.uni.fmi.mjt.dungeon.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class GameClient {

	private static final String PROBLEM_WITH_CLIENT_MESSAGE = "Problem with the client has occurred.";
	private static final String INTERRUPTED_MESSAGE = "Thread interrupted during sleep method.";
	private static final String PROBLEM_OPENING_CHANNEL = "Problem with opening channel has occurred.";

	private static final String PROMPT_MESSAGE = "command----> ";
	private static final String QUIT = "quit";
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 8080;
	private static final int BUFFER_SIZE = 1024;
	private static final int SLEEP_MILLIS = 300;

	public void startGameClient(InputStream in, OutputStream out, SocketChannel socketChannel, ByteBuffer bufferSend,
			ByteBuffer bufferRecieve) {
		try (Scanner scanner = new Scanner(in)) {
			socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

			GameClientReaderThread clientReaderThread = new GameClientReaderThread(socketChannel, bufferRecieve, out);
			clientReaderThread.setDaemon(true);
			clientReaderThread.start();

			while (true) {
				out.write(PROMPT_MESSAGE.getBytes());
				String message = scanner.nextLine();
				bufferSend.clear();
				bufferSend.put(message.getBytes());
				bufferSend.flip();
				socketChannel.write(bufferSend);

				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch (InterruptedException e) {
					throw new RuntimeException(INTERRUPTED_MESSAGE, e);
				}

				if (QUIT.equals(message)) {
					return;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_WITH_CLIENT_MESSAGE, e);
		}
	}

	public void startGameClient(InputStream in, OutputStream out) {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		try (SocketChannel socketChannel = SocketChannel.open()) {
			startGameClient(in, out, socketChannel, buffer, null);
		} catch (IOException e) {
			throw new RuntimeException(PROBLEM_OPENING_CHANNEL, e);
		}
	}

	public static GameClient createGameClient() {
		return new GameClient();
	}

}
