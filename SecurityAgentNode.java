/** Class: SecurityAgentNode
*
* Remarks: this is a node for building linked list of agents.
* holds a SecurityAgent and a pointer to next node.
* -----------------------------------------
*/
class SecurityAgentNode {

    private SecurityAgent agent;
    private SecurityAgentNode next;

    /**
    * creates a new node with an agent and pointer to next node
    *
    * @param agent: the security agent stored in the node
    * @param next: ptr to the next node
    */
    public SecurityAgentNode(SecurityAgent agent, SecurityAgentNode next){
        this.agent = agent;
        this.next = next;
    }

    public SecurityAgent getAgent(){
        return this.agent;
    }

    public SecurityAgentNode getNext(){
        return this.next;
    }

    public void setNext(SecurityAgentNode next){
        this.next = next;
    }
}