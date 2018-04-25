import java.util.HashMap;

/**
 * CPU class
 *
 * @author Patrick Shinn
 * @version 4/21/18
 */
public class CPU implements Runnable{
    private long CCT = 500; // clock cycle time in milliseconds
    private ArithmeticLogicUnit ALU = new ArithmeticLogicUnit();
    private PipelineStage[] pipline = { new PipelineStage(0),
                                        new PipelineStage(1),
                                        new PipelineStage(2),
                                        new PipelineStage(3),
                                        new PipelineStage(4)};

    private HashMap<String, Integer> register = new HashMap<>();
    public CPU(){
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


    @Override
    public void run() {

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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void lock(){
        this.inUse = true;
    }

    public void unlock(){
        this.inUse = false;
    }

    public String getStage() {
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

    public String getName() {
        return name;
    }
}
