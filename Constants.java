/** Class: Constants
*
* Remarks: this class just holds the constant values. every constant
* is timing or probabilities for the simulation.
* -----------------------------------------
*/
class Constants{
    public static final int CRIME_PERCENT = 20; //percent that a passenger is a criminal
    public static final int CAUGHT_PERCENT = 50; //percent that a criminal gets caught during interview
    public static final int LAY_BAGS = 1; //time to lay bags before search starts
    public static final int TIME_PER_CARRYON = 1; //time to check each carryon
    public static final int BASE_SEARCH_TIME = 1; //base time to search a passenger
    public static final int SUSPICIOUS_MULTIPLIER = 4; //multiplier if the passenger is suspicious
    public static final int ESCORT_DELAY = 2; //delay to escort a passenger to secondary screening
    public static final int BAG_CHECK_DELAY = 8; //delay to go back and check a bad carryon
    public static final int INTERVIEW_TIME = 5; //fixed time for an interview
}