package org.vincentyeh.IMG2PDF.commandline.action;

import org.vincentyeh.IMG2PDF.commandline.parser.MainActionParser;
import org.vincentyeh.IMG2PDF.commandline.parser.core.HandledException;

public class MainProgram {
    public static void main(String[] args) {
        MainActionParser parser = new MainActionParser();
        try {
            MainAction action = parser.parse(args);
            action.start();
        } catch (HandledException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
