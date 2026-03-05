package dk.easv.bll.bot;

import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.List;
import java.util.Random;

public class Tictac800Bot implements IBot {

    private static final String BOTNAME = "T-800";
    private Random rand = new Random();
    int value = 0;

    @Override
    public IMove doMove(IGameState state) {
        String player = state.getMoveNumber() % 2 == 0 ? "0" : "1";
        String opponent = player.equals("0") ? "1" : "0";


        if (state.getMoveNumber() == 0){
            value = 0;
        }


        boolean isBotPlayer1 = (state.getMoveNumber() % 2 == 0);
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        if (state.getMoveNumber() == 0 && isBotPlayer1){
            IMove middleMove = new Move(4, 4);
            middleMove = middleStartMove(state,middleMove);
            if (middleMove != null){
                return middleMove;
            }
        }
        //Vi starter med at se om der er et winning move for botten
        IMove winningMove = findWinningMove(state, player);
        if (winningMove != null) {
            return winningMove;
        }
        //Hvis der ikke er et winning move for botten, så kigger den efter et winning move for modstanderen og blokerer
        IMove blockMove = findWinningMove(state, opponent);
        if (blockMove != null) {
            return blockMove;
        }
        //Hvis der ikke er winning moves, så laver den et random move
        List<IMove> moves = state.getField().getAvailableMoves();
        return moves.get(rand.nextInt(moves.size()));
    }


    private IMove findWinningMove(IGameState state, String player) {
        List<IMove> moves = state.getField().getAvailableMoves();
        String[][] board = state.getField().getBoard();

        for (IMove move : moves) {
            //Laver en kopi af 3x3 boardet
            String[][] micro = getMicroboard(board, move.getX(), move.getY());

            //Laver en simulering af at playeren spiller her
            micro[move.getX() % 3][move.getY() % 3] = player;

            if (checkWin3x3(micro, player)) {
                //Hvis der findes et winning move
                return move;
            }
        }

        return null;
    }

        //Vi kigger efter koordinaten på hele brættet
    private String[][] getMicroboard(String[][] board, int x, int y) {
        //Så laves der et nyt, tomt 3x3 array
        String[][] micro = new String[3][3];


        int startX = 0;
        int startY = 0;

        /*
        Så finder vi ud af, hvilken række og hvilken kolonne vi kigger efter, når vi har vores koordiater
        for det move der er blevet valgt, så det kan placeres korrekt i det simulerede board
        */

        //Start X position
        if (x <= 2) startX = 0;
        else if (x <= 5) startX = 3;
        else startX = 6;

        // Start Y position
        if (y <= 2) startY = 0;
        else if (y <= 5) startY = 3;
        else startY = 6;

        //Kopier miniboard
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                micro[i][j] = board[startX + i][startY + j];
            }
        }

        return micro;
    }

    //Vi kigger efter et winning move i 3x3 miniboardet
    private boolean checkWin3x3(String[][] micro, String player) {
        //Rækker og kolonner
        for (int i = 0; i < 3; i++) {
            if ((micro[i][0].equals(player) && micro[i][1].equals(player) && micro[i][2].equals(player)) ||
                    (micro[0][i].equals(player) && micro[1][i].equals(player) && micro[2][i].equals(player))) {
                return true;
            }
        }

        //Diagonaler
        if ((micro[0][0].equals(player) && micro[1][1].equals(player) && micro[2][2].equals(player)) ||
                (micro[0][2].equals(player) && micro[1][1].equals(player) && micro[2][0].equals(player))) {
            return true;
        }

        return false;
    }


    public IMove middleStartMove(IGameState state, IMove middleMove ) {
        if (value == 0) {
            value = 1;
            return middleMove;
        }
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }
}