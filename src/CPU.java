import java.util.Arrays;
import java.util.HashMap;

/**
 * CPU class
 *
 * @author Patrick Shinn
 * @version 4/21/18
 */
class CPU {
    private long CCT = 500; // clock cycle time in milliseconds
    private PipelineStage[] pipline = {new PipelineStage(0),
            new PipelineStage(1),
            new PipelineStage(2),
            new PipelineStage(3),
            new PipelineStage(4)};
    private boolean done = false;
    private int pc = 0;
    private String[] instruction_register;
    private HashMap<String, Integer> register = new HashMap<>(), branch = new HashMap<>();
    private Memory memory = new Memory();

    CPU(Memory memory) {
        this.memory = memory;

        // creating the 32 registers
        // i create both name reference and register reference so either can be used.

        // these registers are called by register
        register.put("x0", 0);
        register.put("x1", 0);
        register.put("x2", 0);
        register.put("x3", 0);
        register.put("x4", 0);
        register.put("x5", 0);
        register.put("x6", 0);
        register.put("x7", 0);
        register.put("x8", 0);
        register.put("x9", 0);
        register.put("x10", 0);
        register.put("x11", 0);
        register.put("x12", 0);
        register.put("x13", 0);
        register.put("x14", 0);
        register.put("x15", 0);
        register.put("x16", 0);
        register.put("x17", 0);
        register.put("x18", 0);
        register.put("x19", 0);
        register.put("x20", 0);
        register.put("x21", 0);
        register.put("x22", 0);
        register.put("x23", 0);
        register.put("x24", 0);
        register.put("x25", 0);
        register.put("x26", 0);
        register.put("x27", 0);
        register.put("x28", 0);
        register.put("x29", 0);
        register.put("x30", 0);
        register.put("x31", 0);

        // the rest are all called by name
        register.put("zero", 0);
        register.put("ra", 0);
        register.put("sp", 0);
        register.put("gp", 0);
        register.put("tp", 0);
        register.put("t0", 0);
        register.put("t1", 0);
        register.put("t2", 0);
        register.put("s0", 0);
        register.put("s1", 0);
        register.put("a0", 0);
        register.put("a1", 0);
        register.put("a2", 0);
        register.put("a3", 0);
        register.put("a4", 0);
        register.put("a5", 0);
        register.put("a6", 0);
        register.put("a7", 0);
        register.put("s2", 0);
        register.put("s3", 0);
        register.put("s4", 0);
        register.put("s5", 0);
        register.put("s6", 0);
        register.put("s7", 0);
        register.put("s8", 0);
        register.put("s9", 0);
        register.put("s10", 0);
        register.put("s11", 0);
        register.put("t3", 0);
        register.put("t4", 0);
        register.put("t5", 0);
        register.put("t6", 0);
    }

    private int next_pc() {
        pc %= 256;   // ensure we don't go out of memory
        return pc++; // return pc val then increment to the next one
    }

    void run(){
        // execute one clock cycle
//        instruction_fetch(memory.getInstruction(next_pc()));
//        Wrapper wrapper = instruction_decode(this.instruction_register);
//        wrapper = execute(wrapper);
//        memory_access(wrapper);
//        write_back(wrapper);

        // handling write back
        write_back(pipline[4].getWrapper());
        pipline[4].unlock();
        pipline[4].setWrapper(pipline[3].getWrapper());
        pipline[4].lock();

        // handling memory access



        // todo uncomment this section
//        try{
//            Thread.sleep(CCT); // simulated clock cycle time.
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }

    }

    private void instruction_fetch(String instruction) {
        instruction_register = instruction.split("\t");
    }

    private Wrapper instruction_decode(String[] instruction_register){
        System.out.println(Arrays.toString(instruction_register)); // todo debug remove

        String return_register = "";    // will store the register to write data to
        int value[] = new int[2];       // the value to be written to the return register
        int instruction = 0;            // stored decoded instruction value for the alu
        String loop = "";               // this stores loop to execute
        String cleaned;                 // used to clean the load/store indexing arguments
        try{
            String[] args = instruction_register[1].split(","); // gets the arguments in array format
            switch (instruction_register[0].trim()) {
                case "ADD":
                    return_register = args[0].trim();
                    value[0] = register.get(args[1].trim());
                    value[1] = register.get(args[2].trim());
                    // we don't need to assign an instruction value here as it is already zero
                    break;
                case "ADDI":
                    return_register = args[0].trim();
                    value[0] = register.get(args[1].trim());
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 1;
                    break;
                case "SUB":
                    return_register = args[0].trim();
                    value[0] = register.get(args[1].trim());
                    value[1] = register.get(args[2].trim());
                    instruction = 2;
                    break;
                case "SUBI":
                    return_register = args[0].trim();
                    value[0] = register.get(args[1].trim());
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 3;
                    break;
                case "MUL":
                    return_register = args[0].trim();
                    value[0] = register.get(args[1].trim());
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 4;
                    break;
                case "BEQ":
                    value[0] = register.get(args[0].trim());
                    value[1] = register.get(args[1].trim());
                    loop = args[1].trim();
                    instruction = 5;
                    break;
                case "BNE":
                    value[0] = register.get(args[0].trim());
                    value[1] = register.get(args[1].trim());
                    loop = args[1].trim();
                    instruction = 6;
                    break;
                case "BNEZ":
                    value[0] = register.get(args[0].trim());
                    loop = args[1].trim();
                    instruction = 7;
                    break;
                case "JAL":
                    instruction = 8;
                    break;
                case "LB":
                    return_register = args[0];
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim()); // offset
                    instruction = 9;
                    break;
                case "SB":
                    return_register = args[0];
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim()); // offset
                    instruction = 10;
                    break;
                case "LW":
                    return_register = args[0];
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim()); // offset
                    instruction = 11;
                    break;
                case "SW":
                    return_register = args[0];
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim()); // offset
                    instruction = 12;
                    break;
                default: // we are declaring a loop
                    // record where the loop starts
                    if (!branch.containsKey(instruction_register[0].trim())) branch.put(instruction_register[0].trim(), pc);
                    // rerun decode without loop declaration in the instruction
                    return instruction_decode( Arrays.copyOfRange(instruction_register, 1, instruction_register.length));
            }
        }catch (NullPointerException | IndexOutOfBoundsException e){ // this will end the program
            this.done = true; // there are no more instructions to fetch, finish what is in the pipeline then end.
            // todo finish pipeline
        }
        return new Wrapper(return_register, value, instruction, loop);
    }

    private Wrapper execute(Wrapper wrapper) {
        switch (wrapper.getInstruction()) {
            case 0: // ADD
                wrapper.setSolution(wrapper.getValue()[0] + wrapper.getValue()[1]);
                break;
            case 1: // ADDI
                wrapper.setSolution(wrapper.getValue()[0] + wrapper.getValue()[1]);
                break;
            case 2: // SUB
                wrapper.setSolution(wrapper.getValue()[0] - wrapper.getValue()[1]);
                break;
            case 3: // SUBI
                wrapper.setSolution(wrapper.getValue()[0] - wrapper.getValue()[1]);
                break;
            case 4: // MUL
                wrapper.setSolution(wrapper.getValue()[0] * wrapper.getValue()[1]);
                break;
            case 5: // BEQ
                // if the condition is true, go to the branch by changing the pc value
                if(wrapper.getValue()[0] != wrapper.getValue()[1]) pc = branch.get(wrapper.getLoop()) -1;
                break;
            case 6: // BNE
                // if the condition is true, go to the branch by changing the pc value
                if(wrapper.getValue()[0] == wrapper.getValue()[1]) pc = branch.get(wrapper.getLoop()) -1;
                break;
            case 7: // BNEZ
                // if the condition is true, go to the branch by changing the pc value
                if(wrapper.getValue()[0] != 0) pc = branch.get(wrapper.getLoop()) -1;
                break;
            case 8: // JAL
                break;
            // all loads/stores are handled in memory_access
        }
        return wrapper;
    }

    private void memory_access(Wrapper wrapper){
        switch (wrapper.getInstruction()){
            case 9:  // LB
                register.put(wrapper.getRegister(), // write value to register
                        Integer.parseInt(memory.getdata(wrapper.getValue()[0], wrapper.getValue()[1])));
                break;
            case 10: // SB
                memory.insert_data(String.valueOf(register.get(wrapper.getRegister())), // write value to memory
                        wrapper.getValue()[0], wrapper.getValue()[1]);
                break;
            case 11: // LW
                register.put(wrapper.getRegister(), // write value to register
                        Integer.parseInt(memory.getdata(wrapper.getValue()[0], wrapper.getValue()[1])));
                break;
            case 12: // SW
                memory.insert_data(String.valueOf(register.get(wrapper.getRegister())), // write value to memory
                        wrapper.getValue()[0], wrapper.getValue()[1]);
                break;
        }
    }

    private void write_back(Wrapper wrapper){
        // write the register value if it exists
        if (wrapper.getSolution() != null )register.put(wrapper.getRegister(), wrapper.getSolution());
    }

    boolean isDone(){
        return done;
    }
}