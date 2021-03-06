
package ElevatorProj;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    Queue<PassengerRequest>[][] floorsQ;
    Queue<PassengerRequest>[] carrage;
    LinkedList<Integer> stops;
    boolean direction; //true-up::false-down
    int currentWeight, maxWeight;
    
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }
    
    public void initialize(Queue<PassengerRequest> requests) {
		servingQueue = requests;
                //0 is down 1 is up!
                floorsQ = new Queue[floors][2];
                carrage = new Queue[floors];
                stops = new LinkedList<>();
                
                addToStops(1);
                
                //false is down, true is up
                direction = true;
                
                //initialize all of the floor call queues
                for(int i=0;i < floorsQ.length; i++){
                    for(int j=0;j<floorsQ[0].length;j++)
                        floorsQ[i][j]=new LinkedList<>();
                }
                
                for(int i=0;i<carrage.length;i++)
                    carrage[i]= new LinkedList<>();
                
                currentWeight = 0;
	}
    
    public ArrayList<PassengerReleased> move() {
        //stop elevator function if it is not supposed to keep running
        if (!continueOperate()) return null;
		
        //initialize array to keep track of released passengers during current move
	ArrayList<PassengerReleased> released =
            new ArrayList<>();
                
        //empty passengerRequests into the queues that represent the 
        //floor call buttons from serving queue until the next 
        //Request "hasn't happend yet" or is empty
        long nextRequestTime = servingQueue.peek().getTimePressedButton().getTime();
        long timeNow = currentTime.getTime();
        
        while(!servingQueue.isEmpty() 
                && nextRequestTime <= timeNow){
            System.out.println(nextRequestTime);
            addToFloors(servingQueue.remove());
            nextRequestTime = servingQueue.peek().getTimePressedButton().getTime();
        }
        
        //if the elevator is at the bottom or top of the shaft then reverse direction
        if(currentFloor==floors-1)
            direction=false;
        else if(currentFloor == 1)
            direction=true;
        
        //let out all passengers looking to get off on this floor
        for(PassengerRequest p: carrage[currentFloor])
            released.add(new PassengerReleased(p,currentTime));
        
        //let passengers that are waiting on this floor to travle in current direction 
        int dirTemp = 0;
        if(direction)
            dirTemp =1;
        for(PassengerRequest p: floorsQ[currentFloor][dirTemp])
            if(!isAtCapacity()){
                carrage[currentFloor].add(p);
                    addToStops(p.getFloorTo());//sorted insert won't duplicate values
            }
                
        

        //look for floor calls in the dirtection of travel
        if(direction){
            int i = currentFloor+1;
            while(i<floors){
                if(!floorsQ[i][1].isEmpty()) 
                    addToStops(i);//sorted insert won't duplicate values
                i++;
            }
        }else{
            int i = currentFloor;
            while(i>0){
                if(!floorsQ[i][0].isEmpty()) 
                    addToStops(i);//sorted insert won't duplicate values
                i--;
            }
        }    
            
        //remove current floor from the list of stops the elevator needs to make
        //stops.remove(currentFloor);
        removeCurrentFloor();
        
        //figure out what the next stop should be 
        int i, nextFloor=currentFloor;
        if(!stops.isEmpty()){
            if(direction){
                i=0;
                while(stops.get(i)<=currentFloor)
                    i++;
                nextFloor = stops.get(i);
            }else{
                i=floors;
                while(stops.get(i)>0)
                    i--;
                nextFloor = stops.get(i);
            }
        }
        
        long timeInMiliseconds = currentTime.getTime() + 
				this.doorDelta*1000 +  // delta to open AND close the door to let the passenger in
				1000*this.timeMoveOneFloor* (Math.abs(currentFloor - nextFloor));
        
        currentTime.setTime(timeInMiliseconds);
        System.out.println("currentFloor: "+currentFloor+" |::| nextFloor: "+nextFloor+" |::| Time"+ currentTime);
        currentFloor = nextFloor;
        
        return released;
    }
    
    public boolean continueOperate() {
        return !servingQueue.isEmpty();
    }
    
    
    private void addToFloors(PassengerRequest passenger){
        if(passenger.getFloorFrom()<passenger.getFloorTo())
            floorsQ[passenger.getFloorFrom()][1].add(passenger);
        else
             floorsQ[passenger.getFloorFrom()][0].add(passenger);
    }
    
    private boolean isCarrageEmpty(){
        return currentWeight==0;
    }
    
    public boolean isAtCapacity(){
        return currentWeight-capacity-maxWeight <=0;
    }
    
    private void addToStops(int i){
        
        if(stops.size() == 0) {
            stops.add(i);
        }else if(stops.contains(i)){
            return;
        }else if(stops.get(0) > i) {
            stops.add(0,i);
        }else if (stops.get(stops.size() - 1) < i) {
            stops.add(stops.size(),i);
        }else {
            int j=0;
            while (stops.get(j) < i) {
                j++;
            }
            stops.add(j, i);
        }

    }
    
    public void removeCurrentFloor(){
        int i = 0;
        while(i<stops.size())
            if(stops.get(i)==currentFloor)
                stops.remove(i); 
    }
    
    public static void main(String[] args){
        Elevator e = new SimpleElevator(10,10,10,15,true);
        e.initialize(RequestGenerator.RequestGenerator(250,10,100,(long)300000));
        
        ArrayList<PassengerReleased> output = e.operate();
		
		long cost = 0;
		
		for (int i = 0; i < output.size(); i++) {
			
			PassengerReleased passenger = output.get(i);
			Time timeRequested = passenger.getTimeArrived();
			Time timeLeft = passenger.getPassengerRequest().getTimePressedButton();

			cost+=Math.abs(timeLeft.getTime() - timeRequested.getTime());
		}
		
		System.out.println("Total cost (in seconds): " + cost/ 1000);
    }
    
    
}
