package com.drawmetry.dociimentor;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 *  @author Erik Colban &copy; 2012 <br> All Rights Reserved Worldwide
 */
public class Test {

    public static void main(String[] args) {
        try {
            System.setProperty("java.net.useSystemProxies", "false");
            List<Proxy> l = ProxySelector.getDefault().select(
                    new URI("http://www.java-tips.org/java.net/how-to-detect-proxy-settings-for-internet-connection.html/"));

            for (Iterator<Proxy> iter = l.iterator(); iter.hasNext();) {

                Proxy proxy = (Proxy) iter.next();

                System.out.println("proxy host type : " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (addr == null) {

                    System.out.println("No Proxy");

                } else {

                    System.out.println("proxy host name : "
                            + addr.getHostName());

                    System.out.println("proxy port : "
                            + addr.getPort());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Properties props = System.getProperties();
        for (String s : props.stringPropertyNames()) {
            System.out.println(s + "=" + props.getProperty(s));
        }
    }
}
