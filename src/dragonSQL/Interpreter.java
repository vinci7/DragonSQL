package dragonSQL;
import java.io.BufferedReader;

/**
 * Created by qi on 15/11/1.
 */

public class Interpreter {
    public static Query query = new Query();

    public static void createTable(){
        Attribute[] attr = new Attribute[4];

        query.cmd = "createTable";
        query.tableName = "student";
        query.primarykey = "sno";

        query.attrNum = 4;

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

        query.attrList = attr;
    }

    public static void dropTable(){
        query.cmd = "createTable";
        query.tableName = "student";
    }

    public static void createIndex(){
        query.cmd = "createIndex";
        query.tableName = "student";
        query.indexName = "stunameidx";
        Attribute[] attr = new Attribute[1];
        attr[0] = new Attribute();
        attr[0].name = "sname";
        query.attrList = attr;
    }

    public static void dropIndex(){
        query.cmd = "createIndex";
        query.indexName = "stunameidx";
    }

    public static void select(){
        query.cmd = "select";
        query.indexName = "student";
        Attribute[] attr = new Attribute[2];

        query.attrNum = 2;

        attr[0] = new Attribute();
        attr[0].name = "sno";
        attr[0].value = "123";

        attr[1] = new Attribute();
        attr[1].name = "sname";
        attr[1].value = "test";

        query.attrList = attr;
    }

    public static void insert(){
        query.cmd = "insert";
        query.tableName = "student";
        Attribute[] attr = new Attribute[4];

        query.attrNum = 4;

        attr[0] = new Attribute();
        attr[0].name = "sno";
        attr[0].value = "123";

        attr[1] = new Attribute();
        attr[1].name = "sname";
        attr[1].value = "123";

        attr[2] = new Attribute();
        attr[2].name = "sage";
        attr[2].value = "1";

        attr[3] = new Attribute();
        attr[3].name = "sgender";
        attr[3].value = "M";

        query.attrList = attr;
    }

    public static void deleteRecord(){
        query.cmd = "deleteRecord";
        query.tableName = "student";
        Attribute[] attr = new Attribute[2];

        query.attrNum = 2;

        attr[0] = new Attribute();
        attr[0].name = "sno";
        attr[0].value = "123";

        attr[1] = new Attribute();
        attr[1].name = "sname";
        attr[1].value = "test";

        query.attrList = attr;
    }

    public static void quit(){
        query.cmd = "quit";
    }

    public static void execfile(){
        query.cmd = "execfile";
        query.fileName = "test.txt";
    }

    public static void main(String input) throws Exception{
        API api = new API();

        //createTable();
        //api.createTable(query);

        //dropTable();
        //api.dropTable(query);

        createIndex();
        api.createIndex(query);

    }
}
