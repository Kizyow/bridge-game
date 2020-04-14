package fr.watch54.bridge.state;

public enum GameState {

    WAITING,
    STARTING,
    PLAYING,
    ENDING;

    private static GameState gameState;

    public static GameState getState(){
        return gameState;

    }

    public static void setState(GameState gameState){
        GameState.gameState = gameState;

    }

    public static boolean isState(GameState state){
        return gameState == state;

    }

}
