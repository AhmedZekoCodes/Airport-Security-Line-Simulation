/** Class: OrderedList
*
* Remarks: this is a linked list that keeps everything in sorted order.
* when you add something it automatically finds the right spot to put it
* so the list stays organized by comparing items.
* -----------------------------------------
*/
class OrderedList extends LinkedList {

    /**
    * adds a new item to the list in the right sorted position
    *
    * @param item: the list item to add in order
    */
    public void insertOrdered(ListItem item){
        // error if there is no item
        if(item == null){
            throw new IllegalArgumentException("null item");
        }
        
        // make a new node with the item
        Node newNode = new Node(item, null);
        
        // if list is empty this becomes the head
        if(this.head == null){
            this.head = newNode;
            this.size++;
            return;
        }
        
        // check if it goes at the very beginning
        if(this.head.getData() instanceof ListItem headItem){
            if(item.compareOrder(headItem) <= 0){
                newNode.setNext(this.head);
                this.head = newNode;
                this.size++;
                return;
            }
        }
        
        // find the right spot in the middle or end
        Node previous = this.head;
        Node current = this.head.getNext();
        
        // loop through the list to find where it goes
        while(current != null){
            // safe cast the current nodes data
            if(current.getData() instanceof ListItem currentItem){
                // if we found the right spot, stop looking
                if(item.compareOrder(currentItem) <= 0){
                    break;
                }
            }
            
            // keep moving forward
            previous = current;
            current = current.getNext();
        }
        
        // insert the new node between previous and current
        newNode.setNext(current);
        previous.setNext(newNode);
        this.size++;
    }

    /**
    * removes and returns the first item from the list
    *
    * @return the first list item
    */
    public ListItem removeFirst(){
        // if list is empty, throw error
        if(this.head == null){
            throw new IllegalStateException("list empty");
        }
        
        // grab the first item safely
        ListItem firstItem = null;
        if(this.head.getData() instanceof ListItem item){
            firstItem = item;
        }
        
        // move head to next node and decrease size
        this.head = this.head.getNext();
        this.size--;
        
        return firstItem;
    }

    /**
    * look at the first item without removing it
    *
    * @return the first list item
    */
    public ListItem peekFirst(){
        // cant peek when list is empty
        if(this.head == null){
            throw new IllegalStateException("list empty");
        }
        
        // safe cast and return the first item
        if(this.head.getData() instanceof ListItem firstItem){
            return firstItem;
        }
        
        return null;
    }
}
