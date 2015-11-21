
package ElevatorProj;

import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class RequestGenerator {
    public static Queue<PassengerRequest> RequestGenerator(int maxWeight, 
            int maxFloor, int totalPassengers, long maxTimeBetweenRequest){
        
        Queue<PassengerRequest> requests = new LinkedList<>();
        Time cTime = new Time(8,0,0);
        Random rand = new Random();
        Random randl = new Random();
        Time tpb = new Time(8,0,0);
        
        
        
        for(int i=0; i<totalPassengers; i++){
            cTime.setTime(Math.abs(randl.nextLong()%maxTimeBetweenRequest)+cTime.getTime());
            tpb.setTime(cTime.getTime());
            int from = rand.nextInt(maxFloor+1);
            int to = rand.nextInt(maxFloor);
            while(from == to) //make sure the destination floor is not the same as the starting floor
                to = rand.nextInt(maxFloor);
            int weight = rand.nextInt(maxWeight);
            PassengerRequest temp = new PassengerRequest(new Time(tpb.getTime()),from+1,to,weight);
            requests.add(temp);
           System.out.println(temp);
        }
        
//        Queue<PassengerRequest> temp = new LinkedList<>();
//        while(!requests.isEmpty()){
//            System.out.println(requests.getLast());
//            temp.add(requests.removeLast());
//        }
        return requests;
    }
    
//    public static Queue<ElevatorProj.PassengerRequest> RequestGenerator(int maxWeight, 
//            int maxFloor, int totalPassengers, long maxTimeBetweenRequest, long duration){
//        
//        Queue<ElevatorProj.PassengerRequest> requests = new LinkedList<>();
//        Time currentTime = new Time(8,0,0);
//        Random rand = new Random();
//        Time tpb = new Time(8,0,0);
//        
//        
//        
//        for(int i=0; i<totalPassengers; i++){
//            tpb.setTime(rand.nextLong()%maxTimeBetweenRequest+currentTime.getTime());
//            int from = rand.nextInt(maxFloor);
//            int to = rand.nextInt(maxFloor);
//            while(from == to) //make sure the destination floor is not the same as the starting floor
//                to = rand.nextInt(maxFloor);
//            int weight = rand.nextInt(maxWeight);
//            
//            requests.add(new PassengerRequest(tpb,from,to,weight));
//        }
//        
//        return requests;
//    }
}
