
package ElevatorProj;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    Queue<PassengerRequest>[][] floorsQ;
    Queue<PassengerRequest>[] carrage;
    LinkedList<Integer> stops;
    boolean direction,//true-up::false-down
            foundStop; 
    int currentWeight, maxWeight;
    
    
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }
    
    public void initialize(Queue<PassengerRequest> requests) {
		servingQueue = requests;
                //0 is down 1 is up!
                floorsQ = new Queue[floors+1][2];
                carrage = new Queue[floors+1];
                stops = new LinkedList<>();
                
                addToStops(1);
                
                //false is down, true is up
                direction = true;
                
                //initialize all of the floor call queues
                for(int i=0;i < floorsQ.length; i++)
                    for(int j=0;j<floorsQ[0].length;j++)
                        floorsQ[i][j]=new LinkedList<>();
                
                
                for(int i=0;i<carrage.length;i++)
                    carrage[i]= new LinkedList<>();
                
                currentWeight = 0;
	}
    
    public ArrayList<PassengerReleased> move() {
        //stop elevator function if it is not supposed to keep running
        if (!continueOperate()) 
            return null;
		
        //initialize array to keep track of released passengers during current move
	ArrayList<PassengerReleased> released =
            new ArrayList<>();
                
        //empty passengerRequests into the queues that represent the 
        //floor call buttons from serving queue until the next 
        //Request "hasn't happend yet" or is empty
        long nextRequestTime = servingQueue.peek().getTimePressedButton().getTime();
        long timeNow = currentTime.getTime();
        
        System.out.println(servingQueue.peek());
        while(!servingQueue.isEmpty() 
                && nextRequestTime <= timeNow){
            
            System.out.println(servingQueue.peek());
            addToFloors(servingQueue.poll());
            if(!servingQueue.isEmpty())
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
        
        //let passengersin that are waiting on this floor to 
        //travle in current direction 
        int dirTemp = 0;
        if(direction)
            dirTemp =1;
        for(PassengerRequest p: floorsQ[currentFloor][dirTemp])
            if(!isAtCapacity()){
                carrage[currentFloor].add(p);
                    addToStops(p.getFloorTo());//sorted insert/won't duplicate
            }
                
        

        //look for floor calls in the dirtection of travel
        findAllCalls();
        
        if(!foundStop){
            
        }
        
        //if there are no more stops in current direction revese direction
        if(!foundStop){
            if(direction)
                direction = false;
            else
                direction = true;
        }
            
            
        //remove current floor from the list of stops the elevator needs to make
        //stops.remove(currentFloor);
        removeCurrentFloor();
        
        //figure out what the next stop should be 
        int i, nextFloor=currentFloor;
        System.out.println("finding next stop...");
        if(!stops.isEmpty()){
            if(direction){
                System.out.println("UP");
                i=0;
                while(i<stops.size() && stops.get(i)<currentFloor){
                    System.out.println(i);
                    i++;
                }
                nextFloor = stops.get(i);
            }else{
                System.out.println("DOWN");
                i=stops.size()-1;
                while(i>0&&stops.get(i)>currentFloor)
                    i--;
                nextFloor = stops.get(i);
            }
        }
        
        long timeInMiliseconds = currentTime.getTime() + 
				this.doorDelta*1000 +  
				1000*this.timeMoveOneFloor* 
                                (Math.abs(currentFloor - nextFloor));
        
        currentTime.setTime(timeInMiliseconds);
        System.out.println("currentFloor: "+currentFloor+
                " |::| nextFloor: "+nextFloor+" |::| Time"+ currentTime);
        System.out.println(stops);
        System.out.println("*************************************************");
        currentFloor = nextFloor;
        
        return released;
    }
    
    
    /*****************************************************************
     * TODO - change to allow the elevator to finish dropping off passengers
     * @return 
     */
    public boolean continueOperate() {
        return !servingQueue.isEmpty();
    }//________________________________________________________________
    
    /*****************************************************************
     * searches floor calls and elevator calls and determines which floor is
     * next. it only checks the diredction of current travel for the elevator.
     * if it finds a stop to be added it sets a flag to true. if after this runs
     * and the flag is still set to false the elevator should change direction
     * than run this again and if still the falg is not set the elevator has no 
     * calls and should either return to "home" position or stay put.
     */
    public void findAllCalls(){
        System.out.println("looking for floor calls...");
        
        foundStop = false;
        if(direction){
            System.out.println("UP");
            int i = currentFloor+1;
            while(i<floors && !foundStop){
                if(!floorsQ[i][1].isEmpty()) {
                    //addToStops(i);//sorted insert won't duplicate values
                    foundStop=true;
                }
                i++;
            }
        }else{
            System.out.println("DOWN");
            int i = currentFloor;
            while(i>0 && !foundStop){
                if(!floorsQ[i][0].isEmpty()) {
                    //addToStops(i);//sorted insert won't duplicate values
                    foundStop = true;
                }
                i--;
            }
        }
    }//_________________________________________________________________
    
    
    /*******************************************************************
     * adds passengerRequest to the button associated with their travel. ie if 
     * their floorFrom is 3 and they are going up they will press the button for
     * up on floor 3, storing them in that queue associated with that button.
     * @param passenger 
     */
    private void addToFloors(PassengerRequest passenger){
        System.out.println("add to floors: "+passenger.getFloorFrom());
        if(passenger.getFloorFrom()<passenger.getFloorTo())
            floorsQ[passenger.getFloorFrom()][1].add(passenger);
        else
             floorsQ[passenger.getFloorFrom()][0].add(passenger);
    }//_________________________________________________________________
    
    
    /********************************************************************
     * returns true if there are no "people" in the elevator. ie current weight 
     * is 0 false otherwise
     * @return 
     */
    private boolean isCarrageEmpty(){
        return currentWeight==0;
    }//__________________________________________________________________
    
    
    /********************************************************************
     * returns true if there is not enough room to fit one more max weight 
     * person into the elevator false otherwise
     * @return 
     */
    public boolean isAtCapacity(){
        return currentWeight-capacity-maxWeight <=0;
    }//_________________________________________________________________
    
    
    /********************************************************************
     * add floor i to stops. inserts new element in order in a linked list and 
     * there are no repeating elements.
     * @param i floor number
     */
    private void addToStops(int i){
        System.out.println("adding to stops: "+i);
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
            while (stops.get(j) < i&&j<=stops.size()) 
                j++;
            stops.add(j, i);
        }

    }//____________________________________________________________________
    
    /**********************************************************************
     * remove current floor from stops
    */
    public void removeCurrentFloor(){
        int i = 1;
        System.out.println("removing floor: "+ currentFloor);
        while(i<stops.size()){
            if(stops.get(i)==currentFloor)
                stops.remove(i); 
            i++;
        }
    }//__________________________________________________________________
    
    
    /**********************************
     * Main Method
     * @param args 
     */
    public static void main(String[] args){
        Elevator e = new SimpleElevator(10,10,10,15,true);
        e.initialize(RequestGenerator.RequestGenerator(250,10,100,(long)30000));
        
       ArrayList<PassengerReleased> output = e.operate();
		
		long cost = 0;
		
		for (int i = 0; i < output.size(); i++) {
			
			PassengerReleased passenger = output.get(i);
			Time timeRequested = passenger.getTimeArrived();
			Time timeLeft = passenger.getPassengerRequest()
                                .getTimePressedButton();

			cost+=Math.abs(timeLeft.getTime() - 
                                timeRequested.getTime());
		}
		
		System.out.println("Total cost (in seconds): " + cost/ 1000);
    }
    
    
}
