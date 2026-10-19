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
Filtrar arquivos
+235
-4
