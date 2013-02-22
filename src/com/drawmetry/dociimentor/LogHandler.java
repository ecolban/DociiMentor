/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drawmetry.dociimentor;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Log handler used for logging. Publishes log records to a JTextArea
 * 
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class LogHandler extends Handler {

    public static final Logger LOGGER = UI.LOGGER;

    static {
        LOGGER.setLevel(Level.ALL);
    }
    
    private final JTextArea output;

    /**
     * Constructor
     * @param output the JTextArea where the messages are written.
     */
    public LogHandler(JTextArea output) {
        this.output = output;
    }

    @Override
    public void publish(final LogRecord record) {
        String message = record.getMessage();
        if (message != null) {
            Object[] params = record.getParameters();
            final long time = record.getMillis();
            final String msg = MessageFormat.format(message, params);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    output.append(MessageFormat.format(
//                            "{0, date, short}, {0, time, short}: {1}", 
                            "{1}", 
                            time, msg));
                }
            });
        }
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
