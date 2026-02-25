/** Class: Passenger
*
* Remarks: this class represents a passenger going through airport security.
* it keeps track of their info like arrival time, flight time, how many bags
* they have, and if theyre suspicious or have illegal stuff.
* -----------------------------------------
*/
class Passenger {

    private static int nextId = 42;
    
    private static java.util.Random criminalGenerator = new java.util.Random(1001);
    private static java.util.Random suspicionRandom = new java.util.Random(1001);

    private int id;
    private int arrivalTimeFirst;
    private int flightDeparture;
    private int carryons;
    private boolean hasIllegal;
    //private boolean suspicious;
    private boolean criminal;
    private boolean firstClass;
    private boolean returning;
    private int suspicion;
    private int firstSearchFinishTime;
    private int finalExitTime;
    private int waitSecurity;
    private int waitSecondary;
    private int secondaryEnterTime;
    private int securityJoinTime;

    /**
    * makes a new passenger with all their info and randomly decides if theyre a criminal
    *
    * @param arrival: when they first arrive at security
    * @param flightDeparture: when their flight leaves
    * @param carryons: how many carry on bags they have
    * @param firstClass: if theyre in first class or coach
    * @param hasIllegal: if they have illegal stuff in their bags
    */
    public Passenger(int arrival, int flightDeparture, int carryons, boolean firstClass, boolean hasIllegal){
        // give them a unique id number
        this.id = nextId++;
        
        // save all their basic info
        this.arrivalTimeFirst = arrival;
        this.flightDeparture = flightDeparture;
        this.carryons = carryons;
        this.firstClass = firstClass;
        this.hasIllegal = hasIllegal;
        this.suspicion = suspicionRandom.nextInt(100) + 1;
        
        // set default values for tracking their progress
        this.returning = false;
        this.firstSearchFinishTime = -1;
        this.finalExitTime = -1;
        this.waitSecurity = 0;
        this.waitSecondary = 0;
        this.secondaryEnterTime = -1;
        this.securityJoinTime = -1;
        
        // randomly decide if theyre a criminal based on constants percentage
        this.criminal = (criminalGenerator.nextInt(100) + 1) < Constants.CRIME_PERCENT;
    }

    //getters
    public int getId(){
        return this.id;
    }
    
    public int getArrivalTimeFirst(){
        return this.arrivalTimeFirst;
    }
    
    public int getFlightDeparture(){
        return this.flightDeparture;
    }
    
    public int getCarryons(){
        return this.carryons;
    }
    
    public boolean hasIllegal(){
        return this.hasIllegal;
    }
    
    public boolean isCriminal(){
        return this.criminal;
    }
    
    public boolean isFirstClass(){
        return this.firstClass;
    }
    
    public boolean isReturning(){
        return this.returning;
    }
    
    public int getFirstSearchFinishTime(){
        return this.firstSearchFinishTime;
    }
    
    public int getFinalExitTime(){
        return this.finalExitTime;
    }
    
    public int getSecondaryEnterTime(){
        return this.secondaryEnterTime;
    }
    
    public int getSecurityJoinTime(){
        return this.securityJoinTime;
    }

    // setters
    public void setReturning(boolean value){
        this.returning = value;
    }
    
    public void setFirstSearchFinishTime(int time){
        this.firstSearchFinishTime = time;
    }
    
    public void setFinalExitTime(int time){
        this.finalExitTime = time;
    }
    
    public void setSecondaryEnterTime(int time){
        this.secondaryEnterTime = time;
    }
    
    public void setSecurityJoinTime(int time){
        this.securityJoinTime = time;
    }

    public int getSuspicion() {
        return this.suspicion;
    }

    public void increaseSuspicion(int value) {
        this.suspicion += value;
        if(this.suspicion > 100){
            this.suspicion = 100;
        }
    }

    /**
    * removes one carry on bag and marks illegal stuff as handled
    */
    public void decCarryon(){
        this.carryons--;
        this.hasIllegal = false;
    }

    /**
    * adds wait time to the security wait total
    *
    * @param waitTime: how long they waited in security
    */
    public void addWaitSecurity(int waitTime){
        this.waitSecurity += waitTime;
    }

    /**
    * adds wait time to the secondary wait total
    *
    * @param waitTime: how long they waited in secondary
    */
    public void addWaitSecondary(int waitTime){
        this.waitSecondary += waitTime;
    }

    /**
    * calculates the total time they spent waiting in both security and secondary
    *
    * @return total wait time in minutes
    */
    public int getTotalWait(){
        return this.waitSecurity + this.waitSecondary;
    }

    /**
    * figures out the status code based on if theyre suspicious or criminal
    *
    * @return SC if both, S if suspicious, C if criminal, or dash if neither
    */
    public String statusCode() {
        boolean isSuspiciousNow = this.suspicion > 50;

        if (isSuspiciousNow && this.criminal) {
            return "SC";
        }
        if (isSuspiciousNow) {
            return "S";
        }
        if (this.criminal) {
            return "C";
        }
        return "-";
    }

    /**
    * returns their class label as a string
    *
    * @return First or Coach depending on their ticket
    */
    public String classLabel(){
        if(this.firstClass){
            return "First";
        }
        else{
            return "Coach";
        }
    }
}

/** Class: FirstClassPassenger
*
* Remarks: this is just a first class passenger which extends the regular passenger.
* it automatically sets them as first class and overrides the label method
* to always return First.
* -----------------------------------------
*/
class FirstClassPassenger extends Passenger {

    private String classLabel = "First";

    /**
    * makes a new first class passenger with their info
    *
    * @param arrival: when they first arrive at security
    * @param flightDeparture: when their flight leaves
    * @param carryons: how many carry on bags they have
    * @param hasIllegal: if they have illegal stuff in their bags
    */
    public FirstClassPassenger(int arrival, int flightDeparture, int carryons, boolean hasIllegal){
        super(arrival, flightDeparture, carryons, true, hasIllegal);
    }

    public String classLabel(){
        return this.classLabel;
    }
}
