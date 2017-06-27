package com.vfinworks.vfsdk.Utils;

import java.util.Map;

public class RSAKey {

    static String publicKey;
    public static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z/XcKWAmUT8J9AkEA8i0WT/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";


    static String charset = "UTF-8";
    static {
        try {
            Map<String, Object> keyMap = RSA.genKeyPair();
                        publicKey = RSA.getPublicKey(keyMap);
                        privateKey = RSA.getPrivateKey(keyMap);
//            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCqxy4Z7eMXulIrKaqJaWt85NU"
//                    + "pBoKmGL7Dj9H7C4xloA4p4RQQKw6viFfpYqtatLMl7egWgAxpxZlY5+e8WwEhV80"
//                    + "zVLPqy3ito7yD/0o2LnxBiOA+ZrJFEJGWH9bZEcTP9qWkmDvjdxClD339/mkCsFg"
//                    + "Pvhr/miFPf0Rk+pjvQIDAQAB";
//
//            privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMKrHLhnt4xe6Uis"
//                    + "pqolpa3zk1SkGgqYYvsOP0fsLjGWgDinhFBArDq+IV+liq1q0syXt6BaADGnFmVj"
//                    + "n57xbASFXzTNUs+rLeK2jvIP/SjYufEGI4D5mskUQkZYf1tkRxM/2paSYO+N3EKU"
//                    + "Pff3+aQKwWA++Gv+aIU9/RGT6mO9AgMBAAECgYBruFYPMM1frpF2dptPIb/5bwSC"
//                    + "3L/QRxzWgb7ApM+2/un676+G3RKw+s7q52bCqY72SaoB4GulDimVdzg3sq4rJkzu"
//                    + "vUuogSuiis4iUUFGn20cCCCekV1vvvF4CSt/FPJijZEdruRXbn5jAY5JVVvgSHrP"
//                    + "dJa85cbcVyaEGMw6YQJBAPNEPddgfXP+wXfm6sRyEUoTQLJyNQ9UDQ8q8ue1ydnA"
//                    + "sAZtNntr+jCjai3rElGnjbXQhiAZ/wtjHCJv2c9FKdkCQQDM26hwsf4XyLXxTfPx"
//                    + "duO89gX3gnYUXOgNWxnY+DauAD00P4gO+tfvi5w8KE9c6T2AIMbsh1j5r8oSbqZl"
//                    + "mxaFAkBROyxbSwEZRqxb2WPzjRNw5NTpwXEuWSazNeg+r1ljuRAOVVGoDPpSW38N"
//                    + "Lj3Dvmt3ltXyyjt8FfBDH45fw/yhAkA7ZBNZWMEJtC7LXoYyov0zc0AXmcMR9D1y"
//                    + "Yc8EkDGKEJet5h1T+nVQBXGuHyGjFhu2YcpKGJM7EDPNVDb5jhThAkBvDuFK23ZC"
//                    + "BC5FbeaRwdx/X1Ah+frzq6yGGRIP5CQ1oOtEOzpAaKg4HEh5sBtvbf6Z22uXF7kr"
//                    + "aQHkoUXbAMGS";

//            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDD0IPzcpbbrmapX74q+a/AcwSQ6SftVnP6/8e4pFtHo9dV8wevHfb+R2HWTMcCl0aV2/X8n61ZDFZ2TcuzD3yvN6oSy94hjkrEOiOVS8+80I9jvIN0xwj+JqpgRbrDFypKPQMKbD1QHKaDSZuUi0SSWKE6IuGdruGeLKWU82OwmwIDAQAB";
//            privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMPQg/NyltuuZqlfvir5r8BzBJDpJ+1Wc/r/x7ikW0ej11XzB68d9v5HYdZMxwKXRpXb9fyfrVkMVnZNy7MPfK83qhLL3iGOSsQ6I5VLz7zQj2O8g3THCP4mqmBFusMXKko9AwpsPVAcpoNJm5SLRJJYoToi4Z2u4Z4spZTzY7CbAgMBAAECgYATexMljSjlAhzEjPnmXKDXjJnPsOKAAjHXrsq32+IY5nvt2RfSnDCOa5uYLls+Lr6e+hsm3tvoGAoiCutRDSFiQKKdWq/8A4xeC7CxQNRujBAs6gS5fhhNUrm8471XFO1mF4vIdi2Je/hjX76QuHX1POE2T20DV7Oi5RW3tmz4QQJBAOFVnEbqWXdfJvxrJrtnSWahU3yn0NmMrsY2SiHXFQBzLofgk2/fYKK0KqJb0cvPaxjZEl2nFIVf96OwbIFmm4UCQQDedndA5RHES1r2Jx5n+olh0C9CnuERia6/WgfKR8U+bm4f28ol6Hiu9TL0ca+gGTWWORwfj915BDfwlwz0eYWfAkBsl1RDvKY2580i8gRtZb4yzmYsebclUC3d6cXZ/wvo9pki9DA5Rp4MauTs73DwVloXVG0MYvt5tyDhaqEvzyH9AkAWoYCSNntvN6dCQUqDk2YkcDROl7EXwqTnTHZcap6zMjK7xPU0lAiq68DKQ0J1i/r6lEa7IzyJkhdKv2MO/8nLAkEAhSkbHGpgDrhd//j+FjJgMBEzVlxOOj7s9h/wJmilGjWqd2mJPyyQYx+1q1hbDxaEzHdUvglGhsgRNIwfR9pX2A==";
//            privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMKrHLhnt4xe6Uispqolpa3zk1SkGgqYYvsOP0fsLjGWgDinhFBArDq+IV+liq1q0syXt6BaADGnFmVjn57xbASFXzTNUs+rLeK2jvIP/SjYufEGI4D5mskUQkZYf1tkRxM/2paSYO+N3EKUPff3+aQKwWA++Gv+aIU9/RGT6mO9AgMBAAECgYBruFYPMM1frpF2dptPIb/5bwSC3L/QRxzWgb7ApM+2/un676+G3RKw+s7q52bCqY72SaoB4GulDimVdzg3sq4rJkzuvUuogSuiis4iUUFGn20cCCCekV1vvvF4CSt/FPJijZEdruRXbn5jAY5JVVvgSHrPdJa85cbcVyaEGMw6YQJBAPNEPddgfXP+wXfm6sRyEUoTQLJyNQ9UDQ8q8ue1ydnAsAZtNntr+jCjai3rElGnjbXQhiAZ/wtjHCJv2c9FKdkCQQDM26hwsf4XyLXxTfPxduO89gX3gnYUXOgNWxnY+DauAD00P4gO+tfvi5w8KE9c6T2AIMbsh1j5r8oSbqZlmxaFAkBROyxbSwEZRqxb2WPzjRNw5NTpwXEuWSazNeg+r1ljuRAOVVGoDPpSW38NLj3Dvmt3ltXyyjt8FfBDH45fw/yhAkA7ZBNZWMEJtC7LXoYyov0zc0AXmcMR9D1yYc8EkDGKEJet5h1T+nVQBXGuHyGjFhu2YcpKGJM7EDPNVDb5jhThAkBvDuFK23ZCBC5FbeaRwdx/X1Ah+frzq6yGGRIP5CQ1oOtEOzpAaKg4HEh5sBtvbf6Z22uXF7kraQHkoUXbAMGS";
//            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCqxy4Z7eMXulIrKaqJaWt85NUpBoKmGL7Dj9H7C4xloA4p4RQQKw6viFfpYqtatLMl7egWgAxpxZlY5+e8WwEhV80zVLPqy3ito7yD/0o2LnxBiOA+ZrJFEJGWH9bZEcTP9qWkmDvjdxClD339/mkCsFgPvhr/miFPf0Rk+pjvQIDAQAB";

//            privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO/St2yfkVg+fQSOwI+oPIixhFrA1YYkNEckFx0KV2h2Mzlum975dhvhGCbfoXqIOPENsRBun8eFU+PReAaUJEqluW7Ikm3yBrmqSoiFag538KZrV8K2+IxR6nbWEiWDVkVxYqAdCgag1fvExPz/RsSyV1op3DE6W/eobFZ+o1XlAgMBAAECgYEAkNdFuo/gu1f6L2978wdbj/8OfbUrAHoLLNAqOXrOcaO5qj0YXKFzaMEY0hmMMDHnmgZ34wG89Eac9OHEyMxd4aAxt9elqJ5uCPTUq0s6OXHYQS+K7Ui1O9ET4qhhv3l+iTkDuI3WkC1WMMoPy4hCdDyLi5SB9mNzUioD5NO6I50CQQD+S7F0avdIdb2dyzpUZO2P7sHSqsFJh0qx9aIlUxEuXf1R/gV8PAaa/Rvp/nBfoJQczOdjlIETtVd6ON4B3ck7AkEA8W4xK5Qm/587A2L7e4nAemgng1aFG1Ziy8Bdu9yhqeKGqy5KZ6/ucbNzgv3TSI+e30maFCXyTpJbt5hXuw5rXwJAb1ON7YyCk4tQJst4zsecpP1+hw7QLbN4BO6nPLXf+K+XHhUaK6hPr/yNAuSsJ4EyNmWSCytRUuJ52H0a3DBPTwJATk/Z7zLNO5lgwQZ4YGgcYRgryPRllKp+vWyWevtkDQEHgbswM1Xj2EnHLDQ9NoovlGoBaousf0QGMBVgT7KrrQJAZ0/54l3kPPaIGOiq4yZdtk0Tb1bHR0Z2Fsmot0FYoktutxU8YmYR2joZW0Rl1aEEHbhlD4hAwesJvN4r/gLkPA==";
//            publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDv0rdsn5FYPn0EjsCPqDyIsYRawNWGJDRHJBcdCldodjM5bpve+XYb4Rgm36F6iDjxDbEQbp/HhVPj0XgGlCRKpbluyJJt8ga5qkqIhWoOd/Cma1fCtviMUep21hIlg1ZFcWKgHQoGoNX7xMT8/0bEsldaKdwxOlv3qGxWfqNV5QIDAQAB";
//            privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAO/6rPCvyCC+IMalLzTy3cVBz/+wamCFNiq9qKEilEBDTttP7Rd/GAS51lsfCrsISbg5td/w25+wulDfuMbjjlW9Afh0p7Jscmbo1skqIOIUPYfVQEL687B0EmJufMlljfu52b2efVAyWZF9QBG1vx/AJz1EVyfskMaYVqPiTesZAgMBAAECgYEAtVnkk0bjoArOTg/KquLWQRlJDFrPKP3CP25wHsU4749t6kJuU5FSH1Ao81d0Dn9m5neGQCOOdRFi23cV9gdFKYMhwPE6+nTAloxI3vb8K9NNMe0zcFksva9c9bUaMGH2p40szMoOpO6TrSHO9Hx4GJ6UfsUUqkFFlN76XprwE+ECQQD9rXwfbr9GKh9QMNvnwo9xxyVl4kI88iq0X6G4qVXo1Tv6/DBDJNkX1mbXKFYL5NOW1waZzR+Z/XcKWAmUT8J9AkEA8i0WT/ieNsF3IuFvrIYG4WUadbUqObcYP4Y7Vt836zggRbu0qvYiqAv92Leruaq3ZN1khxp6gZKl/OJHXc5xzQJACqr1AU1i9cxnrLOhS8m+xoYdaH9vUajNavBqmJ1mY3g0IYXhcbFm/72gbYPgundQ/pLkUCt0HMGv89tn67i+8QJBALV6UgkVnsIbkkKCOyRGv2syT3S7kOv1J+eamGcOGSJcSdrXwZiHoArcCZrYcIhOxOWB/m47ymfE1Dw/+QjzxlUCQCmnGFUO9zN862mKYjEkjDN65n1IUB9Fmc1msHkIZAQaQknmxmCIOHC75u4W0PGRyVzq8KkxpNBq62ICl7xmsPM=";
//            publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDv+qzwr8ggviDGpS808t3FQc//sGpghTYqvaihIpRAQ07bT+0XfxgEudZbHwq7CEm4ObXf8NufsLpQ37jG445VvQH4dKeybHJm6NbJKiDiFD2H1UBC+vOwdBJibnzJZY37udm9nn1QMlmRfUARtb8fwCc9RFcn7JDGmFaj4k3rGQIDAQAB";

            System.err.println("公钥: \n\r" + publicKey);
            System.err.println("私钥： \n\r" + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
         test();
//        testSign();
    }

    static void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String source = "1qaz2wsx";
//        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSA.encryptByPublicKey(data, publicKey);
        System.out.println("加密后文字：\r\n" + new String(encodedData));
        byte[] decodedData = RSA.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
    }

    static void testSign() throws Exception {
        System.err.println("私钥加密——公钥解密");
//        String source = "trade_list=[{\"out_trade_no\":\"160416001448777001\",\"subject\":\"乐乐发2期8\",\"price\":\"10.00\",\"quantity\":\"1\",\"total_amount\":\"10.00\",\"seller\":\"18511885952\",\"seller_type\":\"101\",\"notify_url\":\"http://10.11.146.241:8012/cashier/asyPayCallBack\"}]";
        String source = "[{\"out_trade_no\":\"3436394\",\"subject\":\"牛奶\",\"total_amount\":\"0.01\",\"seller\":\"15721476821\",\"seller_type\":\"MOBILE\",\"price\":\"0.01\",\"quantity\":1}]";
        System.out.println("原文字：\r\n" + source);
        byte[] data = source.getBytes();
//        byte[] encodedData = RSA.encryptByPrivateKey(data, privateKey);
//        System.out.println("加密后：\r\n" + new String(encodedData));
//        byte[] decodedData = RSA.decryptByPublicKey(encodedData, publicKey);
//        String target = new String(decodedData);
//        System.out.println("解密后: \r\n" + target);
//        System.err.println("私钥签名——公钥验证签名");
        String sign = RSA.sign(new String(data, charset), privateKey, charset);
        System.err.println("签名:\r" + sign);
        boolean status = RSA.verify(new String(data, charset), sign, publicKey, charset);
        System.err.println("验证结果:\r" + status);
    }

}