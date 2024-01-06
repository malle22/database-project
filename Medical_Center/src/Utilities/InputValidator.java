package Utilities;

public class InputValidator {

    public boolean isValidDigitUserInput(String input, int maxLength){
        boolean ok = true;
        if (input.length() == maxLength ){
            for (int i = 0; i < input.length(); i++){
                if (!Character.isDigit(input.charAt(i))){
                    ok = false;
                }
            }
        }
        else {
            ok = false;
        }
        return ok;
    }
}
