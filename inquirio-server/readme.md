
<img src="../icons/inquirio.png" width="200">

### Inquirio Server - Technologies Mobiles CEM


#### Déploiement

- Sélectionner l’ensemble du répertoire de votre projet (au niveau où se trouve le fichier pom.xml)
- Copier le dans votre répertoire personnel

> Le numéro de port doit peut-être être modifié dans le fichier pom.xml de votre projet. 

Une fois connecté en SSH, les commandes suivantes servent à :

Démarrer le serveur : depuis le répertoire où se trouvent votre fichier pom.xml, exécutez la commande 

```sh
mvn tomcat7:run &
```

Arrêter le serveur :

```sh
killall java
```




