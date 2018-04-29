/**
 * DataBus class
 *
 * @author Patrick Shinn
 * @version 4/28/18
 *
 * Simple wrapper for register/data pairs that will make my life easier
 */
class DataBus {
    private String register;
    private int instruction;
    private Integer solution; // using the Integer class allows for null evaluation, int does not.
    private int[] value;

    DataBus(String register, int[] value, int instruction){
        this.register = register;
        this.value = value;
        this.instruction = instruction;
    }

    String getRegister() { return register; }

    void setSolution(Integer solution){ this.solution = solution; }

    Integer getSolution(){ return solution; }

    int[] getValue() { return value; }

    int getInstruction(){ return instruction; }


    @Override
    public String toString(){
        return null;
    }
}
