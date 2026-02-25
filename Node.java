/** Class: Node
*
* Remarks: this class builds a simple linked node. it holds an object as data
* and a pointer to the next node
* -----------------------------------------
*/
final class Node {

    private Object data;
    
    private Node next;

    /**
    * makes a new node with an object and a pointer to the next node
    *
    * @param data: the object stored in this node
    * @param next: pointer to next node in the chain
    */
    public Node(Object data, Node next){
        this.data = data;
        this.next = next;
    }

    // getters and setters
    public Object getData(){
        return this.data;
    }

    public void setData(Object data){
        this.data = data;
    }

    public Node getNext(){
        return this.next;
    }

    public void setNext(Node next){
        this.next = next;
    }
}
