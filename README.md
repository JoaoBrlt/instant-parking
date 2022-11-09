# Instant Parking

Temps consacré : 10 heures

## Dépendances

- [Docker Engine](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Lancement 

1. Lancer la base de données et le serveur à l'aide de Docker Compose :

```bash
docker-compose up -d
```

> *Note*: Il est possible que le serveur redémarre plusieurs fois tant que la base de données n'est pas disponible.

2. Le serveur est disponible à l'adresse suivante : http://localhost:8080
3. La documentation de l'API est disponible à l'adresse suivante : http://localhost:8080/swagger-ui/index.html

## Choix réalisés

### 1. Conception d'une application monolithique

Dans le cas d'une petite application, une architecture monolithique offre de nombreux avantages.
Tout d'abord, cela simplifie le développement puisqu'il n'y a qu'une seule base de code.
De plus, cela simplifie le déploiement et l'opération de l'application puisqu'il n'y a qu'un seul service à gérer.
Ces avantages sont d'autant plus importants si l'équipe chargée de l'application est petite.

### 2. Consommation des APIs externes de manière asynchrone

Pour optimiser le nombre de requêtes réseau réalisées, les APIs externes fournissant les informations sur les parkings sont consommées de manière asynchrone.
Les données sur les parkings sont mises à jour à interval régulier (en fonction de la fréquence de mise à jour des différentes APIs externes). 
Par exemple, dans le cas de l'API des parkings de Grand Poitiers, la fréquence de mise à jour est d'une minute.
De cette manière, lorsqu'un utilisateur interroge l'application, celle-ci utilise simplement les informations sur les parkings déjà stockées dans la base de données.

### 3. Utilisation d'une base de données PostgreSQL avec l'extension PostGIS

L'application manipule des données géographiques pour trouver les parkings à proximité.
La manipulation de données géographiques pouvant être gourmande en ressources et complexe, il est judicieux d'utiliser des outils puissants et fiables pour les traiter.
Pour cette raison, l'application utilise une base de données PostgreSQL avec l'extension PostGIS.
Cette extension permet à une base de données PostgreSQL de manipuler des données géographiques sous forme de géométries.
Elle offre notamment des fonctions pour calculer des distances géodésiques, limiter une recherche aux éléments se trouvant dans une zone géographique donnée, etc.

## Problèmes identifiés

### 1. Utilisation d'une requête SQL native pour récupérer les parkings à proximité

Les fonctions de manipulation de données géographiques offertes par PostGIS ne sont actuellement pas prises en charge par Spring Data JPA.
Pour cette raison, l'application utilise pour le moment une requête SQL native pour récupérer les parkings à proximité d'une position donnée (et leur distance par rapport à ce point).
Cela a un impact sur la lisibilité du code et sur la maintenabilité de l'application puisque les champs interrogés sont codés en dur. 
Il serait peut être possible de limiter cet impact en réalisant la requête à l'aide l'API JPA Criteria.

### 2. Quantité conséquente de code spécifique pour la consommation des APIs externes

La consommation des APIs externes génère une quantité conséquente de code spécifique.
Pour faciliter le développement de l'application, il serait envisageable de découper le monolithe en plusieurs modules.
De cette manière, le code spécifique à la consommation de chaque API externe serait bien isolé.
De plus, si les modules sont bien découpés, cela réduit les risques d'impact lorsqu'une modification est apportée.

Il serait également envisageable de découper le monolithe en plusieurs microservices.
Ce découpage entraînerait un surcoût de travail puisqu'il y aurait plus d'artefacts à développer, déployer et opérer.
De plus, il faudrait probablement prévoir un bus de message (Kafka, RabbitMQ, etc.) pour découpler les microservices, assurer une certaine tolérance à la panne, etc.
Cela entraînerait également un surcoût de travail puisque cela ajouterait un autre système à opérer.
Cependant, ces désavantages pourraient être contrebalancés si l'équipe chargée de l'application est suffisamment grande et experte.

### 3. Gestion des parkings supprimés

L'application utilise l'identifiant fourni par chaque API externe pour créer et mettre à jour les informations sur les parkings.
Cependant, elle ne prend pas en charge la suppression des parkings qui ne seraient plus proposés par une API externe (par exemple, dans le cas d'une fermeture définitive).
Cela pourrait être facilement corrigé en identifiant les parkings non proposés par chaque API externe ou par une méthode "annule et remplace".

### 4. Manque d'optimisation de la base de données

L'application pourrait être amenée à évoluer et à consommer de nombreuses APIs externes.
Chacune de ces APIs externes ajouterait des parkings à la base de données.
Dans ce cas, les requêtes de la base de données pourraient devenir plus complexes et plus lentes.
Pour éviter cela, il est possible de mettre en place une indexation spatiale.
Il s'agit d'une des principales fonctionnalités des bases de données spatiales.
L'indexation spatiale accélère la recherche en organisant les données en un arbre de recherche qui peut être parcouru rapidement pour trouver un parking spécifique.

### 5. Manque d'outil de migration de la base de données

Pour l'instant, l'application recrée les différentes tables de la base de données à chaque démarrage.
Cela n'est évidemment pas souhaitable dans un environnement de production.
En effet, dans le cas d'un environnement de production, il faudrait utiliser un outil de migration de base de données (Flyway, Liquibase, etc.).
Ces outils de migration permettent notamment de créer des scripts pour déployer la base de données, d'ordonner les scripts de déploiement, de faciliter les rollbacks en cas d'erreur, etc.
