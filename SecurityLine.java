/** Class: SecurityLine
*
* Remarks: one security screening line at the airport
* it has a queue of passengers waiting and tracks if the line is currently
* busy checking someone or free to start the next person
* -----------------------------------------
*/
class SecurityLine {

    private int id;
    private Queue line;
    private boolean busy;
    private boolean open;
    private SecurityAgent assignedAgent;
    private int totalOpenTime;
    private int agentsWorkedCount;
    private int passengersSearchedCount;
    private int inappropriateCarryonsFoundCount;

    /**
    * makes a new security line with an id number
    *
    * @param id: the line number to identify this specific line
    */
    public SecurityLine(int id){
        // save the line id
        this.id = id;
        
        // make a new empty queue for passengers
        this.line = new Queue();
        
        // start with the line not busy
        this.busy = false;
        this.open = false;
        this.assignedAgent = null;
        this.totalOpenTime = 0;
        this.agentsWorkedCount = 0;
        this.passengersSearchedCount = 0;
        this.inappropriateCarryonsFoundCount = 0;
    }

    public int getId(){
        return this.id;
    }
    
    public boolean isBusy(){
        return this.busy;
    }
    
    public void setBusy(boolean busy){
        this.busy = busy;
    }
    
    public Queue getQueue(){
        return this.line;
    }

    public int length(){
        return this.line.size();
    }

    public boolean isOpen(){
        return this.open;
    }

    public SecurityAgent getAssignedAgent(){
        return this.assignedAgent;
    }

    public int getTotalOpenTime(){
        return this.totalOpenTime;
    }

    public int getAgentsWorkedCount(){
        return this.agentsWorkedCount;
    }

    public int getPassengersSearchedCount(){
        return this.passengersSearchedCount;
    }

    public int getInappropriateCarryonsFoundCount(){
        return this.inappropriateCarryonsFoundCount;
    }

    /**
     * opens a security line by assigning an agent (when a security clocks in)
     * increases count of agents who worked here
     *
     * @param securityAgent: agent who will work here now
     */
    public void openLine(SecurityAgent securityAgent){
        this.open = true;
        this.assignedAgent = securityAgent;
        this.agentsWorkedCount++;
    }

    /**
     * closes the security line and removes the agent (when clocking off)
     * the line will not take new passengers now
     */
    public void closeLine(){
        this.open = false;
        this.assignedAgent = null;
    }

    /**
     * adds one to total passengers searched count
     */
    public void incrementPassengersSearched(){
        this.passengersSearchedCount++;
    }

    /**
     * adds one to inappropriate carryons found count
     */
    public void incrementInappropriateCarryonsFound(){
        this.inappropriateCarryonsFoundCount++;
    }

    /**
     * adds the time to how long this line was open
     *
     * @param time: time to add to open time
     */
    public void addToTotalOpenTime(int time){
        this.totalOpenTime += time;
    }
}
