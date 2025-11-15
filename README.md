# Gestão de Tarefas e Estudos

Projeto desenvolvido para a disciplina de **Programação de Aplicações Móveis (PAM)** do IPS Setúbal.

## Sobre o Projeto

Esta aplicação Android ajuda estudantes a gerir as suas tarefas e tempo de estudo. Foi desenvolvida como projeto final da cadeira de PAM.

## Funcionalidades

- ✅ Gestão de Disciplinas (criar, editar, eliminar)
- ✅ Gestão de Tarefas com prioridades
- ✅ Timer Pomodoro para sessões de estudo
- ✅ Dashboard com estatísticas
- ✅ Suporte para Português e Inglês

## Tecnologias Utilizadas

- Java
- Android SDK (API 24+)
- SQLite Database
- Material Design Components
- Bottom Navigation

## Como Executar

1. Clonar o repositório
2. Abrir no Android Studio
3. Sincronizar o Gradle
4. Executar no emulador ou dispositivo físico

## Estrutura do Projeto

```
app/
├── src/main/
│   ├── java/com/example/gestaodetarefasestudos/
│   │   ├── adapters/          # Adapters para RecyclerView
│   │   ├── database/          # Database e DAOs
│   │   ├── enums/             # Enumerações (Prioridade, Estado)
│   │   ├── fragments/         # Fragmentos da aplicação
│   │   ├── models/            # Modelos de dados
│   │   └── Activities...      # Activities principais
│   └── res/                   # Recursos (layouts, strings, cores)
```

## Funcionalidades Implementadas

### 1. Dashboard (Home)
Mostra estatísticas gerais:
- Total de disciplinas cadastradas
- Número de tarefas pendentes
- Tempo de estudo do dia

### 2. Disciplinas
- Criar disciplinas com nome, código e cor
- Editar disciplinas existentes
- Eliminar disciplinas
- Validação de código único

### 3. Tarefas
- Criar tarefas associadas a disciplinas
- Definir prioridade (Baixa, Média, Alta)
- Data de entrega (não permite datas passadas)
- Marcar tarefas como concluídas
- Editar e eliminar tarefas

### 4. Timer Pomodoro
- Sessões de trabalho (25 minutos padrão)
- Intervalos de descanso (5 minutos padrão)
- Durações configuráveis
- Salva sessões de estudo automaticamente
- Pausa e retoma disponíveis

## Dados de Exemplo

Na primeira execução, a aplicação cria automaticamente alguns dados de exemplo para facilitar os testes:
- 3 disciplinas (PAM, BD, WEB)
- 3 tarefas com diferentes prioridades
- 2 sessões de estudo

## Melhorias Futuras (TODO)

- [ ] Adicionar filtros e ordenação nas listas
- [ ] Seleção de disciplina no Timer
- [ ] Notificações para lembretes de tarefas
- [ ] Gráficos de estatísticas
- [ ] Modo escuro
- [ ] Exportar dados

## Autor

Evers - Estudante de Engenharia Informática
IPS Setúbal - 2025/2026

## Notas de Desenvolvimento

Este projeto foi desenvolvido como parte da aprendizagem de Android. Alguns warnings do compilador são esperados (uso de APIs deprecated em alguns pontos) mas não afetam o funcionamento da aplicação.

### Problemas Conhecidos

- Algumas APIs deprecated foram usadas (onActivityResult) - TODO migrar para ActivityResultLauncher
- Timer salva sessões genéricas (sem disciplina específica) - melhorar no futuro

## Licença

Projeto académico - IPS Setúbal
