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

import java.util.HashSet;

/**
 * 更新对比
 */
public class UpdateComparer {
    private MetaResult remote;
    private MetaResult local;

    public UpdateComparer(MetaResult remote, MetaResult local) {
        this.remote = remote;
        this.local = local;
    }

    /**
     * @return MetaResult 需要更新的文件
     */
    public MetaResult compare() {
        MetaResult        result             = new MetaResult();
        HashSet<FileNode> resultDefaultFiles = result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes();
        remote.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(fileNode -> {
            if (!fileNode.getFile().exists())
                resultDefaultFiles.add(fileNode);
        });

        remote.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(fileNode -> compareUpdateFile(result.getFileNodesByType(MetaNodeType.SyncedFile), fileNode));

        HashSet<DirectoryNode> directoryNodes = new HashSet<>();
        compareUpdateDirectoryNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), directoryNodes);
        result.setDirectoryNodesByType(MetaNodeType.SyncedDirectory, directoryNodes);

        DirectoryNode excludedFiles = result.getFileNodesByType(MetaNodeType.ExcludedFile);

        return result;
    }

    private void compareExcludeFileNodes(HashSet<DirectoryNode> remote, HashSet<DirectoryNode> local, DirectoryNode result) {

    }

    private void compareUpdateFile(DirectoryNode out, FileNode remote) {
        if (!remote.getFile().exists() || !remote.getMD5().equals(remote.getExpectedMd5()))
            out.addFileNode(remote);
    }

    private void compareUpdateDirectoryNodes(HashSet<DirectoryNode> from, HashSet<DirectoryNode> result) {
        for (DirectoryNode directoryNode : from) {
            DirectoryNode     resultDirectoryNode = new DirectoryNode(directoryNode.getDirectory());
            HashSet<FileNode> fileNodes           = directoryNode.getFileNodes();
            fileNodes.forEach(fileNode -> compareUpdateFile(resultDirectoryNode, fileNode));

            if (directoryNode.hasChildDirectory())
                compareUpdateDirectoryNodes(directoryNode.getDirectoryNodes(), resultDirectoryNode.getDirectoryNodes());

            result.add(directoryNode);
        }
    }
}
