package dragonSQL;

/**
 * Created by qi on 15/11/7.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class RecordManager {

    public static final int INT =1;
    public static final int FLOAT =2;
    public static final int CHAR =3;

    RecordManager(){

    }
    //因为.table文件中的数据是二进制格式，数据之间使用'&'进行分隔，需要取出数据后进行分离
    private static Record splitRecord(Table tableInfo,byte[] recordLine){
        Record returnRecord=new Record();
        byte[] tmpbyte;

        int startpos=0;
        for(int i=0;i<tableInfo.attrNum;i++){
            tmpbyte=new byte[tableInfo.attrlist[i].length];
            for(int j=0;j<tmpbyte.length;j++){
                tmpbyte[j]=recordLine[startpos+j];
            }
            if(tableInfo.attrlist[i].type==CHAR){
                int validlength=0;
                for(;validlength<tableInfo.attrlist[i].length && tmpbyte[validlength]!='&';validlength++);
                byte[] tmp;
                if(validlength==tableInfo.attrlist[i].length-1 && tmpbyte[validlength]!='&'){
                    tmp=new byte[validlength+1];
                    tmp=new byte[validlength];
                    for(int j=0;j<validlength+1;j++){
                        tmp[j]=tmpbyte[j];
                    }
                }
                else{
                    tmp=new byte[validlength];
                    for(int j=0;j<validlength;j++){
                        tmp[j]=tmpbyte[j];
                    }
                }

                returnRecord.columns.add(tmp);
            }
            else returnRecord.columns.add(tmpbyte);

            startpos+=tableInfo.attrlist[i].length;
        }

        return returnRecord;
    }


    //将记录字符串分解成属性字符串向量
    private static row bytesToString(Table tableInfo,Record recordLine) throws UnsupportedEncodingException{
        row returnRow=new row();
        String tmpString = null;
        for(int i=0;i<tableInfo.attrNum;i++){
            switch(tableInfo.attrlist[i].type){
                case CHAR:
                    tmpString=new String(recordLine.columns.get(i));
                    break;
                case INT:
                    int intvalue=0;
                    for(int j=0;j<4;j++){
                        intvalue  +=(recordLine.columns.get(i)[j] & 0xFF)<<(8*(3-j));
                    }
                    tmpString=new String(""+intvalue);
                    break;
                case FLOAT:
                    float flvalue=0;
                    int l;
                    l = recordLine.columns.get(i)[0];
                    l &= 0xff;
                    l |= ((long) recordLine.columns.get(i)[ 1] << 8);
                    l &= 0xffff;
                    l |= ((long) recordLine.columns.get(i)[ 2] << 16);
                    l &= 0xffffff;
                    l |= ((long)recordLine.columns.get(i)[ 3] << 24);
                    flvalue= Float.intBitsToFloat(l);
                    tmpString=new String(""+flvalue);
                    break;
            }
            returnRow.columns.add(tmpString);
        }

        return returnRow;
    }

    //将属性值与条件的操作符和特定值进行比较后得出该条记录是否满足条件
    private static boolean Compare(Table tableInfo,Record InfoLine,Vector<Condition> conditions) throws UnsupportedEncodingException{
        for(int i=0;i<conditions.size();i++){ //对and集合成条件向量的每个条件进行对比
            int column=conditions.get(i).columnNum;  //这个条件是在哪个属性上进行的
            String value2=conditions.get(i).value;  //这个条件的比较内容
            switch(tableInfo.attrlist[column].type){ //比较类型
                case CHAR:
                    String value1= new String(InfoLine.columns.get(column),"ISO-8859-1");
                    switch(conditions.get(i).op){
                        case Ls:
                            if(value1.compareTo(value2)>=0) return false;	break;
                        case Le:
                            if(value1.compareTo(value2)>0) return false;	break;
                        case Gt:
                            if(value1.compareTo(value2)<=0) return false;	break;
                        case Ge:
                            if(value1.compareTo(value2)<0) return false;	break;
                        case Eq:
                            if(value1.compareTo(value2)!=0) return false;	break;
                        case Ne:
                            if(value1.compareTo(value2)==0) return false;	break;
                    }
                    break;
                case INT:
                    int intvalue1=0;
                    for(int j=0;j<4;j++){
                        intvalue1  +=(InfoLine.columns.get(column)[j] & 0xFF)<<(8*(3-j));
                    }
                    int intvalue2=Integer.valueOf(value2).intValue();
                    switch(conditions.get(i).op){
                        case Ls:if(intvalue1>=intvalue2) return false;	break;
                        case Le:if(intvalue1>intvalue2) return false;	break;
                        case Gt:if(intvalue1<=intvalue2) return false;	break;
                        case Ge:if(intvalue1<intvalue2) return false;	break;
                        case Eq:if(intvalue1!=intvalue2) return false;	break;
                        case Ne:if(intvalue1==intvalue2) return false;	break;
                    }
                    break;
                case FLOAT:
                    float flvalue1=0;
                    int l;
                    l = InfoLine.columns.get(column)[0];
                    l &= 0xff;
                    l |= ((long) InfoLine.columns.get(column)[ 1] << 8);
                    l &= 0xffff;
                    l |= ((long) InfoLine.columns.get(column)[ 2] << 16);
                    l &= 0xffffff;
                    l |= ((long) InfoLine.columns.get(column)[ 3] << 24);
                    flvalue1= Float.intBitsToFloat(l);
                    float flvalue2=Float.valueOf(value2).floatValue();
                    switch(conditions.get(i).op){
                        case Ls:if(flvalue1>=flvalue2) return false;break;
                        case Le:if(flvalue1>flvalue2) return false;	break;
                        case Gt:if(flvalue1<=flvalue2) return false;break;
                        case Ge:if(flvalue1<flvalue2) return false;	break;
                        case Eq:if(flvalue1!=flvalue2) return false;break;
                        case Ne:if(flvalue1==flvalue2) return false;break;
                    }
                    break;
            }
        }
        return true;
    }

    //创建表
    static public void createTable(Table tableInfo){
        try{
            String filename=tableInfo.tableName+".table";
            PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter(new File(filename))));
            out.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.err.println("创建表失败！");
        }

        System.out.println("创建表成功！");
    }

    //删除表
    public static void dropTable(String tableName){
        String filename = tableName + ".table";
        File file = new File(filename);

        try{
            if(file.exists())
                if(file.delete())
                    System.out.println("表文件已删除");
                else
                    System.out.println("文件"+filename+"没有找到");

            //与该表相关的索引文件全部删除
            Vector<String> allIndex=CatalogManager.relativeIndex(tableName);
            for(int i=0;i<allIndex.size();i++){
                String indexname = allIndex.elementAt(i) + ".index";
                File indexfile = new File(indexname);
                if(indexfile.exists())
                    if(indexfile.delete())
                        System.out.println("索引"+indexname+"删除");
            }
        }catch(Exception   e){
            System.err.println(e.getMessage());
            System.err.println("删除表失败！");
        }
    }

    //插入记录
    public static void insertValue(Table tableInfo,Record InfoLine) throws Exception{
        String filename=tableInfo.tableName+".table";

        BufferBlock blk=BufferManager.getInsertPosition(tableInfo,filename); //找到可插入位置
        if(blk==null){
            blk=BufferManager.createBlock(filename, tableInfo.blockNum);
            CatalogManager.addTableBlockNum(tableInfo.tableName);
        }

        //String tmpColumn;
        int pos = blk.recordNum * tableInfo.recordLength;
        for(int i=0;i<InfoLine.columns.size();i++){
            //长度不足时用&补齐，所以搜索时也要注意长度不足时用&补齐（对API的要求）
            blk.setBytes(pos,InfoLine.columns.get(i));
            pos+=tableInfo.attrlist[i].length;
        }

        //添加入所有相关的索引
        Vector<String> allIndex=CatalogManager.relativeIndex(tableInfo.tableName);
        for(int i=0;i<allIndex.size();i++){
            Index inx2 = CatalogManager.getIndex(allIndex.elementAt(i));
            Vector <byte[]> x = InfoLine.columns;

            IndexManager.insertKey(inx2,InfoLine.columns.get(inx2.column), blk.blockOffset, blk.recordNum);
        }

        blk.recordNum++;
        System.out.println("插入成功！");
    }

    //无条件查找全部属性输出
    public static void select(Table tableInfo) throws UnsupportedEncodingException{
        String filename=tableInfo.tableName+".table";
        Data datas=new Data();

        for(int blockOffset=0; blockOffset< tableInfo.blockNum; blockOffset++){
            BufferBlock block = BufferManager.readBlock(filename,blockOffset);
            for(int offset =0; offset < block.recordNum; offset++){
                int position = offset*tableInfo.recordLength;
                byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
                Record red=splitRecord(tableInfo,RecordLine); //进行拆分

                row Row=bytesToString(tableInfo,red);
                datas.Lines.add(Row);		//添加到展示数据中
            }
        }
        //展示查询结果
        showDatas(datas);
    }

    //条件查找全部属性输出
    public static void select(Table tableInfo,Vector<Condition> conditions) throws UnsupportedEncodingException{
        String filename=tableInfo.tableName+".table";
        Data datas=new Data();

        for(int blockOffset=0; blockOffset< tableInfo.blockNum; blockOffset++){
            BufferBlock block = BufferManager.readBlock(filename,blockOffset);
            for(int offset =0; offset < block.recordNum; offset++){
                int position = offset*tableInfo.recordLength;
                byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
                Record red=splitRecord(tableInfo,RecordLine);
                if(Compare(tableInfo,red,conditions)){ //满足比较条件的才添加到展示数据中
                    row Row=bytesToString(tableInfo,red);
                    datas.Lines.add(Row);
                }
            }
        }

        showDatas(datas);
    }

    //无条件删除
    public static void delete(Table tableInfo) {
        String filename = tableInfo.tableName + ".table";
        try{
            File file = new File(filename);
            FileWriter fw=new FileWriter(file);

            fw.write("");
            fw.flush();
            fw.close();

            //清空与表相关的索引的内容
            Vector<String> allIndex=CatalogManager.relativeIndex(tableInfo.tableName);
            for(int i=0;i<allIndex.size();i++){
                Index inx2 = CatalogManager.getIndex(allIndex.elementAt(i));

                IndexManager.dropIndex(allIndex.elementAt(i));
                CatalogManager.dropIndex(allIndex.elementAt(i));
                BufferManager.dropTable(allIndex.elementAt(i)+".index");

                CatalogManager.createIndex(inx2);
                IndexManager.createIndex(tableInfo,inx2);
            }

        }catch(Exception   e){
            System.err.println(e.getMessage());
            System.err.println("清空表失败！");
        }
        BufferManager.dropTable(filename);
        System.out.println("您已将表"+filename+"的内容清空了");
        //API在catalog里销毁tableInfo还要销毁所有建立其上的index？？？
    }

    //条件删除
    public static void delete(Table tableInfo,Vector<Condition> conditions) throws Exception{
        String filename=tableInfo.tableName+".table";
        int count=0;

        //找出与该表相关的所有索引
        Vector<String> allIndex=CatalogManager.relativeIndex(tableInfo.tableName);

        for(int blockOffset=0; blockOffset< tableInfo.blockNum; blockOffset++){
            BufferBlock block = BufferManager.readBlock(filename,blockOffset);
            for(int offset =0; offset < block.recordNum; offset++){
                int position = offset*tableInfo.recordLength;
                byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
                Record red=splitRecord(tableInfo,RecordLine);
                if(Compare(tableInfo,red,conditions)){    //满足条件
                    BufferManager.deleteValues(blockOffset,position,tableInfo); //请bufferManager来删除这条记录
                    offset--;
                    count++; //删除记录的条数加一
                    block.recordNum--;

                    //更新索引
                    for(int i=0;i<allIndex.size();i++){
                        Index inx2 = CatalogManager.getIndex(allIndex.elementAt(i));
                        IndexManager.deleteKey(inx2,red.columns.get(inx2.column));
                    }
                }
            }
        }

        System.out.println("删除成功！");
        System.out.println("共删除"+count+"条记录！");
    }

    //根据索引提供的位置信息进行查找全部属性输出
    public static void  selectFromIndex(Table tableInfo,offsetInfo off) throws UnsupportedEncodingException{
        String filename=tableInfo.tableName+".table";
        Data datas=new Data();

        if(off==null){
            System.out.println("不能从索引中找到");
            return;
        }

        BufferBlock block=BufferManager.readBlock(filename, off.offsetInfile);
        int position = off.offsetInBlock*tableInfo.recordLength;
        byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
        Record red=splitRecord(tableInfo,RecordLine);  //拆分
        row Row=bytesToString(tableInfo,red);
        datas.Lines.add(Row);	 //添加到展示数据

        showDatas(datas);  //展示
    }

    //根据索引提供的位置信息进行查找部分属性输出
    public static void  selectFromIndex(Table tableInfo,selectAttribute selections,offsetInfo off) throws UnsupportedEncodingException{
        String filename=tableInfo.tableName+".table";
        Data datas=new Data();

        if(off==null){
            System.out.println("不能从索引中找到");
            return;
        }

        BufferBlock block=BufferManager.readBlock(filename, off.offsetInfile);
        int position = off.offsetInBlock*tableInfo.recordLength;
        byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
        Record red=splitRecord(tableInfo,RecordLine);
        Record rred=red.selectRecord(tableInfo,selections); //挑选要展示的属性
        row Row=bytesToString(tableInfo,rred);
        datas.Lines.add(Row);

        showDatas(datas);
    }


    //按行输出查询结果，第一行为属性名（API负责输出属性名）
    public static void showDatas(Data datas){
        if(datas.Lines.size()==0){
            System.out.println("查询结果为空！");
            return;
        }

        for(int i=0;i<datas.Lines.size();i++){
            System.out.println(""+(i+1)+" ");
            for(int j=0;j<datas.Lines.get(i).columns.size();j++){
                System.out.print(datas.Lines.get(i).columns.get(j)+"\t");
            }
            System.out.println();
        }
    }

    public static boolean exist(Table tableInfo, Vector<Condition> conditions) throws UnsupportedEncodingException {
        String filename=tableInfo.tableName+".table";

        for(int blockOffset=0; blockOffset< tableInfo.blockNum; blockOffset++){
            BufferBlock block = BufferManager.readBlock(filename,blockOffset);
            for(int offset =0; offset < block.recordNum; offset++){
                int position = offset*tableInfo.recordLength;
                byte[] RecordLine =block.getBytes(position, tableInfo.recordLength);
                Record red=splitRecord(tableInfo,RecordLine);
                if(Compare(tableInfo,red,conditions)) //满足比较条件的才添加到展示数据中
                    return true;
            }
        }

        return false;
    }

}
