# 🏭 Desafio Projedata Informática — Inventory System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-3.31-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-21c_XE-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![React](https://img.shields.io/badge/React-18-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5.0-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-3.4-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)

**Sistema de controle de estoque e sugestão de produção**

[Sobre](#sobre) •
[Tech Stack](#tech-stack) •
[Arquitetura](#arquitetura) •
[Pré-requisitos](#pre-requisitos) •
[Configuração](#configuracao) •
[Executando](#executando) •
[Endpoints](#endpoints) •
[Algoritmo](#algoritmo)

</div>

---

## 📖 Sobre

Sistema desenvolvido como parte do desafio técnico da **Projedata Informática**. A aplicação gerencia **produtos**, **matérias-primas** e suas **associações**, além de oferecer um **algoritmo greedy** que sugere a produção otimizada baseada no estoque disponível, priorizando produtos de maior valor.

### Requisitos Funcionais

| Código | Requisito                                                      | Status |
| ------ | -------------------------------------------------------------- | ------ |
| RF001  | CRUD de Produtos (backend)                                     | ✅     |
| RF002  | CRUD de Matérias-Primas (backend)                              | ✅     |
| RF003  | Associação Produto ↔ Matéria-Prima (backend)                   | ✅     |
| RF004  | Consulta de produtos produzíveis com estoque disponível (backend) | ✅     |
| RF005  | Interface gráfica CRUD de Produtos (frontend)                  | ✅     |
| RF006  | Interface gráfica CRUD de Matérias-Primas (frontend)           | ✅     |
| RF007  | Interface Associação Produto ↔ MP (integrada ao cadastro de produtos) | ✅     |
| RF008  | Interface Sugestão de Produção com quantidades (frontend)      | ✅     |

---

## 🛠 Tech Stack

### Backend

| Tecnologia                  | Versão | Papel                                  |
| --------------------------- | ------ | -------------------------------------- |
| **Java**                    | 17     | Linguagem                              |
| **Quarkus**                 | 3.31.4 | Framework  |
| **Hibernate ORM + Panache** | —      | ORM com Repository Pattern             |
| **Flyway**                  | —      | Versionamento de schema (migrations)   |
| **RESTEasy Reactive**       | —      | REST API                               |
| **Hibernate Validator**     | —      | Bean Validation                        |
| **SmallRye OpenAPI**        | —      | Swagger UI / Documentação automática   |
| **Jackson**                 | —      | Serialização JSON                      |

### Frontend

| Tecnologia          | Versão | Papel                     |
| ------------------- | ------ | ------------------------- |
| **React**           | 19     | Biblioteca de UI          |
| **TypeScript**      | 5.9    | Tipagem estática          |
| **Vite**            | 7.3    | Bundler e dev server      |
| **Tailwind CSS**    | 4.2    | Estilização utilitária    |
| **React Router**    | 7.13   | Navegação do SPA          |
| **Axios**           | 1.13   | Cliente HTTP              |
| **Vitest**          | 4.0    | Testes Unitários          |
| **Lucide React**    | 0.575  | Ícones SVG                |

### Banco de Dados

| Tecnologia          | Versão | Papel                     |
| ------------------- | ------ | ------------------------- |
| **Oracle Database** | 21c XE | Banco de dados relacional |
| **Docker**          | —      | Container para o Oracle   |

---

## 📐 Backend Architecture

O projeto segue a **Layered Architecture** (arquitetura em camadas) com o **Repository Pattern**:

```
com.autoflex/
├── entity/          → Entidades JPA (mapeamento de tabelas)
│   ├── BaseEntity       → Classe abstrata com campos de auditoria (createdAt, updatedAt)
│   ├── Product           → Produto (id, name, value, active)
│   ├── RawMaterial       → Matéria-Prima (id, name, stockQuantity, active)
│   └── ProductRawMaterial → Tabela associativa (product, rawMaterial, quantity)
│
├── repository/      → Panache Repositories (acesso a dados)
│   ├── ProductRepository
│   ├── RawMaterialRepository
│   └── ProductRawMaterialRepository
│
├── dto/             → Data Transfer Objects (records imutáveis)
│   ├── ProductCreateDTO / ProductResponseDTO
│   ├── RawMaterialCreateDTO / RawMaterialResponseDTO
│   ├── ProductRawMaterialDTO
│   ├── ProductionSuggestionDTO
│   └── PageResponseDTO       → Paginação genérica
│
├── mapper/          → Conversão Entity ↔ DTO (mapeamento manual)
│   ├── ProductMapper
│   ├── RawMaterialMapper
│   └── ProductRawMaterialMapper
│
├── service/         → Lógica de negócio e validações
│   ├── ProductService
│   ├── RawMaterialService
│   ├── ProductRawMaterialService
│   └── ProductionSuggestionService  → Algoritmo greedy
│
├── resource/        → REST Controllers (endpoints HTTP)
│   ├── ProductResource              → /api/products
│   ├── RawMaterialResource          → /api/raw-materials
│   ├── ProductRawMaterialResource   → /api/products/{id}/raw-materials
│   └── ProductionSuggestionResource → /api/production/suggestion
│
└── exception/       → Tratamento global de erros
    ├── ErrorResponse               → Response padronizado
    ├── ResourceNotFoundException   → 404
    ├── BusinessException           → 400
    └── GlobalExceptionHandler      → @ServerExceptionMapper
```

### 📐 Frontend Architecture
O front-end utiliza uma estrutura baseada em **Feature-Driven Development** (agrupamento por responsabilidade) com forte separação entre UI, lógica de estado e integração com a API:
```text
frontend/src/
├── assets/          → Imagens, ícones e arquivos estáticos (ex: logos)
├── components/      → Componentes visuais reutilizáveis (dump components)
│   ├── ConfirmDialog    → Modal padrão de confirmação de exclusões
│   ├── Modal            → Wrapper base de modais da aplicação
│   ├── Pagination       → Controle de paginação de listas e tabelas
│   └── Toast            → Sistema de notificações de sucesso/erro
│
├── hooks/           → Custom React Hooks (lógica de estado isolada)
│   └── useProductionSuggestion → Gerencia a complexidade da tela de dashboard
│
├── models/          → Interfaces e Tipagens TypeScript
│   ├── product          → Tipagens (ProductDTO, ProductCreateDTO)
│   ├── raw-material     → Tipagens (RawMaterialDTO)
│   └── suggestion       → Tipagens exclusivas do cálculo de produção
│
├── routes/          → Telas principais e componentes inteligentes (smart components)
│   ├── Dashboard        → Sugestão de Produção (RF004 / RF008)
│   ├── Products         → CRUD de Produtos e Ficha Técnica (RF001 / RF003 / RF005 / RF007)
│   └── RawMaterials     → CRUD de Matérias-Primas (RF002 / RF006)
│
├── services/        → Camada de integração com a API Quarkus (REST)
│   ├── api              → Instância configurada do Axios (URL base, interceptors)
│   ├── product-service  → Endpoints de /api/products
│   └── raw-material-... → Endpoints de /api/raw-materials
│
├── test/            → Configurações unificadas do Vitest/Testing Library
│   └── setup            → Importações do jest-dom global
│
└── utils/           → Funções puras e genéricas
    └── formatCurrency   → Ex: mask para BRL (R$)

### Padrões Aplicados

- ✅ **Repository Pattern** — separação entre entidades e acesso a dados
- ✅ **DTO Pattern** — nunca expõe entidades JPA na API
- ✅ **Soft Delete** — exclusão lógica com coluna `active` + `@SQLRestriction`
- ✅ **Índice Único Funcional** — unicidade de nomes apenas entre registros ativos
- ✅ **Injeção por Construtor** — testabilidade e clareza
- ✅ **Bean Validation** — validações declarativas nos DTOs
- ✅ **Exception Handler Global** — erros padronizados e informativos
- ✅ **Campos de Auditoria** — `createdAt` e `updatedAt` automáticos via `BaseEntity`

### Modelo de Dados

<img width="720" height="608" alt="Screenshot 2026-02-26 164907" src="https://github.com/user-attachments/assets/9a435621-c532-4d42-afbb-2de4529a964b" />

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [**JDK 17**](https://adoptium.net/) — Java Development Kit
- [**Docker Desktop**](https://www.docker.com/products/docker-desktop/) — para rodar o Oracle Database
- [**Git**](https://git-scm.com/) — controle de versão
- **IntelliJ IDEA** (recomendado) ou qualquer IDE Java

> ⚠️ **Não é necessário instalar Maven!** O projeto já inclui o Maven Wrapper (`mvnw`).

---

## 🐳 Configuração do Banco de Dados

Após o banco baixado e configurado

### 1. Criar o usuário do banco de dados

Conecte-se ao Oracle como administrador e crie o usuário da aplicação:

Dentro do Banco, execute:

```sql
-- Criar o usuário da aplicação
CREATE USER desafio_projedata_info IDENTIFIED BY SenhaForte;

-- Conceder permissões necessárias
GRANT CONNECT, RESOURCE TO desafio_projedata_info;
GRANT CREATE SESSION TO desafio_projedata_info;
GRANT UNLIMITED TABLESPACE TO desafio_projedata_info;

-- Sair
EXIT;
```

---

## 🚀 Executando a Aplicação

### 1. Clonar o repositório

```bash
git clone git@github.com:Gabriellealsantos/desafio-projedata-info.git
cd desafio-projedata-info
```

### 2. Executar o Backend em modo desenvolvimento

```bash
# Linux/macOS
./mvnw quarkus:dev

# Windows (PowerShell)
./mvnw.cmd quarkus:dev
```

**O que acontece automaticamente:**

1. ✅ Quarkus compila e inicia a aplicação
2. ✅ Conecta no Oracle Docker (`localhost:1521/FREEPDB1`)
3. ✅ Flyway executa as **5 migrations** (cria tabelas, insere dados de teste, configura soft delete...)
4. ✅ API fica disponível em `http://localhost:8080`
5. ✅ **Swagger UI** fica disponível em `http://localhost:8080/q/swagger-ui`

### 3. Variáveis de Ambiente (opcional)

A aplicação usa valores padrão, mas você pode customizar via variáveis de ambiente:

| Variável      | Padrão                                      | Descrição               |
| ------------- | ------------------------------------------- | ----------------------- |
| `DB_USER`     | `desafio_projedata_info`                    | Usuário do banco Oracle |
| `DB_PASSWORD` | `SenhaForte`                                | Senha do banco Oracle   |
| `DB_URL`      | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` | URL JDBC do Oracle      |

### 4. Executar o Frontend em modo desenvolvimento

Abra um novo terminal na raiz do projeto e acesse a pasta do frontend:

```bash
cd frontend

# Instalar dependências
yarn install

# Iniciar o servidor de desenvolvimento
yarn dev
```

A aplicação React abrirá automaticamente em `http://localhost:5173`.

---

## 📡 Endpoints da API

### Produtos — `/api/products`

| Método   | Rota                           | Descrição                     |
| -------- | ------------------------------ | ----------------------------- |
| `GET`    | `/api/products?page=0&size=10` | Listar todos (paginado)       |
| `GET`    | `/api/products/{id}`           | Buscar por ID                 |
| `POST`   | `/api/products`                | Criar produto                 |
| `PUT`    | `/api/products/{id}`           | Atualizar produto             |
| `DELETE` | `/api/products/{id}`           | Deletar produto (soft delete) |

---

### Matérias-Primas — `/api/raw-materials`

| Método   | Rota                                | Descrição               |
| -------- | ----------------------------------- | ----------------------- |
| `GET`    | `/api/raw-materials?page=0&size=10` | Listar todas (paginado) |
| `GET`    | `/api/raw-materials/{id}`           | Buscar por ID           |
| `POST`   | `/api/raw-materials`                | Criar matéria-prima     |
| `PUT`    | `/api/raw-materials/{id}`           | Atualizar matéria-prima |
| `DELETE` | `/api/raw-materials/{id}`           | Deletar (soft delete)   |

---

### Composição do Produto — `/api/products/{id}/raw-materials`

| Método   | Rota                                               | Descrição              |
| -------- | -------------------------------------------------- | ---------------------- |
| `GET`    | `/api/products/{id}/raw-materials`                 | Listar MPs do produto  |
| `POST`   | `/api/products/{id}/raw-materials`                 | Vincular MP ao produto |
| `PUT`    | `/api/products/{id}/raw-materials/{rawMaterialId}` | Atualizar quantidade   |
| `DELETE` | `/api/products/{id}/raw-materials/{rawMaterialId}` | Desvincular MP         |

---

### Sugestão de Produção — `/api/production/suggestion`

| Método | Rota                         | Descrição                   |
| ------ | ---------------------------- | --------------------------- |
| `GET`  | `/api/production/suggestion` | Calcular sugestão otimizada |

---

## 🗂 Migrations do Banco de Dados

O Flyway gerencia a evolução do schema automaticamente:

| Migration                             | Descrição                                                                  |
| ------------------------------------- | -------------------------------------------------------------------------- |
| `V1__create_tables.sql`               | Criação das tabelas `product`, `raw_material`, `product_raw_material`      |
| `V2__seed_data.sql`                   | Dados de teste: 10 matérias-primas, 5 produtos, 27 associações             |
| `V3__add_soft_delete.sql`             | Adiciona coluna `active` para exclusão lógica                              |
| `V4__add_audit_columns.sql`           | Adiciona colunas `created_at` e `updated_at`                               |
| `V5__fix_unique_name_soft_delete.sql` | Índice único funcional (permite nomes duplicados entre registros inativos) |

---

## 🧪 Rodando os Testes

### Backend (Quarkus)

```bash
# Na raiz do projeto
# Testes unitários
.\mvnw.cmd test

# Testes de integração
.\mvnw.cmd verify
```

### Frontend (React/Vitest)

```bash
# Entre na pasta do frontend
cd frontend

# 1. Instale as dependências do projeto (Obrigatório)
yarn install
yarn add -D @testing-library/dom

# 2. Rodar todos os testes unitários em modo headless
yarn test
---
````

## 👤 Autor

**Gabriel Santos**

[![GitHub](https://img.shields.io/badge/GitHub-Gabriellealsantos-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Gabriellealsantos)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
