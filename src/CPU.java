import java.util.Arrays;
import java.util.HashMap;

/**
 * CPU class
 *
 * @author Patrick Shinn
 * @version 4/21/18
 */
class CPU {
    private long CCT = 50; // clock cycle time in milliseconds
    private PipelineStage[] pipline = {new PipelineStage(0),
            new PipelineStage(1),
            new PipelineStage(2),
            new PipelineStage(3),
            new PipelineStage(4)};
    private boolean done = false;
    private int pc = 0, temp_pc = 0;
    private String[] instruction_register;
    private HashMap<String, Integer[]> register = new HashMap<>();
    private HashMap<String, Integer> branch = new HashMap<>();
    private Memory memory = new Memory();

    CPU(Memory memory) {
        this.memory = memory;
        Integer[] ints = {0,0};
        // creating the 32 registers
        // i create both name reference and register reference so either can be used.

        // these registers are called by register
        register.put("x0", ints);
        register.put("x1", ints);
        register.put("x2", ints);
        register.put("x3", ints);
        register.put("x4", ints);
        register.put("x5", ints);
        register.put("x6", ints);
        register.put("x7", ints);
        register.put("x8", ints);
        register.put("x9", ints);
        register.put("x10", ints);
        register.put("x11", ints);
        register.put("x12", ints);
        register.put("x13", ints);
        register.put("x14", ints);
        register.put("x15", ints);
        register.put("x16", ints);
        register.put("x17", ints);
        register.put("x18", ints);
        register.put("x19", ints);
        register.put("x20", ints);
        register.put("x21", ints);
        register.put("x22", ints);
        register.put("x23", ints);
        register.put("x24", ints);
        register.put("x25", ints);
        register.put("x26", ints);
        register.put("x27", ints);
        register.put("x28", ints);
        register.put("x29", ints);
        register.put("x30", ints);
        register.put("x31", ints);

        // the rest are all called by name
        register.put("zero", ints);
        register.put("ra", ints);
        register.put("sp", ints);
        register.put("gp", ints);
        register.put("tp", ints);
        register.put("t0", ints);
        register.put("t1", ints);
        register.put("t2", ints);
        register.put("s0", ints);
        register.put("s1", ints);
        register.put("a0", ints);
        register.put("a1", ints);
        register.put("a2", ints);
        register.put("a3", ints);
        register.put("a4", ints);
        register.put("a5", ints);
        register.put("a6", ints);
        register.put("a7", ints);
        register.put("s2", ints);
        register.put("s3", ints);
        register.put("s4", ints);
        register.put("s5", ints);
        register.put("s6", ints);
        register.put("s7", ints);
        register.put("s8", ints);
        register.put("s9", ints);
        register.put("s10", ints);
        register.put("s11", ints);
        register.put("t3", ints);
        register.put("t4", ints);
        register.put("t5", ints);
        register.put("t6", ints);
    }

    private int next_pc() {
        pc %= 256;   // ensure we don't go out of memory
        return pc++; // return pc val then increment to the next one
    }

    void run(){
        // handling write back
        try{
            write_back(pipline[4].getWrapper());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[4].unlock(); // unlock for writing
        pipline[4].setWrapper(pipline[3].getWrapper());
        pipline[4].lock();   // lock until value is used

        // handling memory access
        try{
            memory_access(pipline[3].getWrapper());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[3].unlock(); // unlock for writing
        pipline[3].setWrapper(pipline[2].getWrapper());
        pipline[3].lock();   // lock until value is used

       // handling execution
        try{
           execute(pipline[2].getWrapper());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[2].unlock(); // unlock for writing
        pipline[2].setWrapper(pipline[1].getWrapper());
        pipline[2].lock();   // lock until value is used

        // handling instruction decode
        try{
            pipline[1].setWrapper(instruction_decode(instruction_register));
            // todo handle locking here and stalling
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }

        instruction_fetch(memory.getInstruction(next_pc()));

        // todo uncomment this section
        try{
            Thread.sleep(CCT); // simulated clock cycle time.
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    private void instruction_fetch(String instruction) {
        instruction_register = instruction.split("\t");
    }

    private Wrapper instruction_decode(String[] instruction_register){
        System.out.println(Arrays.toString(instruction_register)); // todo debug remove

        String return_register = "";              // will store the register to write data to
        int value[] = new int[2];                 // the value to be written to the return register
        int instruction = 0;                      // stored decoded instruction value for the alu
        String loop = "";                         // this stores loop to execute
        String cleaned;                           // used to clean the load/store indexing arguments
        Integer[] register_lock = new Integer[2]; // used to lock registers to prevent data hazards
        try{
            String[] args = instruction_register[1].split(","); // gets the arguments in array format
            switch (instruction_register[0].trim()) {
                case "ADD":
                    if (register_lock(args[1], args[2])) return null;       // checking for data hazard
                    return_register = args[0].trim();
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    value[0] = register.get(args[1].trim())[0];
                    value[1] = register.get(args[2].trim())[0];
                    // we don't need to assign an instruction value here as it is already zero
                    break;
                case "ADDI":
                    if (register_lock(args[1])) return null;       // checking for data hazard
                    return_register = args[0].trim();
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    value[0] = register.get(args[1].trim())[0];
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 1;
                    break;
                case "SUB":
                    if (register_lock(args[1], args[2])) return null;       // checking for data hazard
                    return_register = args[0].trim();
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    value[0] = register.get(args[1].trim())[0];
                    value[1] = register.get(args[2].trim())[0];
                    instruction = 2;
                    break;
                case "SUBI":
                    if (register_lock(args[1])) return null;       // checking for data hazard
                    return_register = args[0].trim();
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    value[0] = register.get(args[1].trim())[0];
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 3;
                    break;
                case "MUL":
                    if (register_lock(args[1], args[2])) return null;       // checking for data hazard
                    return_register = args[0].trim();
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    value[0] = register.get(args[1].trim())[0];
                    value[1] = Integer.parseInt(args[2].trim());
                    instruction = 4;
                    break;
                case "BEQ":
                    if (register_lock(args[0], args[1])) return null; // data hazard
                    value[0] = register.get(args[0].trim())[0];
                    value[1] = register.get(args[1].trim())[0];
                    loop = args[1].trim();
                    instruction = 5;
                    temp_pc = pc;                // make a copy of the current pc
                    pc = branch.get(args[1]) -1; // preemptive take branch
                    break;
                case "BNE":
                    if (register_lock(args[0], args[1])) return null; // data hazard
                    value[0] = register.get(args[0].trim())[0];
                    value[1] = register.get(args[1].trim())[0];
                    loop = args[1].trim();
                    instruction = 6;
                    temp_pc = pc;                // make a copy of the current pc
                    pc = branch.get(args[1]) -1; // preemptive take branch
                    break;
                case "BNEZ":
                    if (register_lock(args[0])) return null; // data hazard
                    value[0] = register.get(args[0].trim())[0];
                    loop = args[1].trim();
                    instruction = 7;
                    temp_pc = pc;                // make a copy of the current pc
                    pc = branch.get(args[1]) -1; // preemptive take branch
                    break;
                case "JAL":
                    instruction = 8;
                    break;
                case "LB":
                    return_register = args[0];
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim())[0]; // offset
                    instruction = 9;
                    break;
                case "SB":
                    if (register_lock(args[0])) return null;        // data hazard
                    return_register = args[0];
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    if (register_lock(args[1], args[2])) return null;       // checking for locked registers
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim())[0]; // offset
                    instruction = 10;
                    break;
                case "LW":
                    return_register = args[0];
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim())[0]; // offset
                    instruction = 11;
                    break;
                case "SW":
                    if (register_lock(args[0])) return null;        // data hazard
                    return_register = args[0];
                    register_lock[0] = register.get(return_register)[0];    // saved register value
                    register_lock[1] = 1;                                   // locked register data
                    register.put(return_register, register_lock);           // applying lock
                    cleaned = args[1].replace('(', ',').replace(")", "");
                    value[0] = Integer.parseInt(cleaned.split(",")[0].trim()); // index
                    value[1] = register.get(cleaned.split(",")[1].trim())[0]; // offset
                    instruction = 12;
                    break;
                default: // we are declaring a loop
                    // record where the loop starts
                    if (!branch.containsKey(instruction_register[0].trim())) branch.put(instruction_register[0].trim(), pc);
                    // rerun decode without loop declaration in the instruction
                    return instruction_decode( Arrays.copyOfRange(instruction_register, 1, instruction_register.length));
            }
        }catch (IndexOutOfBoundsException e){ // this will end the program
            this.done = true; // there are no more instructions to fetch, finish what is in the pipeline then end.
            // todo finish pipeline
        }catch (NullPointerException e){
            // this is expected during a stall
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
            // branching is handled in instruction decode
            case 5: // BEQ
                // if the condition is true, go to the branch by changing the pc value
                if(wrapper.getValue()[0] != wrapper.getValue()[1]) pc = branch.get(wrapper.getLoop()) -1;
                break;
            case 6: // BNE
                // if the condition is true, go to the branch by changing the pc value
                if(wrapper.getValue()[0] == wrapper.getValue()[1]) pc = branch.get(wrapper.getLoop()) -1;
                break;
            case 7: // BNEZ
                // if the condition is false, reset the pc value and flush IF and ID
                if(wrapper.getValue()[0] == 0){
                    pc = temp_pc;                // reset pc val to before preemptive branch
                    pipline[1].setWrapper(null); // flush ID
                    instruction_register = null; // flush IF
                }
                break;
            case 8: // JAL
                break;
            // all loads/stores are handled in memory_access
        }
        return wrapper;
    }

    private void memory_access(Wrapper wrapper){
        Integer[] solution = new Integer[2];
        switch (wrapper.getInstruction()){
            case 9:  // LB
                solution[0] = Integer.parseInt(memory.getdata(wrapper.getValue()[0], wrapper.getValue()[1]));
                solution[1] = 0;
                register.put(wrapper.getRegister(),solution); // write value to register
                break;
            case 10: // SB
                memory.insert_data(String.valueOf(register.get(wrapper.getRegister())[0]), // write value to memory
                        wrapper.getValue()[0], wrapper.getValue()[1]);
                break;
            case 11: // LW
                solution[0] = Integer.parseInt(memory.getdata(wrapper.getValue()[0], wrapper.getValue()[1]));
                solution[1] = 0;
                register.put(wrapper.getRegister(),solution); // write value to register
                break;
            case 12: // SW
                memory.insert_data(String.valueOf(register.get(wrapper.getRegister())[0]), // write value to memory
                        wrapper.getValue()[0], wrapper.getValue()[1]);
                break;
        }
    }

    private void write_back(Wrapper wrapper){
        // write the register value if it exists
        Integer[] solution = {wrapper.getSolution(), 0}; // write the solution and unlock the register for use
        if (wrapper.getSolution() != null )register.put(wrapper.getRegister(), solution);
    }

    private boolean register_lock(String register){
        if (this.register.get(register)[1] == 1){ // data hazard
            pc --;
            pipline[1].setWrapper(null); // flush ID
            instruction_register = null; // flush IF
            return true;
        }
        return false;
    }

    private boolean register_lock(String register1, String register2){
        if (register.get(register1)[1] == 1 || register.get(register2)[1] == 1 ){ // data hazard
            pc --;                       // decrement the pc
            pipline[1].setWrapper(null); // flush ID
            instruction_register = null; // flush IF
            return true;
        }
        return false;
    }

    boolean isDone(){
        return done;
    }
}