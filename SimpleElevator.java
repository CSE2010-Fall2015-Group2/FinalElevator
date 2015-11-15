
package elevatorproj;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }
    
    public void initialize(Queue<ElevatorProj.PassengerRequest> requests) {
		Elevator.servingQueue = requests;
                Queue<ElevatorProj.PassengerRequest>[][] floorsQ = new Queue[Elevator.floors][2];
                
                //initialize all of the queues
                for(int i=0;i < floorsQ.length; i++){
                    for(int j=0;j<floorsQ[0].length;j++)
                        floorsQ[i][j]=new LinkedList<>();
                }
	}
    
    public ArrayList<ElevatorProj.PassengerReleased> move() {
		if (!continueOperate()) return null;
		
		ArrayList<ElevatorProj.PassengerReleased> released =
					new ArrayList<>();
                
                while(Elevator.servingQueue.peek().getTimePressedButton().getTime
                        <= Elevator.currentTime.getTime){
                    
                }
                
    }
    
    public boolean continueOperate() {
        return !Elevator.servingQueue.isEmpty();
    }
    
    private void addToFloors(PassengerRequest passenger){
        if(passenger.getFloorFrom()<passenger.getFloorTo()){
            
        }
    }
    
    
}
