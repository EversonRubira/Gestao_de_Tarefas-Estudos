# StudyFlow - Gestao de Tarefas e Estudos

Aplicativo Android para gestao de disciplinas, tarefas e controle de tempo de estudo.
Projeto desenvolvido para a UC de Programacao de Aplicacoes Moveis 2025/26 - IPS Setubal.

---

## Sobre o Projeto

O StudyFlow e uma aplicacao completa para estudantes organizarem suas atividades academicas. Combina gestao de tarefas, timer Pomodoro, calendario visual e estatisticas de produtividade numa unica aplicacao.

### Funcionalidades Principais

- **Gestao de Disciplinas** - Cadastro com nome, codigo e cor personalizada
- **Gestao de Tarefas** - Tarefas com prioridade, prazo e estado
- **Timer Pomodoro** - Sessoes de estudo com registro automatico
- **Calendario Visual** - Visualizacao de tarefas por dia
- **Estatisticas** - Tempo de estudo por disciplina
- **Dashboard** - Visao geral com metricas importantes

---

## Informacoes Tecnicas

| Requisito | Versao |
|-----------|--------|
| SDK Compilacao | 36 (Android 15) |
| SDK Minimo | 24 (Android 7.0) |
| SDK Target | 36 |
| Java | 11 |
| Gradle | 8.13 |
| Room Database | 2.6.1 |

---

## Bibliotecas Utilizadas

Todas as bibliotecas sao oficiais do Android sob Apache License 2.0.

| Biblioteca | Versao | Descricao |
|------------|--------|-----------|
| AppCompat | 1.7.0 | Compatibilidade com versoes antigas |
| Material Components | 1.12.0 | Componentes Material Design |
| ConstraintLayout | 2.2.0 | Layout responsivo e flexivel |
| Room Database | 2.6.1 | ORM para SQLite |
| Security Crypto | 1.1.0-alpha06 | Criptografia de preferencias |

**Bibliotecas de Teste** (nao incluidas no APK):
- JUnit 4.13.2
- Espresso 3.6.1

---

## Arquitetura do Projeto

O projeto segue o padrao MVVM (Model-View-ViewModel):

```
app/src/main/java/com/example/gestaodetarefasestudos/
├── activities/          # 8 Activities
├── fragments/           # 5 Fragments (abas)
├── viewmodels/          # ViewModels
├── repositories/        # Repositorios
├── database/            # Room Database e DAOs
├── models/              # Entidades
├── adapters/            # RecyclerView Adapters
├── services/            # Background Services
├── utils/               # Classes utilitarias
└── enums/               # Enumeracoes
```

### Activities
- `SplashActivity` - Tela inicial animada
- `LoginActivity` - Autenticacao
- `RegistroActivity` - Cadastro de conta
- `MainActivity` - Navegacao principal com 5 abas
- `AdicionarEditarDisciplinaActivity` - CRUD de disciplinas
- `AdicionarEditarTarefaActivity` - CRUD de tarefas
- `DetalhesDisciplinaActivity` - Detalhes da disciplina
- `ConfiguracoesActivity` - Configuracoes do app

### Fragments
- `HomeFragment` - Dashboard
- `SubjectsFragment` - Lista de disciplinas
- `TasksFragment` - Lista de tarefas
- `StatisticsFragment` - Estatisticas
- `TimerFragment` - Timer Pomodoro

---

## Estrutura do Banco de Dados

Room Database (SQLite) com 4 tabelas:

### Tabela: usuarios
| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | INTEGER | Chave primaria |
| nome | TEXT | Nome do usuario |
| email | TEXT | Email (unico) |
| senha_hash | TEXT | Hash PBKDF2 |
| data_criacao | INTEGER | Timestamp |

### Tabela: disciplinas
| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | INTEGER | Chave primaria |
| usuario_id | INTEGER | FK -> usuarios |
| nome | TEXT | Nome da disciplina |
| codigo | TEXT | Codigo (unico por usuario) |
| cor | TEXT | Cor hexadecimal |
| data_criacao | INTEGER | Timestamp |

### Tabela: tarefas
| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | INTEGER | Chave primaria |
| titulo | TEXT | Titulo |
| descricao | TEXT | Descricao |
| disciplina_id | INTEGER | FK -> disciplinas |
| data_entrega | INTEGER | Data limite |
| prioridade | INTEGER | 1=Baixa, 2=Media, 3=Alta |
| estado | INTEGER | 0=Pendente, 1=Concluida |
| data_criacao | INTEGER | Timestamp |

### Tabela: sessoes_estudo
| Coluna | Tipo | Descricao |
|--------|------|-----------|
| id | INTEGER | Chave primaria |
| disciplina_id | INTEGER | FK -> disciplinas |
| duracao | INTEGER | Duracao em segundos |
| data | INTEGER | Timestamp |

**Nota:** Foreign keys com CASCADE DELETE ativado.

---

## Como Executar

### Pre-requisitos
- Android Studio (versao recente)
- JDK 11 ou superior
- Android SDK 36

### Passos

1. Clone o repositorio:
```bash
git clone <url-do-repositorio>
```

2. Abra o Android Studio

3. File > Open > Selecione a pasta do projeto

4. Aguarde a sincronizacao do Gradle

5. Se houver erro de SDK:
   - File > Project Structure
   - Configure o SDK 36

6. Execute: Run > Run 'app'

### Build do APK

```bash
# Debug
./gradlew assembleDebug

# Release
./gradlew assembleRelease
```

O APK sera gerado em `app/build/outputs/apk/`

---

## Seguranca

- **Senhas**: Hash PBKDF2 com SHA256 e salt aleatorio (10.000 iteracoes)
- **Preferencias**: EncryptedSharedPreferences com AES256-GCM
- **Dados**: Isolamento por usuario no banco de dados

---

## Internacionalizacao

Idiomas suportados:
- Portugues (PT)
- Ingles (EN)

Arquivos de strings em:
- `res/values/strings.xml` (ingles)
- `res/values-pt/strings.xml` (portugues)

---

## Problemas Conhecidos

1. Ao mudar o idioma do sistema com a app aberta, e necessario reiniciar a aplicacao
2. O calendario exibe no maximo 3 cores por dia

---

## Documentacao Adicional

- [Manual do Usuario](docs/MANUAL_USUARIO.md) - Guia de uso da aplicacao
- [Manual do Desenvolvedor](docs/MANUAL_DESENVOLVEDOR.md) - Documentacao tecnica

---

## Licenca

Projeto academico desenvolvido para fins educacionais.
Bibliotecas utilizadas sob Apache License 2.0.

---

## Autores

Projeto desenvolvido para a UC de Programacao de Aplicacoes Moveis 2025/26
Instituto Politecnico de Setubal
