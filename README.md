# 🚀 Autoflex API

## 📌 Visão Geral

Esta aplicação é uma **API REST desenvolvida em Java com Spring Boot** para gerenciamento de:

- Produtos  
- Matérias-primas  
- Associação entre produtos e matérias-primas  
- Cálculo da quantidade máxima de produção com base no estoque disponível
  
---

## 🎯 Requisitos Implementados

### ✔ RF001 – CRUD de Produtos  
### ✔ RF002 – CRUD de Matérias-Primas  
### ✔ RF003 – Associação entre Produtos e Matérias-Primas  
### ✔ RF004 – Consulta de produtos possíveis com base no estoque  

A regra de produção considera a matéria-prima limitante, calculando a quantidade máxima possível com base no estoque disponível.

---

## 🛠 Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Docker
- H2 (perfil de teste)
- Postgres
- JUnit 5
- Mockito
- MockMvc

---

## 🧩 Arquitetura

O projeto segue arquitetura em camadas:

- **Controller** – Exposição dos endpoints REST  
- **Service** – Regras de negócio  
- **Repository** – Acesso a dados com JPA  
- **DTOs** – Separação entre modelo de domínio e contratos da API  
- **Mappers** – Conversão entre entidades e DTOs  
- **Tratamento centralizado de exceções**

---

## ⚙️ Regra de Produção

O cálculo da produção disponível:

- Analisa todas as matérias-primas associadas ao produto  
- Divide o estoque disponível pela quantidade necessária por unidade  
- Considera a matéria-prima limitante (menor resultado)  
- Utiliza `RoundingMode.FLOOR` para evitar produção fracionada  

---

## 🧪 Testes

### ✔ Testes Unitários
- Cobertura das regras de negócio da camada de serviço  
- Uso de Mockito para isolamento de dependências  

### 🔗 Testes de Integração
- Validação de endpoints REST com MockMvc  
- Teste de fluxo completo (Controller → Service → Repository)  
- Execução com profile de teste e banco em memória  

---

## 🌐 Endpoints Principais

### 📦 Produtos
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| **POST** | `/products` | Cadastra um novo produto |
| **GET** | `/products` | Lista todos os produtos (Paginação disponível) |
| **GET** | `/products/{id}` | Busca um produto pelo ID |
| **PUT** | `/products/{id}` | Atualiza os dados de um produto |
| **DELETE** | `/products/{id}` | Remove um produto do sistema |

### 🔗 Associação de Materiais (Composição)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| **POST** | `/products/{productId}/materials` | Associa uma matéria-prima ao produto |
| **PATCH** | `/products/{productId}/materials/{matId}` | Ajusta a quantidade de material na composição |
| **DELETE** | `/products/{productId}/materials/{matId}` | Remove um material da composição do produto |

### ⚙️ Produção e Estoque
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| **GET** | `/products/availableProduction` | Calcula o potencial de produção baseado no estoque atual |

### 🧪 Matéria-prima (Raw Materials)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| **POST** | `/rawMaterials` | Cadastra uma nova matéria-prima |
| **GET** | `/rawMaterials` | Lista todas as matérias-primas |
| **GET** | `/rawMaterials/{id}` | Busca matéria-prima por ID |
| **PUT** | `/rawMaterials/{id}` | Atualiza dados da matéria-prima |
| **DELETE** | `/rawMaterials/{id}` | Remove uma matéria-prima |

---

## 🗄️ Persistência e Infraestrutura

O projeto utiliza **Docker Compose** para orquestrar a aplicação e o banco de dados.

- **Banco de Dados:** PostgreSQL 15 (Docker)
- **Perfil Ativo:** `dev` (configurado para integração automática com o container do banco)
- **Portas Customizadas:**
   - Aplicação: `8085`
   - Banco: `5433`

---

## ▶️ Como Executar

### 1. Clone o repositório

```bash
git clone git@github.com:stefanopaulo/autoflex-api.git
cd autoflex-api
```
### 2. Inicie a aplicação com Docker Compose

```bash
docker-compose up -d --build
```

Esse comando irá:

- Realizar o build da imagem
- Baixar dependências do Maven
- Configurar o banco PostgreSQL
- Subir a API

---

## 🌐 Acesso e Testes

A aplicação estará disponível em:  
👉 http://localhost:8085

---

## 📖 Documentação e Endpoints

### Swagger UI (Interativo)

A documentação completa e a ferramenta de testes podem ser acessadas em:  
👉 http://localhost:8085/swagger-ui/index.html

---

## 📌 Exemplo de Paginação

Para endpoints que listam recursos (como `Product` e `RawMaterial`), utilize parâmetros de URL:

```http
GET /products?page=0&size=10&sort=name
```

---

## 👨‍💻 Autor

**Stefano Souza**
*Desenvolvedor Java focado em construção de APIs REST bem estruturadas e aplicação de boas práticas de arquitetura.*
