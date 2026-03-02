package dk.easv.bll.bot;

import dk.easv.bll.bot.IBot;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;
import dk.easv.bll.field.IField;

import java.util.List;
import java.util.Random;

public class Tictac800Bot implements IBot {

    private static final String BOTNAME = "T-800 Bot";
    private Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        String player = state.getMoveNumber() % 2 == 0 ? "0" : "1";
        String opponent = player.equals("0") ? "1" : "0";

        // 1️⃣ Prøv at vinde
        IMove winMove = findWinningMove(state, player);
        if (winMove != null) return winMove;

        // 2️⃣ Blokér modstander
        IMove blockMove = findWinningMove(state, opponent);
        if (blockMove != null) return blockMove;

        // 3️⃣ Prioriter center → hjørner → kanter
        IMove priorityMove = findPriorityMove(state);
        if (priorityMove != null) return priorityMove;

        // 4️⃣ Tilfældig træk som fallback
        List<IMove> moves = state.getField().getAvailableMoves();
        return moves.get(rand.nextInt(moves.size()));
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    // =====================
    // Hjælpe-metoder
    // =====================

    private IMove findWinningMove(IGameState state, String player) {
        List<IMove> moves = state.getField().getAvailableMoves();

        for (IMove move : moves) {
            // Lav et simpelt 3x3 microboard
            String[][] board = state.getField().getBoard();
            String[][] micro = new String[3][3];
            int startX = (move.getX() / 3) * 3;
            int startY = (move.getY() / 3) * 3;

            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    micro[i][j] = board[startX + i][startY + j];

            // Sæt markeringen midlertidigt
            micro[move.getX() % 3][move.getY() % 3] = player;

            // Tjek om det vinder microboardet
            if (checkWin3x3(micro, player)) return move;
        }
        return null;
    }

    private boolean checkWin3x3(String[][] micro, String player) {
        // Rækker og kolonner
        for (int i = 0; i < 3; i++)
            if ((micro[i][0].equals(player) && micro[i][1].equals(player) && micro[i][2].equals(player)) ||
                    (micro[0][i].equals(player) && micro[1][i].equals(player) && micro[2][i].equals(player)))
                return true;

        // Diagonaler
        if ((micro[0][0].equals(player) && micro[1][1].equals(player) && micro[2][2].equals(player)) ||
                (micro[0][2].equals(player) && micro[1][1].equals(player) && micro[2][0].equals(player)))
            return true;

        return false;
    }

    private IMove findPriorityMove(IGameState state) {
        int[][] priorities = {
                {1, 1}, // center
                {0, 0}, {0, 2}, {2, 0}, {2, 2}, // corners
                {0, 1}, {1, 0}, {1, 2}, {2, 1}  // edges
        };

        List<IMove> moves = state.getField().getAvailableMoves();
        for (int[] p : priorities) {
            for (IMove move : moves) {
                if (move.getX() % 3 == p[0] && move.getY() % 3 == p[1])
                    return move;
            }
        }
        return null;
    }
}