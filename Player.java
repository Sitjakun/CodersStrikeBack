import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    private static boolean boostAvailable = true;
    private static boolean boost = false;
    private static boolean lapOneFinished = false;
    private static Coordinates bestCheckpointToBoost = null;
    private static final Set<Coordinates> checkpoints = new LinkedHashSet<>();
    private static final Map<Coordinates, Coordinates> optimizedCheckpointCoordinates = new HashMap<>();
    private static Coordinates nextCheckpointCoordinates;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();

            new ShipAnalysis().printDirection(x, y, nextCheckpointX, nextCheckpointY, nextCheckpointDist, nextCheckpointAngle, opponentX, opponentY);
        }
    }

    public static boolean isBoostAvailable() {
        return boostAvailable;
    }

    public static void setBoostAvailable(boolean boostAvailable) {
        Player.boostAvailable = boostAvailable;
    }

    public static boolean isLapOneFinished() {
        return lapOneFinished;
    }

    public static void setLapOneFinished(boolean lapOneFinished) {
        Player.lapOneFinished = lapOneFinished;
    }

    public static Coordinates getBestCheckpointToBoost() {
        return bestCheckpointToBoost;
    }

    public static void setBestCheckpointToBoost(Coordinates bestCheckpointToBoost) {
        Player.bestCheckpointToBoost = bestCheckpointToBoost;
    }

    public static Set<Coordinates> getCheckpoints() {
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

    public void printDirection(int x, int y, int nextCheckpointX, int nextCheckpointY, int nextCheckpointDist, int nextCheckpointAngle, int opponentX, int opponentY) {

        memorizeCheckpointsAndBestBoostOpportunity(nextCheckpointX, nextCheckpointY);

        Coordinates optimizedDirection = Player.getOptimizedCheckpointCoordinates().get(new Coordinates(nextCheckpointX, nextCheckpointY));
        if (optimizedDirection != null) {
            nextCheckpointDist = (int) Math.round(computeDist(new Coordinates(x, y), optimizedDirection));
        }

        int thrust = computeThrust(nextCheckpointAngle, nextCheckpointDist, x, y, opponentX, opponentY);

        System.err.println("Angle = " + nextCheckpointAngle);
        System.err.println("Distance = " + nextCheckpointDist);

        if (Player.isBoostAvailable() && Player.isBoost() && Math.abs(nextCheckpointAngle) < 10) {
            System.out.println(optimizedDirection.getX() + " " + optimizedDirection.getY() + " BOOST");
            Player.setBoostAvailable(false);
            Player.setBoost(false);
        } else {
            int targetX = optimizedDirection != null ? optimizedDirection.getX() : nextCheckpointX;
            int targetY = optimizedDirection != null ? optimizedDirection.getY() : nextCheckpointY;
            Coordinates target = chooseBestTarget(x, y, targetX, targetY);

            if (nextCheckpointDist < 600) {

            }

            System.out.println(target.getX() + " " + target.getY() + " " + thrust);
        }
    }

    private void memorizeCheckpointsAndBestBoostOpportunity(int nextCheckpointX, int nextCheckpointY) {

        Coordinates nextCheckpointCoord = new Coordinates(nextCheckpointX, nextCheckpointY);

        // If nextCheckpoint changed and if the lap one is not defined as finished
        if (!nextCheckpointCoord.equals(Player.getNextCheckpointCoordinates()) && !Player.isLapOneFinished()) {
            Player.setNextCheckpointCoordinates(nextCheckpointCoord);
            boolean isNewCheckpoint = Player.getCheckpoints().add(nextCheckpointCoord);
            if (!isNewCheckpoint && !Player.isLapOneFinished()) {
                System.err.println("Debug message - lap one finished - computing best boost opportunity");
                optimizeCheckpointsCoordinatesToTarget();
                Player.setLapOneFinished(true);
                double maxDist = 0;
                Coordinates bestCheckpointToBoost = Player.getOptimizedCheckpointCoordinates().get(nextCheckpointCoord);
                for (int i = 0; i < Player.getCheckpoints().size(); i++) {
                    double dist;
                    int checkpointIndice = 0;
                    if (i < Player.getCheckpoints().size() - 1) {
                        checkpointIndice = i + 1;
                    } else {
                        checkpointIndice = 0;
                    }
                    dist = computeDist(Player.getOptimizedCheckpointCoordinates().get((Coordinates) Player.getCheckpoints().toArray()[i]), Player.getOptimizedCheckpointCoordinates().get((Coordinates) Player.getCheckpoints().toArray()[checkpointIndice]));
                    if (maxDist < dist) {
                        maxDist = dist;
                        bestCheckpointToBoost = Player.getOptimizedCheckpointCoordinates().get((Coordinates) Player.getCheckpoints().toArray()[checkpointIndice]);
                    }
                }
                Player.setBestCheckpointToBoost(bestCheckpointToBoost);
            }
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
            checkpoint1 = (Coordinates) Player.getCheckpoints().toArray()[i];
            checkpoint2 = (Coordinates) Player.getCheckpoints().toArray()[checkpoint2Indice];

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

    public int computeThrust(int angle, int distance, int currX, int currY, int oppX, int oppY) {

        angle = Math.abs(angle);
        if (angle > 90) {
            return 0;
        }
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
