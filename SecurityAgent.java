/**
 * CLASS: SecurityAgent
 *
 * REMARKS: this class represents a security worker in the airport simulation
 * it tracks when the agent arrives, leaves, their suspicion level to flag people,
 * and keeps data about how many passengers and bags they checked.
 * -----------------------------------------
 */
public class SecurityAgent {

    private int agentNumber;
    private int arrivalTime;
    private int shiftEndTime;
    private int suspicionThreshold;
    private int passengersSearched;
    private int carryonsRejected;
    private int actualWorkTime;

    /**
     * constructor for security agent.
     *
     * @param agentNumber: the unique number for the agent
     * @param arrivalTime: time when agent comes to work
     * @param shiftEndTime: time when agent's scheduled shift ends
     * @param suspicionThreshold: the level when agent flags passengers for extra screening
     */
    public SecurityAgent(int agentNumber, int arrivalTime, int shiftEndTime, int suspicionThreshold){
        this.agentNumber = agentNumber;
        this.arrivalTime = arrivalTime;
        this.shiftEndTime = shiftEndTime;
        this.suspicionThreshold = suspicionThreshold;
        this.passengersSearched = 0;
        this.carryonsRejected = 0;
        this.actualWorkTime = 0;
    }

    // getters and setters

    public int getAgentNumber(){
        return this.agentNumber;
    }

    public int getArrivalTime(){
        return this.arrivalTime;
    }

    public int getShiftEndTime(){
        return this.shiftEndTime;
    }

    public int getSuspicionThreshold(){
        return this.suspicionThreshold;
    }

    public int getPassengersSearched(){
        return this.passengersSearched;
    }

    public int getCarryonsRejected(){
        return this.carryonsRejected;
    }

    public int getActualWorkTime(){
        return this.actualWorkTime;
    }

    /**
     * adds one to the number of passengers this agent searched
     */
    public void incrementPassengersSearched(){
        this.passengersSearched++;
    }

    /**
     * adds one to the number of carryons this agent rejected
     */
    public void incrementCarryonsRejected(){
        this.carryonsRejected++;
    }

    /**
     * adds given time to the actual work time
     *
     * @param timeToAdd: amount of time to add to work time
     */
    public void addActualWorkTime(int timeToAdd){
        this.actualWorkTime += timeToAdd;
    }
}