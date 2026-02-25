//-----------------------------------------
// REMARKS: this is the main method for the airport security simulation,
// it asks the user for an input file, creates the simulation, and runs all
// the events until everything is done.
//-----------------------------------------

import java.io.*;

class Main {

    /**
    * the main method that starts the whole program
    *
    * @param args: command line arguments (not used)
    */
    public static void main(String[] args){
        String fileName = null;

        // try to read the input file name from the user
        try{
            // set up the console reader
            InputStreamReader inStream = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(inStream);
            
            // ask the user for the file name
            System.out.println("Please enter input file name:");
            fileName = in.readLine();
            
        }
        catch(IOException exception){
            // if we cant read from console something is really wrong
            System.out.println("Console input could not be opened! " + exception);
            System.exit(1);
        }

        // try to run the simulation
        try{
            // create the simulation with the file
            Simulation sim = new Simulation(fileName);
            
            // print the welcome message
            System.out.println("Welcome to the Franz Kafka Airport!\n");
            System.out.println("We have " + sim.lines.length + " security lines and " + sim.secondaryTotalRooms() + " interview rooms for your inconvenience.\n");
            
            // start reading passengers from the file
            sim.readHeaderAndPrime();
            
            // keep running events until theres none left
            while(!sim.events.isEmpty()){
                // get the next event from the list
                ListItem item = sim.events.removeFirst();
                
                // make sure its actually an event
                if(item instanceof Event event){
                    // update the simulation time and run the event
                    sim.setTime(event.getTime());
                    // track how long each open security line stays open and how long agents are active
                    for(SecurityLine line : sim.lines){
                        if(line.isOpen()){
                            // add one time unit to open line total
                            line.addToTotalOpenTime(1);

                            // if this line has an assigned agent, give them credit for working time
                            if(line.getAssignedAgent() != null){
                                line.getAssignedAgent().addActualWorkTime(1);
                            }
                        }
                    }

                    event.execute(sim);
                }
                else{
                    // if its not an event something went wrong
                    throw new IllegalStateException("Non-Event in event list");
                }
            }
            
            // all events are done so print the final summary
            sim.printFinalSummary();
            
        }
        catch(Exception exception){
            // if anything goes wrong print the error message
            System.out.println("Runtime error: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
