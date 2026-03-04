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




        // Prøv at finde et træk, der vinder mikroboardet
        IMove winningMove = findWinningMove(state, player);
        if (winningMove != null) {
            return winningMove;
        }

        // Forsøger at blokere modstanderen fra et winning move
        IMove blockMove = findWinningMove(state, opponent);
        if (blockMove != null) {
            return blockMove;
        }

        // Ellers spil tilfældigt
        List<IMove> moves = state.getField().getAvailableMoves();
        return moves.get(rand.nextInt(moves.size()));
    }

    // Find et træk, der kan give sejr i mikroboardet
    private IMove findWinningMove(IGameState state, String player) {
        List<IMove> moves = state.getField().getAvailableMoves();
        String[][] board = state.getField().getBoard();

        for (IMove move : moves) {
            // Kopier mikroboardet
            String[][] micro = getMicroboard(board, move.getX(), move.getY());

            // Simuler at player spiller her
            micro[move.getX() % 3][move.getY() % 3] = player;

            if (checkWin3x3(micro, player)) {
                return move; // Vi fandt et vindende træk
            }
        }

        return null; // Ingen vindende træk
    }

    // Find miniboard uden at bruge /3*3
    private String[][] getMicroboard(String[][] board, int x, int y) {
        String[][] micro = new String[3][3];

        int startX = 0;
        int startY = 0;

        // Start X
        if (x <= 2) startX = 0;
        else if (x <= 5) startX = 3;
        else startX = 6;

        // Start Y
        if (y <= 2) startY = 0;
        else if (y <= 5) startY = 3;
        else startY = 6;

        // Kopier miniboard
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                micro[i][j] = board[startX + i][startY + j];
            }
        }

        return micro;
    }

    // Tjek for winning move i 3x3 miniboard
    private boolean checkWin3x3(String[][] micro, String player) {
        // Tjek rækker og kolonner
        for (int i = 0; i < 3; i++) {
            if ((micro[i][0].equals(player) && micro[i][1].equals(player) && micro[i][2].equals(player)) ||
                    (micro[0][i].equals(player) && micro[1][i].equals(player) && micro[2][i].equals(player))) {
                return true;
            }
        }

        // Tjek diagonaler
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