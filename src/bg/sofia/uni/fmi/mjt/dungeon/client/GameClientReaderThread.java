package bg.sofia.uni.fmi.mjt.dungeon.client;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class GameClientReaderThread extends Thread {

	private static final int BUFFER_SIZE = 1024;
	private static final String BUFFER_COULD_NOT_BE_FILLED = "Buffer could not be filled.";
	private static final String OUTPUT_PROBLEM = "Problem with writing to output was detected.";

	private static final String USER_QUIT_MESSAGE = " has quit game.";
	private static final String USER_DEAD_MESSAGE = " RIP";

	private SocketChannel socketChannel;
	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	private OutputStream output;

	GameClientReaderThread(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
		this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
		this.output = System.out;
	}

	GameClientReaderThread(SocketChannel socketChannel, ByteBuffer buffer, OutputStream output) {
		this.socketChannel = socketChannel;
		this.buffer = (buffer != null) ? buffer : ByteBuffer.allocate(BUFFER_SIZE);
		this.output = output;
	}

	@Override
	public void run() {
		while (true) {
			buffer.clear();
			try {
				socketChannel.read(buffer);
			} catch (IOException e) {
				throw new RuntimeException(BUFFER_COULD_NOT_BE_FILLED, e);
			}
			buffer.flip();
			String reply = new String(buffer.array(), 0, buffer.limit());

			try {
				output.write((reply + System.lineSeparator()).getBytes());
			} catch (IOException e) {
				throw new RuntimeException(OUTPUT_PROBLEM, e);
			}

			if (reply.endsWith(USER_QUIT_MESSAGE) || reply.endsWith(USER_DEAD_MESSAGE)) {
				break;
			}
		}
	}

}
