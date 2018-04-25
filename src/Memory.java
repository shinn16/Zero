import java.util.Arrays;

/**
 * Memory Class
 *
 * @author Patrick Shinn
 * @version 4/25/18
 */
class Memory {
    private String [] memory = new String[256];
    private int current_index = 0;

    Memory(){};

    void insert(String data){
        if (current_index == memory.length) current_index = 0; // prevent array index out of bounds
        memory[current_index] = data;
        current_index ++;
    }

    String getAtIndex(int index){
        return memory[index];
    }

    @Override
    public String toString(){
        return Arrays.toString(memory);
    }

}
