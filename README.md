Generic System API module
=========================

What is Generic System ?
------------------------
Generic System (GS) est un moteur d’information **open-source** développé par la société [Middleware Factory](http://www.middlewarefactory.com/).
Ce moteur innovant implémenté en Java est à la fois persistant, transactionnel, concurrentiel et restructurable.
Generic System vient s’intégrer dans l’écosystème des bases de données NoSQL, en mettant en œuvre une logique tout objet, et sans **aucune dépendance à une base de données relationnelle**.

Some of the key highlights include:
* Persistant : Generic System est exécuté en mémoire et possède son propre mécanisme de persistance.
Au démarrage, le moteur de Generic System récupère l’image archivée la plus récente (dans un répertoire prévu à cet effet) et construit le système d’information.
Périodiquement, GS crée des images du système qui constituent des points de restauration.
Enfin, lorsque le moteur s’arrête, il archive une dernière image du système à l’instant d’arrêt.
* Transactionnel : GS est un système d’information transactionnel.
La transaction GS peut être comparée à la transaction d’une base de données relationnelle.
Elle sait lire, écrire, modifier et supprimer les données.  
Toutes les écritures sont exécutées au travers d’une transaction qui veille à ce qu’elles s’exécutent toutes à un même instant t.
La conception interne de Generic System garantit que toute lecture d’un objet ou d’un ensemble d’objets, faite à un instant t, est reproductible.
Cette disposition a permis de garantir l’isolation des transactions au sens des lectures sales, des lectures non reproductibles et des lectures fantômes.  
Pour information les opérations de lecture sont globalement prioritaires sur les opérations d’écritures afin d’améliorer la disponibilité du système.
* Concurrentiel : GS permet à plusieurs personnes de travailler en parallèle.
Les algorithmes sont inspirés de ceux dits [MVCC (MultiVersion Concurrency Control)](http://en.wikipedia.org/wiki/Multiversion_concurrency_control).
Ils assurent aux utilisateurs de Generic System des transactions correctement isolées et des données requêtées parfaitement cohérentes.
* Restructurable et Souple : GS est dit fortement restructurable car il offre la possibilité de modifier la structure de l’information de la même manière que l’information elle-même (c’est à dire de 
manière transactionnelle et concurrentielle).
Ainsi, et contrairement aux bases de données relationnelles, GS permet de modifier à chaud la structure de l’information.  
Un des défauts majeurs des bases de données relationnelles est que la structure de la base de données n’est pas adressée de la même manière que les données elles-mêmes.
Bien que ce choix soit raisonnable du point de vue de la complexité des développements et de la sécurité au sens large, il dégrade irrémédiablement la flexibilité et l’évolutivité des applications en 
figeant la structure des données sur laquelle elles reposent.
Ce choix est tout simplement en contradiction directe avec ce que préconisent les méthodologies modernes de développement d’applications, dans lesquelles on a besoin de faire évoluer le cahier des charges 
au cours d’un projet.  
Encore une fois, Generic System fait ici le choix d’adresser la structure de l’information comme n’importe quelle autre information, c’est à dire de manière transactionnelle et concurrentielle, ce qui 
donne sa grande souplesse.

Prerequisites
-------------
Some prerequisites.

Examples
--------

### First example
    // Create an engine named myDataBase and which is persistent
	Engine engine = new Engine("myDataBase", "[1]");
    // Create a vehicle with a color
	Generic vehicle = engine.addInstance("Vehicle");
	Generic color = vehicle.addAttribute("Color");
    // Instantiate a red vehicle
    Generic myVehicle = vehicle.addInstance("myVehicle");
    myVehicle.addInstance("red", color);
*[1] répertoire de sauvegarde du modèle de données sous la forme de chaîne de caractères*

Directory structure
-------------------

* `src/` - the source files of the module
    * `main/` - the main files of the module
        * `java/` - the Java source files of the module

Licence
-------

The content of this repository is released under the ??? as provided in the LICENSE file that accompanied this code.

