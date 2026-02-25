# Core Tech Store

Projeto de e-commerce desenvolvido para simular o funcionamento de uma loja virtual de produtos eletrônicos, contemplando navegação, compra, avaliação de produtos e gestão administrativa.

## 📋 Sobre o Projeto

A aplicação oferece:

- ✅ Registro e autenticação de usuários
- ✅ Catálogo completo de produtos com imagens e descrições detalhadas
- ✅ Pesquisa por nome do produto
- ✅ Filtro por categoria
- ✅ Ordenação por data de cadastro, preço, número de avaliações e nota média
- ✅ Sistema de avaliações com comentários e classificação por estrelas
- ✅ Carrinho de compras com controle de itens
- ✅ Histórico de compras do usuário
- ✅ Dashboard administrativo com CRUD de produtos
- ✅ Gerenciamento completo de pedidos com alteração de status
- ✅ Importação de produtos via arquivo CSV
- ✅ Interface responsiva e experiência otimizada para desktop e mobile

## 🔗 Repositórios Relacionados

- [Frontend](https://github.com/ViniAvemaria/coretech-project-frontend)

## 🌐 Acesse a Aplicação

A aplicação está disponível em: [Core Tech Store](https://app.coretechstore.dedyn.io/)

O projeto está hospedado em ambiente cloud com a seguinte arquitetura:

- Frontend: [Vercel](https://vercel.com/)
- Backend: [Render](https://render.com/)
- Banco de Dados: [Neon](https://neon.com/)

## 📊 Status do Serviço

Acompanhe o status em tempo real: [Status da Core Tech Store](https://status.coretechstore.dedyn.io/)

# Backend

Camada responsável por fornecer a API REST para o frontend, gerenciando autenticação, produtos, pedidos e usuários. Estruturado por responsabilidade, com separação em controllers, services, repositories e suporte a segurança via JWT e validação de dados.

## 🔧 Tecnologias Utilizadas

- **Java 21** – Linguagem principal da aplicação
- **Spring Boot** – Framework para construção da API REST
- **Spring Security** – Autenticação, autorização e segurança da API
- **JWT** – Access e Refresh tokens para gerenciamento de sessão
- **Spring WebMVC** – Desenvolvimento de endpoints REST
- **Spring WebFlux** – Suporte a programação reativa e endpoints não bloqueantes
- **Spring Data JPA** – Persistência e gerenciamento de dados
- **PostgreSQL** – Banco de dados relacional principal
- **H2** – Banco de dados em memória para testes
- **Apache POI** – Importação de produtos via arquivo Excel/CSV
- **Spring Mail** – Envio de e-mails para confirmação de conta
-  **Boot Actuator** – Monitoramento da aplicação
- **Lombok** – Redução de boilerplate em Java
- **SpringDoc OpenAPI** – Documentação interativa da API
- **Spring Boot DevTools** – Facilita desenvolvimento com reload automático
- **dotenv (springboot4-dotenv)** – Gerenciamento de variáveis de ambiente

## 🏗️ Arquitetura

A aplicação backend é organizada por responsabilidade e camadas.

- `advice` centraliza o tratamento global de exceções e formatação de respostas.
- `configuration` contém configurações gerais da aplicação, incluindo segurança (`security`) e filtros JWT.
- `controller` define os endpoints da API e recebe as requisições do frontend.
- `dto` armazena os objetos de transferência de dados, separados entre `request` e `response`.
- `entity` contém as entidades que representam tabelas no banco de dados.
- `exception` define exceções personalizadas usadas na aplicação.
- `repository` fornece interfaces de acesso ao banco de dados via Spring Data JPA.
- `service` implementa a lógica de negócio da aplicação.
- `specs` contém especificações JPA (`Specification`) para consultas dinâmicas, como filtros de produtos por categoria ou pesquisa por nome.

Essa organização garante separação clara de responsabilidades, facilita manutenção e escalabilidade.

## 📁 Estrutura de Pastas

```
src/main/java/com/vinicius/coretech/
├── advice
├── configuration
│   └── security
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── exception
├── repository
├── service
└── specs
```

## 🔐 Autenticação e Segurança

A API utiliza Spring Security para gerenciar autenticação e autorização, combinando JWT, roles e cookies HTTP-only.

- **Endpoints públicos:** registro, login, refresh token, confirmação de e-mail, recuperação de senha, H2 console, 
Swagger e health check.
- **Controle de acesso por roles:** apenas usuários com role `ADMIN` podem criar, atualizar ou deletar produtos e 
  alterar 
status de pedidos.
- **JWT com RSA:** Access e Refresh tokens são assinados com chave RSA, validados via `JwtDecoder` e codificados via 
`JwtEncoder`.
- **Extração de roles:** `JwtAuthenticationConverter` transforma claims de roles em authorities para autorização.
- **Bearer token via cookie:** tokens são enviados pelo frontend em HTTP-only cookies, aumentando a segurança contra 
  XSS.
- **CORS configurado:** apenas o frontend permitido, com métodos e headers seguros.
- **Stateless:** não há sessão de servidor; a API depende apenas dos tokens JWT.

O frontend utiliza este esquema para enviar requisições autenticadas e renovar automaticamente a sessão via refresh token.

## 🗄️ Banco de Dados

Para desenvolvimento, a API utiliza H2 com persistência em arquivo:

```properties
# H2 em arquivo (com persistência de dados)
spring.datasource.url=jdbc:h2:file:./data/coretech-db

# H2 em memória (sem persistência, apenas para testes)
# spring.datasource.url=jdbc:h2:mem:coretech-db
```

Banco de dados disponível em: [H2-Console](http://localhost:8080/api/h2-console/)

Para produção com **PostgreSQL**, crie um arquivo .env com as variáveis:

```env
DATABASE_URL=postgres://<HOST>:<PORT>/<DB_NAME>
DATABASE_USER=<USERNAME>
DATABASE_PASSWORD=<PASSWORD>
```

Após o banco estar ativo, execute o SQL abaixo para criar as roles e o usuário admin:

```SQL
INSERT INTO roles (authority, created_at)
VALUES
('ADMIN', NOW()),
('USER', NOW());

INSERT INTO users (first_name, last_name, email, password, enabled, created_at)
VALUES ('Admin', 'CoreTech', 'admin@email.com', '$2a$10$Kh4sesJXSrx8LYQPpMlJ0uHEoq8R797orhHrsU5aiHQjB88vOcrNW', TRUE, NOW());

INSERT INTO user_role_junction (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, roles r
WHERE u.email = 'admin@email.com'
AND r.authority IN ('ADMIN', 'USER');
```

> Com este SQL, o usuário admin será `admin@email.com` com senha `password`.
Para alterar a senha inicial, criptografe o valor desejado com [bcrypt](https://bcrypt-generator.com/) usando 10 rounds de hash.

## ▶️ Como Executar o Projeto

Acesse a documentação da API em: [Swagger UI](http://localhost:8080/api/swagger-ui/swagger-ui/index.html)

**1. Clone o repositório**

```bash
git clone https://github.com/ViniAvemaria/coretech-project-backend
```
**2. Abra o projeto no IntelliJ**

> Recomenda-se IntelliJ para melhor suporte ao Spring Boot e Maven.

**3. Configure o perfil de execução**

O projeto possui dois perfis: `dev` e `prod`.

Defina a variável de ambiente na IDE:

```env
SPRING_PROFILES_ACTIVE=dev
```

Isso usará `application-dev.properties`, que ativa:

- Banco de dados H2 (com persistência em file)
- Swagger UI
- Configurações padrão de portas: frontend em 5173 e backend em 8080

>Se estiver usando portas diferentes, altere no application-dev.properties:

```properties
app.frontend-base-url=http://localhost:5173
app.backend-base-url=http://localhost:8080
```

**4. Configure as variáveis de ambiente no .env**

```env
DATABASE_URL=postgres://<user>:<password>@<host>:<port>/<db_name>
DATABASE_USER=<username>
DATABASE_PASSWORD=<password>
MAIL_API=<chave_mailtrap>
```

>É utilizo o Mailtrap via API para envio de emails de confirmação de cadastro, alteração de email ou senha.
Para usar o serviço, é necessário ter uma conta Mailtrap e informar a chave da API em MAIL_API.
Caso não queira utilizar emails, defina em `application.properties`: `email.service.enabled=false`.

**5. Execute o projeto**

No IntelliJ, execute a classe `CoreTechApplication`

O backend estará disponível em:

```
http://localhost:8080/api
```

>Certifique-se de que o frontend também esteja rodando se for testar a aplicação completa.
