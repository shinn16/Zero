/**
 * Wrapper class
 *
 * @author Patrick Shinn
 * @version 4/28/18
 *
 * Simple wrapper for register/data pairs that will make my life easier
 */
class Wrapper {
    private String register, loop;
    private int instruction;
    private Integer solution; // using the Integer class allows for null evaluation, int does not.
    private int[] value;

    Wrapper(String register, int[] value, int instruction, String loop){
        this.register = register;
        this.value = value;
        this.instruction = instruction;

        this.loop = loop;
    }

    String getRegister() {
        return register;
    }

    void setSolution(Integer solution){
        this.solution = solution;
    }

    Integer getSolution(){
        return solution;
    }

    int[] getValue() {
        return value;
    }


    int getInstruction(){ return instruction; }

    String getLoop(){ return loop; }
}
