package ru.shipcollision.api.mechanics;

public class GameRulesHelper {

    public static final int MAX_SECONDS_TO_MOVE = 20;

    public static int getShipsCountForPlayers(int playersCount) {
        switch (playersCount) {
            case 2:
                return ShipsCountVariants.FOR_2_PLAYERS;
            case 3:
                return ShipsCountVariants.FOR_3_PLAYERS;
            case 4:
                return ShipsCountVariants.FOR_4_PLAYERS;
            default:
                return 0;
        }
    }

    public static int getFieldDimForPlayers(int playersCount) {
        switch (playersCount) {
            case 2:
                return FieldDimVariants.FOR_2_PLAYERS;
            case 3:
                return FieldDimVariants.FOR_3_PLAYERS;
            case 4:
                return FieldDimVariants.FOR_4_PLAYERS;
            default:
                return 0;
        }
    }

    private static class ShipsCountVariants {

        public static final int FOR_2_PLAYERS = 10;

        public static final int FOR_3_PLAYERS = 15;

        public static final int FOR_4_PLAYERS = 20;
    }

    private static class FieldDimVariants {

        public static final int FOR_2_PLAYERS = 10;

        public static final int FOR_3_PLAYERS = 15;

        public static final int FOR_4_PLAYERS = 20;
    }
}
