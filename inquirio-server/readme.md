
<img src="./icons/inquirio.png" width="200">

### Inquirio Server - Technologies Mobiles CEM

### Deploiement

Votre numéro d’étudiant pour le cours est le :  `15`

Votre numéro de port est : `7015`

Url : http://5a5.di.college-em.info:7015

#### Connexion ssh

Votre compte est `etudiant15`.

#### Déploiement

- Sélectionner l’ensemble du répertoire de votre projet (au niveau où se trouve le fichier pom.xml)
- Copier le dans votre répertoire personnel (`/home/etudiant15`) sur le serveur Web `scp://5a5.di.college-em.info`

> Toutes les applications roulent sur le même serveur. Pour les distinguer, vous avez chacun un numéro de port attitré. Ce numéro de port doit être modifié dans le fichier pom.xml de votre projet. Vous devrez spécifier le port `7015`. 

Une fois connecté en SSH, les commandes suivantes servent à :

Démarrer le serveur : depuis le répertoire où se trouvent votre fichier pom.xml, exécutez la commande 

```sh
mvn tomcat7:run &
```

Arrêter le serveur :

```sh
killall java
```




