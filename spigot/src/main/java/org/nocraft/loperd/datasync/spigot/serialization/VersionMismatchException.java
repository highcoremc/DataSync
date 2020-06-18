package org.nocraft.loperd.datasync.spigot.serialization;

public class VersionMismatchException extends Exception {
    private final int receivedVersion;
    private final int supportedVersion;

    public VersionMismatchException(int receivedVersion, int supportedVersion) {
        super("The received object is of version serialization " + receivedVersion + " while this plugin expects version " + supportedVersion);
        this.receivedVersion = receivedVersion;
        this.supportedVersion = supportedVersion;
    }

    public int getReceivedVersion() {
        return receivedVersion;
    }

    public int getSupportedVersion() {
        return supportedVersion;
    }
}
