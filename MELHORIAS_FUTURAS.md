# StudyFlow - Documentacao Completa do Projeto

App Android para gestao de estudos e organizacao de tarefas academicas.

---

## Indice

1. [Visao Geral](#visao-geral)
2. [Arquitetura Atual](#arquitetura-atual)
3. [Funcionalidades Implementadas](#funcionalidades-implementadas)
4. [Banco de Dados](#banco-de-dados)
5. [Melhorias Pendentes](#melhorias-pendentes)
6. [Roadmap de Desenvolvimento](#roadmap-de-desenvolvimento)
7. [Debitos Tecnicos](#debitos-tecnicos)
8. [Guia para Retomar Desenvolvimento](#guia-para-retomar-desenvolvimento)

---

## Visao Geral

### Missao
Ajudar estudantes a organizar tarefas academicas e otimizar tempo de estudo de forma simples e eficiente.

### Publico-alvo
- Estudantes universitarios
- Estudantes do ensino medio
- Qualquer pessoa que precise organizar estudos e entregas

### Diferencial
App que combina gestao de tarefas + timer Pomodoro + estatisticas + notificacoes em uma interface limpa, sem complexidade desnecessaria.

### Stack Tecnologica

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Java 11 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Banco de Dados | Room 2.6.1 |
| UI | Material Components |
| Arquitetura | MVVM (ViewModel + LiveData + Repository) |

---

## Arquitetura Atual

### Estrutura de Pastas

```
com.example.gestaodetarefasestudos/
├── activities/
│   ├── SplashActivity.java
│   ├── LoginActivity.java
│   ├── RegistroActivity.java
│   ├── MainActivity.java
│   ├── ConfiguracoesActivity.java
│   ├── AdicionarEditarTarefaActivity.java
│   ├── AdicionarEditarDisciplinaActivity.java
│   └── DetalhesDisciplinaActivity.java
├── fragments/
│   ├── HomeFragment.java
│   ├── SubjectsFragment.java
│   ├── TasksFragment.java
│   ├── TimerFragment.java
│   └── StatisticsFragment.java
├── adapters/
│   ├── TarefaAdapter.java
│   ├── DisciplinaAdapter.java
│   ├── CalendarioAdapter.java
│   └── EstatisticaAdapter.java
├── models/
│   ├── Usuario.java
│   ├── Disciplina.java
│   ├── Tarefa.java
│   ├── SessaoEstudo.java
│   └── DiaCalendario.java
├── database/
│   ├── AppDatabase.java
│   ├── Converters.java
│   └── dao/
│       ├── UsuarioRoomDAO.java
│       ├── DisciplinaRoomDAO.java
│       ├── TarefaRoomDAO.java
│       └── SessaoEstudoRoomDAO.java
├── services/
│   └── TimerService.java (Foreground Service)
├── receivers/
│   ├── TaskNotificationReceiver.java
│   └── BootReceiver.java
├── utils/
│   ├── PasswordHelper.java
│   ├── NotificationHelper.java
│   └── TaskNotificationScheduler.java
├── enums/
│   ├── Prioridade.java (BAIXA, MEDIA, ALTA)
│   └── EstadoTarefa.java (PENDENTE, EM_PROGRESSO, CONCLUIDA)
├── viewmodels/
│   ├── TasksViewModel.java
│   ├── SubjectsViewModel.java
│   ├── HomeViewModel.java
│   └── TimerViewModel.java
├── repositories/
│   ├── TarefaRepository.java
│   ├── DisciplinaRepository.java
│   ├── HomeRepository.java
│   └── TimerRepository.java
└── PreferenciasApp.java (EncryptedSharedPreferences)
```

### Fluxo de Navegacao

```
SplashActivity (2s)
    |
    +-- Logado? --> MainActivity (BottomNav)
    |                   |
    |                   +-- Home (Dashboard + Calendario)
    |                   +-- Subjects (CRUD Disciplinas)
    |                   +-- Tasks (CRUD Tarefas)
    |                   +-- Timer (Pomodoro)
    |                   +-- Statistics (Graficos)
    |                   +-- Settings (Tema, Idioma, Logout)
    |
    +-- Nao Logado? --> LoginActivity
                            |
                            +-- RegistroActivity
```

---

## Funcionalidades Implementadas

### 1. Autenticacao

| Feature | Status | Detalhes |
|---------|--------|----------|
| Login com email/senha | Implementado | Validacao completa |
| Registro de usuario | Implementado | Nome, email, senha |
| Hash de senha seguro | Implementado | PBKDF2 com salt aleatorio |
| Sessao persistente | Implementado | SharedPreferences |
| Logout | Implementado | Limpa sessao |

**Arquivos principais:**
- `LoginActivity.java`
- `RegistroActivity.java`
- `PasswordHelper.java`
- `PreferenciasApp.java`

---

### 2. Gestao de Disciplinas

| Feature | Status | Detalhes |
|---------|--------|----------|
| Criar disciplina | Implementado | Nome, codigo, cor |
| Editar disciplina | Implementado | Todos os campos |
| Deletar disciplina | Implementado | Com cascade para tarefas |
| Cores personalizadas | Implementado | 12 cores disponiveis |
| Detalhes da disciplina | Implementado | Estatisticas + tarefas |

**Arquivos principais:**
- `SubjectsFragment.java`
- `AdicionarEditarDisciplinaActivity.java`
- `DetalhesDisciplinaActivity.java`
- `DisciplinaAdapter.java`

---

### 3. Gestao de Tarefas

| Feature | Status | Detalhes |
|---------|--------|----------|
| Criar tarefa | Implementado | Titulo, descricao, disciplina, data, prioridade |
| Editar tarefa | Implementado | Todos os campos |
| Deletar tarefa | Implementado | Swipe left + undo |
| Marcar como concluida | Implementado | Checkbox + swipe right |
| Prioridades | Implementado | Baixa (verde), Media (laranja), Alta (vermelho) |
| Estados | Implementado | Pendente, Em Progresso, Concluida |
| Validacao de data | Implementado | Nao permite datas passadas |
| Notificacoes agendadas | Implementado | 24h, 1h antes e no vencimento |

**Arquivos principais:**
- `TasksFragment.java`
- `AdicionarEditarTarefaActivity.java`
- `TarefaAdapter.java`
- `TaskNotificationScheduler.java`

---

### 4. Timer Pomodoro

| Feature | Status | Detalhes |
|---------|--------|----------|
| Timer 25/5/15 | Implementado | Classico Pomodoro |
| Selecao de disciplina | Implementado | Obrigatorio para iniciar |
| Progress ring animado | Implementado | Indica tempo restante visualmente |
| Indicadores de ciclo | Implementado | 4 bolinhas (1/4, 2/4, 3/4, 4/4) |
| Estatisticas do dia | Implementado | Sessoes hoje, tempo hoje, streak |
| Salva sessao automatico | Implementado | Ao completar ciclo ou parar |
| Foreground Service | Implementado | Timer continua em background |
| Notificacao persistente | Implementado | Mostra tempo restante na barra |
| Notificacao ao terminar | Implementado | Som/vibracao quando sessao termina |

**Arquivos principais:**
- `TimerFragment.java`
- `TimerService.java`
- `NotificationHelper.java`
- `fragment_timer.xml`

**Drawables do Timer:**
- `progress_ring_background.xml` - Fundo do anel de progresso
- `progress_ring_progress.xml` - Progresso animado
- `circle_indicator_active.xml` - Indicador de ciclo ativo (branco)
- `circle_indicator_inactive.xml` - Indicador de ciclo inativo (transparente)
- `circle_timer_background.xml` - Fundo circular do timer
- `ic_timer_work.xml` - Icone para sessao de trabalho
- `ic_timer_break.xml` - Icone para intervalo
- `ic_stop.xml` - Icone do botao parar

---

### 5. Dashboard (Home)

| Feature | Status | Detalhes |
|---------|--------|----------|
| Total de disciplinas | Implementado | Card com contagem |
| Tarefas pendentes | Implementado | Card com contagem |
| Tempo de estudo hoje | Implementado | Card com duracao |
| Calendario mensal | Implementado | Grid visual 7x7 |
| Navegacao entre meses | Implementado | Setas prev/next |
| Indicadores no calendario | Implementado | Cores das disciplinas com tarefas |
| Tarefas do dia | Implementado | Click no dia mostra lista |

**Arquivos principais:**
- `HomeFragment.java`
- `CalendarioAdapter.java`
- `DiaCalendario.java`

---

### 6. Estatisticas

| Feature | Status | Detalhes |
|---------|--------|----------|
| Tempo por disciplina (7 dias) | Implementado | Lista com barras de progresso |
| Tempo total | Implementado | Soma de todas sessoes |

**Arquivos principais:**
- `StatisticsFragment.java`
- `EstatisticaAdapter.java`

---

### 7. Configuracoes

| Feature | Status | Detalhes |
|---------|--------|----------|
| Tema claro/escuro | Implementado | AppCompatDelegate |
| Idioma PT/EN | Implementado | Locale dinamico |
| Logout | Implementado | Limpa sessao e redireciona |

**Arquivos principais:**
- `ConfiguracoesActivity.java`
- `PreferenciasApp.java`

---

### 8. Sistema de Notificacoes

| Feature | Status | Detalhes |
|---------|--------|----------|
| Canais de notificacao | Implementado | Timer (alta prioridade) + Tarefas (padrao) |
| Notificacao timer running | Implementado | Persistente com progresso |
| Notificacao timer finished | Implementado | Som + vibracao |
| Lembrete 24h antes | Implementado | "Entrega amanha" |
| Lembrete 1h antes | Implementado | "Entrega em 1 hora" |
| Notificacao de atraso | Implementado | "Tarefa Atrasada!" |
| Reagendar apos boot | Implementado | BootReceiver |
| Cancelar ao deletar tarefa | Implementado | Automatico |

**Arquivos principais:**
- `NotificationHelper.java`
- `TaskNotificationScheduler.java`
- `TaskNotificationReceiver.java`
- `BootReceiver.java`
- `TimerService.java`

**Permissoes (AndroidManifest.xml):**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

---

### 9. Internacionalizacao

| Idioma | Status | Arquivo |
|--------|--------|---------|
| Ingles (padrao) | Implementado | `values/strings.xml` |
| Portugues | Implementado | `values-pt/strings.xml` |

---

### 10. UI/UX

| Feature | Status | Detalhes |
|---------|--------|----------|
| Material Design 3 | Implementado | Componentes modernos |
| Dark Mode | Implementado | values-night/themes.xml |
| Swipe gestures | Implementado | Delete (esquerda) e Complete (direita) |
| Snackbar com Undo | Implementado | Ao deletar tarefa |
| Empty states | Parcial | Mensagens basicas |
| Animacoes | Parcial | Fade in, slide up, progress ring |

---

## Banco de Dados

### Versao Atual: 3

**ATENCAO:** O app usa `fallbackToDestructiveMigration()` que APAGA todos os dados em atualizacoes. Isso PRECISA ser corrigido antes do lancamento!

### Entidades

#### Usuario
```java
@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String nome;
    private String email;
    private String senhaHash;  // PBKDF2 com salt
    private long dataCriacao;
}
```

#### Disciplina
```java
@Entity(tableName = "disciplinas",
        foreignKeys = @ForeignKey(
            entity = Usuario.class,
            parentColumns = "id",
            childColumns = "usuario_id",
            onDelete = CASCADE))
public class Disciplina {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long usuarioId;
    private String nome;
    private String codigo;
    private String cor; // Hex color (#FF5722)
    private long dataCriacao;
}
```

#### Tarefa
```java
@Entity(tableName = "tarefas",
        foreignKeys = @ForeignKey(
            entity = Disciplina.class,
            parentColumns = "id",
            childColumns = "disciplina_id",
            onDelete = CASCADE))
public class Tarefa {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String titulo;
    private String descricao;
    private long disciplinaId;
    private long dataEntrega; // Timestamp
    private Prioridade prioridade; // BAIXA=1, MEDIA=2, ALTA=3
    private EstadoTarefa estado; // PENDENTE=0, EM_PROGRESSO=1, CONCLUIDA=2
    private long dataCriacao;
}
```

#### SessaoEstudo
```java
@Entity(tableName = "sessoes_estudo",
        foreignKeys = @ForeignKey(
            entity = Disciplina.class,
            parentColumns = "id",
            childColumns = "disciplina_id",
            onDelete = CASCADE))
public class SessaoEstudo {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long disciplinaId;
    private long duracao; // Em segundos
    private long data; // Timestamp
}
```

### Type Converters
- `Prioridade` (enum) <-> int
- `EstadoTarefa` (enum) <-> int

---

## Melhorias Pendentes

### Prioridade 1 - CRITICO (Antes do Lancamento)

#### 1.1 ~~Remover fallbackToDestructiveMigration~~ CONCLUIDO
**Status:** Implementado em Janeiro 2026
**Solucao:** Criado sistema de migrations explicitas em `AppDatabase.java`

#### 1.2 ~~Solicitar Permissao de Notificacao (Android 13+)~~ CONCLUIDO
**Status:** Implementado em Janeiro 2026
**Solucao:** Dialog explicativo + ActivityResultLauncher em `MainActivity.java`

#### 1.3 ~~Criptografar SharedPreferences~~ CONCLUIDO
**Status:** Implementado em Janeiro 2026
**Solucao:** EncryptedSharedPreferences com AES256 em `PreferenciasApp.java`

---

### Prioridade 2 - IMPORTANTE (v1.0)

#### 2.1 ~~Busca e Filtros de Tarefas~~ CONCLUIDO
**Status:** Implementado em Janeiro 2026
**Implementado:**
- Campo de busca por texto (titulo/descricao)
- Filtros por periodo (todas, hoje, semana, atrasadas, concluidas)
- Ordenacao (data, prioridade, disciplina)
- Chips de filtro rapido no topo
- Integrado com MVVM (TasksViewModel)

#### 2.2 Estatisticas com Graficos
**Status:** PARCIAL (lista simples)
**O que falta:**
- Grafico de barras (tempo por dia da semana)
- Grafico pizza (distribuicao por disciplina)
- Historico completo (alem de 7 dias)
- Comparativo semanal

**Biblioteca sugerida:** MPAndroidChart

#### 2.3 ~~Confirmacao ao Deletar Disciplina~~ CONCLUIDO
**Status:** Ja existia em `DisciplinaAdapter.java`
AlertDialog com aviso sobre cascade delete implementado.

---

### Prioridade 3 - DIFERENCIAL (v1.1+)

#### 3.1 Subtarefas (Checklists)
Quebrar tarefas grandes em etapas menores.

**Nova tabela:**
```sql
CREATE TABLE subtarefas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tarefa_id INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    concluida INTEGER DEFAULT 0,
    ordem INTEGER DEFAULT 0,
    FOREIGN KEY(tarefa_id) REFERENCES tarefas(id) ON DELETE CASCADE
);
```

#### 3.2 Export/Import de Dados
- Exportar para JSON (backup manual)
- Importar de arquivo JSON
- Restaurar dados em novo dispositivo

#### 3.3 Widget para Home Screen
- Widget 2x2: Proximas 2 tarefas
- Widget 2x1: Timer rapido

#### 3.4 Som Personalizado no Timer
- Selecao de som de notificacao
- Opcao de vibracao on/off

---

### Prioridade 4 - ESCALA (v2.0+)

#### 4.1 ~~Migrar para MVVM~~ CONCLUIDO
**Status:** Implementado em Janeiro 2026
**Implementado:**
- ViewModels: TasksViewModel, SubjectsViewModel, HomeViewModel, TimerViewModel
- Repositories: TarefaRepository, DisciplinaRepository, HomeRepository, TimerRepository
- LiveData para observacao reativa
- Separacao clara View/ViewModel/Model

**Pendente para v2.0:**
- Hilt para injecao de dependencia

#### 4.2 ~~Testes Automatizados~~ PARCIAL
**Status:** Implementado parcialmente em Janeiro 2026
**Implementado:**
- Unit tests: PasswordHelperTest (11 testes)
- Instrumented tests: TarefaDAOTest (14 testes), DisciplinaDAOTest (10 testes)

**Pendente:**
- Testes para ViewModels
- UI tests com Espresso

#### 4.3 Backup em Nuvem
- Firebase ou Google Drive
- Sincronizacao manual
- Restaurar em novo dispositivo

#### 4.4 Modelo Freemium
**Versao Gratuita:**
- 5 disciplinas
- Estatisticas 7 dias

**Versao Pro (R$ 19-29):**
- Ilimitado
- Graficos completos
- Backup em nuvem
- Widgets

---

## Debitos Tecnicos

### Criticos - TODOS RESOLVIDOS

| Problema | Status | Solucao |
|----------|--------|---------|
| ~~fallbackToDestructiveMigration~~ | RESOLVIDO | Migrations explicitas |
| ~~Sem pedir permissao POST_NOTIFICATIONS~~ | RESOLVIDO | Dialog + ActivityResultLauncher |
| ~~SharedPreferences sem criptografia~~ | RESOLVIDO | EncryptedSharedPreferences |

### Moderados

| Problema | Arquivo | Status |
|----------|---------|--------|
| ~~Variaveis static no TimerFragment~~ | TimerFragment.java | RESOLVIDO - Migrado para ViewModel |
| Sem tratamento de erro no Executor | Varios | Pendente |
| Cursor sem try-catch | StatisticsFragment.java | Pendente |
| Falta indices nas foreign keys | Models | Pendente |

### Menores (Warnings)

| Problema | Arquivo | Impacto |
|----------|---------|---------|
| CURSOR_MISMATCH | DAOs | Warnings no build |
| Multiplos constructors | Models | Warnings no build |

---

## Guia para Retomar Desenvolvimento

### Setup do Ambiente

1. Android Studio Hedgehog ou superior
2. JDK 11+
3. Gradle 8.x

### Comandos Uteis

```bash
# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease

# Limpar build
./gradlew clean

# Rodar testes (quando existirem)
./gradlew test
```

### Ordem de Implementacao Sugerida

1. **PRIMEIRO:** Corrigir fallbackToDestructiveMigration
2. Adicionar solicitacao de permissao de notificacao
3. Implementar busca e filtros de tarefas
4. Adicionar graficos nas estatisticas
5. Adicionar confirmacao ao deletar disciplina
6. Migrar gradualmente para MVVM
7. Adicionar testes

### Arquivos Mais Importantes por Funcionalidade

| Funcionalidade | Arquivos Principais |
|----------------|---------------------|
| Timer | `TimerFragment.java`, `TimerService.java`, `fragment_timer.xml` |
| Tarefas | `TasksFragment.java`, `TarefaAdapter.java`, `AdicionarEditarTarefaActivity.java` |
| Notificacoes | `NotificationHelper.java`, `TaskNotificationScheduler.java`, `TimerService.java` |
| Banco | `AppDatabase.java`, `*RoomDAO.java` |
| Auth | `LoginActivity.java`, `PasswordHelper.java`, `PreferenciasApp.java` |
| Home | `HomeFragment.java`, `CalendarioAdapter.java` |

### Cores do App

| Nome | Hex | Uso |
|------|-----|-----|
| Primary | #6200EE | Cor principal, botoes |
| Primary Dark | #3700B3 | Status bar |
| Accent | #03DAC5 | Destaques |
| Error | #B00020 | Erros, prioridade alta |
| Prioridade Baixa | #4CAF50 | Verde |
| Prioridade Media | #FF9800 | Laranja |
| Prioridade Alta | #F44336 | Vermelho |

---

## O Que NAO Fazer

### Gamificacao Excessiva
- XP, niveis, badges elaborados
- **Por que:** Distrai do proposito, parece infantil

### Features Sociais
- Chat, grupos, compartilhamento
- **Por que:** Fora do escopo, adiciona complexidade

### Integracoes Complexas
- Google Calendar, Moodle, etc
- **Por que:** Cada integracao e ponto de falha

### IA/ML
- "Sugestoes inteligentes"
- **Por que:** Complexo, impreciso, desnecessario

---

## Concorrentes e Posicionamento

| App | Foco | Fraqueza | Nos somos melhores porque |
|-----|------|----------|---------------------------|
| Google Tasks | Tarefas genericas | Sem timer, sem estatisticas | Timer + Estatisticas integrados |
| Todoist | Produtividade geral | Complexo demais | Interface simples e focada |
| Forest | Timer gamificado | Sem gestao de tarefas | Tarefas + Timer em um so app |
| MyStudyLife | Horarios academicos | Interface datada | UI moderna, Material Design |

**Posicionamento StudyFlow:**
"Simples como Google Tasks + Timer integrado + Estatisticas para estudantes."

---

## Changelog

### v1.0.0 (Em desenvolvimento)

**Funcionalidades Core:**
- Sistema de autenticacao com hash seguro (PBKDF2)
- CRUD completo de disciplinas e tarefas
- Timer Pomodoro com ciclos automaticos (25/5/15)
- Calendario visual com indicadores de tarefas
- Estatisticas basicas (tempo por disciplina - 7 dias)
- Dark mode e internacionalizacao (PT/EN)

**Melhorias de UX (Janeiro 2026):**
- Progress ring animado no timer (visual do tempo restante)
- Indicadores de ciclo (4 bolinhas mostrando progresso)
- Estatisticas do dia no timer (sessoes, tempo total, streak)
- Swipe to delete/complete com undo
- Busca e filtros de tarefas (texto, periodo, ordenacao)
- Chips de filtro rapido

**Sistema de Notificacoes (Janeiro 2026):**
- Foreground Service para timer em background
- Notificacao persistente durante timer ativo
- Notificacao com som/vibracao ao terminar sessao
- Lembretes de tarefas: 24h antes, 1h antes, no vencimento
- Reagendamento automatico apos reinicio do dispositivo
- Cancelamento automatico ao deletar tarefa
- Solicitacao de permissao Android 13+ com dialog explicativo

**Arquitetura MVVM (Janeiro 2026):**
- ViewModels: TasksViewModel, SubjectsViewModel, HomeViewModel, TimerViewModel
- Repositories: TarefaRepository, DisciplinaRepository, HomeRepository, TimerRepository
- LiveData para comunicacao reativa View-ViewModel
- Separacao clara de responsabilidades

**Seguranca (Janeiro 2026):**
- EncryptedSharedPreferences (AES256) para dados sensiveis
- Removido fallbackToDestructiveMigration - migrations explicitas
- ProGuard/R8 configurado para release

**Testes (Janeiro 2026):**
- Unit tests: PasswordHelperTest (11 testes)
- Instrumented tests: TarefaDAOTest (14 testes), DisciplinaDAOTest (10 testes)

---

*Ultima atualizacao: 2026-01-15*
*Versao do documento: 3.0*
*Versao do App: 1.0.0-rc1*
