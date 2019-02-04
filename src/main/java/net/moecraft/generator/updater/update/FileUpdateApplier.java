//--------------------------------------------------
// Class FileUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.Environment;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class FileUpdateApplier {
    private final MetaResult task;

    public FileUpdateApplier(MetaResult task) {
        this.task = task;
    }

    public void start() {
        task.getFileNodesByType(MetaNodeType.ExcludedFile).getFileNodes().forEach(fileNode -> FileHandler.delete(fileNode.getPath()));
        handleExcludedDirectoryNodes(task.getDirectoryNodesByType(MetaNodeType.ExcludedDirectory));

        task.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(this::handleNewFiles);
        handleNewDirectoryNodes(task.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        task.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(this::handleNewFiles);
    }

    public void handleNewDirectoryNodes(List<DirectoryNode> directoryNodes) {
        directoryNodes.forEach(directoryNode -> {
            DirectoryHandler.create(directoryNode.getPath());

            if(directoryNode.hasChildDirectory())
                handleNewDirectoryNodes(directoryNode.getDirectoryNodes());

            directoryNode.getFileNodes().forEach(this::handleNewFiles);
        });
    }

    public void handleExcludedDirectoryNodes(List<DirectoryNode> directoryNodes) {
        directoryNodes.forEach(directoryNode -> {
            if(directoryNode.hasChildDirectory())
                handleExcludedDirectoryNodes(directoryNode.getDirectoryNodes());
            else
                DirectoryHandler.delete(directoryNode.getPath());
        });
    }

    public void handleNewFiles(FileNode fileNode) {
        FileHandler.linkCompatible(getObjectPath(fileNode), Paths.get(Environment.getBaseMoeCraftPath()).resolve(fileNode.getRelativePath()), FileHandler.LinkType.Hard);
    }

    public Path getObjectPath(FileNode fileNode) {
        return Environment.getUpdaterObjectPath().resolve(fileNode.getExpectedMd5());
    }
}
