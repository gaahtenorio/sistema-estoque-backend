# Sistema de Estoque — Back-end

API do sistema de gestão de estoque de supermercado (Java + Spring Boot + PostgreSQL).

> **Responsável por esta entrega:** Pessoa 4 — Banco de dados e Categorias
> Branch: `feat/categorias`

---

## 1. O que já está pronto

| Item | Status |
|---|---|
| Projeto Spring Boot configurado (Java 21, Spring Boot 4.1.2) | ✅ |
| Conexão com PostgreSQL via variáveis de ambiente | ✅ |
| Spring Data JPA + Hibernate configurados | ✅ |
| Entidade `Categoria` + tabela `categorias` | ✅ |
| `CategoriaRepository`, `CategoriaService`, `CategoriaController` | ✅ |
| Cadastro, listagem, busca, edição e exclusão de categorias | ✅ |
| Relacionamento `Categoria 1:N Produto` (coluna `categoria_id`) | ✅ |
| Validação dos campos, erros padronizados e CORS para o Angular | ✅ |
| 9 testes automatizados passando (`./mvnw test`) | ✅ |

A documentação dos endpoints está em **[docs/API-CATEGORIAS.md](docs/API-CATEGORIAS.md)**.

---

## 2. Pré-requisitos

- **JDK 21** (o projeto usa `java.version=21`)
- **PostgreSQL** rodando localmente (ou em outro host, veja as variáveis abaixo)
- Não precisa instalar Maven: use o `./mvnw` que já está no projeto

---

## 3. Criar o banco de dados

No pgAdmin ou no terminal (`psql -U postgres`):

```sql
CREATE DATABASE estoque WITH ENCODING 'UTF8' TEMPLATE template0;
```

Também existe o script pronto em [`db/create-database.sql`](db/create-database.sql).

As **tabelas são criadas automaticamente** pelo Hibernate na primeira execução
(`spring.jpa.hibernate.ddl-auto=update`). O script traz o `CREATE TABLE` só como
referência da modelagem.

---

## 4. Rodar a aplicação

```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API sobe em **http://localhost:8080**.

Se o seu PostgreSQL usar outro usuário/senha/porta, defina as variáveis antes de rodar:

```bash
# Linux/macOS
export DB_URL=jdbc:postgresql://localhost:5432/estoque
export DB_USER=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run
```

```powershell
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/estoque"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgres"
mvnw.cmd spring-boot:run
```

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/estoque` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `SERVER_PORT` | `8080` |

> Nenhuma senha fica no repositório — é só isso que evita vazar credencial no GitHub.

---

## 5. Rodar os testes

```bash
./mvnw test
```

Resultado esperado: `Tests run: 9, Failures: 0, Errors: 0`.

Os testes usam **H2 em memória** (perfil `test`), ou seja: **não precisam do PostgreSQL
ligado** e não alteram o banco de ninguém.

---

## 6. Estrutura de pastas (parte de categorias)

```
src/main/java/com/supermercado/estoque/
├── EstoqueApplication.java
├── config/
│   └── CorsConfig.java                 # libera o front Angular (localhost:4200)
├── controller/
│   └── CategoriaController.java        # endpoints /api/categorias
├── dto/
│   ├── CategoriaRequestDTO.java        # o que o front envia
│   └── CategoriaResponseDTO.java       # o que a API devolve
├── exception/
│   ├── GlobalExceptionHandler.java     # erros em JSON padronizado
│   ├── ErroResponse.java
│   ├── RecursoNaoEncontradoException.java
│   └── RegraDeNegocioException.java
├── model/
│   ├── Categoria.java
│   └── Produto.java                    # ⚠️ stub provisório (Pessoa 5)
├── repository/
│   ├── CategoriaRepository.java
│   └── ProdutoRepository.java          # ⚠️ stub provisório (Pessoa 5)
└── service/
    └── CategoriaService.java
```

---

## 7. Combinados da equipe (importante)

- **Mudou uma entidade?** Avise no grupo. `ddl-auto=update` altera a tabela no banco
  de todo mundo na próxima execução.
- **Nomes de campos e JSON** da categoria estão documentados em
  `docs/API-CATEGORIAS.md` — não invente outro nome no front.
- **Produto** é da Pessoa 5: o arquivo `model/Produto.java` é provisório e existe
  apenas para o relacionamento compilar. Veja o contrato no doc acima.

---

## 8. Enviar para o GitHub

```bash
git checkout -b feat/categorias     # já criada neste ambiente
git add .
git commit -m "feat: API de categorias com PostgreSQL, validações e testes"
git push -u origin feat/categorias
```

Depois abra um Pull Request para a `main` e peça revisão de alguém do grupo.
