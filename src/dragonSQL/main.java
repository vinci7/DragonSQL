package dragonSQL;

import java.util.Scanner;

/**
 * Created by qi on 15/10/31.
 */

public class main {

    public static void welcome(){
        System.out.println("***********************************************************************");
        System.out.println("\t\tWelcome to DragonSQL!");
        System.out.println("\t\tVersion(1.0)");
        System.out.println("\t\t");
        System.out.println("copyright(2015) all right reserved!");
        System.out.println("***********************************************************************");
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        welcome();
        String query = new String();
        Scanner in = new Scanner(System.in);
        System.out.print("DragonSQL-->");

        try{
            Interpreter.main(query);
        } catch (Exception e){
            e.printStackTrace();
        }


        /*
        while (query != null){
            try{
                Interpreter.main(query);
            } catch (Exception e){
                e.printStackTrace();
            }
            System.out.print("DragonSQL-->");
            query = in.nextLine();
        }
        */
    }

}
