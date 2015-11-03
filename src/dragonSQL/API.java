package dragonSQL;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


/**
 * Created by qi on 15/11/1.
 */
public class API {

    public static String SQL;
    public static final int INT =1;
    public static final int FLOAT =2;
    public static final int CHAR =3;

    public static void main(){
        System.out.println("Here's API.main();");
    }

    public static void createTable(Query query){
        int length = query.attrList.length;
        Table table = new Table();
        table.tableName = query.tableName;
        table.primaryKey = query.primarykey;
        table.attrNum = query.attrNum;
        table.attrlist = new Attribute[length];
        for (int i = 0; i < table.attrNum; i++) {
            table.attrlist[i] = new Attribute();
            table.attrlist[i] = query.attrList[i];
        }
        table.blockNum = 1;
        table.recordLength = 0;
        for (int i = 0; i < table.attrNum; i++) {
            table.recordLength += table.attrlist[i].length;
        }
        table.maxRecordsPerBlock = 4096/table.recordLength;

        dragonSQL.CatalogManager.createTable(table);
        RecordManager.createTable(table);

        if (table.primaryKey != null){
            for (int i = 0; i < table.attrNum; i++) {
                if (table.attrlist[i].name.equals(table.primaryKey)){ //建立索引
                    Index index = new Index();
                    index.indexName = table.tableName+"-primary-index";
                    index.tableName = table.tableName;
                    index.column = i;
                    index.columnLength = table.attrNum;
                    index.rootNum = 0;
                    index.blockNum = 0;
                    dragonSQL.CatalogManager.createIndex(index);            //待实现
                    IndexManager.createIndex(table, index);                 //待实现
                    break;
                }
            }
        }
    }

    public static void dropTable(Query query){
        String tableName = query.tableName;
        RecordManager.dropTable(tableName);                             //待实现
        dragonSQL.CatalogManager.dropTable(tableName);                  //待实现
        BufferManager.dropTable(tableName+".table");                    //待实现
    }

    public static void createIndex(Query query){
        Index index = new Index();
        index.indexName = query.indexName;
        Table table = CatalogManager.getTable(query.tableName);
        index.tableName = query.tableName;
        for (int i = 0; i < table.attrNum; i++) {
            if (table.attrlist[i].name.equals(query.attrList[0].name)) {
                index.column = i;
                index.columnLength = table.attrlist[i].length;
                break;
            }
        }
        index.rootNum = 0;
        index.blockNum = 0;
        CatalogManager.createIndex(index);
        IndexManager.createIndex(table,index);
    }

    public static void dropIndex(Query query) {
        IndexManager.dropIndex(query.indexName);                        //待实现
        CatalogManager.dropIndex(query.indexName);                      //待实现
        BufferManager.dropIndex(query.indexName + ".index");            //待实现
    }

    public static void select(Query query){
        int flag = 0;
        int isCondition = 0;
        String tableName = 
    }

    public static void insert(Query query){
        String tableName = query.tableName;
        Table table = new Table();
        Record record = new Record();
        table = CatalogManager.getTable(tableName);
        Vector<Condition> cds = new Vector<Condition>();
        boolean isExist = false;
        for (int i = 0; i < query.attrNum; i++) {
            if (table.attrlist[i].unique){
                Condition cd = new Condition();
                cd.op = Comparison.Eq;
                cd.value = query.attrList[i].value;
                cd.columnNum = i;

                cds.add(cd);
                try{
                    if (RecordManager.exist(table,cds)) {
                        System.out.println(table.attrlist[i].name+"="+query.attrList[i].value+" is existed!");
                        isExist = true;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try{
                byte[] bytes = stringToBytes(table.attrlist[i],query.attrList[i].value);
                record.columns.add(bytes);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        if (!isExist){
            try {
                RecordManager.insertValue(table,record);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void delete(Query query){
        Table table = new Table();
        String tableName = query.tableName;
        table = CatalogManager.getTable(tableName);
        Vector<Condition> cds = new Vector<>();
        for (int i = 0; i < query.attrNum; i++) {
            Condition cd = new Condition();
            cd.op = query.attrList[i].signal;
            cd.value = query.attrList[i].value;
            cd.columnNum = query.attrList[i].order;
            cds.add(cd);
        }
        if (query.attrNum == 0){
            RecordManager.delete(table);
            CatalogManager.setTableBlockNum(table,1);
        } else {
            try{
                RecordManager.delete(table,cds);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void quit(Query query){
        System.out.println("Quit.");
        System.exit(0);
    }

    public static void execfile(Query query){

    }

    static public byte[] stringToBytes(Attribute attr,String tmpString) throws UnsupportedEncodingException{

        byte[] tmpbyte=new byte[attr.length];

        switch(attr.type){
            case CHAR:
                byte[] tmpb=tmpString.getBytes("ISO-8859-1");
                int i=0;
                for(;i<tmpb.length;i++){
                    tmpbyte[i]=tmpb[i];
                }
                for(;i<attr.length;tmpbyte[i++]='&');

                break;
            case INT:
                tmpbyte=new byte[4];
                int intvalue1=Integer.valueOf(tmpString).intValue();
                for(int j=0;j<4;j++){
                    tmpbyte[j]=(byte)(intvalue1>>8*(3-j)&0xFF);
                }
                break;
            case FLOAT:
                tmpbyte=new byte[4];
                float flvalue2=Float.valueOf(tmpString).floatValue();
                int l = Float.floatToIntBits(flvalue2);
                for (int j = 0; j < 4; j++) {
                    tmpbyte[j] = new Integer(l).byteValue();
                    l = l >> 8;
                }
                break;
        }
        return tmpbyte;
    }
}
