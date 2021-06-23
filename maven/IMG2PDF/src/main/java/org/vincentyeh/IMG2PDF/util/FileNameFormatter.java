package org.vincentyeh.IMG2PDF.util;

import org.vincentyeh.IMG2PDF.util.interfaces.NameFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * https://regex101.com/
 *
 * @author VincentYeh
 */
public class FileNameFormatter extends NameFormatter<File> {
    private enum CurrentTime {
        year("$CY"), month("$CM"), day("$CD"), hour("$CH"), minute("$CN"), second("$CS");

        private final String symbol;

        CurrentTime(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private enum ModifyTime {
        year("$MY"), month("$MM"), day("$MD"), hour("$MH"), minute("$MN"), second("$MS");

        private final String symbol;

        ModifyTime(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public FileNameFormatter(File file) {
        super(file);
    }

    private void getCurrentTimeMap(Date current_date, HashMap<String, String> map) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(current_date);
        map.put(CurrentTime.year.getSymbol(), String.format("%d", cal.get(Calendar.YEAR)));
        map.put(CurrentTime.month.getSymbol(), String.format("%02d", cal.get(Calendar.MONTH) + 1));
        map.put(CurrentTime.day.getSymbol(), String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
        map.put(CurrentTime.hour.getSymbol(), String.format("%02d", cal.get(Calendar.HOUR)));
        map.put(CurrentTime.minute.getSymbol(), String.format("%02d", cal.get(Calendar.MINUTE)));
        map.put(CurrentTime.second.getSymbol(), String.format("%02d", cal.get(Calendar.SECOND)));
    }

    private void getModifyTimeMap(Date current_date, HashMap<String, String> map) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(current_date);
        map.put(ModifyTime.year.getSymbol(), String.format("%d", cal.get(Calendar.YEAR)));
        map.put(ModifyTime.month.getSymbol(), String.format("%02d", cal.get(Calendar.MONTH) + 1));
        map.put(ModifyTime.day.getSymbol(), String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
        map.put(ModifyTime.hour.getSymbol(), String.format("%02d", cal.get(Calendar.HOUR)));
        map.put(ModifyTime.minute.getSymbol(), String.format("%02d", cal.get(Calendar.MINUTE)));
        map.put(ModifyTime.second.getSymbol(), String.format("%02d", cal.get(Calendar.SECOND)));
    }

    private void getFileMap(HashMap<String, String> map)
            throws NumberFormatException {
        Path p = getData().toPath();
        map.put("$NAME", p.getFileName().toString().split("\\.")[0]);

        for (int i = 1; i < p.getNameCount(); i++) {
            map.put("$PARENT{" + (i - 1) + "}", p.getName(p.getNameCount() - 1 - i).getFileName().toString());
        }

        if (p.isAbsolute())
            map.put("$ROOT", p.getRoot().toString());
    }
    @Override
    public String format(String format) {
        HashMap<String, String> map = new HashMap<>();

        getFileMap(map);
        getCurrentTimeMap(new Date(), map);
        getModifyTimeMap(new Date(getData().lastModified()), map);

        for (String key : map.keySet()) {
            format = format.replace(key, map.get(key));
        }

        return format;
    }

}
