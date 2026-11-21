# API de Categorias — contrato para o time

Base: `http://localhost:8080`

| Método | Endpoint | O que faz | Sucesso | Erros possíveis |
|---|---|---|---|---|
| GET | `/api/categorias` | lista todas (ordem alfabética) | 200 | — |
| GET | `/api/categorias?busca=beb` | filtra por parte do nome | 200 | — |
| GET | `/api/categorias/{id}` | busca uma categoria | 200 | 404 |
| POST | `/api/categorias` | cadastra | 201 + header `Location` | 400, 409 |
| PUT | `/api/categorias/{id}` | edita | 200 | 400, 404, 409 |
| DELETE | `/api/categorias/{id}` | exclui | 204 | 404, 409 |

### Campos (JSON) — este é o combinado com o front

**Enviar (POST/PUT):**

```json
{
  "nome": "Bebidas",
  "descricao": "Sucos, refrigerantes e águas"
}
```

| Campo | Obrigatório | Regra |
|---|---|---|
| `nome` | sim | 2 a 80 caracteres, **único** (não diferencia maiúsculas: "Bebidas" = "bebidas") |
| `descricao` | não | até 255 caracteres |

**Receber (GET/POST/PUT):**

```json
{
  "id": 1,
  "nome": "Bebidas",
  "descricao": "Sucos, refrigerantes e águas",
  "quantidadeProdutos": 0
}
```

`quantidadeProdutos` é só leitura — serve para o front mostrar "12 produtos" e para
desabilitar o botão de excluir quando for maior que zero.

### Regras de negócio implementadas

1. Nome obrigatório e único, sem diferenciar maiúsculas/minúsculas.
2. Espaços extras nas pontas e repetidos no meio são normalizados (`"  Bebidas  "` → `"Bebidas"`).
3. **Não é possível excluir** categoria que ainda tem produtos vinculados (HTTP 409 com a contagem).
4. `id` inexistente → 404 com mensagem pronta para o usuário.

### Formato padronizado de erro

Erro de validação (400):

```json
{
  "dataHora": "2026-11-20T21:00:45",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos estão inválidos",
  "caminho": "/api/categorias",
  "campos": { "nome": "O nome da categoria é obrigatório" }
}
```

Erro de regra de negócio (409):

```json
{
  "dataHora": "2026-11-20T21:00:45",
  "status": 409,
  "erro": "Regra de negócio violada",
  "mensagem": "Já existe uma categoria com o nome 'bebidas'",
  "caminho": "/api/categorias"
}
```

No Angular, o jeito fácil de exibir é ler `erro.error.mensagem` e, quando existir,
`erro.error.campos` para marcar campo por campo no formulário.

---

## Exemplos de requisição (curl)

```bash
# Listar
curl http://localhost:8080/api/categorias

# Cadastrar
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome":"Bebidas","descricao":"Sucos, refrigerantes e águas"}'

# Editar
curl -X PUT http://localhost:8080/api/categorias/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Bebidas","descricao":"Bebidas em geral"}'

# Excluir
curl -X DELETE http://localhost:8080/api/categorias/3
```

---

## Saída real da API (execução de validação)

Transcrição de uma execução de ponta a ponta, para conferência:

```
### POST /api/categorias (Bebidas)
{"id":1,"nome":"Bebidas","descricao":"Sucos, refrigerantes e águas","quantidadeProdutos":0}
[HTTP 201]

### POST nome repetido -> 409
{"dataHora":"2026-11-21T00:00:45.100","status":409,"erro":"Regra de negócio violada","mensagem":"Já existe uma categoria com o nome 'bebidas'","caminho":"/api/categorias"}
[HTTP 409]

### POST nome vazio -> 400
{"dataHora":"2026-11-21T00:00:45.121","status":400,"erro":"Dados inválidos","mensagem":"Um ou mais campos estão inválidos","caminho":"/api/categorias","campos":{"nome":"O nome da categoria é obrigatório"}}
[HTTP 400]

### GET /api/categorias
[{"id":1,"nome":"Bebidas","descricao":"Sucos, refrigerantes e águas","quantidadeProdutos":0},{"id":3,"nome":"Hortifruti","quantidadeProdutos":0},{"id":2,"nome":"Limpeza","descricao":"Produtos de limpeza","quantidadeProdutos":0}]
[HTTP 200]

### GET /api/categorias?busca=limp
[{"id":2,"nome":"Limpeza","descricao":"Produtos de limpeza","quantidadeProdutos":0}]
[HTTP 200]

### PUT /api/categorias/1
{"id":1,"nome":"Bebidas","descricao":"Bebidas em geral","quantidadeProdutos":0}
[HTTP 200]

### DELETE /api/categorias/3
[HTTP 204]

### GET /api/categorias/3 (excluída)
{"dataHora":"2026-11-21T00:00:45.273","status":404,"erro":"Recurso não encontrado","mensagem":"Categoria não encontrada com o id 3","caminho":"/api/categorias/3"}
[HTTP 404]
```

CORS conferido com `Origin: http://localhost:4200`:

```
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
```

---

## Contrato para a PESSOA 5 (Produtos)

Os arquivos `model/Produto.java` e `repository/ProdutoRepository.java` são **provisórios**
(criados só para o relacionamento compilar). A Pessoa 5 deve **completá-los**, mantendo
estes pontos — o resto é livre:

```java
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... campos da Pessoa 5: codigoBarras, preco, quantidadeEmEstoque, estoqueMinimo ...

    // NÃO mude: é este mapeamento que cria a coluna categoria_id
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_categoria"))
    private Categoria categoria;
}
```

Checklist da Pessoa 5:

- [ ] Manter a tabela chamada `produtos`.
- [ ] Manter `@ManyToOne` com `@JoinColumn(name = "categoria_id", nullable = false)`.
- [ ] Manter `countByCategoriaId(Long)` no `ProdutoRepository` (a exclusão de categoria depende dele).
- [ ] Se quiser a lista no lado da categoria, ela já existe: `Categoria.getProdutos()`.
- [ ] Não usar `@JsonIgnore`/serializar `categoria.getProdutos()` no JSON de produto
      (evita loop infinito) — devolva um `categoriaId` + `categoriaNome` no DTO.

Sugestão de JSON de produto para alinhar com o front:

```json
{
  "id": 10,
  "nome": "Refrigerante Cola 2L",
  "codigoBarras": "7894900011517",
  "preco": 8.49,
  "quantidadeEmEstoque": 24,
  "estoqueMinimo": 5,
  "categoriaId": 1,
  "categoriaNome": "Bebidas"
}
```

---

## Observações técnicas (Spring Boot 4 / Jackson 3)

- No Spring Boot 4 o Jackson é o **3.x** (`tools.jackson`). A propriedade
  `spring.jackson.serialization.write-dates-as-timestamps` **não existe mais** e
  derruba a aplicação na inicialização. As datas `java.time` já saem em ISO-8601.
- Anotações de teste mudaram de pacote: `@AutoConfigureMockMvc` agora está em
  `org.springframework.boot.webmvc.test.autoconfigure`.
- Os testes usam o perfil `test` (`src/test/resources/application-test.properties`),
  então o `application.properties` principal continua sendo validado nos testes —
  foi assim que pegamos o erro do Jackson acima.

---

## Próximos passos (fora do escopo da Pessoa 4)

- Flyway para versionar o banco (hoje é `ddl-auto=update`).
- Swagger/OpenAPI (`springdoc`) para documentação interativa.
- `GET /api/produtos/estoque-baixo` e endpoints de movimentação (Pessoas 5 e 6).
