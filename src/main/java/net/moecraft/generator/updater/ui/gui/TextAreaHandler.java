//--------------------------------------------------
// Class TextAreaHandler
//--------------------------------------------------
// Copy-Paste by Zbx1425 <zbx1425@outlook.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.gui;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.logging.Handler.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.concurrent.Task;
import javafx.application.Platform;

public class TextAreaHandler extends StreamHandler {
    TextArea textArea = null;

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();

        Platform.runLater(() -> {
            textArea.appendText("[" + record.getLevel().getName() + "]" + record.getMessage() + "\n");
        });
    }
}