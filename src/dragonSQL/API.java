package dragonSQL;

import java.io.BufferedReader;

/**
 * Created by qi on 15/11/1.
 */
public class API {

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
                    dragonSQL.CatalogManager.createIndex(index);        //待实现
                    IndexManager.createTable(table, index);              //待实现
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
        IndexManager.createTable(table,index);
    }

    public static void deleteIndex(Query query){
        IndexManager.dropIndex(query.indexName);                        //待实现
        CatalogManager.dropIndex(query.indexName);                      //待实现
        BufferManager.dropIndex(query.indexName + ".index");            //待实现
    }

    public static void select(Query query){

    }
}
