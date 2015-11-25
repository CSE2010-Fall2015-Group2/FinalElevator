/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElevatorProj;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author nicholas
 */
public class TestDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
        int eCap = 3000;//lbs
        int numOfFloors = 15;
        int passengerDelta = 30000;//max time between passengers in mS.
        int passengers = 100;//number of people
        int maxPassengerWeight = 250;
        int betweenFloors = 10;
        int doorOpenTime = 15;
        
        long smartCost=0;
        long dumbCost=0;
        for(int run = 0; run<100; run++){
            Queue<PassengerRequest> pq 
                    = RequestGenerator.RequestGenerator(
                            maxPassengerWeight,numOfFloors,
                            passengers,passengerDelta);
            
            Elevator e = new SimpleElevator(eCap,betweenFloors,numOfFloors,doorOpenTime,true);
            e.initialize(new LinkedList<PassengerRequest>(pq));
            
            Elevator s = new ImprovedElevator(eCap,betweenFloors,numOfFloors,doorOpenTime,true);
            s.initialize(pq);

            ArrayList<PassengerReleased> dumbOutput = e.operate();
            ArrayList<PassengerReleased> smartOutput = s.operate();

            for (int i = 0; i < dumbOutput.size(); i++) {

                    PassengerReleased passenger = dumbOutput.get(i);
                    Time timeRequested = passenger.getTimeArrived();
                    Time timeLeft = passenger.getPassengerRequest()
                            .getTimePressedButton();

                    dumbCost+=Math.abs(timeLeft.getTime() - 
                            timeRequested.getTime());
            }
            for (int i = 0; i < smartOutput.size(); i++) {

                    PassengerReleased passenger = smartOutput.get(i);
                    Time timeRequested = passenger.getTimeArrived();
                    Time timeLeft = passenger.getPassengerRequest()
                            .getTimePressedButton();

                    smartCost+=Math.abs(timeLeft.getTime() - 
                            timeRequested.getTime());
            }

            System.out.println("dumb cost (in seconds): " + dumbCost/ 1000/100+"\n"+
                    "smart cost (in seconds): " +smartCost/1000/100);
        }
    }//_________________________________________________________________main
    
}
