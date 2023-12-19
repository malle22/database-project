package Utilities;
import  java.util.Random;

public class NumberGenerator {

    private Random rand;
    public String Generate8DigitNbr(){
        rand = new Random();
        int randNbr = (rand.nextInt(99999999 - 10000000)) + 10000000;
        System.out.println(randNbr);
        return String.valueOf(randNbr);
    }
}
