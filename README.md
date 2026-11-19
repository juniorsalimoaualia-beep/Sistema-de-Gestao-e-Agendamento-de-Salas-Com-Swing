# Sistema de Gestão e Agendamento de Salas

Aplicação de desktop em Java Swing para gerir salas, horários e reservas. A aplicação utiliza MySQL para guardar os dados.

## Requisitos

- JDK instalado e configurado na IDE.
- MySQL Server 8 ou compatível.
- MySQL Workbench ou outro cliente de base de dados MySQL.

Os drivers JDBC do MySQL e jbcrypt estão incluídos na pasta `lib`.

## Preparar a base de dados

1. Abra o MySQL Workbench (ou outro cliente) e ligue-se ao seu servidor MySQL.
2. Abra e execute primeiro o ficheiro `database/sgsas.sql`. Este script cria a base de dados `sgsas` e a estrutura inicial.
3. Selecione a base de dados `sgsas` como esquema ativo.
4. Abra e execute `database/sgsas1_com_dados.sql`. Este script recria as tabelas e carrega dados de exemplo; execute-o depois do primeiro script.

> **Atenção:** `sgsas1_com_dados.sql` elimina e recria tabelas. Se já tiver dados que queira guardar, faça uma cópia de segurança antes de o executar.

## Configurar a ligação

1. Na pasta principal do projeto, copie `config.example.properties` e renomeie a cópia para `config.properties`. Mantenha o ficheiro de exemplo para referência.
2. Abra `config.properties` e preencha os dados da sua instalação MySQL:

   ```properties
   db.url=jdbc:mysql://localhost:3306/sgsas?useSSL=false&serverTimezone=UTC
   db.user=SEU_UTILIZADOR
   db.password=SUA_PASSWORD
   ```

   Ajuste o endereço, a porta, o utilizador e a palavra-passe conforme a configuração do seu servidor. O ficheiro `config.properties` deve ficar na pasta principal do projeto, ao lado deste README.

> Não partilhe nem envie o seu `config.properties` para o GitHub: contém credenciais da base de dados. O ficheiro está excluído pelo `.gitignore`.

## Abrir e executar na IDE

1. Abra a pasta principal do projeto no VS Code, IntelliJ IDEA, NetBeans ou outra IDE Java.
2. Configure `src/main/java` como pasta de código-fonte, se a IDE não o fizer automaticamente.
3. Execute a classe principal:

   `com.mycompany.sistemadegestaoeagendamentodesalas.Sgsas`
   O arquivo `Sgsas.java`.

O projeto não inclui um ficheiro Maven ou Gradle; configure as bibliotecas incluídas diretamente na IDE.
