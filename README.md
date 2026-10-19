# API de Alunos

API REST para gerenciamento de alunos, desenvolvida com Java e Spring Boot. Permite cadastrar, listar, consultar, atualizar e excluir alunos, com validação dos dados de entrada e tratamento centralizado de erros.

## Funcionalidades

- Cadastro de alunos com ID gerado automaticamente.
- Listagem de todos os alunos e consulta por ID.
- Atualização e exclusão de cadastros.
- Verificação de e-mail duplicado, sem diferenciar maiúsculas e minúsculas.
- Validação de campos com Jakarta Bean Validation.
- Respostas com DTOs, sem expor a senha do aluno.
- Documentação interativa com Swagger UI.

Os dados são armazenados em uma lista em memória e são perdidos ao reiniciar a aplicação. Não é necessário configurar um banco de dados.

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java 21 | Linguagem e versão configurada no projeto |
| Spring Boot 4.1.1 | Inicialização e configuração da aplicação |
| Spring Web MVC | Endpoints REST |
| Jakarta Bean Validation | Validação das requisições |
| Springdoc OpenAPI 3.1.0 | Documentação OpenAPI e Swagger UI |
| Maven Wrapper | Build e gerenciamento de dependências |
| JUnit Jupiter e AssertJ | Testes automatizados |

## Como executar

### Pré-requisitos

- JDK 21 instalado, com `JAVA_HOME` configurado.
- Git para clonar o repositório.
- Acesso à internet na primeira execução para baixar o Maven e as dependências.

O repositório inclui o Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

### Clonar o repositório

```bash
git clone https://github.com/pattyheiden/api-alunos.git
cd api-alunos
```

### Iniciar a aplicação

No Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Com a configuração padrão, a API fica disponível em `http://localhost:8080/alunos`.

### Documentação interativa

Com a aplicação em execução, acesse:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html): visualização e teste dos endpoints.
- [OpenAPI JSON](http://localhost:8080/v3/api-docs): especificação da API.

## Endpoints

| Método | Rota | Descrição | Resposta de sucesso |
| --- | --- | --- | --- |
| `GET` | `/alunos` | Lista todos os alunos | `200 OK` |
| `GET` | `/alunos/{id}` | Consulta um aluno por ID | `200 OK` |
| `POST` | `/alunos` | Cadastra um aluno | `201 Created` |
| `PUT` | `/alunos/{id}` | Atualiza os dados de um aluno | `200 OK` |
| `DELETE` | `/alunos/{id}` | Exclui um aluno | `204 No Content` |

A listagem retorna `[]` quando não há cadastros. A exclusão retorna `204` sem corpo, inclusive quando o ID não existe. A consulta e a atualização de um ID inexistente retornam `404`, desde que a requisição não seja rejeitada antes por validação ou conflito de e-mail.

### Dados de entrada

Os endpoints `POST` e `PUT` recebem JSON com os seguintes campos:

| Campo | Tipo | Validação |
| --- | --- | --- |
| `nome` | String | Obrigatório, não pode conter apenas espaços e deve ter até 50 caracteres |
| `email` | String | Obrigatório, formato de e-mail válido, entre 5 e 50 caracteres e único entre os alunos |
| `senha` | String | Obrigatória, não pode conter apenas espaços e deve ter até 50 caracteres |
| `dataNascimento` | String de data | Obrigatória, no formato `AAAA-MM-DD`, sem data futura |
| `media` | Número | Entre 0 e 10, inclusive |

O `PUT` recebe o conjunto completo de dados do aluno. O campo `id` é gerado pelo serviço e não faz parte da requisição. Como `media` é um `double` primitivo, sua omissão assume o valor `0` na desserialização padrão.

### Cadastrar um aluno

Envie `POST /alunos` com o cabeçalho `Content-Type: application/json` e o corpo:

```json
{
  "nome": "Ana Silva",
  "email": "ana@example.com",
  "senha": "Exemplo123",
  "dataNascimento": "2000-05-15",
  "media": 8.5
}
```

Exemplo de resposta `201 Created`, considerando o primeiro cadastro após iniciar a aplicação:

```json
{
  "id": 1,
  "nome": "Ana Silva",
  "email": "ana@example.com",
  "dataNascimento": "2000-05-15",
  "media": 8.5
}
```

A senha não é incluída nas respostas. O mesmo formato é utilizado na consulta e na atualização; a listagem retorna um array desses objetos.

### Testar pelo PowerShell

Com a aplicação em execução, execute em outro terminal:

```powershell
$aluno = @{
    nome = "Ana Silva"
    email = "ana@example.com"
    senha = "Exemplo123"
    dataNascimento = "2000-05-15"
    media = 8.5
}

# Cadastrar e guardar o ID retornado
$cadastro = Invoke-RestMethod -Uri "http://localhost:8080/alunos" -Method Post -ContentType "application/json" -Body ($aluno | ConvertTo-Json)
$alunoId = $cadastro.id

# Listar todos os alunos
Invoke-RestMethod -Uri "http://localhost:8080/alunos"

# Consultar pelo ID
Invoke-RestMethod -Uri "http://localhost:8080/alunos/$alunoId"

# Atualizar os dados
$aluno.media = 9.0
Invoke-RestMethod -Uri "http://localhost:8080/alunos/$alunoId" -Method Put -ContentType "application/json" -Body ($aluno | ConvertTo-Json)

# Excluir o cadastro
Invoke-RestMethod -Uri "http://localhost:8080/alunos/$alunoId" -Method Delete
```

## Tratamento de erros

| Status | Situação |
| --- | --- |
| `400 Bad Request` | Campos que não atendem às validações |
| `404 Not Found` | Aluno não encontrado na consulta ou atualização |
| `409 Conflict` | E-mail já utilizado por outro aluno |

Exemplo de erro de aluno não encontrado:

```json
{
  "status": 404,
  "mensagem": "Aluno não encontrado",
  "timestamp": "2026-10-19T12:00:00Z"
}
```

Exemplo de erro de validação:

```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "timestamp": "2026-10-19T12:00:00Z",
  "errosAtributos": [
    {
      "atributo": "nome",
      "mensagem": "Nome não pode ser vazio"
    }
  ]
}
```

O `timestamp` é gerado no momento do erro. No conflito de e-mail, a mensagem é `E-mail já existe`, com o mesmo formato do erro `404` e status `409`.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/lima/api_alunos/
│   │   ├── controller/       # Endpoints HTTP
│   │   ├── dto/              # Objetos de entrada e saída
│   │   ├── exception/        # Exceções e respostas de erro
│   │   ├── model/            # Modelo Aluno
│   │   ├── service/          # Regras de negócio e armazenamento em memória
│   │   └── ApiAlunosApplication.java
│   └── resources/
│       └── application.properties
└── test/java/com/lima/api_alunos/
    ├── service/AlunoServiceTest.java
    └── ApiAlunosApplicationTests.java
```

## Testes

No Windows:

```powershell
.\mvnw.cmd test
```

No Linux ou macOS:

```bash
./mvnw test
```

O projeto contém testes do serviço para cadastro, listagem, consulta, atualização e exclusão, além de um teste de carregamento do contexto Spring.

Há duas inconsistências nos cenários atuais de `AlunoServiceTest` identificadas pela leitura do código:

- O teste de listagem cadastra `Henrique Heiden`, mas espera o nome `Lucas` na asserção final.
- O teste de conflito de e-mail na atualização mantém o e-mail do próprio aluno, mas espera uma exceção de duplicidade.

Esses cenários precisam ser ajustados para que as expectativas correspondam aos dados preparados. Os testes não foram executados durante a elaboração deste README.

## Estado atual

O projeto implementa o CRUD com armazenamento em memória. Não há autenticação ou autorização, e a senha recebida é armazenada em memória sem hash, embora seja omitida nas respostas. Persistência em banco de dados e proteção de credenciais são possíveis evoluções do projeto.
