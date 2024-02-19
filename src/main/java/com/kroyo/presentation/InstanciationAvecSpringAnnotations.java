package com.kroyo.presentation;

import com.kroyo.metier.IMetier;
import com.kroyo.metier.MetierImpl;
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
