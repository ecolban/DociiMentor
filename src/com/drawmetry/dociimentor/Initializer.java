package com.drawmetry.dociimentor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.SwingUtilities;

/**
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Initializer {

    private File docDir;
    private static final Logger LOGGER = Logger.getLogger("com.drawmetry.dociimentor");

    public static void main(String[] args) {
        LOGGER.setLevel(Level.ALL);
        new Initializer().checkConfigurationSettings();
        Configuration.initialize();
        SwingUtilities.invokeLater(new UI());
    }

    private void checkConfigurationSettings() {
        File file = new File(System.getProperty("user.dir"));
        assert file.exists();
        file = new File(file, ".dociimentor");
        boolean configDirExists = file.exists();
        if (!configDirExists) {
            configDirExists = file.mkdir();
        }
        if (!configDirExists) {
            assert false;
        }
        file = new File(file, "dociiconfig.xml");
        if (!file.exists()) {
            ConfigurationDialog dialog = new ConfigurationDialog(null, true);
            dialog.setVisible(true);
            docDir = dialog.getSelectedDirectory();
            dialog.dispose();
            dialog = null;
            try {
                InputStream configInStream = this.getClass().getResourceAsStream("resources/dociiconfig.xml");
                assert configInStream != null;
                filter(configInStream, file);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        try {
            File logFile = new File(System.getProperty("user.dir"), ".dociimentor/logfile.txt");
            Handler logFileHandler = new StreamHandler(
                    new FileOutputStream(logFile), new SimpleFormatter());
            LOGGER.addHandler(logFileHandler);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Initializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filter(InputStream streamIn, File fileOut) throws IOException {
        String lineIn;
        String lineOut;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(streamIn));
            writer = new BufferedWriter(new FileWriter(fileOut));
            while ((lineIn = reader.readLine()) != null) {
                if (lineIn.matches("\\s*<localfiles.*>\\s*")) {
                    lineOut =
                            "    <localfiles root = \"" + docDir + "\">\n";
                } else {
                    lineOut = lineIn + "\n";
                }
                writer.write(lineOut);
            }

        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
