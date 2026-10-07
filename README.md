# Sistema de Gestão de Vendas

## Identificação

| | |
|---|---|
| **Integrante** | Arthur Andrade Lima |
| **Disciplina** | Projeto de Banco de Dados |
| **Professor** | Anderson Soares Costa |

## Vídeo explicativo

▶️ [Assistir à apresentação no YouTube](https://youtu.be/pR0tvXxfHeY)

## Sobre o projeto

Aplicação desktop para gestão de vendas de uma loja de produtos de informática. Ela resolve o problema de controlar de forma organizada o cadastro de clientes, a criação de pedidos, o cálculo de descontos e a baixa de estoque, centralizando as regras de negócio no banco de dados.

Funcionalidades:

- **Cadastro de clientes** (nome, e-mail e telefone).
- **Criação de pedidos:** o usuário seleciona cliente, produto e quantidade; o desconto é calculado pela function `fn_calcular_desconto` e o pedido é gravado com status `PENDENTE`.
- **Relatório de pedidos:** listagem em tempo real a partir da view `vw_relatorio_pedidos`.
- **Finalização de pedidos:** a procedure `sp_finalizar_pedido` dá baixa no estoque dos produtos e marca o pedido como `CONCLUIDO`.

## Tecnologias utilizadas

- Java (interface gráfica com Swing)
- JDBC (driver `postgresql-42.7.3.jar`)
- PostgreSQL
- pgAdmin

## Banco de dados

**SGBD utilizado:** PostgreSQL

### Principais tabelas

| Tabela | Descrição |
|---|---|
| `clientes` | Dados dos clientes (nome, e-mail único, telefone, data de cadastro). |
| `produtos` | Produtos à venda, com preço unitário e quantidade em estoque. |
| `pedidos` | Pedidos realizados, vinculados a um cliente, com status (`PENDENTE`, `CONCLUIDO`, `CANCELADO`) e valor total. |
| `itens_pedido` | Itens de cada pedido (produto, quantidade e preço unitário). |

### View criada

- **`vw_relatorio_pedidos`**: relatório que junta pedidos, clientes e itens, exibindo o nome do cliente, data, status, o total calculado a partir dos itens e o total registrado no pedido.

### Function criada

- **`fn_calcular_desconto(p_valor NUMERIC)`**: retorna um desconto de 10% para compras acima de R$ 100,00; caso contrário, retorna 0.

### Procedure criada

- **`sp_finalizar_pedido(p_pedido_id INT)`**: percorre os itens do pedido, reduz a quantidade em estoque de cada produto e altera o status do pedido para `CONCLUIDO`.

### Organização dos scripts

```
database/
├── tables/       scripts de criação das tabelas
├── views/        scripts das views
├── functions/    scripts das functions
├── procedures/   scripts das procedures
└── inserts/      scripts de inserção de dados
```

## Como executar

### Pré-requisitos

- JDK 8 ou superior
- PostgreSQL (e, opcionalmente, pgAdmin)

### 1. Criar o banco de dados

Crie um banco chamado `sistema_vendas` no PostgreSQL e execute os scripts da pasta `database/` **nesta ordem**:

1. `tables/` → `01_clientes.sql`, `02_produtos.sql`, `03_pedidos.sql`, `04_itens_pedido.sql`
2. `views/vw_relatorio_pedidos.sql`
3. `functions/fn_calcular_desconto.sql`
4. `procedures/sp_finalizar_pedido.sql`
5. `inserts/` → `01_clientes.sql`, `02_produtos.sql`

### 2. Configurar a conexão

Se necessário, ajuste host, porta, usuário e senha em [`src/database/ConexaoPostgres.java`](src/database/ConexaoPostgres.java) (padrão: `localhost:5432`, usuário `postgres`, senha `root`).

### 3. Executar a aplicação

**No VS Code:** abra a pasta do projeto (com o *Extension Pack for Java* instalado), abra `src/MainApp.java` e clique em **Run**.

**Pelo terminal** (na raiz do projeto):

```bash
javac -cp "lib/postgresql-42.7.3.jar;src" -d bin src/database/ConexaoPostgres.java src/MainApp.java
java -cp "bin;lib/postgresql-42.7.3.jar" MainApp
```

> No Linux ou macOS, substitua `;` por `:` no classpath.
