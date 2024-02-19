package com.kroyo.presentation;

import com.kroyo.dao.DaoImpl;
import com.kroyo.metier.MetierImpl;

public class InstanciationStatique {

    public static void main(String[] args) {

        DaoImpl dao = new DaoImpl();

        MetierImpl metier = new MetierImpl(dao);

        System.out.println("Instanciation statique");
        System.out.println("Resultat : " + metier.calcul());
    }

}
