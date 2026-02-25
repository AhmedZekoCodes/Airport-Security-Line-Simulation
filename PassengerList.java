/** Class: PassengerList
*
* Remarks: this class keeps track of all the passengers using a linked list.
* you can add passengers to it and find them later by their id number.
* its like a simple database for passengers.
* -----------------------------------------
*/
class PassengerList {

    private PassengerNode head;

    /**
    * adds a new passenger to the front of the list
    *
    * @param passenger: the passenger to add to the list
    */
    public void add(Passenger passenger){
        // cant add a null passenger
        if(passenger == null){
            throw new IllegalArgumentException("null passenger");
        }
        
        // add the new passenger at the beginning
        this.head = new PassengerNode(passenger, this.head);
    }

    /**
    * searches through the list to find a passenger by their id
    *
    * @param id: the passenger id number to search for
    * @return the passenger with that id or null if not found
    */
    public Passenger findById(int id){
        // start at the beginning of the list
        PassengerNode current = this.head;
        
        // loop through all the nodes until we find the current
        while(current != null){
            // check if this is the passenger were looking for
            if(current.getPassenger().getId() == id){
                return current.getPassenger();
            }
            
            // move to the next node
            current = current.getNext();
        }
        
        // null if we didnt find anyone with that id
        return null;
    }

    public boolean isEmpty(){
        return this.head == null;
    }
}
