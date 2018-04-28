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

    void insert_instruction(String data){
        instruction_index %= 256; // prevent array index out of bounds
        memory[instruction_index] = data;
        instruction_index ++;
    }

    String getInstruction(int index){
        return memory[index];
    }

    void setData_memory_start() {
        this.data_index = instruction_index; // this sets the boundary for application data
    }

    void insert_data(String data,int index, int offset){
        index = (data_index + index + offset)%256;
        if (index < instruction_index){
            index= data_index; // prevent instructions from being overwritten
            System.out.println(Arrays.toString(memory));
        }
        memory[index]  = data;
    }

    String getdata(int index, int offset){
        index = (data_index + index + offset)%256;
        if (index < instruction_index) index= data_index + 1; // prevent instructions from being overwritten
        return memory[index];
    }

    @Override
    public String toString(){
        return Arrays.toString(memory);
    }

}
