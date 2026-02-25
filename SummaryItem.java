/** Class: SummaryItem
*
* Remarks: this class holds passenger data so we can put it in a list,
* it extends ListItem so it can be compared and sorted with other items
* by using the passengers ID number
* -----------------------------------------
*/
class SummaryItem extends ListItem {

    public final Passenger passenger;

    /**
    * makes a new summary item with the passenger info
    *
    * @param passenger: the passenger object to store in this item
    */
    public SummaryItem(Passenger passenger){
        this.passenger = passenger;
    }

    
    /**
    * compares this item with another one to figure out the order
    *
    * @param other: the other list item we're comparing to
    * @return the difference between IDs, negative if this comes first, positive if after
    */
    @Override
    public int compareOrder(ListItem other){
        // check if the other item is actually a SummaryItem, safe cast
        if(other instanceof SummaryItem otherSummary){
            // compare the passenger IDs to figure out order
            return this.passenger.getId() - otherSummary.passenger.getId();
        }
        // if its not a SummaryItem then something went wrong
        throw new IllegalArgumentException("not SummaryItem");
    }
}
