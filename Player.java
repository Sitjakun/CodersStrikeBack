import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static boolean boostAvailable = true;
    private static boolean boost = false;
    private static boolean optimizeCoordinates = true;
    private static Coordinates bestCheckpointToBoost = null;
    private static final Map<Integer, Coordinates> checkpoints = new HashMap<>();
    private static final Map<Coordinates, Coordinates> optimizedCheckpointCoordinates = new HashMap<>();
    private static Coordinates nextCheckpointCoordinates;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int laps = in.nextInt();
        int checkpointCount = in.nextInt();
        for (int i = 0; i < checkpointCount; i++) {
            int checkpointX = in.nextInt();
            int checkpointY = in.nextInt();
            checkpoints.put(i, new Coordinates(checkpointX, checkpointY));
        }

        while (true) {
            for (int i = 0; i < 2; i++) {
                int x = in.nextInt(); // x position of your pod
                int y = in.nextInt(); // y position of your pod
                int vx = in.nextInt(); // x speed of your pod
                int vy = in.nextInt(); // y speed of your pod
                int angle = in.nextInt(); // angle of your pod
                int nextCheckPointId = in.nextInt(); // next check point id of your pod
                new ShipAnalysis().printDirection(x, y, angle, nextCheckPointId);
            }
            for (int i = 0; i < 2; i++) {
                int x2 = in.nextInt(); // x position of the opponent's pod
                int y2 = in.nextInt(); // y position of the opponent's pod
                int vx2 = in.nextInt(); // x speed of the opponent's pod
                int vy2 = in.nextInt(); // y speed of the opponent's pod
                int angle2 = in.nextInt(); // angle of the opponent's pod
                int nextCheckPointId2 = in.nextInt(); // next check point id of the opponent's pod
            }
            // double syso ?
        }
    }

    public static boolean isBoostAvailable() {
        return boostAvailable;
    }

    public static void setBoostAvailable(boolean boostAvailable) {
        Player.boostAvailable = boostAvailable;
    }

    public static boolean isOptimizeCoordinates() {
        return optimizeCoordinates;
    }

    public static void setOptimizeCoordinates(boolean optimizeCoordinates) {
        Player.optimizeCoordinates = optimizeCoordinates;
    }

    public static Coordinates getBestCheckpointToBoost() {
        return bestCheckpointToBoost;
    }

    public static void setBestCheckpointToBoost(Coordinates bestCheckpointToBoost) {
        Player.bestCheckpointToBoost = bestCheckpointToBoost;
    }

    public static Map<Integer, Coordinates> getCheckpoints() {
        return checkpoints;
    }

    public static Coordinates getNextCheckpointCoordinates() {
        return nextCheckpointCoordinates;
    }

    public static void setNextCheckpointCoordinates(Coordinates checkpointCoordinates) {
        Player.nextCheckpointCoordinates = checkpointCoordinates;
    }

    public static boolean isBoost() {
        return boost;
    }

    public static void setBoost(boolean boost) {
        Player.boost = boost;
    }

    public static Map<Coordinates, Coordinates> getOptimizedCheckpointCoordinates() {
        return optimizedCheckpointCoordinates;
    }
}

class ShipAnalysis {

    public void printDirection(int x, int y, int angle, int nextCheckPointId) {

        Coordinates nextCheckpoint = Player.getCheckpoints().get(nextCheckPointId);
        int nextCheckpointX = nextCheckpoint.getX();
        int nextCheckpointY = nextCheckpoint.getY();

        memorizeCheckpointsAndBestBoostOpportunity(nextCheckpointX, nextCheckpointY);

        Coordinates optimizedDirection = Player.getOptimizedCheckpointCoordinates().get(nextCheckpoint);
        int nextCheckpointDist;
        nextCheckpointDist = (int) Math.round(computeDist(new Coordinates(x, y), optimizedDirection));

        int thrust = computeThrust(angle, nextCheckpointDist, x, y);

        System.err.println("Angle = " + angle);
        System.err.println("Distance = " + nextCheckpointDist);

        if (Player.isBoostAvailable() && Player.isBoost() ) { // todo angle a prendre en compte
            System.out.println(optimizedDirection.getX() + " " + optimizedDirection.getY() + " BOOST");
            Player.setBoostAvailable(false);
            Player.setBoost(false);
        } else {
            int targetX = optimizedDirection.getX();
            int targetY = optimizedDirection.getY();

            if (nextCheckpointDist < 1000) {
                int mapSize = Player.getCheckpoints().size();
                for (int i = 0; i < mapSize; i++) {
                    if ((Player.getCheckpoints().get(i)).equals(new Coordinates(nextCheckpointX, nextCheckpointY))) {
                        int ind;
                        if (i == mapSize - 1) {
                            ind = 0;
                        } else {
                            ind = i + 1;
                        }

                        optimizedDirection = Player.getOptimizedCheckpointCoordinates().get(Player.getCheckpoints().get(ind));
                        targetX = optimizedDirection.getX();
                        targetY = optimizedDirection.getY();
                        break;
                    }
                }
            }
            Coordinates target = chooseBestTarget(x, y, targetX, targetY);
            System.out.println(target.getX() + " " + target.getY() + " " + thrust);
        }
    }

    private void memorizeCheckpointsAndBestBoostOpportunity(int nextCheckpointX, int nextCheckpointY) {

        Coordinates nextCheckpointCoord = new Coordinates(nextCheckpointX, nextCheckpointY);

        // If nextCheckpoint changed and if the lap one is not defined as finished
        Player.setNextCheckpointCoordinates(nextCheckpointCoord);
        if (Player.isOptimizeCoordinates()) {
            System.err.println("Debug message - lap one finished - computing best boost opportunity");
            optimizeCheckpointsCoordinatesToTarget();
            Player.setOptimizeCoordinates(false);
            double maxDist = 0;
            Coordinates bestCheckpointToBoost = Player.getOptimizedCheckpointCoordinates().get(nextCheckpointCoord);
            for (int i = 0; i < Player.getCheckpoints().size(); i++) {
                double dist;
                int checkpointIndice;
                if (i < Player.getCheckpoints().size() - 1) {
                    checkpointIndice = i + 1;
                } else {
                    checkpointIndice = 0;
                }
                dist = computeDist(Player.getOptimizedCheckpointCoordinates().get(Player.getCheckpoints().get(i)), Player.getOptimizedCheckpointCoordinates().get(Player.getCheckpoints().get(checkpointIndice)));
                if (maxDist < dist) {
                    maxDist = dist;
                    bestCheckpointToBoost = Player.getOptimizedCheckpointCoordinates().get((Player.getCheckpoints().get(checkpointIndice)));
                }
            }
            Player.setBestCheckpointToBoost(bestCheckpointToBoost);
        }
        if (Player.isBoostAvailable() && Player.getBestCheckpointToBoost() != null && Player.getOptimizedCheckpointCoordinates().get(nextCheckpointCoord).equals(Player.getBestCheckpointToBoost())) {
            Player.setBoost(true);
        }
    }


    private void optimizeCheckpointsCoordinatesToTarget() {
        for (int i = 0; i < Player.getCheckpoints().size(); i++) {

            Coordinates checkpoint1;
            Coordinates checkpoint2;
            int checkpoint2Indice;
            if (i < Player.getCheckpoints().size() - 1) {
                checkpoint2Indice = i + 1;
            } else {
                checkpoint2Indice = 0;
            }
            checkpoint1 = Player.getCheckpoints().get(i);
            checkpoint2 = Player.getCheckpoints().get(checkpoint2Indice);

            Coordinates vector = new Coordinates(checkpoint2.getX() - checkpoint1.getX(), checkpoint2.getY() - checkpoint1.getY());

            double vectorOneX = vector.getX() / norm(vector);
            double vectorOneY = vector.getY() / norm(vector);

            vector = new Coordinates((int) Math.round(vector.getX() - vectorOneX * 600), (int) Math.round(vector.getY() - vectorOneY * 600));

            Player.getOptimizedCheckpointCoordinates().put(checkpoint2, new Coordinates(checkpoint1.getX() + vector.getX(), checkpoint1.getY() + vector.getY()));
        }
        System.err.println(Player.getOptimizedCheckpointCoordinates());
    }

    private double computeDist(Coordinates coord1, Coordinates coord2) {
        return Math.sqrt(Math.pow(coord1.getX() - coord2.getX(), 2) + Math.pow(coord1.getY() - coord2.getY(), 2));
    }

    private double norm(Coordinates vector) {
        return Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2));
    }

    public Coordinates chooseBestTarget(int x, int y, int nextCheckpointX, int nextCheckpointY) {
        Coordinates target = new Coordinates(nextCheckpointX, nextCheckpointY);
        return target;
    }

    public int computeThrust(int angle, int distance, int currX, int currY) {
        /**
        angle = Math.abs(angle);
        if (angle > 90) {
            return 0;
        }*/
        return 100;
    }
}

class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
