import java.util.ArrayList;
import java.util.List;

/** Class: SecurityAgentList
*
* Remarks: this class keeps track of all security agents using a linked list.
* similar to PassengerList, allows adding and finding agents.
* -----------------------------------------
*/
class SecurityAgentList {

    private SecurityAgentNode head;

    /**
    * adds a new SecurityAgent to the front of the list
    *
    * @param agent: the SecurityAgent to add to the list
    */
    public void add(SecurityAgent agent){
        if(agent == null){
            throw new IllegalArgumentException("null agent");
        }
        this.head = new SecurityAgentNode(agent, this.head);
    }

    /**
    * find an agent by their agent number
    *
    * @param agentNumber: the number of the agent to find
    * @return the agent or null if not found
    */
    public SecurityAgent findByNumber(int agentNumber){
        SecurityAgentNode current = this.head;
        while(current != null){
            if(current.getAgent().getAgentNumber() == agentNumber){
                return current.getAgent();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
    * checks if the list is empty
    *
    * @return true if empty, else false
    */
    public boolean isEmpty(){
        return this.head == null;
    }

    public SecurityAgent getHead(){
        SecurityAgentNode current = this.head;
        return current.getAgent();
    }

    /**
     * returns an iterable list of all security agents for easy iteration
     * 
     * @return list of all agents in insertion order
     */
    public List<SecurityAgent> toList(){
        List<SecurityAgent> agents = new ArrayList<>();
        SecurityAgentNode current = this.head;
        while(current != null){
            agents.add(current.getAgent());
            current = current.getNext();
        }
        return agents;
    }

}