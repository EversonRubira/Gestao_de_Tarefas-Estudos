# Manual de Utilizador - StudyFlow

Guia passo a passo de como usar a aplicação.

## Índice
1. Primeira Utilização
2. Gerir Disciplinas
3. Gerir Tarefas
4. Usar o Timer de Estudo
5. Ver Estatísticas
6. Usar o Calendário
7. Navegar na App

---

## 1. Primeira Utilização

### Passo 1: Abrir a App
- Tocar no ícone do StudyFlow no telemóvel
- Aparece uma tela inicial (Splash Screen) com animação
- Aguardar 3 segundos

![Splash Screen - mostra logo e gradiente azul/roxo]

### Passo 2: Tela Principal
- Após o splash, abre automaticamente na aba "Home" (Dashboard)
- Na primeira vez, a app cria 3 disciplinas de exemplo e algumas tarefas
- Estes dados são só para demonstração, podem ser apagados depois

![Dashboard inicial com dados de exemplo]

---

## 2. Gerir Disciplinas

### 2.1 Ver Lista de Disciplinas

**Passo 1**: Tocar na aba "Subjects" (segundo ícone da navegação inferior)

**Passo 2**: Aparecem todas as disciplinas em lista, cada uma mostra:
- Nome da disciplina
- Código da disciplina
- Cor associada (barra colorida à esquerda)

![Lista de disciplinas]

### 2.2 Adicionar Nova Disciplina

**Passo 1**: Na aba "Subjects", tocar no botão redondo azul com símbolo "+"

**Passo 2**: Preencher o formulário:
- **Nome**: Escrever nome da disciplina (ex: "Matemática")
- **Código**: Código único da disciplina (ex: "MAT101")
- **Cor**: Tocar no selector de cor e escolher uma cor

**Passo 3**: Tocar em "GUARDAR"

**Nota**: Se o código já existir, aparece erro e não deixa guardar.

![Formulário de adicionar disciplina]

### 2.3 Ver Detalhes de uma Disciplina

**Passo 1**: Na lista de disciplinas, tocar numa disciplina

**Passo 2**: Abre tela com:
- Nome e código no topo (com a cor)
- Estatísticas: Total tarefas, Pendentes, Concluídas, Tempo de estudo
- Lista de todas as tarefas dessa disciplina

![Detalhes da disciplina]

### 2.4 Editar Disciplina

**Passo 1**: Abrir detalhes da disciplina

**Passo 2**: Tocar nos 3 pontinhos no canto superior direito

**Passo 3**: Escolher "Editar"

**Passo 4**: Alterar os dados necessários

**Passo 5**: Guardar

### 2.5 Apagar Disciplina

**Passo 1**: Abrir detalhes da disciplina

**Passo 2**: Tocar nos 3 pontinhos no canto superior direito

**Passo 3**: Escolher "Eliminar"

**Passo 4**: Confirmar na mensagem que aparece

**ATENÇÃO**: Apagar uma disciplina apaga TODAS as tarefas e sessões de estudo associadas!

---

## 3. Gerir Tarefas

### 3.1 Ver Lista de Tarefas

**Passo 1**: Tocar na aba "Tasks" (terceiro ícone)

**Passo 2**: Aparecem todas as tarefas em lista com:
- Título da tarefa
- Nome da disciplina (com cor)
- Data de entrega
- Indicador de prioridade (barra colorida: verde=baixa, laranja=média, vermelho=alta)
- Estado atual
- Checkbox para marcar como concluída

![Lista de tarefas]

### 3.2 Filtrar Tarefas

**Passo 1**: Na aba Tasks, tocar no menu dropdown no topo

**Passo 2**: Escolher filtro:
- Todas
- Pendentes
- Em Progresso
- Concluídas

### 3.3 Adicionar Nova Tarefa

**Passo 1**: Na aba "Tasks", tocar no botão "+" azul

**Passo 2**: Preencher formulário:
- **Disciplina**: Escolher de uma lista (obrigatório)
- **Título**: Nome da tarefa (obrigatório)
- **Descrição**: Detalhes da tarefa (opcional)
- **Data de Entrega**: Tocar no calendário e escolher data
- **Prioridade**: Escolher Baixa, Média ou Alta
- **Estado**: Escolher Pendente, Em Progresso ou Concluída

**Passo 3**: Tocar em "GUARDAR"

![Formulário de adicionar tarefa]

### 3.4 Marcar Tarefa como Concluída (Forma Rápida)

**Passo 1**: Na lista de tarefas, tocar no checkbox à esquerda da tarefa

**Passo 2**: A tarefa fica automaticamente marcada como Concluída

### 3.5 Editar Tarefa

**Passo 1**: Tocar na tarefa da lista

**Passo 2**: Abre o formulário com dados preenchidos

**Passo 3**: Alterar o que for necessário

**Passo 4**: Guardar

### 3.6 Apagar Tarefa

**Passo 1**: Tocar na tarefa para editar

**Passo 2**: Tocar em botão "ELIMINAR" (se tiver)

OU usar menu de contexto se disponível

---

## 4. Usar o Timer de Estudo (Pomodoro)

### 4.1 Iniciar uma Sessão de Estudo

**Passo 1**: Ir para a aba "Timer" (quinto ícone - relógio)

**Passo 2**: Escolher a disciplina no dropdown (OBRIGATÓRIO)

**Passo 3**: (Opcional) Configurar tempos:
- Tempo de Trabalho: Quantos minutos de estudo (padrão: 25 min)
- Tempo de Descanso: Quantos minutos de pausa (padrão: 5 min)

**Passo 4**: Tocar no botão "INICIAR"

**Passo 5**: O cronómetro começa a contagem regressiva

![Timer em funcionamento]

### 4.2 Durante a Sessão

- O timer vai contando o tempo
- Mostra se está em modo "Trabalho" ou "Descanso"
- Quando termina o tempo de trabalho, passa automaticamente para descanso
- Quando termina o descanso, pode começar novo ciclo

### 4.3 Parar o Timer

**Passo 1**: Tocar em "PARAR"

**Passo 2**: O tempo parcial é guardado automaticamente no banco de dados

### 4.4 Ver Tempo Guardado

O tempo de cada sessão é guardado e aparece:
- No Dashboard (Home) - tempo de hoje por disciplina
- Nas Estatísticas - lista dos últimos 7 dias
- Nos Detalhes da Disciplina - tempo total estudado

---

## 5. Ver Estatísticas

### 5.1 Aceder às Estatísticas

**Passo 1**: Tocar na aba "Statistics" (quarto ícone)

**Passo 2**: Aparece uma lista com cards de cada disciplina:

![Ecrã de estatísticas com lista]

### 5.2 Lista de Estatísticas

- **O que mostra**: Tempo de estudo por disciplina nos últimos 7 dias
- **Como ler**: Cada card mostra o nome da disciplina e o tempo estudado
- **Cores**: Barra lateral de cada card tem a cor da disciplina
- **Formato**: Tempo aparece em minutos (ex: "120 min") ou horas (ex: "2.5 h")

### 5.3 Sem Dados

Se ainda não usou o timer, aparece mensagem:
"Sem dados de estudo disponíveis. Comece a usar o timer!"

---

## 6. Usar o Calendário

O calendário está na tela Home (Dashboard).

### 6.1 Visualizar o Calendário

**Passo 1**: Ir para aba "Home"

**Passo 2**: Fazer scroll para baixo até ver o calendário

**Passo 3**: O calendário mostra:
- Grade 7x7 com os dias do mês actual
- Cada quadrado é um dia
- Dias com tarefas têm bolinhas coloridas
- Número em cima das bolinhas = quantidade de tarefas

![Calendário visual com cores]

### 6.2 Entender as Cores

- Cada bolinha colorida representa uma disciplina que tem tarefas nesse dia
- As cores são as mesmas das disciplinas
- Máximo 3 cores por dia (se houver mais, só mostra as 3 primeiras)
- Número mostra total de tarefas (mesmo que de mais de 3 disciplinas)

### 6.3 Ver Tarefas de um Dia

**Passo 1**: Tocar num dia que tenha bolinhas coloridas

**Passo 2**: Aparece mensagem (Toast) com lista das tarefas desse dia

Exemplo: "Matemática: Fazer exercícios..."

### 6.4 Navegar Entre Meses

**Passo 1**: Usar seta esquerda "<" para mês anterior

**Passo 2**: Usar seta direita ">" para próximo mês

**Passo 3**: Nome do mês actual aparece no centro

### 6.5 Dia Actual

O dia de hoje tem borda especial para identificar facilmente.

---

## 7. Navegar na App

### 7.1 Bottom Navigation (Barra Inferior)

A app tem 5 abas na parte de baixo:

1. **Home** (ícone casa): Dashboard com resumo geral
2. **Subjects** (ícone livro): Lista de disciplinas
3. **Tasks** (ícone checklist): Lista de tarefas
4. **Statistics** (ícone gráfico): Estatísticas de estudo
5. **Timer** (ícone relógio): Cronómetro Pomodoro

**Como usar**: Tocar no ícone para ir para essa secção

### 7.2 Voltar Atrás

- Usar botão "Voltar" do Android para fechar formulários ou voltar à tela anterior
- Se estiver numa aba principal, voltar fecha a app

### 7.3 Menus de Contexto

Algumas telas têm menu de 3 pontinhos (⋮) no canto superior:
- Detalhes da Disciplina: Editar ou Eliminar

---

## Dicas de Utilização

### Para Organização
- Usar cores diferentes para cada disciplina facilita identificação
- Marcar prioridades nas tarefas ajuda a saber o que fazer primeiro
- Consultar o calendário diariamente para ver carga de trabalho

### Para Estudar Melhor
- Usar o Timer Pomodoro: 25 min estudo + 5 min pausa
- Escolher sempre a disciplina no timer para ter estatísticas correctas
- Ver as estatísticas semanalmente para acompanhar progresso

### Para Manter Actualizado
- Marcar tarefas como concluídas assim que terminar
- Atualizar estados das tarefas (Pendente → Em Progresso → Concluída)
- Apagar disciplinas/tarefas antigas que já não são necessárias

---

## Resolução de Problemas

### A app não abre
- Verificar se o Android é versão 7.0 ou superior
- Reinstalar a aplicação

### Dados desapareceram
- Os dados são guardados localmente no telemóvel
- Se limpar dados da app nas configurações do Android, perde tudo
- Não há backup automático

### Estatísticas não aparecem
- Normal se ainda não usou o timer
- Precisa ter pelo menos uma sessão de estudo guardada nos últimos 7 dias

### Calendário sem cores
- Normal se não tiver tarefas criadas
- Criar tarefas com datas de entrega para aparecerem no calendário

### Timer não guarda tempo
- Verificar se escolheu a disciplina antes de iniciar
- O timer só guarda se disciplina estiver seleccionada

---

## Alterar Idioma

A app detecta automaticamente o idioma do sistema:
- Se Android em Português → App em PT
- Se Android em Inglês → App em EN
- Outros idiomas → App em EN (padrão)

**Para mudar**: Alterar idioma nas definições do Android e reiniciar a app

---

## Informações de Suporte

Esta é uma app académica desenvolvida para projeto de PAM.
Para dúvidas ou problemas, contactar os autores do projeto.

**Versão**: 1.0
**Última atualização**: Dezembro 2025
