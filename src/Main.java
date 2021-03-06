import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
        CPU cpu = new CPU(memory);

        // other variables needed for simulation
        Scanner scanner = new Scanner(System.in);
        PrintWriter writer;
        Scanner fileReader;
        long start_time, end_time;
        DecimalFormat df = new DecimalFormat("#.##");

        // getting user file to execute
        printWelcome();
        try{
            // load the program file to memory
            String fileName = scanner.nextLine();
            fileReader = new Scanner(new File(fileName));
            System.out.print("Loading file...");
            while (fileReader.hasNextLine()){        // read the file to memory
                String line = fileReader.nextLine();
                line = line.trim();
                memory.insert_instruction(line);
            }
            System.out.println("Done!\n" +
                    "Executing file, this will take some time.");
            memory.setData_memory_start();           // instructions have been loaded, mark start of data memory
            start_time = System.currentTimeMillis(); // start timer
            while(!cpu.isDone()){                    // starting machine
                cpu.run();
            }
            // stop timer
            end_time = System.currentTimeMillis();
            System.out.println("Simulation finished in: " + df.format((end_time - start_time)*0.001) + " seconds.");

            // generating the log file.
            System.out.print("Writing log file. please wait...");
            writer = new PrintWriter(new File("execution.log"));
            for (String x : cpu.getLog()){
                writer.print(x);
                writer.print("\n");
            }
            writer.close();

            System.out.println("Done!\n" +
                    "Zero is shutting down.");

        }catch (IOException e){
            System.out.println("That file does not exist. Please restart Zero and try again.\n" +
                    "System shutting down...");
        }


    }

    private static void printWelcome(){
        System.out.println("Welcome to Machine Zero");
        System.out.print("Please enter a file location to execute:> ");

    }
}
