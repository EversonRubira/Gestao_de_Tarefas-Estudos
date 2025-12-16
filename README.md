# StudyFlow - Gestão de Tarefas e Estudos

App Android para gerir disciplinas, tarefas e tempo de estudo. Feito para o projeto de PAM 2025/26.

## Informações Técnicas

- **SDK Compilação**: 36
- **SDK Mínimo**: 24 (Android 7.0)
- **SDK Target**: 36
- **Java Version**: 11
- **Gradle**: 8.7
- **Android Gradle Plugin**: 8.7.2

## Bibliotecas Utilizadas

### AndroidX (Nativas)
- AppCompat v1.7.0
- Material Design v1.12.0
- ConstraintLayout v2.2.0
- RecyclerView (incluído no AppCompat)

### Bibliotecas Externas
- Nenhuma biblioteca externa utilizada
- Apenas bibliotecas nativas do Android SDK

## Como Importar o Projeto

1. Abrir Android Studio
2. File > Open
3. Selecionar a pasta do projeto
4. Esperar o Gradle sync terminar
5. Se der erro de SDK, ir em File > Project Structure e configurar SDK 36
6. Run > Run 'app'

Nota: Projeto não usa bibliotecas externas, apenas Android SDK nativo.

## Descrição da App

O StudyFlow serve para estudantes organizarem as disciplinas, tarefas e controlarem o tempo de estudo. Tem várias funcionalidades uteis para quem estuda.

### Funcionalidades Principais

**Gestão de Disciplinas**
- Adicionar disciplinas com nome, código e cor personalizada
- Cada disciplina tem uma cor diferente para facilitar identificação
- Ver detalhes completos da disciplina com estatísticas
- Editar e apagar disciplinas

**Gestão de Tarefas**
- Criar tarefas associadas a disciplinas
- Definir data de entrega
- Marcar prioridade (Baixa, Média, Alta) com cores diferentes
- Estados: Pendente, Em Progresso, Concluída
- Filtrar tarefas por estado
- Marcar tarefas como concluídas rapidamente

**Calendário Visual**
- Calendário do mês com grid de dias
- Cada dia mostra até 3 cores de disciplinas que têm tarefas
- Bolinhas coloridas indicam as disciplinas
- Contador mostra quantas tarefas tem no dia
- Clicar no dia mostra as tarefas desse dia
- Navegar entre meses (anterior/próximo)
- Dia atual marcado com borda diferente

**Timer Pomodoro**
- Cronometro para sessões de estudo
- Tempo de trabalho e descanso configuráveis
- Tem que escolher a disciplina antes de começar
- Salva automaticamente o tempo estudado no banco de dados
- Se parar antes do fim, salva o tempo parcial
- Alterna automaticamente entre trabalho e descanso

**Estatísticas**
- Lista com tempo de estudo por disciplina (últimos 7 dias)
- Cards coloridos para cada disciplina com tempo formatado
- Cores das disciplinas aparecem nos cards
- Se não tiver dados mostra mensagem

**Dashboard (Home)**
- Cards com resumo: total de disciplinas, tarefas pendentes, tempo estudado hoje
- Tempo de estudo detalhado por disciplina
- Calendário visual integrado
- Tudo numa tela só para ter visão geral rápida

**Outras Features**
- Splash screen com animação no início
- Bottom Navigation para navegar entre as 5 secções
- Suporte para Português e Inglês
- Dados de exemplo criados na primeira vez que abre a app
- Todas as cores e textos organizados nos recursos

## Estrutura do Banco de Dados

**SQLite** com 3 tabelas:

### Tabela: disciplinas
- id (chave primária)
- nome
- codigo (único)
- cor (hexadecimal tipo #FF5722)
- data_criacao (timestamp)

### Tabela: tarefas
- id (chave primária)
- titulo
- descricao
- disciplina_id (foreign key -> disciplinas)
- data_entrega (timestamp)
- prioridade (1=Baixa, 2=Média, 3=Alta)
- estado (0=Pendente, 1=Em Progresso, 2=Concluída)
- data_criacao (timestamp)

### Tabela: sessoes_estudo
- id (chave primária)
- disciplina_id (foreign key -> disciplinas)
- duracao (em segundos)
- data (timestamp)

Nota: As foreign keys têm CASCADE DELETE, ou seja, se apagar uma disciplina apaga automaticamente as tarefas e sessões dessa disciplina.

## Arquitetura do Código

Seguimos uma arquitetura em camadas:

- **Activities**: Telas principais (5 ao todo)
  - SplashActivity
  - MainActivity (com Bottom Navigation)
  - AdicionarEditarDisciplinaActivity
  - AdicionarEditarTarefaActivity
  - DetalhesDisciplinaActivity

- **Fragments**: Secções da app (5 abas)
  - HomeFragment (dashboard)
  - SubjectsFragment
  - TasksFragment
  - StatisticsFragment
  - TimerFragment

- **DAOs**: Acesso ao banco de dados
  - DisciplinaDAO
  - TarefaDAO
  - SessaoEstudoDAO

- **Adapters**: Para as listas (RecyclerView)
  - DisciplinaAdapter
  - TarefaAdapter
  - CalendarioAdapter

- **Models**: Classes com os dados
  - Disciplina
  - Tarefa
  - SessaoEstudo
  - DiaCalendario

- **Enums**: Para ter valores fixos
  - Prioridade
  - EstadoTarefa

- **Database**: Gestão do SQLite
  - DatabaseHelper (Singleton)

## Como Usar a App

### Primeira Vez
1. Abre a app e vê o splash screen
2. Na primeira abertura cria automaticamente 3 disciplinas e algumas tarefas de exemplo
3. Podes apagar estas e criar as tuas próprias

### Adicionar Disciplina
1. Ir na aba "Subjects"
2. Clicar no botão + (FloatingActionButton)
3. Preencher nome, código e escolher cor
4. Guardar

### Adicionar Tarefa
1. Ir na aba "Tasks"
2. Clicar no botão +
3. Escolher disciplina, preencher título, descrição (opcional)
4. Escolher data de entrega, prioridade e estado
5. Guardar

### Usar o Timer
1. Ir na aba "Timer"
2. Escolher a disciplina (obrigatório)
3. Configurar tempo de trabalho e descanso se quiser
4. Clicar em Iniciar
5. O tempo é salvo automaticamente quando termina ou quando clicas em Parar

### Ver Estatísticas
1. Ir na aba "Statistics"
2. Ver lista com tempo por disciplina (últimos 7 dias)
3. Cada disciplina aparece num card com sua cor e tempo estudado

### Ver Detalhes de Disciplina
1. Ir na aba "Subjects"
2. Clicar numa disciplina da lista
3. Vê total de tarefas, pendentes, concluídas e tempo estudado
4. Vê lista de tarefas dessa disciplina
5. Pode editar ou apagar a disciplina pelo menu (3 pontinhos em cima)

### Calendário
1. Na Home (aba inicial)
2. Scroll até o calendário
3. Dias com tarefas têm bolinhas coloridas e um número
4. Clicar num dia para ver que tarefas tem
5. Usar setas para mudar de mês

## Navegação

A app usa Bottom Navigation com 5 abas:
- Home (ícone casa)
- Subjects (ícone livro)
- Tasks (ícone checklist)
- Statistics (ícone gráfico)
- Timer (ícone relógio)

Também tem menus de contexto em alguns sítios (ex: detalhes da disciplina tem opção editar/eliminar).

## Padrões de Design Usados

- **Singleton**: DatabaseHelper para ter só uma instância do banco
- **DAO**: Separar lógica de banco de dados
- **ViewHolder**: Nos adapters para performance
- **Callback/Listener**: Para comunicação entre componentes

## Internacionalização

A app funciona em:
- Português (PT)
- Inglês (EN)

Todos os textos estão em `res/values/strings.xml` (inglês) e `res/values-pt/strings.xml` (português).

## Cores e Tema

Usamos Material Design com paleta de cores personalizada definida em `res/values/colors.xml`.
Tem 43 cores diferentes para usar nas disciplinas e na interface.

## Notas do Desenvolvimento

- Usamos SharedPreferences para verificar se é a primeira vez que abre (para criar dados de exemplo)
- O banco de dados usa Singleton para não criar várias instâncias
- Foreign keys estão ativadas no SQLite
- Queries otimizadas com JOIN e GROUP BY para performance
- Comentários no código em português para facilitar entendimento
- Validação de dados nos formulários (ex: código de disciplina tem que ser único)

## Problemas Conhecidos

- Se mudar o idioma do sistema enquanto a app está aberta, precisa fechar e abrir de novo
- O calendário só mostra até 3 cores por dia, se tiver mais disciplinas não aparecem todas

## Melhorias Futuras Possíveis

- Notificações para lembrar das tarefas próximas do prazo
- Export de dados para CSV ou PDF
- Temas escuro/claro
- Widget para Home Screen
- Backup na nuvem
- Login/registo de utilizadores

## Apps Similares (Pesquisadas)

- Google Tasks
- Microsoft To Do
- Todoist
- Forest (para timer de estudo)
- MyStudyLife

O nosso diferencial é juntar tudo numa app só: tarefas + timer + estatísticas + calendário visual único.

## Autores

Projeto desenvolvido para a UC de Programação de Aplicações Móveis 2025/26 - IPS Setúbal

## Licença

Projeto académico sem licença específica.
