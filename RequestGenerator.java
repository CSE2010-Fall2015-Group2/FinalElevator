
package elevatorproj;

import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class RequestGenerator {
    public Queue<ElevatorProj.PassengerRequest> RequestGenerator(int maxWeight, 
            int maxFloor, int totalPassengers, long maxTimeBetweenRequest){
        
        Queue<ElevatorProj.PassengerRequest> requests = new LinkedList<>();
        Time currentTime = new Time(8,0,0);
        Random rand = new Random();
        Time requestTime = new Time(8,0,0);
        
        
        
        for(int i=0; i<totalPassengers; i++){
            requestTime.setTime(rand.nextLong()%maxTimeBetweenRequest+currentTime.getTime());
            int from = rand.nextInt(maxFloor);
            int to = rand.nextInt(maxFloor);
            while(from == to) //make sure the destination floor is not the same as the starting 
                to = rand.nextInt(maxFloor);
            int weight = rand.nextInt(maxWeight);
            
            requests.add(new PassengerRequest(requestTime,from,to,weight));
        }
        
        return requests;
    }
}
