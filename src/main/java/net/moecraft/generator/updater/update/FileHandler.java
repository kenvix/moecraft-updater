//--------------------------------------------------
// Class FileHandler
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.Environment;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileHandler {
    private static final boolean isWindows = System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");

    public enum LinkType {
        Symbol, Hard, DirectorySymbol, DirectoryJoin
    }

    public static void delete(Path target) {
        try {
            FileUtils.forceDelete(target.toFile());
            Environment.getLogger().finest("Delete: " + target.toString());
        } catch (IOException ex) {
            throw new UpdateCriticalException("Delete file failed: " + target.toString(), 80, ex);
        }
    }

    public static void copy(Path from, Path dest) {
        try {
            FileUtils.forceMkdirParent(dest.toFile());
            FileUtils.copyFile(from.toFile(), dest.toFile());

            Environment.getLogger().finest("Copy : " + from.toString() + " -> " + dest.toString());
        } catch (IOException ex) {
            throw new UpdateCriticalException("Copy file failed: " + from.toString() + " -> " + dest.toString(), 81, ex);
        }
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

    public static void link(Path target, Path linkPath, LinkType type) {
        try {
            FileUtils.forceMkdirParent(linkPath.toFile());

            if(linkPath.toFile().exists())
                FileUtils.forceDelete(linkPath.toFile());

            if (isWindows)
                LinkForWindows(target, linkPath, type);
            else
                linkForOtherPlatform(target, linkPath, type);

            Environment.getLogger().finest("Link : " + linkPath.toString() + " => " + target.toString());
        } catch (IOException ex) {
            throw new UpdateCriticalException("Link file failed: " + target.toString() + " -> " + linkPath.toString(), 82, ex);
        }
    }

    public static void linkCompatible(Path target, Path linkPath, LinkType type) {
        try {
            link(target, linkPath, type);
        } catch (UpdateCriticalException ex) {
            Environment.getLogger().fine("Link file failed . Fallback into copy mode: " + ex.getMessage());
            copy(target, linkPath);
        }
    }


    public static void LinkForWindows(Path target, Path linkPath, LinkType type) throws IOException {
        String command = String.format("/c mklink %s \"%s\" \"%s\"", getWindowsLinkTypeCommand(type), linkPath.toAbsolutePath().toString(), target.toAbsolutePath().toString());
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", command);
        Process process = builder.start();

        try {
            process.waitFor();
        } catch (InterruptedException ex) {}

        if (process.exitValue() != 0)
            throw new IOException("Create link failed");
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
}