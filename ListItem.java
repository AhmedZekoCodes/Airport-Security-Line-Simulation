/** Class: ListItem
*
* Remarks: abstract base for things that can be stored inside
* ordered lists. we define compareOrder so different data types
* can be compared and sorted.
* -----------------------------------------
*/
abstract class ListItem {

    /**
    * compares this item with another one to figure out the ordering
    *
    * @param other: the other list item to compare with
    * @return negative if this comes first, zero if equal, positive if after
    */
    public abstract int compareOrder(ListItem other);
}