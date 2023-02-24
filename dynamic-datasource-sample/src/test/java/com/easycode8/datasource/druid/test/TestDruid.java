package com.easycode8.datasource.druid.test;
import com.alibaba.druid.filter.config.ConfigTools;


public class TestDruid {
    public static void main(String[] args) throws Exception {

        // 生产公钥和私钥 并使用私钥加密
        ConfigTools.main(new String[]{"123456"});



        String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEA17m8rOxVAoh6O8Pg8NXjIenR10MbBDAVZrrcA8Oj+XCznj1+McbTn2ZOZI7c4yPsiOBZ29U4Ai5Tz32tyIukHwIDAQABAkEAzVjQcQrGIRMox5s9lAMFolKgnPgTz3i15oQYyryAaf9OS2EVm5u1RPoGrJN6Olm5w4p/YkUnJEnD6zrVTfO0AQIhAPOb/nQN7raVhJ4mrGcuJ5UKlsDtvBqAlzi9c/9Gvhk/AiEA4rKr5XNqhuBStBalLE92YgKBQ2Qh3/IwlIRacEsB3SECIG0hkdENuMXEn3911b/3VjYXY0eUeLLqrlP10zQlzd1jAiBPYs47ba1ySf/sPLneyHzmWYY2uUv7hm5os+iPxlSCwQIhAKqT7tO3OZf9y4/GoZCQXbP8lAVRceAlGYN07VI5xbEx";
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANe5vKzsVQKIejvD4PDV4yHp0ddDGwQwFWa63APDo/lws549fjHG059mTmSO3OMj7IjgWdvVOAIuU899rciLpB8CAwEAAQ==";

        String myEncryptedPassword01  = ConfigTools.encrypt(privateKey, "123456");
        System.out.println("私钥加密:" + myEncryptedPassword01);
        System.out.println("公钥解密:" + ConfigTools.decrypt(publicKey, myEncryptedPassword01));

    }


}
