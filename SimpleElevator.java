package ElevatorProj;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleElevator extends Elevator{
    
    
    Queue<PassengerRequest>[][] floorsQ;
    Queue<PassengerRequest>[] carriage;
    LinkedList<Integer> stops;
    boolean direction,//true-up::false-down
            foundStop; 
    int currentWeight;
    static int passengers, eCap, numOfFloors,
             maxPassengerWeight, doorOpenTime, betweenFloors;
    static long passengerDelta;
    
    
    /******************************************************************
     * Constructor for simple elevator
     * @param capacity
     * @param timeMoveOneFloor
     * @param floors
     * @param doorDelta
     * @param verbose 
     */
    public SimpleElevator(int capacity, int timeMoveOneFloor, 
			int floors, int doorDelta, boolean verbose){
        super(capacity, timeMoveOneFloor, floors, doorDelta, verbose);
    }//_____________________________________________________Defualt Constructor
    
    
    /*********************************************************************
     * initialize the elevator to starting conditions and load in servingQueue 
     * @param requests 
     */
    public void initialize(Queue<PassengerRequest> requests) {
        servingQueue = requests;
        //0 is down 1 is up!
        floorsQ = new Queue[floors+1][2];//used to represent up and down button on each floor
        carriage = new Queue[floors+1];//used to hold the passengers in the elevator-possible room for improvement return entire queue or list instead of one at a time
        stops = new LinkedList<>();//holding points the elevator must visit

        //false is down, true is up
        direction = true;

        //initialize all of the floor call queues
        for(int i=0;i < floorsQ.length; i++)
            for(int j=0;j<floorsQ[0].length;j++)
                floorsQ[i][j]=new LinkedList<>();

        //initialize all of the queues for holing passengers in the elevator
        for(int i=0;i<carriage.length;i++)
            carriage[i]= new LinkedList<>();

        
        currentWeight = 0;
        
        //start up gui around here
    }//_____________________________________________________________initialized
    
    
    /********************************************************************
     * This method does the heavy lifting. all of the functions of the elevator
     * are carried out here
     * @return 
     */
    public ArrayList<PassengerReleased> move() {
        //stop elevator function if it is not supposed to keep running
        if (!continueOperate()) 
            return null;
		
        //initialize array to keep track of released passengers during current move
	ArrayList<PassengerReleased> released = new ArrayList<>();
                
        //empty passengerRequests into the queues that represent the 
        //floor call buttons from serving queue until the next 
        //Request "hasn't happend yet" or is empty
        if(!servingQueue.isEmpty()){
            long nextRequestTime = servingQueue.peek().getTimePressedButton().getTime();
            long timeNow = currentTime.getTime();

            System.out.println(servingQueue.peek());
            while(!servingQueue.isEmpty() 
                    && nextRequestTime <= timeNow){

                System.out.println(servingQueue.peek());
                addToFloors(servingQueue.poll());
                if(!servingQueue.isEmpty())
                    nextRequestTime = servingQueue.peek().getTimePressedButton().getTime();
                //can pass these people or some repusentation of them to the gui
            }
        }
        
        
        //if the elevator is at the bottom or top of the shaft then reverse direction
        if(currentFloor==floors)
            direction=false;
        else if(currentFloor == 1)
            direction=true;
        
        
        //let out all passengers looking to get off on this floor
        for(PassengerRequest p: carriage[currentFloor])
            released.add(
                    new PassengerReleased(p,new Time(currentTime.getTime())));

        carriage[currentFloor].clear();
        //can pass these to gui as well either here or just the entire released at the end 
        
        
        //let passengersin that are waiting on this floor to 
        //travle in current direction 
        int dirTemp = 0;
        if(direction)
            dirTemp =1;
        while(!floorsQ[currentFloor][dirTemp].isEmpty()
                &&!isAtCapacity(floorsQ[currentFloor][dirTemp]
                        .peek().getWeight())){
            PassengerRequest p = floorsQ[currentFloor][dirTemp].remove();
            carriage[p.getFloorTo()].add(p);
        }
            
        
        //move to next floor in current direction.
        if(direction)
            currentFloor++;
        else
            currentFloor--;
        
        //add the time to move one floor and time to open and close door
        long timeInMiliseconds = currentTime.getTime() + 
				this.doorDelta*1000 +  
                                1000*this.timeMoveOneFloor;

        currentTime.setTime(timeInMiliseconds);
        
        //status print for debug and tracking purposes
        System.out.println("currentFloor: "+currentFloor+
                //" |::| nextFloor: "+nextFloor+
                " |::| Time"+ currentTime);
        System.out.println("*************************************************");

        
        return released;
    }//_______________________________________________________________move
    
    
    /*****************************************************************
     * returns true if there are people in the serving queue, waiting on 
     * the floors or in the elevator itself; false otherwise.
     * @return 
     */
    public boolean continueOperate() {
        if(!servingQueue.isEmpty())
            return true;
        for(int i = 0; i<floors; i++)
            if(!floorsQ[i][0].isEmpty() || !floorsQ[i][1].isEmpty())
                return true;  
        for(int i = 0; i<floors; i++)
            if(!carriage[i].isEmpty())
                return true;
        return false;
    }//_____________________________________________________continueOperate
    
    
    
    /*****************************************************************
     * DEPRICATED 
     * searches floor calls and elevator calls and determines which floor is
     * next. it only checks the direction of current travel for the elevator.
     * if it finds a stop to be added it sets a flag to true. if after this runs
     * and the flag is still set to false the elevator should change direction
     * than run this again and if still the flag is not set the elevator has no 
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
    }//____________________________________________________________findAllCalls
    
    
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
    }//_____________________________________________________________addToFloors
    
    
    /********************************************************************
     * returns true if there are no "people" in the elevator. ie current weight 
     * is 0 false otherwise
     * @return 
     */
    private boolean isCarrageEmpty(){
        return currentWeight==0;
    }//__________________________________________________________isCarrageEmpty
    
    
    /********************************************************************
     * returns true if there is not enough room to fit one more max weight 
     * person into the elevator false otherwise
     * @return 
     */
    public boolean isAtCapacity(int nextPerson){
        return capacity - currentWeight - nextPerson <=0;
    }//___________________________________________________________isAtCapacity
    
    
    /********************************************************************
     * DEPRICATED.
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

    }//_____________________________________________________________addToStops
    
    
    /**********************************************************************
     * DEPRICATED. 
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
    }//______________________________________________________removeCurrentFloor
    
    
    /********************************************************************
     * Main Method
     * @param args 
     */
    
    
    public static void main(String[] args){
        
        eCap = 3000;//lbs
        numOfFloors = 150;
        passengerDelta = 30000;//max time between passengers in mS.
        passengers = 100;//number of people
        maxPassengerWeight = 250;
        betweenFloors = 10;
        doorOpenTime = 15;
        
        Elevator e = new SimpleElevator(eCap,betweenFloors,numOfFloors,doorOpenTime,true);
        e.initialize(RequestGenerator.RequestGenerator(maxPassengerWeight,numOfFloors,passengers,passengerDelta));
        
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
    }//_________________________________________________________________main
    
    
}
