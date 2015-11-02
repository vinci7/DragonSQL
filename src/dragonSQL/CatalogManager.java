package dragonSQL;

/**
 * Created by qi on 15/11/2.
 */

public class CatalogManager {

    public static void createTable(Table table){
        System.out.println("Here's CatalogManager.createTable();");
    }

    public static void createIndex(Index index){
        System.out.println("Here's CatalogManager.createIndex();");
    }

    public static void dropTable(String tableName){
        System.out.println("Here's CatalogManager.dropTable();");
    }

    public static void dropIndex(String indexName){
        System.out.println("Here's CatalogManager.dropIndex();");
    }

    public static Table getTable(String tableName){
        System.out.println("Here's CatalogManager.getTable();");
        Table table = new Table();
        table.tableName = "student";
        table.primaryKey = "sno";

        Attribute[] attr = new Attribute[4];

        attr[0] = new Attribute();
        attr[0].name = "sno";
        attr[0].unique = false;
        attr[0].length = 8;
        attr[0].type = 3;
        attr[0].isPrimaryKey = true;

        attr[1] = new Attribute();
        attr[1].name = "sname";
        attr[1].unique = true;
        attr[1].length = 16;
        attr[1].type = 3;
        attr[1].isPrimaryKey = false;

        attr[2] = new Attribute();
        attr[2].name = "sage";
        attr[2].unique = false;
        attr[2].length = 0;
        attr[2].type = 1;
        attr[2].isPrimaryKey = false;

        attr[3] = new Attribute();
        attr[3].name = "sgender";
        attr[3].unique = false;
        attr[3].length = 1;
        attr[3].type = 3;
        attr[3].isPrimaryKey = false;

        table.attrlist = attr;
        table.blockNum = 1;
        table.recordLength = 0;
        table.maxRecordsPerBlock = 0;

        return table;
    }
}