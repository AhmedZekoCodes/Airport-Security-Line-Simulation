import java.io.*;
import java.util.*;

/** Class: Simulation
*
* Remarks: this is the main class that runs the full airport simulation.
* it reads passenger and security data from a file, processes all the events in order,
* and keeps track of what happens to everyone.
* -----------------------------------------
*/
class Simulation {
    public final EventList events = new EventList();
    public final SecurityLine[] lines;
    public final SecondaryArea secondary;
    private final Scanner scan;
    private boolean hasMore;
    private int now;
    private int processedUnique;
    private long sumWait;
    private final OrderedList arrestedList = new OrderedList();
    private final OrderedList missedList = new OrderedList();
    private final PassengerList allPassengers = new PassengerList();
    private final SecurityAgentList allAgents = new SecurityAgentList();

    /**
    * makes a new simulation by reading the input file and setting everything up
    *
    * @param filename: the name of the file with passenger data
    */
    public Simulation(String filename) throws Exception {
        // read the header file
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String header = reader.readLine();
        
        // make sure the file isnt empty
        if(header == null){
            throw new IllegalArgumentException("Input file is empty or missing header line");
        }

        // split the header it into parts
        header = header.trim();
        String[] headerParts = header.split("\\s+");
        
        // check that we got the right number of values
        if(headerParts.length < 2){
            throw new IllegalArgumentException("Header line must contain number of lines and rooms");
        }

        // add how many security lines and rooms there are
        int numLines = Integer.parseInt(headerParts[0]);
        int rooms = Integer.parseInt(headerParts[1]);
        
        // initializing the lines
        this.lines = new SecurityLine[numLines];
        
        // make all the security lines
        for(int index = 0; index < numLines; index++){
            this.lines[index] = new SecurityLine(index);
        }

        // make the secondary screening area
        this.secondary = new SecondaryArea(rooms);

        // scanning the rest of the file
        this.scan = new Scanner(reader);
        this.hasMore = this.scan.hasNextLine();
        this.now = 0;
        this.processedUnique = 0;
        this.sumWait = 0L;
    }

    /**
    * starts reading passengers from the file
    */
    public void readHeaderAndPrime(){
        this.readNextArrival();
    }

    public void setTime(int time){
        this.now = time;
    }
    
    public int time(){
        return this.now;
    }
    
    public int secondaryTotalRooms(){
        return this.secondary.getTotalRooms();
    }

    public SecurityAgentList getAllAgents(){
        return this.allAgents;
    }


    /**
    * reads the next passenger from the input file and adds them to the event list
    */
    public void readNextArrival(){
        // if theres no more passengers just stop
        if(!this.hasMore){
            return;
        }
        
        // read the next line and trim it up
        String line = this.scan.nextLine().trim();
        
        // skip any blank lines
        while(line.isEmpty() && this.scan.hasNextLine()){
            line = this.scan.nextLine().trim();
        }
        
        // if we ran out of data, we're done
        if(line.isEmpty()){
            this.hasMore = false;
            return;
        }

        // split the line into parts
        String[] parts = line.split("\\s+");
        // check the first character to see if its an agent or passenger
        char type = parts[0].charAt(0);

        // if it's an agent line
        if(type == 'A'){
            if(parts.length < 4){
                throw new IllegalArgumentException("Wrong agent input line: " + line);
            }

            // parse agent info: A arrivalTime shiftEnd threshold
            int arrivalTime = Integer.parseInt(parts[1]);
            int shiftEnd = Integer.parseInt(parts[2]);
            int suspicionThreshold = Integer.parseInt(parts[3]);

            int agentNumber = 1;
            // find last agent's number in list to assign next number
            if(!this.allAgents.isEmpty()){
                for(SecurityAgent agent : this.allAgents.toList()){
                    if(agent.getAgentNumber() >= agentNumber){
                        agentNumber = agent.getAgentNumber() + 1;
                    }
                }
            }


            // create agent and add to list
            SecurityAgent agent = new SecurityAgent(agentNumber, arrivalTime, shiftEnd, suspicionThreshold);
            this.allAgents.add(agent);

            // schedule their arrival event
            this.events.insertOrdered(new ArrivalEvent(arrivalTime, null, true, 'A'));

            // check if more lines to read
            this.hasMore = this.scan.hasNextLine();

            return;
        }
        else if(type == 'P'){
            // Passenger line: P arrivalTime carryons flightTime [flags]

            if(parts.length < 4){
                throw new IllegalArgumentException("bad passenger input line: " + line);
            }

            int arrivalTime = Integer.parseInt(parts[1]);
            int carryons = Integer.parseInt(parts[2]);
            int flightTime = Integer.parseInt(parts[3]);

            boolean isFirstClass = false;
            boolean isSuspicious = false;
            boolean hasCriminalStuff = false;

            if(parts.length >= 5){
                String flags = parts[4].toUpperCase();
                for(int i = 0; i < flags.length(); i++){
                    char c = flags.charAt(i);
                    if(c == 'F'){
                        isFirstClass = true;
                    }
                    else if(c == 'S'){
                        isSuspicious = true;
                    }
                    else if(c == 'C'){
                        hasCriminalStuff = true;
                    }
                }
            }

            Passenger passenger;
            if(isFirstClass){
                passenger = new FirstClassPassenger(arrivalTime, flightTime, carryons, hasCriminalStuff);
            }
            else{
                passenger = new Passenger(arrivalTime, flightTime, carryons, false, hasCriminalStuff);
            }

            this.allPassengers.add(passenger);
            this.events.insertOrdered(new ArrivalEvent(arrivalTime, passenger, true, 'P'));
            this.hasMore = this.scan.hasNextLine();
            return;
        }
        else{
            if(parts.length < 3){
                throw new IllegalArgumentException("bad input line: " + line);
            }
            int arrivalTime = Integer.parseInt(parts[0]);
            int carryons = Integer.parseInt(parts[1]);
            int flightTime = Integer.parseInt(parts[2]);
            boolean isFirstClass = false;
            boolean isSuspicious = false;
            boolean hasCriminalStuff = false;

            if(parts.length >= 4){
                String flags = parts[3].toUpperCase();
                for(int i=0; i<flags.length(); i++){
                    char c = flags.charAt(i);
                    if(c == 'F'){
                        isFirstClass = true;
                    }
                    else if(c == 'S'){
                        isSuspicious = true;
                    }
                    else if(c == 'C'){
                        hasCriminalStuff = true;
                    }
                }
            }

            Passenger passenger;
            if(isFirstClass){
                passenger = new FirstClassPassenger(arrivalTime, flightTime, carryons, hasCriminalStuff);
            }
            else{
                passenger = new Passenger(arrivalTime, flightTime, carryons, false, hasCriminalStuff);
            }

            this.allPassengers.add(passenger);
            this.events.insertOrdered(new ArrivalEvent(arrivalTime, passenger, true, 'P'));
            this.hasMore = this.scan.hasNextLine();
        }
    }

    /**
    * handles a passenger leaving the airport
    *
    * @param passenger: the passenger who is exiting
    */
    public void handleExit(Passenger passenger){
        // count them as processed
        this.processedUnique++;
        
        // add their wait time to the total
        this.sumWait += passenger.getTotalWait();
        
        // check if they missed their flight
        boolean missedFlight = passenger.getFinalExitTime() > passenger.getFlightDeparture();
        
        // if they missed it, add them to the missed list
        if(missedFlight){
            this.missedList.insertOrdered(new SummaryItem(passenger));
        }
    }

    /**
    * handles a passenger getting arrested at security
    *
    * @param passenger: the passenger who got arrested
    */
    public void handleArrest(Passenger passenger){
        // count them as processed
        this.processedUnique++;
        
        // add their wait time to the total
        this.sumWait += passenger.getTotalWait();
        
        // add them to the arrested list
        this.arrestedList.insertOrdered(new SummaryItem(passenger));
    }

    /**
    * prints a message when a passenger arrives at security
    *
    * @param passenger: the passenger arriving
    * @param lineId: which security line they joined
    * @param fromFile: if they came from the input file or are returning
    */
    public void logArrival(Passenger passenger, int lineId, boolean fromFile){
        // figure out if theyre a criminal
        String criminalNote = passenger.isCriminal() ? "a criminal - " : "";
        
        // figure out if theyre coming back
        String returningNote = passenger.isReturning() ? " *again*" : "";
        
        // print the arrival message
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " arrives" + returningNote + " (" + criminalNote + passenger.getCarryons() + " carryons, enters security line " + lineId + ")");
    }

    /**
    * prints a message when a security agent arrives
    *
    * @param agent: the agent arriving
    * @param lineId: which security line they work at
    */
    public void logAgentArrival(SecurityAgent agent, int lineId){
        System.out.println("Time " + this.time() + ": Security Agent " + agent.getAgentNumber() + " begins shift at line " + lineId +
            " (threshold " + agent.getSuspicionThreshold() + ", shift ends at " + agent.getShiftEndTime() + ")");
    }


    /**
    * prints a message when a passengers search starts
    *
    * @param passenger: the passenger being searched
    * @param lineId: which security line theyre in
    * @param waitedThisLine: how long they waited
    * @param duration: how long the search will take
    */
    public void logStartSearch(Passenger passenger, int lineId, int waitedThisLine, int duration){
        // figure out if theyre suspicious
        String suspicionNote = passenger.getSuspicion() > 50 ? " plus suspicion" : "";
        
        // print the search start message
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " starts search (line " + lineId + ", waited " + waitedThisLine + " - with " + passenger.getCarryons() + " carryons" + suspicionNote + ", requires " + duration + " units)");
    }

    /**
    * prints a message when a passengers search finishes
    *
    * @param passenger: the passenger who finished
    * @param lineId: which security line they were in
    * @param result: what happened to them
    */
    public void logFinishSearch(Passenger passenger, int lineId, String result){
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " finishes search (line " + lineId + " - result: " + result + ")");
    }

    /**
    * prints a message when a passenger enters secondary screening
    *
    * @param passenger: the passenger entering secondary
    * @param roomsFreeOnArrival: how many rooms were free when they got there
    */
    public void logEnterSecondaryDirect(Passenger passenger, int roomsFreeOnArrival){
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " enters secondary security area waiting room (rooms available on arrival: " + roomsFreeOnArrival + ")");
    }

    /**
    * prints a message when a passengers interview starts
    *
    * @param passenger: the passenger being interviewed
    * @param roomsRemain: how many rooms are still free
    * @param waitedSecondary: how long they waited for secondary
    */
    public void logStartInterview(Passenger passenger, int roomsRemain, int waitedSecondary){
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " starts interview (waited " + waitedSecondary + ", " + roomsRemain + " room(s) remain)");
    }

    /**
    * prints a message when a passengers interview finishes
    *
    * @param passenger: the passenger who finished the interview
    * @param arrested: if they got arrested or not
    * @param roomsRemain: how many rooms are still free
    * @param totalWait: how long they waited total
    */
    public void logFinishInterview(Passenger passenger, boolean arrested, int roomsRemain, int totalWait){
        // figure out if they were arrested or released
        String result = arrested ? "arrested" : "released";
        
        // print the interview finish message
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() + " finishes interview (result: " + result + ", spent " + totalWait + " units waiting, " + roomsRemain + " room(s) remain)");
    }

    /**
    * prints a message when a passenger moves to a new open security line
    *
    * @param passenger: the passenger who just switched lines
    * @param newLineId: which new security line they joined
    */
    public void logLineupChange(Passenger passenger, int newLineId){
        System.out.println("Time " + this.time() + ": Passenger " + passenger.getId() +
            " moves to new line " + newLineId + " after closure.");
    }


    /**
    * prints out the list of passengers
    *
    * @param list: the ordered list of summary items to print
    */
    private void printSummaryList(OrderedList list){
        // make a temporary list so we can put stuff back
        OrderedList tempList = new OrderedList();
        
        // loop through all the passengers in the list
        while(!list.isEmpty()){
            // get the next item from the list
            ListItem listItem = list.removeFirst();
            
            // make sure its actually a summary item
            if(!(listItem instanceof SummaryItem summaryItem)){
                throw new IllegalStateException("summary item type");
            }
            
            // get the passenger from the summary item
            Passenger passenger = summaryItem.passenger;
            
            // get their status code
            String status = passenger.statusCode();
            
            // figure out when they finished their first search
            int searchedTime = passenger.getFirstSearchFinishTime() < 0 ? 0 : passenger.getFirstSearchFinishTime();
            
            // figure out when they finally left
            int exitTime = passenger.getFinalExitTime() < 0 ? 0 : passenger.getFinalExitTime();
            
            // print all their info
            System.out.printf("%d %d %s %d %s %d %d %d %d%n",
                passenger.getId(),
                passenger.getFlightDeparture(),
                passenger.classLabel(),
                passenger.getCarryons(),
                status,
                passenger.getArrivalTimeFirst(),
                searchedTime,
                exitTime,
                passenger.getTotalWait());
            
            // put the item in the temp list so we can restore it
            tempList.insertOrdered(summaryItem);
        }
        
        // put everything back in the original list
        while(!tempList.isEmpty()){
            list.insertOrdered(tempList.removeFirst());
        }
    }

    /**
    * prints the final summary with arrested and missed flight passengers
    */
    public void printFinalSummary(){
        System.out.println("\n...All events complete. Final Summary:");
        System.out.println("\nPassengers Arrested:\n");
        System.out.println("ID      Flight  Class  Carryons  Status  Arrival  Searched  Exit   Waiting");
        System.out.println("Number  Time                      Time     Time      Time   Time");
        System.out.println("-----------------------------------------------------------------------------------");
        
        // print the arrested passengers
        this.printSummaryList(this.arrestedList);
        
        System.out.println("\nPassengers Missing Their Flights:\n");
        System.out.println("ID      Flight  Class  Carryons  Status  Arrival  Searched  Exit   Waiting");
        System.out.println("Number  Time                      Time     Time      Time   Time");
        System.out.println("-----------------------------------------------------------------------------------");
        
        // print the passengers who missed their flights
        this.printSummaryList(this.missedList);
        
        System.out.println("\nTotal passengers processed: " + this.processedUnique);
        
        // calculate the average wait time
        double averageWait;
        if(this.processedUnique == 0){
            averageWait = 0.0;
        }
        else{
            averageWait = ((double) this.sumWait) / this.processedUnique;
        }
        
        System.out.printf("Average time spent waiting per passenger: %.2f%n", averageWait);

        // print all agent data at end of simulation
        System.out.println("\nSecurity Agent Summary:");
        System.out.println("Agent  Arrived  Worked  Passengers  CarryonsRejected  Threshold");
        System.out.println("---------------------------------------------------------------");
        for(SecurityAgent agent : getAllAgents().toList()){
            System.out.printf("%5d %8d %8d %11d %17d %11d%n",
                agent.getAgentNumber(),
                agent.getArrivalTime(),
                agent.getActualWorkTime(),
                agent.getPassengersSearched(),
                agent.getCarryonsRejected(),
                agent.getSuspicionThreshold());
        }

        System.out.println("\nSecurity Line Summary:");
        System.out.println("Line  OpenTime  AgentsWorked  PassengersSearched  InappropriateCarryons");
        System.out.println("-----------------------------------------------------------------------");
        for(SecurityLine line : this.lines){
            System.out.printf("%4d %10d %13d %19d %24d%n",
                line.getId(),
                line.getTotalOpenTime(),
                line.getAgentsWorkedCount(),
                line.getPassengersSearchedCount(),
                line.getInappropriateCarryonsFoundCount());
        }

    }
}
