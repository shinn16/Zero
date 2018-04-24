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

    private HashMap<String, String> register = new HashMap<>();
    public CPU(){
        // creating the registers
        register.put("X0", "0");
        register.put("X1", "");
        register.put("X2", "");
        register.put("X3", "");
        register.put("X4", "");
        register.put("X5", "");
        register.put("X6", "");
        register.put("X7", "");
        register.put("X8", "");
        register.put("X9", "");
        register.put("X10", "");
        register.put("X11", "");
        register.put("X12", "");
        register.put("X13", "");
        register.put("X14", "");
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
