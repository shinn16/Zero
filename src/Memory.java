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

    Memory(){};

    void insert_instruction(String data){
        instruction_index %= 256; // prevent array index out of bounds
        memory[instruction_index] = data;
        instruction_index++;
    }

    String getAtIndex(int index){
        return memory[index];
    }

    void setData_memory_start() {
        this.data_index = instruction_index; // this sets the boundary for application data
    }

    void insert_data(String data, int offset){
        data_index%=256;
        if (data_index < instruction_index) data_index = instruction_index; // prevent instructions from being overwritten
        memory[data_index + offset]  = data;
    }

    @Override
    public String toString(){
        return Arrays.toString(memory);
    }

}
