package com.example.sergei.testingedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Selection.getSelectionEnd;
import static android.text.Selection.getSelectionStart;
import static android.text.Selection.setSelection;

public class MainActivity extends AppCompatActivity {

    private boolean isSelfChanged;
    boolean stringRightFromComma = false;
    String textBeforeChanged = "";
    int startIndexBeforeChanging = 0;
    int oldSymbolsDeleted = 0;
    int newSymbolsAdded = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText)findViewById(R.id.edit_text);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if(isSelfChanged){
                    return;
                }

                textBeforeChanged = s.toString().replace(",", ".");
                startIndexBeforeChanging = start;
                oldSymbolsDeleted = count;
                newSymbolsAdded = after;


               if(s.toString().contains(",")){
                   //Текст вводится справа от разделителя
                   stringRightFromComma = start > s.toString().indexOf(",");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isSelfChanged) {
                    return;
                }

                String formatted = editable.toString().replace(",",".");
                int curIndex = getSelectionEnd(editable);

                if(formatted.contains(".")){

                    boolean isMoreThanOneSeparatorsInserted = formatted.length() - formatted.replaceAll("[.]", "").length() > 1;

                    //Не даем возможности пользователю ввести вторую запятую
                    if(isMoreThanOneSeparatorsInserted){
                        formatted = textBeforeChanged;
                        curIndex = (startIndexBeforeChanging == textBeforeChanged.length()) ? startIndexBeforeChanging : startIndexBeforeChanging + 1;
                    }
                    //Если текст вводится справа от разделителя он работает аналогично нажатой клавише Insert, т.е. введеный символ заменяет текущий
                    else if(stringRightFromComma){

                            String substring = formatted.substring(startIndexBeforeChanging, startIndexBeforeChanging + newSymbolsAdded);

                            String unchangeableStartPartTextBefore = textBeforeChanged.substring(0, startIndexBeforeChanging);
                            String unchangeableEndPartTextBefore  = textBeforeChanged.substring(startIndexBeforeChanging + oldSymbolsDeleted, textBeforeChanged.length());

                            int availableSymbolsAddingCount = 3 - startIndexBeforeChanging + formatted.indexOf(".");

                            String availableNewSymbolsAdded = substring.substring(0, substring.length() >= availableSymbolsAddingCount ? availableSymbolsAddingCount : substring.length());

                            unchangeableEndPartTextBefore = unchangeableEndPartTextBefore.substring(availableNewSymbolsAdded.length() > unchangeableEndPartTextBefore.length()
                                    ? unchangeableEndPartTextBefore.length() : availableNewSymbolsAdded.length());
                            formatted = unchangeableStartPartTextBefore + availableNewSymbolsAdded +  unchangeableEndPartTextBefore;

                            curIndex = startIndexBeforeChanging + availableNewSymbolsAdded.length();
                    }
                    else{
                        //Не даем возможности поставить запятую в неправильном месте (на три и более позиции левее начала числа)
                        boolean isSeparatorInsertedUncorrected = formatted.indexOf('.') < formatted.length() - 3;
                        if(isSeparatorInsertedUncorrected){

                            formatted = textBeforeChanged;
                            curIndex = (startIndexBeforeChanging == textBeforeChanged.length()) ? startIndexBeforeChanging : startIndexBeforeChanging + 1;

                        }

                    }

                }


                InputCurrencyFormatter formatter = new InputCurrencyFormatter();
                //форматируем число, расставляем правильно пробелы между группами, убираем нули слева, изменяем положения курсора в зависимости от форматирования
                formatted = formatter.format(formatted, curIndex);
                curIndex = formatter.getCurIndex();

                formatted = formatted.replace('.', ',');


                isSelfChanged = true;
                editable.replace(0, editable.length(), formatted, 0, formatted.length());
                isSelfChanged = false;


                if(curIndex >= 0){
                    setSelection(editable, curIndex);
                }

            }

        });

    }

}
