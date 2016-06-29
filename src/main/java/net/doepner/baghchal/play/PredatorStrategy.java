package net.doepner.baghchal.play;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Levels;
import net.doepner.baghchal.model.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * The computer player's strategy (playing the tigers)
 */
public class PredatorStrategy implements Player {

    private final Levels levels;

    public PredatorStrategy(Levels levels) {
        this.levels = levels;
    }

    @Override
    public Move play(Board board) {
        final List<Move> possibleMoves = new ArrayList<>();
        board.addPossibleJumpsTo(possibleMoves, PREDATOR, PREY);
        final Move take = tryMoveFrom(possibleMoves, board);
        if (take != null) {
            return take;
        }
        board.addPossibleStepsTo(possibleMoves, PREDATOR);
        if (possibleMoves.isEmpty()) {
            return null;
        }
        final Move threateningMove = tryThreateningMove(levels.getLevel(), possibleMoves, board);
        if (threateningMove != null) {
            return threateningMove;
        }
        return tryMoveFrom(possibleMoves, board);
    }

    private static Move tryMoveFrom(List<Move> moves, Board board) {
        if (moves.isEmpty()) {
            return null;
        } else {
            final Move move = getRandomFrom(moves);
            board.movePiece(move);
            return move;
        }
    }

    private static Move getRandomFrom(List<Move> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private static Move tryThreateningMove(int level, Iterable<Move> possibleMoves, Board board) {
        final Map<Integer, List<Move>> threateningMoves = getThreateningMoves(possibleMoves, board);
        for (int i = level; i > 0; i--) {
            final Move move = tryMoveFrom(threateningMoves.get(i), board);
            if (move != null) {
                return move;
            }
        }
        return null;
    }

    private static Map<Integer, List<Move>> getThreateningMoves(Iterable<Move> possibleMoves, Board board) {
        final Map<Integer, List<Move>> threateningMoves = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            threateningMoves.put(i, new ArrayList<>());
        }
        for (Move m : possibleMoves) {
            int npt = numberOfPiecesThreatened(m, board);
            if (npt > 0) {
                threateningMoves.get(npt).add(m);
            }
        }
        return threateningMoves;
    }

    private static int numberOfPiecesThreatened(Move m, Board board) {
        final Board b = board.copyBoard();
        b.movePiece(m);
        final List<Move> jumps = new ArrayList<>();
        b.tryStepsWhere(PREDATOR, PREY, step -> b.addPossibleJump(jumps, step));
        return jumps.size();
    }
}
