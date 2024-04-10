package com.example.quicktap_clientes.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase para encriptar las contraseñas con el algoritmo SHA-256
 */
public class SHA {
    public static String encrypt(String cadena) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] inputBytes = cadena.getBytes();

            byte[] hashBytes = sha256.digest(inputBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}
