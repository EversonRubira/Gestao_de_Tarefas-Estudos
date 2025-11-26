# 🎨 Atualizações Planejadas - Gestão de Estudos
**Data:** 26 de Novembro de 2025
**Versão:** 2.0 - Redesign Completo

---

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Design System](#design-system)
3. [Componentes](#componentes)
4. [Layouts](#layouts)
5. [Assets](#assets)
6. [Checklist de Implementação](#checklist-de-implementação)

---

## 🎯 Visão Geral

### Objetivo
Transformar o app em uma experiência visual moderna e imersiva, com:
- **Foto de fundo** em todas as telas (60%+ de visibilidade)
- **Glassmorphism** (efeito vidro fosco) nos cards
- **Cards compactos** e bem espaçados
- **Bottom Navigation** moderno tipo cápsula/pill
- **Logo redesenhado** mais atraente

### Inspiração do Design
- Material Design 3
- Glassmorphism trend
- Apps modernos de produtividade (Notion, Todoist, Forest)

---

## 🎨 Design System

### Nova Paleta de Cores

#### Cores Primárias (harmonizam com foto de estudo)
```xml
<!-- Tons Neutros e Naturais (base para glassmorphism) -->
<color name="primary">#5B7C99</color>           <!-- Azul-acinzentado suave -->
<color name="primary_dark">#3D5A75</color>       <!-- Azul escuro elegante -->
<color name="primary_light">#8FA3B8</color>      <!-- Azul claro suave -->

<!-- Accent - Laranja com gradiente -->
<color name="accent_start">#FFB74D</color>       <!-- Amarelo-laranja -->
<color name="accent">#FF9800</color>             <!-- Laranja médio -->
<color name="accent_end">#F57C00</color>         <!-- Laranja escuro -->

<!-- Background & Surface -->
<color name="background_overlay">#CC000000</color> <!-- 80% preto para overlay -->
<color name="surface_glass">#B3FFFFFF</color>    <!-- Branco 70% (glassmorphism) -->
<color name="surface_glass_dark">#99FFFFFF</color> <!-- Branco 60% -->
```

#### Cores Funcionais
```xml
<!-- Status Colors -->
<color name="success">#66BB6A</color>            <!-- Verde suave -->
<color name="error">#EF5350</color>              <!-- Vermelho suave -->
<color name="warning">#FFA726</color>            <!-- Laranja aviso -->
<color name="info">#42A5F5</color>               <!-- Azul info -->

<!-- Text Colors (para glassmorphism) -->
<color name="text_on_glass">#1A1A1A</color>      <!-- Quase preto -->
<color name="text_secondary_glass">#424242</color>
<color name="text_on_accent">#FFFFFF</color>     <!-- Branco puro -->
```

### Tipografia
- **Títulos:** 16sp - 18sp (Bold)
- **Subtítulos:** 13sp - 14sp (Medium)
- **Corpo:** 12sp - 13sp (Regular)
- **Caption:** 10sp - 11sp (Regular)

### Espaçamentos
- **Padding interno cards:** 12dp - 16dp
- **Margin entre cards:** 10dp - 12dp
- **Margin lateral:** 16dp
- **Card corner radius:** 16dp - 20dp (mais arredondado)

---

## 🧩 Componentes

### 1. Glassmorphism Card
**Características:**
- Background: Branco 60-70% transparência
- Backdrop blur: 10-20dp
- Border: 1dp branco 20%
- Elevation: 4-6dp com sombra suave
- Corner radius: 16-20dp

**XML Base:**
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="@color/surface_glass"
    app:cardCornerRadius="18dp"
    app:cardElevation="6dp"
    app:strokeColor="#33FFFFFF"
    app:strokeWidth="1dp">
    <!-- Conteúdo -->
</com.google.android.material.card.MaterialCardView>
```

### 2. Card Laranja com Gradiente
**Usado para:** Tarefas pendentes, destaques importantes

**Gradiente:**
```xml
<!-- drawable/gradient_orange_accent.xml -->
<shape>
    <gradient
        android:angle="135"
        android:startColor="@color/accent_start"
        android:centerColor="@color/accent"
        android:endColor="@color/accent_end"
        android:type="linear"/>
    <corners android:radius="18dp"/>
</shape>
```

### 3. Bottom Navigation Pill
**Design:**
- Formato cápsula arredondado
- Flutuante com margin 16dp dos lados
- Fundo branco 80% transparência
- Altura: 64dp
- Largura: match_parent - 32dp (margin)

**Características:**
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:layout_width="0dp"
    android:layout_height="64dp"
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp"
    android:layout_marginBottom="12dp"
    android:background="@drawable/bottom_nav_pill_background"
    android:elevation="12dp"
    app:itemIconTint="@color/bottom_nav_selector"
    app:itemTextColor="@color/bottom_nav_selector"
    app:labelVisibilityMode="labeled"/>

<!-- drawable/bottom_nav_pill_background.xml -->
<shape>
    <solid android:color="@color/surface_glass"/>
    <corners android:radius="32dp"/>
    <stroke
        android:width="1dp"
        android:color="#33FFFFFF"/>
</shape>
```

---

## 📱 Layouts

### 1. Splash Screen (activity_splash.xml)
**Alterações:**
- ✅ Background: Foto de estudo com overlay escuro (60%)
- ✅ Logo: Novo design moderno (ver seção Assets)
- ✅ Animações: Fade in + scale suave
- ✅ Subtítulo com glassmorphism

**Layout:**
```
┌─────────────────────────────┐
│                             │
│    [Foto de Fundo 100%]     │
│                             │
│         [Novo Logo]         │
│                             │
│    📚 Gestão de Estudos     │
│   "Organize seus estudos"   │
│                             │
│    © 2025 IPS - Setúbal     │
└─────────────────────────────┘
```

### 2. Home (fragment_home.xml)
**Alterações:**
- ✅ Background: Foto com overlay 70%
- ✅ 3 Cards principais compactos com glassmorphism
- ✅ Card laranja para tarefas pendentes
- ✅ Layout mais espaçoso e respirável

**Estrutura:**
```
┌─────────────────────────────┐
│  [Foto de Fundo - 60-70%]   │
│                             │
│  ┌─────────────────────┐    │
│  │ 📊 Total Disciplinas│    │ ← Card Glass Azul
│  │        5            │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ 📝 Tarefas Pendentes│    │ ← Card Gradiente Laranja
│  │        12           │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ ⏱️ Tempo de Estudo  │    │ ← Card Glass Branco
│  │      2h 45min       │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ 📅 Calendário       │    │ ← Card Glass expandido
│  │    [grid dias]      │    │
│  └─────────────────────┘    │
│                             │
└──────[Bottom Nav Pill]──────┘
```

**Tamanhos dos Cards:**
- Cards estatística: 90dp altura (mais compacto que 120dp atual)
- Card calendário: wrap_content (compacto)
- Margin entre cards: 12dp

### 3. Tarefas (fragment_tasks.xml)
**Alterações:**
- ✅ Background: Foto com overlay
- ✅ Item cards com glassmorphism
- ✅ FAB laranja com gradiente
- ✅ Filtros em pills

### 4. Disciplinas (fragment_subjects.xml)
**Alterações:**
- ✅ Background: Foto com overlay
- ✅ Cards disciplinas com glassmorphism
- ✅ Color indicator mais sutil
- ✅ FAB com gradiente

### 5. Timer (fragment_timer.xml)
**Alterações:**
- ✅ Background: Foto com overlay
- ✅ Timer central com glassmorphism
- ✅ Botões com glass effect
- ✅ Estatísticas em cards compactos

---

## 🎨 Assets

### Logo Redesenhado
**Conceito:** Livro + Cronômetro (fusão de estudo e tempo)

**Design:**
- Livro aberto estilizado
- Cronômetro integrado ao design
- Cores: Gradiente azul → laranja
- Estilo: Flat, moderno, minimalista
- Tamanhos: 48dp, 72dp, 120dp

**Arquivo:** `drawable/app_logo_v2.xml`

### Background Image
**Especificações:**
- Imagem: Livro aberto com laptop (ambiente de estudo)
- Resolução: 1080x1920 (FHD)
- Formato: JPG (otimizado)
- Localização: `drawable/background_study.jpg`

**Alternativas:**
- `background_study_light.jpg` - Versão mais clara
- `background_study_dark.jpg` - Versão mais escura
- `background_study_blur.jpg` - Versão desfocada

### Drawables Adicionais

#### 1. Background Overlay
```xml
<!-- drawable/background_full.xml -->
<layer-list>
    <item>
        <bitmap
            android:src="@drawable/background_study"
            android:gravity="fill"
            android:scaleType="centerCrop"/>
    </item>
    <item>
        <shape>
            <solid android:color="@color/background_overlay"/>
        </shape>
    </item>
</layer-list>
```

#### 2. Card Glassmorphism
```xml
<!-- drawable/card_glass_background.xml -->
<layer-list>
    <item>
        <shape>
            <solid android:color="@color/surface_glass"/>
            <corners android:radius="18dp"/>
        </shape>
    </item>
    <item>
        <shape>
            <stroke
                android:width="1dp"
                android:color="#33FFFFFF"/>
            <corners android:radius="18dp"/>
        </shape>
    </item>
</layer-list>
```

---

## ✅ Checklist de Implementação

### Fase 1: Preparação (Assets & Colors)
- [ ] Adicionar imagem de fundo (background_study.jpg)
- [ ] Criar novo logo (app_logo_v2.xml)
- [ ] Atualizar colors.xml com nova paleta
- [ ] Criar drawables base (glassmorphism, gradientes)

### Fase 2: Componentes Base
- [ ] Criar drawable para glassmorphism card
- [ ] Criar gradiente laranja (gradient_orange_accent.xml)
- [ ] Criar background full com overlay (background_full.xml)
- [ ] Criar bottom nav pill background

### Fase 3: Splash Screen
- [ ] Atualizar activity_splash.xml com novo layout
- [ ] Aplicar background com foto
- [ ] Integrar novo logo
- [ ] Adicionar animações suaves

### Fase 4: Home Fragment
- [ ] Aplicar background em fragment_home.xml
- [ ] Redesenhar cards com glassmorphism
- [ ] Reduzir tamanho dos cards (90dp)
- [ ] Card laranja para tarefas pendentes
- [ ] Ajustar espaçamentos (12dp margins)
- [ ] Compactar calendário

### Fase 5: Outros Fragments
- [ ] Atualizar fragment_tasks.xml
- [ ] Atualizar fragment_subjects.xml
- [ ] Atualizar fragment_timer.xml
- [ ] Aplicar glassmorphism em item cards

### Fase 6: Bottom Navigation
- [ ] Atualizar activity_main.xml
- [ ] Aplicar estilo pill ao bottom nav
- [ ] Adicionar margins e elevation
- [ ] Configurar corners arredondados (32dp)

### Fase 7: Ajustes Finais
- [ ] Testar em diferentes tamanhos de tela
- [ ] Ajustar contraste de texto
- [ ] Verificar acessibilidade
- [ ] Otimizar performance
- [ ] Testar animações

---

## 📊 Resultado Esperado

### Checklist do Resultado Perfeito
Quando implementado, o app deve ter:

✅ **3 cards completos** na home
✅ **Cards pequenos/compactos** (≤90dp altura)
✅ **Imagem de fundo bem visível** (60%+ da tela)
✅ **Card laranja com gradiente** amarelo→laranja sutil
✅ **Glassmorphism** (vidro fosco transparente)
✅ **Bottom nav moderno** arredondado (tipo cápsula/pill)
✅ **Layout equilibrado** e espaçoso
✅ **Novo logo** moderno e atraente
✅ **Foto de fundo** em TODAS as telas
✅ **Splash screen** com foto principal

---

## 🎯 Próximos Passos

1. **Criar/obter a imagem de fundo**
   - Pesquisar imagem de estudo de alta qualidade
   - Ou usar gerador de IA (Midjourney, DALL-E)
   - Otimizar para mobile (1080x1920)

2. **Redesenhar o logo**
   - Criar no Figma ou vetor SVG
   - Converter para Android XML vector

3. **Implementar em ordem**
   - Seguir checklist fase por fase
   - Testar após cada fase
   - Ajustar conforme necessário

4. **Validar design**
   - Comparar com referências
   - Validar usabilidade
   - Ajustar cores se necessário

---

## 🎨 Referências de Design

### Apps Inspiração
- **Notion:** Cards glassmorphism e layouts espaçosos
- **Todoist:** Bottom nav moderno e cores suaves
- **Forest:** Integração foto de fundo com UI
- **Google Calendar:** Cards compactos e informativos

### Recursos
- Material Design 3: https://m3.material.io
- Glassmorphism Generator: https://hype4.academy/tools/glassmorphism-generator
- Color Palette Tool: https://coolors.co

---

## 📝 Notas Importantes

### Compatibilidade
- Mínimo Android: API 24 (Android 7.0)
- Target Android: API 34 (Android 14)
- Glassmorphism: Requer elevation e translucent backgrounds

### Performance
- Imagem de fundo: Usar Glide para carregamento eficiente
- Cache: Implementar cache de background
- Blur effect: Usar RenderScript ou biblioteca RenderEffect

### Acessibilidade
- Contraste texto: Mínimo 4.5:1 (WCAG AA)
- Touch targets: Mínimo 48dp
- Text scaling: Suportar até 200%

---

**Documento criado em:** 26/11/2025
**Última atualização:** 26/11/2025
**Versão:** 1.0
