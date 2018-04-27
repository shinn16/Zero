import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * Main class
 *
 * @author Patrick Shinn
 * @version 4/21/18
 */
public class Main {
    public static void main(String[] args){
        // instantiating machine hardware
        Memory memory = new Memory();
        CPU cpu = new CPU();

        // other variables needed for simulation
        Scanner scanner = new Scanner(System.in);
        Scanner fileReader;
        long start_time, end_time;
        DecimalFormat df = new DecimalFormat("#.##");

        // getting user file to execute
        printWelcome();
        try{
            // load the program file to memory
            // String fileName = scanner.nextLine();
            String fileName = "program.txt";
            fileReader = new Scanner(new File(fileName));
            while (fileReader.hasNextLine()){ // read the file to memory
                String line = fileReader.nextLine();
                line = line.trim();
                memory.insert_instruction(line);
            }
            // start timer
            start_time = System.currentTimeMillis();

            // loading first instruction
            // todo remove debug
            int counter = 0;
            while(!cpu.isDone()){
                cpu.instruction_fetch(memory.getAtIndex(cpu.next_pc()));
                counter ++;
            }
            // stop timer
            // todo remove debug
            System.out.println(counter);
            end_time = System.currentTimeMillis();
            System.out.println("Simulation finished in: " + df.format((end_time - start_time)*0.001) + " seconds.");




        }catch (FileNotFoundException e){
            System.out.println("That file does not exist. Please restart Zero and try again.\n" +
                    "System shutting down...");
        }


    }

    private static void printWelcome(){
        System.out.println("Welcome to Machine Zero");
        System.out.print("Please enter a file location to execute:> ");

    }
}
