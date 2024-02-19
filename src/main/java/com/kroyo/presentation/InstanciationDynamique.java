package com.kroyo.presentation;

import com.kroyo.dao.IDao;
import com.kroyo.metier.IMetier;

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
