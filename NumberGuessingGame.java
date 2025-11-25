import java.util.*;

public class NumberGuessingGame {

    // Colors removed for CodeSoft compatibility
    private static final String RESET = "";
    private static final String GREEN = "";
    private static final String RED = "";
    private static final String YELLOW = "";
    private static final String CYAN = "";
    private static final String PURPLE = "";

    // Logic Puzzle class
    static class LogicPuzzle {
        String question;
        int answer;
        String logic;

        public LogicPuzzle(String question, int answer, String logic) {
            this.question = question;
            this.answer = answer;
            this.logic = logic;
        }
    }

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<LogicPuzzle> puzzleBank = new ArrayList<>();
    private static final Random rand = new Random();

    private static int totalScore = 0; // Total score across rounds

    public static void main(String[] args) {
        loadPuzzles();
        System.out.println("=============================================");
        System.out.println("🚀 WELCOME TO SMART NUMBER GUESSING GAME");
        System.out.println("=============================================");
        System.out.println("📝 Guess the number from 1 to 100");
        System.out.println("🧠 Solve puzzles to unlock smart hints!");

        boolean playAgain = true;
        while (playAgain) {
            playRound();

            System.out.print("\n🔁 Do you want to play again? (yes/no): ");
            String resp = sc.next();
            playAgain = resp.equalsIgnoreCase("yes") || resp.equalsIgnoreCase("y");
        }

        System.out.println("\n🎯 Your Total Score Across Rounds: " + totalScore);
        System.out.println("👋 Thanks for playing — Smart Number Guessing Game!");
    }

    // MAIN GAME ROUND
    private static void playRound() {
        int secret = rand.nextInt(100) + 1;
        int maxAttempts = 8;
        int attempts = 0;
        boolean won = false;

        System.out.println("\n---------------------------------------------");
        System.out.println("🎲 NEW ROUND STARTED! (Number is between 1 - 100)");
        System.out.println("---------------------------------------------");

        ArrayList<LogicPuzzle> usedPuzzles = new ArrayList<>(); // To avoid repeats

        while (attempts < maxAttempts) {
            System.out.print("\n👉 Attempt " + (attempts + 1) + "/" + maxAttempts + " | Enter guess: ");
            int guess = getValidInput();
            attempts++;

            if (guess == secret) {
                System.out.println("\n🎉 CORRECT! You guessed the number: " + secret);
                int score = (maxAttempts - attempts + 1) * 100;
                totalScore += score; // add to total
                System.out.println("🏆 Your Score: " + score);
                System.out.println("💯 Total Score: " + totalScore);
                won = true;
                break;
            } else if (guess < secret) {
                System.out.println("📉 Too Low!");
            } else {
                System.out.println("📈 Too High!");
            }

            // Hints via puzzles
            if (attempts == 2) {
                System.out.println("\n🔓 PUZZLE UNLOCKED (Hint: Even/Odd)");
                askRandomPuzzle(secret, 1, usedPuzzles);
            } else if (attempts == 5) {
                System.out.println("\n🔓 SUPER PUZZLE UNLOCKED (Hint: >50 or ≤50)");
                askRandomPuzzle(secret, 2, usedPuzzles);
            }
        }

        if (!won) {
            showGameOver();
            System.out.println("🔢 The correct number was: " + secret);
        }
    }

    // PUZZLE HANDLER
    private static void askRandomPuzzle(int secret, int hintType, ArrayList<LogicPuzzle> usedPuzzles) {
        ArrayList<LogicPuzzle> availablePuzzles = new ArrayList<>(puzzleBank);
        availablePuzzles.removeAll(usedPuzzles);

        if (availablePuzzles.isEmpty()) return; // safety

        LogicPuzzle p = availablePuzzles.get(rand.nextInt(availablePuzzles.size()));
        usedPuzzles.add(p); // mark as used

        System.out.println("\n🧠 PUZZLE: " + p.question);
        System.out.print("Your Answer: ");
        int ans = getValidInput();

        if (ans == p.answer) {
            System.out.println("✅ Correct! (" + p.logic + ")");
            System.out.print("💡 HINT: ");
            if (hintType == 1)
                System.out.println(secret % 2 == 0 ? "The number is EVEN" : "The number is ODD");
            else
                System.out.println(secret > 50 ? "The number is GREATER than 50" : "The number is 50 or LESS");
        } else {
            System.out.println("❌ Wrong! Answer was: " + p.answer);
            System.out.println("🔒 Hint Locked!");
        }
    }

    // INPUT VALIDATION
    private static int getValidInput() {
        while (!sc.hasNextInt()) {
            System.out.print("⚠ Enter a valid number: ");
            sc.next();
        }
        return sc.nextInt();
    }

    // LOAD PUZZLES
    private static void loadPuzzles() {
        puzzleBank.add(new LogicPuzzle("2, 4, 8, 16... Next?", 32, "Powers of 2"));
        puzzleBank.add(new LogicPuzzle("1, 4, 9, 16... Next?", 25, "Squares"));
        puzzleBank.add(new LogicPuzzle("3, 6, 9, 12... Next?", 15, "Multiples of 3"));
        puzzleBank.add(new LogicPuzzle("100, 90, 80, 70... Next?", 60, "Subtracting 10"));
        puzzleBank.add(new LogicPuzzle("Fibonacci: 1,1,2,3,5,8... Next?", 13, "Fibonacci Series"));
        puzzleBank.add(new LogicPuzzle("Prime: 2,3,5,7,11... Next?", 13, "Prime Numbers"));
        puzzleBank.add(new LogicPuzzle("If 5+3=28, then 9+1=?", 810, "Diff & Sum Trick"));
        puzzleBank.add(new LogicPuzzle("Solve: 7*8 - 6", 50, "BODMAS"));
        puzzleBank.add(new LogicPuzzle("Solve: 3x + 5 = 20", 5, "Algebra"));
        puzzleBank.add(new LogicPuzzle("Half of 2 + 2 = ?", 3, "Trick Question"));
        puzzleBank.add(new LogicPuzzle("How many sides in a Hexagon?", 6, "Geometry"));
        puzzleBank.add(new LogicPuzzle("Cube root of 125?", 5, "Math Fact"));
        puzzleBank.add(new LogicPuzzle("Square root of 144?", 12, "Math Fact"));
        puzzleBank.add(new LogicPuzzle("A=1, B=2... Z=?", 26, "Alphabet Positions"));
        puzzleBank.add(new LogicPuzzle("2^5 =", 32, "Exponents"));
        puzzleBank.add(new LogicPuzzle("Hours in 2 days?", 48, "24×2"));
        puzzleBank.add(new LogicPuzzle("Cricket team players?", 11, "General Knowledge"));
        puzzleBank.add(new LogicPuzzle("Binary 111 = ?", 7, "Binary Conversion"));
        puzzleBank.add(new LogicPuzzle("Solve: 15 - X = 7", 8, "Basic Equation"));
    }

    // GAME OVER MESSAGE
    private static void showGameOver() {
        System.out.println("\n💀 GAME OVER 💀");
        System.out.println("Better luck next time!");
    }
}