/** Class: Queue
*
* Remarks: this is a queue data structure that works like a line.
* FIFO, first in first out. it extends LinkedList and keeps
* track of both the front and back of the line.
* -----------------------------------------
*/
class Queue extends LinkedList {

    private Node tail;

    /**
    * makes a new empty queue with nothing in it
    */
    public Queue(){
        super();
        this.tail = null;
    }

    /**
    * adds a new object to the back of the queue
    *
    * @param obj: the object to add to the queue
    */
    public void enqueue(Object obj){
        // cant add nothing to the queue
        if(obj == null){
            throw new IllegalArgumentException("enqueue null");
        }
        
        // make a new node with the object
        Node newNode = new Node(obj, null);
        
        // if the queue is empty, this is both the head and tail
        if(this.head == null){
            this.head = newNode;
            this.tail = newNode;
        }
        else{
            // otherwise add it to the back and update tail
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        
        //incrementing the size
        this.size++;
    }

    /**
    * removes and returns the object from the front of the queue
    *
    * @return the object that was at the front
    */
    public Object dequeue(){
        // checking if empty, cant remove from an empty queue
        if(this.head == null){
            throw new IllegalStateException("dequeue empty");
        }
        
        // grab the data from the front
        Object obj = this.head.getData();
        
        // move the head to the next node
        this.head = this.head.getNext();
        this.size--;
        
        // if we just removed the last object, tail should be null too
        if(this.head == null){
            this.tail = null;
        }
        
        return obj;
    }

    /**
    * looks at the front object without removing it
    *
    * @return the object at the front of the queue
    */
    public Object peek(){
        // cant look at an empty queue
        if(this.head == null){
            throw new IllegalStateException("peek empty");
        }
        
        // just return the data without changing anything
        return this.head.getData();
    }
}
