package regression.objectconstruction.testgeneration.example.graphcontruction;

public class BasicRules {
	public boolean checkRules(Action action, GameState state) {
		Player actor = state.player(action.getActor());
		Player target = state.player(action.getTarget());
		if (state.getGameState() == 1) {
			return false;
		} else if (actor != null && !actor.isDead()) {
			if (action.getAction() != 1 && action.getAction() != 7 && (target == null || target.isDead())) {
				return false;
			} else {
				switch (action.getAction()) {
					case 0 :
						if (actor == target) {
							return false;
						}

						if (!actor.isBoss()) {
							return false;
						}

						if (target.gangBoss() == actor) {
							return false;
						}
						break;
					case 1 :
						if (actor.isBoss()) {
							return false;
						}
						break;
					case 2 :
						if (state.getGameState() == 2) {
							return false;
						}

						if (actor == target) {
							return false;
						}

						if (!actor.isBoss()) {
							return false;
						}

						if (!target.isBoss()) {
							return false;
						}
						break;
					case 3 :
						if (actor == target) {
							return false;
						}

						if (!actor.isBoss()) {
							return false;
						}

						if (target.gangBoss() == actor) {
							return false;
						}
						break;
					case 4 :
						if (actor == target) {
							return false;
						}

						if (!target.isBoss()) {
							return false;
						}

						if (actor.gangBoss() == target) {
							return false;
						}
						break;
					case 5 :
						if (actor == target) {
							return false;
						}

						if (!target.isBoss()) {
							return false;
						}

						if (!target.isJoinOK(actor, false)) {
							return false;
						}
						break;
					case 6 :
						if (actor == target) {
							return false;
						}

						if (!actor.isBoss()) {
							return false;
						}

						if (!target.isJoinOK(actor, true)) {
							return false;
						}
					case 7 :
					default :
						break;
					case 8 :
						if (actor == target) {
							return false;
						}

						if (actor != target.boss) {
							return false;
						}
				}

				return true;
			}
		} else {
			return false;
		}
	}
}
