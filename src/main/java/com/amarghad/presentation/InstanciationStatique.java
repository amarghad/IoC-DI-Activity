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
