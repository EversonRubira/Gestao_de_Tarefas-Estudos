Relatório do Projeto - StudyFlow

Programação de Aplicações Móveis - 2025/26
Instituto Politécnico de Setúbal

================================================================================

1. Identificação do Projeto

Nome da Aplicação: StudyFlow

Slogan: "Organiza. Estuda. Conquista."

Tipo: Aplicação móvel Android para gestão académica

Tema: Produtividade e Educação

================================================================================

2. Descrição da Aplicação

2.1 Objetivo Central

O StudyFlow é uma aplicação Android desenvolvida para ajudar estudantes a gerir as suas disciplinas, tarefas académicas e tempo de estudo de forma integrada e visual.

A app resolve três problemas principais dos estudantes:

1. Desorganização de tarefas
   Os estudantes frequentemente esquecem-se de trabalhos e prazos de entrega. A app centraliza todas as tarefas num só local com alertas visuais.

2. Falta de controlo de tempo
   Muitos estudantes não sabem quanto tempo dedicam a cada disciplina. A app regista automaticamente o tempo de estudo através do timer integrado.

3. Falta de visão global
   Sem uma visão geral, os estudantes não têm noção da carga de trabalho semanal ou mensal. O calendário visual da app resolve este problema.

2.2 Público-Alvo

A aplicação é direcionada para estudantes do ensino superior e secundário que precisam de:
  - Organizar várias disciplinas simultaneamente
  - Gerir múltiplas tarefas com diferentes prazos
  - Acompanhar o tempo dedicado a cada disciplina
  - Ter uma visão geral do mês de estudo

================================================================================

3. Funcionalidades Implementadas

3.1 Gestão de Disciplinas

A app permite adicionar, editar e eliminar disciplinas. Cada disciplina tem um código único e uma cor personalizada para identificação visual rápida. É possível visualizar estatísticas completas por disciplina, incluindo número de tarefas e tempo de estudo total.

3.2 Gestão de Tarefas

Os utilizadores podem criar tarefas associadas a disciplinas específicas. Para cada tarefa é possível definir data de entrega, prioridade (Baixa, Média ou Alta) e estado (Pendente, Em Progresso ou Concluída). As tarefas podem ser filtradas por estado e marcadas rapidamente como concluídas através de um checkbox. Cada prioridade tem um código de cor diferente para facilitar a identificação.

3.3 Calendário Visual Híbrido (FUNCIONALIDADE DESTAQUE)

Esta é a funcionalidade mais inovadora da aplicação. O calendário mensal apresenta-se num formato grid 7x7. Cada dia pode mostrar até 3 cores de disciplinas que têm tarefas agendadas. Os indicadores visuais incluem bolinhas coloridas e um contador do número total de tarefas. É possível navegar entre meses usando setas. Ao clicar num dia, aparece a lista completa de tarefas desse dia. O dia atual aparece destacado com uma borda diferente.

Diferencial: Este sistema é único porque combina um calendário tradicional com um código de cores por disciplina, permitindo ver rapidamente a carga de trabalho de cada dia sem precisar abrir os detalhes.

3.4 Timer Pomodoro Inteligente

A app integra um cronómetro baseado na técnica Pomodoro, com períodos de trabalho e descanso. Os tempos são configuráveis pelo utilizador. É obrigatório selecionar uma disciplina antes de iniciar, para permitir o rastreamento correto. O salvamento das sessões é automático no banco de dados. Mesmo que o utilizador pare manualmente, o tempo parcial é guardado. O timer alterna automaticamente entre ciclos de trabalho e descanso.

3.5 Estatísticas Visuais

A secção de estatísticas apresenta uma lista com a distribuição de tempo de estudo por disciplina dos últimos 7 dias. Usa cards coloridos com as cores dinâmicas de cada disciplina. O tempo é formatado automaticamente em minutos ou horas conforme necessário. O layout é limpo e fácil de interpretar.

3.6 Dashboard Completo

O dashboard apresenta cards com as métricas principais: total de disciplinas, tarefas pendentes e tempo estudado hoje. Também mostra o tempo de estudo detalhado por disciplina para o dia atual. O calendário visual está integrado diretamente no dashboard. Há navegação rápida para outras secções através de botões.

3.7 Navegação e Interface

A navegação principal usa uma Bottom Navigation Bar com 5 abas. Ao iniciar a app aparece um Splash Screen animado. O design segue o Material Design com um tema personalizado. Existem menus de contexto para acções secundárias. FloatingActionButtons permitem adição rápida de disciplinas e tarefas.

3.8 Internacionalização

A app tem suporte total para Português (PT) e Inglês (EN). A detecção do idioma do sistema é automática. Todas as strings estão externalizadas nos ficheiros de recursos.

================================================================================

4. Análise de Mercado

4.1 Aplicações Similares

Analisámos várias apps existentes no mercado:

Google Tasks: Oferece gestão de tarefas e integração com o ecossistema Google, mas não tem timer de estudo, não tem estatísticas e não usa cores por categoria.

Microsoft To Do: Tem tarefas, listas e lembretes, mas não tem timer de estudo nem calendário visual.

Todoist: Oferece gestão avançada de tarefas com prioridades, mas não tem timer integrado e não tem foco específico em estudo.

Forest: Tem um timer de estudo gamificado muito popular, mas não tem gestão de tarefas nem calendário.

MyStudyLife: Oferece horários, tarefas e gestão de exames, mas usa um calendário tradicional, não tem timer e a interface é considerada complexa.

4.2 Diferenciais do StudyFlow

O que torna o StudyFlow único:

1. Tudo-em-um: Combina tarefas, timer e estatísticas numa única aplicação. O utilizador não precisa usar múltiplas apps.

2. Calendário visual híbrido: O sistema único com código de cores e contadores não existe em nenhuma outra app analisada.

3. Simplicidade: A interface é limpa e directa, sem features desnecessárias que confundem o utilizador.

4. Foco em estudos: Desenhado especificamente para o ambiente académico, não é uma app genérica de produtividade.

5. Offline-first: Funciona totalmente sem internet, todos os dados são locais e privados.

================================================================================

5. Requisitos Técnicos Implementados

5.1 Requisitos Obrigatórios

Todos os 10 requisitos obrigatórios foram cumpridos:

Qualidade e organização do código: O código está estruturado em packages bem definidos (models, database, adapters, fragments, enums, utils). Cada classe tem uma responsabilidade clara.

Validação de dados: Implementámos validação de códigos únicos para disciplinas, validação de campos obrigatórios, validação de formato de email e tamanho mínimo de senha.

Internacionalização EN e PT: Todas as strings estão externalizadas em values/strings.xml (Inglês) e values-pt/strings.xml (Português). Nenhuma string está hardcoded no código.

Layouts adequados: Utilizamos ConstraintLayout, LinearLayout, GridLayout e RecyclerView. No total foram criados 17 layouts XML diferentes.

Tema personalizado: Criámos themes.xml e values-night/themes.xml com uma paleta de 43 cores. A app tem ícone próprio customizado.

Splash Screen: A SplashActivity tem animações fade-in e slide-up implementadas tanto em XML como em código.

5+ Activities: Implementámos 8 Activities diferentes: SplashActivity, LoginActivity, RegistroActivity, MainActivity, AdicionarEditarDisciplinaActivity, AdicionarEditarTarefaActivity, DetalhesDisciplinaActivity e ConfiguracoesActivity.

Menus de navegação: Usamos Bottom Navigation Bar com 5 tabs, menus de contexto em várias telas e menu principal com overflow.

Persistência SQLite: Implementámos Room Database 2.6.1 com 4 tabelas (usuarios, disciplinas, tarefas, sessoes_estudo) e 4 DAOs correspondentes.

Internacionalização completa: A BaseActivity sobrescreve attachBaseContext() para aplicar o Locale dinamicamente conforme a preferência do utilizador.

5.2 Funcionalidades Extra (Bónus)

Login e Registo de Utilizadores: Implementámos um sistema completo de autenticação que inclui:
  - Hash de passwords usando SHA-256 através do PasswordHelper
  - Validação rigorosa de email e passwords
  - Persistência de sessão via SharedPreferences
  - Verificação de email único durante o registo
  - Logout com diálogo de confirmação

Justificativa: Esta funcionalidade foi implementada para permitir que múltiplos utilizadores possam usar a app no mesmo dispositivo mantendo os dados isolados. Cada utilizador vê apenas as suas próprias disciplinas e tarefas.

5.3 Componentes Avançados Utilizados

RecyclerView com Adapters customizados:
Criámos 4 adapters diferentes: DisciplinaAdapter para a lista de disciplinas com cores, TarefaAdapter para a lista de tarefas com prioridades, CalendarioAdapter para o grid 7x7 do calendário e EstatisticaAdapter para os cards de estatísticas.

Bibliotecas:
Todas as bibliotecas são oficiais do Android: Room Database 2.6.1, Material Components 1.12.0, AndroidX AppCompat 1.7.0 e ConstraintLayout 2.2.0.

Padrões de Design:
Utilizámos vários padrões profissionais: Singleton para o AppDatabase, DAO (Data Access Object) com 4 DAOs diferentes, ViewHolder para performance dos RecyclerViews, Observer/Listener para comunicação entre componentes e BaseActivity para reutilização de código.

Room Database Avançado:
Implementámos Foreign Keys com CASCADE DELETE, queries complexas usando JOINs e GROUP BY, TypeConverters para Enums e Dates, e índices automáticos nas chaves estrangeiras para melhor performance.

Animações:
Criámos animações de Fade-in e Slide-up no Splash Screen usando tanto XML como código, transições suaves entre Activities e ripple effects nos botões seguindo o Material Design.

================================================================================

6. Arquitetura e Estrutura

6.1 Camadas da Aplicação

A aplicação segue uma arquitetura em camadas bem definida:

UI Layer (Activities/Fragments): Esta é a camada de apresentação que interage diretamente com o utilizador.

Adapters (RecyclerView): Camada intermediária que faz a ponte entre os dados e a interface visual das listas.

Business Logic: Camada onde estão as regras de negócio da aplicação, validações e processamento de dados.

DAOs (Data Access): Camada de acesso a dados que abstrai as queries SQL do resto da aplicação.

Database (SQLite): Camada de persistência onde os dados são armazenados fisicamente.

6.2 Modelo de Dados

Entidades Principais:

Disciplina: Contém id, nome, codigo, cor e data_criacao. Representa uma disciplina académica.

Tarefa: Contém id, titulo, descricao, disciplina_id, data_entrega, prioridade e estado. Representa um trabalho ou exercício a fazer.

SessaoEstudo: Contém id, disciplina_id, duracao e data. Representa um período de estudo registado pelo timer.

Usuario: Contém id, nome, email, senha_hash e data_criacao. Representa um utilizador da aplicação.

Relacionamentos:

Uma disciplina pode ter muitas tarefas (relação 1:N). Uma disciplina pode ter muitas sessões de estudo (relação 1:N). A eliminação é em cascata, ou seja, eliminar uma disciplina elimina automaticamente todas as suas tarefas e sessões.

6.3 Navegação

O fluxo de navegação da aplicação é o seguinte:

Ao iniciar, a SplashActivity aparece durante 3 segundos. Depois redireciona para LoginActivity ou MainActivity conforme o estado de autenticação.

A MainActivity contém a BottomNavigation com 5 fragments: HomeFragment (dashboard inicial), SubjectsFragment (que pode levar a DetalhesDisciplinaActivity), TasksFragment, StatisticsFragment e TimerFragment.

Os formulários estão em Activities separadas: AdicionarEditarDisciplinaActivity e AdicionarEditarTarefaActivity.

================================================================================

7. Interface e Experiência do Utilizador

7.1 Princípios de Design Seguidos

Material Design: Usamos componentes padrão do Google para garantir familiaridade.

Consistência visual: As mesmas cores e estilos são usados em toda a app para criar coesão.

Feedback imediato: Animações e confirmações de acções dão feedback ao utilizador.

Hierarquia clara: A informação mais importante está sempre em destaque.

Cores com significado: As prioridades e estados usam cores intuitivas que o utilizador já conhece (vermelho para urgente, verde para concluído, etc).

7.2 Paleta de Cores

Primária: Azul (#2196F3) - Transmite confiança e produtividade.

Secundária: Roxo (#9C27B0) - Representa criatividade.

Accent: Laranja (#FF9800) - Transmite energia e foco.

Gradientes: Usados no splash screen para dar um toque moderno.

Disciplinas: Mais de 40 cores disponíveis para os utilizadores personalizarem as suas disciplinas.

7.3 Ícones

Criámos um logo próprio "StudyFlow" em formato XML vector. Usamos ícones do Material Design para toda a navegação. O ícone da app foi customizado para dar identidade própria.

================================================================================

8. Sensores e Interações (Não Implementados)

Devido ao foco nas funcionalidades core da aplicação, decidimos não implementar features com sensores nesta primeira versão.

Possíveis melhorias futuras que usariam sensores:
  - Shake para pausar ou retomar o timer
  - Notificações para lembretes de tarefas próximas do prazo
  - Widget para a home screen do telemóvel
  - Temas claro/escuro automático baseado no sensor de luz ambiente

================================================================================

9. Mockups e Wireframes

Tela 1: Splash Screen

A primeira tela que o utilizador vê ao abrir a app. Tem um fundo com gradiente de azul para roxo. O logo SVG aparece no centro com animação fade-in. O texto "StudyFlow" aparece em baixo com animação slide-up. A tela toda dura cerca de 2-3 segundos antes de redirecionar.

Tela 2: Dashboard (Home)

A tela principal tem três secções. No topo aparecem 3 cards pequenos lado a lado mostrando o número de disciplinas (ex: 5), tarefas pendentes (ex: 12) e tempo estudado hoje (ex: 45min). Abaixo há uma secção com o título "Tempo por Disciplina" listando cada disciplina com o tempo estudado hoje (ex: "Mat: 45min", "Fis: 30min"). Na parte inferior está o calendário visual mostrando o mês atual (ex: "Dezembro 2025") com setas para navegar. O grid 7x7 mostra os dias da semana (S T Q Q S S D) e todos os dias do mês com cores. A bottom navigation bar está sempre visível.

Tela 3: Lista de Disciplinas

Mostra o título "Disciplinas" no topo. Cada disciplina aparece num card retangular com uma barra colorida vertical à esquerda. O nome da disciplina aparece em texto grande (ex: "Matemática") e o código em baixo em texto menor (ex: "MAT101"). No canto inferior direito está o botão flutuante circular "+" para adicionar novas disciplinas.

Tela 4: Timer Pomodoro

O topo mostra "Timer Estudo". Logo abaixo há um dropdown para selecionar a disciplina (ex: "Matemática"). No centro da tela aparece o tempo em números grandes (ex: "25:00") e logo abaixo a indicação "(TRABALHO)" ou "(DESCANSO)". Um botão grande no centro diz "INICIAR" (ou "PAUSAR" quando ativo). Na parte inferior aparecem os tempos configurados: "Trabalho: 25min" e "Descanso: 5min".

Tela 5: Estatísticas

O título "Estatísticas" aparece no topo. Abaixo há dois gráficos. O primeiro é um gráfico de barras mostrando o tempo de estudo por disciplina com barras de diferentes alturas. As iniciais das disciplinas aparecem em baixo (Mat, Fis, Qui, etc). O segundo é um gráfico de linha mostrando a evolução ao longo da semana. Os dias da semana (S T Q Q S S D) aparecem no eixo horizontal.

================================================================================

10. Testes e Validação

10.1 Testes Realizados

Testes Funcionais:
Testámos todas as operações CRUD (Create, Read, Update, Delete) para disciplinas e tarefas. Verificámos que o timer conta corretamente e que o salvamento de sessões funciona. Confirmámos que as estatísticas mostram dados correctos. Testámos que o calendário mostra tarefas nos dias certos. Verificámos que os filtros de tarefas funcionam como esperado. Testámos a mudança de idioma em tempo real.

Validações:
Confirmámos que o código de disciplina é único e não permite duplicados. Testámos que campos obrigatórios são validados e mostram erro. Verificámos que apenas datas válidas são aceites. Confirmámos que o timer só inicia quando uma disciplina está escolhida.

Performance:
Testámos listas com mais de 50 itens e confirmámos que o scroll é fluído graças ao RecyclerView. As queries são rápidas devido aos índices criados no banco. Confirmámos que não há memory leaks devido ao uso correcto do padrão Singleton.

10.2 Dispositivos Testados

Testámos a aplicação em duas plataformas: emulador do Android Studio com API 24 e API 36, e num dispositivo físico com Android 12 ou superior. A app funcionou correctamente em todas as plataformas testadas.

================================================================================

11. Desafios e Soluções

Desafio 1: Calendário com múltiplas cores

Problema: Era necessário mostrar tarefas de várias disciplinas diferentes num só dia sem ficar confuso ou ocupar muito espaço.

Solução: Implementámos um sistema de bolinhas coloridas com um máximo de 3 cores visíveis. Se houver mais de 3 disciplinas num dia, mostramos as 3 primeiras e um contador com o total de tarefas.

Desafio 2: Performance em listas grandes

Problema: Ao fazer scroll em listas com muitas tarefas, a app ficava lenta e com lag.

Solução: Implementámos o padrão ViewHolder correctamente em todos os adapters e optimizámos as queries do banco de dados usando índices nas foreign keys.

Desafio 3: Timer em background

Problema: Manter o timer a funcionar quando a app é minimizada ou quando o utilizador muda de aba.

Solução: Usamos o CountDownTimer do Android e implementámos salvamento parcial ao parar. Quando o utilizador volta à aba do timer, o estado é restaurado usando variáveis estáticas.

Desafio 4: Sincronização de dados

Problema: Actualizar as estatísticas automaticamente quando o utilizador muda uma tarefa ou completa uma sessão do timer.

Solução: Implementámos callbacks e refresh automático ao voltar para os fragments. Usamos onResume() para recarregar dados quando necessário.

================================================================================

12. Conclusão

O StudyFlow cumpre todos os requisitos obrigatórios do projecto e implementa funcionalidades únicas como o calendário visual híbrido e o timer integrado com estatísticas automáticas.

A aplicação está funcional, testada e pronta a usar, oferecendo uma solução completa para estudantes organizarem os seus estudos de forma eficiente.

Pontos Fortes

O código está bem organizado e comentado em português para facilitar a compreensão. A interface é intuitiva e visual, não requer manual extenso para usar. As funcionalidades são inovadoras, especialmente o calendário híbrido que não existe em outras apps. A performance está optimizada para funcionar bem mesmo com muitos dados. Há cobertura completa do ciclo de estudo: criar tarefas, estudar com timer e ver estatísticas.

Melhorias Futuras

Para versões futuras da aplicação, seria interessante adicionar notificações push para lembrar de tarefas próximas do prazo. Também seria útil ter export de dados para PDF ou CSV. Um sistema de backup na cloud garantiria que os dados não se perdem. A implementação de modo escuro completo melhoraria a experiência à noite. Um widget para a home screen do telemóvel daria acesso rápido às informações. Por fim, integração com o calendário do sistema permitiria sincronizar com outras apps.

================================================================================

Data de Entrega: 12 Janeiro 2026
Programação de Aplicações Móveis - IPS Setúbal
