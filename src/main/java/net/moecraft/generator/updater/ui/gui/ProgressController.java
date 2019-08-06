//--------------------------------------------------
// Class ProgressController
//--------------------------------------------------
// Written by Zbx1425 <zbx1425@outlook.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.gui;
import com.kenvix.utils.FileTool;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.ObjectEngine;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.repo.RepoNetworkUtil;
import net.moecraft.generator.updater.update.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("all") // Extremely bad habit! but I'm too lazy.
public class ProgressController implements Initializable {
	public static RubbishModel model = new RubbishModel();
	
	@FXML
	private ProgressIndicator ProgressABar;
	@FXML
	private ProgressBar ProgressBBar;
	@FXML
	private ProgressBar ProgressCBar;
	
	@FXML
	private Label TitleBar;
	@FXML
	private Label ProgressA;
	@FXML
	private Label ProgressB;
	
	@FXML
	private Button ExitBtn;
	
	@FXML
	private TextArea LogArea;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Evil Output Redirect!
		TextAreaHandler tah = new TextAreaHandler();
		tah.setTextArea(LogArea);
		Environment.getLogger().setLevel(java.util.logging.Level.FINEST);
		Environment.getLogger().addHandler(tah);
		
		model.textProperty().addListener((obs, oldText, newText) -> MajorWork(Integer.parseInt(newText)));
	}
	
	@SuppressWarnings("unchecked") //Relax! Nothing could go wrong.
	private void MajorWork(int node){
		final Repo objrepo = Environment.getRepos()[node];
		Task task = new Task<Void>() {
			@Override public Void call() throws Exception {
				//Metainfo
				ParserEngine parserEngine = new NewMoeEngine(); 
				RepoNetworkUtil networkUtil = new RepoNetworkUtil(objrepo);
				MetaResult remoteResult = null;
				this.updateMessage("......");
				Platform.runLater(() -> {
					ProgressABar.setProgress(0);
					ProgressBBar.setProgress(-1);
					ProgressCBar.setProgress(0);
					ProgressA.setText("下载更新信息 "+objrepo.getMetaFileName()+" ……");
				});
				for (int i = 0; i < Environment.getDownloadMaxTries(); i++) {
					try {
						String remoteJSONData = networkUtil.downloadRepoMetaAsString();
						remoteResult = parserEngine.decode(remoteJSONData);
						
						//TODO: Temporary JSON String process, should be replaced by proper process.
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append(remoteJSONData.trim());
						stringBuilder.insert(1,"\"node_url\":\""+objrepo.getUrl()+objrepo.getMetaFileName()+"\",");
						File file = new File(Environment.getUpdaterPath().resolve("metadata.json").toString());
						if (!file.exists()) file.createNewFile();
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),"utf-8"));
						bw.write(stringBuilder.toString());
						bw.close();
						break;
					} catch (java.io.IOException ex) {
						final int q = i;
						this.updateMessage("失败重试("+(q+1)+"/"+Environment.getDownloadMaxTries()+"): "+ex.getMessage());
					}
				}

				if(remoteResult == null)
					throw new UpdateCriticalException("无法下载更新信息，更新失败。请检查您的网络", 71);
				
				//ScanLocal
				this.updateMessage("......");
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.5);
					ProgressA.setText("扫描本地文件");
				});
				MetaScanner metaScanner = new MetaScanner(new FileScanner());
				MetaResult localResult = metaScanner.scan();
				
				//Compare
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.15);
					ProgressA.setText("比较确认缺少文件");
				});
				UpdateComparer updateComparer = new UpdateComparer(remoteResult, localResult);
				MetaResult compareResult = updateComparer.compare();
				
				//Download
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.2);
					ProgressA.setText("下载新文件");
				});
				if (!Environment.getCachePath().toFile().exists()) {
					if (!Environment.getCachePath().toFile().mkdirs())
						throw new UpdateCriticalException("无法创建缓存文件夹", 73);
				}

				for (int i=0;i<compareResult.getGlobalObjects().entrySet().size();i++) {
					Map.Entry<String, List<FileNode>> objectList = (Map.Entry<String, List<FileNode>>) compareResult.getGlobalObjects().entrySet().toArray()[i];
					final int fi = i;
					for (int j=0;j<objectList.getValue().size();j++) {
						final int fj = j;
						FileNode object = (FileNode) objectList.getValue().toArray()[j];
						final FileNode finalobj = object;
						Path savePath = Environment.getCachePath().resolve(object.getFile().getName());
						boolean hasCached = false;

						if(savePath.toFile().exists()) {
							String cacheFileMd5 = FileTool.getFileMD5(savePath.toFile());
							if(cacheFileMd5 != null && cacheFileMd5.equals(object.getExpectedMd5()))
								hasCached = true;
						}
						if(hasCached) {
							this.updateMessage("不需下载: " + finalobj.getPath());
							Platform.runLater(() -> {
								ProgressCBar.setProgress(fj/objectList.getValue().size());
								ProgressBBar.setProgress((fi+ProgressCBar.getProgress())/compareResult.getGlobalObjects().entrySet().size());
								ProgressABar.setProgress(0.2+0.5*ProgressBBar.getProgress());
							});
						} else {
							this.updateMessage("正在下载: " + finalobj.getPath());
							Platform.runLater(() -> {
								ProgressCBar.setProgress(fj/objectList.getValue().size());
								ProgressBBar.setProgress((fi+ProgressCBar.getProgress())/compareResult.getGlobalObjects().entrySet().size());
								ProgressABar.setProgress(0.2+0.5*ProgressBBar.getProgress());
							});
							int failNum = 0;
							for(; failNum < Environment.getDownloadMaxTries(); failNum++) {
								try {
									networkUtil.simpleDownloadFile(networkUtil.getRepoFileURL(object), savePath);

									String downloadedFileMd5 = FileTool.getFileMD5(savePath.toFile());

									if(downloadedFileMd5 == null || !downloadedFileMd5.equals(object.getExpectedMd5()))
										throw new FileDamagedException(String.format("下载的文件已损坏 ( 下载的文件: %s，服务器上的文件：%s", downloadedFileMd5, object.getExpectedMd5()));
									else
										break;
								} catch (Exception ex) {
									final int ffailnum = failNum;
									this.updateMessage(String.format("失败重试(%d/%d): %s -> %s", ffailnum+1, Environment.getDownloadMaxTries(), ex.getMessage(), finalobj.getPath()));
								}
							}
							if(failNum == Environment.getDownloadMaxTries())
								throw new UpdateCriticalException("无法下载新文件，请检查您的网络", 75);
						}
					}
				}
				
				//Merge
				try {
					if(!Environment.getUpdaterObjectPath().toFile().exists())
						FileUtils.forceMkdir(Environment.getUpdaterObjectPath().toFile());

					for (int i=0;i<compareResult.getGlobalObjects().entrySet().size();i++) {
						Map.Entry<String, List<FileNode>> objectList = (Map.Entry<String, List<FileNode>>) compareResult.getGlobalObjects().entrySet().toArray()[i];
						final String ffkey = objectList.getKey();
						this.updateMessage("正在合并: " + ffkey);
						Platform.runLater(() -> {
							ProgressABar.setProgress(0.7);
							ProgressBBar.setProgress(-1);
							ProgressCBar.setProgress(0);
							ProgressA.setText("合并对象");
						});
						ObjectEngine.mergeObject(objectList.getKey(), objectList.getValue());
					}
				} catch (IOException ex) {
					throw new UpdateCriticalException("合并文件对象失败：" + ex.getMessage(), 78);
				}
				
				//Apply
				this.updateMessage("......");
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.75);
					ProgressBBar.setProgress(-1);
					ProgressA.setText("应用更新");
				});
				FileUpdateApplier updateApplier = new FileUpdateApplier(compareResult);
				/*
				for (int i=0;i<compareResult.getFileNodesByType(MetaNodeType.ExcludedFile).getFileNodes().size();i++){
					final int fi = i;
					final FileNode fileNode = (FileNode) compareResult.getFileNodesByType(MetaNodeType.ExcludedFile).getFileNodes().toArray()[i];
					this.updateMessage("额外文件: " + fi);
					if (i % 30 == 0) Platform.runLater(() -> {
								ProgressCBar.setProgress(fi/compareResult.getFileNodesByType(MetaNodeType.ExcludedFile).getFileNodes().size());
								ProgressBBar.setProgress((ProgressCBar.getProgress())*0.2);
								ProgressABar.setProgress(0.75+0.15*ProgressBBar.getProgress());
							});
					FileHandler.delete(fileNode.getPath());
				}
				this.updateMessage("额外目录……");
				updateApplier.handleExcludedDirectoryNodes(compareResult.getDirectoryNodesByType(MetaNodeType.ExcludedDirectory));
				for (int i=0;i<compareResult.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().size();i++){
					final int fi = i;
					final FileNode fileNode = (FileNode) compareResult.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().toArray()[i];
					this.updateMessage("同步文件: " + fi);
					if (i % 30 == 0) Platform.runLater(() -> {
								ProgressCBar.setProgress(fi/compareResult.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes().size());
								ProgressBBar.setProgress((2+ProgressCBar.getProgress())*0.2);
								ProgressABar.setProgress(0.75+0.15*ProgressBBar.getProgress());
							});
					updateApplier.handleNewFiles(fileNode);
				}
				this.updateMessage("同步目录……");
				updateApplier.handleNewDirectoryNodes(compareResult.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
				this.updateMessage("默认文件……");
				for (int i=0;i<compareResult.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().size();i++){
					final int fi = i;
					final FileNode fileNode = (FileNode) compareResult.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().toArray()[i];
					this.updateMessage("默认文件: " + fi);
					if (i % 30 == 0) Platform.runLater(() -> {
								ProgressCBar.setProgress(fi/compareResult.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes().size());
								ProgressBBar.setProgress((4+ProgressCBar.getProgress())*0.2);
								ProgressABar.setProgress(0.75+0.15*ProgressBBar.getProgress());
							});
					updateApplier.handleNewFiles(fileNode);
				}*/
				
				updateApplier.start();
				
				//RegisterCustomMods
				this.updateMessage("......");
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.9);
					ProgressA.setText("注册自定义Mod");
				});
				UserFileRegister.registerUserMods();
				
				//CleanCache
				Platform.runLater(() -> {
					ProgressABar.setProgress(0.95);
					ProgressA.setText("清理缓存");
				});
				try {
					FileUtils.deleteDirectory(Environment.getCachePath().toFile());
				} catch (Exception ex) {
					Environment.getLogger().info("Clean cache failed: " + ex.getMessage());
				}
				
				//Finish
				final MetaResult frrs = remoteResult;
				if (compareResult.getGlobalObjects().entrySet().size()==0){
					this.updateMessage(frrs.getDescription());
					Platform.runLater(() -> {
						TitleBar.setText("无需更新");
						ProgressA.setText(String.format("已是最新版本 %s 发行时间: %s\n", frrs.getVersion(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(frrs.getTime()))));
					});
				} else {
					this.updateMessage(frrs.getDescription());
					Platform.runLater(() -> {
						TitleBar.setText("更新完成，谢谢！");
						ProgressA.setText(String.format("成功更新至 %s 发行时间: %s\n", frrs.getVersion(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(frrs.getTime()))));	
					});
				}
				return null;
			}
			
			@Override protected void succeeded() {
				super.succeeded();
				Platform.runLater(() -> {
					ProgressABar.setProgress(1);
					ProgressBBar.setProgress(1);
					ExitBtn.setText("启动游戏");
					ExitBtn.setDisable(false);
				});
			}
			
			@Override protected void failed() {
				super.failed();
				StringWriter sw = new StringWriter(); 
				getException().printStackTrace(new PrintWriter(sw, true)); 
				Environment.getLogger().log(java.util.logging.Level.SEVERE,sw.toString());
				this.updateMessage("请检查网络链接，再重试。若问题反复出现，请联系管理员。");
				Platform.runLater(() -> {
					ProgressABar.setProgress(0);
					ProgressBBar.setProgress(0);
					TitleBar.setText("更新失败");
					ProgressA.setText(getException().getMessage());
					ExitBtn.setText("退出");
					ExitBtn.setDisable(false);
				});
			}
		};
		ProgressB.textProperty().bind(task.messageProperty());
		new Thread(task).start();
	}
	
	private void ftlog(String content){
		LogArea.appendText(content);
		//LogArea.selectEnd();
		//LogArea.deselect();
	}
	
	public void ExitBtnClick(ActionEvent event){
		try {
			if (ExitBtn.getText().equals("启动游戏")) 
				Runtime.getRuntime().exec(new String[]{"java","-jar",Environment.getBaseMoeCraftPath()+"/launcher.jar"},new String[]{}, new File(Environment.getBaseMoeCraftPath()));
			Platform.exit();
		} catch (Exception ex) {
			TitleBar.setText("未能启动MoeCraft");
			ProgressA.setText("请检查问题，或试图手动启动MoeCraft。如果无法启动，请联系管理员。");
			ProgressB.setText(Environment.getBaseMoeCraftPath()+"/launcher.jar");
		}
	}

}