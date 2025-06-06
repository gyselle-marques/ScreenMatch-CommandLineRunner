<h1 align="center"> :tv: Screen Match (Versão CommandLineRunner) :tv: </h1>

<p align="center">Screen Match é um projeto Java desenvolvido para consumir e processar dados de séries de TV, utilizando integração com APIs externas e recursos de tradução automática. O objetivo é facilitar a busca, análise e exibição de informações sobre séries, temporadas e episódios em linha de comando.</p>

## :gear: Funcionalidades
- Consumo de APIs para obtenção de dados de séries.
- Conversão e modelagem de dados em objetos Java.
- Tradução automática de textos utilizando serviços externos (Chat GPT ou [MyMemory API](https://mymemory.translated.net/doc/spec.php)).
- Organização dos dados em categorias, temporadas e episódios.

## :pushpin: Estrutura do Projeto
O projeto segue a estrutura padrão do Spring Boot, com os principais pacotes:
- `model`: Modelos de dados (Serie, Episodio, Temporada, etc.).
- `repository`: Repositórios para manipulação de dados.
- `service`: Serviços para consumo de APIs, conversão de dados e tradução.
- `main`: Classe principal de execução, implementando a interface `CommandLineRunner` para inicialização automática de rotinas ao iniciar a aplicação.

## :arrow_forward: Como Executar
1. Certifique-se de possuir o Java 17+ instalado.
2. Clone o repositório e acesse a pasta do projeto.
3. Execute o comando:
   ```bash
   ./mvnw spring-boot:run
   ```
4. O projeto será iniciado e estará pronto para uso.

## :computer: Dependências
- Spring Boot
- Spring Data JPA
- Jackson Databind
- OpenAI-Java
- PostgreSQL (ou outro banco de dados relacional)

## :pencil2: Contribuição
Sinta-se à vontade para abrir issues ou enviar pull requests para melhorias.

## :page_facing_up: Licença
Este projeto está sob a licença MIT.
