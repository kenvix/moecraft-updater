//--------------------------------------------------
// Class FileHandler
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.meta.FileNode;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileHandler {
    private final boolean isWindows;

    public FileHandler() {
        isWindows = System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    public enum LinkType {
        Symbol, Hard, DirectorySymbol, DirectoryJoin
    }

    public static void delete(Path target) throws IOException {
        FileUtils.forceDelete(target.toFile());
    }

    public static void copy(Path from, Path dest) throws IOException {
        FileUtils.copyFile(from.toFile(), dest.toFile());
    }

    public static String getWindowsLinkTypeCommand(LinkType type) {
        switch (type) {
            case Hard:
                return "/H";

            case DirectoryJoin:
                return "/J";

            case DirectorySymbol:
                return "/D";

            default:
                return "";
        }
    }

    public static void link(Path target, Path linkPath, LinkType type) throws IOException {

    }

    public static void LinkForWindows(Path target, Path linkPath, LinkType type) throws IOException {
        String command = String.format("/c mklink %s \"%s\" \"%s\"", getWindowsLinkTypeCommand(type), linkPath.toAbsolutePath().toString(), target.toAbsolutePath().toString());
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", command);
        Process process = builder.start();

        try {
            process.waitFor();
        } catch (InterruptedException ex) {}

        if (process.exitValue() != 0)
            throw new IOException("Create link failed" );
    }

    public static void linkForOtherPlatform(Path target, Path linkPath, LinkType type) throws IOException {
        switch (type) {
            case Symbol:
            case DirectorySymbol:
                Files.createSymbolicLink(linkPath, target);
                break;

            case Hard:
                Files.createLink(linkPath, target);
                break;

            case DirectoryJoin:
                throw new IllegalArgumentException("DirectoryJoin is not supported on this platform.");
        }
    }

    public static void linkCompat(FileNode fileNode) {

    }
}