package org.firstinspires.ftc.teamcode;

/**
 * Created by naapp on 11/24/2016.
 */

public class ClassFinder {

    //used to see what class called the method init() in the Hardware Class (can be applied in other cases too)
    //using this method allows Hardware to be better organized and have less repeated code (due to only wanting to initialize certain motors for testing specific functions of the robot
    //Thanks to Denys Seguret and Jared Rummler for the method
    public static String getCallingClassName(){
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ClassFinder.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
                if (callerClassName==null)//gets class that called this method and sets it equal to callerClassName - AP
                    callerClassName = ste.getClassName();
                else if (!callerClassName.equals(ste.getClassName())) //if the class name in the specific position of "ste" isn't equal to the class calling this method or this class's name (ClassFinder), it returns that class as a String -AP
            return ste.getClassName();
        }
        }
        return null;
    }
}
