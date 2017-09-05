package nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;

/**
 * Created by 618 on 2015-09-07.
 */
public class NdefMessageParser {
    private NdefMessageParser(){
    }
    public  static ArrayList<ParsedRecord> parse(NdefMessage ndefMessage){
        return  getRecords(ndefMessage.getRecords());
    }
    public static ArrayList<ParsedRecord> getRecords(NdefRecord[] records){
        ArrayList<ParsedRecord> elements = new ArrayList<ParsedRecord>();
        for(NdefRecord record : records){
            if(TextRecord.isText(record)){
                elements.add(TextRecord.parse(record));
            }
        }
        return elements;
    }
}

