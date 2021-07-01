/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package means;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author MY PC
 */
public class EventCollector {
    public int lastKey;
    public long lastKeyReleaseTime;
    // Whether a key has been pressed before the previous key is released. 
    private boolean overlap;
    private int lastKeyBackup; // helps handle overlap
    //
    private int count;
    private long start;
    private final ConcurrentHashMap<Integer,Long>timeReleased;
   
    // Temporary map of currently pressed keys and the time they were pressed. 
    private final ConcurrentHashMap<Integer, Long> pressedKeys;
   

    // Total durations which each of the keys has been pressed.
    private final ConcurrentHashMap<Integer, Long> totalKeyDurations;
    // How many samples were counted, so we can get an average later. 
    private final ConcurrentHashMap<Integer, Integer> totalKeyCounts;
    
     public EventCollector() {
        lastKey = -1;
        lastKeyReleaseTime = -1;
        overlap = false;
        lastKeyBackup = -1;
        pressedKeys = new ConcurrentHashMap<>();
        totalKeyDurations = new ConcurrentHashMap<>();
        totalKeyCounts = new ConcurrentHashMap<>();
        //
        count=0;
        start=0;
        timeReleased=new ConcurrentHashMap<>();
    }
     /**
     * Notifies that a key has been pressed.
     *
     * @param keyEvent
     * @param isError
     */
    public void press(java.awt.event.KeyEvent keyEvent, boolean isError) {
        Long time = System.currentTimeMillis();
        int code = keyEvent.getKeyCode();

        if (!isError) {
            // A key must be released to be pressed again; So,
            // pressedKeys will never have key code. 
            pressedKeys.put(code, time);
           //
          // System.out.println("Pressed Keys "+pressedKeys);//list of pressed keys with pressed time
           count=count+1;
           if(count==1)
           {
               start=pressedKeys.get(code);
           }

           System.out.println("First press key time "+start);
            // not the first ever key press
            // neither there is an overlap
            if (lastKey != -1 && !pressedKeys.contains(lastKey)) {
                long duration = time - lastKeyReleaseTime;
               

               if (lastKey != 1) {
                lastKeyBackup = lastKey; //we're going to change it
                overlap = true; // let it happen when the key is released
            }

            // set the last pressed key
            lastKey = code;
        }

         // lastKey=Number.LongValue(lastKey);
    }
    }

     long totalDwellTime=0;
    
    /**
     * Notifies that a key has been released.
     *
     * @param keyEvent
     * @param isError
     */
    public void release(java.awt.event.KeyEvent keyEvent, boolean isError) {
        Long time = System.currentTimeMillis();//System.nanoTime();
        int code = keyEvent.getKeyCode();
        long duration= time - pressedKeys.get(code);
        
        if (!isError) {
            timeReleased.put(code,time);
           // System.out.println("Released Keys "+timeReleased);//list of released keys with pressed time
            // A key must be pressed in order to be released; so, 
            // the key code is guaranteed to exist. 
           
            // System.out.print(keyEvent.getKeyChar() + " pressed for " + duration + ", ");
            if (totalKeyCounts.containsKey(code)) {
                totalKeyCounts.put(code, totalKeyCounts.get(code) + 1);
                totalKeyDurations.put(code, duration + totalKeyDurations.get(code));
            } else {
                totalKeyCounts.put(code, 1);
                totalKeyDurations.put(code, duration);
            }
            
            lastKeyReleaseTime = time;
            
            //
           System.out.print("Released time: "+time+"\t"+ keyEvent.getKeyChar() + " pressed for " 
                   + duration + ", " + "\tCode: "+code +  "\tCounts="+count);
        
      
         totalDwellTime+=totalKeyDurations.get(code);
           System.out.println("\tTotal DwellTime "+totalDwellTime);
           
            System.out.println();
             
       
        long pressTime=0;
        long totalTime=0;
        long end=0;
        
        pressTime=time - duration;
       
        
        if(count!=1)
        {
            System.out.println("press start time is "+pressTime);
            end=time;
            System.out.println("last key release time is "+end);
            totalTime=end - start;
            System.out.println("Total time is "+totalTime);
            long flightTime=totalTime-totalDwellTime;
            System.out.println("Flight Time is "+flightTime);
            
            double mean=flightTime/(count-1);
            System.out.println("Mean value is "+mean);    
        }
         
            
           
         pressedKeys.remove(code);
   
        } 

    }
    
}
