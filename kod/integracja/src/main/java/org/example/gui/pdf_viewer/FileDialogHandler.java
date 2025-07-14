package org.example.gui.pdf_viewer;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.Optional;

public interface FileDialogHandler {

    static String getSaveFileDialogSelectedPath(String dialogTitle, String fileName, String fileExtension){

        FileDialog fileDialog = new FileDialog((Frame) null, dialogTitle, FileDialog.SAVE);

        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

        fileDialog.setDirectory(homeDirectory.getAbsolutePath());
        fileDialog.setFile(fileName + fileExtension);
//        fileDialog.setFilenameFilter((directory, name) -> name.endsWith(fileExtension));

        fileDialog.setVisible(true);

        String savedFileName = fileDialog.getFile();

        if (savedFileName == null) {
            return "";
        }

        return fileDialog.getDirectory() + savedFileName;
    }

    static Optional<File> getLoadFileDialogSelectedPath(String dialogTitle, String fileExtension){

        FileDialog fileDialog = new FileDialog((Frame) null, dialogTitle, FileDialog.LOAD);

        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

        fileDialog.setDirectory(homeDirectory.getAbsolutePath());
        fileDialog.setFile(fileExtension);
//        fileDialog.setFilenameFilter((directory, name) -> name.endsWith(fileExtension));

        fileDialog.setVisible(true);

        File[] gotFiles = fileDialog.getFiles();

        if(gotFiles.length == 0){

            return Optional.empty();
        }

        File gotFile = gotFiles[0];

        return Optional.of(gotFile);
    }

}
