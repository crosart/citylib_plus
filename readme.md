<p align="center">
<img src="https://upload.wikimedia.org/wikipedia/fr/0/0d/Logo_OpenClassrooms.png" alt="OC logo">
</p>

### Projet OpenClassrooms - Parcours Développeur d'application Java
# BIBLIOTHEQUE


##### Objectif : développer le nouveau système d’information de la bibliothèque d’une grande ville.

<hr>

## CONTEXTE
#### Fonctionnalités :
- Un utilisateur doit pouvoir rechercher des ouvrages et voir le nombre d’exemplaires disponibles.
- Un usager doit pouvoir consulter ses prêts en cours. Les prêts sont pour une période de 4 semaines.
- Un usager doit pouvoir prolonger un prêt en cours. Le prêt d’un ouvrage n’est prolongeable qu’une seule fois. La prolongation ajoute une nouvelle période de prêt (4 semaines) à la période initiale.

#### Contraintes fonctionnelles :
- API web en REST ou en SOAP : les clients (site web, batch, logiciel pour les personnels, application mobile) communiqueront à travers une API web. Factorisation de la logique métier.
- Application web avec un framework MVC (Spring MVC, Struts, …).
- Packaging avec Maven.

<hr>

## DEPLOIEMENT
- La source du *projet* est hérbergée sur ***GitLab*** à l'adresse : *https://gitlab.com/crosart/city-library*
- L'*application* est accessible pour test à l'adresse : *https://citylib.crosart.dev*
  - Le déploiement a été effectué grâce à ***Docker*** sur ***Google Cloud Platform*** via ***Kubernetes Engine***.
- Le *projet* est géré par ***Apache Maven***.
- Les *services de l'application* se buildent par Maven au format ***.jar***.
- Les *frameworks* utilisés sont :
  - ***Spring Boot*** / ***Spring JPA***
  - ***Apache Struts 2***
  - ***Bootstrap 4***
- L'*application* s'appuie sur les services :
  - ***Spring Cloud Config*** pour distribuer les configurations de chaque service.
  - ***Netflix Eureka*** pour le routage des requêtes entre services.
  - ***Swagger2*** pour une vue globale des *endpoints* du backend.
- La *base de données* est déployée sur un serveur ***PostgreSQL 12***.
- L'*application* est déployée sur un serveur ***Apache Tomcat v9***.
- Chaque *service* embarque un Dockerfile pour un possible déploiement containerisé grâce à ***Docker***.
- Pour le service "*citylib-services*", il est nécessaire de saisir en variable d'environnement :
  - DB_URL = l'adresse du serveur PostgreSQL au format >> *jdbc:postgresql://(host):(port)/(db_name)*.
  - DB_USER = le nom d'utilisateur de la connexion à la base de données.
  - DB_PASS = le mot de passe de connexion à la base de données.
- Pour le service "*citylib-batch*", il est nécessaire de saisir en variable d'environnement :
  - citylib_smtpUser = le nom d'utilisateur de la connexion au serveur SMTP à utiliser.
  - citylib_smtpPass = le mot de passe de connexion au serveur SMTP à utiliser.
  - citylib_smtpHost = l'adresse de l'hôte du serveur SMTP à utiliser.
  - citylib_smtpPort = le port du serveur SMTP à utiliser.
- Chaque service est câble par défaut pour chercher le serveur Netflix Eureka Discovery à l'adresse *http://localhost:8761/eureka*, cela peut être modifié par l'ajout en variable d'environnement de EUREKA_SERVER lors du déploiement.

## RESSOURCES
- Le *script SQL* nécessaire pour créer la base de données, avec un jeu de données de test, sur un serveur *PostgreSQL 12* est située : "*/DEPLOYMENT RESOURCES/create_db*.***sql***"
- Le jeu de données contient :
    - Une liste de livres afin de visualiser les fonctionnalités et l'articulation du "*site/application*".
    - Une liste d'emprunts en cours.
    - Pour une simplification du déploiement, la même base embarque également une table de rôles basique pour préparer les Releases suivantes (Il n'y a pas de déifférenciation au niveau des rôles sur l'API actuelle et le site web).
    - Une liste d'utilisateurs de test, en particulier un utilisateur permettant de visualiser la page "*MES EMPRUNTS*" :
      - Mail : *user@gmail.com*
      - Mot de passe : *password*
