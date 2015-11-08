package dragonSQL;

import java.util.Scanner;

/**
 * Created by qi on 15/10/31.
 */

public class main {

    public static void welcome(){
        System.out.println("***********************************************************************");
        System.out.println("\t\t\t\tWelcome to DragonSQL!");
        System.out.println("\t\t\t\t\tVersion(1.0)");
        System.out.println("\t\t\t\t");
        System.out.println("\t\t\tcopyright(2015) all right reserved!");
        System.out.println("***********************************************************************");
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        CatalogManager catalog = new CatalogManager();
        welcome();
        String query = new String();

        Scanner in = new Scanner(System.in);
        System.out.print("DragonSQL-->");

        query = in.nextLine();
        while (query != null){
            try{
                Interpreter.Inter(query);
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.print("DragonSQL-->");
            query = in.nextLine();
        }
    }

}
