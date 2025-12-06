# 🏗️ GUIA DE ARQUITETURA - GESTÃO DE TAREFAS E ESTUDOS

> **📌 LEMBRETE**: Este documento contém uma análise completa da arquitetura do projeto e um plano de melhorias para você implementar gradualmente. Use como referência para evoluir o projeto!

---

## 📋 ÍNDICE

1. [Arquitetura Atual](#1-arquitetura-atual)
2. [Análise Detalhada](#2-análise-detalhada)
3. [Problemas Identificados](#3-problemas-identificados)
4. [Plano de Melhorias](#4-plano-de-melhorias)
5. [Exemplos Práticos](#5-exemplos-práticos)
6. [Checklist de Implementação](#6-checklist-de-implementação)
7. [Recursos para Estudar](#7-recursos-para-estudar)
8. [Glossário](#8-glossário)

---

## 1. ARQUITETURA ATUAL

### 1.1. Padrão Identificado

**MVC Simplificado (Model-View-Controller) + DAO Pattern**

```
📦 Projeto
├── 📁 models/           → MODEL (Dados)
├── 📁 database/dao/     → DATA ACCESS (Persistência)
├── 📁 Activities        → VIEW + CONTROLLER (misturados!)
├── 📁 Fragments         → VIEW + CONTROLLER (misturados!)
└── 📁 Adapters          → VIEW (RecyclerView)
```

### 1.2. Diagrama da Arquitetura Atual

```
┌─────────────────────────────────────────┐
│   CAMADA DE APRESENTAÇÃO                │
│   Activities + Fragments + Adapters     │
│   ⚠️  (UI + LÓGICA MISTURADAS)          │
└──────────────┬──────────────────────────┘
               │ Acesso direto
               │ (ALTO ACOPLAMENTO)
               ↓
┌─────────────────────────────────────────┐
│   CAMADA DE DADOS                       │
│   DAOs → DatabaseHelper → SQLite        │
└─────────────────────────────────────────┘
```

### 1.3. Estrutura de Pacotes

```
com.example.gestaodetarefasestudos/
│
├── 📁 models/
│   ├── Tarefa.java
│   ├── Disciplina.java
│   ├── Usuario.java
│   ├── SessaoEstudo.java
│   └── DiaCalendario.java
│
├── 📁 database/
│   ├── DatabaseHelper.java (Singleton ✓)
│   └── dao/
│       ├── TarefaDAO.java
│       ├── DisciplinaDAO.java
│       ├── UsuarioDAO.java
│       └── SessaoEstudoDAO.java
│
├── 📁 fragments/
│   ├── HomeFragment.java
│   ├── TasksFragment.java
│   ├── SubjectsFragment.java
│   ├── TimerFragment.java
│   └── DashboardFragment.java
│
├── 📁 adapters/
│   ├── TarefaAdapter.java
│   ├── DisciplinaAdapter.java
│   └── CalendarioAdapter.java
│
├── 📁 enums/
│   ├── Prioridade.java
│   └── EstadoTarefa.java
│
├── MainActivity.java
├── LoginActivity.java
├── RegisterActivity.java
└── SplashActivity.java
```

---

## 2. ANÁLISE DETALHADA

### 2.1. Separação de Responsabilidades

#### ✅ O QUE ESTÁ BEM

**Models - Muito Bom!**
```java
// Tarefa.java - Responsabilidade única: representar dados
public class Tarefa {
    private long id;
    private String titulo;
    private String descricao;

    // Apenas getters, setters e métodos auxiliares simples
    public boolean estaPendente() {
        return estado == EstadoTarefa.PENDENTE;
    }
}
```

**DAOs - Excelente!**
```java
// TarefaDAO.java - Responsabilidade única: operações de banco
public class TarefaDAO {
    public long adicionar(Tarefa tarefa) { /* SQL INSERT */ }
    public List<Tarefa> obterTodas() { /* SQL SELECT */ }
    public int atualizar(Tarefa tarefa) { /* SQL UPDATE */ }
    public int deletar(long id) { /* SQL DELETE */ }
}
```

#### ❌ O QUE ESTÁ MAL

**Fragments com MÚLTIPLAS responsabilidades:**

```java
// HomeFragment.java faz TUDO:
public class HomeFragment extends Fragment {

    // 1. Gerencia UI
    private TextView tvGreeting;
    private TextView tvTotalSubjects;

    // 2. Acessa dados diretamente (ACOPLAMENTO!)
    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;

    // 3. Faz lógica de negócio (deveria estar em ViewModel!)
    private void configurarSaudacao() {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);

        String periodoSaudacao;
        if (hora >= 5 && hora < 12) {
            periodoSaudacao = "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            periodoSaudacao = "Boa tarde";
        } else {
            periodoSaudacao = "Boa noite";
        }
        // ...
    }

    // 4. Formata dados (deveria estar em Utils!)
    private String formatarTempoEstudo(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    // 5. Carrega estatísticas (deveria estar em ViewModel!)
    private void carregarEstatisticas() {
        int totalDisciplinas = disciplinaDAO.contarTotal();
        tvTotalSubjects.setText(String.valueOf(totalDisciplinas));
        // ...
    }
}
```

**Problemas:**
- ❌ Fragment conhece DAOs (acoplamento alto)
- ❌ Lógica de formatação dentro da UI
- ❌ Lógica de saudação dentro da UI
- ❌ Dificulta testes unitários
- ❌ Quebra Single Responsibility Principle

**Adapters modificando dados:**

```java
// TarefaAdapter.java - NÃO deveria fazer isso!
holder.checkboxTarefa.setOnCheckedChangeListener((buttonView, isChecked) -> {
    // ❌ Adapter salvando diretamente no banco!
    tarefaDAO.atualizarEstado(tarefa.getId(), novoEstado);
    tarefa.setEstado(novoEstado);
    notifyItemChanged(position);
});
```

**Por que é ruim?**
- Adapter deveria apenas EXIBIR dados
- Lógica de persistência deveria estar no ViewModel/Repository
- Dificulta testes
- Quebra separação de camadas

### 2.2. Acoplamento

#### 🔴 ALTO ACOPLAMENTO (Problemas)

**1. Fragments acoplados aos DAOs**

```java
// TODOS os Fragments fazem isso:
public class HomeFragment extends Fragment {

    private DisciplinaDAO disciplinaDAO; // Dependência concreta!
    private TarefaDAO tarefaDAO;         // Dependência concreta!

    private void inicializarDAOs() {
        // Criação direta = acoplamento!
        disciplinaDAO = new DisciplinaDAO(requireContext());
        tarefaDAO = new TarefaDAO(requireContext());
    }
}
```

**Problema:** Se você quiser trocar SQLite por API REST, precisa modificar TODOS os Fragments!

**Solução (que você vai aprender):**
```java
// Com Repository e injeção de dependências:
public class HomeFragment extends Fragment {

    @Inject // Injetado automaticamente
    private TarefaRepository repository;

    // Não precisa criar, é injetado!
}
```

**2. SharedPreferences espalhado por todo código**

Código duplicado encontrado em:
- `TarefaDAO.java` (linha 340)
- `DisciplinaDAO.java` (linha 165)
- `SessaoEstudoDAO.java` (linha 201)
- `HomeFragment.java` (linha 94)
- `LoginActivity.java` (linha 46)

```java
// Repetido em MUITOS lugares:
SharedPreferences prefs = context.getSharedPreferences("GestaoTarefasPrefs", MODE_PRIVATE);
long usuarioId = prefs.getLong("usuario_id", 0);
```

**3. Método duplicado em todos os DAOs**

```java
// TarefaDAO.java
private long obterUsuarioLogadoId() {
    SharedPreferences prefs = context.getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
    return prefs.getLong("usuario_id", 0);
}

// DisciplinaDAO.java
private long obterUsuarioLogadoId() {
    SharedPreferences prefs = context.getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
    return prefs.getLong("usuario_id", 0);
}

// SessaoEstudoDAO.java
private long obterUsuarioLogadoId() {
    SharedPreferences prefs = context.getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
    return prefs.getLong("usuario_id", 0);
}

// ❌ VIOLAÇÃO DO PRINCÍPIO DRY (Don't Repeat Yourself)
```

#### 🟢 BAIXO ACOPLAMENTO (Boas Práticas)

**1. DatabaseHelper como Singleton**
```java
// DatabaseHelper.java - Excelente!
private static DatabaseHelper instancia;

public static synchronized DatabaseHelper obterInstancia(Context context) {
    if (instancia == null) {
        instancia = new DatabaseHelper(context.getApplicationContext());
    }
    return instancia;
}
```

**Por que é bom?**
- ✓ Apenas uma instância do banco
- ✓ Thread-safe (synchronized)
- ✓ Usa ApplicationContext (não vaza memória)

**2. Uso de Enums**
```java
// Prioridade.java - Type-safe!
public enum Prioridade {
    BAIXA(0), MEDIA(1), ALTA(2);

    private int valor;

    Prioridade(int valor) {
        this.valor = valor;
    }
}
```

**Por que é bom?**
- ✓ Type-safe (compilador verifica)
- ✓ Evita valores mágicos (0, 1, 2)
- ✓ Fácil de manter

### 2.3. Testabilidade

#### 🔴 MUITO DIFÍCIL DE TESTAR

**Problema 1: Dependências concretas**

```java
// HomeFragment.java
private void inicializarDAOs() {
    // ❌ Criação com "new" = impossível mockar!
    tarefaDAO = new TarefaDAO(requireContext());
}
```

**Como deveria ser (testável):**
```java
// Com interface e injeção:
public interface ITarefaRepository {
    List<Tarefa> obterTodas();
    long adicionar(Tarefa tarefa);
}

public class HomeFragment extends Fragment {
    private ITarefaRepository repository; // Interface!

    // No teste, você injeta um Mock
    public void setRepository(ITarefaRepository repository) {
        this.repository = repository;
    }
}

// No teste:
@Test
public void deveCarregarTarefas() {
    ITarefaRepository mockRepo = mock(ITarefaRepository.class);
    when(mockRepo.obterTodas()).thenReturn(listaTeste);

    fragment.setRepository(mockRepo);
    // ... teste
}
```

**Problema 2: Lógica dentro de UI**

```java
// HomeFragment.java
private void configurarSaudacao() {
    Calendar agora = Calendar.getInstance(); // ❌ Depende do sistema!
    int hora = agora.get(Calendar.HOUR_OF_DAY);
    // ...
}
```

**Como testar?** Você não consegue! O método depende do relógio do sistema.

**Como deveria ser:**
```java
// Utils/SaudacaoHelper.java
public class SaudacaoHelper {
    public static String getSaudacao(int hora) {
        if (hora >= 5 && hora < 12) return "Bom dia";
        if (hora >= 12 && hora < 18) return "Boa tarde";
        return "Boa noite";
    }
}

// Teste:
@Test
public void deveMostrarBomDia() {
    assertEquals("Bom dia", SaudacaoHelper.getSaudacao(8));
}

@Test
public void deveMostrarBoaTarde() {
    assertEquals("Boa tarde", SaudacaoHelper.getSaudacao(14));
}
```

**Problema 3: DAOs dependem de Context**

```java
public class TarefaDAO {
    private Context context;
    private DatabaseHelper dbHelper;

    public TarefaDAO(Context context) { // ❌ Depende de Android
        this.context = context;
        dbHelper = DatabaseHelper.obterInstancia(context);
    }
}
```

**Estimativa de Cobertura de Testes Possível:**

| Camada | Testabilidade | Motivo |
|--------|--------------|--------|
| Models | 80% ✓ | Apenas POJOs, fácil |
| DAOs | 30% ⚠️ | Precisa banco real ou Room |
| Fragments | 10% ❌ | Muita dependência do Android |
| Lógica de negócio | 5% ❌ | Está misturada com UI |

---

## 3. PROBLEMAS IDENTIFICADOS

### 3.1. Críticos (Resolver urgente)

#### ❌ 1. Ausência de Camada de Negócio

**Problema:**
```java
// Lógica espalhada em TODOS os lugares:

// MainActivity.java - linha 150
private void criarDadosExemplo() {
    // Criação de dados de exemplo misturada na Activity!
}

// HomeFragment.java - linha 148
private void configurarSaudacao() {
    // Lógica de saudação dentro do Fragment!
}

// TarefaAdapter.java - linha 92
holder.checkboxTarefa.setOnCheckedChangeListener(...) {
    // Lógica de atualização dentro do Adapter!
}
```

**Impacto:**
- Dificulta manutenção
- Impossibilita testes
- Código duplicado
- Quebra SOLID

#### ❌ 2. Alto Acoplamento UI-Dados

**Problema:**
```java
// HomeFragment.java
public class HomeFragment extends Fragment {
    private DisciplinaDAO disciplinaDAO; // ❌ Fragment conhece DAO!

    private void carregarEstatisticas() {
        int total = disciplinaDAO.contarTotal(); // ❌ Acesso direto!
    }
}
```

**Por que é ruim?**
- Se trocar SQLite por API REST, precisa modificar TODOS os Fragments
- Não consegue testar sem banco de dados
- Fragments sabem "como" buscar dados (deviam saber só "o que")

#### ❌ 3. Código Duplicado

**Exemplos encontrados:**

1. **obterUsuarioLogadoId()** - Repetido em 4 DAOs
2. **SharedPreferences** - Acesso direto em 6 lugares
3. **formatarTempoEstudo()** - Em HomeFragment e DashboardFragment
4. **Validações** - Repetidas em LoginActivity e RegisterActivity

**Impacto:**
- Se precisar mudar, tem que mudar em vários lugares
- Alto risco de bugs
- Viola DRY (Don't Repeat Yourself)

#### ❌ 4. Falta de Tratamento de Erros

**Problema:**
```java
// LoginActivity.java - linha 95
Usuario usuario = usuarioDAO.autenticar(email, senha);
// E se o banco falhar?
// E se houver uma SQLException?
// Sem try-catch, sem tratamento!

if (usuario != null) {
    // Login sucesso
} else {
    // Login falhou - mas por quê? Não sabemos!
    Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
}
```

**Problemas:**
- App pode crashar sem aviso
- Usuário não sabe o que aconteceu
- Dificulta debug

#### ❌ 5. Adapter Modificando Dados

**Problema:**
```java
// TarefaAdapter.java - linha 92
holder.checkboxTarefa.setOnCheckedChangeListener((buttonView, isChecked) -> {
    // ❌ Adapter NÃO deveria acessar banco diretamente!
    EstadoTarefa novoEstado = isChecked ? EstadoTarefa.CONCLUIDA : EstadoTarefa.PENDENTE;
    tarefaDAO.atualizarEstado(tarefa.getId(), novoEstado);
});
```

**Por que é ruim?**
- Adapter só deveria EXIBIR dados
- Quebra separação de responsabilidades
- Dificulta testes
- Se precisar adicionar validações, onde colocar?

### 3.2. Médios (Resolver em breve)

#### ⚠️ 6. Falta de Repository Pattern

**Problema:**
```java
// Fragments acessam DAOs diretamente
tarefaDAO.obterTodas();
```

**Limitações:**
- Não tem cache
- Não tem validações centralizadas
- Não tem tratamento de erros padronizado
- Difícil adicionar API REST depois

#### ⚠️ 7. SharedPreferences Não Encapsulado

**Problema:**
```java
// Código espalhado:
SharedPreferences prefs = getSharedPreferences("GestaoTarefasPrefs", MODE_PRIVATE);
long userId = prefs.getLong("usuario_id", 0);

// Se quiser mudar a chave "usuario_id", precisa mudar em 6 lugares!
```

#### ⚠️ 8. Sem Gerenciamento de Estado

**Problema:**
```java
// HomeFragment.java
@Override
public void onResume() {
    super.onResume();
    carregarEstatisticas(); // ❌ Recarrega TUDO toda vez!
}
```

**Impacto:**
- Performance ruim
- Sem cache
- Queries desnecessárias ao banco

### 3.3. Menores (Melhorias futuras)

#### 🔹 9. Strings Hardcoded

```java
// LoginActivity.java - linha 78
tilEmail.setError("E-mail é obrigatório"); // ❌ Deveria usar R.string
```

#### 🔹 10. Uso de Métodos Deprecated

```java
// onActivityResult é deprecated
// Deveria usar ActivityResultLauncher
```

---

## 4. PLANO DE MELHORIAS

### 4.1. Roadmap Geral

```
Mês 1: FUNDAMENTOS
├── Semana 1-2: SessionManager + Utils
└── Semana 3-4: Repositories

Mês 2: MVVM
├── Semana 1-2: ViewModels básicos
└── Semana 3-4: LiveData + Observers

Mês 3: CLEAN ARCHITECTURE
├── Semana 1-2: Use Cases
└── Semana 3-4: Dependency Injection

Mês 4+: AVANÇADO
├── Migration para Room
├── Testes unitários
└── Navigation Component
```

### 4.2. Nível 1 - Quick Wins (Semanas 1-2)

#### 📌 Tarefa 1.1: Criar SessionManager

**Objetivo:** Encapsular acesso a SharedPreferences

**Criar arquivo:** `utils/SessionManager.java`

```java
package com.example.gestaodetarefasestudos.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "GestaoTarefasPrefs";
    private static final String KEY_USER_ID = "usuario_id";
    private static final String KEY_USER_NAME = "usuario_nome";
    private static final String KEY_USER_EMAIL = "usuario_email";
    private static final String KEY_LOGGED_IN = "logado";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    // Singleton
    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    /**
     * Salva dados do login
     */
    public void salvarLogin(long usuarioId, String nome, String email) {
        editor.putLong(KEY_USER_ID, usuarioId);
        editor.putString(KEY_USER_NAME, nome);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Retorna ID do usuário logado
     */
    public long getUsuarioId() {
        return prefs.getLong(KEY_USER_ID, 0);
    }

    /**
     * Retorna nome do usuário logado
     */
    public String getNomeUsuario() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Verifica se está logado
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Faz logout (limpa tudo)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
```

**Como usar:**

```java
// Antes (em TODOS os DAOs):
private long obterUsuarioLogadoId() {
    SharedPreferences prefs = context.getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
    return prefs.getLong("usuario_id", 0);
}

// Depois:
private long obterUsuarioLogadoId() {
    return SessionManager.getInstance(context).getUsuarioId();
}

// OU melhor ainda, passar como parâmetro:
public List<Tarefa> obterTodas(long usuarioId) {
    // ...
}
```

**Benefícios:**
- ✓ Código duplicado eliminado
- ✓ Chaves centralizadas (fácil mudar)
- ✓ Type-safe
- ✓ Mais testável

#### 📌 Tarefa 1.2: Criar Classe Utils

**Criar arquivo:** `utils/FormatUtils.java`

```java
package com.example.gestaodetarefasestudos.utils;

import java.util.Locale;

public class FormatUtils {

    /**
     * Formata segundos em "Xh XXm"
     * Ex: 3665 → "1h 01m"
     */
    public static String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    /**
     * Retorna saudação baseada na hora
     */
    public static String getSaudacao(int hora) {
        if (hora >= 5 && hora < 12) {
            return "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            return "Boa tarde";
        } else {
            return "Boa noite";
        }
    }
}
```

**Como usar:**

```java
// Antes (HomeFragment.java):
private String formatarTempoEstudo(long segundos) {
    // ... código duplicado
}

// Depois:
import static com.example.gestaodetarefasestudos.utils.FormatUtils.*;

String tempo = formatarTempoEstudo(segundos);
String saudacao = getSaudacao(hora);
```

**Benefícios:**
- ✓ Código reutilizável
- ✓ Fácil de testar (métodos estáticos puros)
- ✓ Elimina duplicação

#### 📌 Tarefa 1.3: Criar DateUtils

**Criar arquivo:** `utils/DateUtils.java`

```java
package com.example.gestaodetarefasestudos.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /**
     * Retorna timestamp do início do dia
     */
    public static long getInicioDia(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * Retorna timestamp do fim do dia
     */
    public static long getFimDia(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * Formata data como dd/MM/yyyy
     */
    public static String formatarData(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Verifica se é hoje
     */
    public static boolean isHoje(long timestamp) {
        Calendar hoje = Calendar.getInstance();
        Calendar data = Calendar.getInstance();
        data.setTimeInMillis(timestamp);

        return hoje.get(Calendar.YEAR) == data.get(Calendar.YEAR) &&
               hoje.get(Calendar.DAY_OF_YEAR) == data.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Verifica se é amanhã
     */
    public static boolean isAmanha(long timestamp) {
        Calendar hoje = Calendar.getInstance();
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_YEAR, 1);
        Calendar data = Calendar.getInstance();
        data.setTimeInMillis(timestamp);

        return amanha.get(Calendar.YEAR) == data.get(Calendar.YEAR) &&
               amanha.get(Calendar.DAY_OF_YEAR) == data.get(Calendar.DAY_OF_YEAR);
    }
}
```

**Refatoração a fazer:**

```java
// DashboardFragment.java - linha 435-447 (ANTES)
Calendar cal = Calendar.getInstance();
cal.setTimeInMillis(dia.getTimestamp());
cal.set(Calendar.HOUR_OF_DAY, 0);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 0);
cal.set(Calendar.MILLISECOND, 0);
long inicioDia = cal.getTimeInMillis();

// DEPOIS:
long inicioDia = DateUtils.getInicioDia(dia.getTimestamp());
long fimDia = DateUtils.getFimDia(dia.getTimestamp());
```

### 4.3. Nível 2 - Repositories (Semanas 3-4)

#### 📌 Tarefa 2.1: Criar TarefaRepository

**Objetivo:** Centralizar toda lógica de acesso a tarefas

**Criar arquivo:** `repository/TarefaRepository.java`

```java
package com.example.gestaodetarefasestudos.repository;

import android.content.Context;
import android.util.Log;

import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.utils.SessionManager;

import java.util.List;

/**
 * Repository para Tarefas
 *
 * Responsabilidades:
 * - Centralizar acesso aos dados
 * - Adicionar validações
 * - Tratamento de erros
 * - Cache (futuro)
 * - Sincronização com API (futuro)
 */
public class TarefaRepository {

    private static final String TAG = "TarefaRepository";

    private TarefaDAO tarefaDAO;
    private SessionManager sessionManager;
    private Context context;

    // Cache simples (opcional)
    private List<Tarefa> cachedTarefas;
    private long lastUpdate = 0;
    private static final long CACHE_DURATION = 30000; // 30 segundos

    public TarefaRepository(Context context) {
        this.context = context.getApplicationContext();
        this.tarefaDAO = new TarefaDAO(context);
        this.sessionManager = SessionManager.getInstance(context);
    }

    /**
     * Obtém todas as tarefas do usuário logado
     */
    public Result<List<Tarefa>> obterTodas() {
        try {
            // Cache simples
            if (cachedTarefas != null && (System.currentTimeMillis() - lastUpdate) < CACHE_DURATION) {
                Log.d(TAG, "Retornando do cache");
                return Result.success(cachedTarefas);
            }

            List<Tarefa> tarefas = tarefaDAO.obterTodas();

            // Atualiza cache
            cachedTarefas = tarefas;
            lastUpdate = System.currentTimeMillis();

            return Result.success(tarefas);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter tarefas", e);
            return Result.error("Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    /**
     * Adiciona nova tarefa
     */
    public Result<Long> adicionar(Tarefa tarefa) {
        try {
            // Validações
            if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
                return Result.error("Título é obrigatório");
            }

            if (tarefa.getTitulo().length() > 100) {
                return Result.error("Título muito longo (máx 100 caracteres)");
            }

            // Salva no banco
            long id = tarefaDAO.adicionar(tarefa);

            if (id > 0) {
                // Invalida cache
                cachedTarefas = null;

                Log.d(TAG, "Tarefa adicionada: ID = " + id);
                return Result.success(id);
            } else {
                return Result.error("Falha ao salvar tarefa");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao adicionar tarefa", e);
            return Result.error("Erro ao salvar: " + e.getMessage());
        }
    }

    /**
     * Atualiza tarefa existente
     */
    public Result<Integer> atualizar(Tarefa tarefa) {
        try {
            // Validações
            if (tarefa.getId() <= 0) {
                return Result.error("ID inválido");
            }

            if (tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
                return Result.error("Título é obrigatório");
            }

            int linhas = tarefaDAO.atualizar(tarefa);

            if (linhas > 0) {
                // Invalida cache
                cachedTarefas = null;

                Log.d(TAG, "Tarefa atualizada: ID = " + tarefa.getId());
                return Result.success(linhas);
            } else {
                return Result.error("Tarefa não encontrada");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao atualizar tarefa", e);
            return Result.error("Erro ao atualizar: " + e.getMessage());
        }
    }

    /**
     * Deleta tarefa
     */
    public Result<Integer> deletar(long id) {
        try {
            int linhas = tarefaDAO.deletar(id);

            if (linhas > 0) {
                // Invalida cache
                cachedTarefas = null;

                Log.d(TAG, "Tarefa deletada: ID = " + id);
                return Result.success(linhas);
            } else {
                return Result.error("Tarefa não encontrada");
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao deletar tarefa", e);
            return Result.error("Erro ao deletar: " + e.getMessage());
        }
    }

    /**
     * Obtém tarefas pendentes
     */
    public Result<List<Tarefa>> obterPendentes() {
        try {
            List<Tarefa> tarefas = tarefaDAO.obterPendentes();
            return Result.success(tarefas);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter tarefas pendentes", e);
            return Result.error("Erro ao carregar: " + e.getMessage());
        }
    }

    /**
     * Conta tarefas pendentes
     */
    public int contarPendentes() {
        return tarefaDAO.contarPendentes();
    }

    /**
     * Invalida cache (forçar reload)
     */
    public void invalidarCache() {
        cachedTarefas = null;
        lastUpdate = 0;
    }
}
```

#### 📌 Tarefa 2.2: Criar Classe Result

**Criar arquivo:** `utils/Result.java`

```java
package com.example.gestaodetarefasestudos.utils;

/**
 * Wrapper para resultados de operações
 * Permite retornar sucesso ou erro de forma type-safe
 */
public class Result<T> {

    private T data;
    private String error;
    private boolean success;

    private Result(T data, String error, boolean success) {
        this.data = data;
        this.error = error;
        this.success = success;
    }

    /**
     * Cria resultado de sucesso
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(data, null, true);
    }

    /**
     * Cria resultado de erro
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, false);
    }

    /**
     * Verifica se foi sucesso
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Verifica se foi erro
     */
    public boolean isError() {
        return !success;
    }

    /**
     * Retorna dados (pode ser null se erro)
     */
    public T getData() {
        return data;
    }

    /**
     * Retorna mensagem de erro (null se sucesso)
     */
    public String getError() {
        return error;
    }
}
```

**Como usar o Repository:**

```java
// TasksFragment.java (ANTES)
private void carregarTarefas() {
    List<Tarefa> tarefas = tarefaDAO.obterTodas(); // Pode crashar!
    adapter.setTarefas(tarefas);
}

// TasksFragment.java (DEPOIS)
private TarefaRepository repository;

private void carregarTarefas() {
    Result<List<Tarefa>> result = repository.obterTodas();

    if (result.isSuccess()) {
        adapter.setTarefas(result.getData());
    } else {
        Toast.makeText(requireContext(),
            "Erro: " + result.getError(),
            Toast.LENGTH_SHORT).show();
    }
}
```

### 4.4. Nível 3 - MVVM (Mês 2)

#### 📌 Tarefa 3.1: Adicionar ViewModel ao Gradle

**Editar:** `app/build.gradle`

```gradle
dependencies {
    // ... dependências existentes

    // ViewModel e LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.6.2"
    implementation "androidx.lifecycle:lifecycle-livedata:2.6.2"
    implementation "androidx.lifecycle:lifecycle-runtime:2.6.2"

    // Fragment KTX (facilita uso de ViewModels)
    implementation "androidx.fragment:fragment:1.6.1"
}
```

#### 📌 Tarefa 3.2: Criar HomeViewModel

**Criar arquivo:** `viewmodel/HomeViewModel.java`

```java
package com.example.gestaodetarefasestudos.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gestaodetarefasestudos.repository.TarefaRepository;
import com.example.gestaodetarefasestudos.repository.DisciplinaRepository;
import com.example.gestaodetarefasestudos.utils.FormatUtils;
import com.example.gestaodetarefasestudos.utils.Result;

import java.util.Calendar;

/**
 * ViewModel para HomeFragment
 *
 * Responsabilidades:
 * - Gerenciar estado da tela
 * - Buscar dados dos Repositories
 * - Processar lógica de negócio
 * - Sobreviver a rotações de tela
 */
public class HomeViewModel extends AndroidViewModel {

    // Repositories
    private TarefaRepository tarefaRepository;
    private DisciplinaRepository disciplinaRepository;

    // LiveData - Observáveis
    private MutableLiveData<String> saudacao = new MutableLiveData<>();
    private MutableLiveData<Integer> totalDisciplinas = new MutableLiveData<>();
    private MutableLiveData<Integer> tarefasPendentes = new MutableLiveData<>();
    private MutableLiveData<String> tempoEstudoHoje = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        // Inicializa repositories
        tarefaRepository = new TarefaRepository(application);
        disciplinaRepository = new DisciplinaRepository(application);

        // Carrega dados iniciais
        carregarDados();
    }

    /**
     * Carrega todos os dados
     */
    public void carregarDados() {
        configurarSaudacao();
        carregarEstatisticas();
    }

    /**
     * Configura saudação baseada na hora
     */
    private void configurarSaudacao() {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);
        String periodo = FormatUtils.getSaudacao(hora);

        // TODO: Buscar nome do usuário
        String nome = "Usuário";

        saudacao.setValue(periodo + ", " + nome + "!");
    }

    /**
     * Carrega estatísticas
     */
    private void carregarEstatisticas() {
        // Total de disciplinas
        Result<Integer> resultDisciplinas = disciplinaRepository.contarTotal();
        if (resultDisciplinas.isSuccess()) {
            totalDisciplinas.setValue(resultDisciplinas.getData());
        } else {
            errorMessage.setValue(resultDisciplinas.getError());
        }

        // Tarefas pendentes
        int pendentes = tarefaRepository.contarPendentes();
        tarefasPendentes.setValue(pendentes);

        // Tempo de estudo hoje
        // TODO: Implementar no SessaoRepository
        // tempoEstudoHoje.setValue("2h 30m");
    }

    // Getters para LiveData (Fragment observa estes)
    public LiveData<String> getSaudacao() {
        return saudacao;
    }

    public LiveData<Integer> getTotalDisciplinas() {
        return totalDisciplinas;
    }

    public LiveData<Integer> getTarefasPendentes() {
        return tarefasPendentes;
    }

    public LiveData<String> getTempoEstudoHoje() {
        return tempoEstudoHoje;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
```

#### 📌 Tarefa 3.3: Refatorar HomeFragment para usar ViewModel

**Editar:** `HomeFragment.java`

```java
// ANTES (versão antiga - muito código)
public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvTotalSubjects;

    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;

    private void inicializarDAOs() {
        disciplinaDAO = new DisciplinaDAO(requireContext());
        tarefaDAO = new TarefaDAO(requireContext());
    }

    private void configurarSaudacao() {
        // 20 linhas de código...
    }

    private void carregarEstatisticas() {
        // 15 linhas de código...
    }
}

// DEPOIS (com ViewModel - muito mais limpo!)
public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    // Views
    private TextView tvGreeting;
    private TextView tvTotalSubjects;
    private TextView tvPendingTasks;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inicializa Views
        inicializarComponentes(view);

        // Observa mudanças
        observarDados();
    }

    private void observarDados() {
        // Observa saudação
        viewModel.getSaudacao().observe(getViewLifecycleOwner(), saudacao -> {
            tvGreeting.setText(saudacao);
        });

        // Observa total de disciplinas
        viewModel.getTotalDisciplinas().observe(getViewLifecycleOwner(), total -> {
            tvTotalSubjects.setText(String.valueOf(total));
        });

        // Observa tarefas pendentes
        viewModel.getTarefasPendentes().observe(getViewLifecycleOwner(), pendentes -> {
            tvPendingTasks.setText(String.valueOf(pendentes));
        });

        // Observa erros
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarrega dados
        viewModel.carregarDados();
    }
}
```

**Benefícios:**
- ✓ Fragment MUITO mais simples (apenas gerencia UI)
- ✓ Lógica separada (testável)
- ✓ Sobrevive a rotações de tela
- ✓ LiveData gerencia ciclo de vida automaticamente
- ✓ Não tem memory leaks

### 4.5. Nível 4 - Use Cases (Mês 3)

#### 📌 Exemplo: ValidarLoginUseCase

**Criar arquivo:** `domain/usecase/ValidarLoginUseCase.java`

```java
package com.example.gestaodetarefasestudos.domain.usecase;

import android.util.Patterns;

import com.example.gestaodetarefasestudos.models.Usuario;
import com.example.gestaodetarefasestudos.repository.UsuarioRepository;
import com.example.gestaodetarefasestudos.utils.Result;

/**
 * Use Case: Validar e executar login
 *
 * Responsabilidade ÚNICA: Lógica de autenticação
 */
public class ValidarLoginUseCase {

    private UsuarioRepository repository;

    public ValidarLoginUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Executa login com validações
     */
    public Result<Usuario> execute(String email, String senha) {

        // Validação 1: Email vazio
        if (email == null || email.trim().isEmpty()) {
            return Result.error("E-mail é obrigatório");
        }

        // Validação 2: Email inválido
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.error("E-mail inválido");
        }

        // Validação 3: Senha vazia
        if (senha == null || senha.isEmpty()) {
            return Result.error("Senha é obrigatória");
        }

        // Validação 4: Senha muito curta
        if (senha.length() < 6) {
            return Result.error("Senha deve ter no mínimo 6 caracteres");
        }

        // Executa autenticação
        return repository.autenticar(email, senha);
    }
}
```

**Como usar:**

```java
// LoginActivity.java
public class LoginActivity extends AppCompatActivity {

    private ValidarLoginUseCase validarLoginUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UsuarioRepository repository = new UsuarioRepository(this);
        validarLoginUseCase = new ValidarLoginUseCase(repository);
    }

    private void tentarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        // Executa use case
        Result<Usuario> result = validarLoginUseCase.execute(email, senha);

        if (result.isSuccess()) {
            Usuario usuario = result.getData();

            // Salva sessão
            SessionManager.getInstance(this).salvarLogin(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
            );

            // Vai para MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else {
            // Mostra erro específico
            Toast.makeText(this, result.getError(), Toast.LENGTH_SHORT).show();
            tilSenha.setError(result.getError());
        }
    }
}
```

---

## 5. EXEMPLOS PRÁTICOS

### 5.1. Refatoração Completa: HomeFragment

#### ANTES (Arquitetura Atual)

```java
public class HomeFragment extends Fragment {

    // ❌ Muitas responsabilidades
    private TextView tvGreeting;
    private TextView tvTotalSubjects;
    private TextView tvPendingTasks;
    private TextView tvStudyTime;

    // ❌ Acoplamento direto com DAOs
    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;
    private SessaoEstudoDAO sessaoDAO;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ❌ Cria DAOs diretamente
        inicializarDAOs();
        inicializarComponentes(view);

        // ❌ Lógica de negócio no Fragment
        configurarSaudacao();
        carregarEstatisticas();
    }

    // ❌ Criação manual de dependências
    private void inicializarDAOs() {
        disciplinaDAO = new DisciplinaDAO(requireContext());
        tarefaDAO = new TarefaDAO(requireContext());
        sessaoDAO = new SessaoEstudoDAO(requireContext());
    }

    // ❌ Lógica de apresentação misturada
    private void configurarSaudacao() {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);

        String periodoSaudacao;
        if (hora >= 5 && hora < 12) {
            periodoSaudacao = "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            periodoSaudacao = "Boa tarde";
        } else {
            periodoSaudacao = "Boa noite";
        }

        // Busca nome do usuário
        SharedPreferences prefs = requireActivity()
            .getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
        long usuarioId = prefs.getLong("usuario_id", -1);

        UsuarioDAO usuarioDAO = new UsuarioDAO(requireContext());
        Usuario usuario = usuarioDAO.obterPorId(usuarioId);

        String nomeUsuario = "";
        if (usuario != null) {
            nomeUsuario = usuario.getNome();
        }

        String saudacao;
        if (!nomeUsuario.isEmpty()) {
            saudacao = periodoSaudacao + ", " + nomeUsuario + "!";
        } else {
            saudacao = periodoSaudacao + "!";
        }

        tvGreeting.setText(saudacao);
    }

    // ❌ Acesso direto aos DAOs
    private void carregarEstatisticas() {
        int totalDisciplinas = disciplinaDAO.contarTotal();
        tvTotalSubjects.setText(String.valueOf(totalDisciplinas));

        int tarefasPendentes = tarefaDAO.contarPendentes();
        tvPendingTasks.setText(String.valueOf(tarefasPendentes));

        long tempoTotalSegundos = sessaoDAO.obterTempoEstudoHoje();
        String tempoFormatado = formatarTempoEstudo(tempoTotalSegundos);
        tvStudyTime.setText(tempoFormatado);
    }

    // ❌ Lógica de formatação dentro do Fragment
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    // ❌ Recarrega tudo a cada onResume
    @Override
    public void onResume() {
        super.onResume();
        carregarEstatisticas();
    }
}
```

**Problemas:**
- 100+ linhas de código
- Lógica misturada com UI
- Acoplamento direto com DAOs
- Código duplicado (formatação)
- Difícil de testar
- Recarrega tudo toda vez

#### DEPOIS (Arquitetura Ideal - MVVM)

```java
public class HomeFragment extends Fragment {

    // ✓ Apenas gerencia UI
    private HomeViewModel viewModel;

    // Views
    private TextView tvGreeting;
    private TextView tvTotalSubjects;
    private TextView tvPendingTasks;
    private TextView tvStudyTime;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✓ ViewModel gerencia lógica
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // ✓ Inicializa apenas Views
        inicializarComponentes(view);

        // ✓ Observa mudanças
        observarDados();
    }

    private void inicializarComponentes(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvTotalSubjects = view.findViewById(R.id.tv_total_subjects);
        tvPendingTasks = view.findViewById(R.id.tv_pending_tasks);
        tvStudyTime = view.findViewById(R.id.tv_study_time);
    }

    // ✓ Apenas observa, não processa
    private void observarDados() {
        viewModel.getSaudacao().observe(getViewLifecycleOwner(), saudacao -> {
            tvGreeting.setText(saudacao);
        });

        viewModel.getTotalDisciplinas().observe(getViewLifecycleOwner(), total -> {
            tvTotalSubjects.setText(String.valueOf(total));
        });

        viewModel.getTarefasPendentes().observe(getViewLifecycleOwner(), pendentes -> {
            tvPendingTasks.setText(String.valueOf(pendentes));
        });

        viewModel.getTempoEstudoHoje().observe(getViewLifecycleOwner(), tempo -> {
            tvStudyTime.setText(tempo);
        });
    }

    // ✓ Não precisa recarregar, LiveData gerencia
    @Override
    public void onResume() {
        super.onResume();
        viewModel.atualizarDados(); // ViewModel decide se precisa recarregar
    }
}
```

**Melhorias:**
- ✓ 50 linhas de código (metade do anterior!)
- ✓ Lógica separada (no ViewModel)
- ✓ Sem acoplamento direto
- ✓ Sem código duplicado
- ✓ Totalmente testável
- ✓ Cache inteligente (no ViewModel)

### 5.2. Comparação: Adapter

#### ANTES

```java
// TarefaAdapter.java
public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.ViewHolder> {

    private TarefaDAO tarefaDAO; // ❌ Adapter conhece DAO!

    public TarefaAdapter(Context context, List<Tarefa> tarefas) {
        this.context = context;
        this.tarefas = tarefas;
        this.tarefaDAO = new TarefaDAO(context); // ❌ Cria DAO diretamente
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarefa tarefa = tarefas.get(position);

        // ❌ Adapter modifica banco de dados!
        holder.checkboxTarefa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EstadoTarefa novoEstado = isChecked ?
                EstadoTarefa.CONCLUIDA : EstadoTarefa.PENDENTE;

            tarefaDAO.atualizarEstado(tarefa.getId(), novoEstado); // ❌❌❌
            tarefa.setEstado(novoEstado);
            notifyItemChanged(position);
        });
    }
}
```

#### DEPOIS

```java
// TarefaAdapter.java
public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.ViewHolder> {

    // ✓ Interface de callback
    public interface OnTarefaInteractionListener {
        void onTarefaCheckChanged(Tarefa tarefa, boolean concluida);
        void onTarefaClicked(Tarefa tarefa);
    }

    private OnTarefaInteractionListener listener;

    public TarefaAdapter(List<Tarefa> tarefas, OnTarefaInteractionListener listener) {
        this.tarefas = tarefas;
        this.listener = listener; // ✓ Recebe listener
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarefa tarefa = tarefas.get(position);

        // ✓ Apenas notifica, não modifica dados
        holder.checkboxTarefa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTarefaCheckChanged(tarefa, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTarefaClicked(tarefa);
            }
        });
    }
}

// TasksFragment.java
public class TasksFragment extends Fragment implements TarefaAdapter.OnTarefaInteractionListener {

    private TasksViewModel viewModel;

    @Override
    public void onTarefaCheckChanged(Tarefa tarefa, boolean concluida) {
        // ✓ Fragment delega para ViewModel
        viewModel.atualizarEstadoTarefa(tarefa, concluida);
    }

    @Override
    public void onTarefaClicked(Tarefa tarefa) {
        // ✓ Navega para edição
        Intent intent = new Intent(requireContext(), AdicionarEditarTarefaActivity.class);
        intent.putExtra("tarefa_id", tarefa.getId());
        startActivity(intent);
    }
}

// TasksViewModel.java
public class TasksViewModel extends ViewModel {

    private TarefaRepository repository;

    public void atualizarEstadoTarefa(Tarefa tarefa, boolean concluida) {
        // ✓ Lógica de negócio no lugar certo
        EstadoTarefa novoEstado = concluida ?
            EstadoTarefa.CONCLUIDA : EstadoTarefa.PENDENTE;

        tarefa.setEstado(novoEstado);

        Result<Integer> result = repository.atualizar(tarefa);

        if (result.isSuccess()) {
            // Atualiza LiveData
            carregarTarefas();
        } else {
            errorMessage.setValue(result.getError());
        }
    }
}
```

---

## 6. CHECKLIST DE IMPLEMENTAÇÃO

### ☐ Fase 1: Fundamentos (Semanas 1-2)

#### Semana 1
- [ ] Criar `utils/SessionManager.java`
- [ ] Refatorar todos os DAOs para usar SessionManager
- [ ] Criar `utils/FormatUtils.java`
- [ ] Mover `formatarTempoEstudo()` para FormatUtils
- [ ] Mover `getSaudacao()` para FormatUtils
- [ ] Criar `utils/DateUtils.java`
- [ ] Refatorar DashboardFragment para usar DateUtils

#### Semana 2
- [ ] Criar `utils/Result.java`
- [ ] Criar `repository/TarefaRepository.java`
- [ ] Adicionar validações no Repository
- [ ] Adicionar tratamento de erros
- [ ] Implementar cache simples
- [ ] Testar Repository manualmente

### ☐ Fase 2: Repositories (Semanas 3-4)

#### Semana 3
- [ ] Criar `repository/DisciplinaRepository.java`
- [ ] Criar `repository/SessaoEstudoRepository.java`
- [ ] Criar `repository/UsuarioRepository.java`
- [ ] Adicionar validações em todos

#### Semana 4
- [ ] Refatorar HomeFragment para usar Repositories
- [ ] Refatorar TasksFragment para usar Repositories
- [ ] Refatorar SubjectsFragment para usar Repositories
- [ ] Testar todas as telas

### ☐ Fase 3: MVVM (Semanas 5-8)

#### Semana 5
- [ ] Adicionar dependências do ViewModel ao Gradle
- [ ] Criar `viewmodel/HomeViewModel.java`
- [ ] Implementar LiveData para todas as propriedades
- [ ] Refatorar HomeFragment para observar LiveData

#### Semana 6
- [ ] Criar `viewmodel/TasksViewModel.java`
- [ ] Implementar LiveData<List<Tarefa>>
- [ ] Refatorar TasksFragment
- [ ] Refatorar TarefaAdapter (remover lógica de persistência)

#### Semana 7
- [ ] Criar `viewmodel/SubjectsViewModel.java`
- [ ] Refatorar SubjectsFragment
- [ ] Refatorar DisciplinaAdapter

#### Semana 8
- [ ] Criar `viewmodel/DashboardViewModel.java`
- [ ] Refatorar DashboardFragment
- [ ] Testar todas as ViewModels

### ☐ Fase 4: Use Cases (Opcional - Semanas 9-12)

- [ ] Criar `domain/usecase/ValidarLoginUseCase.java`
- [ ] Criar `domain/usecase/CriarTarefaUseCase.java`
- [ ] Criar `domain/usecase/AtualizarTarefaUseCase.java`
- [ ] Integrar Use Cases nos ViewModels

### ☐ Fase 5: Dependency Injection (Opcional - Semanas 13-16)

- [ ] Adicionar Hilt ao projeto
- [ ] Criar Modules para Repositories
- [ ] Anotar ViewModels com @HiltViewModel
- [ ] Anotar Fragments com @AndroidEntryPoint

### ☐ Fase 6: Room Migration (Opcional - Semanas 17-20)

- [ ] Adicionar dependência do Room
- [ ] Criar Entities
- [ ] Criar DAOs do Room (interfaces)
- [ ] Criar Database
- [ ] Migrar dados do SQLite para Room

---

## 7. RECURSOS PARA ESTUDAR

### 7.1. Documentação Oficial

#### Google Android Developers
1. **Architecture Components**
   - https://developer.android.com/topic/architecture
   - Conceitos fundamentais de arquitetura

2. **ViewModel**
   - https://developer.android.com/topic/libraries/architecture/viewmodel
   - Guia oficial do ViewModel

3. **LiveData**
   - https://developer.android.com/topic/libraries/architecture/livedata
   - Observáveis lifecycle-aware

4. **Room**
   - https://developer.android.com/training/data-storage/room
   - ORM oficial do Android

5. **Hilt (Dependency Injection)**
   - https://developer.android.com/training/dependency-injection/hilt-android
   - DI recomendado pelo Google

### 7.2. Artigos e Tutoriais

#### Medium
1. **MVVM Architecture Pattern**
   - Buscar: "Android MVVM Architecture Pattern Tutorial"
   - Explicações práticas com exemplos

2. **Repository Pattern**
   - Buscar: "Android Repository Pattern Best Practices"
   - Como implementar corretamente

3. **Clean Architecture**
   - Buscar: "Clean Architecture in Android"
   - Por que e como aplicar

#### Outros Recursos
- **Vogella Tutorials**: https://www.vogella.com/tutorials/android.html
- **Ray Wenderlich**: https://www.raywenderlich.com/android
- **Android Weekly**: https://androidweekly.net/

### 7.3. Livros Recomendados

1. **"Clean Code" - Robert C. Martin (Uncle Bob)**
   - Princípios fundamentais
   - SOLID principles
   - Refactoring

2. **"Clean Architecture" - Robert C. Martin**
   - Arquitetura em camadas
   - Dependency Inversion
   - Use Cases

3. **"Android Programming: The Big Nerd Ranch Guide"**
   - Práticas recomendadas
   - Arquitetura Android

4. **"Head First Design Patterns"**
   - Padrões de projeto
   - Repository, Observer, Singleton, etc.

### 7.4. Cursos Online

#### Udemy
- "Android Architecture Components & MVVM"
- "Clean Architecture for Android"
- "Android Testing with JUnit & Mockito"

#### Coursera
- "Advanced Android App Development"
- Google's Android Development Course

#### YouTube (Canais)
- **Philipp Lackner** - MVVM, Clean Architecture
- **Coding in Flow** - Android Architecture
- **Stevdza-San** - Tutoriais práticos

### 7.5. GitHub - Projetos Exemplo

Buscar no GitHub:
```
- "android mvvm clean architecture example"
- "android repository pattern example"
- "android hilt example"
```

Repositórios recomendados:
- **android-architecture** (oficial Google)
- **android-clean-architecture-mvi-boilerplate**
- **Android-CleanArchitecture**

---

## 8. GLOSSÁRIO

### A

**Acoplamento (Coupling)**
- Grau de dependência entre componentes
- Alto acoplamento = difícil de manter
- Baixo acoplamento = desejável

**Adapter**
- Padrão de projeto
- No Android: gerencia views de RecyclerView
- Deve apenas EXIBIR dados, não modificar

**Architecture Pattern**
- Padrão de organização de código
- Ex: MVC, MVP, MVVM, Clean Architecture

### C

**Cache**
- Armazenamento temporário em memória
- Evita buscas repetidas ao banco
- Melhora performance

**Clean Architecture**
- Arquitetura em camadas
- Separação clara de responsabilidades
- Independência de frameworks

**Coesão (Cohesion)**
- Grau de relacionamento dentro de um módulo
- Alta coesão = desejável
- Componentes relacionados juntos

### D

**DAO (Data Access Object)**
- Padrão de projeto
- Encapsula acesso a dados
- SQL fica isolado aqui

**Dependency Injection (DI)**
- Padrão de projeto
- Injeta dependências em vez de criar
- Facilita testes e desacoplamento
- Ferramentas: Hilt, Koin, Dagger

**DRY (Don't Repeat Yourself)**
- Princípio de não duplicar código
- Cada conhecimento tem uma representação única

### L

**LiveData**
- Observável lifecycle-aware do Android
- Atualiza UI automaticamente
- Gerencia ciclo de vida

### M

**Model**
- Camada de dados
- POJOs, Entities
- Representam objetos do domínio

**MVC (Model-View-Controller)**
- Padrão arquitetural
- Model: dados
- View: UI
- Controller: lógica

**MVP (Model-View-Presenter)**
- Evolução do MVC
- Presenter substitui Controller
- Melhor testabilidade

**MVVM (Model-View-ViewModel)**
- Padrão recomendado pelo Google
- ViewModel separa lógica da UI
- Usa LiveData/StateFlow

### R

**Repository Pattern**
- Padrão de projeto
- Centraliza acesso a dados
- Abstrai fonte de dados (SQLite, API, etc.)

**Result**
- Wrapper para resultados
- Pode ser Success ou Error
- Type-safe error handling

### S

**Separation of Concerns**
- Princípio de separar responsabilidades
- Cada módulo faz UMA coisa
- Facilita manutenção

**Singleton**
- Padrão de projeto
- Apenas uma instância
- Ex: DatabaseHelper, SessionManager

**SOLID Principles**
- **S**ingle Responsibility Principle
- **O**pen/Closed Principle
- **L**iskov Substitution Principle
- **I**nterface Segregation Principle
- **D**ependency Inversion Principle

### T

**Testability**
- Capacidade de ser testado
- Código desacoplado = mais testável
- Usar interfaces e injeção

### U

**Use Case**
- Representa uma ação do usuário
- Contém lógica de negócio
- Reutilizável
- Ex: "Fazer Login", "Criar Tarefa"

### V

**View**
- Camada de apresentação
- UI do app
- Activities, Fragments, XML layouts

**ViewModel**
- Componente do MVVM
- Gerencia estado da tela
- Sobrevive a rotações
- Processa lógica de negócio

---

## 9. DIAGRAMA DA ARQUITETURA IDEAL

```
┌───────────────────────────────────────────────────────────────┐
│                    CAMADA DE APRESENTAÇÃO                     │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │  Activities  │  │  Fragments   │  │   Adapters   │       │
│  │              │  │              │  │              │       │
│  │  - Login     │  │  - Home      │  │  - Tarefa    │       │
│  │  - Main      │  │  - Tasks     │  │  - Disciplina│       │
│  │  - Register  │  │  - Subjects  │  │  - Calendario│       │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘       │
│         │                  │                                  │
│         └────────┬─────────┘                                  │
│                  ↓                                            │
│         ┌─────────────────┐                                   │
│         │   ViewModels    │                                   │
│         │                 │                                   │
│         │  - HomeVM       │                                   │
│         │  - TasksVM      │                                   │
│         │  - SubjectsVM   │                                   │
│         └────────┬────────┘                                   │
└──────────────────┼───────────────────────────────────────────┘
                   │
                   │ LiveData / StateFlow
                   │
┌──────────────────┼───────────────────────────────────────────┐
│                  ↓     CAMADA DE DOMÍNIO (BUSINESS LOGIC)    │
│                                                               │
│  ┌──────────────────────┐  ┌──────────────────────┐         │
│  │     Use Cases        │  │     Validators       │         │
│  │                      │  │                      │         │
│  │  - ValidarLogin      │  │  - EmailValidator    │         │
│  │  - CriarTarefa       │  │  - SenhaValidator    │         │
│  │  - AtualizarTarefa   │  │  - TarefaValidator   │         │
│  └──────────┬───────────┘  └──────────────────────┘         │
│             │                                                 │
│             ↓                                                 │
│  ┌────────────────────────────────────────┐                  │
│  │          Repositories                  │                  │
│  │                                        │                  │
│  │  - TarefaRepository                   │                  │
│  │  - DisciplinaRepository               │                  │
│  │  - UsuarioRepository                  │                  │
│  │  - SessaoEstudoRepository             │                  │
│  │                                        │                  │
│  │  [Cache, Validações, Error Handling]  │                  │
│  └────────────────┬───────────────────────┘                  │
└───────────────────┼──────────────────────────────────────────┘
                    │
                    │
┌───────────────────┼──────────────────────────────────────────┐
│                   ↓        CAMADA DE DADOS                    │
│                                                               │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │    DAOs     │  │   Session    │  │    Utils     │        │
│  │             │  │   Manager    │  │              │        │
│  │ - TarefaDAO │  │              │  │ - FormatUtils│        │
│  │ - DisciplinaDAO  - getUserId()  │ - DateUtils  │        │
│  │ - UsuarioDAO│  │  - isLogged()│  │              │        │
│  │ - SessaoDAO │  │  - logout()  │  │              │        │
│  └──────┬──────┘  └──────┬───────┘  └──────────────┘        │
│         │                │                                    │
│         ↓                ↓                                    │
│  ┌─────────────┐  ┌──────────────┐                          │
│  │ SQLite DB   │  │ SharedPrefs  │                          │
│  │ (Tabelas)   │  │ (Session)    │                          │
│  └─────────────┘  └──────────────┘                          │
└───────────────────────────────────────────────────────────────┘

FLUXO DE DADOS:
===============
1. User clica no botão (View)
2. Fragment chama método do ViewModel
3. ViewModel chama Use Case (validações)
4. Use Case chama Repository
5. Repository consulta DAO
6. DAO acessa SQLite
7. Resultado volta pelo mesmo caminho
8. LiveData notifica UI automaticamente
```

---

## 10. PRÓXIMOS PASSOS

### Esta Semana
1. **Leia todo este documento** 📖
2. **Crie o SessionManager** (Tarefa 1.1)
3. **Teste o SessionManager** em um DAO

### Próxima Semana
1. **Crie FormatUtils e DateUtils** (Tarefas 1.2 e 1.3)
2. **Refatore HomeFragment** para usar utils
3. **Crie a classe Result** (Tarefa 2.2)

### Próximo Mês
1. **Implemente TarefaRepository** (Tarefa 2.1)
2. **Estude sobre ViewModels** (documentação oficial)
3. **Prepare ambiente para MVVM** (dependências Gradle)

### Próximos 3 Meses
1. **Migre para MVVM** completamente
2. **Adicione Use Cases** para lógica complexa
3. **Escreva testes unitários** básicos

---

## 📌 LEMBRETE FINAL

**Não tente fazer tudo de uma vez!**

Arquitetura é uma jornada, não um destino. O projeto atual já mostra bom entendimento dos fundamentos. Agora é evoluir gradualmente:

1. ✅ **Semana 1-2**: Utils e SessionManager (fácil)
2. ✅ **Semana 3-4**: Repositories (médio)
3. ✅ **Mês 2**: MVVM com ViewModels (desafiador)
4. ✅ **Mês 3+**: Use Cases e DI (avançado)

**Cada pequena melhoria conta!**

Bons estudos! 🚀

---

**Criado em:** 2025-01-30
**Versão:** 1.0
**Autor:** Análise de Arquitetura - Claude AI
