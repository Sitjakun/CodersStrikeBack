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

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int laps = in.nextInt();
        int checkpointCount = in.nextInt();
        for (int i = 0; i < checkpointCount; i++) {
            int checkpointX = in.nextInt();
            int checkpointY = in.nextInt();
            checkpoints.put(i, new Coordinates(checkpointX, checkpointY));
        }

        Map<String, Decision> podDecision = new HashMap<>();
        Map<String, Pod> podData =  new HashMap<>();

        while (true) {
            for (int i = 1; i < 3; i++) {
                int x = in.nextInt(); // x position of your pod
                int y = in.nextInt(); // y position of your pod
                int vx = in.nextInt(); // x speed of your pod
                int vy = in.nextInt(); // y speed of your pod
                int angle = in.nextInt(); // angle of your pod
                int nextCheckPointId = in.nextInt(); // next check point id of your pod
                podDecision.put("pod" + i, new ShipAnalysis().printDirection( x, y, angle, nextCheckPointId));
                podData.put("pod" + i, new Pod(new Coordinates(x, y), angle));
            }
            for (int i = 1; i < 3; i++) {
                int x2 = in.nextInt(); // x position of the opponent's pod
                int y2 = in.nextInt(); // y position of the opponent's pod
                int vx2 = in.nextInt(); // x speed of the opponent's pod
                int vy2 = in.nextInt(); // y speed of the opponent's pod
                int angle2 = in.nextInt(); // angle of the opponent's pod
                int nextCheckPointId2 = in.nextInt(); // next check point id of the opponent's pod
                podData.put("opp" + i, new Pod(new Coordinates(x2, y2), angle2));
            }

            Decision pod1Decision = podDecision.get("pod1");
            Decision pod2Decision = podDecision.get("pod2");

            new ShipAnalysis().handleCollisions(pod1Decision, pod2Decision, podData);

            System.out.println(pod1Decision.getCoord().getX() + " " + pod1Decision.getCoord().getY() + " " + pod1Decision.getThrust());
            System.out.println(pod2Decision.getCoord().getX() + " " + pod2Decision.getCoord().getY() + " " + pod2Decision.getThrust());
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

    public Decision printDirection(int x, int y, int angle, int nextCheckPointId) {

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
            Player.setBoostAvailable(false);
            Player.setBoost(false);
            return new Decision(optimizedDirection, "BOOST");
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
            return new Decision(chooseBestTarget(x, y, targetX, targetY), Integer.toString(thrust));
        }
    }

    private void memorizeCheckpointsAndBestBoostOpportunity(int nextCheckpointX, int nextCheckpointY) {

        Coordinates nextCheckpointCoord = new Coordinates(nextCheckpointX, nextCheckpointY);

        // If nextCheckpoint changed and if the lap one is not defined as finished
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

    public void handleCollisions(Decision pod1Decision, Decision pod2Decision, Map<String, Pod> podData) {
        Pod pod1 = podData.get("pod1");
        Pod pod2 = podData.get("pod2");
        Pod opp1 = podData.get("opp1");
        Pod opp2 = podData.get("opp2");

        // friendly collisions
        if(isCollision(pod1, pod2)) {
            pod1Decision.setThrust("SHIELD");
        }

        // nasty collisions
        if(isCollision(pod1, opp1) || isCollision(pod1, opp2)) {
            pod1Decision.setThrust("SHIELD");
        }
        if(isCollision(pod2, opp1) || isCollision(pod2, opp2)) {
            pod2Decision.setThrust("SHIELD");
        }
    }

    private boolean isCollision(Pod pod1, Pod pod2) {
        if (computeDist(pod1.getCoord(), pod2.getCoord()) <= 1000 && Math.abs(pod1.getAngle() - pod2.getAngle()) > 30) {
            return true;
        } else {
            return false;
        }
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

class Pod {
    Coordinates coord;
    Coordinates speed;
    int angle;
    int getNextCheckPointId;

    public Pod() {
    }

    public Pod(Coordinates coord, int angle) {
        this.coord = coord;
        this.angle = angle;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public Coordinates getSpeed() {
        return speed;
    }

    public void setSpeed(Coordinates speed) {
        this.speed = speed;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getGetNextCheckPointId() {
        return getNextCheckPointId;
    }

    public void setGetNextCheckPointId(int getNextCheckPointId) {
        this.getNextCheckPointId = getNextCheckPointId;
    }
}

class Decision {
    Coordinates coord;
    String thrust;

    public Decision(Coordinates coord, String thrust) {
        this.coord = coord;
        this.thrust = thrust;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public String getThrust() {
        return thrust;
    }

    public void setThrust(String thrust) {
        this.thrust = thrust;
    }
}
