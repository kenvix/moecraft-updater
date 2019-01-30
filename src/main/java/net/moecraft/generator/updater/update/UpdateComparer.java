//--------------------------------------------------
// Class ScanComparer
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.Environment;
import net.moecraft.generator.meta.*;

import java.io.File;
import java.util.List;

/**
 * 更新对比
 */
public class UpdateComparer {
    private MetaResult remote;
    private MetaResult local;
    private MetaResult result = null;

    public UpdateComparer(MetaResult remote, MetaResult local) {
        this.remote = remote;
        this.local = local;
    }

    /**
     * @return MetaResult 需要更新的文件
     */
    public MetaResult compare() {
        result             = new MetaResult();
        List<FileNode> resultDefaultFiles = result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes();
        remote.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().forEach(fileNode -> {
            if (!fileNode.getFile().exists())
                resultDefaultFiles.add(fileNode);
        });

        remote.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().forEach(fileNode -> compareUpdateFile(result.getFileNodesByType(MetaNodeType.SyncedFile), fileNode));

        List<DirectoryNode> directoryNodes = result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory);
        compareUpdateDirectoryNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), directoryNodes);

        DirectoryNode            excludedFiles = result.getFileNodesByType(MetaNodeType.ExcludedFile);
        List<DirectoryNode> excludedDirs  = result.getDirectoryNodesByType(MetaNodeType.ExcludedDirectory);
        compareExcludeDirectoryAndFileNodes(remote.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), local.getDirectoryNodesByType(MetaNodeType.SyncedDirectory), excludedFiles, excludedDirs);
        return result;
    }

    private void compareExcludeDirectoryAndFileNodes(List<DirectoryNode> remote, List<DirectoryNode> local, DirectoryNode resultFiles, List<DirectoryNode> resultDirs) {
        for(DirectoryNode directoryNode : local) {
            if(remote.contains(directoryNode)) {
                int remoteDirectoryIndex = remote.indexOf(directoryNode);
                List<FileNode> remoteFileNodes = remote.get(remoteDirectoryIndex).getFileNodes();
                List<FileNode> localFileNodes = directoryNode.getFileNodes();
                for (FileNode fileNode : localFileNodes) {
                    if(!remoteFileNodes.contains(fileNode)) {
                        resultFiles.addFileNode(fileNode);
                        Environment.getLogger().finest("- Delete: " + fileNode.getFile().getPath());
                    }
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
        if (!remote.getFile().exists() || !remote.getMD5().equals(remote.getExpectedMd5())) {
            out.addFileNode(remote);
            Environment.getLogger().finest("+ Add " + remote.getFile().getPath());

            if(!result.hasGlobalObject(remote.getExpectedMd5())) {
                File installedObjectFile = Environment.getUpdaterObjectPath().resolve(ObjectEngine.getObjectFileName(remote.getExpectedMd5())).toFile();

                if(!installedObjectFile.exists() || !installedObjectFile.isFile() || !FileTool.getFileMD5(installedObjectFile).equals(remote.getExpectedMd5())) {
                    result.putGlobalObjectsByMd5(remote.getExpectedMd5(), remote.getObjects());
                    Environment.getLogger().finest("# Download Object: " + installedObjectFile.getName());
                }
            }
        }
    }

    private void compareUpdateDirectoryNodes(List<DirectoryNode> from, List<DirectoryNode> result) {
        for (DirectoryNode directoryNode : from) {
            DirectoryNode       resultDirectoryNode = new DirectoryNode(directoryNode.getDirectory());
            List<FileNode> fileNodes           = directoryNode.getFileNodes();
            fileNodes.forEach(fileNode -> compareUpdateFile(resultDirectoryNode, fileNode));

            if (directoryNode.hasChildDirectory())
                compareUpdateDirectoryNodes(directoryNode.getDirectoryNodes(), resultDirectoryNode.getDirectoryNodes());

            result.add(directoryNode);
        }
    }
}
