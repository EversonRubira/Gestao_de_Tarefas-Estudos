StudyFlow - Manual de Utilizador

Programação de Aplicações Móveis 2025/26
Instituto Politécnico de Setúbal

================================================================================

1. Introdução

O StudyFlow é uma aplicação Android para gestão de disciplinas académicas, tarefas e controlo de tempo de estudo usando a técnica Pomodoro.

Funcionalidades:
  - Gestão de disciplinas com cores personalizadas
  - Criação de tarefas com prioridades (Alta, Média, Baixa)
  - Timer Pomodoro (25min trabalho / 5min descanso)
  - Calendário visual com tarefas agendadas
  - Estatísticas de tempo de estudo por disciplina
  - Internacionalização (Português/Inglês)
  - Tema claro e escuro

================================================================================

2. Instalação e Primeiro Acesso

2.1 Requisitos

Para usar a aplicação é necessário ter um dispositivo com Android 7.0 ou superior (API 24) e pelo menos 50 MB de espaço livre.

2.2 Registo de Conta

Passo 1: Abra a aplicação

Ao abrir a app pela primeira vez verá o Splash Screen com o logo da aplicação durante 2 segundos.

Passo 2: Na tela de Login, clique em "Criar Conta"

Se já tiver conta pode fazer login diretamente. Se não, precisa criar uma conta nova clicando no botão de criar conta.

Passo 3: Preencha o formulário de registo

Vai precisar preencher os seguintes campos:
  - Nome (mínimo 3 caracteres)
  - Email (formato válido)
  - Password (mínimo 6 caracteres)
  - Confirmar Password

Todos os campos são obrigatórios e têm validação.

Passo 4: Clique em "Registar"

Depois de preencher tudo corretamente, clique no botão Registar. A conta será criada e você será redirecionado automaticamente para o Dashboard.

Dados de Exemplo: Na primeira execução, a app cria automaticamente 3 Disciplinas (PAM, BD, WEB), 3 Tarefas de exemplo e 2 Sessões de estudo. Pode apagar estes dados e criar os seus próprios.

================================================================================

3. Funcionalidades Principais

3.1 Dashboard (Home)

O Dashboard dá uma visão geral do seu progresso académico. É composto por três partes principais:

a) Resumo Estatístico (3 cards no topo)
   Mostra o total de disciplinas cadastradas, tarefas pendentes e tempo de estudo hoje.

b) Calendário Visual
   O calendário permite navegar entre meses usando as setas. Os dias com pontos coloridos têm tarefas agendadas. As cores representam disciplinas diferentes. Pode clicar num dia para ver as tarefas desse dia específico. O dia atual aparece marcado com uma borda diferente.

c) Tempo de Estudo Hoje
   Lista com o tempo estudado hoje para cada disciplina. Por exemplo:
   PAM - 1h 25m
   BD - 0h 45m

================================================================================

3.2 Disciplinas

Adicionar Disciplina

Para adicionar uma nova disciplina siga estes passos:

Passo 1: Clique no botão flutuante "+" (canto inferior direito)

Passo 2: Preencha os campos do formulário
  - Nome: Por exemplo "Matemática Aplicada"
  - Código: Por exemplo "MAT" (mínimo 2 caracteres, convertido automaticamente para maiúsculas)
  - Cor: Selecione uma das 12 cores disponíveis na paleta

Passo 3: Clique em "Guardar"

A disciplina será criada e aparecerá na lista.

Editar/Eliminar Disciplina

Para editar ou eliminar uma disciplina existente:
  1. Clique no ícone de três pontos da disciplina
  2. Selecione "Editar" ou "Eliminar"

ATENÇÃO: Eliminar uma disciplina remove todas as tarefas associadas a ela! Esta operação não pode ser desfeita.

Ver Detalhes

Pode clicar no card de qualquer disciplina para ver os detalhes completos. Na tela de detalhes pode ver estatísticas (total de tarefas, pendentes, concluídas), tempo total de estudo dedicado à disciplina e a lista completa de tarefas dessa disciplina.

================================================================================

3.3 Tarefas

Adicionar Tarefa

Passo 1: Clique no botão "+"

Passo 2: Preencha os campos do formulário
  - Título: Por exemplo "Trabalho de grupo"
  - Descrição: Campo opcional para detalhes adicionais
  - Disciplina: Selecione da lista de disciplinas cadastradas
  - Data de Entrega: Clique no ícone do calendário para escolher
  - Prioridade: Escolha entre Baixa, Média ou Alta

Passo 3: Clique em "Guardar"

Estados da Tarefa

As tarefas podem ter três estados diferentes:
  - Pendente (cor cinza): A tarefa ainda não foi iniciada
  - Em Progresso (cor azul): A tarefa está em andamento
  - Concluída (cor verde): A tarefa foi finalizada

Para alterar o estado de uma tarefa pode marcar a checkbox ao lado dela para marcar como concluída, ou usar o menu de três pontos para escolher "Marcar como Concluída/Pendente".

Prioridades

As prioridades aparecem com badges coloridos:
  - Alta: Badge vermelho com texto "ALTA"
  - Média: Badge laranja com texto "MÉDIA"
  - Baixa: Badge verde com texto "BAIXA"

================================================================================

3.4 Timer Pomodoro

Técnica Pomodoro

A técnica Pomodoro consiste em três passos simples:
  1. Trabalhar focado durante 25 minutos
  2. Descansar durante 5 minutos
  3. Repetir o ciclo

Como Usar

Passo 1: (Opcional) Configurar durações

Por padrão o timer usa 25 minutos para trabalho e 5 minutos para descanso. Se quiser pode alterar estes valores. Os valores aceites vão de 1 a 120 minutos.

Passo 2: OBRIGATÓRIO - Selecionar disciplina

Tem que escolher uma disciplina antes de iniciar o timer. Clique em "Selecionar Disciplina" e escolha da lista. Isto é necessário para registar o tempo de estudo corretamente.

Passo 3: Clique em "INICIAR"

O cronómetro inicia a contagem regressiva. O botão muda automaticamente para "PAUSAR".

Controles:
  - PAUSAR: Pausa o timer sem perder o progresso
  - CONTINUAR: Retoma de onde parou
  - PARAR: Reseta completamente (mas guarda o tempo se já estudou mais de 10 segundos)

Funcionalidade Especial - Troca de Abas

Se trocar de aba durante o timer, o tempo estudado até aquele momento é guardado automaticamente. Ao voltar para a aba do Timer, pode clicar em "CONTINUAR" para prosseguir de onde parou.

Ciclo Completo

Após completar 25 minutos de trabalho, o tempo é guardado automaticamente e aparece a mensagem "Sessão de trabalho completa! Hora de descansar." O timer muda automaticamente para o modo Descanso de 5 minutos.

Após os 5 minutos de descanso, aparece a mensagem "Descanso terminado! Pronto para outra sessão?" e o timer volta para o modo Trabalho de 25 minutos.

================================================================================

3.5 Estatísticas

A secção de estatísticas mostra o tempo de estudo dos últimos 7 dias.

Período: Últimos 7 dias

Informações por disciplina:
  - Nome da disciplina
  - Cor (barra lateral colorida)
  - Tempo total de estudo (formatado em horas e minutos)

Exemplo de como aparecem as estatísticas:

━━━━━━━━━━━━━━━━━━━━━━
│ PAM
│ 3h 45m
━━━━━━━━━━━━━━━━━━━━━━
│ BD
│ 1h 30m
━━━━━━━━━━━━━━━━━━━━━━

Atualização: As estatísticas são atualizadas automaticamente sempre que completa uma sessão do Timer.

================================================================================

3.6 Configurações

Para aceder às configurações, clique no menu superior (ícone de três pontos) e depois em Configurações.

Idioma

A app suporta dois idiomas:
  - Português
  - English

Como trocar o idioma:
  1. Clique em "Idioma"
  2. Selecione o idioma desejado
  3. A app reinicia automaticamente com o novo idioma

Tema

Pode escolher entre dois temas:
  - Claro (para usar durante o dia)
  - Escuro (para usar à noite ou ambientes escuros)

Como trocar o tema:
  1. Clique em "Tema"
  2. Selecione o tema desejado
  3. A mudança acontece imediatamente

Logout

Para sair da conta:
  1. Abra o Menu e clique em "Logout"
  2. Confirme a ação
  3. Será redirecionado para a tela de Login

================================================================================

4. Resolução de Problemas

Problema: "Não consigo criar tarefas"

Causa: Não tem disciplinas cadastradas

Solução:
  1. Vá para a aba "Disciplinas"
  2. Crie pelo menos uma disciplina
  3. Volte para a aba "Tarefas" e tente novamente

--------------------------------------------------------------------------------

Problema: "Timer não inicia"

Causa: Não selecionou uma disciplina

Solução:
  1. Na aba Timer, clique em "Selecionar Disciplina"
  2. Escolha uma disciplina da lista
  3. Clique em "INICIAR"

--------------------------------------------------------------------------------

Problema: "Calendário sem tarefas"

Verificações a fazer:
  1. As tarefas têm data de entrega definida?
  2. Está a ver o mês correto?
  3. As tarefas estão marcadas como concluídas? (Tarefas concluídas não aparecem no calendário)

--------------------------------------------------------------------------------

Problema: "Estatísticas vazias"

Causa: Não tem sessões de estudo nos últimos 7 dias

Solução:
  - Use o Timer para estudar
  - Complete pelo menos uma sessão de 25 minutos
  - As estatísticas aparecerão automaticamente

================================================================================

5. Dicas de Uso

Boas Práticas

1. Cadastre todas as disciplinas logo no início
   Isto facilita a criação de tarefas depois.

2. Use prioridades corretamente
   - Alta: Para trabalhos urgentes e provas
   - Média: Para trabalhos normais
   - Baixa: Para leituras opcionais e exercícios extra

3. Pomodoro: 25 minutos de foco total, sem distrações
   Durante a sessão de trabalho, desligue notificações e concentre-se apenas na tarefa.

4. Revise as estatísticas semanalmente
   Ajuda a perceber quanto tempo está a dedicar a cada disciplina.

5. Atualize o calendário ao receber novos trabalhos
   Adicione as tarefas assim que souber dos prazos.

================================================================================

6. Atalhos

Ação: Adicionar disciplina
Como Fazer: Botão "+" na aba Disciplinas (canto inferior direito)

Ação: Adicionar tarefa
Como Fazer: Botão "+" na aba Tarefas (canto inferior direito)

Ação: Marcar tarefa como concluída
Como Fazer: Checkbox ao lado da tarefa

Ação: Ver detalhes de uma disciplina
Como Fazer: Toque no card da disciplina

Ação: Ver tarefas de um dia específico
Como Fazer: Toque no dia do calendário

================================================================================

7. Informações Técnicas

Versão: 1.0
Data: Janeiro 2026
SDK Compilação: 36 (Android 15)
SDK Mínimo: 24 (Android 7.0)
Gradle: 8.7
Room Database: 2.6.1

================================================================================

Desenvolvido por: [Seu Nome]
Instituição: IPS - EST Setúbal
Ano Letivo: 2025/26

================================================================================

Fim do Manual
