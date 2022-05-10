package org.vincentyeh.IMG2PDF;

import org.fusesource.jansi.AnsiConsole;
import org.vincentyeh.IMG2PDF.commandline.MainCommandMaker;
import org.vincentyeh.IMG2PDF.setting.SettingManager;
import picocli.CommandLine;
import java.io.*;

public class MainProgram {

    public static void main(String[] args) {

        try {
            SettingManager.load();
            SettingManager.save();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Config file error");
            System.exit(2);
        }

        int exitCode;
        try {
            AnsiConsole.systemInstall();

            CommandLine cmd = MainCommandMaker.make();

            exitCode = cmd.execute(args);

        } catch (Error e) {
//            ErrorHandler handler = new ErrorHandler(null,);
//            try {
//                PrinterUtils.printColor(handler.handle(e), Ansi.Color.RED);
//            } catch (Handler.CantHandleException cantHandleException) {
//                cantHandleException.printStackTrace();
//            }
            exitCode = 100;
        } finally {
            AnsiConsole.systemUninstall();
        }

        System.exit(exitCode);

    }

}