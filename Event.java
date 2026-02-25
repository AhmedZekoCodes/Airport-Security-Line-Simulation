
/** Class: Event
*
* Remarks: this is the base class for all different types of events that can happen.
* each event has a time when it happens and a passenger its about. events get
* sorted by time and passenger id so they happen in the right order.
* -----------------------------------------
*/
abstract class Event extends ListItem {

    protected int time;
    
    protected Passenger passenger;
    
    protected String type;

    /**
    * makes a new event with a time, passenger, and type of event
    *
    * @param time: when the event happens
    * @param passenger: the passenger in the event
    * @param type: what kind of event this is
    */
    public Event(int time, Passenger passenger, String type){
        this.time = time;
        this.passenger = passenger;
        this.type = type;
    }

    public int getTime(){
        return this.time;
    }
    
    public Passenger getPassenger(){
        return this.passenger;
    }
    
    public String getType(){
        return this.type;
    }

    public abstract void execute(Simulation sim);

    /**
    * compares this event with another one to figure out which is first
    *
    * @param other: the other list item to compare with
    * @return negative if this comes first, zero if equal, positive if after
    */
    @Override
    public int compareOrder(ListItem other){
        if(!(other instanceof Event otherEvent)){
            throw new IllegalArgumentException("ListItem not Event");
        }

        // Prioritize by time first
        if(this.time != otherEvent.time){
            return this.time - otherEvent.time;
        }

        // If both passengers are null (probably agents)
        if(this.passenger == null && otherEvent.passenger == null){
            return 0;
        }

        // Agent events come before passenger events
        if(this.passenger == null){
            return -1;
        }
        if(otherEvent.passenger == null){
            return 1;
        }

        // Otherwise, compare by passenger id
        return this.passenger.getId() - otherEvent.passenger.getId();
    }
}

/** Class: ArrivalEvent
*
* Remarks: event that happens when a passenger shows up at security.
* they pick the shortest line and get in it. if the line isnt busy
* they start getting searched right away.
* -----------------------------------------
*/
class ArrivalEvent extends Event {

    private boolean fromFile;
    private char typePerson;

    /**
    * makes a new arrival event for a passenger
    *
    * @param time: when the passenger arrives
    * @param passenger: the passenger arriving
    * @param fromFile: if this passenger came from the input file
    */
    public ArrivalEvent(int time, Passenger passenger, boolean fromFile, char typePerson){
        super(time, passenger, "Arrival");
        this.fromFile = fromFile;
        this.typePerson = typePerson;
    }

    /**
    * runs the arrival event and adds passenger to shortest line
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){

        // If agent arriving
        if(this.typePerson == 'A'){
            SecurityAgent agentArrived = null;
            for(SecurityAgent agent : sim.getAllAgents().toList()){
                if(agent.getArrivalTime() == this.time){
                    agentArrived = agent;
                    break;
                }
            }
            if(agentArrived == null){
                throw new IllegalStateException("No matching SecurityAgent found for arrival at time " + this.time);
            }


            // assign agent to first unstaffed, closed security line
            for(int index = 0; index < sim.lines.length; index++){

                // if line has no assigned agent, assign this agent and open it
                if(sim.lines[index].getAssignedAgent() == null){
                    sim.lines[index].openLine(agentArrived);
                    sim.logAgentArrival(agentArrived, sim.lines[index].getId());

                    // schedule shift completed event for when the agent ends shift
                    sim.events.insertOrdered(new ShiftCompletedEvent(agentArrived.getShiftEndTime(), agentArrived, sim.lines[index]));

                    break;
                }
            }

            // read next arrival from file only if original one was from file
            if(this.fromFile){
                sim.readNextArrival();
            }

        }
        else if(this.typePerson == 'P'){
            // passenger arriving, find shortest open security line
            SecurityLine bestLine = null;

            for(int index = 0; index < sim.lines.length; index++){
                if(sim.lines[index].isOpen()){
                    if(bestLine == null || sim.lines[index].length() < bestLine.length()){
                        bestLine = sim.lines[index];
                    }
                }
            }

            if(bestLine == null){
                throw new IllegalStateException("No open security lines for passenger arrival");
            }

            // remember when passenger joined the line
            this.passenger.setSecurityJoinTime(this.time);

            // add passenger to the shortest line
            bestLine.getQueue().enqueue(this.passenger);

            // log that the passenger arrived
            sim.logArrival(this.passenger, bestLine.getId(), this.fromFile);

            // if line isnt busy, start searching them right away
            if(!bestLine.isBusy()){
                bestLine.setBusy(true);
                sim.events.insertOrdered(new StartSearchEvent(this.time + Constants.LAY_BAGS, this.passenger, bestLine));
            }

            // if this passenger was from file, read the next one
            if(this.fromFile){
                sim.readNextArrival();
            }
        }
        else{
            throw new IllegalArgumentException("Invalid typePerson for ArrivalEvent");
        }
    }
}

/** Class: StartSearchEvent
*
* Remarks: this event starts when they begin searching a passenger at security.
* it figures out how long they waited and how long the search will take
* based on how many bags they have and if theyre suspicious.
* -----------------------------------------
*/
class StartSearchEvent extends Event {

    private SecurityLine line;

    /**
    * makes a new start search event for a passenger
    *
    * @param time: when the search starts
    * @param passenger: the passenger being searched
    * @param line: which security line theyre in
    */
    public StartSearchEvent(int time, Passenger passenger, SecurityLine line){
        super(time, passenger, "StartSearch");
        this.line = line;
    }

    /**
    * runs the start search event and calculates how long it takes
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){
        // figure out how long they waited in line
        int waitTime = this.time - this.passenger.getSecurityJoinTime() - Constants.LAY_BAGS;
        if(waitTime < 0){
            waitTime = 0;
        }
        
        // add the wait time to their total
        this.passenger.addWaitSecurity(waitTime);
        
        // calculate how long the search takes based on bags
        int searchDuration = this.passenger.getCarryons() * Constants.TIME_PER_CARRYON + Constants.BASE_SEARCH_TIME;
        
        // if theyre suspicious it takes way longer
        if(this.passenger.getSuspicion() > 50){
            searchDuration += Constants.BASE_SEARCH_TIME * (Constants.SUSPICIOUS_MULTIPLIER - 1);
        }
        
        // log that the search started
        sim.logStartSearch(this.passenger, this.line.getId(), waitTime, searchDuration);
        
        // schedule when the search will finish
        sim.events.insertOrdered(new FinishSearchEvent(this.time + searchDuration, this.passenger, this.line));
    }
}

/** Class: FinishSearchEvent
*
* Remarks: this event happens when a security search finishes.
* it checks if they have illegal stuff, if theyre suspicious, or if theyre good to go.
* then it starts searching the next person in line if there is one.
* -----------------------------------------
*/
class FinishSearchEvent extends Event {

    private SecurityLine line;

    private static java.util.Random carryonChecker = new java.util.Random(1001);

    /**
    * makes a new finish search event for a passenger
    *
    * @param time: when the search finishes
    * @param passenger: the passenger who finished being searched
    * @param line: which security line they were in
    */
    public FinishSearchEvent(int time, Passenger passenger, SecurityLine line){
        super(time, passenger, "FinishSearch");
        this.line = line;
    }

    /**
    * runs the finish search event and decides what happens next
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){
        // record when they first finished if this is their first time
        if(this.passenger.getFirstSearchFinishTime() < 0){
            this.passenger.setFirstSearchFinishTime(this.time);
        }

        Queue queue = this.line.getQueue();

        if(queue.isEmpty()){
            throw new IllegalStateException("dequeue empty in FinishSearchEvent for line " + this.line.getId());
        }

        // remove passenger from the queue
        Object dequeuedObject = queue.dequeue();
        if(!(dequeuedObject instanceof Passenger)){
            throw new IllegalStateException("Queue head not Passenger");
        }

        // check if there's more people waiting            
        if(!queue.isEmpty()){
            // start searching the next person
            Object peekedObject = queue.peek();
            if(peekedObject instanceof Passenger nextPassenger){
                sim.events.insertOrdered(new StartSearchEvent(this.time + Constants.LAY_BAGS, nextPassenger, this.line));
            }
            else{
                throw new IllegalStateException("Queue next not Passenger");
            }
        }
        else{
            this.line.setBusy(false);
        }

        // calculate scaled suspicion for search time adjustments (1-10 scale)
        int scaledSuspicion = Math.max(1, (int)Math.round(this.passenger.getSuspicion() / 10.0));

        // check once per search for inappropriate carryon if any carryons
        boolean foundInappropriate = false;
        if(this.passenger.getCarryons() > 0){
            int inappropriateCheck = carryonChecker.nextInt(100);
            if(inappropriateCheck < 50){
                foundInappropriate = true;
                this.passenger.increaseSuspicion(10);
                // update line and agent stats for inappropriate carryons found and rejected
                this.line.incrementInappropriateCarryonsFound();
                SecurityAgent agent = this.line.getAssignedAgent();
                if(agent != null){
                    agent.incrementCarryonsRejected();
                }
            }
        }

        // decide next step for passenger based on illegal stuff and suspicion threshold of agent
        if(this.passenger.hasIllegal()){
            // illegal stuff found, send them back to check bags
            this.passenger.decCarryon();
            this.passenger.setReturning(true);
            sim.logFinishSearch(this.passenger, this.line.getId(), "improper checked baggage found, passenger rejected");
            sim.events.insertOrdered(new ArrivalEvent(this.time + Constants.BAG_CHECK_DELAY, this.passenger, false, 'P'));
        }
        else{
            // check if passenger suspicion exceeds agent threshold, send to secondary if yes
            SecurityAgent agent = this.line.getAssignedAgent();
            if(agent != null && this.passenger.getSuspicion() > agent.getSuspicionThreshold()){
                sim.logFinishSearch(this.passenger, this.line.getId(), "passenger suspicion over threshold, secondary screening needed");
                sim.events.insertOrdered(new EnterSecondaryEvent(this.time + Constants.ESCORT_DELAY, this.passenger));
            }
            else{
                // passenger released
                this.passenger.setFinalExitTime(this.time);
                sim.logFinishSearch(this.passenger, this.line.getId(), "released");
                sim.handleExit(this.passenger);
                // track passengers searched for the agent
                if(agent != null){
                    agent.incrementPassengersSearched();
                }
                this.line.incrementPassengersSearched();
            }
        }
    }
}

/** Class: EnterSecondaryEvent
*
* Remarks: this event happens when a suspicious passenger goes to secondary screening.
* if theres a free room they go right in, otherwise they have to wait in line.
* -----------------------------------------
*/
class EnterSecondaryEvent extends Event {

    /**
    * makes a new enter secondary event for a passenger
    *
    * @param time: when they enter secondary screening
    * @param passenger: the passenger going to secondary
    */
    public EnterSecondaryEvent(int time, Passenger passenger){
        super(time, passenger, "EnterSecondary");
    }

    /**
    * runs the enter secondary event and puts them in a room or queue
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){
        // rmember how many rooms were free when they got here
        int freeRooms = sim.secondary.getFreeRooms();
        
        // check if theres a room available
        if(sim.secondary.hasFreeRoom()){
            // take a room and start interview right away
            sim.secondary.decRoom();
            this.passenger.setSecondaryEnterTime(this.time);
            sim.events.insertOrdered(new StartInterviewEvent(this.time, this.passenger));
            sim.logEnterSecondaryDirect(this.passenger, freeRooms);
        }
        else{
            // no rooms so they have to wait
            this.passenger.setSecondaryEnterTime(this.time);
            sim.secondary.getWaitingQueue().enqueue(this.passenger);
            sim.logEnterSecondaryDirect(this.passenger, freeRooms);
        }
    }
}

/** Class: StartInterviewEvent
*
* Remarks: this event starts when they begin interviewing a passenger in secondary
* it figures out how long they waited and schedules when the interview ends
* -----------------------------------------
*/
class StartInterviewEvent extends Event {

    /**
    * makes a new start interview event for a passenger
    *
    * @param time: when the interview starts
    * @param passenger: the passenger being interviewed
    */
    public StartInterviewEvent(int time, Passenger passenger){
        super(time, passenger, "StartInterview");
    }

    /**
    * runs the start interview event and schedules the finish
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){
        // figure out how long they waited for secondary
        int secondaryWait = this.time - this.passenger.getSecondaryEnterTime();
        if(secondaryWait < 0){
            secondaryWait = 0;
        }
        
        // add the wait time to their total
        this.passenger.addWaitSecondary(secondaryWait);
        
        // log that the interview started
        sim.logStartInterview(this.passenger, sim.secondary.getFreeRooms(), secondaryWait);
        
        // schedule when the interview will finish
        // calculate interview time by suspicion
        int scaledInterviewTime = Math.max(1, (int)Math.round(this.passenger.getSuspicion() / 10.0));
        sim.events.insertOrdered(new FinishInterviewEvent(this.time + scaledInterviewTime, this.passenger));

    }
}

/** Class: FinishInterviewEvent
*
* Remarks: this event happens when a secondary interview finishes
* if theyre a criminal theres a chance they get arrested, then it frees up
* the room and starts interviewing the next person if anyones waiting
* -----------------------------------------
*/
class FinishInterviewEvent extends Event {

    private static java.util.Random crimeCatcher = new java.util.Random(1001);

    /**
    * makes a new finish interview event for a passenger
    *
    * @param time: when the interview finishes
    * @param passenger: the passenger who finished the interview
    */
    public FinishInterviewEvent(int time, Passenger passenger){
        super(time, passenger, "FinishInterview");
    }

    /**
    * runs the finish interview event and decides if they get arrested
    *
    * @param sim: the simulation this event is running in
    */
    public void execute(Simulation sim){
        boolean isArrested = false;
        
        // if theyre a criminal, maybe they get caught
        if(this.passenger.isCriminal()){
            isArrested = (crimeCatcher.nextInt(100) + 1) < Constants.CAUGHT_PERCENT;
        }
        
        // record when they left
        this.passenger.setFinalExitTime(this.time);
        
        // either arrest them or let them go
        if(isArrested){
            sim.handleArrest(this.passenger);
        }
        else{
            sim.handleExit(this.passenger);
        }
        
        // free up the room
        sim.secondary.incRoom();
        
        // if someones waiting, start interviewing them
        if(!sim.secondary.getWaitingQueue().isEmpty()){
            Object dequeuedObject = sim.secondary.getWaitingQueue().dequeue();
            if(dequeuedObject instanceof Passenger nextPassenger){
                sim.secondary.decRoom();
                sim.events.insertOrdered(new StartInterviewEvent(this.time, nextPassenger));
            }
            else{
                throw new IllegalStateException("Secondary queue item not Passenger");
            }
        }
        
        // log that the interview finished
        sim.logFinishInterview(this.passenger, isArrested, sim.secondary.getFreeRooms(), this.passenger.getTotalWait());
    }
}

/**
 * Class: ShiftCompletedEvent
 *
 * Remarks: event that happens when a security agent finishes their shift,
 * it closes the security line they were working at and causes passengers
 * still in line (except ones being searched) to move to other lines
 * -----------------------------------------
 */
class ShiftCompletedEvent extends Event {

    private SecurityLine line;

    /**
     * makes a new shift completed event for an agent
     *
     * @param time: when the shift ends
     * @param agent: the agent finishing the shift
     * @param line: the line the agent worked at
     */
    public ShiftCompletedEvent(int time, SecurityAgent agent, SecurityLine line){
        super(time, null, "ShiftCompleted");
        this.passenger = null;
        this.line = line;
    }

    /**
     * runs the shift completed event, closing the line and moving passengers
     *
     * @param sim: the simulation this event is running in
     */
    public void execute(Simulation sim){
        // mark line as closed but allow current passenger to finish
        this.line.setBusy(true);

        Queue queue = this.line.getQueue();
        Queue passengersToMove = new Queue();

        // keep the first passenger (being searched), move the rest
        boolean firstKept = false;
        Queue newQueue = new Queue();

        while(!queue.isEmpty()){
            Object obj = queue.dequeue();
            if(obj instanceof Passenger movedPassenger){
                if(!firstKept){
                    newQueue.enqueue(movedPassenger); // keep currently searched passenger
                    firstKept = true;
                }
                else{
                    passengersToMove.enqueue(movedPassenger); // move others
                }
            }
        }

        // replace the old queue with the new one
        this.line.setBusy(!newQueue.isEmpty());
        this.line.getQueue().clear();
        while(!newQueue.isEmpty()){
            this.line.getQueue().enqueue(newQueue.dequeue());
        }

        // move remaining passengers
        int eventTime = this.time + 1;
        while(!passengersToMove.isEmpty()){
            Object nextObj = passengersToMove.dequeue();
            if(nextObj instanceof Passenger movedPassenger){
                sim.events.insertOrdered(new ChangeLineUpEvent(eventTime, movedPassenger));
            }
        }

        this.line.closeLine(); // close line AFTER scheduling moves
    }

}

/**
 * Class: ChangeLineUpEvent
 *
 * Remarks: this event happens when a passenger needs to move to a new open security line
 * it's used when a line closes and passengers have to find a new line
 * it chooses the shortest open line and the passenger joins it
 * -----------------------------------------
 */
class ChangeLineUpEvent extends Event {

    /**
     * makes a new change lineup event for a passenger
     *
     * @param time: when the passenger changes line
     * @param passenger: the passenger moving to a new line
     */
    public ChangeLineUpEvent(int time, Passenger passenger){
        super(time, passenger, "ChangeLineUp");
    }

    /**
     * runs the event, and moves the passenger to a new open line
     *
     * @param sim: the simulation this event is running in
     */
    public void execute(Simulation sim){
        SecurityLine bestLine = null;
        
        // find the shortest open security line for the passenger
        for(int index = 0; index < sim.lines.length; index++){
            if(sim.lines[index].isOpen()){
                if(bestLine == null || sim.lines[index].length() < bestLine.length()){
                    bestLine = sim.lines[index];
                }
            }
        }
        
        if(bestLine == null){
            throw new IllegalStateException("No open lines available for passenger lineup change");
        }
        
        // remember when passenger joined the line
        this.passenger.setSecurityJoinTime(this.time);
        
        // add passenger to the new line queue
        bestLine.getQueue().enqueue(this.passenger);
        
        sim.logLineupChange(this.passenger, bestLine.getId());
        
        // if the line was free, start searching this passenger
        if(!bestLine.isBusy()){
            bestLine.setBusy(true);
            sim.events.insertOrdered(new StartSearchEvent(this.time + Constants.LAY_BAGS, this.passenger, bestLine));
        }
    }
}
