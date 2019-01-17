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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
        MetaResult          result             = new MetaResult();
        ArrayList<FileNode> resultDefaultFiles = result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes();
        remote.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(fileNode -> {
            if (!fileNode.getFile().exists())
                resultDefaultFiles.add(fileNode);
        });

        remote.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(fileNode -> compareUpdateFile(result.getFileNodesByType(MetaNodeType.SyncedFile), fileNode));

        ArrayList<DirectoryNode> directoryNodes = result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory);
        compareUpdateDirectoryNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), directoryNodes);

        DirectoryNode            excludedFiles = result.getFileNodesByType(MetaNodeType.ExcludedFile);
        ArrayList<DirectoryNode> excludedDirs  = result.getDirectoryNodesByType(MetaNodeType.ExcludedDirectory);
        compareExcludeDirectoryAndFileNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), local.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), excludedFiles, excludedDirs);
        return result;
    }

    private void compareExcludeDirectoryAndFileNodes(ArrayList<DirectoryNode> remote, ArrayList<DirectoryNode> local, DirectoryNode resultFiles, ArrayList<DirectoryNode> resultDirs) {
        for(DirectoryNode directoryNode : local) {
            if(remote.contains(directoryNode)) {
                int remoteDirectoryIndex = remote.indexOf(directoryNode);
                ArrayList<FileNode> remoteFileNodes = remote.get(remoteDirectoryIndex).getFileNodes();
                ArrayList<FileNode> localFileNodes = directoryNode.getFileNodes();
                for (FileNode fileNode : localFileNodes) {
                    if(!remoteFileNodes.contains(fileNode))
                        resultFiles.addFileNode(fileNode);
                }
                if (directoryNode.hasChildDirectory()) {
                    DirectoryNode remoteChild = remote.get(remoteDirectoryIndex);
                    if(remoteChild.hasChildDirectory())
                        compareExcludeDirectoryAndFileNodes(remoteChild.getDirectoryNodes(), directoryNode.getDirectoryNodes(), resultFiles, resultDirs);
                }
            } else {
                resultDirs.add(directoryNode);
            }
        }
    }

    private void compareUpdateFile(DirectoryNode out, FileNode remote) {
        if (!remote.getFile().exists() || !remote.getMD5().equals(remote.getExpectedMd5()))
            out.addFileNode(remote);
    }

    private void compareUpdateDirectoryNodes(ArrayList<DirectoryNode> from, ArrayList<DirectoryNode> result) {
        for (DirectoryNode directoryNode : from) {
            DirectoryNode       resultDirectoryNode = new DirectoryNode(directoryNode.getDirectory());
            ArrayList<FileNode> fileNodes           = directoryNode.getFileNodes();
            fileNodes.forEach(fileNode -> compareUpdateFile(resultDirectoryNode, fileNode));

            if (directoryNode.hasChildDirectory())
                compareUpdateDirectoryNodes(directoryNode.getDirectoryNodes(), resultDirectoryNode.getDirectoryNodes());

            result.add(directoryNode);
        }
    }
}
