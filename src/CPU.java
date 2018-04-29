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
    private int pc = 0, temp_pc = 0;
    private String[] instruction_register;
    private HashMap<String, Integer[]> register = new HashMap<>();
    private HashMap<String, Integer> branch = new HashMap<>();
    private Memory memory = new Memory();

    /**
     * The CPU constructor essentially creates and populates the registers.
     * This includes making the ability to registers by either name or by
     * register. However one scheme or the other should be used as the calling
     * a1 and then calling x10 would result in different values due to each register
     * being its own discrete structure in this program.
     *
     * @param memory Memory
     */
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

    /**
     * Increments the pc value.
     * @return incremented pc value.
     */
    private int next_pc() {
        pc %= 256;   // ensure we don't go out of memory
        return pc++; // return pc val then increment to the next one
    }

    /***
     * Simulates one clock cycle with the pipeline.
     */
    void run(){
        // handling write back
        try{
            write_back(pipline[4].getDataBus());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[4].unlock(); // unlock for writing
        pipline[4].setDataBus(pipline[3].getDataBus());
        pipline[4].lock();   // lock until value is used

        // handling memory access
        try{
            memory_access(pipline[3].getDataBus());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[3].unlock(); // unlock for writing
        pipline[3].setDataBus(pipline[2].getDataBus());
        pipline[3].lock();   // lock until value is used

       // handling execution
        try{
           execute(pipline[2].getDataBus());
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }
        pipline[2].unlock(); // unlock for writing
        pipline[2].setDataBus(pipline[1].getDataBus());
        pipline[2].lock();   // lock until value is used

        // handling instruction decode
        try{
            pipline[1].setDataBus(instruction_decode(instruction_register));
            // todo handle locking here and stalling
        }catch (NullPointerException e){
            // this is expected when the pipeline is being populated
        }

        // handling instruction fetch
        instruction_fetch(memory.getInstruction(next_pc()));

        try{
            Thread.sleep(CCT); // simulated clock cycle time.
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    /***
     * Instruction fetch gets an instruction string out of the memory,
     * splits it into a string array, then writes it to the instruction register.
     *
     * @param instruction String: instruction to be decoded
     */
    private void instruction_fetch(String instruction) {
        instruction_register = instruction.split("\t");
    }

    /***
     * Instruction decode is responsible for parsing the assembly code,
     * fetching all needed data for code execution, and checking for data hazards
     * that could occur from accessing the data. This method also puts a lock on registers
     * that are being used to return data and will take a branch preemptively. The execute
     * stage will check for correctness and correct the instruction fetch if necessary.
     *
     * @param instruction_register String Array: instruction register value
     * @return DataBus: data wrapper to make things easier. This could by seen as a bus.
     */
    private DataBus instruction_decode(String[] instruction_register){
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
        }catch (NullPointerException e){
            // this is expected during a stall
        }
        return new DataBus(return_register, value, instruction, loop);
    }

    /**
     * This simulates the ALU. Execute is responsible for all computations.
     * @param dataBus DataBus: dataBus generated by instruction decode.
     * @return DataBus: updated version of the input dataBus.
     */
    private DataBus execute(DataBus dataBus) {
        switch (dataBus.getInstruction()) {
            case 0: // ADD
                dataBus.setSolution(dataBus.getValue()[0] + dataBus.getValue()[1]);
                break;
            case 1: // ADDI
                dataBus.setSolution(dataBus.getValue()[0] + dataBus.getValue()[1]);
                break;
            case 2: // SUB
                dataBus.setSolution(dataBus.getValue()[0] - dataBus.getValue()[1]);
                break;
            case 3: // SUBI
                dataBus.setSolution(dataBus.getValue()[0] - dataBus.getValue()[1]);
                break;
            case 4: // MUL
                dataBus.setSolution(dataBus.getValue()[0] * dataBus.getValue()[1]);
                break;

            /*
                Branching is handled in instruction decode, here we are ensuring we took
                the right branch and correcting the error if not. Please note that if we
                have to correct the branch, it will result in a stall cycle.
             */
            case 5: // BEQ
                // if the condition is false, reset the pc value and flush IF and ID
                if(dataBus.getValue()[0] == dataBus.getValue()[1]) {
                    pc = temp_pc;                // reset pc val to before preemptive branch
                    pipline[1].setDataBus(null); // flush ID
                    instruction_register = null; // flush IF
                }
                break;
            case 6: // BNE
                // if the condition is false, reset the pc value and flush IF and ID
                if(dataBus.getValue()[0] != dataBus.getValue()[1]) {
                    pc = temp_pc;                // reset pc val to before preemptive branch
                    pipline[1].setDataBus(null); // flush ID
                    instruction_register = null; // flush IF
                }
                break;
            case 7: // BNEZ
                // if the condition is false, reset the pc value and flush IF and ID
                if(dataBus.getValue()[0] == 0){
                    pc = temp_pc;                // reset pc val to before preemptive branch
                    pipline[1].setDataBus(null); // flush ID
                    instruction_register = null; // flush IF
                }
                break;
            case 8: // JAL
                break;

            // all loads/stores are handled in memory_access
        }
        return dataBus;
    }

    /***
     * Memory access handles all stores and loads.
     * Once finished, any locked return registers will
     * be unlocked allowing any stalled stages that depend
     * on the register to advance.
     *
     * @param dataBus DataBus: output of execute
     */
    private void memory_access(DataBus dataBus){
        Integer[] solution = new Integer[2];
        switch (dataBus.getInstruction()){
            case 9:  // LB
                solution[0] = Integer.parseInt(memory.getdata(dataBus.getValue()[0], dataBus.getValue()[1]));
                solution[1] = 0;
                register.put(dataBus.getRegister(),solution); // write value to register
                break;
            case 10: // SB
                memory.insert_data(String.valueOf(register.get(dataBus.getRegister())[0]), // write value to memory
                        dataBus.getValue()[0], dataBus.getValue()[1]);
                break;
            case 11: // LW
                solution[0] = Integer.parseInt(memory.getdata(dataBus.getValue()[0], dataBus.getValue()[1]));
                solution[1] = 0;
                register.put(dataBus.getRegister(),solution); // write value to register
                break;
            case 12: // SW
                memory.insert_data(String.valueOf(register.get(dataBus.getRegister())[0]), // write value to memory
                        dataBus.getValue()[0], dataBus.getValue()[1]);
                break;
        }
    }

    /***
     * Write back handles all register value updates. This also unlocks the
     * locked return register allowing a dependent stage to advance.
     *
     * @param dataBus DataBus: output of execute.
     */
    private void write_back(DataBus dataBus){
        // write the register value if it exists
        Integer[] solution = {dataBus.getSolution(), 0}; // write the solution and unlock the register for use
        if (dataBus.getSolution() != null )register.put(dataBus.getRegister(), solution);
    }

    /**
     * Check for a lock on a single register
     *
     * @param register String: register name/register
     * @return boolean: true if locked, false if not
     */
    private boolean register_lock(String register){
        if (this.register.get(register)[1] == 1){ // data hazard
            pc --;
            pipline[1].setDataBus(null); // flush ID
            instruction_register = null; // flush IF
            return true;
        }
        return false;
    }

    /**
     * Check for a lock on two registers.
     *
     * @param register1 String: register name/register
     * @param register2 String: register name/register
     * @return boolean: true if locked, false if not
     */
    private boolean register_lock(String register1, String register2){
        if (register.get(register1)[1] == 1 || register.get(register2)[1] == 1 ){ // data hazard
            pc --;                       // decrement the pc
            pipline[1].setDataBus(null); // flush ID
            instruction_register = null; // flush IF
            return true;
        }
        return false;
    }

    /**
     * Allows the Main class to determine if the cpu is done
     * executing the assembly script.
     *
     * @return boolean done
     */
    boolean isDone(){
        return done;
    }
}