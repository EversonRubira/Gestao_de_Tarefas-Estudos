package com.example.gestaodetarefasestudos.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Classe utilitária para hash e verificação de senhas
 * Usa PBKDF2 (Password-Based Key Derivation Function 2) com salt aleatório
 *
 * PBKDF2 é um padrão da indústria que:
 * - Aplica uma função hash milhares de vezes (iterações)
 * - Usa um "salt" (valor aleatório) único para cada senha
 * - Torna ataques de força bruta e rainbow tables ineficazes
 */
public class PasswordHelper {

    private static final int ITERATIONS = 10000;  // Número de iterações do PBKDF2
    private static final int KEY_LENGTH = 256;     // Tamanho da chave em bits
    private static final int SALT_LENGTH = 16;     // Tamanho do salt em bytes

    /**
     * Gera um hash PBKDF2 da senha fornecida
     * Formato do retorno: salt:hash (ambos em Base64)
     */
    public static String gerarHash(String senha) {
        try {
            // Gerar salt aleatório
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Gerar hash usando PBKDF2
            byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Codificar salt e hash em Base64 e retornar no formato: salt:hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    /**
     * Verifica se a senha fornecida corresponde ao hash armazenado
     */
    public static boolean verificarSenha(String senha, String hashArmazenado) {
        try {
            // Separar salt e hash do formato "salt:hash"
            String[] parts = hashArmazenado.split(":");
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hashOriginal = Base64.getDecoder().decode(parts[1]);

            // Gerar hash da senha fornecida com o mesmo salt
            byte[] hashTeste = pbkdf2(senha.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Comparar os hashes
            return compareBytes(hashOriginal, hashTeste);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Aplica o algoritmo PBKDF2 com HmacSHA256
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Compara dois arrays de bytes de forma segura (tempo constante)
     * Previne ataques de timing
     */
    private static boolean compareBytes(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
