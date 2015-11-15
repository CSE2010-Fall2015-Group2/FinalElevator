
package elevatorproj;

import java.util.ArrayList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }
    
    public void initialize(Queue<ElevatorProj.PassengerRequest> requests) {
		Elevator.servingQueue = requests;
	}
    
    public ArrayList<ElevatorProj.PassengerReleased> move() {
		if (!continueOperate()) return null;
		
		ArrayList<ElevatorProj.PassengerReleased> released =
					new ArrayList<>();
                
    }
    
    public boolean continueOperate() {
        return !Elevator.servingQueue.isEmpty();
    }
}
