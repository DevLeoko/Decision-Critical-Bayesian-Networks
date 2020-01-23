package io.dcbn.backend.graph.services;

import io.dcbn.backend.graph.Graph;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

public class GraphService {

    @Getter
    private Map<Long, GraphLock> lock;


    public GraphService() {
        this.lock = new HashMap<>();
    }

    //checks if Graph is a DAG //TODO: Implement CycleSearch
    public boolean validate(Graph graph){
        return false;
    }

    //locks Graph if possible
    public void lockGraph(long graphId, long userId){
        if(lock.containsKey(graphId)){
            throw new IllegalArgumentException("Graph is being edited by another User");
        }
        else{
            //check and remove all existing locks from thee user
            for(Map.Entry<Long,GraphLock> entry : lock.entrySet()){
                if(entry.getValue().getUserId() == userId){
                    lock.remove(entry.getKey());
                }
            }
            lock.put(graphId,new GraphLock(userId));
        }
    }

    //unlocks the locked Graph
    public void unlockGraph(long graphId, long userId){
        if(lock.containsKey(graphId)){
            lock.remove(graphId);
        }
        else{
            throw new IllegalArgumentException("Graph not found");
        }
    }

    //checks if any locks have timed out and removes such locks
    @Scheduled(fixedRateString = "$graphlock.refresh.time")
    public void refreshLocks(){
        for(Map.Entry<Long,GraphLock> entry : lock.entrySet()){
            if(entry.getValue().getExpireTime() < System.currentTimeMillis()){
                lock.remove(entry.getKey());
            }
        }
    }
}
