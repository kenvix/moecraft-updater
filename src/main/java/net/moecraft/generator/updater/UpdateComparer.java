//--------------------------------------------------
// Class ScanComparer
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater;

import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;

import java.io.File;
import java.util.HashSet;

/**
 * 更新对比
 */
public class UpdateComparer {
    private MetaResult remote;

    public UpdateComparer(MetaResult remote) {
        this.remote = remote;
    }

    /**
     * @return MetaResult 需要更新的文件
     */
    public MetaResult compare() {
        MetaResult        result             = new MetaResult();
        HashSet<FileNode> resultDefaultFiles = result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes();
        remote.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(fileNode -> {
            if(!fileNode.getFile().exists())
                resultDefaultFiles.add(fileNode);
        });
        remote.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(fileNode -> compareFile(result.getFileNodesByType(MetaNodeType.SyncedFile), fileNode));
        HashSet<DirectoryNode> directoryNodes = new HashSet<>();
        compareDirectoryNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), directoryNodes);
        result.setDirectoryNodesByType(MetaNodeType.SyncedDirectory, directoryNodes);
        return result;
    }

    private void compareFile(DirectoryNode out, FileNode remote) {
        if(!remote.getFile().exists() || !remote.getMD5().equals(remote.getExpectedMd5()))
            out.addFileNode(remote);
    }

    private void compareDirectoryNodes(HashSet<DirectoryNode> from, HashSet<DirectoryNode> result) {
        for (DirectoryNode directoryNode : from) {
            DirectoryNode resultDirectoryNode = new DirectoryNode(directoryNode.getDirectory());
            HashSet<FileNode> fileNodes = directoryNode.getFileNodes();
            fileNodes.forEach(fileNode -> compareFile(resultDirectoryNode, fileNode));
            if(directoryNode.hasChildDirectory())
                compareDirectoryNodes(directoryNode.getDirectoryNodes(), resultDirectoryNode.getDirectoryNodes());
            result.add(directoryNode);
        }
    }
}
