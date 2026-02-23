# Análise Técnica — StudyFlow: Gestão de Tarefas e Estudos

**Data da análise:** 2026-02-23
**Versão analisada:** v1.0 (commit `d7c0d2a`)
**Plataforma:** Android (Java, min SDK 24)

---

## Resumo Executivo

O projeto **StudyFlow** é uma aplicação Android de gestão de tarefas académicas com timer Pomodoro integrado, desenvolvida no âmbito da disciplina de Programação de Aplicações Móveis (IPS Setúbal, 2025/26). A aplicação demonstra sólido domínio das práticas modernas de desenvolvimento Android — MVVM, Room, LiveData, EncryptedSharedPreferences — e apresenta qualidade de código acima do esperado para um projeto académico.

---

## Pontos Fortes

### 1. Arquitetura MVVM bem aplicada
A separação em camadas (View → ViewModel → Repository → DAO → Database) é consistente em todos os módulos. Os ViewModels gerem corretamente o estado com `LiveData` e `MutableLiveData`, e os Repositories abstraem o acesso à base de dados de forma limpa. Não há lógica de negócio nas Activities/Fragments.

### 2. Segurança acima da média
- **PasswordHelper** implementa PBKDF2 com HmacSHA256, 10.000 iterações e salt aleatório de 16 bytes por palavra-passe — equivalente a boas práticas de produção.
- A comparação de hashes usa **tempo constante** (`result |= a[i] ^ b[i]`), prevenindo ataques de timing.
- As preferências são armazenadas com **EncryptedSharedPreferences** (AES256-GCM / AES256-SIV).
- Há fallback defensivo para dispositivos que não suportam as APIs de criptografia, com log explícito do downgrade.

### 3. Gestão da base de dados Room
- O `AppDatabase` usa o padrão **Singleton thread-safe** com `volatile` + `synchronized` (double-checked locking).
- `exportSchema = true` garante rastreabilidade do esquema em controlo de versões.
- O comentário de aviso sobre `fallbackToDestructiveMigration()` demonstra consciência das implicações de produção.
- O template `MIGRATION_3_4` facilita futuras evoluções do esquema.

### 4. Timer Pomodoro robusto
O `TimerViewModel` gere correctamente o ciclo completo (trabalho → descanso curto → descanso longo após 4 ciclos). Pontos notáveis:
- O timer **sobrevive a rotações de ecrã** por estar no ViewModel.
- Sessões parciais (stop antes do fim) são guardadas se durarem ≥ 10 segundos, evitando registos espúrios.
- O `onCleared()` cancela o `CountDownTimer`, prevenindo memory leaks.

### 5. Internacionalização (PT/EN)
Os recursos de strings estão separados em `values/` (inglês) e `values-pt/` (português), seguindo a convenção Android correta.

### 6. Documentação abrangente
5 documentos totalizando ~2.300 linhas: README, manual do utilizador (PT), manual do desenvolvedor, e um roadmap de melhorias futuras — incomum e valioso num projeto académico.

---

## Pontos a Melhorar / Riscos

### 1. Falta de cobertura de testes
O projeto tem a infraestrutura de testes configurada (JUnit, Espresso, Mockito, `core-testing`) mas apenas `PasswordHelper` tem testes. Funcionalidades críticas como DAOs, Repositories, e o fluxo de autenticação não têm cobertura automatizada.

**Risco:** Regressões silenciosas em mudanças futuras.

### 2. `CountDownTimer` com tick a 50ms desnecessário
```java
cronometro = new CountDownTimer(tempo, 50) { ... }
```
O intervalo de 50ms (20 ticks/segundo) é excessivo para um display de `MM:SS`. Bastaria 500ms ou 1000ms, reduzindo o overhead na main thread.

### 3. Mensagens de evento como strings mágicas
```java
mensagemEvento.setValue("ERRO_SEM_DISCIPLINA");
mensagemEvento.postValue("TRABALHO_COMPLETO");
```
Usar strings literais como tokens de evento é frágil (erros de digitação silenciosos, difícil de refatorar). Um `enum TimerEvent` seria mais seguro e explícito.

### 4. Fallback de preferências expõe dados sem criptografia
```java
return context.getSharedPreferences(NOME_ARQUIVO + "_fallback", Context.MODE_PRIVATE);
```
O fallback para `SharedPreferences` normais armazena `usuario_id` e `usuario_email` em texto simples. Numa aplicação de produção, o comportamento correto seria lançar uma exceção ou recusar o login neste dispositivo.

### 5. `usuarioId` não inicializado no ViewModel pode causar bugs silenciosos
Em `TimerViewModel`, se `carregarDisciplinas()` for chamado antes de `setUsuarioId()`, a query é executada com `usuarioId = 0`, retornando dados de outro utilizador ou resultados vazios sem aviso.

### 6. `package com.example.*` em produção
O package `com.example.gestaodetarefasestudos` é adequado para desenvolvimento, mas num contexto de publicação na Play Store exigiria um identificador único (ex: `pt.ips.setubal.studyflow`).

### 7. Dependência em alpha (`security-crypto:1.1.0-alpha06`)
```gradle
androidx.security:security-crypto:1.1.0-alpha06
```
Versões `alpha` não têm garantias de API estável. A versão estável `1.0.0` cobre os mesmos casos de uso para a maioria dos dispositivos.

---

## Métricas do Projeto

| Métrica | Valor |
|---------|-------|
| Linhas de código Java | ~7.447 |
| Activities | 8 |
| Fragments | 5 |
| ViewModels | 4 |
| Repositories | 4 |
| DAOs | 4 |
| Entidades Room | 4 |
| Tabelas na base de dados | 4 |
| Commits | 7 |
| Linhas de documentação | ~2.291 |

---

## Avaliação por Categoria

| Categoria | Nota | Justificação |
|-----------|------|-------------|
| Arquitetura | 9/10 | MVVM consistente, separação de responsabilidades clara |
| Segurança | 8/10 | PBKDF2 correto, AES256; penalizado pelo fallback não criptografado |
| Qualidade de código | 8/10 | Código legível, comentado; penalizado pelas strings mágicas e tick 50ms |
| Testes | 4/10 | Infraestrutura presente mas cobertura mínima |
| Documentação | 10/10 | Excecionalmente completa para projeto académico |
| UX/Design | 8/10 | Material Design 3, dark mode, i18n; limitação de 3 cores no calendário |

**Nota global estimada: 8/10**

---

## Conclusão

O projeto está bem acima do padrão académico habitual. A aplicação implementa corretamente os pilares do desenvolvimento Android moderno — MVVM, Room com migrações, LiveData reativo, segurança com EncryptedSharedPreferences e PBKDF2 — demonstrando compreensão genuína das tecnologias utilizadas, não apenas cópia de tutoriais.

Os pontos de melhoria identificados (cobertura de testes, uso de enums em vez de strings mágicas, intervalo do timer) são refinamentos de qualidade de produção, não falhas de design. A documentação é o ponto mais destacado: raramente encontrada a este nível em projetos de âmbito académico.
