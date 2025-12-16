# Relatório do Projeto - StudyFlow

**Programação de Aplicações Móveis - 2025/26**
**Instituto Politécnico de Setúbal**

---

## 1. Identificação do Projeto

**Nome da Aplicação**: StudyFlow

**Slogan**: "Organiza. Estuda. Conquista."

**Tipo**: Aplicação móvel Android para gestão académica

**Tema**: Produtividade e Educação

---

## 2. Descrição da Aplicação

### 2.1 Objetivo Central

O StudyFlow é uma aplicação Android desenvolvida para ajudar estudantes a gerir as suas disciplinas, tarefas académicas e tempo de estudo de forma integrada e visual.

A app resolve três problemas principais dos estudantes:
1. **Desorganização de tarefas** - Esquecem-se de trabalhos e prazos
2. **Falta de controlo de tempo** - Não sabem quanto tempo estudam cada disciplina
3. **Falta de visão global** - Não têm noção da carga de trabalho semanal/mensal

### 2.2 Público-Alvo

Estudantes do ensino superior e secundário que precisam de:
- Organizar várias disciplinas simultaneamente
- Gerir múltiplas tarefas com diferentes prazos
- Acompanhar o tempo dedicado a cada disciplina
- Ter uma visão geral do mês de estudo

---

## 3. Funcionalidades Implementadas

### 3.1 Gestão de Disciplinas
- Adicionar, editar e eliminar disciplinas
- Código único para cada disciplina
- Cor personalizada para identificação visual rápida
- Visualização de estatísticas por disciplina (tarefas, tempo de estudo)

### 3.2 Gestão de Tarefas
- Criar tarefas associadas a disciplinas
- Definir data de entrega, prioridade (Baixa/Média/Alta) e estado (Pendente/Em Progresso/Concluída)
- Filtrar tarefas por estado
- Marcar rapidamente como concluída através de checkbox
- Código de cores para prioridades

### 3.3 Calendário Visual Híbrido (FUNCIONALIDADE DESTAQUE)
- Calendário mensal em formato grid 7x7
- Cada dia mostra até 3 cores de disciplinas com tarefas
- Indicadores visuais: bolinhas coloridas + contador de tarefas
- Navegação entre meses
- Click no dia mostra lista de tarefas desse dia
- Dia actual destacado

**Diferencial**: Sistema único que combina calendário tradicional com código de cores por disciplina.

### 3.4 Timer Pomodoro Inteligente
- Cronómetro com técnica Pomodoro (trabalho + descanso)
- Tempos configuráveis pelo utilizador
- Selecção obrigatória de disciplina para rastreamento
- Salvamento automático de sessões no banco de dados
- Salvamento parcial quando parado manualmente
- Alternância automática entre ciclos de trabalho e descanso

### 3.5 Estatísticas Visuais
- **Lista de Estatísticas**: Distribuição de tempo de estudo por disciplina (últimos 7 dias)
  - Cards coloridos com cores dinâmicas de cada disciplina
  - Tempo formatado automaticamente (minutos ou horas)
  - Layout limpo e fácil de interpretar

### 3.6 Dashboard Completo
- Cards com métricas principais: total disciplinas, tarefas pendentes, tempo estudado hoje
- Tempo de estudo detalhado por disciplina (hoje)
- Calendário visual integrado
- Navegação rápida para outras secções

### 3.7 Navegação e Interface
- Bottom Navigation Bar com 5 abas principais
- Splash Screen animado na inicialização
- Design Material com tema personalizado
- Menus de contexto para acções secundárias
- FloatingActionButtons para adição rápida

### 3.8 Internacionalização
- Suporte total para Português (PT) e Inglês (EN)
- Detecção automática do idioma do sistema
- Todas as strings externalizadas

---

## 4. Análise de Mercado

### 4.1 Aplicações Similares

| App | Funcionalidades | Limitações |
|-----|----------------|------------|
| **Google Tasks** | Gestão de tarefas, integração Google | Sem timer, sem estatísticas, sem cores por categoria |
| **Microsoft To Do** | Tarefas, listas, lembretes | Sem timer de estudo, sem calendário visual |
| **Todoist** | Tarefas avançadas, prioridades | Sem timer integrado, sem foco em estudo |
| **Forest** | Timer de estudo gamificado | Sem gestão de tarefas, sem calendário |
| **MyStudyLife** | Horários, tarefas, exames | Calendário tradicional, sem timer, interface complexa |

### 4.2 Diferenciais do StudyFlow

1. **Tudo-em-um**: Combina tarefas + timer + estatísticas numa única app
2. **Calendário visual híbrido**: Sistema único com código de cores e contadores
3. **Simplicidade**: Interface limpa e directa, sem features desnecessárias
4. **Foco em estudos**: Desenhado especificamente para ambiente académico
5. **Offline-first**: Funciona totalmente sem internet, dados locais

---

## 5. Requisitos Técnicos Implementados

### 5.1 Requisitos Obrigatórios ✓

- [x] Qualidade e organização do código
- [x] Validação de dados (códigos únicos, campos obrigatórios)
- [x] Internacionalização EN e PT
- [x] Layouts adequados (ConstraintLayout, RecyclerView, GridLayout)
- [x] Tema personalizado com paleta de 43 cores
- [x] Ícone e logo próprios
- [x] Splash Screen com animações
- [x] 5 Activities implementadas
- [x] Bottom Navigation Bar + Menus de contexto
- [x] Persistência SQLite com 3 tabelas relacionadas
- [x] Suporte multi-idioma

### 5.2 Componentes Avançados Utilizados

**RecyclerView com Adapters customizados**:
- DisciplinaAdapter
- TarefaAdapter
- CalendarioAdapter (grid 7x7)
- EstatisticaAdapter (lista de estatísticas)

**Bibliotecas**:
- Apenas bibliotecas nativas do Android SDK
- Sem dependências externas

**Padrões de Design**:
- Singleton (DatabaseHelper)
- DAO (Data Access Object)
- ViewHolder (performance de listas)
- Callback/Listener (comunicação entre componentes)

**SQLite Avançado**:
- Foreign Keys com CASCADE DELETE
- Queries com JOINs e GROUP BY
- Índices para performance

**Animações**:
- Fade-in e Slide-up no Splash Screen
- Transições entre Activities

---

## 6. Arquitetura e Estrutura

### 6.1 Camadas da Aplicação

```
┌─────────────────────────────────┐
│   UI Layer (Activities/Fragments)│
├─────────────────────────────────┤
│   Adapters (RecyclerView)       │
├─────────────────────────────────┤
│   Business Logic                │
├─────────────────────────────────┤
│   DAOs (Data Access)            │
├─────────────────────────────────┤
│   Database (SQLite)             │
└─────────────────────────────────┘
```

### 6.2 Modelo de Dados

**Entidades Principais**:
- Disciplina (id, nome, codigo, cor, data_criacao)
- Tarefa (id, titulo, descricao, disciplina_id, data_entrega, prioridade, estado)
- SessaoEstudo (id, disciplina_id, duracao, data)

**Relacionamentos**:
- Uma disciplina tem muitas tarefas (1:N)
- Uma disciplina tem muitas sessões de estudo (1:N)
- Eliminação em cascata

### 6.3 Navegação

```
SplashActivity (3s)
    ↓
MainActivity (BottomNavigation)
    ├── HomeFragment
    ├── SubjectsFragment → DetalhesDisciplinaActivity
    ├── TasksFragment
    ├── StatisticsFragment
    └── TimerFragment

Formulários:
├── AdicionarEditarDisciplinaActivity
└── AdicionarEditarTarefaActivity
```

---

## 7. Interface e Experiência do Utilizador

### 7.1 Princípios de Design Seguidos

- **Material Design**: Componentes padrão Google
- **Consistência visual**: Mesmas cores e estilos em toda a app
- **Feedback imediato**: Animações e confirmações de acções
- **Hierarquia clara**: Informação mais importante em destaque
- **Cores com significado**: Prioridades e estados com cores intuitivas

### 7.2 Paleta de Cores

- **Primária**: Azul (#2196F3) - Confiança e produtividade
- **Secundária**: Roxo (#9C27B0) - Criatividade
- **Accent**: Laranja (#FF9800) - Energia
- **Gradientes**: Usados no splash para modernidade
- **Disciplinas**: 40+ cores disponíveis para personalização

### 7.3 Ícones

- Logo próprio "StudyFlow" (formato XML vector)
- Ícones Material Design para navegação
- Ícone da app customizado

---

## 8. Sensores e Interações (Não Implementados)

Devido ao foco nas funcionalidades core, não foram implementadas features com sensores.

**Possíveis melhorias futuras**:
- Shake para pausar/retomar timer
- Notificações para lembretes de tarefas
- Widget para home screen
- Temas claro/escuro automático por sensor de luz

---

## 9. Mockups e Wireframes

### Tela 1: Splash Screen
```
┌──────────────────┐
│                  │
│   [GRADIENTE]    │
│                  │
│    [LOGO SVG]    │
│                  │
│   "StudyFlow"    │
│                  │
└──────────────────┘
Animação: Fade-in (logo) + Slide-up (texto)
```

### Tela 2: Dashboard (Home)
```
┌──────────────────┐
│ Cards Estatísticas│
│ ┌──┐ ┌──┐ ┌──┐  │
│ │ 5│ │12│ │45│  │
│ └──┘ └──┘ └──┘  │
│ Disc Tare Tempo  │
├──────────────────┤
│ Tempo por Discipl │
│ Mat: 45min       │
│ Fis: 30min       │
├──────────────────┤
│   CALENDÁRIO     │
│ S T Q Q S S D    │
│ [Grid 7x7 cores] │
│ < Dezembro 2025 >│
└──────────────────┘
  [Navigation Bar]
```

### Tela 3: Lista de Disciplinas
```
┌──────────────────┐
│  Disciplinas     │
├──────────────────┤
│ │ Matemática     │
│ │ MAT101         │
├──────────────────┤
│ │ Física         │
│ │ FIS201         │
├──────────────────┤
│                  │
│        [+]       │
└──────────────────┘
Barra colorida à esquerda
```

### Tela 4: Timer Pomodoro
```
┌──────────────────┐
│  Timer Estudo    │
├──────────────────┤
│ Disciplina:      │
│ [Matemática ▼]   │
├──────────────────┤
│                  │
│      25:00       │
│    (TRABALHO)    │
│                  │
│   [INICIAR]      │
├──────────────────┤
│ Trabalho: 25min  │
│ Descanso: 5min   │
└──────────────────┘
```

### Tela 5: Estatísticas
```
┌──────────────────┐
│  Estatísticas    │
├──────────────────┤
│  [GRÁFICO BARRAS]│
│   ▃ ▅ ▂ ▄ ▁     │
│  Mat Fis Qui... │
├──────────────────┤
│ [GRÁFICO LINHA]  │
│      ╱╲╱╲        │
│   ─────────────  │
│   S T Q Q S S D  │
└──────────────────┘
```

---

## 10. Testes e Validação

### 10.1 Testes Realizados

**Funcionais**:
- ✓ Adicionar/editar/eliminar disciplinas
- ✓ Adicionar/editar/eliminar tarefas
- ✓ Timer conta correctamente
- ✓ Salvamento de sessões funciona
- ✓ Estatísticas mostram dados correctos
- ✓ Calendário mostra tarefas nos dias certos
- ✓ Filtros de tarefas funcionam
- ✓ Mudança de idioma funciona

**Validações**:
- ✓ Código de disciplina único
- ✓ Campos obrigatórios validados
- ✓ Datas válidas
- ✓ Timer só inicia com disciplina escolhida

**Performance**:
- ✓ Listas com mais de 50 itens fluídas (RecyclerView)
- ✓ Queries rápidas (índices no banco)
- ✓ Sem memory leaks (Singleton correcto)

### 10.2 Dispositivos Testados

- Emulador Android Studio (API 24 e 36)
- Dispositivo físico Android 12+

---

## 11. Desafios e Soluções

### Desafio 1: Calendário com múltiplas cores
**Problema**: Mostrar tarefas de várias disciplinas num só dia
**Solução**: Sistema de bolinhas coloridas (máx 3) + contador total

### Desafio 2: Performance em listas grandes
**Problema**: Lag ao scroll com muitas tarefas
**Solução**: ViewHolder pattern e queries optimizadas

### Desafio 3: Timer em background
**Problema**: Manter timer quando app minimizada
**Solução**: CountDownTimer + salvamento parcial ao parar

### Desafio 4: Sincronização de dados
**Problema**: Actualizar estatísticas quando muda tarefa/timer
**Solução**: Callbacks e refresh ao voltar para fragment

---

## 12. Conclusão

O StudyFlow cumpre todos os requisitos obrigatórios do projecto e implementa funcionalidades únicas como o calendário visual híbrido e timer integrado com estatísticas.

A aplicação está funcional, testada e pronta a usar, oferecendo uma solução completa para estudantes organizarem os seus estudos.

### Pontos Fortes
- Código bem organizado e comentado
- Interface intuitiva e visual
- Funcionalidades inovadoras (calendário híbrido)
- Performance optimizada
- Cobertura completa do ciclo de estudo (tarefas → tempo → estatísticas)

### Melhorias Futuras
- Notificações push para lembretes
- Export de dados para PDF/CSV
- Backup na cloud
- Modo escuro
- Widget para home screen
- Integração com calendário do sistema

---

**Data de Entrega**: 12 Janeiro 2026
**Programação de Aplicações Móveis - IPS Setúbal**
