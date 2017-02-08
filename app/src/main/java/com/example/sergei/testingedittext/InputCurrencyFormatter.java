package com.example.sergei.testingedittext;

/**
 * Created by Sergei on 08.02.2017.
 */

public class InputCurrencyFormatter {

    private int curIndex = -1;

    public int getCurIndex(){
        return curIndex;
    }

    public String format(CharSequence s, int curIndex){

        boolean symbolBeforeSeparator = !s.toString().contains(".");

        StringBuilder formattedNumber = new StringBuilder();
        int groupCounter = 0;

        int startZeroAndSpacesCounter = 0;

        //Считаем количество нулей либо пробелов, если они есть в начале строки. Нули и пробелы слева не будут добавлены в результирую строку (кроме нуля перед разделителем).
        for(int i = 0; i < s.length(); i++){

            char symb = s.charAt(i);

            boolean isLastSymbol = i == s.length() - 1;
            boolean hasSeparatorAfter = i < s.length() - 1 && s.charAt(i+1) == '.';


            if(symb == '0' && !hasSeparatorAfter && !isLastSymbol || symb == ' '){
                startZeroAndSpacesCounter++;
                curIndex--;
            }
            else{
                break;
            }


        }

        //Правильно расставляем пробелы между группами и меняем положение курсора
        for(int i = s.length(); i > startZeroAndSpacesCounter; i--){
            char symb = s.charAt(i-1);
            if(!symbolBeforeSeparator){
                formattedNumber.append(symb);
            }
            else{
                if(symb != ' ' && groupCounter == 3){
                    formattedNumber.append(' ').append(symb);
                    groupCounter = 1;
                    curIndex++;
                }
                else if(symb == ' ' && groupCounter < 3){
                    curIndex--;
                }
                else if(symb == ' ' && groupCounter == 3){
                    formattedNumber.append(symb);
                    groupCounter = 0;
                }
                else{
                    formattedNumber.append(symb);
                    groupCounter++;
                }
            }
            if(symb == '.'){
                symbolBeforeSeparator = true;
            }

        }

        //если строка начинается с запятой, подставить в результирующую строку ноль
        if (s.length()> 0 && s.toString().replace(" ", "").charAt(0) == '.' || s.length() == 0){
            formattedNumber.append("0");
            curIndex++;
        }



        this.curIndex = curIndex;
        return formattedNumber.reverse().toString();

    }


}
