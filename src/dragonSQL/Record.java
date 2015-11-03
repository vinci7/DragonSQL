package dragonSQL;

import java.util.Vector;

/**
 * Created by qi on 15/11/2.
 */


//长度不足时用&补齐，所以搜索时也要注意长度不足时的&补齐（对API的要求）
public class Record {
    public Vector <byte[]> columns;

    Record()
    {
        columns = new Vector <byte[]>();
    }

    Record selectRecord(Table tableInfo,selectAttribute selections){//提取出要展示的属性列
        Record returnRecord=new Record();

        for(int i=0;i<tableInfo.attrNum;i++)
            for(int j=0;j<selections.columns.size();j++)
                if(i==selections.columns.get(j)){
                    returnRecord.columns.addElement(this.columns.get(i));
                    break;
                }

        return returnRecord;
    }
}
