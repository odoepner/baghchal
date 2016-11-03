package net.doepner.baghchal.model;

/**
 * Possible control pieces with their move-ability constraints
 */
public enum Piece implements MoveConstraints {

    PREY {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.getPositions().isBorderToBoard(move)
                    || gameTable.isBorderEmpty() && gameTable.isStepAlongLine(move);
        }
    },
    PREDATOR {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return gameTable.isStepAlongLine(move) || (move.isJump() && gameTable.get(move.middle()) == Piece.PREY);
        }
    },
    INVALID {
        @Override
        public boolean isValid(Move move, GameTable gameTable) {
            return false;
        }
    }

}
