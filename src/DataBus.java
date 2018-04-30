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

    /**
     * Gets the string value for the register
     * @return String: register
     */
    String getRegister() { return register; }

    /**
     * Sets the solution to the instruction, AKA the output of the ALU.
     * @param solution Integer
     */
    void setSolution(Integer solution){ this.solution = solution; }

    /**
     * Gets the solution
     * @return int: solution
     */
    Integer getSolution(){ return solution; }

    /**
     * Gets the value array, AKA the register arguments
     * @return int[]: values
     */
    int[] getValue() { return value; }

    /**
     * Gets the stored instruction
     * @return String: instruction
     */
    int getInstruction(){ return instruction; }


    @Override
    public String toString(){
        return null;
    }
}
