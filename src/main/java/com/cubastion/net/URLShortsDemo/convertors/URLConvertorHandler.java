package com.cubastion.net.URLShortsDemo.convertors;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class URLConvertorHandler {

    private URLConvertorHandler() {
        this.initCharToIndexTable();
        this.initIndexForCharTable();
    }

    private HashMap<Character, Integer> charToIndexTable;
    private ArrayList<Character> indexForCharTable;

    private void
    initCharToIndexTable() {
        this.charToIndexTable = new HashMap<>();
        for (int i = 0; i < 26; ++i) {
            char ch = 'a';
            ch += i;
            this.charToIndexTable.put(ch, i);
        }

        for (int i = 26; i < 52; ++i) {
            char ch = 'A';
            ch += (i - 26);
            this.charToIndexTable.put(ch, i);
        }

        for (int i = 52; i < 62; ++i) {
            char ch = '0';
            ch += (i - 52);
            this.charToIndexTable.put(ch, i);
        }
    }

    private void
    initIndexForCharTable() {
        this.indexForCharTable = new ArrayList<>();
        for (int i = 0; i < 26; ++i) {
            char ch = 'a';
            ch += i;
            this.indexForCharTable.add(ch);
        }

        for (int i = 26; i < 52; ++i) {
            char ch = 'A';
            ch += (i - 26);
            this.indexForCharTable.add(ch);
        }

        for(int i = 52; i < 62; ++i){
            char ch = '0';
            ch += (i - 52);
            this.indexForCharTable.add(ch);
        }
    }

    public String
    createUniqueIdFromLongId(Long id){
        ArrayList<Integer> base64ConvertedID = this.createBase10ToBase64(id);
        StringBuilder builderForUniqueKeyURL = new StringBuilder();
        for(int iteratorForBase64ConvertedID : base64ConvertedID){
            builderForUniqueKeyURL.append(
                    this.indexForCharTable.get(iteratorForBase64ConvertedID)
            );
        }
        return builderForUniqueKeyURL.toString();
    }

    private ArrayList<Integer>
    createBase10ToBase64(Long id){
        Stack<Integer> digitStack = new Stack<>();
        ArrayList<Integer> digitArray = new ArrayList<>();
        while(id > 0){
            int reminder = (int) (id % 62);
            digitStack.push(reminder);
            id /= 62;
        }

        while(!digitStack.empty()){
            int topElement = digitStack.pop();
            digitArray.add(topElement);
        }
        return digitArray;
    }

    public Long
    getDictKeysFromBase64String(String base64ID){
        ArrayList<Character> base64Number = new ArrayList<>();
        for(int i = 0; i < base64ID.length(); ++i){
            base64Number.add(base64ID.charAt(i));
        }
        return this.convertBase64ToBase10ID(base64Number);
    }

    private Long
    convertBase64ToBase10ID(ArrayList<Character> ids){
        long id = 0L;
        for(int i = 0, exp = ids.size() - 1; i < ids.size(); i++, --exp){
            int base10Nums = this.charToIndexTable.get(ids.get(i));
            id += (long) (base10Nums * Math.pow(62.0, exp));
        }
        return id;
    }

}
