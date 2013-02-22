package com.drawmetry.dociimentor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * An all trusting trust manager to bypass authentication of the server.
 * 
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class AllTrustingTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];//{rootCert};
    }

    @Override
    public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType)
            throws CertificateException {
    }
}
