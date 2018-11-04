package br.edu.ulbra.election.election.validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidateStateCode {
    private static final List<String> brStates = Arrays.asList("AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO");

    public ValidateStateCode(){}

    public static boolean stateBRExists(String state){
        return brStates.contains(state);
    }
}
