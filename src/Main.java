import java.util.Random;

public class Main {

	private static final Random PRNG = new Random();

	private static long numberOfSpins = 10_000_000;

	private static int stops[] = new int[5];

	private static char view[][] = new char[5][5];

	private static int freeGames = 0;

	private static long numberOfBaseGames = 0L;

	private static long numberOfBaseFreeGames = 0L;

	private static long numberOfFreeFreeGames = 0L;

	private static long baseBonusCount = 0L;

	private static long freeBonusCount = 0L;

	private static long baseFreeActivationCount = 0L;

	private static long freeFreeActivationCount = 0L;

	private static long baseRegularWon = 0L;

	private static long baseBonusWon = 0L;

	private static long baseLost = 0L;

	private static long freeRegularWon = 0L;

	private static long freeBonusWon = 0L;

	private static long scatterBaseWon = 0L;

	private static long scatterFreeWon = 0L;

	private static long totalWon = 0L;

	private static long totalLost = 0L;

	private static char payouts[][] = { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 },
			{ 100, 5, 10, 50, 100, 5 }, { 250, 10, 50, 100, 150, 10 }, { 500, 50, 100, 200, 250, 50 }, };

	private static char strips[][] = {
			{ 'B', 'C', 'A', 'C', 'A', 'W', 'A', 'A', 'C', 'A', 'D', 'C', 'B', 'B', 'S', 'C', 'A', 'C', 'C', 'B', 'A',
					'D', 'B', 'C', 'D', 'B', 'C', 'A', 'B', 'A', 'B', },
			{ 'A', 'W', 'B', 'A', 'B', 'A', 'D', 'C', 'B', 'S', 'C', 'C', 'A', 'B', 'A', 'A', 'A', 'A', 'D', 'A', 'C',
					'B', 'D', 'B', 'B', 'A', 'C', 'A', 'A', 'A', 'A', },
			{ 'D', 'B', 'A', 'B', 'D', 'B', 'D', 'A', 'B', 'C', 'S', 'A', 'D', 'B', 'A', 'W', 'B', 'A', 'D', 'A', 'A',
					'A', 'D', 'B', 'A', 'C', 'A', 'C', 'D', 'D', 'D', },
			{ 'D', 'A', 'A', 'B', 'D', 'B', 'C', 'B', 'A', 'D', 'A', 'A', 'D', 'D', 'W', 'A', 'D', 'B', 'C', 'B', 'C',
					'B', 'C', 'B', 'S', 'C', 'A', 'D', 'A', 'A', 'D', },
			{ 'C', 'A', 'A', 'W', 'A', 'A', 'B', 'A', 'C', 'B', 'S', 'A', 'A', 'C', 'C', 'B', 'B', 'B', 'D', 'B', 'B',
					'A', 'D', 'A', 'C', 'C', 'D', 'B', 'D', 'A', 'B', }, };

	private static void spin() {
		for (int i = 0; i < view.length && i < strips.length; i++) {
			int r = PRNG.nextInt(strips[i].length);
			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = strips[i][(r + j) % strips[i].length];
			}
		}
	}

	private static void print() {
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				System.out.print(view[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	private static long wildLineWin(int index) {
		int count = 0;
		for (int i = 0; i < view.length; i++) {
			if (view[i][index] == 'W') {
				count++;
			}
		}

		return payouts[count][0];
	}

	private static long lineWin(int index) {
		char symbol = view[0][index];

		long win = 0;
		if (symbol == 'W') {
			win = wildLineWin(index);
			for (int i = 0; i < view.length; i++) {
				if (view[i][index] != 'W') {
					symbol = view[i][index];
					break;
				}
			}
		}

		if (symbol == 'W') {
			return win;
		}

		int count = 0;
		for (int i = 0; i < view.length; i++) {
			if (view[i][index] == symbol) {
				count++;
			} else if (view[i][index] == 'W') {
				count++;
			} else {
				break;
			}
		}

		switch (symbol) {
		case 'W':
			return Math.max(payouts[count][0], win);
		case 'A':
			return Math.max(payouts[count][1], win);
		case 'B':
			return Math.max(payouts[count][2], win);
		case 'C':
			return Math.max(payouts[count][3], win);
		case 'D':
			return Math.max(payouts[count][4], win);
		}

		return 0;
	}

	private static long linesWin() {
		long result = 0;

		for (int j = 0; j < 5; j++) {
			result += lineWin(j);
		}

		return result;
	}

	private static long scatterWin() {
		int count = 0;

		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (view[i][j] == 'S') {
					count++;
				}
			}
		}

		return payouts[count][5];
	}

	private static int numberOfFreeGames() {
		int count = 0;

		for (int i = 0; i < view.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				if (view[i][j] == 'S') {
					count++;
				}
			}
		}

		switch (count) {
		case 3:
			return 5;
		case 4:
			return 10;
		case 5:
			return 20;
		}

		return 0;
	}

	private static boolean hasX() {
		char symbol = view[0][0];

		for (int i = 0; i < view.length && i < view[i].length; i++) {
			if (view[i][i] != symbol) {
				return false;
			}
			if (view[i][view.length - i - 1] != symbol) {
				return false;
			}
		}

		return true;
	}

	private static void repace() {
		char symbol = view[0][0];

		switch (view[0][0]) {
		case 'A':
			symbol = 'B';
			break;
		case 'B':
			symbol = 'C';
			break;
		case 'C':
			symbol = 'D';
			break;
		case 'D':
			symbol = 'W';
			break;
		}

		for (int i = 0; i < view.length && i < view[i].length; i++) {
			view[i][i] = symbol;
			view[i][view.length - i - 1] = symbol;
		}
	}

	private static void initStatistics() {
		numberOfBaseGames = 0L;
		numberOfBaseFreeGames = 0L;
		numberOfFreeFreeGames = 0L;
		baseBonusCount = 0L;
		freeBonusCount = 0L;
		baseFreeActivationCount = 0L;
		freeFreeActivationCount = 0L;
		baseRegularWon = 0L;
		baseBonusWon = 0L;
		baseLost = 0L;
		freeRegularWon = 0L;
		freeBonusWon = 0L;
		scatterBaseWon = 0L;
		scatterFreeWon = 0L;
		totalWon = 0L;
		totalLost = 0L;
	}

	private static void printStatistics() {
		System.out.print("Bonus games in base game:\t");
		System.out.println(100D * (double) baseBonusCount / numberOfBaseGames);

		System.out.print("Bonus gaems in free games:\t");
		System.out.println(100D * (double) freeBonusCount / (numberOfBaseFreeGames + numberOfFreeFreeGames));

		System.out.print("Free games frequency in base game:\t");
		System.out.println(100D * (double) baseFreeActivationCount / numberOfBaseGames);

		System.out.print("Free games frequency in free games:\t");
		System.out.println(100D * (double) freeFreeActivationCount / numberOfBaseFreeGames);

		System.out.print("Won by regular lines in base game:\t");
		System.out.println(100D * (double) baseRegularWon / totalLost);

		System.out.print("Won by bonus lines in base game:\t");
		System.out.println(100D * (double) baseBonusWon / totalLost);

		System.out.print("Won by regular lines in free games:\t");
		System.out.println(100D * (double) freeRegularWon / totalLost);

		System.out.print("Won by bonus lines in free games:\t");
		System.out.println(100D * (double) freeBonusWon / totalLost);

		System.out.print("Won by scatters in base game:\t");
		System.out.println(100D * (double) scatterBaseWon / totalLost);

		System.out.print("Won by scatters in free games:\t");
		System.out.println(100D * (double) scatterFreeWon / totalLost);

		System.out.print("Total RTP:\t");
		System.out.println(100D * (double) totalWon / totalLost);
	}

	private static void monteCarlo() {
		initStatistics();

		long win1 = 0;
		long win2 = 0;
		for (long g = 0; g < numberOfSpins; g++) {
			numberOfBaseGames++;
			baseLost += 5;
			totalLost += 5;
			spin();
			win1 = linesWin();
			win2 = scatterWin();
			baseRegularWon += win1;
			scatterBaseWon += 5 * win2;
			totalWon += win1 + 5 * win2;

			/*
			 * Check for free games.
			 */
			freeGames = numberOfFreeGames();
			if (freeGames > 0) {
				numberOfBaseFreeGames += freeGames;
				baseFreeActivationCount++;
			}

			/*
			 * Handle X bonus.
			 */
			if (hasX() == true) {
				/*
				 * Scatter win is counted only once and no extra free spins
				 * added.
				 */
				repace();
				win1 = linesWin();
				baseBonusWon += win1;
				totalWon += win1;
				baseBonusCount++;
			}

			/*
			 * Free games mode.
			 */
			while (freeGames > 0) {
				spin();
				win1 = linesWin();
				win2 = scatterWin();
				freeRegularWon += win1;
				scatterFreeWon += 5 * win2;
				totalWon += win1 + 5 * win2;

				freeGames--;
				int count = numberOfFreeGames();
				if (count > 0) {
					numberOfFreeFreeGames += count;
					freeFreeActivationCount++;
				}
				freeGames += count;

				if (hasX() == true) {
					/*
					 * Scatter win is counted only once and no extra free spins
					 * added.
					 */
					repace();
					win1 = linesWin();
					freeBonusWon += win1;
					totalWon += win1;
					freeBonusCount++;
				}
			}
		}

		printStatistics();
	}

	private static void nextScreen() {
		for (int i = 0; i < view.length && i < strips.length; i++) {
			for (int j = 0; j < view[i].length; j++) {
				view[i][j] = strips[i][(stops[i] + j) % strips[i].length];
			}
		}
	}

	private static void nextCombination() {
		stops[stops.length - 1]++;
		for (int i = stops.length - 1; i > 0; i--) {
			if (stops[i] > strips[i].length) {
				stops[i - 1]++;
				stops[i] = 0;
			}
		}
	}

	private static void bruteForce() {
		initStatistics();

		stops = new int[] { 0, 0, 0, 0, 0 };

		long numberOfCombinations = 1;
		for (int i = 0; i < strips.length; i++) {
			numberOfCombinations *= strips[i].length;
		}

		long win1 = 0;
		long win2 = 0;
		for (long g = 0; g < numberOfCombinations; g++) {
			nextScreen();

			numberOfBaseGames++;
			baseLost += 5;
			totalLost += 5;
			win1 = linesWin();
			win2 = scatterWin();
			baseRegularWon += win1;
			scatterBaseWon += 5 * win2;
			totalWon += win1 + 5 * win2;

			/*
			 * Check for free games.
			 */
			freeGames = numberOfFreeGames();
			if (freeGames > 0) {
				numberOfBaseFreeGames += freeGames;
				baseFreeActivationCount++;
			}

			if (hasX() == true) {
				/*
				 * Scatter win is counted only once and no extra free spins
				 * added.
				 */
				repace();
				win1 = linesWin();
				baseBonusWon += win1;
				totalWon += win1;
				baseBonusCount++;
			}

			nextCombination();
		}

		printStatistics();
	}

	public static void main(String[] args) {
		System.out.println("=== Monte Carlo ===");
		monteCarlo();
		System.out.println("=== Brute Force ===");
		bruteForce();
	}
}
