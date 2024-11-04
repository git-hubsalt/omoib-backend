package com.githubsalt.omoib.global.util;

import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Value("${aes.encryption.secret-key}")
    private String SECRET_KEY;
    private static final int IV_SIZE = 16;

    public String encrypt(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 무작위 IV 생성
            byte[] iv = new byte[IV_SIZE];
            new Random().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes());

            // IV를 함께 저장
            byte[] encryptedDataWithIv = new byte[IV_SIZE + encryptedData.length];
            System.arraycopy(iv, 0, encryptedDataWithIv, 0, IV_SIZE);
            System.arraycopy(encryptedData, 0, encryptedDataWithIv, IV_SIZE, encryptedData.length);

            return Base64.getEncoder().encodeToString(encryptedDataWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);

            // IV 추출
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(decodedData, 0, iv, 0, IV_SIZE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 암호화된 데이터 추출
            byte[] encryptedDataBytes = new byte[decodedData.length - IV_SIZE];
            System.arraycopy(decodedData, IV_SIZE, encryptedDataBytes, 0, encryptedDataBytes.length);

            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedData = cipher.doFinal(encryptedDataBytes);

            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
