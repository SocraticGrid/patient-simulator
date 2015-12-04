package com.cognitive.bp.poc.logger;

import com.cognitive.data.SimulationClockToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.jcpsim.data.JCpSimData;
import org.jcpsim.data.JCpSimParameter;

/**
 *
 * @author esteban
 */
public class JCpSimDataFileLogger {

    private final FileWriter writer;
    
    private final DecimalFormat df = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.ENGLISH));
    private final DateFormat timeFormatter = new SimpleDateFormat("kk:mm:ss:SS");

    private String[] logHeaders = new String[]{
        "SIM TIME",
        JCpSimParameter.TIME.name(),
        JCpSimParameter.AA_P_WAVE.name(),
        JCpSimParameter.AA_P_DAMP.name(),
        JCpSimParameter.AA_P_FLUSH.name(),
        JCpSimParameter.AA_P_CLINE.name(),
        JCpSimParameter.AA_P_LLINE.name(),
        JCpSimParameter.AA_P_RLINE.name(),
        JCpSimParameter.AA_P_MOD.name(),
        JCpSimParameter.AA_O_FLOW.name(),
        JCpSimParameter.AA_O_DAMP_COEFF.name(),
        JCpSimParameter.AA_O_FREQ.name(),
        JCpSimParameter.AA_O_PREAL.name(),
        JCpSimParameter.AA_O_PRESP.name()};

    public JCpSimDataFileLogger(File logFile) throws IOException {
        writer = new FileWriter(logFile);
        this.printHeaders();
    }

    public void processJCpSimData(JCpSimData data, SimulationClockToken clock) throws IOException {
        
        writer.append(df.format((double)((double)clock.getTimeMillis()/1000.00)));
        
        String separator = ",";
        for (String h : logHeaders) {
            writer.append(separator);
            if (h.equals("SIM TIME")) {
                writer.append(timeFormatter.format(new Date(System.currentTimeMillis())));
            } else if (h.equals(JCpSimParameter.TIME.name())) {
                writer.append(timeFormatter.format(data.get(JCpSimParameter.valueOf(h))));
            } else {
                writer.append(df.format(data.get(JCpSimParameter.valueOf(h))));
            }
        }
        writer.write("\n");
        writer.flush();
    }

    private void printHeaders() throws IOException {
        
         writer.append("EXEC TIME (s)");
        
        String separator = ",";
        for (String header : logHeaders) {
            writer.append(separator);
            writer.append(header);
        }
        writer.append("\n");
        writer.flush();
    }

}
