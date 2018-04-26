import java.util.Arrays;
import java.util.HashMap;

/**
 * CPU class
 *
 * @author Patrick Shinn
 * @version 4/21/18
 */
class CPU {
    private long CCT = 500; // clock cycle time in milliseconds
    private ArithmeticLogicUnit ALU = new ArithmeticLogicUnit();
    private PipelineStage[] pipline = {new PipelineStage(0),
            new PipelineStage(1),
            new PipelineStage(2),
            new PipelineStage(3),
            new PipelineStage(4)};
    private boolean done = false;
    private int pc = 0;
    private String[] instruction;
    private HashMap<String, Integer> register = new HashMap<>(), branch = new HashMap<>();

    CPU() {
        // creating the 32 registers
        register.put("x0", 0);
        register.put("ra", 0);
        register.put("sp", 0);
        register.put("gp", 0);
        register.put("tp", 0);
        register.put("t0", 0);
        register.put("t1", 0);
        register.put("t2", 0);
        register.put("s0", 0);
        register.put("s1", 0);
        register.put("a0", 0);
        register.put("a1", 0);
        register.put("a2", 0);
        register.put("a3", 0);
        register.put("a4", 0);
        register.put("a5", 0);
        register.put("a6", 0);
        register.put("a7", 0);
        register.put("s2", 0);
        register.put("s3", 0);
        register.put("s4", 0);
        register.put("s5", 0);
        register.put("s6", 0);
        register.put("s7", 0);
        register.put("s8", 0);
        register.put("s9", 0);
        register.put("s10", 0);
        register.put("s11", 0);
        register.put("t3", 0);
        register.put("t4", 0);
        register.put("t5", 0);
        register.put("t6", 0);
    }

    int next_pc() {
        return pc++; // return pc val then increment to the next one
    }

    void loadInstruction(String instruction) {
        try {
            this.instruction = instruction.split("\t");
            Wrapper wrapper = execute(this.instruction);
            if (register.containsKey(wrapper.getRegister())) {
                register.put(wrapper.getRegister(), wrapper.getValue()); // write the register value
            }
        }catch (NullPointerException | IndexOutOfBoundsException e){
            this.done = true;
        }
    }

    private Wrapper execute(String[] instruction) {
        System.out.println(Arrays.toString(instruction));
        String return_register = "", off_set = "";
        String[] args = instruction[1].split(",");
        int value = 0, load = 0;
        switch (instruction[0].trim()) {
            case "ADD":
                return_register = args[0].trim();
                value = register.get(args[1].trim()) + register.get(args[2].trim());
                break;
            case "ADDI":
                return_register = args[0].trim();
                value = register.get(args[1].trim()) + Integer.parseInt(args[2].trim());
                break;
            case "SUB":
                return_register = args[0].trim();
                value = register.get(args[1].trim()) - register.get(args[2].trim());
                break;
            case "SUBI":
                return_register = args[0].trim();
                value = register.get(args[1].trim()) - Integer.parseInt(args[2].trim());
                break;
            case "MUL":
                return_register = args[0].trim();
                value = register.get(args[1].trim()) * register.get(args[2].trim());
                break;
            case "BEQ":
                break;
            case "BNE":
                if (register.get(args[0].trim()) == register.get(args[1].trim())) value = 1;
                else{
                    value = 0;
                    pc = branch.get(args[1]) -1; // go to branch
                }
                return_register = instruction[0]; // return register returned as branch to take
                break;
            case "BNEZ":
                if (register.get(args[0].trim()) == 0) value = 1;
                else{
                    value = 0;
                    pc = branch.get(args[1]) -1; // go to branch
                }
                return_register = args[1];
                break;
            case "JAL":
                break;
            case "LB":
                return_register = args[0];
                off_set = args[1];
                load = 1;
                break;
            case "SB":
                return_register = args[0];
                off_set = args[1];
                load = 2;
                break;
            case "LW":
                return_register = args[0];
                off_set = args[1];
                load = 1;
                break;
            case "SW":
                return_register = args[0];
                off_set = args[1];
                load = 2;
                break;
            default: // we are declaring a loop
                // record where the loop starts
                if (!branch.containsKey(instruction[0].trim())) branch.put(instruction[0].trim(), pc);
                // execute the instruction
                return execute(Arrays.copyOfRange(instruction, 1, instruction.length));
        }
        return new Wrapper(return_register, value, load, off_set);
    }

    boolean isDone(){
        return done;
    }
}

class ArithmeticLogicUnit {
    ArithmeticLogicUnit(){

    }
}

/***
 * PipelineStage
 *
 * Represents the RISC V pipeline stages
 */
class PipelineStage {
    private boolean inUse = false;
    private String instruction;
    private Stage stage;

    PipelineStage(int stage){
        switch (stage){
            case 0:
                this.stage = Stage.IF;
                break;
            case 1:
                this.stage = Stage.ID;
            case 2:
                this.stage = Stage.EX;
            case 3:
                this.stage = Stage.MEM;
            case 4:
                this.stage = Stage.WB;
        }
    }

    String getInstruction() {
        return instruction;
    }

    void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    boolean isInUse() {
        return inUse;
    }

    void lock(){
        this.inUse = true;
    }

    void unlock(){
        this.inUse = false;
    }

    String getStage() {
        return stage.getName();
    }
}


/***
 * Stage
 *
 * enumerated stage names for convenience.
 */
enum Stage{
    IF ("Instruction Fetch"),
    ID ("Instruction Decode"),
    EX ("Execution"),
    MEM ("Memory Access"),
    WB ("Cache Write Back");

    private String name;
    Stage(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}

/***
 * Simple wrapper for register/data pairs that will make my life easier
 */
class Wrapper{
    private String register, offset;
    private int value, load;

    Wrapper(String register, int value, int load, String offset){ // used for load/store operations
        this.register = register;
        this.value = value;
        this.load = load;
        this.offset = offset;
    }

    String getRegister() {
        return register;
    }

    int getValue() {
        return value;
    }

    int getLoad(){
        return load;
    }
}
