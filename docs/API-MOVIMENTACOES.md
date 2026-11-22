# API de Movimentações e Estoque — contrato para o time

Base: `http://localhost:8080`

| Método | Endpoint | O que faz | Sucesso | Erros possíveis |
|---|---|---|---|---|
| POST | `/api/movimentacoes/entrada` | Registra entrada de mercadoria e soma ao saldo do produto | 201 + header `Location` | 400, 404, 409 |
| POST | `/api/movimentacoes/saida` | Registra saída de mercadoria e subtrai do saldo do produto | 201 + header `Location` | 400, 404, 409 |
| GET | `/api/movimentacoes` | Lista o histórico de movimentações (mais recentes primeiro; aceita `?produtoId=` e `?tipo=`) | 200 | 400, 404 |
| GET | `/api/movimentacoes/produto/{produtoId}` | Lista o histórico de movimentações de um produto específico | 200 | 404 |
| GET | `/api/produtos/estoque-baixo` | Lista produtos com `quantidadeEmEstoque <= estoqueMinimo` | 200 | — |

## Campos (JSON)

### Enviar em `POST /api/movimentacoes/entrada` e `POST /api/movimentacoes/saida`

```json
{
  "produtoId": 1,
  "quantidade": 15,
  "observacao": "Compra fornecedor"
}

{
  "id": 1,
  "produtoId": 1,
  "produtoNome": "Refrigerante Cola 2L",
  "tipo": "ENTRADA",
  "quantidade": 15,
  "estoqueAtual": 25,
  "dataMovimentacao": "2026-11-22T14:00:00",
  "observacao": "Compra fornecedor"
}

[
  {
    "id": 1,
    "nome": "Refrigerante Cola 2L",
    "codigoBarras": "7894900011517",
    "preco": 8.49,
    "quantidadeEmEstoque": 3,
    "estoqueMinimo": 5,
    "categoriaId": 1,
    "categoriaNome": "Bebidas"
  }
]

{
  "dataHora": "2026-11-22T14:00:00",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos estão inválidos",
  "caminho": "/api/movimentacoes/entrada",
  "campos": {
    "quantidade": "A quantidade deve ser maior que zero"
  }
}

{
  "dataHora": "2026-11-22T14:00:00",
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Produto não encontrado com o id 999",
  "caminho": "/api/movimentacoes/entrada"
}

{
  "dataHora": "2026-11-22T14:00:00",
  "status": 409,
  "erro": "Regra de negócio violada",
  "mensagem": "Quantidade solicitada (50) maior que o estoque disponível (10) para o produto 'Refrigerante Cola 2L'.",
  "caminho": "/api/movimentacoes/saida"
}

# Registrar entrada
curl.exe -X POST http://localhost:8080/api/movimentacoes/entrada `
  -H "Content-Type: application/json" `
  -d '{\"produtoId\": 1, \"quantidade\": 15, \"observacao\": \"Compra fornecedor\"}'

# Registrar saída
curl.exe -X POST http://localhost:8080/api/movimentacoes/saida `
  -H "Content-Type: application/json" `
  -d '{\"produtoId\": 1, \"quantidade\": 4, \"observacao\": \"Venda caixa\"}'

# Consultar histórico geral
curl.exe http://localhost:8080/api/movimentacoes

# Consultar histórico por produto e/ou tipo
curl.exe "http://localhost:8080/api/movimentacoes?produtoId=1"
curl.exe "http://localhost:8080/api/movimentacoes?tipo=SAIDA"

# Consultar produtos com estoque baixo
curl.exe http://localhost:8080/api/produtos/estoque-baixo

