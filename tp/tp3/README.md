# TP3

```
.
+-- linux_config
|   +-- interfaces
|   +-- resolv.conf
|   +-- TP3Server.sh : init script (à placer dans /etc/init.d)
+-- tp3server
|   +-- python-404.png : image pour la page d'erreur 404
|   +-- python-art.png : image retourné par le test3
|   +-- TP3Server.py : code pour le serveur CherryPy (test1, test2, test3)
|   +-- tp3_server.conf : configuration du serveur CherryPy (listen on 0.0.0.0:80)
```

Les logs du serveur sont enregistrés dans `requests.log` et `errors.log`.

Le NetworkManager se désactive automatiquement pour les interfaces définis dans `/etc/network/interfaces`.

Nous avons pas nécessairement installé de serveur FTP étant donnée qu'il est possible d'utiliser SSH/SCP.
