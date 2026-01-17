package com.example.gestaodetarefasestudos.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testes unitarios para PasswordHelper
 * Testa a geracao e verificacao de hashes PBKDF2
 */
public class PasswordHelperTest {

    @Test
    public void gerarHash_deveRetornarHashNoFormatoCorreto() {
        String senha = "minhasenha123";

        String hash = PasswordHelper.gerarHash(senha);

        // Hash deve estar no formato "salt:hash"
        assertNotNull(hash);
        assertTrue(hash.contains(":"));

        String[] parts = hash.split(":");
        assertEquals(2, parts.length);

        // Salt e hash devem estar em Base64 (nao vazios)
        assertTrue(parts[0].length() > 0);
        assertTrue(parts[1].length() > 0);
    }

    @Test
    public void gerarHash_deveProduzirHashesDiferentesParaMesmaSenha() {
        String senha = "mesmasenha";

        String hash1 = PasswordHelper.gerarHash(senha);
        String hash2 = PasswordHelper.gerarHash(senha);

        // Hashes devem ser diferentes devido ao salt aleatorio
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void verificarSenha_deveRetornarTrueParaSenhaCorreta() {
        String senha = "senhaCorreta123";
        String hash = PasswordHelper.gerarHash(senha);

        boolean resultado = PasswordHelper.verificarSenha(senha, hash);

        assertTrue(resultado);
    }

    @Test
    public void verificarSenha_deveRetornarFalseParaSenhaIncorreta() {
        String senhaOriginal = "senhaCorreta";
        String senhaErrada = "senhaErrada";
        String hash = PasswordHelper.gerarHash(senhaOriginal);

        boolean resultado = PasswordHelper.verificarSenha(senhaErrada, hash);

        assertFalse(resultado);
    }

    @Test
    public void verificarSenha_deveRetornarFalseParaHashInvalido() {
        String senha = "qualquersenha";

        // Hash sem formato correto
        assertFalse(PasswordHelper.verificarSenha(senha, "hashsemformato"));
        assertFalse(PasswordHelper.verificarSenha(senha, ""));
        assertFalse(PasswordHelper.verificarSenha(senha, "abc"));
    }

    @Test
    public void verificarSenha_deveSerCaseSensitive() {
        String senhaOriginal = "SenhaComMaiusculas";
        String senhaDiferente = "senhacommaiusculas";
        String hash = PasswordHelper.gerarHash(senhaOriginal);

        assertFalse(PasswordHelper.verificarSenha(senhaDiferente, hash));
    }

    @Test
    public void verificarSenha_deveFuncionarComCaracteresEspeciais() {
        String senha = "s3nh@_C0m!C@r@ct3r3s#Esp$ci@is%";
        String hash = PasswordHelper.gerarHash(senha);

        assertTrue(PasswordHelper.verificarSenha(senha, hash));
    }

    @Test
    public void verificarSenha_deveFuncionarComSenhaVazia() {
        String senha = "";
        String hash = PasswordHelper.gerarHash(senha);

        assertTrue(PasswordHelper.verificarSenha(senha, hash));
        assertFalse(PasswordHelper.verificarSenha("qualquercoisa", hash));
    }

    @Test
    public void verificarSenha_deveFuncionarComSenhaLonga() {
        // Senha muito longa
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String senhaLonga = sb.toString();
        String hash = PasswordHelper.gerarHash(senhaLonga);

        assertTrue(PasswordHelper.verificarSenha(senhaLonga, hash));
    }

    @Test
    public void verificarSenha_deveFuncionarComUnicode() {
        String senha = "senha_com_émojis_🔐_e_acentos_àéîõü";
        String hash = PasswordHelper.gerarHash(senha);

        assertTrue(PasswordHelper.verificarSenha(senha, hash));
    }
}
