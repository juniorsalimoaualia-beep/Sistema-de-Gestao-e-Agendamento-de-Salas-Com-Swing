# Relatório Académico do Projecto

## Sistema de Gestão e Agendamento de Salas com Swing (SGSAS)

**Instituição:** [Nome da instituição]  
**Faculdade/Escola:** [Nome da faculdade ou escola]  
**Curso:** [Nome do curso]  
**Disciplina:** [Nome da disciplina]  
**Docente:** [Nome do docente]  
**Autores/Grupo:** [Nomes dos membros do grupo]  
**Local:** [Cidade], Moçambique  
**Data:** 15 de Novembro de 2026  
**Nota:** substituir os campos entre parênteses rectos antes da entrega.

---

## Resumo

Este relatório descreve o Sistema de Gestão e Agendamento de Salas (SGSAS), uma aplicação desktop desenvolvida em Java, com interface gráfica Swing e armazenamento de dados em MySQL. O projecto destina-se a apoiar a organização de cursos, disciplinas, salas, horários, inscrições de estudantes e reservas de espaços, disponibilizando funcionalidades diferenciadas para administrador, secretário, docente e estudante.

O documento apresenta a finalidade do sistema, a arquitectura, as tecnologias utilizadas, o modelo de dados e as principais regras de negócio observadas no código e no esquema SQL. Apresenta igualmente desafios técnicos evidenciados pela implementação, incluindo a gestão de relações opcionais, a prevenção de conflitos de horários, a preservação dos dados dos formulários em caso de erro e a execução de operações de base de dados sem bloquear a interface.

A análise foi feita por inspecção do código-fonte e do ficheiro `database/sgsas.sql`. A compilação dos 58 ficheiros Java de produção foi concluída com sucesso. Não foi feita uma execução integrada com uma instância MySQL; por isso, as regras descritas representam o comportamento observado no código e não uma certificação operacional em produção.

**Palavras-chave:** gestão académica; agendamento de salas; Java; Swing; MySQL; reservas; inscrições.

---

## 1. Introdução

Instituições de ensino precisam de coordenar salas, horários, cursos, disciplinas e actividades académicas sem sobreposições e com responsabilidades bem definidas. A gestão manual destas informações pode originar conflitos de utilização, inconsistências nos registos e dificuldades de acompanhamento por parte dos diferentes intervenientes.

O SGSAS procura centralizar estes processos numa aplicação desktop. O sistema oferece áreas distintas conforme o perfil do utilizador e conserva os dados numa base de dados relacional. Este relatório descreve o problema abordado, a justificação do projecto, os objectivos, a organização técnica, os dados geridos, as regras de negócio, os desafios de implementação e os pontos que ainda carecem de validação.

### 1.1 Problema e justificação

A utilização de registos dispersos para gerir salas e actividades académicas dificulta a confirmação da disponibilidade dos espaços, o controlo de conflitos e a consulta de informação por estudantes, docentes e pessoal administrativo. O projecto justifica-se pela necessidade de reunir estes dados num sistema comum, com operações adequadas às responsabilidades de cada perfil e validações destinadas a reduzir erros de registo e sobreposições.

### 1.2 Objectivo geral

Descrever o SGSAS e explicar de que modo as suas funcionalidades e regras de negócio apoiam a gestão de salas e actividades académicas.

### 1.3 Objectivos específicos

1. Identificar as tecnologias e os componentes principais do projecto.
2. Descrever os perfis de utilizador e as funcionalidades disponíveis.
3. Apresentar o modelo de dados e as regras de negócio implementadas.
4. Explicar os fluxos de inscrição, horário e reserva de salas.
5. Registar desafios técnicos, limitações observadas e recomendações.

### 1.4 Âmbito e limites

A análise refere-se ao projecto presente na pasta `sistemadegestaoeagendamentodesalascomswing`, no estado observado em 15 de Novembro de 2026. Não se presume que esta pasta corresponda a outra cópia local ou remota do projecto.

O trabalho foi feito por leitura estática dos ficheiros e compilação dos fontes de produção. Não foram executados testes integrados com uma instância MySQL, nem foi validado o comportamento visual da aplicação em execução. Assim, as conclusões descrevem o código e o esquema inspeccionados, mas não certificam a operação em ambiente real.

## 2. Metodologia

Foi realizada uma análise documental e estática das seguintes áreas:

- estrutura de directórios e distribuição dos ficheiros Java;
- classes de arranque, interface, modelos, controladores, serviços e DAOs;
- esquema SQL, índices, restrições únicas e triggers;
- configuração de exemplo e bibliotecas incluídas;
- teste automatizado encontrado no projecto;
- regras de negócio visíveis nos fluxos de reserva e inscrição.

O inventário contabilizou **58 ficheiros Java de produção**, um ficheiro Java de teste e aproximadamente **6 451 linhas Java** na árvore `src`. Foram identificados nove ficheiros no pacote de interface, 13 no pacote de controladores, 15 DAOs, 17 modelos, três serviços, a classe de entrada e um teste. Os 58 fontes de produção foram compilados com `javac`; os números são contagens dos ficheiros presentes e não constituem, por si só, uma medida de qualidade ou completude.

## 3. Caracterização do sistema

### 3.1 Finalidade

O SGSAS é uma aplicação desktop para apoiar a gestão de recursos e actividades académicas. A partir do código e das interfaces analisados, os principais domínios funcionais são:

- administração de contas e dados institucionais;
- organização de departamentos, cursos e disciplinas;
- registo e consulta de salas;
- definição e consulta de horários;
- submissão e tratamento de pedidos de reserva;
- inscrição de estudantes em disciplinas;
- consulta de horários e informações académicas por perfil.

### 3.2 Tecnologias observadas

| Componente | Tecnologia identificada | Utilização |
|---|---|---|
| Linguagem | Java | Lógica de aplicação, modelos e acesso a dados |
| Interface gráfica | Java Swing | Janelas, formulários, tabelas, painéis e mensagens |
| Persistência | MySQL | Armazenamento relacional de dados |
| Conectividade | JDBC / MySQL Connector/J 9.4.0 | Comunicação entre Java e MySQL |
| Protecção de senhas | jBCrypt 0.4 | Geração e verificação de hashes; o código também lê hashes PBKDF2 legados |
| Testes | JUnit 5 no único ficheiro de teste | Teste de ligação ao MySQL, condicionado por variável de ambiente |
| Dependências | Bibliotecas `.jar` locais em `lib` | Connector/J e jBCrypt; não foi encontrado manifesto Maven ou Gradle |

A versão exacta do JDK não está fixada num manifesto de construção. A aplicação é iniciada pela classe `Sgsas`, que agenda a criação da janela principal (`MenuUI`) na fila de eventos do Swing.

## 4. Arquitectura e organização

### 4.1 Organização em camadas

O código apresenta uma separação funcional em camadas:

| Camada | Pacote principal | Responsabilidade observada |
|---|---|---|
| Apresentação | `view` | Janelas Swing, dashboards por perfil, formulários, tabelas e validação visual |
| Coordenação | `controller` | Encaminhamento de operações entre a interface, serviços e DAOs |
| Domínio | `model` | Entidades académicas, estados, perfis, tipos de reserva e dias da semana |
| Persistência | `dao` | Consultas SQL, gravação, autenticação e ligação JDBC |
| Serviços | `service` | Operações de inscrição e utilitários de domínio auxiliares |

Esta organização torna os principais conceitos reconhecíveis e permite localizar responsabilidades por pacote. Contudo, a separação não é absoluta: algumas regras de negócio são aplicadas simultaneamente na interface, no DAO e na base de dados. A sua manutenção exige, por isso, que as validações equivalentes permaneçam coerentes.

### 4.2 Interfaces por perfil

| Perfil | Funcionalidades observadas |
|---|---|
| Administrador | Registo e consulta de salas, cursos, departamentos, disciplinas, horários, turmas, inscrições, docentes, estudantes e secretários; associação de docentes às disciplinas e de secretários aos departamentos; remoção de disciplinas, docentes e secretários, com confirmação e validações de dependências |
| Secretário | Consulta das salas do seu departamento; registo de salas e horários; consulta, confirmação, recusa e cancelamento de reservas do departamento |
| Docente | Consulta das disciplinas e horários associados; consulta de estudantes inscritos; pedido e consulta de reservas; cancelamento das próprias reservas pendentes ou confirmadas |
| Estudante | Consulta da grelha semanal, das disciplinas em que está inscrito e de actividades/reservas associadas ao seu curso; inscrição em disciplinas permitidas |

O ecrã inicial permite escolher um perfil e iniciar sessão. O código inclui o registo da primeira conta de administrador, consulta dos dados da conta e alteração da senha. As acções de remoção e de associação contextual são apresentadas na interface administrativa quando existe uma linha seleccionada.

### 4.3 Modelos de domínio

Os modelos representam utilizadores e especializações (docente, estudante e secretário), departamentos, cursos, disciplinas, salas, turmas, horários, inscrições e reservas. Enumeradores delimitam perfis, dias da semana, tipos de reserva e estados de sala/reserva.

## 5. Modelo de dados e persistência

### 5.1 Entidades principais

O ficheiro `database/sgsas.sql` cria a base de dados `sgsas` com codificação `utf8mb4` e define dez tabelas:

| Tabela | Finalidade |
|---|---|
| `usuarios` | Contas, perfil, dados pessoais, atributos específicos e hash de senha |
| `departamentos` | Departamentos e associações com curso, sala e secretário |
| `cursos` | Cursos e associação ao departamento |
| `disciplinas` | Disciplinas, docente responsável e curso |
| `salas` | Salas, estado de utilização e referência de reserva |
| `turmas` | Turmas por ano e curso |
| `turma_estudantes` | Associação entre estudantes e turmas |
| `inscricoes` | Inscrições de estudantes em disciplinas |
| `horarios` | Curso, disciplina, sala, dia e intervalo horário |
| `reservas` | Pedidos de sala, responsável, data, intervalo, tipo e estado |

As tabelas utilizam índices para consultas por identificadores e atributos frequentes. Existem restrições únicas para email por perfil, secretário associado a departamento, turma por ano/curso e inscrição repetida do mesmo estudante na mesma disciplina. A associação de docente e curso à disciplina é opcional; também é opcional a associação de secretário ao departamento. Assim, um departamento pode existir sem secretário e um secretário pode existir sem departamento.

### 5.2 Integridade implementada no MySQL

O esquema declara triggers para validar:

- a atribuição de um utilizador com perfil de secretário a um departamento;
- horários com curso e sala, intervalo válido e ausência de sobreposição para o mesmo curso;
- inscrições associadas a estudantes, compatibilidade do curso, limite de sete disciplinas e duplicação;
- reservas com data e intervalo válidos, dia compatível com a data, docente válido, sala utilizável, disciplina obrigatória para aulas e ausência de conflitos em reservas pendentes ou confirmadas.

As consultas observadas no acesso a dados usam `PreparedStatement`, que permite parametrizar valores em vez de concatenar directamente a maioria dos dados fornecidos pelo utilizador.

### 5.3 Observação sobre relações

O esquema associa entidades por colunas como `curso_id`, `sala_id`, `docente_id` e `disciplina_id`, mas o ficheiro consultado não declara restrições `FOREIGN KEY`. Assim, parte da integridade referencial depende da lógica da aplicação e dos triggers existentes. Recomenda-se avaliar a introdução de chaves estrangeiras, após verificar os dados existentes e definir as políticas de eliminação/actualização.

## 6. Regras e fluxos funcionais

### 6.1 Autenticação e perfis

1. O utilizador escolhe um dos perfis disponíveis: `ADMIN`, `SECRETARIO`, `DOCENTE` ou `ESTUDANTE`, e autentica-se com email e senha.
2. A combinação de perfil e email é única; o esquema não exige que o email seja único entre perfis diferentes.
3. Na criação de contas pela interface, os campos obrigatórios são validados e a senha deve ter pelo menos oito caracteres. A criação do primeiro administrador só é disponibilizada quando ainda não existe uma conta `ADMIN`.
4. As senhas são guardadas como hashes BCrypt. O código mantém compatibilidade de leitura com hashes PBKDF2 legados e actualiza o hash para BCrypt depois de uma autenticação bem-sucedida.
5. Cada utilizador autenticado pode consultar os dados da sua conta e iniciar a alteração da senha, informando a senha actual e confirmando a nova senha.

### 6.2 Departamentos, cursos e disciplinas

1. Um departamento pode ser criado sem secretário. Um secretário também pode existir sem departamento.
2. Um secretário pode estar associado a, no máximo, um departamento, devido ao índice único sobre `departamentos.secretario_id`. A atribuição é validada para aceitar somente utilizadores com o perfil `SECRETARIO`.
3. Na interface administrativa, a criação de um curso exige a selecção de um departamento existente, embora a coluna `cursos.departamento_id` aceite `NULL` no esquema.
4. Uma disciplina exige nome, mas o docente e o curso são opcionais. Uma disciplina sem curso é tratada como geral; a atribuição de docente pode ser feita durante a criação ou posteriormente.
5. Uma disciplina pode ser removida se não tiver inscrições, horários ou reservas associados. Caso existam dependências, a operação é rejeitada para preservar os registos relacionados.

### 6.3 Salas e horários

1. As salas são registadas e associadas ao departamento responsável; o secretário só gere as salas do seu próprio departamento.
2. O estado de uma sala pertence ao conjunto `LIVRE`, `OCUPADA`, `MANUTENCAO` e `RESERVADA`. Salas em manutenção não podem receber reservas.
3. Um horário requer curso, sala, dia e horas válidos. A disciplina associada é opcional.
4. A hora final deve ser posterior à hora inicial. Não são aceites horários sobrepostos para o mesmo curso no mesmo dia; intervalos adjacentes, em que um termina exactamente quando o outro começa, não se sobrepõem segundo a condição usada no código.
5. Os dias definidos no modelo são de segunda-feira a sábado. O domingo não pode ser representado no horário.

### 6.4 Turmas e inscrições

1. Cada turma corresponde a um curso e a um ano. A combinação curso/ano é única.
2. Um estudante só pode inscrever-se em disciplinas gerais ou em disciplinas do seu próprio curso.
3. Um estudante não pode inscrever-se duas vezes na mesma disciplina e o limite máximo é de sete inscrições.
4. A aplicação e a base de dados repetem validações de curso, duplicação e limite. A gravação bloqueia a linha do estudante durante a verificação, reduzindo o risco de exceder o limite em operações simultâneas.
5. O estudante consulta as suas inscrições e a grelha semanal do curso. A lista de actividades/reservas apresentada é filtrada pelo curso, pela turma ou pela disciplina relacionada.

### 6.5 Reservas de salas

O sistema reconhece os tipos `AULA`, `EVENTO`, `EXAME` e `TESTE`, e os estados `PENDENTE`, `CONFIRMADA`, `RECUSADA` e `CANCELADA`. As regras observadas são:

1. Uma reserva requer sala, docente válido, data, hora de início, hora de fim, tipo e dia da semana. O docente deve possuir o perfil `DOCENTE`.
2. A data não pode estar no passado. A data e o dia da semana declarados devem corresponder, e o intervalo horário deve ser positivo. O calendário do sistema não inclui domingo.
3. Uma reserva do tipo `AULA` exige disciplina. Para os outros tipos, a disciplina pode ficar vazia.
4. Salas em manutenção não aceitam reservas. Na interface do docente, as salas disponíveis são filtradas pelo departamento, data e intervalo escolhidos.
5. São conflitos os intervalos sobrepostos na mesma data que partilhem sala, docente ou turma, quando aplicável. A comparação considera apenas reservas `PENDENTE` ou `CONFIRMADA`; reservas recusadas ou canceladas não bloqueiam esses recursos.
6. Um novo pedido começa em `PENDENTE`. O secretário do departamento pode confirmá-lo, recusá-lo ou cancelá-lo. A confirmação só é aceite para uma reserva pendente e futura e volta a verificar a disponibilidade; a recusa só se aplica a pedidos pendentes. O cancelamento pode aplicar-se a pedidos pendentes ou confirmados.
7. O docente também dispõe de uma acção para cancelar as próprias reservas pendentes ou confirmadas. A interface do estudante permite consultar actividades/reservas associadas ao seu curso, sem as alterar.
8. Depois de uma recusa ou cancelamento, o estado da sala é recalculado considerando as reservas activas que ainda lhe estão associadas.

Há duas ressalvas importantes. Primeiro, `ReservaDAO.salvar` grava o pedido como `PENDENTE` e, na mesma operação, marca a sala como `RESERVADA`. Como o estado é global à sala, não representa por si só as datas e horas de reservas futuras distintas. Segundo, a verificação de disponibilidade usada na confirmação e a alteração de estado são operações separadas; sob concorrência, recomenda-se revalidar o conflito e mudar o estado dentro da mesma transacção.

O texto da interface sugere que aulas decorrem de segunda a sexta-feira e que sábado é uma excepção, enquanto os restantes tipos podem ocorrer até sábado. Porém, `TipoReserva.permiteDia` aceita todos os dias existentes no enum para todos os tipos. A distinção por tipo e uma eventual autorização da excepção de sábado não estão efectivamente aplicadas pelo método.

### 6.6 Remoção e preservação de dados

1. A remoção de uma disciplina é bloqueada quando existem inscrições, horários ou reservas associados.
2. A remoção de um docente é bloqueada se houver reservas ligadas a esse docente. Se não houver, as disciplinas permanecem no sistema, mas ficam sem docente associado.
3. Ao remover um secretário, a associação ao departamento é primeiro limpa; o departamento continua a existir.
4. A interface administrativa pede confirmação e só apresenta as acções contextuais de remoção ou associação quando existe uma linha seleccionada.

## 7. Desafios enfrentados pelo grupo

A organização do código e as regras implementadas permitem identificar desafios técnicos que o grupo teve de resolver durante o desenvolvimento:

| Desafio | Resposta observada no projecto |
|---|---|
| Representar relações académicas que podem ser opcionais, como docente/disciplina e secretário/departamento, sem impedir a criação dos registos principais. | As associações são anuláveis no modelo e na base de dados; a interface apresenta opções como “Sem docente” e “Sem secretário”. |
| Evitar inconsistências entre a interface, as operações Java e as regras da base de dados. | As validações são feitas em mais de uma camada, com transacções em operações críticas e triggers MySQL para horários, inscrições e reservas. |
| Evitar a utilização simultânea da mesma sala, docente ou turma em períodos coincidentes. | A aplicação compara datas e intervalos e considera activas as reservas pendentes e confirmadas; algumas operações usam bloqueios e transacções. |
| Manter a interface Swing responsiva durante operações de base de dados. | As operações de gravação e actualização usam um executor partilhado de uma única thread, e os resultados são devolvidos à fila de eventos do Swing. |
| Não perder os dados já introduzidos quando há campos inválidos ou falhas de persistência. | Os formulários permanecem abertos em caso de erro e só são fechados após uma gravação bem-sucedida. |
| Remover registos sem apagar informação académica relacionada. | A remoção de disciplinas e docentes verifica referências; os secretários são desassociados do departamento antes da eliminação. |
| Reproduzir os testes e a configuração noutra máquina. | O projecto inclui dependências locais e um exemplo de configuração, mas ainda carece de um processo de construção/teste totalmente reproduzível. |

Como não foram realizadas entrevistas aos membros, os desafios aqui apresentados são inferidos a partir dos requisitos e das soluções presentes no código. O grupo poderá acrescentar situações concretas que não estejam documentadas nos artefactos do projecto.

## 8. Avaliação técnica

### 8.1 Aspectos positivos

1. **Separação funcional:** a divisão em `view`, `controller`, `model`, `dao` e `service` facilita a localização de responsabilidades.
2. **Validação em mais de uma camada:** parte das regras está implementada na aplicação e reforçada por triggers SQL.
3. **Protecção de credenciais:** a autenticação utiliza BCrypt em vez de guardar senhas em texto simples.
4. **Parametrização SQL:** os DAOs usam `PreparedStatement` nas consultas observadas.
5. **Tratamento de conflitos:** o sistema considera sobreposição de intervalos de sala, docente e turma em reservas activas.
6. **Interfaces específicas por perfil:** os dashboards apresentam acções e dados relacionados com as responsabilidades de cada tipo de utilizador.
7. **Responsividade e recuperação:** as operações de dados decorrem fora da fila de eventos Swing, e os formulários conservam os valores quando uma operação falha.
8. **Preservação de relações:** a aplicação impede algumas remoções que apagariam ou deixariam inconsistentes inscrições, horários e reservas.

### 8.2 Limitações e aspectos a melhorar

| Prioridade | Observação no código analisado | Impacto potencial |
|---|---|---|
| Alta | `ReservaDAO.salvar` marca a sala como `RESERVADA` logo após inserir uma reserva `PENDENTE`. | A disponibilidade global da sala pode parecer comprometida antes da decisão do secretário; o estado da sala não representa claramente uma reserva futura específica. |
| Média | `DocenteUI` permite ao docente cancelar reservas pendentes ou confirmadas, além das acções de gestão do secretário. | A política de permissões de cancelamento deve ser confirmada e documentada; o código implementa ambas as possibilidades. |
| Alta | `TipoReserva.permiteDia` aceita todos os dias representados no enum para todos os tipos. | Não codifica a regra de que aulas são normalmente de segunda a sexta e de que sábado é uma excepção; também não há autorização diferenciada para essa excepção. |
| Média | A confirmação consulta disponibilidade e depois actualiza o estado da reserva em operação separada no `GestorReservaDAO`. | A decisão deve ser testada sob concorrência e idealmente protegida por uma transacção que revalide o conflito no momento da alteração. |
| Média | O SQL não declara chaves estrangeiras, embora existam colunas que representam relações entre entidades. | Podem persistir referências a registos inexistentes caso a aplicação não cubra todos os caminhos de escrita. |
| Média | `ConexaoSqlTest` configura propriedades de sistema `sgsas.db.*`, mas `ConexaoSql` carrega os valores de `config.properties` e não lê essas propriedades. | O teste condicionado por variáveis de ambiente pode não ligar à base de dados indicada pelo próprio teste. |
| Média | Não foi identificado um manifesto de construção Maven/Gradle na raiz; há bibliotecas `.jar` incluídas e apenas um teste Java encontrado. | A compilação e execução dos testes podem depender de configuração manual, dificultando a reprodução noutra máquina. |
| Baixa | `config.example.properties` contém campos vazios para URL, utilizador e senha. | Novos utilizadores não têm um exemplo operacional nem instruções completas de configuração. |

As prioridades reflectem a relação das observações com as regras centrais do projecto e com a possibilidade de reproduzir e validar o sistema; não equivalem a uma classificação formal de risco.

## 9. Recomendações

### 9.1 Prioridade imediata — consolidar o fluxo de reservas

1. Definir se a sala deve ser marcada como `RESERVADA` enquanto o pedido ainda está `PENDENTE` e representar a disponibilidade com base em data e intervalo.
2. Confirmar e documentar os perfis autorizados a cancelar uma reserva, mantendo a regra uniforme na interface e no controlador.
3. Tornar atómica a confirmação: bloquear/revalidar a reserva e os conflitos na mesma transacção que altera o estado.

### 9.2 Prioridade alta — explicitar as regras de calendário

1. Implementar a regra de aulas em dias úteis por defeito e definir explicitamente como se autoriza a excepção de sábado.
2. Confirmar que eventos, testes e exames podem ocorrer de segunda-feira a sábado, sem aceitar domingo.
3. Reproduzir as mesmas regras na validação Java e nos triggers SQL e criar testes para cada tipo de reserva e dia.

### 9.3 Prioridade média — integridade e testes

1. Rever as relações do esquema e introduzir chaves estrangeiras onde forem compatíveis com os dados e operações existentes.
2. Corrigir a leitura da configuração JDBC para aceitar as propriedades de teste definidas pelo próprio teste, evitando depender da configuração local.
3. Criar testes automatizados para autenticação, validação de períodos, conflitos, transições de estado, remoções e limite de inscrições.
4. Adicionar um processo de construção reproduzível, com versões de dependências e instruções para preparar a base de dados.

### 9.4 Prioridade contínua — manutenção

1. Documentar os passos de instalação, configuração da base de dados, criação do primeiro administrador e execução.
2. Rever a consistência das mensagens de erro e o registo técnico de excepções, evitando depender exclusivamente de `System.out`/`System.err`.
3. Manter um único ponto de referência para as regras de domínio ou documentar claramente as validações duplicadas entre aplicação e base de dados.

## 10. Validação e limitações da análise

Os **58 ficheiros Java de produção foram compilados com sucesso** durante a elaboração deste relatório. O projecto contém um teste `ConexaoSqlTest`, condicionado pela presença de `SGSAS_TEST_DB_URL`, destinado a verificar a ligação ao MySQL. A leitura do código revelou, contudo, que as propriedades de sistema definidas pelo teste não são consumidas pelo `ConexaoSql`, que lê `config.properties`.

Não foi iniciada uma instância MySQL nem foram executados os fluxos gráficos; o teste de ligação também não foi executado. Não foi identificado um manifesto de construção reproduzível na raiz. Portanto, a compilação não comprova, por si só, a persistência correcta em execução. As conclusões são evidência da inspecção do código e devem ser confirmadas por testes funcionais após a configuração do ambiente.

## 11. Conclusão

O SGSAS constitui uma aplicação desktop orientada à gestão de espaços e actividades académicas. A implementação integra módulos para utilizadores, departamentos, cursos, disciplinas, salas, turmas, horários, inscrições e reservas, com operações adaptadas aos perfis de administrador, secretário, docente e estudante. O uso de Java Swing, JDBC, MySQL, consultas parametrizadas, hashes de senha e validações distribuídas entre a aplicação e a base de dados constitui uma base funcional para a continuidade do projecto.

Durante a implementação, os desafios centrais consistiram em representar associações opcionais, proteger a disponibilidade dos recursos, manter a interface responsiva e preservar os dados em operações que falham. As soluções presentes — validações, transacções, um executor partilhado e remoções que respeitam dependências — respondem a parte destes desafios.

Persistem pontos que merecem evolução, em particular a gestão do estado global da sala, a atomicidade da confirmação, a definição rigorosa dos dias permitidos por tipo de reserva, a integridade referencial e a configuração de testes. Com estas melhorias e uma validação integrada em MySQL, o sistema poderá oferecer resultados mais consistentes e reproduzíveis. Conclui-se que o projecto responde a necessidades concretas de organização académica e constitui uma experiência prática de aplicação de conceitos de programação orientada a objectos, interfaces gráficas e bases de dados relacionais.

## Referências

### Fontes do projecto

- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/Sgsas.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/MenuUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/AdminUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/SecretarioUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/DocenteUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/EstudanteUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/view/PerfilDashboardUI.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/AutenticacaoDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/ConexaoSql.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/DisciplinaDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/DocenteDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/InscricaoDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/HorarioDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/ReservaDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/GestorReservaDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/SecretarioDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/TurmaDAO.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/service/DatabaseTaskExecutor.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/model/DiaSemana.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/model/EstadoReserva.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/model/EstadoSala.java`
- `src/main/java/com/mycompany/sistemadegestaoeagendamentodesalas/model/TipoReserva.java`
- `database/sgsas.sql`
- `config.example.properties`
- `src/test/java/com/mycompany/sistemadegestaoeagendamentodesalas/dao/ConexaoSqlTest.java`

### Documentação técnica

1. Oracle. *The Java Tutorials: Creating a GUI With Swing*. Disponível em: <https://docs.oracle.com/javase/tutorial/uiswing/>.
2. MySQL. *MySQL Reference Manual*. Disponível em: <https://dev.mysql.com/doc/refman/8.4/en/>.
3. MySQL. *MySQL Connector/J Developer Guide*. Disponível em: <https://dev.mysql.com/doc/connector-j/en/>.
4. jBCrypt. *jBCrypt 0.4*, biblioteca utilizada pelo projecto para hashing de senhas; dependência incluída em `lib/jbcrypt-0.4.jar`.
