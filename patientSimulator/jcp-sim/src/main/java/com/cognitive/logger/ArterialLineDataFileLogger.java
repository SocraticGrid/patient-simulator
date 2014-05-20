package com.cognitive.logger; 

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author esteban
 */
public class ArterialLineDataFileLogger {

    private final FileWriter writer;
    
    private final DecimalFormat df = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.ENGLISH));
    private final DateFormat timeFormatter = new SimpleDateFormat("kk:mm:ss:SS");

    public ArterialLineDataFileLogger(File logFile) throws IOException {
        writer = new FileWriter(logFile);
        this.printHeaders();
    }

    public synchronized void write(double presp, double preal, double pmod) throws IOException {
        writer.append(timeFormatter.format(new Date()));
        writer.write(",");
        writer.append(df.format(presp)+","+df.format(preal)+","+df.format(pmod));
        writer.write("\n");
        writer.flush();
    }

    private void printHeaders() throws IOException {
        writer.append("TIME,PRESP,PREAL,PMOD");
        writer.append("\n");
        writer.flush();
    }

}
