import java.util.Arrays;

/**
 * Memory Class
 *
 * @author Patrick Shinn
 * @version 4/25/18
 */
class Memory {
    private String [] memory = new String[256];
    private int instruction_index = 0;
    private int data_index = 0;

    Memory(){
        // write all memory to 0
        for (int i = 0; i < 256; i ++){
            memory[i] = "0";
        }
    }

    /**
     * Used to add program instructions to memory.
     * Keeps program data and instructions separated.
     *
     * @param data String
     */
    void insert_instruction(String data){
        instruction_index %= 256; // prevent array index out of bounds
        memory[instruction_index] = data;
        instruction_index ++;
    }

    /**
     * Gets instruction from memory.
     *
     * @param index int: index
     * @return String: instruction
     */
    String getInstruction(int index){
        return memory[index];
    }

    /**
     * Used to separate program instructions and program
     * data.
     */
    void setData_memory_start() {
        this.data_index = instruction_index; // this sets the boundary for application data
    }

    /**
     * Inserts program data.
     *
     * @param data String
     * @param index int
     * @param offset int
     */
    void insert_data(String data,int index, int offset){
        index = (data_index + index + offset)%256;
        if (index < instruction_index){
            index= data_index; // prevent instructions from being overwritten
        }
        memory[index]  = data;
    }

    /**
     * Gets program data from memory.
     *
     * @param index int
     * @param offset int
     * @return String: data
     */
    String getdata(int index, int offset){
        index = (data_index + index + offset)%256;
        if (index < instruction_index) index= data_index + 1; // prevent instructions from being overwritten
        return memory[index];
    }

    /**
     * Used for debugging.
     *
     * @return String: memory array
     */
    @Override
    public String toString(){
        return Arrays.toString(memory);
    }

}
