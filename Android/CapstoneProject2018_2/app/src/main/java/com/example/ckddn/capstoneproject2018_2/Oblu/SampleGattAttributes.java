package com.example.ckddn.capstoneproject2018_2.Oblu;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    // BEGIN - Added by GT Silicon - BEGIN //
    /**
     * UART characteristics
     */
    public static final String SERVER_UART = "0003cdd0-0000-1000-8000-00805f9b0131";//sevice
    public static final String SERVER_UART_tx = "0003cdd1-0000-1000-8000-00805f9b0131";//characterristic
    public static final String SERVER_UART_rx = "0003cdd2-0000-1000-8000-00805f9b0131";//characterristic static



    static {
        // Sample Services.
        attributes.put(SERVER_UART, "UART Service");
        attributes.put(SERVER_UART_rx, "UART rx Service");
        attributes.put(SERVER_UART_tx, "UART tx Service");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
// END - Added by GT Silicon - END //