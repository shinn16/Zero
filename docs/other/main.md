\mainpage About The Project

You can find all code documentation by going to the classes link above and looking at the classes.
All major methods are documented. Below are some explanations on assumptions and on why things were
done the way they were done.

## Assumptions
The pipeline follows the in-class examples we did. To increase the speed
of the pipeline, I assumed that all register values are accessible through
a register file as they are being written allowing a dependent instruction to
execute one clock cycle sooner. 

## Logic
#### The ALU
There is not a class for the ALU in my project. I started out with one,
but then decided it was not necessary. The ALU is responsible for the execution
phase in the pipeline, which I wrote a method for. Writing an ALU class would essentially
be a wrapper for this method. Since it served no other purpose, I just left the method as is.

#### The Pipeline
Each PipelineStage is an object wrapper. It contains a string that is the instruction to execute (
though this is only for the log print out), a DataBus which is an object wrapper for passing data 
between pipeline stages, and a boolean lock. The pipeline implementation is done using 5 methods in the CPU class.
The DataBus object for each instruction is passed between each stage until its execution is finished. The DataBus
object allows for easy handling of instruction arguments instruction decoding.


The pipeline is executing in the following order: write back, memory access, execution, instruction decode, and instruction
fetch. In reality all of these stages execute simultaneously, but since I did not thread them I elected this order because
it worked best for passing the DataBus objects around.

#### Memory
My Memory array is partitioned into two sections:

    1. Program Instructions
    2. Program Data
    
This is done to prevent the program from over writing its own instructions. The Memory is one string array of 256 length,
but it is made into its own class so that I can easily handle the partitioning described above and easily manage inserting 
data and instructions.


## Log File
The log file produces a table with the pipeline stages and what instruction they are executing.
Each row in the table is a clock cycle. Whenever a stall occurs, it will show as a stall in the
column of the pipeline stage that it occurred at. Null values will appear when a stage is completely empty.