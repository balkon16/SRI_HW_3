package edu.pja.sri.plonca.sri3jms.helpers;

import java.util.ArrayList;
import java.util.Random;

public class engineGenerator {

    public static Double generateDimenValue(ArrayList<Double> values, Double probability) {
        Random rand1 = new Random();
        int lowerBound = 0;
        int upperBound = values.toArray().length;
        if (rand1.nextDouble() < probability) {
            return values.get(0);
        }
        int random_integer = rand1.nextInt(upperBound - lowerBound) + lowerBound;
        return values.get(random_integer);
    }
}
