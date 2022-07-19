package lungsimulator.utils;

import com.fazecast.jSerialComm.SerialPort;

public class COMPortAdapter {
	SerialPort comPort;

	public COMPortAdapter(String comName) {
		comPort = SerialPort.getCommPort(comName);
		comPort.setBaudRate(9600);
		if (!comPort.openPort())
			System.err.println("Errore nell'apertura della porta COM. \nLa porta potrebbe essere già aperta in un'altra applicazione");
	}

	public int readData() {
		if (comPort.bytesAvailable() == 0)
			return 0;

		byte[] readBuffer = new byte[comPort.bytesAvailable()];
		try {
	      comPort.readBytes(readBuffer, readBuffer.length);
		} catch (Exception e) { e.printStackTrace(); }

		return Byte.toUnsignedInt(readBuffer[0]);
	}

	public void sendData(String value) {
		comPort.writeBytes(value.getBytes(), value.getBytes().length);
	}

	public void dispose() {
		comPort.closePort();
	}
}
