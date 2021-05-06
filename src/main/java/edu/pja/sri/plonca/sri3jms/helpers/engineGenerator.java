package edu.pja.sri.plonca.sri3jms.helpers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class engineGenerator {

    public static Double generateDimenValue(ArrayList<Double> values, Double probability) {
        Random rand1 = new Random();
        int lowerbound = 0;
        int upperbound = values.toArray().length;
        if (rand1.nextDouble() < probability) {
            return values.get(0);
        }
        int random_integer = rand1.nextInt(upperbound - lowerbound) + lowerbound;
        return values.get(random_integer);

    }

//    public static void main(String[] args) {
//        ArrayList<Double> testList = new ArrayList<>();
//        testList.add(10.1);
//        testList.add(-110.1);
//        testList.add(814.1);
//        testList.add(566721.1);
//        testList.add(0.12);
//
//        Random rand1 = new Random();
//        int upperbound = 5;
//        int lowerbound = 0;
//        for (int i = 0; i < 15; i++) {
//            if (rand1.nextDouble() < 0.2) {
//                System.out.println(testList.get(0));
//            } else {
//                int random_integer = rand1.nextInt(upperbound - lowerbound) + lowerbound;
//                System.out.println(testList.get(random_integer));
//            }
//        }
//    }

}
