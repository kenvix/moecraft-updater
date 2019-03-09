//--------------------------------------------------
// Class NodeSelectController
//--------------------------------------------------
// Written by Zbx1425 <zbx1425@outlook.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.gui;
import com.kenvix.utils.FileTool;
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
import net.moecraft.generator.updater.ui.UpdaterUI;
import net.moecraft.generator.updater.update.*;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

public class NodeSelectController implements Initializable {
	@FXML
    private AnchorPane rootPane;
	
	@FXML
	private Button StartBtn;
	
	@FXML
	private ChoiceBox NodeBox;
	
	@Override
	@SuppressWarnings("all") // Extremely bad habit! but I'm too lazy.
	public void initialize(URL location, ResourceBundle resources) {
		Repo[] repos = Environment.getRepos();
		UserFileRegister.createUserModsDir();
        //logln("说明: 该程序将于它所在的文件夹下的 MoeCraft 文件夹
		//安装本客户端, 并删除该文件夹内的其他 MineCraft 版本. 请勿把安装器与无关文件此文件夹内, 否则, 使用者需自行承担可能发生的数据损失.");
        //logln("注意: 如果你需要添加自定义 Mod, 请打开 Updater/Mods 文件夹(注意大小写), 并把你的 Mod 放入这个文件夹中. 不要把 Mod 直接放在 .minecraft/mods 中, 否则它们会被删除.");
		NodeBox.getItems().addAll(repos);
        NodeBox.converterProperty().set(new StringConverter<Repo>() {
			@Override
			public String toString(Repo object) {
				return object.getDescription();
			}

			@Override
			public Repo fromString(String string) {
				return null;
			}
		});
		NodeBox.setValue(repos[0]);
	}

	public void confirmNode(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("progress.fxml"));
            AnchorPane progress = (AnchorPane) loader.load();
			ProgressController control = (ProgressController) loader.getController();
			control.model.setText(Integer.toString(((Repo)NodeBox.getValue()).getOrder()));
			rootPane.getChildren().setAll(progress.getChildren());
		} catch (java.io.IOException e) {
			e.printStackTrace();
			//Whatever! I don't care this fucking bullshit.
		}
	}

}