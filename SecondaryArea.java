/** Class: SecondaryArea
*
* Remarks: this class represents the secondary screening area at the airport.
* it keeps track of how many interview rooms are free and has a waiting queue
* for passengers who have to wait for a room to open up.
* -----------------------------------------
*/
class SecondaryArea {

    private Queue waiting;
    
    private int freeRooms;
    
    private int totalRooms;

    /**
    * makes a new secondary area with a certain number of interview rooms
    *
    * @param rooms: how many interview rooms there are
    */
    public SecondaryArea(int rooms){
        // make a new queue for passengers waiting
        this.waiting = new Queue();
        
        // set up the room counts
        this.freeRooms = rooms;
        this.totalRooms = rooms;
    }

    public int getFreeRooms(){
        return this.freeRooms;
    }
    
    public int getTotalRooms(){
        return this.totalRooms;
    }

    /**
    * decreases one room when a passenger starts their interview
    */
    public void decRoom(){
        // make sure there are rooms available
        if(this.freeRooms <= 0){
            throw new IllegalStateException("no free rooms");
        }
        
        // decrease a room
        this.freeRooms--;
    }

    /**
    * adds back one room when an interview finishes
    */
    public void incRoom(){
        // making sure we dont go over the total rooms
        if(this.freeRooms >= this.totalRooms){
            throw new IllegalStateException("room count overflow");
        }
        
        // free up a room
        this.freeRooms++;
    }

    public Queue getWaitingQueue(){
        return this.waiting;
    }

    public boolean hasFreeRoom(){
        return this.freeRooms > 0;
    }
}
