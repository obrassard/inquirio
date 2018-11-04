
<img src="./icons/inquirio.png" width="200">

### Version Android 1.0.0 - Technologies Mobiles CEM

### Inquirio est une application de recherche communautaire d'objets perdus pour Android. En effet, un objet égarés peut être déclaré comme perdu dans Inquirio avec son emplacement approximatif, puis la communauté est avertie de la perte et se met en oeuvre pour retrouver votre objet ! [Obtenir plus de details...](https://projects.obrassard.ca/inquirio/)

### Informations importantes

1. L'application utilise une base de donnée MySQL hébergée sur un serveur distant. Le projet est configuré correctement pour se connecter à la base de donnée. Cependant, en executant le servlet en local au cégep, il semblerait que le port 3306 soit bloqué alors ça ne fonctionne pas. 

    1.1 Actuellement l'application est configurée pour se connecter au serveur (5a5...) et de là, il n'y a pas de problème de connexion a mon serveur de BD. Je te conseille donc de tester à partir de la version du servlet sur le serveur du cours.

2. L'activité principale permet de voir les items perdus dans une rayon d'un KM de la position de l'appareil. Ainsi, si on ajoute un nouvel item perdu à un emplacement qui est situé à plus de 1 km de la postion, il n'apparaitra pas dans la liste sur le 'MainActivity'. Il est cependant possible de voir tous les items qu'un utilisateur à déclaré comme perdu, dans l'activité *"Mes items"*

3. En cliquant sur *Ajouter une photo* dans l'activité d'item retrouvé. **La première fois que l'intent de prise de photo est lancé** (seulement), l'intent demande la permission d'utiliser la caméra. Pour une raison inconue, une fois l'autorisation accordée, l'intent ne retourne plus à l'application.
Il faut donc autoriser la caméra (si ce n'est pas déjà fait), revenir manuellement à l'app et cliquer une seconde fois sur "Ajouter une photo". 

4. Pour contacter un utilisateur qui aurais retrouvé un item, l'application utilise un intent d'envoie de SMS. Cependant, comme les tablettes n'ont pas d'application de messagerie, je te conseille d'installer l'app `Pulse`, j'ai testé et ça fonctionne.


### Exemple d'utilisation

1. L'utilisateur **A** déclare un objet perdu au *Cégep Édouard-Montpetit*
    - Un utilisateur ne peut pas dire qu'il a lui même retrouvé son objet, il peut cependant supprimer sa demande

2. L'utilisateur **B**, qui se situe à moins de 1km du *Cégep Édouard-Montpetit* voit l'objet perdu de l'utilisateur **A**

3. Supposons que l'utilisateur **B** pense avoir trouvé l'objet perdu de **A**, il peut lui envoyer une notification pour le lui indiquer en cliquant sur "J'ai trouvé cet item !"

    - L'utilisateur **B** doit ensuite prendre une photos de l'objet pour que l'utilisateur **A** puisse confirmer que c'est bien son objet

4. L'utilisateur **A** retrouvera ensuite la notification de l'utilisateur **B** (dans sa section *notifications*). Il pourra ainsi voir l'image de l'objet proposé par **B** et indiquer si l'objet est bien celui qu'il a perdu

5. Si c'est le cas, l'utilisateur **A** confirme. Il obtient en retour le numéro de cellulaire de l'utilisateur **B** afin de pouvoir le contacter. (L'utilisateur **A** peut retrouver les informations de contact de **B** dans la section 'Vos objets retrouvés' de l'activité *Mes Items*)


### Liens et ressources
[Thème de couleur](http://www.color-hex.com/color-palette/65109)<br>
[Wireframes](https://wireframepro.mockflow.com/view/M1d76315aa4b21aa7f2ff0f4cf4c8ec731535398216705#/page/340b9e8053c947868846c62866efa710)<br>
[Source des icones](https://www.flaticon.com)<br>
[Google PlacePicker](https://developers.google.com/places/android-sdk/placepicker)

