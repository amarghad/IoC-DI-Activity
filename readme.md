# Définition des classes

## Partie DAO

Nous commençons par créer une interface appelée `IDao`. Cette interface contient une seule méthode qui renvoie un `double` sans paramètres.

```java
package com.amarghad.dao;

public interface IDao {
    double getData();
}
```

Ensuite, on crée une implémentation `DaoImpl` de cette interface qui renvoie un nombre aléatoire entre 0 et 1.


```java
package com.amarghad.dao;

public class DaoImpl implements IDao {

    /**
     * Génerer un nombre aléatoire entre 0 et 1
     * @return double
     */
    @Override
    public double getData() {
        return Math.random();
    }
}

```


## Partie Métier

Tout comme la couche DAO, nous créons une interface `IMetier` qui déclare une unique méthode `calcul` renvoyant un type `double`.

```java
package com.amarghad.metier;

public interface IMetier {

    double calcul();

}
```

On ajoute une implémentation de cette interface appelée `MetierImpl` qui présente un faible couplage avec la partie DAO, conformément à l'interface `IDao`. La méthode `calcul` de cette implémentation multiplie le résultat de `getData()` par 100.

L'objet DAO de l'implémentation est fourni via le constructeur.

``` java
package com.amarghad.metier;
import com.amarghad.dao.IDao;
public class MetierImpl implements IMetier {

    private IDao dao;

    public MetierImpl(IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        return dao.getData() * 100;
    }
}

```

## Partie Présentation : Instanciation et injection des dépendances
Dans cette section, nous parvenons à créer les objets métiers nécessaires pour mettre en œuvre le métier de l'application en utilisant la méthode `calcul`. Il vous suffit de créer un objet DAO et de l'injecter dans l'objet Métier.

### Instanciation statique
La méthode la plus simple est d'instancier chaque objet de manière statique.
```java
package com.amarghad.presentation;

import com.amarghad.dao.DaoImpl;
import com.amarghad.metier.MetierImpl;

public class InstanciationStatique {

    public static void main(String[] args) {

        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao);
        System.out.println("Instanciation statique");
        System.out.println("Resultat : " + metier.calcul());
    }

}
```

Cette méthode présente une particularité : chaque modification au niveau de l'instanciation des objets nécessite une modification du code source de l'application. Cela a pour conséquence de rendre l'application moins flexible aux ajustements sans fermer complètement la porte à la modification.

### Instaciation dynamique
Cette méthode consiste à instancier les objets à partir des fichiers de configuration.

On crée un fichier texte `config.txt` à la racine du projet. La première ligne de ce fichier doit contenir le nom de la classe DAO à instancier, et la deuxième ligne doit contenir le nom de la classe Métier.

```
com.amarghad.dao.DaoImpl
com.amarghad.metier.MetierImpl
```

Grâce à l'utilisation de l'API Reflection, il est possible d'instancier les classes nécessaires et d'injecter dynamiquement les dépendances, le tout sans altérer le code source. On extrait le contenu du texte et on crée les instances de chaque classe.

```java
package com.amarghad.presentation;

import com.amarghad.dao.IDao;
import com.amarghad.metier.IMetier;

import java.io.*;

public class InstanciationDynamique {

    public static void main(String[] args) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new FileReader("config.txt"));
        String daoClassName = bufferedReader.readLine(),
                metierClassName = bufferedReader.readLine();

        Class<?> daoClass = Class.forName(daoClassName),
                metierClass = Class.forName(metierClassName);

        IDao dao = (IDao) daoClass.getConstructor().newInstance();
        IMetier metier = (IMetier) metierClass.getConstructor(IDao.class).newInstance(dao);


        System.out.println("Instanciation dynamique");
        System.out.println("Resultat : " + metier.calcul());

    }

}
```

### Instaciation en utilisant Spring : Version XML
La bibliothèque Spring Context nous permet d'instancier les classes et d'injecter les dépendances de manière dynamique en utilisant un fichier XML (similaire à l'instanciation dynamique avec un fichier texte).

Premièrement, On ajoute les dépendances Maven dans le fichier `pom.xml` incluent les bibliothèques principales de Spring telles que `spring-core`, `spring-context`, et `spring-beans`.
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>6.0.16</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.0.16</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
        <version>6.0.16</version>
    </dependency>
</dependencies>
```

Dans un fichier `context.xml`, nous pouvons instancier les classes en utilisant les balises `<bean>`.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="com.amarghad.metier.MetierImpl">
        <!-- Injecter le bean via l'argument du constructeur -->
        <constructor-arg type="com.amarghad.dao.IDao">
            <bean class="com.amarghad.dao.DaoImpl"/>
        </constructor-arg>
    </bean>
</beans>
```
La classe `InstanciationAvecSpringXML` charge un contexte Spring depuis le fichier `context.xml`. Ensuite, elle récupère une instance de la classe `IMetier` (Ce cas, il s'agit d'un object `MetierImpl`) à partir du contexte Spring et affiche le résultat du calcul effectué par cette instance.

```java
package com.amarghad.presentation;

import com.amarghad.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InstanciationAvecSpringXML {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        IMetier metier = context.getBean(IMetier.class);

        System.out.println("Instanciation avec -Spring- XML");
        System.out.println("Resultat : " + metier.calcul());

    }

}
```

### Instaciation en utilisant Spring : Version Annotations
La bibliothèque Spring Context nous offre la possibilité d'injecter les dépendances à l'aide d'annotations.

Nous ajoutons l'annotation `@Component` aux classes `MetierImpl` et `DaoImpl`, en spécifiant le nom du bean. Pour une spécification supplémentaire, nous utilisons l'annotation `@Repository` pour la classe `DaoImpl` et `@Service` pour la classe `MetierImpl`, qui joue précisément le rôle d'alias.
```java
package com.amarghad.dao;

import org.springframework.stereotype.Repository;

@Repository("dao")
public class DaoImpl implements IDao {
    @Override
    public double getData() { return Math.random(); }
}
```
La classe prend en dépendance un objet `IDao` et utilise l'annotation `@Qualifier("dao")` pour spécifier le bean `IDao` qui doit être injecté lors de l'injection de dépendances.

```java
package com.amarghad.metier;

import com.amarghad.dao.IDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("metier")
public class MetierImpl implements IMetier {

    private IDao dao;

    public MetierImpl(@Qualifier("dao") IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() { return dao.getData() * 100; }
}
```


La classe suivant crée un contexte d'application Spring en utilisant `AnnotationConfigApplicationContext`, spécifiant les packages où trouver les noms des packages métier et d'accès aux données (dao).

```java
package com.amarghad.presentation;

import com.amarghad.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class InstanciationAvecSpringAnnotations {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext("com.kroyo.dao", "com.kroyo.metier");
        IMetier metier = context.getBean(IMetier.class);

        System.out.println("Instanciation avec -Spring- Annotations");
        System.out.println("Resultat : " + metier.calcul());

    }

}
```