
package elevatorproj;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    Queue<ElevatorProj.PassengerRequest>[][] floorsQ;
    Queue<ElevatorProj.PassengerRequest>[] carrage;
    boolean direction;
    
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }
    
    public void initialize(Queue<ElevatorProj.PassengerRequest> requests) {
		Elevator.servingQueue = requests;
                //0 is down 1 is up!
                floorsQ = new Queue[Elevator.floors][2];
                carrage = new Queue[Elevator.floors];
                
                //false is down, true is up
                direction = true;
                
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
                
                while(!Elevator.servingQueue.isEmpty() 
                        && Elevator.servingQueue.peek().getTimePressedButton().getTime
                        <= Elevator.currentTime.getTime)
                    addToFloors(Elevator.servingQueue.pop());
                
                if(isCarrageEmpty()){
                    
                }
                
                
    }
    
    public boolean continueOperate() {
        return !Elevator.servingQueue.isEmpty();
    }
    
    
    private void addToFloors(PassengerRequest passenger){
        if(passenger.getFloorFrom()<passenger.getFloorTo())
            floorsQ[passenger.getFloorFrom()][1]=passenger;
        else
             floorsQ[passenger.getFloorFrom()][0]=passenger;
    }
    
    private boolean isCarrageEmpty(){
        return Elevator.weight==0;
    }
    
    
}
