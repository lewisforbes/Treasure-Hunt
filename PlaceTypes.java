import java.util.Random;

public enum PlaceTypes {
    PUB,
    BAR,
    RESTAURANT,
    PARK;

    private static final PlaceTypes[] VALUES = values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM = new Random();

    public static PlaceTypes getRandomPlaceType() {
        return VALUES[RANDOM.nextInt(SIZE)];
    }
}