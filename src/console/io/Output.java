package console.io;

import com.google.inject.Singleton;

/**
 * Makes testing easier, so we can verify string output of the 
 * application. Makes dependencies clearer by not exposing
 * static methods. Allows room for future work for hooking into the 
 * output with replacement implementations/plugins/events.
 */
@Singleton
public class Output {
    public void writeLnOut(String line) {
        System.out.println(line);
    }
    
    public void writeLnErr(String line) {
        System.err.println(line);
    }
}
