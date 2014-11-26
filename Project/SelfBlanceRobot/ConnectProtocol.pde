import processing.serial.*;

public static class ConnectProtocol extends PApplet{

    public ConnectProtocol (String com, int buad) {
        port = new Serial(this, com, buad);
        port.bufferUntil('\n');
        inFloatDict = new FloatDict();
    }

    public static FloatDict stringToFloat(String str) {

        String inStr = str.replaceAll(" ", "");

        String stringSplitByComma[] = inStr.split(",");

        FloatDict stringToFloat = new FloatDict();

        for (String itr : stringSplitByComma) {
            String[] stringSplitByEqual = itr.split("=");
            stringToFloat.set(stringSplitByEqual[0], Float.parseFloat(stringSplitByEqual[1]));
        }
        return stringToFloat;
    }

    public int available() {
        return inFloatDictSize;
    }

    public FloatDict getInFloatDict() {
        inFloatDictSize = 0;
        return inFloatDict;
    }

    public void serialEvent(Serial port) {
        inFloatDictSize = 0;
        inString = port.readString();
        // println(inString);
        if (inFloatDict.size() > 0) inFloatDict.clear();
        inFloatDict = stringToFloat(inString);
        inFloatDictSize = inFloatDict.size();
    }

    public void stop() {
        port.stop();
    }

    public void sendByteArray(byte send[]) {
        port.write(send);
    }

    public Serial port;
    private FloatDict inFloatDict;

    private int inFloatDictSize;
    private String inString;
}