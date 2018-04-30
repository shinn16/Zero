/**
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
    private DataBus dataBus;

    PipelineStage(int stage){
        switch (stage){
            case 0:
                this.stage = Stage.IF;
                break;
            case 1:
                this.stage = Stage.ID;
                break;
            case 2:
                this.stage = Stage.EX;
                break;
            case 3:
                this.stage = Stage.MEM;
                break;
            case 4:
                this.stage = Stage.WB;
                break;
        }
    }

    /**
     * Gets the instruction held in the pipeline stage.
     *
     * @return String: instruction
     */
    String getInstruction() {
        return instruction;
    }

    /**
     * Sets the dataBus object.
     * @param dataBus: DataBus
     */
    void setDataBus(DataBus dataBus){
        this.dataBus = dataBus;
    }

    /**
     * Gets the dataBus for the pipeline stage.
     * @return DataBus
     */
    DataBus getDataBus(){
        return dataBus;
    }

    /**
     * Sets the instruction in the pipeline. This does not have anything to
     * do with execution, this is purely for logging purposes.
     * @param instruction String
     */
    void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * Locks the pipe instruction
     */
    void lock(){
        this.inUse = true;
    }

    /**
     * Unlocks the pipe for instruction write
     */
    void unlock(){
        this.inUse = false;
    }

    /**
     * Gets the stage name
     * @return String: stage name
     */
    private String getStage() {
        return stage.getName();
    }

    @Override
    public String toString(){
        return getStage() + ": " + getInstruction();
    }
}


/**
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
