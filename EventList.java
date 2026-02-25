/** Class: EventList
*
* Remarks: this is the ordered list of all pending events that need to happen.
* events are kept sorted by their time and passenger id so we always know
* what should happen next.
* -----------------------------------------
*/
class EventList extends OrderedList {

    /**
    * adds an event into the list sorted by its time and passenger number
    *
    * @param event: the event object to insert
    */
    public void addEvent(Event event){
        // cant add a null event to the list
        if(event == null){
            throw new IllegalArgumentException("cannot add null event");
        }
        
        // use the parent class method to insert it in order
        this.insertOrdered(event);
    }

    /**
    * removes the earliest event from the front of the list
    *
    * @return the earliest event
    */
    public Event removeEvent(){
        // cant remove from an empty list
        if(this.isEmpty()){
            throw new IllegalStateException("no events");
        }
        
        // safe cast the removed item to an Event
        if(this.removeFirst() instanceof Event removedEvent){
            return removedEvent;
        }
        
        return null;
    }

    /**
    * checks the next event without removing it
    *
    * @return the next event or null if empty
    */
    public Event peekEvent(){
        // if theres nothing just return null
        if(this.isEmpty()){
            return null;
        }
        
        // safe cast the head data to an Event
        if(this.head.getData() instanceof Event nextEvent){
            return nextEvent;
        }
        
        return null;
    }
}
