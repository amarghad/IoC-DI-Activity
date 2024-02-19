package com.kroyo.presentation;

import com.kroyo.metier.IMetier;
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
