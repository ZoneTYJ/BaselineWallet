package com.vfinworks.vfsdk.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by xiaoshengke on 2016/4/18.
 */
public class SSLUtils {
    private static String CER_BAIDU = "-----BEGIN CERTIFICATE-----\n" +
            "MIIF5DCCBMygAwIBAgIQHM96/WM5QJFtNu3YzKSD3DANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\n" +
            "EwJVUzEdMBsGA1UEChMUU3ltYW50ZWMgQ29ycG9yYXRpb24xHzAdBgNVBAsTFlN5bWFudGVjIFRy\n" +
            "dXN0IE5ldHdvcmsxLzAtBgNVBAMTJlN5bWFudGVjIENsYXNzIDMgU2VjdXJlIFNlcnZlciBDQSAt\n" +
            "IEc0MB4XDTE1MDkxNzAwMDAwMFoXDTE2MDgzMTIzNTk1OVowgagxCzAJBgNVBAYTAkNOMRAwDgYD\n" +
            "VQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMTowOAYDVQQKDDFCZWlqaW5nIEJhaWR1IE5l\n" +
            "dGNvbSBTY2llbmNlIFRlY2hub2xvZ3kgQ28uLCBMdGQuMSUwIwYDVQQLDBxzZXJ2aWNlIG9wZXJh\n" +
            "dGlvbiBkZXBhcnRtZW50MRIwEAYDVQQDDAliYWlkdS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IB\n" +
            "DwAwggEKAoIBAQCocs/rdlG7AR4WURwOZFsmWfVbgiAWPnd4YsDi9lMeCS1itCcHOl2bmjwEL2kL\n" +
            "HmSZpvDm2GyCfgoAcsGMJ57ysmtsBmVQoLMNKvrf+6z0MmGsp1k7LIIYwPvXAA7YCH5THt+wpOvu\n" +
            "MCgn68XdgsUgcy5eQFHt5idy6sAkml3C+BuwYSW+Xi+7HBHWoNHwMAfFKEpaTCQjskBodDvtk9eH\n" +
            "EibEAQ8KCWh0HF0YqbJr106y7DYLkrjGtp7KTlm9JnnSleFpLehKrCxE0cYzq35v2Spy4Dtky6sb\n" +
            "0wXbxnaK7msUKu9ZSCo9C5PdbnIuo+vQO4kNipJV3QKJxJMuz86vAgMBAAGjggIxMIICLTCB5gYD\n" +
            "VR0RBIHeMIHbggsqLmJhaWR1LmNvbYILKi5udW9taS5jb22CDCouaGFvMTIzLmNvbYIOKi5iZHN0\n" +
            "YXRpYy5jb22CEHd3dy5iYWlkdS5jb20uY26CDHd3dy5iYWlkdS5jboISc2FwaS5tYXAuYmFpZHUu\n" +
            "Y29tghFsb2MubWFwLmJhaWR1LmNvbYIQbG9nLmhtLmJhaWR1LmNvbYIJYmFpZHUuY29tghFhcGku\n" +
            "bWFwLmJhaWR1LmNvbYIVY29uc29sZS5iY2UuYmFpZHUuY29tghNsb2dpbi5iY2UuYmFpZHUuY29t\n" +
            "MAkGA1UdEwQCMAAwDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcD\n" +
            "AjBhBgNVHSAEWjBYMFYGBmeBDAECAjBMMCMGCCsGAQUFBwIBFhdodHRwczovL2Quc3ltY2IuY29t\n" +
            "L2NwczAlBggrBgEFBQcCAjAZGhdodHRwczovL2Quc3ltY2IuY29tL3JwYTAfBgNVHSMEGDAWgBRf\n" +
            "YM9hkFXfhEMUimAqsvV69EMY7zArBgNVHR8EJDAiMCCgHqAchhpodHRwOi8vc3Muc3ltY2IuY29t\n" +
            "L3NzLmNybDBXBggrBgEFBQcBAQRLMEkwHwYIKwYBBQUHMAGGE2h0dHA6Ly9zcy5zeW1jZC5jb20w\n" +
            "JgYIKwYBBQUHMAKGGmh0dHA6Ly9zcy5zeW1jYi5jb20vc3MuY3J0MA0GCSqGSIb3DQEBCwUAA4IB\n" +
            "AQB2r0YDO73Ynz4rYkjdjYkyB0kgIAdOM8IlT76442KFIp9ri38LzH8/QqRf4G9Enpw4cUkvngCY\n" +
            "dvhmF7rtuoIaU5icBKOKbP87T/9X61CdRMvIGHkaIJ/0Gc9drLRfDFm7D7GPdZoJQ1RJE1oHhNVI\n" +
            "llfzXIBBuWibgB6Vu7WPaxxa4JaWTPCsEDeH9Cb2X2vhE3jj5S3M3kpHXSEgvthhq6ovPu07sFKz\n" +
            "Knlu+JDkRQcOkkpENVueeAy9BzhKD+9DWZSBu7hlydCI03wbjwnctx8u8P0matViAHC2Ktpo5aPn\n" +
            "LZXoceuCkUDz5Ns0GqImjaakv1Eo75edVSM3uKz4" +
            "\n-----END CERTIFICATE-----";

    public static SSLSocketFactory getSSLSocketFactory() {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");

//        InputStream in = getAssets().open("zhy_server.cer");
//        InputStream in = getAssets().open("baidu.cer");
            //采用字符串
            ByteArrayInputStream in = new ByteArrayInputStream(CER_BAIDU.getBytes());
            Certificate ca = cf.generateCertificate(in);

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keystore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), new SecureRandom());
            return context.getSocketFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
