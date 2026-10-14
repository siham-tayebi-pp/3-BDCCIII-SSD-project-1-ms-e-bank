# Compte rendu : Projet eBank (architecture microservices)

## 1. Présentation du projet eBank

Dans ce projet, nous allons développer une application **eBank basée sur une architecture microservices**.

L'application sera composée de deux microservices métier principaux :

* **Customer Service** : ce microservice sera responsable de la gestion des clients (*Customers*).
* **eBank Service** : ce microservice sera responsable de la gestion des comptes bancaires (*Accounts*).

### Communication entre les microservices

Une relation existe entre les deux microservices, puisqu'un **compte bancaire appartient à un Customer**.

Pour permettre aux deux microservices de communiquer entre eux, nous allons utiliser une **communication REST synchrone** basée sur **OpenFeign**.

OpenFeign permettra à un microservice d'appeler facilement les endpoints REST de l'autre microservice lorsqu'une interaction entre les deux services sera nécessaire.

### Infrastructure des microservices

Pour gérer l'architecture distribuée et faciliter l'accès aux différents services, nous allons mettre en place plusieurs composants :

* **API Gateway** : point d'entrée vers les microservices.
* **Discovery Service** : permet aux microservices de s'enregistrer et de se découvrir dynamiquement.
* **Config Service** : permet de centraliser et de gérer la configuration des différents microservices.

Une fois ces différents composants créés, nous allons tester l'ensemble de l'architecture à travers un **frontend Angular**.

### Intégration de Spring AI et création du chatbot

Après avoir mis en place l'architecture de base, nous allons intégrer **Spring AI** afin de créer un nouveau microservice appelé **eBank Chatbot Service**.

Ce chatbot sera basé sur un **LLM (Large Language Model)** et permettra aux utilisateurs de poser des questions concernant leurs clients, leurs comptes et les fonctionnalités disponibles dans l'application eBank.

L'utilisateur pourra envoyer une question depuis le **frontend Angular** via le protocole **HTTP**.

Le chatbot devra alors :

1. Recevoir et analyser la question de l'utilisateur.
2. Comprendre l'intention de la question.
3. Déterminer quel microservice doit être contacté pour obtenir les informations nécessaires.
4. Appeler le service approprié.
5. Récupérer les données nécessaires.
6. Générer une réponse pertinente à partir des informations récupérées.
7. Retourner la réponse à l'utilisateur.

### Utilisation de MCP

Pour permettre au chatbot d'interagir avec les différents services, nous allons utiliser le **Model Context Protocol (MCP)**.

Nous utiliserons notamment le **transport Streamable HTTP** afin de permettre la communication entre le chatbot et les serveurs MCP.

Le point le plus important du chatbot sera sa capacité à **déterminer quel service doit être contacté en fonction de la question de l'utilisateur**.

Par exemple :

* Une question concernant les informations d'un client → **Customer Service**.
* Une question concernant un compte bancaire → **eBank Service**.
* Une question nécessitant des informations provenant des deux services → le chatbot devra être capable d'interagir avec les deux services.

Le chatbot ne devra donc pas simplement générer une réponse à partir du LLM : il devra également être capable de **sélectionner et d'utiliser les bons outils/services** afin de produire une réponse basée sur les données réelles de l'application.

### Intégration de Telegram

Enfin, nous allons intégrer un **client Telegram** afin de permettre aux utilisateurs d'interagir avec le chatbot directement depuis Telegram.

L'utilisateur pourra poser ses questions directement dans Telegram.

Dans ce cas, les requêtes passeront par **l'API Telegram** et pourront communiquer avec le **Chatbot Service sans passer directement par l'API Gateway** utilisée par le frontend Angular.

Le chatbot recevra donc les questions provenant de deux sources :

* **Frontend Angular → HTTP → Chatbot Service**
* **Telegram → Telegram API → Chatbot Service**

Dans les deux cas, le chatbot devra analyser la question, déterminer le ou les services nécessaires, récupérer les informations appropriées et générer la réponse finale.

### Architecture globale

L'architecture finale du projet sera donc composée des éléments suivants :

```text
                         ┌──────────────────┐
                         │   Angular        │
                         │    Frontend      │
                         └────────┬─────────┘
                                  │ HTTP
                                  ▼
                         ┌──────────────────┐
                         │   API Gateway    │
                         └────────┬─────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             │                    │                    │
             ▼                    ▼                    ▼
      ┌─────────────┐      ┌─────────────┐     ┌──────────────┐
      │   Customer  │      │   eBank     │     │   Chatbot    │
      │   Service   │◄────►│   Service   │     │   Service    │
      └─────────────┘      └─────────────┘     └───────┬──────┘
             │                    │                    │
             │                    │                    │ MCP
             │                    │                    ▼
             │                    │             ┌──────────────┐
             │                    │             │ MCP Servers  │
             │                    │             └──────────────┘
             │                    │
             └────────────┬───────┘
                          │
                   Discovery Service
                   Config Service


      ┌─────────────────┐
      │    Telegram     │
      │     Client      │
      └────────┬────────┘
               │
               ▼
        ┌──────────────┐
        │ Telegram API │
        └──────┬───────┘
               │
               ▼
        ┌──────────────┐
        │   Chatbot    │
        │   Service    │
        └──────────────┘
```

L'objectif final est donc de construire une **application eBank moderne basée sur les microservices**, puis d'y intégrer un **chatbot intelligent basé sur Spring AI et un LLM**, capable de comprendre les questions des utilisateurs, de sélectionner automatiquement les services nécessaires via **MCP**, de récupérer les données appropriées et de générer une réponse contextualisée, avec une interaction possible à la fois depuis **Angular et Telegram**.

---

## 2. Création du projet parent

Voici le projet que nous avons créé, sans aucune dépendance. Ensuite, nous allons y ajouter les microservices un par un.

![img.png](images/img.png)

---

## 3. Customer Service

### 3.1 Création du microservice

![img_2.png](images/img_2.png)

**Dépendances :**

| Dépendance | Rôle |
|---|---|
| Spring Web | Création de l'API REST |
| Spring Data JPA | Accès aux données |
| H2 Database | Base de données en mémoire |
| Lombok | Réduction du code répétitif (getters, setters, constructeurs, builder) |
| Eureka Discovery Client | Permet au microservice de **s'enregistrer** auprès du Discovery Service |
| Config Client | Permet au microservice de **chercher sa configuration** auprès du Config Service |
| Actuator | **Monitoring** du microservice |

> La dépendance **MCP** sera ajoutée plus tard, lorsque nous arriverons à la partie Chatbot.

![img_1.png](images/img_1.png)

### 3.2 Structure des packages

On crée les packages suivants :

* `entities` : `Customer`
  ![img_3.png](images/img_3.png)
* `repositories` : `CustomerRepository`
  ![img_4.png](images/img_4.png)
* `services` : `CustomerService`
  ![img_5.png](images/img_5.png)
* `controllers` : `CustomerRestController`
  ![img_6.png](images/img_6.png)

### 3.3 Entité `Customer`

```java
package net.tayebi.customerservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
}
```

### 3.4 Repository `CustomerRepository`

```java
package net.tayebi.customerservice.repositories;

import net.tayebi.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
```

### 3.5 Service `CustomerService`

```java
package net.tayebi.customerservice.services;

import net.tayebi.customerservice.entities.Customer;
import net.tayebi.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Customer with id %s not found", id)));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
```

### 3.6 Contrôleur `CustomerRestController`

```java
package net.tayebi.customerservice.controllers;

import net.tayebi.customerservice.entities.Customer;
import net.tayebi.customerservice.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public Customer findCustomerById(@PathVariable Long id) {
        return customerService.findCustomerById(id);
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }
}
```

**Différence entre `@PathVariable` et `@RequestBody` :**

* `@PathVariable` → la donnée se trouve **dans l'URL** (ex. `/customers/3`).
* `@RequestBody` → la donnée (l'objet) se trouve **dans le corps de la requête**, et non dans l'URL.

**Gestion des exceptions :**

* `@ControllerAdvice` (ou `@RestControllerAdvice`) permet de **gérer les exceptions de manière centralisée**.
* On peut aussi créer des `@ExceptionHandler` : lorsqu'une exception est levée, on l'intercepte et on renvoie une réponse HTTP appropriée (ex. 404 au lieu d'une erreur 500).

### 3.7 `CommandLineRunner` : initialisation des données

On ajoute un bean qui sert à créer nos customers au démarrage :

```java
package net.tayebi.customerservice;

import net.tayebi.customerservice.entities.Customer;
import net.tayebi.customerservice.services.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(CustomerService customerService) {
        return args -> {
            List<String> names = List.of("siham", "imane", "omar", "ali", "fouzia");
            names.forEach(name -> {
                customerService.createCustomer(
                        Customer.builder()
                                .name(name)
                                .email(name + "@gmail.com")
                                .build()
                );
            });
        };
    }
}
```

### 3.8 Configuration (`application.properties`)

On **désactive** :

* `spring.cloud.config.enabled=false` : sinon le microservice va chercher un service de configuration centralisé (qui n'existe pas encore).
* `eureka.client.enabled=false` : sinon il va essayer de s'enregistrer auprès d'Eureka (qui n'existe pas encore).
* `spring.cloud.config.discovery.enabled=false` : sinon il va essayer de trouver le Config Service via la découverte.

```properties
spring.application.name=customer-service
server.port=8056
spring.cloud.config.discovery.enabled=false
spring.cloud.config.enabled=false
eureka.client.enabled=false
spring.datasource.url=jdbc:h2:mem:customers-db
spring.h2.console.enabled=true
```

### 3.9 Test de l'application

On démarre l'application puis on consulte : <http://localhost:8056/customers>

![img_7.png](images/img_7.png)

Voilà tous nos customers. On peut aussi consulter la base de données via la console H2 : <http://localhost:8056/h2-console>

Et voilà, la console affiche tous nos customers :

![img_8.png](images/img_8.png)

### 3.10 Documentation Swagger

Ensuite, on ajoute la documentation Swagger :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.1.1</version>
</dependency>
```

Et voilà notre documentation de l'API web : <http://localhost:8056/swagger-ui/index.html>

![img_9.png](images/img_9.png)

On teste donc les endpoints :

**GET `/customers`**

![img_10.png](images/img_10.png)

**POST `/customers`**

![img_11.png](images/img_11.png)

**GET `/customers/{id}`**

![img_12.png](images/img_12.png)

---

## 4. eBank Service

On passe à la création de notre **eBank Service**, avec les **mêmes dépendances** que Customer Service.

![img_13.png](images/img_13.png)

On ajoute aussi les packages suivants :

```text
entities
└── BankAccount

repositories
└── BankAccountRepository

services
└── BankAccountService

controllers
└── BankAccountController

model
└── Customer
```

### 4.1 Entité `BankAccount`

```java
package net.tayebi.ebankservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tayebi.ebankservice.model.Customer;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {
    @Id
    private String id;
    private Date createdAt;
    private double balance;
    private String type;
    private Long customerId;
    @Transient
    private Customer customer;
}
```

> `@Transient` : le champ `customer` n'est **pas persisté** en base. Il est rempli dynamiquement via OpenFeign à partir de `customerId`.

### 4.2 Modèle `Customer` (côté eBank Service)

```java
package net.tayebi.ebankservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private Long id;
    private String name;
    private String email;
}
```

### 4.3 Repository `BankAccountRepository`

```java
package net.tayebi.ebankservice.repositories;

import net.tayebi.ebankservice.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    List<BankAccount> findByCustomerId(Long id);
}
```

### 4.4 Service `BankAccountService` (version initiale)

```java
package net.tayebi.ebankservice.services;

import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {

    private final BankAccountRepository accountRepository;

    public BankAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    public BankAccount getBankAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public BankAccount save(BankAccount bankAccount) {
        return accountRepository.save(bankAccount);
    }
}
```

### 4.5 Contrôleur `BankAccountController`

```java
package net.tayebi.ebankservice.controllers;

import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping("/accounts")
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountService.getAllBankAccounts();
    }

    @GetMapping("/accounts/{id}")
    public BankAccount getBankAccountById(@PathVariable String id) {
        return bankAccountService.getBankAccountById(id);
    }

    @PostMapping("/accounts")
    public BankAccount save(@RequestBody BankAccount bankAccount) {
        return bankAccountService.save(bankAccount);
    }
}
```

### 4.6 `CommandLineRunner` : création des comptes

On ajoute un bean dans la classe principale pour créer des comptes (5 comptes pour chacun des 3 premiers customers) :

```java
package net.tayebi.ebankservice;

import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EbankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(BankAccountService bankAccountService) {
        return args -> {
            for (long i = 1; i <= 3; i++) {
                for (int j = 0; j < 5; j++) {
                    bankAccountService.save(
                            BankAccount.builder()
                                    .type(Math.random() > 0.5 ? "CURRENT_ACCOUNT" : "SAVING_ACCOUNT")
                                    .balance(1000 + Math.random() * 60000)
                                    .customerId(i)
                                    .build());
                }
            }
        };
    }
}
```

### 4.7 Configuration d'eBank Service

```properties
spring.application.name=ebank-service
server.port=8057
eureka.client.enabled=false
spring.cloud.config.enabled=false
spring.cloud.config.discovery.enabled=false
spring.datasource.url=jdbc:h2:mem:accounts-db
spring.h2.console.enabled=true
```

On exécute, puis on teste.

### 4.8 Swagger

Avant de tester, on ajoute aussi Swagger :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.1.1</version>
</dependency>
```

![img_14.png](images/img_14.png)

On teste donc notre API.

**Test de `GET /accounts` (get all)**

![img_15.png](images/img_15.png)

**Test de `GET /accounts/{id}` (get by id)**

![img_17.png](images/img_17.png)

---

## 5. API Gateway

On passe à la création de notre **Gateway Service**, avec les dépendances :

* Reactive Gateway
* Discovery Client (Eureka Discovery Client)
* Actuator

![img_16.png](images/img_16.png)

### 5.1 Routage statique

Pour le **routage statique**, on utilise un fichier de configuration `application.yml` :

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: r1
              uri: http://localhost:8056
              predicates:
                - Path=/customers/**
            - id: r2
              uri: http://localhost:8057
              predicates:
                - Path=/accounts/**
```

> Attention : en YAML, l'**indentation** est essentielle, et il ne faut pas d'espace dans `Path=/customers/**`.

On passe ensuite à la configuration de la Gateway, et on **désactive Eureka** car le Discovery Service n'est pas encore créé :

```properties
spring.application.name=gateway-service
server.port=9999
eureka.client.enabled=false
spring.cloud.config.enabled=false
```

Et voilà, on teste. Ça marche : <http://localhost:9999/customers>

![img_18.png](images/img_18.png)

Et ça marche aussi : <http://localhost:9999/accounts>

![img_19.png](images/img_19.png)

---

## 6. Discovery Service (Eureka Server)

Ça, c'était le routage statique. On passe maintenant à la création de notre **Discovery Service**, avec les dépendances :

* Eureka Server
* Actuator

![img_20.png](images/img_20.png)

### 6.1 Configuration (port 8761)

```properties
spring.application.name=discovery-service
server.port=8761
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

On désactive `fetch-registry` et `register-with-eureka` car, si on les active, le serveur Eureka va lui-même essayer de s'enregistrer (et de récupérer le registre) auprès d'un autre serveur Eureka, ce qui est inutile ici.

### 6.2 Activation d'Eureka Server

On active Eureka Server avec `@EnableEurekaServer`, sinon cela ne marche pas :

```java
package net.tayebi.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

On exécute : c'est bon, l'URL fonctionne : <http://localhost:8761/>

![img_21.png](images/img_21.png)

### 6.3 Enregistrement des microservices

Pour le moment, **aucun service n'est enregistré**. Pour cela, on revient à la configuration de chaque microservice (Customer Service, eBank Service, Gateway) et on **active la découverte** :

```properties
eureka.client.enabled=true
```

* `eureka.client.enabled=true` : « je veux utiliser Eureka pour m'enregistrer et pour découvrir les autres services ».
* `spring.cloud.config.discovery.enabled` concerne uniquement la recherche du **Config Service** via Eureka : on le laisse à `false` tant que le Config Service n'est pas créé.

![img_22.png](images/img_22.png)

---

## 7. Communication Customer Service ↔ eBank Service via OpenFeign

Maintenant, on doit établir une communication entre Customer Service et eBank Service via **OpenFeign**, dans notre **ebank-service**.

### 7.1 Dépendances

Eureka Client (pour la découverte) :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Puis on ajoute OpenFeign dans eBank Service :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 7.2 Client Feign `CustomerRestClient`

```java
package net.tayebi.ebankservice.feign;

import net.tayebi.ebankservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    Customer getCustomer(@PathVariable Long id);
}
```

Ici, on envoie une requête avec l'`id`, puis on récupère la réponse et on la stocke dans l'objet `Customer` créé côté eBank Service.

### 7.3 Routage dynamique dans la Gateway

Pour que cela marche, on a besoin de configurer le **système de routage de manière dynamique** : on met la configuration statique du fichier `application.yml` **en commentaire**, puis on crée le bean via la méthode `ReactiveDiscoveryClient` dans la Gateway :

```java
package net.tayebi.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    DiscoveryClientRouteDefinitionLocator routes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp) {
        return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
    }
}
```

Et on redémarre la Gateway. Désormais, <http://localhost:9999/accounts> ne fonctionne plus :

![img_23.png](images/img_23.png)

Il faut maintenant utiliser le **nom du service** dans l'URL. Par défaut, les services s'enregistrent dans Discovery **en majuscules**, d'où :

<http://localhost:9999/EBANK-SERVICE/accounts>

Et ça marche :

![img_24.png](images/img_24.png)

<http://localhost:9999/CUSTOMER-SERVICE/customers>

![img_25.png](images/img_25.png)

> Astuce : avec `spring.cloud.gateway.discovery.locator.lower-case-service-id=true` (ou la propriété équivalente `...server.webflux.discovery.locator...` selon la version), on peut utiliser les noms en minuscules.

### 7.4 Utilisation du client Feign dans eBank Service

On va dans eBank Service et on essaie de récupérer un customer via les méthodes du client REST.

Pour pouvoir **injecter** le client Feign comme un bean dans notre service, on doit ajouter `@EnableFeignClients` dans la classe principale :

```java
@SpringBootApplication
@EnableFeignClients
public class EbankServiceApplication { ... }
```

`@EnableFeignClients` permet l'**injection** des clients Feign. Ensuite, on modifie le service :

```java
package net.tayebi.ebankservice.services;

import lombok.AllArgsConstructor;
import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.feign.CustomerRestClient;
import net.tayebi.ebankservice.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BankAccountService {

    private BankAccountRepository accountRepository;
    private CustomerRestClient customerRestClient;

    public List<BankAccount> getAllBankAccounts() {
        return accountRepository.findAll();
    }

    public BankAccount getBankAccountById(String id) {
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        bankAccount.setCustomer(customerRestClient.getCustomer(bankAccount.getCustomerId()));
        return bankAccount;
    }

    public BankAccount save(BankAccount bankAccount) {
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        return accountRepository.save(bankAccount);
    }
}
```

On teste OpenFeign : on a créé un client REST de manière **déclarative**. Ça marche bien :

<http://localhost:9999/EBANK-SERVICE/accounts/63123a42-365a-4616-9684-6ba57e741a8c>

![img_26.png](images/img_26.png)

Là, on a le compte avec eBank Service, **avec les informations du customer** obtenues à l'aide de Customer Service.

### 7.5 Règle d'intégrité référentielle

On ne doit **pas pouvoir enregistrer un compte si le customer n'existe pas**. Il revient à nous de gérer les règles d'intégrité référentielle :

```java
public BankAccount save(BankAccount bankAccount) {
    // Vérifie que le customer existe (lève une exception sinon)
    Customer customer = customerRestClient.getCustomer(bankAccount.getCustomerId());
    bankAccount.setId(UUID.randomUUID().toString());
    bankAccount.setCreatedAt(new Date());
    return accountRepository.save(bankAccount);
}
```

> Remarque : avec le circuit breaker de la section suivante, la méthode de repli renvoie un customer « par défaut » ; la vérification ne lèvera donc plus d'exception si Customer Service est arrêté. Pour une vraie règle d'intégrité, il faudrait par exemple tester `customer.getName()` ≠ `"Default name"` ou ne pas appliquer de fallback sur la création.

---

## 8. Tolérance aux pannes avec Resilience4j (Circuit Breaker)

On utilise **Resilience4j** pour résoudre le problème de **tolérance aux pannes**.

On ajoute les dépendances **Resilience4j** et **Circuit Breaker** pour que, si un service n'est pas disponible, l'application ne plante pas :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

On ajoute `@CircuitBreaker` sur les méthodes qui peuvent générer des exceptions, afin de les **rediriger vers une méthode par défaut** (fallback) :

```java
package net.tayebi.ebankservice.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.tayebi.ebankservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    @CircuitBreaker(name = "customer-service", fallbackMethod = "defaultCustomerMethod")
    Customer getCustomer(@PathVariable Long id);

    default Customer defaultCustomerMethod(Long id, Exception e) {
        return new Customer(id, "Default name", "default@gmail.com");
    }
}
```

> Points de cohérence : le type du paramètre `id` doit être **le même** (`Long`) dans la méthode et dans le fallback, et la méthode de repli doit avoir la même signature + un paramètre `Exception` (en dernier).

**Test :** on arrête Customer Service pour voir ce qui s'affiche.

![img_27.png](images/img_27.png)

Là, on obtient les **valeurs par défaut**, car le service Customer est arrêté.

On le relance, et tout refonctionne normalement :

![img_28.png](images/img_28.png)

---

## 9. Prochaines étapes

* Config Service (configuration centralisée).
* Frontend Angular.
* eBank Chatbot Service avec Spring AI, MCP (Streamable HTTP) et un LLM.
* Intégration de Telegram.

---

## 10. Récapitulatif des corrections apportées

| # | Endroit | Erreur dans la version d'origine | Correction |
|---|---|---|---|
| 1 | `CustomerRepository`, `CustomerRestController` | Imports inutiles (`Entity`, `Id`, `GeneratedValue`, `lombok.*`, `CustomerRepository`) | Imports supprimés |
| 2 | `application.properties` (Customer) | `spring.application.name` en double | Une seule occurrence |
| 3 | `BankAccount` | Annotation `@Entity` **manquante** (JPA ne la persistait pas) | Ajout de `@Entity` |
| 4 | `BankAccount` | `@Builder` manquant alors que `BankAccount.builder()` est utilisé | Ajout de `@Builder` |
| 5 | `BankAccount.customerId` | Type `long` alors que `Customer.id` est `Long` | Uniformisé en `Long` |
| 6 | `CommandLineRunner` (eBank) | Accolade fermante manquante (code qui ne compile pas) et espace avant `.type (` | Accolades corrigées |
| 7 | `CommandLineRunner` (eBank) | Valeurs incohérentes `"CURRENT-ACCOUNT"` / `"SAVING_ACCOUNT"` | `CURRENT_ACCOUNT` / `SAVING_ACCOUNT` |
| 8 | `application.properties` (eBank) | `spring.cloud.discovery.enabled` (propriété inexistante) | `spring.cloud.config.discovery.enabled` |
| 9 | `application.properties` (eBank) | Pas d'URL de datasource explicite | Ajout de `spring.datasource.url=jdbc:h2:mem:accounts-db` |
| 10 | YAML de la Gateway | Indentation cassée, espaces dans `Path= /customers/**` | YAML valide et `Path=/customers/**` |
| 11 | `GatewayServiceApplication` | Imports erronés/inutiles (`com.netflix.discovery.DiscoveryClient`, `ServerProperties`) | Supprimés |
| 12 | Config de découverte | Commentaires `//` dans un `.properties` (invalide) et rôle de `spring.cloud.config.discovery.enabled` mal décrit | Explications déplacées dans le texte ; seul `eureka.client.enabled=true` sert à l'enregistrement |
| 13 | `CustomerRestClient` | `@PathVariable int id` puis `long id` (types incohérents avec `Long`) | `@PathVariable Long id` partout |
| 14 | Fallback Resilience4j | Signature `(Exception e, Long id)` incompatible avec `long id` ; ordre des paramètres inversé | Signature `(Long id, Exception e)` cohérente avec la méthode |
| 15 | Dépendances Resilience4j | Dépendances non précisées | Ajout de `spring-cloud-starter-circuitbreaker-resilience4j` et `spring-boot-starter-aop` |
| 16 | `BankAccountService.save` | `try/catch` qui relance la même exception (inutile) et variable `customer` non utilisée | Simplifié, avec remarque sur l'interaction avec le fallback |
| 17 | `@EnableFeignClients` | Mentionné mais absent du code | Ajouté dans la classe principale d'eBank Service |
| 18 | Orthographe / formulation | Nombreuses fautes de frappe (« rouage », « sevrice », « cre »…) | Texte reformulé et corrigé |
