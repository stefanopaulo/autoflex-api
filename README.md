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

### Produtos
```bash
POST   /products
GET    /products
GET    /products/{id}
PUT    /products/{id}
DELETE /products/{id}
```

### Associação de Materiais
```bash
POST   /products/{productId}/materials
PATCH  /products/{productId}/materials/{materialId}
DELETE /products/{productId}/materials/{materialId}
```

### Produção Disponível
```bash
GET /products/availableProduction
```

### Matéria-prima
```bash
POST   /rawMaterials
GET    /rawMaterials
GET    /rawMaterials/{id}
PUT    /rawMaterials/{id}
DELETE /rawMaterials/{id}
```

---

## 🗄️ Persistência e Perfis (Spring Profiles)
- O projeto está configurado para operar em diferentes ambientes através de perfis do Spring:

- Perfil de Teste (Padrão no Docker): Utiliza o banco de dados H2 In-Memory. Esta é a forma encontrada para subir o projeto rapidamente, pois não exige nenhuma dependência externa ou configuração de banco de dados.

- Perfil de Desenvolvimento (Dev): Configurado para PostgreSQL.

- As configurações detalhadas estão em src/main/resources/application-dev.properties.

- Para utilizar este perfil, altere a propriedade spring.profiles.active=dev no arquivo application.properties e certifique-se de que uma instância do PostgreSQL esteja rodando localmente.

**Observação:** *A imagem Docker descrita na seção "Como Executar" foi pré-configurada com o Perfil de Teste. Isso garante que a aplicação seja iniciada imediatamente após o clone, sem necessidade de setup manual de banco de dados por parte do avaliador.*

---

## ▶ Como Executar

1. Clone o repositório
```bash
git clone git@github.com:stefanopaulo/autoflex-api.git
cd autoflex-api
```

2. Build da imagem Docker

```bash
docker build -t autoflex-api .
```

3. Executar o container
```bash
docker run -p 8080:8080 --name autoflex-api autoflex-api
```

4. Acesso e Testes
   - Após o container subir, a aplicação estará disponível em http://localhost:8080

- Documentação interativa
```bash
http://localhost:8080/swagger-ui/index.html
```

- Exemplo de Paginação (Para findAll de Product e RawMaterial)
```json
{
  "page": 0,
  "size": 10,
  "sort": ["name"]
}
```

- H2 console
```bash
http://localhost:8080/h2-console
```
  - JDBC URL: jdbc:h2:mem:testdb

  - User: sa | Password: (em branco)

---

## 👨‍💻 Autor

**Stefano Souza**
*Desenvolvedor Java focado em construção de APIs REST bem estruturadas e aplicação de boas práticas de arquitetura.*
