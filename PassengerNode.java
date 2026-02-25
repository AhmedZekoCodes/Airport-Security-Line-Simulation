/** Class: PassengerNode
*
* Remarks: this is a node for building a linked list for the passengers,
* it holds a passenger and a pointer to the next node so you can
* link them all together.
* -----------------------------------------
*/
class PassengerNode {

    private Passenger passenger;
    
    private PassengerNode next;

    /**
    * makes a new passenger node with a passenger and link to next node
    *
    * @param passenger: the passenger to store in this node
    * @param next: pointer to the next node
    */
    public PassengerNode(Passenger passenger, PassengerNode next){
        this.passenger = passenger;
        this.next = next;
    }

    public Passenger getPassenger(){
        return this.passenger;
    }
    
    public PassengerNode getNext(){
        return this.next;
    }
    
    public void setNext(PassengerNode next){
        this.next = next;
    }
}
