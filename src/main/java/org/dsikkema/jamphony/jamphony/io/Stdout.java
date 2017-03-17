package org.dsikkema.jamphony.jamphony.io;

import com.google.inject.Singleton;

/**
 * Makes testing easier, so we can verify string output of the 
 * application. Makes dependencies clearer, showing which classes
 * talk to stdout. Allows room in the future for hooking into the 
 * output with replacement implementations/plugins/events.
 */
@Singleton
public class Stdout {
    public void writeLnOut(String line) {
        System.out.println(line);
    }
    
    public void writeLnErr(String line) {
        System.err.println(line);
    }
}
