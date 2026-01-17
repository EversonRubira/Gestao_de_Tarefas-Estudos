# Manual do Desenvolvedor - StudyFlow

Documentacao tecnica para desenvolvedores que pretendem contribuir ou modificar o projeto.

---

## Indice

1. [Ambiente de Desenvolvimento](#ambiente-de-desenvolvimento)
2. [Arquitetura](#arquitetura)
3. [Estrutura do Projeto](#estrutura-do-projeto)
4. [Banco de Dados](#banco-de-dados)
5. [Componentes Principais](#componentes-principais)
6. [Seguranca](#seguranca)
7. [Servicos e Notificacoes](#servicos-e-notificacoes)
8. [Testes](#testes)
9. [Build e Deploy](#build-e-deploy)
10. [Convencoes de Codigo](#convencoes-de-codigo)

---

## Ambiente de Desenvolvimento

### Requisitos

| Ferramenta | Versao Minima |
|------------|---------------|
| Android Studio | Arctic Fox ou superior |
| JDK | 11 |
| Android SDK | 36 |
| Gradle | 8.13 |

### Configuracao Inicial

1. Clone o repositorio
2. Abra o projeto no Android Studio
3. Sincronize o Gradle (File > Sync Project with Gradle Files)
4. Configure o SDK 36 em Project Structure se necessario

### Dependencias

As dependencias estao definidas em `app/build.gradle`:

```groovy
dependencies {
    // AndroidX Core
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'

    // Room Database
    implementation 'androidx.room:room-runtime:2.6.1'
    annotationProcessor 'androidx.room:room-compiler:2.6.1'

    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.8.7'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.8.7'

    // Security
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'

    // Testes
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.2.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
}
```

---

## Arquitetura

O projeto utiliza o padrao **MVVM (Model-View-ViewModel)** com as seguintes camadas:

```
View (Activities/Fragments)
    |
    v
ViewModel (Logica de apresentacao, LiveData)
    |
    v
Repository (Abstrai acesso a dados)
    |
    v
DAO (Data Access Object - Room)
    |
    v
Database (SQLite via Room)
```

### Fluxo de Dados

1. **View** observa LiveData do ViewModel
2. **ViewModel** solicita dados ao Repository
3. **Repository** executa queries via DAO em thread de background
4. **DAO** retorna LiveData que propaga mudancas automaticamente

---

## Estrutura do Projeto

```
app/src/main/java/com/example/gestaodetarefasestudos/
├── activities/
│   ├── SplashActivity.java          # Tela inicial
│   ├── LoginActivity.java           # Autenticacao
│   ├── RegistroActivity.java        # Cadastro
│   ├── MainActivity.java            # Navegacao principal
│   ├── AdicionarEditarTarefaActivity.java
│   ├── AdicionarEditarDisciplinaActivity.java
│   ├── DetalhesDisciplinaActivity.java
│   ├── ConfiguracoesActivity.java
│   └── BaseActivity.java            # Classe base
│
├── fragments/
│   ├── HomeFragment.java            # Dashboard
│   ├── SubjectsFragment.java        # Disciplinas
│   ├── TasksFragment.java           # Tarefas
│   ├── StatisticsFragment.java      # Estatisticas
│   └── TimerFragment.java           # Pomodoro
│
├── viewmodels/
│   ├── HomeViewModel.java
│   ├── TasksViewModel.java
│   ├── SubjectsViewModel.java
│   ├── TimerViewModel.java
│   └── StatisticsViewModel.java
│
├── repositories/
│   ├── HomeRepository.java
│   ├── TarefaRepository.java
│   ├── DisciplinaRepository.java
│   └── TimerRepository.java
│
├── database/
│   ├── AppDatabase.java             # Room Database
│   ├── Converters.java              # Type Converters
│   ├── UsuarioRoomDAO.java
│   ├── DisciplinaRoomDAO.java
│   ├── TarefaRoomDAO.java
│   └── SessaoEstudoRoomDAO.java
│
├── models/
│   ├── Usuario.java
│   ├── Disciplina.java
│   ├── Tarefa.java
│   ├── SessaoEstudo.java
│   └── DiaCalendario.java           # Auxiliar
│
├── adapters/
│   ├── TarefaAdapter.java
│   ├── DisciplinaAdapter.java
│   ├── CalendarioAdapter.java
│   └── EstatisticaAdapter.java
│
├── services/
│   └── TimerService.java            # Foreground Service
│
├── receivers/
│   ├── BootReceiver.java
│   └── TaskNotificationReceiver.java
│
├── utils/
│   ├── PasswordHelper.java          # Hash PBKDF2
│   ├── NotificationHelper.java
│   └── TaskNotificationScheduler.java
│
├── enums/
│   ├── Prioridade.java
│   └── EstadoTarefa.java
│
└── PreferenciasApp.java             # SharedPreferences
```

---

## Banco de Dados

### Room Database

Arquivo: `database/AppDatabase.java`

```java
@Database(
    entities = {Usuario.class, Disciplina.class, Tarefa.class, SessaoEstudo.class},
    version = 3,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioRoomDAO usuarioDAO();
    public abstract DisciplinaRoomDAO disciplinaDAO();
    public abstract TarefaRoomDAO tarefaDAO();
    public abstract SessaoEstudoRoomDAO sessaoEstudoDAO();
}
```

### Entidades

#### Usuario
```java
@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nome;
    @ColumnInfo(name = "email")
    private String email;          // Unico
    private String senhaHash;      // PBKDF2
    private long dataCriacao;
}
```

#### Disciplina
```java
@Entity(tableName = "disciplinas",
    foreignKeys = @ForeignKey(
        entity = Usuario.class,
        parentColumns = "id",
        childColumns = "usuarioId",
        onDelete = CASCADE
    ))
public class Disciplina {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int usuarioId;
    private String nome;
    private String codigo;
    private String cor;            // Hexadecimal
    private long dataCriacao;
}
```

#### Tarefa
```java
@Entity(tableName = "tarefas",
    foreignKeys = @ForeignKey(
        entity = Disciplina.class,
        parentColumns = "id",
        childColumns = "disciplinaId",
        onDelete = CASCADE
    ))
public class Tarefa {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String titulo;
    private String descricao;
    private int disciplinaId;
    private long dataEntrega;
    private Prioridade prioridade; // ALTA, MEDIA, BAIXA
    private EstadoTarefa estado;   // PENDENTE, CONCLUIDA
    private long dataCriacao;
}
```

#### SessaoEstudo
```java
@Entity(tableName = "sessoes_estudo",
    foreignKeys = @ForeignKey(
        entity = Disciplina.class,
        parentColumns = "id",
        childColumns = "disciplinaId",
        onDelete = CASCADE
    ))
public class SessaoEstudo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int disciplinaId;
    private long duracao;          // Em segundos
    private long data;
}
```

### DAOs

Exemplo de queries comuns:

```java
@Dao
public interface TarefaRoomDAO {

    @Query("SELECT * FROM tarefas WHERE disciplinaId = :disciplinaId")
    LiveData<List<Tarefa>> getTarefasPorDisciplina(int disciplinaId);

    @Query("SELECT * FROM tarefas WHERE estado = :estado")
    LiveData<List<Tarefa>> getTarefasPorEstado(EstadoTarefa estado);

    @Query("SELECT * FROM tarefas WHERE dataEntrega BETWEEN :inicio AND :fim")
    LiveData<List<Tarefa>> getTarefasEntreDatas(long inicio, long fim);

    @Insert
    long inserir(Tarefa tarefa);

    @Update
    void atualizar(Tarefa tarefa);

    @Delete
    void eliminar(Tarefa tarefa);
}
```

### Migrations

Para novas versoes do banco:

```java
static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE tarefas ADD COLUMN novaColuna TEXT");
    }
};
```

---

## Componentes Principais

### MainActivity

Gerencia a navegacao por abas usando Bottom Navigation e FragmentManager:

```java
public class MainActivity extends BaseActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_subjects) {
                fragment = new SubjectsFragment();
            }
            // ...
            loadFragment(fragment);
            return true;
        });
    }
}
```

### ViewModels

Gerenciam estado e logica de apresentacao:

```java
public class TasksViewModel extends ViewModel {
    private final TarefaRepository repository;
    private LiveData<List<Tarefa>> tarefas;

    public TasksViewModel(Application app) {
        repository = new TarefaRepository(app);
        tarefas = repository.getAllTarefas();
    }

    public LiveData<List<Tarefa>> getTarefas() {
        return tarefas;
    }

    public void inserir(Tarefa tarefa) {
        repository.inserir(tarefa);
    }
}
```

### Repositories

Abstraem o acesso a dados e executam operacoes em background:

```java
public class TarefaRepository {
    private final TarefaRoomDAO tarefaDAO;
    private final ExecutorService executor;

    public TarefaRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        tarefaDAO = db.tarefaDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    public void inserir(Tarefa tarefa) {
        executor.execute(() -> tarefaDAO.inserir(tarefa));
    }
}
```

---

## Seguranca

### Hash de Senhas

Arquivo: `utils/PasswordHelper.java`

Implementacao PBKDF2 com SHA256:

```java
public class PasswordHelper {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String hashSenha(String senha) {
        byte[] salt = gerarSalt();
        byte[] hash = pbkdf2(senha, salt);
        return Base64.encodeToString(salt, DEFAULT) + ":" +
               Base64.encodeToString(hash, DEFAULT);
    }

    public static boolean verificarSenha(String senha, String senhaHash) {
        String[] partes = senhaHash.split(":");
        byte[] salt = Base64.decode(partes[0], DEFAULT);
        byte[] hashOriginal = Base64.decode(partes[1], DEFAULT);
        byte[] hashNovo = pbkdf2(senha, salt);
        return MessageDigest.isEqual(hashOriginal, hashNovo);
    }
}
```

### Preferencias Criptografadas

Arquivo: `PreferenciasApp.java`

```java
public class PreferenciasApp {
    private SharedPreferences prefs;

    public PreferenciasApp(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

            prefs = EncryptedSharedPreferences.create(
                context,
                "prefs_seguras",
                masterKey,
                PrefKeyEncryptionScheme.AES256_SIV,
                PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            // Fallback para SharedPreferences normal
            prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        }
    }
}
```

---

## Servicos e Notificacoes

### TimerService

Foreground Service para manter o timer ativo:

```java
public class TimerService extends Service {
    private static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        return START_STICKY;
    }
}
```

### Notificacoes de Tarefas

Agendamento via AlarmManager:

```java
public class TaskNotificationScheduler {
    public void agendar(Context context, Tarefa tarefa) {
        AlarmManager alarmManager = (AlarmManager)
            context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra("tarefa_id", tarefa.getId());

        PendingIntent pending = PendingIntent.getBroadcast(
            context, tarefa.getId(), intent, FLAG_UPDATE_CURRENT);

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            tarefa.getDataEntrega(),
            pending
        );
    }
}
```

### BootReceiver

Reagenda notificacoes apos reinicializacao:

```java
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reagendar todas as notificacoes
        }
    }
}
```

---

## Testes

### Testes Unitarios

Localizados em `app/src/test/`:

```java
public class PasswordHelperTest {
    @Test
    public void testHashEVerificacao() {
        String senha = "minhasenha123";
        String hash = PasswordHelper.hashSenha(senha);

        assertTrue(PasswordHelper.verificarSenha(senha, hash));
        assertFalse(PasswordHelper.verificarSenha("outrasenha", hash));
    }
}
```

### Testes Instrumentados

Localizados em `app/src/androidTest/`:

```java
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AppDatabase db;
    private TarefaRoomDAO tarefaDAO;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        tarefaDAO = db.tarefaDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void inserirEBuscarTarefa() {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Teste");
        tarefaDAO.inserir(tarefa);

        List<Tarefa> todas = tarefaDAO.getAllSync();
        assertEquals(1, todas.size());
    }
}
```

### Executar Testes

```bash
# Testes unitarios
./gradlew test

# Testes instrumentados
./gradlew connectedAndroidTest
```

---

## Build e Deploy

### Debug Build

```bash
./gradlew assembleDebug
```

APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

1. Configure a keystore em `app/build.gradle`:

```groovy
android {
    signingConfigs {
        release {
            storeFile file("keystore.jks")
            storePassword "password"
            keyAlias "alias"
            keyPassword "password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
}
```

2. Execute:
```bash
./gradlew assembleRelease
```

### ProGuard Rules

Arquivo: `app/proguard-rules.pro`

```proguard
# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Enums
-keepclassmembers enum * { *; }
```

---

## Convencoes de Codigo

### Nomenclatura

| Tipo | Convencao | Exemplo |
|------|-----------|---------|
| Classes | PascalCase | `TarefaAdapter` |
| Metodos | camelCase | `getTarefas()` |
| Variaveis | camelCase | `listaTarefas` |
| Constantes | UPPER_SNAKE | `MAX_ITEMS` |
| Layouts | snake_case | `activity_main.xml` |
| IDs | snake_case | `@+id/btn_salvar` |

### Estrutura de Activity/Fragment

```java
public class ExemploActivity extends AppCompatActivity {
    // 1. Constantes
    private static final String TAG = "ExemploActivity";

    // 2. Views
    private Button btnSalvar;
    private EditText edtNome;

    // 3. ViewModels e dados
    private ExemploViewModel viewModel;

    // 4. onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exemplo);

        initViews();
        setupViewModel();
        setupListeners();
        observeData();
    }

    // 5. Metodos de inicializacao
    private void initViews() { }
    private void setupViewModel() { }
    private void setupListeners() { }
    private void observeData() { }

    // 6. Outros metodos
}
```

### Commits

Formato sugerido:
```
tipo: descricao curta

Descricao detalhada se necessario.
```

Tipos: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`

---

## Recursos Adicionais

### Strings

- `res/values/strings.xml` - Ingles (padrao)
- `res/values-pt/strings.xml` - Portugues

### Cores

Definidas em `res/values/colors.xml`. Inclui 43 cores para disciplinas.

### Icones

Localizados em `res/drawable/`. Usar Vector Drawables quando possivel.

---

## Problemas Comuns

### Erro de SDK

```
SDK location not found
```
Solucao: File > Project Structure > SDK Location

### Erro de Gradle

```
Could not resolve dependencies
```
Solucao: File > Invalidate Caches / Restart

### Room Compile Error

```
Cannot find implementation for...
```
Solucao: Verificar anotacoes e rebuild project

---

*Documentacao atualizada em Janeiro 2026*
