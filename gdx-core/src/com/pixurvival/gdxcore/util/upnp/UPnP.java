package com.pixurvival.gdxcore.util.upnp;

public class UPnP {

    private static Gateway defaultGW = null;
    private static final GatewayFinder finder = new GatewayFinder() {
        @Override
        public void gatewayFound(Gateway g) {
            synchronized (finder) {
                if (defaultGW == null) {
                    defaultGW = g;
                }
            }
        }
    };

    /**
     * Waits for UPnP to be initialized (takes ~3 seconds).<br>
     * It is not necessary to call this method manually before using UPnP functions
     */
    public static void waitInit() {
        while (finder.isSearching()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Is there an UPnP gateway?<br>
     * This method is blocking if UPnP is still initializing<br>
     * All UPnP commands will fail if UPnP is not available
     *
     * @return true if available, false if not
     */
    public static boolean isUPnPAvailable() {
        waitInit();
        return defaultGW != null;
    }

    /**
     * Opens a TCP port on the gateway
     *
     * @param port TCP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    public static boolean openPortTCP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.openPort(port, false);
    }

    /**
     * Opens a UDP port on the gateway
     *
     * @param port UDP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    public static boolean openPortUDP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.openPort(port, true);
    }

    /**
     * Closes a TCP port on the gateway<br>
     * Most gateways seem to refuse to do this
     *
     * @param port TCP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    public static boolean closePortTCP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.closePort(port, false);
    }

    /**
     * Closes a UDP port on the gateway<br>
     * Most gateways seem to refuse to do this
     *
     * @param port UDP port (0-65535)
     * @return true if the operation was successful, false otherwise
     */
    public static boolean closePortUDP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.closePort(port, true);
    }

    /**
     * Checks if a TCP port is mapped<br>
     *
     * @param port TCP port (0-65535)
     * @return true if the port is mapped, false otherwise
     */
    public static boolean isMappedTCP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.isMapped(port, false);
    }

    /**
     * Checks if a UDP port is mapped<br>
     *
     * @param port UDP port (0-65535)
     * @return true if the port is mapped, false otherwise
     */
    public static boolean isMappedUDP(int port) {
        if (!isUPnPAvailable())
            return false;
        return defaultGW.isMapped(port, false);
    }

    /**
     * Gets the external IP address of the default gateway
     *
     * @return external IP address as string, or null if not available
     */
    public static String getExternalIP() {
        if (!isUPnPAvailable())
            return null;
        return defaultGW.getExternalIP();
    }

    /**
     * Gets the internal IP address of this machine
     *
     * @return internal IP address as string, or null if not available
     */
    public static String getLocalIP() {
        if (!isUPnPAvailable())
            return null;
        return defaultGW.getLocalIP();
    }

}
