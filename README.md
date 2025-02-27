[JAVA_BADGE]:https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]:https://img.shields.io/badge/spring-%2382B54B.svg?style=for-the-badge&logo=spring&logoColor=white
[JUNIT_BADGE]:https://img.shields.io/badge/JUnit5-25A162.svg?style=for-the-badge&logo=JUnit5&logoColor=white
[MOCKITO_BADGE]:https://img.shields.io/badge/Mockito-4D4D4D.svg?style=for-the-badge&logo=Mockito&logoColor=white
[DOCKER_BADGE]:https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white

<div align="center">
  <h1 style="font-weight: bold;">Encurtador de URL 🔗</h1>
</div>

![spring][SPRING_BADGE]
![java][JAVA_BADGE]
![docker][DOCKER_BADGE]
![junit][JUNIT_BADGE]
![mockito][MOCKITO_BADGE]

<br>

<p align="center">
  <b>Projeto para o encurtamento de URLs.</b>
</p>

## 🚀 Começando

Este projeto é uma API de encurtamento de URL desenvolvida com Spring Boot, JPA, Lombok, Validation e PostgreSQL. As URLs que são armazenadas são verificadas pelas validações e tem um tempo de expiração definido pelo usuario.

## ⚙️ Tecnologias

- **Linguagem**: Java 17
- **Framework**: Spring Boot (Web, Jpa, Lombok, Validation)
- **Banco de Dados**: PostgreSQL, H2 (testes)
- **Ferramenta de construção**: Maven
- **Testes**: JUnit 5, Mockito
- **Contêinerização**: Docker

## 🔄 Clonando

Clone o projeto usando HTTPS:
```
https://github.com/notAvoiid/short-url.git
```

Ou, se preferir usar SSH:
```
git@github.com:notAvoiid/short-url.git
```

## 🟢 Executando o projeto
```bash
# 1. Navegue até o diretório do projeto:
cd short-url

# 2. (Opcional) Para Linux: Verifica e para o PostgreSQL caso esteja sendo usado em background:
sudo service postgresql stop

# 3. Crie um arquivo na raiz do projeto chamado `.env`:
touch .env 

# 4. Abra o arquivo e substitua pelos valores apropriados:

POSTGRES_DB=${POSTGRES_DB}
POSTGRES_PASSWORD=${POSTGRES_USER}
POSTGRES_USER=${POSTGRES_PASSWORD}

SPRING_DATASOURCE_URL={SPRING_DATASOURCE_URL}
SPRING_DATASOURCE_USERNAME={SPRING_DATASOURCE_USERNAME}
SPRING_DATASOURCE_PASSWORD={SPRING_DATASOURCE_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO={SPRING_DDL_AUTO}
SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE=America/Sao_Paulo

# 5. Altere as configurações no `application.yml:

spring:
  datasource:
    url=${DATABASE_URL}
    username=${DATABASE_USERNAME}
    password=${DATABASE_PASSWORD}

# 6. Faça o build da aplicação:

mvn clean install

# 7. Inicialize o projeto utilizando o docker-compose.yml:

docker compose up -d

```
## 📋 Exemplos de Uso

**Criar URL encurtada:**
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"url": "https://www.example.com", "minutes": 3}' \
http://localhost:8080/api/url
```

**Captura a URL criada:**
```bash
curl -v http://localhost:8080/api/{shortCode}
```

## 📄 Documentação

1. Certifique-se de que o projeto está rodando localmente.
2. Navegue até `http://localhost:8080/swagger-ui/index.html#/` no seu navegador ou clique aqui segurando CTRL: [Swagger](http://localhost:8080/swagger-ui/index.html#/)  

## 📫 Contribuição

Para me ajudar a melhorar o projeto ou me ajudar a melhorar:

1. Clone: `git clone https://github.com/notAvoiid/short-url.git` ou `git clone git@github.com:notAvoiid/short-url.git`
2. Criando sua própria feature: `git checkout -b feature/NAME`
3. Siga os padrões de commit.
4. Abra um Pull Request explicando o problema resolvido ou a feature implementada. Prints com detalhes são importantes!
