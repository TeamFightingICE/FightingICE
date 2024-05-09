package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SocketUtil {
	
	public static void socketSend(DataOutputStream dout, byte[] byteArray, boolean withHeader) throws IOException {
		if (withHeader) {
			int dataLength = byteArray.length;
			byte[] lengthBytes = ByteBuffer.allocate(4)
					.order(ByteOrder.LITTLE_ENDIAN)
					.putInt(dataLength)
					.array();
			dout.write(lengthBytes);
		}
		dout.write(byteArray);
	}
	
	public static byte[] socketRecv(DataInputStream din, int dataLength) throws IOException {
		if (dataLength == -1) {
			byte[] lengthBytes = din.readNBytes(4);
			dataLength = ByteBuffer.wrap(lengthBytes)
					.order(ByteOrder.LITTLE_ENDIAN)
					.getInt();
		}
		return din.readNBytes(dataLength);
	}
	
}
