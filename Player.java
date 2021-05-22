import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static boolean boostAvailable = true;
    // map memory for checkpoints for boost
    // map memory for deviation

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = Math.abs(in.nextInt()); // angle between your pod orientation and the direction of the next checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();

            int thrust = computeThrust(nextCheckpointAngle, nextCheckpointDist, x, y, opponentX, opponentY);

            if(boostAvailable && nextCheckpointAngle == 0 && nextCheckpointDist >= 6000 ) {
                System.out.println(nextCheckpointX + " " + nextCheckpointY + " BOOST");
                boostAvailable = false;
            } else {
                
                List<Integer> direction = chooseBestCoordinates(x, y, nextCheckpointX, nextCheckpointY);
                int nextX = direction.get(0);
                int nextY = direction.get(1);

                System.out.println(nextX + " " + nextY + " " + thrust);

            }
        }
    }

    public static List<Integer> chooseBestCoordinates(int x, int y, int nextCheckpointX, int nextCheckpointY) {
        List<Integer> direction = new LinkedList<>();

        if (x < nextCheckpointX){
            direction.add(nextCheckpointX + 600);
        } else {
            direction.add(nextCheckpointX - 600);
        }

        if (y < nextCheckpointY){
            direction.add(nextCheckpointY + 600);
        } else {
            direction.add(nextCheckpointY - 600);
        }

        return direction;
    }

    public static int computeThrust(int angle, int distance, int currX, int currY, int oppX, int oppY) {
        /**
       if (Math.sqrt(Math.pow(currX - oppX, 2) + Math.pow(currY - oppY, 2)) < 1000) {
            return 100;
        }
        int threshold = 15;
        if (angle > threshold) {
            if (distance > 1000) {
                return  Math.abs(100/(threshold-angle));
            } else return Math.abs(threshold/(threshold-angle));
        } else return 100;
             */ 
        if (angle > 90) {
            return 0;
        }
        if (distance < 600) {
            return 75;
        }
        return 100;
    }
}
