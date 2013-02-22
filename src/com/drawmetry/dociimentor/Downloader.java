package com.drawmetry.dociimentor;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.SwingUtilities;

/**
 * Instances of this class are used to download all missing files in the
 * documentList component {@link UI#documentList}. It implements a Runnable,
 * which runs in a separate thread without freezing the user interface.
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Downloader implements Runnable {

    private static final ResourceBundle messageBundle =
            ResourceBundle.getBundle("com/drawmetry/dociimentor/resources/MessageBundle");
    private static final String DOWNLOAD_ABORTED = messageBundle.getString("DOWNLOAD ABORTED");
    private static final String STARTING_DOWNLOAD = messageBundle.getString("STARTING DOWNLOAD");
    private static final String DOWNLOAD_COMPLETE = messageBundle.getString("DOWNLOAD COMPLETE");
    private final UI gui;
    private boolean abortFlag = false;

    public Downloader(UI gui) {
        this.gui = gui;
        AllTrustingTrustManager singleManager = new AllTrustingTrustManager();
        X509TrustManager[] trustManagers = {singleManager};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustManagers, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        UI.LOGGER.log(Level.INFO, String.format("%s\n", STARTING_DOWNLOAD));
        List<DocumentObject> fileMap = gui.getDocsToDownload();
        for (Iterator<DocumentObject> i = fileMap.iterator(); i.hasNext();) {
            DocumentObject doc = i.next();
            File dir = Configuration.getDirectory(doc.getFileName());
            if (dir.isDirectory() && new File(dir, doc.getFileName()).exists()) {
                i.remove();
            }
        }
        int missingFiles = fileMap.size();
        boolean local = isGriffinAccessible();
        try {
            for (Iterator<DocumentObject> i = fileMap.iterator(); !isAborted() && i.hasNext();) {
                DocumentObject docObj = i.next();
                UI.LOGGER.log(Level.INFO, String.format("[%d]", missingFiles));
                String fileName = docObj.getFileName();
                File dir = Configuration.getDirectory(fileName);
                downloadFile(docObj.getURL(), dir, docObj.getFileName(), local);
                gui.repaint();
                missingFiles--;
            }
            if (!isAborted()) {
                UI.LOGGER.log(Level.INFO, "{0}\n", DOWNLOAD_COMPLETE);
            } else {
                setAbort(false);
                UI.LOGGER.log(Level.INFO, "{0}\n", DOWNLOAD_ABORTED);
            }
        } catch (SocketException ex) {
            UI.LOGGER.log(Level.INFO, "{0}\n", DOWNLOAD_ABORTED);
        }
        releaseSynLock();
    }

    /**
     * @return the abortFlag
     */
    public synchronized boolean isAborted() {
        return abortFlag;
    }

    /**
     * @param flag the abortFlag to set
     */
    public synchronized void setAbort(boolean flag) {
        this.abortFlag = flag;
    }

    /**
     * Downloads the file that is specified by the URL into localDirectory
     * Pre-cond. localFileName != null && source is OK
     *
     * @param source URL from where the file is downloaded
     * @param localDirectory directory to which the file is saved.
     * @param localFileName the name of file saved.
     */
    private void downloadFile(URL source, File localDirectory, String localFileName, boolean griffin)
            throws SocketException {

        if (griffin) {
            try {
                downloadFileFromGriffin(source, localDirectory, localFileName);
            } catch (IOException ex) {
                downloadFileFromMentor(source, localDirectory, localFileName);
            }
        } else {
            downloadFileFromMentor(source, localDirectory, localFileName);
        }

    }

    private void downloadFileFromMentor(URL source, File localDirectory, String localFileName)
            throws SocketException {
        assert localFileName != null;
        assert localDirectory.isDirectory();
        File file = new File(localDirectory, localFileName);
        assert !file.exists();

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            HttpsURLConnection con = (HttpsURLConnection) source.openConnection();
            double contentLength = con.getContentLength();
            InputStream is = con.getInputStream();
            inputStream = new BufferedInputStream(is);
            UI.LOGGER.log(Level.INFO, String.format("Copying %s...", localFileName));
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[32 * 1024];
            int count = 0;
            int len;
            while ((len = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, len);
                count += len;
                gui.setDownloadProgress((int) (100 * count / contentLength));
            }
            UI.LOGGER.log(Level.INFO, String.format("%d bytes downloaded.\n", count));
        } catch (SocketException ex) {
            throw ex;
        } catch (MalformedURLException ex) {
            UI.LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());
        } catch (IOException ex) {
            UI.LOGGER.log(Level.SEVERE, "Exception: {0}\n", ex.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
            gui.setDownloadProgress(0);
        }
    }

    private void downloadFileFromGriffin(URL source, File localDirectory, String localFileName)
            throws SocketException, IOException {
        assert localFileName != null;
        assert localDirectory.isDirectory();
        File file = new File(localDirectory, localFileName);
        assert !file.exists();

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            URL griffinSource = mapToGriffin(source);
            HttpURLConnection con = (HttpURLConnection) griffinSource.openConnection();
            InputStream is = con.getInputStream();
            inputStream = new BufferedInputStream(is);
            UI.LOGGER.log(Level.INFO, String.format("Downloading %s...", localFileName));
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buff = new byte[32 * 1024];
            int count = 0;
            int len;
            while ((len = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, len);
                count += len;
            }
            UI.LOGGER.log(Level.INFO, String.format("%d bytes downloaded.\n", count));
        } catch (SocketException ex) {
            throw ex;
        } catch (MalformedURLException ex) {
            UI.LOGGER.log(Level.SEVERE, "{0}\n", ex.getMessage());

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public void downloadNow(DocumentObject docObj) {
        URL source = docObj.getURL();
        String fileName = docObj.getFileName();
        File localDirectory = Configuration.getDirectory(fileName);
        try {
            downloadFile(source, localDirectory, fileName, isGriffinAccessible());
        } catch (SocketException ex) {
            UI.LOGGER.log(Level.INFO, "{0}\n", DOWNLOAD_ABORTED);
        }
    }

    private void releaseSynLock() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.setSyncLock(false);
            }
        });
    }

    private URL mapToGriffin(URL source) throws MalformedURLException {
        String path = source.getPath();
        Pattern sourcePattern = Pattern.compile("(.*)/dcn(.*)");
        Matcher m = sourcePattern.matcher(path);
        if (m.matches()) {
//            return new URL("http://griffin.event.ieee.org/docs" + m.group(1) + m.group(2));
            return new URL("http://173.197.99.167/docs" + m.group(1) + m.group(2));
        } else {
            throw new MalformedURLException();
        }
    }

    private boolean isGriffinAccessible() {
        try {
            URL griffin = new URL("http://griffin.event.ieee.org");
//            URL griffin = new URL("http://173.197.99.167");
            HttpURLConnection con = (HttpURLConnection) griffin.openConnection();
            con.connect();
        } catch (UnknownHostException ex) {
            return false;
        } catch (IOException ex) {
            UI.LOGGER.log(Level.SEVERE, ex.getMessage());
        }
        return true;
//        return false;
    }

    public static void main(String[] args) {
        Downloader dl = new Downloader(null);
        boolean local = dl.isGriffinAccessible();
        System.out.println("local = " + local);
    }
}