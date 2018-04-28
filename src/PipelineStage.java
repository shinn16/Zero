/***
 * PipelineStage
 *
 * @author Patrick Shinn
 * @version 4/28/18
 *
 * Represents the RISC V pipeline stages
 */
class PipelineStage {
    private boolean inUse = false;
    private String instruction;
    private Stage stage;
    private Wrapper wrapper;

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

    void setWrapper(Wrapper wrapper){
        this.wrapper = wrapper;
    }

    Wrapper getWrapper(){
        return wrapper;
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
