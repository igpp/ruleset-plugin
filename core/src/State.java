package igpp.ruleset;

/** 
 * State defines execution states.
 *
 * @author      Todd King
 * @author      Planetary Data System
 * @version     1.0, 04/21/03
 * @since       1.0
 */
public class State {
	/** The list of elements in the label */
 	public boolean		mGood = false;
 	
    /** 
     * Creates an instance of a state.
	 *
     * @param good	the execution of the state.
     *
     * @since           1.0
     */
 	public State(boolean good) {
 		mGood = good;
	}
}