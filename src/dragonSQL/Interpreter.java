package dragonSQL;

/**
 * Created by qi on 15/11/5.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Interpreter {
    //static String message;
    static Query query = new Query();

    /*------------------------辅助性方法-----------------------*/
    public static int filter(StringBuffer SQL, int beg)
    {
        while(SQL.charAt(beg)==' ')
            beg++;
        return beg;
    }
    private static boolean isBlank(String str) {//判断该字符串是否都是空格
        boolean flag = true;
        for(int i=0;i<str.length();i++)
            if(str.charAt(i)!=' ')
            {
                flag = false;
                break;
            }
        return flag;
    }
    static String toValid(String word)//把一个有效的单词转化
    {
        int start=0;
        StringBuffer t = new StringBuffer(word);
        start = filter(t,start);
        word += " ";
        int end = word.length() - 1;
        while(word.charAt(end) == ' ')
            end--;
        word = word.substring(start,end+1);
        return word;
    }
    static boolean isValid(String word)//判断是否是有效的名称
    {
        boolean flag = true;
        StringBuffer tmp = new StringBuffer(word);
        String p = tmp.toString();
        int start=0;
        start = filter(tmp,start);
        p += " ";
        int end = p.length() - 1;
        while(p.charAt(end) == ' ')
            end--;
        p = p.substring(start,end+1);
        for(int i=0;i<p.length();i++)
            if(p.charAt(i)<'a'||p.charAt(i)>'z')
                if(!(p.charAt(i)<='9'&&p.charAt(i)>='0')&&p.charAt(i)!='_'&&p.charAt(i)!='-')
                    flag = false;
        return flag;
    }
    private static boolean isValidNum(String word)//判断该字符串表示的是不是一个有效的数字
    {
        boolean flag = true;
        StringBuffer tmp = new StringBuffer(word);
        String p = tmp.toString();
        int start=0;
        start = filter(tmp,start);
        //tmp.append(' ');
        p += " ";
        int end = p.length() - 1;
        while(p.charAt(end) == ' ')
            end--;
        p = p.substring(start,end+1);
        for(int i=0;i<p.length();i++)
            if(p.charAt(i)<'0'||p.charAt(i)>'9')
                flag = false;
        if(Integer.parseInt(word) < 1 || Integer.parseInt(word) > 255)//char类型的长度必须在1-255之间
            flag = false;
        return flag;
    }
    /*------------------------依据第一个关键字判断-----------------------*/
    public static void Inter(String input) throws Exception{
		/*---------获取用户输入---------*/

        Attribute[] attr = new Attribute[35];

        String str = new String();
        char c;
        int beg = 0;//单词的起始位置
        int end = 0;//单词的结束位置
        if(!input.isEmpty())
        {
            str = input;
            str = str.replace("\t", " ");//替换字符串中的制表符等
            str = str.replace("\r", " ");
            str = str.replace("\n", " ");
        }
        else//读取用户的输入
        {
            while((c = (char)System.in.read())!=';')
            {
                if(c != '\t'&&c != '\r'&& c != '\n')
                    str += c;
                else
                    str += ' ';
            }
            str += c;//写入;号
            while((c = (char)System.in.read())!='\r')
            {
                continue;
            }
            c = (char)System.in.read();
        }
        StringBuffer SQL = new StringBuffer(str.toLowerCase());//转换成小写
        int last = SQL.lastIndexOf(";");
        if(SQL.length() > last + 1)
        {
            query.cmd="Error";
            System.out.println("Error：语法错误。请以';'结束语句");
            return ;
        }
        beg = filter(SQL,beg);
        end = SQL.indexOf(" ",beg);
        if(end == -1)
        {
            end = SQL.indexOf(";",beg);
            if(end == -1)
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。请输入指令");
                return ;
            }
        }

        str = SQL.substring(beg,end);//找到第一个关键字
        beg = end + 1;//将beg向后移
        switch(str)
        {
            case "create":query.attrNum = 0;createCase(SQL, beg);break;//attrNum记录属性的数量
            case "drop":dropCase(SQL, beg);break;
            case "select":query.attrNum = 0;select(SQL,beg);break;
            case "insert":query.attrNum = 0;insert(SQL,beg);break;
            case "delete":query.attrNum = 0;delete(SQL,beg);break;
            case "quit":query.cmd = "quit";break;
            case "execfile":query.cmd = "execfile";execfile(SQL,beg);break;
            case "help":query.cmd = "help";break;
            default:query.cmd = "Error";System.out.println("Error:语法错误。未知的指令。");break;
        }

        switch (query.cmd){
            case "createTable":
                API.createTable(query);
                break;
            case "createIndex":
                API.createIndex(query);
                break;
            case "dropTable":
                API.dropTable(query);
                break;
            case "dropIndex":
                API.dropIndex(query);
                break;
            case "select":
                API.select(query);
                break;
            case "insert":
                API.insert(query);
                break;
            case "delete":
                API.delete(query);
                break;
            case "quit":
                API.quit(query);
                break;
            case "execfile":
                break;
            case "Error":
                break;
            default:
                System.out.println("系统发生了一些事情");
                break;
        }
    }



    /*------------------------依据第二个关键字判断-----------------------*/
    //Create
    public static void createCase(StringBuffer SQL, int begin)
    {
        int beg = filter(SQL,begin);
        int end;
        String second_key;
        end = SQL.indexOf(" ",beg);
        if(end == -1)
        {
            //message = "Error";
            query.cmd = "Error";
            System.out.println("Error:语法错误。指令缺少参数");
            return;
        }
        second_key = SQL.substring(beg, end);//获取第二个关键字
        beg = end + 1;
        switch(second_key)
        {
            //case "database":create_database(SQL,beg);break;
            case "table":createTable(SQL, beg);break;
            case "index":createIndex(SQL, beg);break;
            default:query.cmd="Error";System.out.println("Error:语法错误。\"create "+second_key+"\"不是有效的指令");break;
        }
        return;
    }
    //Drop
    public static void dropCase(StringBuffer SQL, int begin)
    {
        int beg = filter(SQL,begin);
        int end;
        String second_key;
        end = SQL.indexOf(" ",beg);
        if(end == -1)
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。指令缺少参数");
            return;
        }
        second_key = SQL.substring(beg, end);
        beg = end + 1;
        //word.toLowerCase();//将输入的字符串全部转化为小写，方便判断
        switch(second_key)
        {
            //case "database":drop_database(SQL,beg);break;
            case "table":dropTable(SQL, beg);break;
            case "index":dropIndex(SQL, beg);break;
            default:query.cmd="Error";System.out.println("Error:语法错误。\"create "+second_key+"\"不是有效的指令");break;
        }
    }
    //Select
    public static void select(StringBuffer SQL, int begin)
    {
		/*------------------------语句解析----------------------------*/
        query.cmd = "select";
        int beg = filter(SQL,begin);
        int tmp;
        String att = null;
        String tab = null;
        String where = null;

        tmp = SQL.indexOf("from", beg);//获取from的下标
        if(tmp != -1)
        {
            att = SQL.substring(beg, tmp);//截取select和from中间的属性名称
            if(isBlank(att))
            {
                System.out.println("Error:语法错误。属性不能为空");
                query.cmd = "Error";
                return;
            }
            beg = tmp + 4;//beg移动到from之后
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error：语法错误。请选择属性");
            return;
        }

        beg = filter(SQL,beg);//查找from之后的表名
        tmp = SQL.indexOf("where", beg);
        if(tmp != -1)//有where
        {
            tab = SQL.substring(beg, tmp);//截取表名
            beg = tmp + 5;//beg移动到where之后
        }
        else//没有where
        {
            tmp = SQL.lastIndexOf(";");
            tab = SQL.substring(beg, tmp).trim();
            beg = tmp;
        }

        beg = filter(SQL,beg);
        tmp = SQL.lastIndexOf(";");
        where = SQL.substring(beg,tmp);
        if(isBlank(tab))//from之后没有表
        {
            query.cmd = "Error";
            System.out.println("Error：语法错误。请选择表");
            return;
        }
        if(isValid(tab))//检查表名是否有效
        {
            tab = toValid(tab);
            if(CatalogManager.isTable(tab))//检查表是否存在
            {
                query.tableName = tab;
            }
            else
            {
                query.cmd = "Error";
                System.out.println("Error:"+tab+"表不存在");
                return;
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。\""+tab+"\"不是一个有效的表名");
            return;
        }

		/*------------------------条件分析----------------------------*/
        if(!isBlank(where))
        {
            int b = 0;//begin
            int e;//end
            String t = null;
            while((e = where.indexOf("and",b)) != -1)//循环获取条件
            {
                t = where.substring(b,e);//获取一个条件
                b = e + 3;//begin移动到and后面
                int length = t.length()-1;
                while(t.charAt(length) == ' ')
                    length--;
                t = t.substring(0, length+1);//去掉后面可能的空格
                t = findCondition(t,tab);
                //query.attrNum++;
                //query.attrList[query.attrNum-1].
            }
        }
        //对条件的判定
        if(!isBlank(where))
        {
            //有筛选条件
            int b=0,e;
            String t = null;
            while((e = where.indexOf("and", b))!=-1)//不是最后一个条件
            {
                //获取一个条件
                t = where.substring(b, e);
                b = e + 3;
                int length = t.length()-1;
                while(t.charAt(length) == ' ')
                    length--;
                t = t.substring(0, length+1);
                findCondition(t,tab);
            }
            //最后一个条件
            t = where.substring(b);
            int length = t.length()-1;
            while(t.charAt(length) == ' ')
                length--;
            t = t.substring(0, length+1);
            t = toValid(t);
            findCondition(t,tab);
        }
    }
    //Insert
    public static void insert(StringBuffer SQL, int begin)
    {
        query.cmd = "insert";
        int beg = filter(SQL,begin);
        int end = SQL.indexOf(" ",beg);//获取第二个关键字
        if(end == -1)
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。\"Insert\"语句错误");
            return;
        }
        String str = SQL.substring(beg, end);
        beg = end + 1;
        if(str.equals("into"))
        {
            beg = filter(SQL,beg);//获取表名
            end = SQL.indexOf("values",beg);//获得关键字value的下标
            if(end == -1)//没有value关键字
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。缺少关键字\"Value\"");
                return;
            }
            else
            {
                str = SQL.substring(beg, end).trim();
                query.tableName = str;
                beg = end + 6;//跳过"value("    --------------------------------???---------------------------------------

                if(isBlank(str)){
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。缺少表名");
                    return;
                }
                if(isValid(str))//判断表名的有效性
                {
                    str = toValid(str);
                    if(CatalogManager.isTable(str))
                    {
                        beg = SQL.indexOf("(", beg);
                        if(beg == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。缺少\"(\"或者\")\"");
                            return;
                        }
                        beg += 1;
                        end = SQL.indexOf(")", beg);
                        if(end == -1)//没有')'
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。缺少\"(\"或者\")\"");
                            return;
                        }
                        if(beg == -1 || end == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。\"Insert\"语句错误");
                            return;
                        }
                        else
                        {
                            int count = 1;//记录属性个数，初始为1
                            while((end>beg)&&(!isBlank(SQL.substring(beg, end))))//循环获取属性
                            {
                                beg = filter(SQL,beg);
                                int p=SQL.indexOf(",", beg);
                                if(p == -1)
                                {
                                    p = SQL.indexOf(")", beg);
                                    if(p == -1)
                                    {
                                        query.cmd = "Error";
                                        System.out.println("Error:语法错误。缺少\")\"");
                                        return;
                                    }
                                }
                                String t = SQL.substring(beg, p);//获取一个属性
                                beg = p + 1;
                                if(CatalogManager.matchType(t,str,count))
                                {
                                    if(t.charAt(0)=='\''&&t.charAt(t.length()-1)=='\'')
                                        t=t.substring(1, t.length()-1);
                                    query.attrNum++;
                                    query.attrList[query.attrNum-1].value = t;
                                    count++;
                                }
                                else
                                {
                                    query.cmd = "Error";
                                    System.out.println("Error:语法错误。\""+t+"\"与属性不匹配");
                                    return;
                                }
                            }
                        }
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。\""+str+"\"表不存在");
                        return;
                    }
                }
                else
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\""+str+"\"不是有效的表名");
                    return;
                }
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。缺少关键字\"into\"");
            return;
        }
    }
    //Delete
    static void delete(StringBuffer SQL,int begin)
    {
        //message = "delete ";
        query.cmd = "delete";
        String tab = null;//表名
        int beg = filter(SQL,begin);//获取第二个单词
        int end = SQL.indexOf(" ",beg);
        if(end == -1)
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。\"Delete\"语句错误");
            return;
        }
        String str = SQL.substring(beg, end);
        beg = end + 1;
        if(str.equals("from"))
        {
            beg = filter(SQL,beg);//获取表名
            end = SQL.indexOf("where", beg);
            if(end == -1)//缺少where
            {
                end = SQL.indexOf(";");
                if(end == -1)
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\"Delete\"语句错误");
                    return;
                }
                str = SQL.substring(beg, end).trim();
                tab = SQL.substring(beg, end).trim();
                if(isValid(str))//检查是否有效
                {
                    str = toValid(str);//检查是否存在
                    if(CatalogManager.isTable(str))
                    {
                        query.tableName = str;
                        System.out.println("query.tableName:" + query.tableName);
                        return;
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。表\""+str+"\"不存在");
                        return;
                    }
                }
                else
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\""+str+"\"不是一个有效的表名");
                    return;
                }
            }
            else//有where
            {
                str = SQL.substring(beg, end).trim();
                query.tableName = str;
                if(isValid(str))
                {
                    str = toValid(str);
                    if(CatalogManager.isTable(str))
                    {
                        beg = end +5;
                        String t;
                        int length ;
                        while((end = SQL.indexOf("and", beg))!=-1)//不是最后一个条件
                        {
                            t=SQL.substring(beg, end).trim();
                            length = t.length()-1;
                            while(t.charAt(length)==' ')
                                length--;
                            t=t.substring(beg, length+1);
                            t = findCondition(t,str);
                            if(query.cmd == "Error")//出错了
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。");
                                return;
                            }
                            beg = end + 3;
                        }
                        end = SQL.indexOf(";");//最后一个条件
                        if(end == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。\"Delete\"语句错误");
                            return;
                        }
                        t=SQL.substring(beg, end).trim();
                        t =toValid(t);
                        t = findCondition(t,str);
                        return;
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。表\""+str+"\"不存在");
                        return;
                    }
                }
                else
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\""+str+"\"不是一个有效的表名");
                    return;
                }
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。缺少关键字\"from\"");
            return;
        }
    }
    //Execfile
    static void execfile(StringBuffer SQL,int begin) throws Exception
    {
        //读取文件名（第二个）
        int beg = filter(SQL,begin);
        int end = SQL.indexOf(";",beg);
        if(end == -1)
        {
            query.cmd = "Error";
            System.out.println("Error:文件名错误");
            return;
        }
        String str = SQL.substring(beg, end);
        int length = str.length()-1;
        while(str.charAt(length) == ' ')
            length--;
        str=str.substring(0, length+1);
        String lineTxt = null;
        String f = new String();
        try {
            File file = new File(str);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    f +=lineTxt;//将该行存入f中
                }
                read.close();
            } else {
                query.cmd = "Error";
                System.out.println("Error:找不到文件");
                return;
            }
        } catch (Exception e) {
            query.cmd = "Error";
            System.out.println("Error:文件包含错误");
            e.printStackTrace();
            return;
        }
        //将读入的文件内容分为多条语句
        int s=0;//start
        int e=0;//end
        StringBuffer t = new StringBuffer(f);
        while((e=t.indexOf(";",s))!=-1)//没有读完指令
        {
            s=filter(t,s);
            String p = t.substring(s, e+1);
            s = e + 1;
            Interpreter.Inter(p);
        }
    }

    //create_table
    static void createTable(StringBuffer SQL,int begin)
    {
        int beg,end;
        String str;
        query.cmd="createTable";
        //message="CreateTable ";
        end = SQL.indexOf("(");
        if(end == -1)
        {
            query.cmd="Error";
            System.out.println("Error:语法错误。找不到相应属性");
            return;
        }
        else
        {
            str = SQL.substring(begin, end).trim();//获取表名
            str = str.trim();
            query.tableName = str;
            beg = end + 1;
            if(isValid(str) == true)//表名称是有效的
            {
                str = toValid(str);
                //判断该表是不是已经在数据库中建立了，如果没有则反馈错误信息
                if(CatalogManager.isTable(str) == true)
                {
                    query.cmd="Error";
                    System.out.println("Error:语法错误。表\""+str +"\"已创建");
                    return;
                }
                else
                {
                    //message+=(str + " ");
                    //接下来添加属性
                    addAttribution(SQL, beg);
                    return;
                }
            }
            else
            {
                query.cmd="Error";
                System.out.println("Error:语法错误。\""+str +"\"不是一个有效的表名");
                return;
            }
        }
    }
    //create_index
    static void createIndex(StringBuffer SQL,int begin)
    {
        //找到index名
        query.cmd = "createIndex";
        query.attrNum++;
        int start = filter(SQL,begin);
        int end = SQL.indexOf(" ", start);
        if(end == -1)
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。缺少索引名称");
            return;
        }
        String word = SQL.substring(start, end);
        if(isValid(word))//index名有效
        {
            word = toValid(word);
            if(CatalogManager.isIndex(word))
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。索引 \""+word+"\"已存在");
                return;
            }
            query.indexName = word;//获取索引名
            start = filter(SQL,end);
            end = SQL.indexOf(" ", start);
            if(end == -1)
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。\"Create index\"命令错误");
                return;
            }
            word = SQL.substring(start, end);
            //判断有没有on
            if(word.equals("on"))
            {
                //找到表名
                start = filter(SQL,end);
                end = SQL.indexOf("(");
                if(end == -1)
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。缺少表名");
                    return;
                }
                else
                {
                    //判断表名是否是已经生成的表
                    word = SQL.substring(start,end).trim();
                    if(isValid(word))//如果表名是有效的
                    {
                        word = toValid(word);
                        if(CatalogManager.isTable(word) == true)//如果表是已经生成的表
                        {
                            //表名有效,判断是否属性有效
                            query.tableName = word;
                            start = filter(SQL,end + 1);
                            end = SQL.indexOf(")", start);
                            if(end == -1)
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。缺少\")\"");
                                return;
                            }
                            String wordatt = SQL.substring(start, end);
                            if(isValid(wordatt))
                            {
                                wordatt = toValid(wordatt);
                                //属性名有效，判断是否是该表的属性
                                if(CatalogManager.isAttribution(word,wordatt))
                                {
                                    query.attrList[0].name = word;
                                    return;
                                }
                                else
                                {
                                    query.cmd = "Error";
                                    System.out.println("Error:语法错误。"+"\""+wordatt+"\""+"不是"+word+"的属性");
                                    return;
                                }
                            }
                            else
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。\""+wordatt+"\"" +"不是有效的属性");
                                return;
                            }
                        }
                        else
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。\""+word+"\""+"表不存在");
                            return;
                        }
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。\""+word+"\""+"不是有效的表名");
                        return;
                    }
                }
            }
            else
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。\"create index\"命令错误");
                return;
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。\""+word+"\""+"不是有效的Index名称");
            return;
        }
    }
    //drop_table
    static void dropTable(StringBuffer SQL,int begin)
    {
        int end;
        String word;
        query.cmd = "dropTable";
        end = SQL.lastIndexOf(";");
        word = SQL.substring(begin, end).trim();
        if(isValid(word) == true)//表名称是有效的
        {
            word = toValid(word);
            //判断该表是不是已经在数据库中建立了，如果没有则反馈错误信息
            if(CatalogManager.isTable(word) == true)
            {
                query.tableName = word;
                return;
            }
            else
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。表"+"\""+word+"\"" +"不存在");
                return;
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。"+word + "不是有效的表名 ");
            return;
        }
    }
    //drop_index
    static void dropIndex(StringBuffer SQL,int begin)
    {
        int end;
        String word;
        query.cmd = "dropIndex";
        end = SQL.lastIndexOf(";");
        word = SQL.substring(begin, end).trim();
        if(isValid(word) == true)//检查索引名称是否是有效的
        {
            word = toValid(word);
            //判断索引是不是已经建立了，如果没有则反馈错误信息
            if(CatalogManager.isIndex(word) == true)
            {
                query.indexName = word;
            }
            else
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。索引"+"\""+word+"\"" +"不存在");
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。"+word + "不是有效的索引名称 ");
        }
    }

    //Add_attribution
    static void addAttribution(StringBuffer SQL,int begin)
    {
        int start = filter(SQL,begin);
        String att;
        String word;
        int end;
        while((end = SQL.indexOf(",",start)) != -1)//还没有到最后一行属性
        {
            if(query.cmd.equals("Error"))
                break;
            att = SQL.substring(start, end);//获取列名
            end = att.indexOf(' ');
            if(end == -1)
            {
                query.cmd = "Error";
                //message = "Error";
                System.out.println("Error:语法错误。缺少属性名称");
                return;
            }
            word = SQL.substring(start, start+end);//获取属性名
            query.attrNum++; //属性数量增加
            //Attribute x = query.attrList[0];
            query.attrList[query.attrNum-1].name = word;
            start = end +start+ 1;//start移动到类型的起点
            if(isValid(word))
            {
                word = toValid(word);
                //判断类型
                start = filter(SQL,start);
                if((SQL.indexOf(" ",start)<SQL.indexOf(",",start))&&(SQL.indexOf(" ", start)>=0))
                    end = SQL.indexOf(" ",start);//有unique
                else
                    end = SQL.indexOf(",",start);//无unique
                if(end == -1)
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。缺少属性类型");
                    return;
                }
                word = SQL.substring(start, end);
                //start += end +1;
                if(word.equals("int"))
                {
                    query.attrList[query.attrNum-1].type = 1;//记录属性为int类型
                    query.attrList[query.attrNum-1].length = 4;//int类型长度属性为4
                    start = end;
                }
                else if(word.equals("float"))
                {
                    query.attrList[query.attrNum-1].type = 2;//记录属性为float类型
                    query.attrList[query.attrNum-1].length = 4;//float类型长度属性为4
                    start = end;
                }
                else if(word.startsWith("char"))//char类型
                {
                    query.attrList[query.attrNum-1].type = 3;
                    word = word.replace(" ","");
                    int s,e;
                    s = SQL.indexOf("(",start);
                    e = SQL.indexOf(")",start);
                    start += end;
                    if(s==-1||e==-1)//缺少'('或者')'
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。\""+word+"\"不是有效的属性类型");
                        return;
                    }
                    else
                    {
                        String num = SQL.substring(s+1,e);//获取char类型的长度
                        if(isValidNum(num))//检测是否是有效的数字
                        {
                            num = toValid(num);
                            query.attrList[query.attrNum-1].length = Integer.parseInt(num);
                            start = e + 1;
                        }
                        else
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。\""+num +"\""+"不是有效的数字");
                            return;
                        }
                    }
                }
                else
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\""+word +"\""+"不是有效的变量类型");
                    return;
                }
                //判断是不是unique
                if(!query.cmd.equals("Error"))
                {
                    //start = end + 1;
                    end = SQL.indexOf(",",start);
                    if(end == -1)
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。变量格式错误");
                        return;
                    }
                    word = SQL.substring(start, end);
                    if(isBlank(word))//语句中没有unique
                        query.attrList[query.attrNum-1].unique = false;
                    else
                    {
                        //判断是不是有效的单词
                        if(isValid(word))
                        {
                            word = toValid(word);
                            if(word.equals("unique"))
                            {
                                query.attrList[query.attrNum-1].unique = true;
                                start += end+1;
                            }
                            else
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。\""+word+"\""+"不是关键字");
                                return;
                            }
                        }
                        else
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。\""+word+"\""+"不是有效的关键字");
                            return;
                        }
                    }
                }
            }
            else
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。\""+word+"\""+"不是有效的属性名");
                return;
            }
            start = filter(SQL,end+1);
        }
        if(!query.cmd.equals("Error"))//最后一行属性
        {
            //获取第一个单词
            end = SQL.lastIndexOf(")");
            if(end == -1)
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。命令缺少\")\"");
                return;
            }
            att = SQL.substring(start, end);
            end = SQL.indexOf(" ",start);//获取列名
            if(end == -1)
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。最后一行属性格式错误");
                return;
            }
            word = SQL.substring(start, end);
            start = end + 1;
            if(isValid(word))
            {
                word = toValid(word);
                //判断是不是primary
                if(word.equals("primary"))//是primary key
                {
                    start = filter(SQL,start);
                    end = SQL.indexOf(" ",start);
                    if(end == -1)
                    {
                        end = SQL.indexOf("(",start);
                        if(end == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。命令缺少\"(\"");
                            return;
                        }
                    }
                    word = SQL.substring(start, end);
                    //start = end ;
                    if(word.startsWith("key"))
                    {
                        if(word.endsWith("key"))
                            start=end;
                        start = SQL.indexOf("(", start);
                        end = SQL.indexOf(")",start);
                        if(start == -1 || end == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。最后一行属性格式错误");
                            return;
                        }
                        else
                        {
                            word = SQL.substring(start+1, end);
                            if(isValid(word))//如果是正确格式的键值属性
                            {
                                word = toValid(word);
                                boolean flag = false;
                                int i;
                                for(i = 0; i <= query.attrNum-1; i++)//检查这个属性在属性列表中是否存在
                                {
                                    if(word.equals(query.attrList[i].name)) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if(flag)
                                {
                                    query.attrList[i].unique = true;
                                    query.attrList[i].isPrimaryKey = true;
                                    query.primarykey = query.attrList[i].name;
                                }
                                else
                                {
                                    query.cmd = "Error";
                                    System.out.println("Error:语法错误。"+word+"属性未定义");
                                }
                            }
                            else
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。"+word+"不是有效的词语");
                                return;
                            }
                        }
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。"+word+"不是有效的词语");
                        return;
                    }
                }
                else//只是普通属性
                {
                    //判断类型
                    query.attrNum++;
                    query.attrList[query.attrNum-1].name = word;
                    start = filter(SQL,start);
                    if((SQL.indexOf(" ",start)<SQL.indexOf(")",start))&&(SQL.indexOf(" ",start)>0))
                        end = SQL.indexOf(" ",start);
                    else
                        end = SQL.indexOf(")",start);
                    if(end == -1)
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。最后一行属性格式错误");
                        return;
                    }
                    word = SQL.substring(start, end);
                    if(isValid(word))
                        word = toValid(word);
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。"+word+"不是有效的类型");
                    }
                    //start = end +1;
                    if(word.equals("int"))
                    {
                        query.attrList[query.attrNum-1].type = 1;
                        start = end ;
                    }
                    else if(word.equals("float"))
                    {
                        query.attrList[query.attrNum-1].type = 2;
                        start = end ;
                    }
                    else if(word.startsWith("char"))//char类型
                    {
                        query.attrList[query.attrNum-1].type = 3;
                        int s,e;
                        s = SQL.indexOf("(",start);
                        e = SQL.indexOf(")",start);
                        if(s==-1||e==-1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。"+word+"不是有效的属性类型");
                            return;
                        }
                        else
                        {
                            String num = SQL.substring(s+1,e);
                            if(isValidNum(num))
                            {
                                num = toValid(num);
                                query.attrList[query.attrNum-1].length = Integer.parseInt(num);
                                start = e + 1;
                            }
                            else
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。"+num+"不是有效的数字");
                                return;
                            }
                        }
                    }
                    else
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。"+word+"不是有效的变量类型");
                        return;
                    }
                    //判断是不是unique
                    if(!query.cmd.equals("Error"))
                    {
                        start = end;
                        end = SQL.indexOf(")",start);
                        if(end == -1)
                        {
                            query.cmd = "Error";
                            System.out.println("Error:语法错误。命令缺少\")\"");
                            return;
                        }
                        word = SQL.substring(start, end);
                        if(isBlank(word))
                            query.attrList[query.attrNum-1].unique = false;
                        else
                        {
                            //判断是不是有效的单词
                            if(isValid(word))
                            {
                                word = toValid(word);
                                if(word.equals("unique"))
                                    query.attrList[query.attrNum-1].unique = true;
                                else
                                {
                                    query.cmd = "Error";
                                    System.out.println("Error:语法错误。"+word+"不是一个关键字");
                                    return;
                                }
                            }
                            else
                            {
                                query.cmd = "Error";
                                System.out.println("Error:语法错误。"+word+"不是有效的关键字");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    static String findCondition(String t,String table)//分解筛选条件
    {
        //找到属性
        String tmp = null;
        StringBuffer k = new StringBuffer(t);
        int i = 0;
        int index;
        for(i = 0;i<t.length();i++)
            if(t.charAt(i)=='>'||t.charAt(i)=='<'||t.charAt(i)=='=')
                break;
        if(i==t.length())//没有找到'>''<''='等符号
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。选择条件出错");
            tmp = "err";
            return tmp;
        }
        String att = t.substring(0, i);//获得属性名
        if(isValid(att))
        {
            att=toValid(att);
            //判断该属性是不是该表的属性
            if(!CatalogManager.isAttribution(table, att))
            {
                query.cmd = "Error";
                System.out.println("Error:语法错误。\""+att+"\"不是表"+table+"的属性");
                tmp = "err";
                return tmp;
            }
            else
            {
                //有效的该表的属性
                //att = toValid(att);
                query.attrNum++;
                query.attrList[query.attrNum-1].name = att;//把选择条件添加到query中
                tmp = (att+"");
                //判断操作符
                i=filter(k,i);
                index = i;
                for(;i<t.length();i++)
                    if(t.charAt(i)!='>'&&t.charAt(i)!='<'&&t.charAt(i)!='=')
                        break;
                if(i==t.length())//没有找到op
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。找不到运算符");
                    tmp = "err";
                    return tmp;
                }
                String op = t.substring(index, i);
                if(!(op.equals("<")||op.equals(">")||op.equals("=")||op.equals("<=")||op.equals(">=")||op.equals("<>")))
                {
                    query.cmd = "Error";
                    System.out.println("Error:语法错误。\""+op+"\"不是有效的运算符");
                    tmp = "err";
                    return tmp;
                }
                else
                {
                    //有效的操作符
                    tmp += (op+"");
                    //将操作符保存在query中
                    if(op.equals("<"))
                        query.attrList[query.attrNum-1].signal = Comparison.Ls;
                    if(op.equals("="))
                        query.attrList[query.attrNum-1].signal = Comparison.Eq;
                    if(op.equals(">"))
                        query.attrList[query.attrNum-1].signal = Comparison.Gt;
                    if(op.equals("<="))
                        query.attrList[query.attrNum-1].signal = Comparison.Le;
                    if(op.equals(">="))
                        query.attrList[query.attrNum-1].signal = Comparison.Ge;
                    if(op.equals("<>"))
                        query.attrList[query.attrNum-1].signal = Comparison.Ne;
                    //判断操作数
                    i=filter(k,i);
                    index = i;
                    String num = t.substring(index);
                    num=toValid(num);
                    if(!CatalogManager.Type(att, num, table))
                    {
                        query.cmd = "Error";
                        System.out.println("Error:语法错误。\""+num+"\"和\""+att+"\"的属性不匹配");
                        tmp = "err";
                        return tmp;
                    }
                    else
                    {
                        if(num.charAt(0)=='\'')
                            num=num.substring(1, num.length()-1);
                        query.attrList[query.attrNum-1].value = num;//获得属性的值
                        tmp += (num);
                    }
                }
            }
        }
        else
        {
            query.cmd = "Error";
            System.out.println("Error:语法错误。"+att+"不是有效的属性名");
            tmp = "err";
            return tmp;
        }
        return tmp;
    }
}
