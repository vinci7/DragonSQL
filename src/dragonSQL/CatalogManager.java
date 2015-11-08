package dragonSQL;

/**
 * Created by qi on 15/11/5.
 */

/*数据存储在table。xml和index.xml文档中，在创建出catalog实例的时候即解析这两个文档并载入内存中.
 * 由于这两个文档使用频繁并且占用内存并不大，所以由catalog单独保管，而不交给buffermanager管理。
 * */
import java.io.*;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class CatalogManager {
    static Document tableDocument ;
    static Document indexDocument ;


    //初始化tableDocument和indexDocument
    public CatalogManager() {
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            tableDocument = builder.parse("table.xml");
            indexDocument = builder.parse("index.xml");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //将内存内变化写会xml文档
    static  public void writeDocument(Document d,int i){
        try{
            TransformerFactory tffactory = TransformerFactory.newInstance();
            Transformer tf = tffactory.newTransformer();
            if(i==0)
                tf.transform(new DOMSource(d), new StreamResult(new FileOutputStream("table.xml")));
            else
                tf.transform(new DOMSource(d), new StreamResult(new FileOutputStream("index.xml")));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static public void setTableBlockNum(Table table,int sourceInt){

        table.blockNum=sourceInt;

        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(table.tableName)){
                e.getElementsByTagName("blockNum").item(0).getChildNodes().item(0).setNodeValue(""+table.blockNum);
                break;
            }

        }

        writeDocument(tableDocument, 0);
    }

    //创建表
    static  public void createTable(Table table){
        Element newtable = tableDocument.createElement("table");
        newtable.setAttribute("name",table.tableName);
        Element primaryKey =  tableDocument.createElement("primaryKey");
        primaryKey.setTextContent(table.primaryKey);
        newtable.appendChild(primaryKey);

        for(int i= 0;i<table.attrNum;i++){

            Attribute a = table.attrlist[i];
            Element attr =  tableDocument.createElement("attr");
            attr.setAttribute("name", a.name);
            Element length = tableDocument.createElement("length");
            Element type = tableDocument.createElement("type");
            Element unique = tableDocument.createElement("unique");
            Element isPrimeryKey=tableDocument.createElement("isPrimeryKey");
            length.setTextContent(""+a.length);
            type.setTextContent(""+a.type);
            unique.setTextContent(""+a.unique);
            isPrimeryKey.setTextContent(""+a.isPrimaryKey);
            attr.appendChild(length);
            attr.appendChild(type);
            attr.appendChild(unique);
            attr.appendChild(isPrimeryKey);
            newtable.appendChild(attr);
        }

        Element blockNum =  tableDocument.createElement("blockNum");
        blockNum.setTextContent(""+table.blockNum);
        newtable.appendChild(blockNum);

        Element recordLength =  tableDocument.createElement("recordLength");
        recordLength.setTextContent(""+table.recordLength);
        newtable.appendChild(recordLength);

        Element maxRecordsPerBlock =  tableDocument.createElement("maxRecordsPerBlock");
        maxRecordsPerBlock.setTextContent(""+table.maxRecordsPerBlock);
        newtable.appendChild(maxRecordsPerBlock);

        Element tableStore = (Element) tableDocument.getElementsByTagName("tableStore").item(0);
        tableStore.appendChild(newtable);
        writeDocument(tableDocument, 0);

    }




    //创建索引
    static  public void createIndex(Index index){


        Element newIndex = indexDocument.createElement("index");
        newIndex.setAttribute("name", index.indexName);

        Element rootNum =  indexDocument.createElement("rootNum");
        rootNum.setTextContent(""+index.rootNum);
        newIndex.appendChild(rootNum);

        Element tableName =  indexDocument.createElement("tableName");
        tableName.setTextContent(index.tableName);
        newIndex.appendChild(tableName);

        Element column =  indexDocument.createElement("column");
        column.setTextContent(""+index.column);
        newIndex.appendChild(column);

        Element columnLength =  indexDocument.createElement("columnLength");
        columnLength.setTextContent(""+index.columnLength);
        newIndex.appendChild(columnLength);

        Element blockNum =  indexDocument.createElement("blockNum");
        blockNum.setTextContent(""+index.blockNum);
        newIndex.appendChild(blockNum);

        Element indexStore = (Element) indexDocument.getElementsByTagName("indexStore").item(0);
        indexStore.appendChild(newIndex);

        writeDocument(indexDocument, 1);

    }



    //　向字符串增加信息
    //String add_info(String info,int length,String attr){
    //return null;
    //}

    //判断该属性名是不是该表中的属性

    //判断表是否存在
    static public boolean isTable(String tablename){
        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i=0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if(e.getAttribute("name").equals(tablename))
                return true;
        }
        return false;
    }

    //判断索引是否存在
    static public  boolean isIndex(String indexname){
        NodeList list = indexDocument.getElementsByTagName("index");
        for(int i=0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if(e.getAttribute("name").equals(indexname))
                return true;
        }
        return false;
    }


    //判断该属性名是不是该表中的属性
    static public  boolean isAttribution(String tablename,String attributionname){


        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i=0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if(e.getAttribute("name").equals(tablename)){
                NodeList list1 = (e.getElementsByTagName("attr"));
                for(int j=0;j<list1.getLength();j++){
                    Element e1 = (Element)list1.item(j);
                    String s = e1.getAttribute("name");
                    //System.out.println(attributionname+s);
                    if(s.equals(attributionname)){
                        //if(s.compareTo(attributionname)==0){
                        return true;
                    }
                }
            }
        }


        return false;
    }

    static public  boolean isUnique(String tablename,String attributionname){


        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i=0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if(e.getAttribute("name").equals(tablename)){
                NodeList list1 = (e.getElementsByTagName("attr"));
                for(int j=0;j<list1.getLength();j++){
                    Element e1 = (Element)list1.item(j);
                    String s = e1.getAttribute("name");
                    //System.out.println(attributionname+s);
                    if(s.equals(attributionname)){
                        boolean unique = Boolean.parseBoolean(e1.getElementsByTagName("unique").item(0).getTextContent());
                        return unique;
                    }
                }
            }
        }

        System.out.println("Attribution not found!");
        return false;
    }
    //删除数据库

    static public Vector<String> relativeIndex(String tableName){
        Vector<String> allIndex= new Vector<String>();

        NodeList indexlist = indexDocument.getElementsByTagName("index");
        for(int i =0;i<indexlist.getLength();i++){
            Element e = (Element) indexlist.item(i);
            if(e.getElementsByTagName("tableName").item(0).getChildNodes().item(0).getNodeValue().equals(tableName)){
                allIndex.add(e.getAttribute("name"));
            }
        }

        return allIndex;
    }



    //删除表
    static  public void dropTable(String tableName){

        NodeList indexlist = indexDocument.getElementsByTagName("index");
        for(int i =0;i<indexlist.getLength();i++){
            Element e = (Element) indexlist.item(i);
            if(e.getElementsByTagName("tableName").item(0).getChildNodes().item(0).getNodeValue().equals(tableName)){
                e.getParentNode().removeChild(e);
                i--;
            }
        }

        writeDocument(indexDocument, 1);

        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(tableName)){
                e.getParentNode().removeChild(e);
                break;
            }

        }

        writeDocument(tableDocument, 0);

    }

    //删除索引
    static  public void dropIndex(String indexName){


        NodeList list = indexDocument.getElementsByTagName("index");
        System.out.println("call the drop index ");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            System.out.println("the index name is :" + indexName);
            if(e.getAttribute("name").equals(indexName)){
                System.out.println("found the  index to drop ");
                e.getParentNode().removeChild(e);
                break;
            }
        }

        writeDocument(indexDocument, 1);

    }




    //设置索引根块在文件中的block偏移量
    static  public void setIndexRoot(String indexName,int number){

        NodeList list = indexDocument.getElementsByTagName("index");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(indexName)){
                Element e2=(Element) e.getElementsByTagName("rootNum").item(0);
                e2.setTextContent(""+number);
                break;
            }
        }

        writeDocument(indexDocument, 1);
    }

    //获得索引根块在文件中的block偏移量， 如果该index不存在则返回-1
    static  public int getIndexRoot(String indexName){

        NodeList list = indexDocument.getElementsByTagName("index");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(indexName)){
                int j =new Integer(e.getElementsByTagName("rootNum").item(0).getTextContent());
                return j;
            }
        }
        return -1;

    }

    //获得表信息，表存在则返回表信息，表不存在则返回空
    static  public Table getTable(String tableName){

        Table t = new Table();
        NodeList list = tableDocument.getElementsByTagName("table");

        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);

            if(e.getAttribute("name").equals(tableName)){
                t.tableName=e.getAttribute("name");
                t.primaryKey = e.getElementsByTagName("primaryKey").item(0).getTextContent();
                t.blockNum =new Integer( e.getElementsByTagName("blockNum").item(0).getTextContent());
                t.recordLength = new Integer( e.getElementsByTagName("recordLength").item(0).getTextContent());
                t.maxRecordsPerBlock = new Integer( e.getElementsByTagName("maxRecordsPerBlock").item(0).getTextContent());
                NodeList attr = e.getElementsByTagName("attr");
                Attribute[] attrlist = new Attribute[attr.getLength()];
                for (int j =0;j<attr.getLength();j++){
                    attrlist[j]=new Attribute();
                    Element a = (Element)attr.item(j);
                    attrlist[j].unique =Boolean.parseBoolean(a.getElementsByTagName("unique").item(0).getTextContent());
                    attrlist[j].name = a.getAttribute("name");
                    attrlist[j].length = new Integer(a.getElementsByTagName("length").item(0).getTextContent());
                    attrlist[j].type = new Integer(a.getElementsByTagName("type").item(0).getTextContent());
                    attrlist[j].isPrimaryKey =Boolean.parseBoolean(a.getElementsByTagName("isPrimeryKey").item(0).getTextContent());

                }
                t.attrlist = attrlist;
                t.attrNum = attr.getLength();
                return t;
            }

        }
        System.out.println("getTable返回null");
        return null;
    }

    //获得索引信息，索引存在有则返回索引信息，没有则返回null
    static   public Index getIndex(String indexName){
        Index index = new Index();

        NodeList list = indexDocument.getElementsByTagName("index");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if(e.getAttribute("name").equals(indexName)){
                index.indexName = e.getAttribute("name");
                index.tableName = e.getElementsByTagName("tableName").item(0).getTextContent();
                index.column = new Integer(e.getElementsByTagName("column").item(0).getTextContent());
                index.columnLength = new Integer(e.getElementsByTagName("columnLength").item(0).getTextContent());
                index.rootNum = new Integer(e.getElementsByTagName("rootNum").item(0).getTextContent());
                index.blockNum = new Integer(e.getElementsByTagName("blockNum").item(0).getTextContent());
                return index;
            }
        }


        return null;
    }

    //当表增加了一个block时，修改对应的表信息中的blockNum增加一
    static   public void addTableBlockNum(String tableName){
        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if( e.getAttribute("name").equals(tableName)){
                int blockNum = new Integer(e.getElementsByTagName("blockNum").item(0).getTextContent());
                blockNum++;
                e.getElementsByTagName("blockNum").item(0).setTextContent(""+blockNum);
                break;
            }
        }
        writeDocument(tableDocument, 0);
    }

    //当索引增加了一个block时，修改对应的索引信息中的blockNum增加一
    static  public void addIndexBlockNum(String indexName){
        NodeList list = indexDocument.getElementsByTagName("index");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element)list.item(i);
            if( e.getAttribute("name").equals(indexName)){
                int blockNum = new Integer(e.getElementsByTagName("blockNum").item(0).getTextContent());
                blockNum++;
                e.getElementsByTagName("blockNum").item(0).setTextContent(""+blockNum);
                break;
            }
        }
        writeDocument(indexDocument, 1);
    }


    //获取某张表中第n个属性的类型
    static public int getAttrType(String tableName, int n){
        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(tableName)){
                Element attr =  (Element) e.getElementsByTagName("attr").item(n-1);
                int type = new Integer( attr.getElementsByTagName("type").item(0).getTextContent());
                return type;
            }

        }
        return -1;
    }

    //获取某张表中某个属性的类型
    static   public  int getAttrType(String tableName, String attrName){
        NodeList list = tableDocument.getElementsByTagName("table");
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(tableName)){
                NodeList list2 =  e.getElementsByTagName("attr");
                for(int j=0;j<list2.getLength();j++){
                    Element e2 = (Element) list2.item(j);
                    if(e2.getAttribute("name").equals(attrName)){
                        int type = new Integer( e2.getElementsByTagName("type").item(0).getTextContent());
                        return type;
                    }
                }
            }

        }
        return -1;
    }


    //判断输入的word是否可以转化为表table的第n个属性的类型
    static  public  boolean matchType(String word,String table,int n){

        int type = getAttrType(table, n);
        switch(type){

            case 1:if(word.matches("[0-9]*")) return true;break;//int
            case 2:if(word.matches("[0-9]*|([0-9]*.[0-9]*)")) return true;break;//float
            case 3:if(word.matches("'[a-zA-Z0-9_]*'")&&(type==3)) return true;break;//char
        }

        return false;
    }
    //att为table中的属性，判断word的类型是否和att的类型相同
    static  public  boolean Type(String att,String word,String table){
        int type = getAttrType(table, att);

        switch(type){

            case 1:if(word.matches("[0-9]*")) return true;break;//int
            case 2:if(word.matches("[0-9]*|([0-9]*.[0-9]*)")) return true;break;//float
            case 3: if (word.matches("'[a-zA-Z0-9_]*'")&&(type==3)) return true;break;//char
        }

        return false;

    }
    //根据表名和属性名查找索引，若存在则返回索引，若不存在则返回NULL

    public static Index getIndexfromTable(String tableName,  String attrName){
        NodeList list = tableDocument.getElementsByTagName("table");
        int column = -1;
        for(int i =0;i<list.getLength();i++){
            Element e = (Element) list.item(i);
            if(e.getAttribute("name").equals(tableName)){
                NodeList list2 =  e.getElementsByTagName("attr");
                for(int j=0;j<list2.getLength();j++){
                    Element e2 = (Element) list2.item(j);
                    if(e2.getAttribute("name").equals(attrName)){
                        column = j;
                        break;
                    }
                }
                if(column!=-1) break;
            }
        }
        if(column!=-1){
            NodeList indexList = indexDocument.getElementsByTagName("index");
            for (int i =0;i<indexList.getLength();i++){
                Element e  = (Element)indexList.item(i);
                String tname = e.getElementsByTagName("tableName").item(0).getTextContent();
                int indexColumn = new Integer(e.getElementsByTagName("column").item(0).getTextContent());
                if((tname.equals(tableName))&&(indexColumn==column)){
                    String indexName = e.getAttribute("name");
                    Index in = getIndex(indexName);
                    return in;
                }
            }

        }
        return null;
    }

}
