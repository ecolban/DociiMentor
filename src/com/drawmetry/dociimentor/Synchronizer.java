package com.drawmetry.dociimentor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingUtilities;

/**
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Synchronizer implements Runnable {

    private final UI ui;
    private final URL hostUrl;
    private final String path;
    private final String query;
    private boolean abortFlag = false;
    private static final ResourceBundle messageBundle =
            ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle");
    private static final String STARTING_SYNC = messageBundle.getString("STARTING SYNC");
    private static final String SYNC_ABORTED = messageBundle.getString("SYNC ABORTED");
    private static final String SYNC_COMPLETE = messageBundle.getString("SYNC COMPLETE");
    private static final String NUM_NEW_DOCS_ON_PAGE = messageBundle.getString("NUM NEW DOCS FOUND ON PAGE");

    public Synchronizer(UI ui, URL hostUrl, String path, String query) {
        this.ui = ui;
        this.hostUrl = hostUrl;
        this.path = path;
        this.query = query;
        AllTrustingTrustManager trustManager = new AllTrustingTrustManager();
//                    trustManager.init();
        X509TrustManager[] trustAllCerts = {trustManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        UI.LOGGER.log(Level.INFO, String.format("%s\n", STARTING_SYNC));
        URL url = null;
        try {
            PageHandler handler = new PageHandler(ui);
            do {
                handler.newPage();
                url = new URL(hostUrl, path + "?o=7d&n=" + handler.getPage() + query);
//                SocketAddress addr = new InetSocketAddress("192.168.1.72", 9666);
//                Proxy proxy = new Proxy(Proxy.Type.HTTP,addr);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                java.lang.String line = null;
                while ((line = input.readLine()) != null) {
                    handler.readLine(line);
                }
                UI.LOGGER.log(Level.INFO, NUM_NEW_DOCS_ON_PAGE + "\n",
                        new Object[]{handler.getNumDocsOnPage(), handler.getPage()});

            } while (!handler.done() && !isAborted());
        } catch (SocketException ex) {
            UI.LOGGER.log(Level.INFO, "{0}\n", SYNC_ABORTED);
        } catch (MalformedURLException ex) {
            UI.LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            UI.LOGGER.log(Level.SEVERE, null, ex);
        }
        if (!isAborted()) {
            UI.LOGGER.log(Level.INFO, String.format("%s\n", SYNC_COMPLETE));
        } else {
            setAbort(false);
            UI.LOGGER.log(Level.INFO, String.format("%s\n", SYNC_ABORTED));
        }
        releaseSynLock();
    }

    private synchronized boolean isAborted() {
        return abortFlag;
    }

    public synchronized void setAbort(boolean b) {
        abortFlag = b;
    }

    private void releaseSynLock() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ui.setSyncLock(false);
            }
        });
    }
}
