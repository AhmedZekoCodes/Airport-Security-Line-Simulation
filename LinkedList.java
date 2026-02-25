/** Class: LinkedList
*
* Remarks: this is the main blueprint for making linked lists.
* its abstract so you cant make it directly but other classes can
* use it to build their own list types.
* -----------------------------------------
*/
abstract class LinkedList {

    protected Node head;
    
    protected int size;
    
    /**
    * sets up a new empty linked list
    */
    protected LinkedList(){
        this.head = null;
        this.size = 0;
    }
    
    public boolean isEmpty(){
        return this.size == 0;
    }
    
    public int size(){
        return this.size;
    }
    
    /**
    * clears out everything from the list and makes it empty again
    */
    public void clear(){
        this.head = null;
        this.size = 0;
    }
}
