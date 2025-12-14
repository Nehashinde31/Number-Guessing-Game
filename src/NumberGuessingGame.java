// NumberGuessingGame.java
// Upgraded console number guessing game with levels, scoring, highscore persistence and puzzles.
// Save as NumberGuessingGame.java
// Compile: javac NumberGuessingGame.java
// Run: java NumberGuessingGame

import java.io.*;
import java.util.*;

public class NumberGuessingGame {

    static final Scanner sc = new Scanner(System.in);
    static final Random rnd = new Random();

    // Persistent highscore file
    static final String HIGHSCORE_FILE = "highscore.txt";

    // Puzzle structure
    static class Puzzle {
        String q;
        int a;
        String logic;
        Puzzle(String q, int a, String logic) { this.q=q; this.a=a; this.logic=logic; }
    }

    static final List<Puzzle> puzzleBank = new ArrayList<>();
    static {
        puzzleBank.add(new Puzzle("2, 4, 8, 16... Next?", 32, "Powers of 2"));
        puzzleBank.add(new Puzzle("1, 4, 9, 16... Next?", 25, "Squares"));
        puzzleBank.add(new Puzzle("3, 6, 9, 12... Next?", 15, "Multiples of 3"));
        puzzleBank.add(new Puzzle("100, 90, 80, 70... Next?", 60, "Subtracting 10"));
        puzzleBank.add(new Puzzle("Fibonacci: 1,1,2,3,5,8... Next?", 13, "Fibonacci"));
        puzzleBank.add(new Puzzle("Prime: 2,3,5,7,11... Next?", 13, "Primes"));
        puzzleBank.add(new Puzzle("If 5+3=28, then 9+1=?", 810, "Diff & Sum Trick"));
        puzzleBank.add(new Puzzle("Solve: 7*8 - 6", 50, "BODMAS"));
        puzzleBank.add(new Puzzle("Solve: 3x + 5 = 20", 5, "Algebra"));
        puzzleBank.add(new Puzzle("Half of 2 + 2 = ?", 3, "Trick"));
        puzzleBank.add(new Puzzle("How many sides in a Hexagon?", 6, "Geometry"));
        puzzleBank.add(new Puzzle("Cube root of 125?", 5, "Math Fact"));
        puzzleBank.add(new Puzzle("Square root of 144?", 12, "Math Fact"));
        puzzleBank.add(new Puzzle("A=1, B=2... Z=?", 26, "Alphabet"));
        puzzleBank.add(new Puzzle("2^5 =", 32, "Exponents"));
        puzzleBank.add(new Puzzle("Hours in 2 days?", 48, "24×2"));
        puzzleBank.add(new Puzzle("Cricket team players?", 11, "General Knowledge"));
        puzzleBank.add(new Puzzle("Binary 111 = ?", 7, "Binary"));
        puzzleBank.add(new Puzzle("Solve: 15 - X = 7", 8, "Basic Equation"));
    }

    // Game state
    static int totalScore = 0;
    static int highScore = 0;

    public static void main(String[] args) {
        System.out.println("\n===============================================");
        System.out.println("   🚀 SMART NUMBER GUESSING - Console (v1.1)");
        System.out.println("   🎯 Guess, solve puzzles, unlock hints & score");
        System.out.println("===============================================\n");

        // load highscore if present
        highScore = loadHighscore();

        boolean keepPlaying = true;
        while (keepPlaying) {

            System.out.println("Choose difficulty: 1) Easy  2) Normal  3) Hard");
            int diff = readChoice(1,3);
            Difficulty d = Difficulty.fromChoice(diff);

            playRound(d);

            System.out.println("\n🔁 Play another round? (y/n)");
            String r = sc.next().trim().toLowerCase();
            keepPlaying = r.equals("y") || r.equals("yes");
        }

        System.out.println("\n📊 Session Summary:");
        System.out.println("Total Score: " + totalScore);
        System.out.println("High Score (all time): " + highScore);
        System.out.println("\n✨ Thanks — Share screenshots for CodSoft submission!");
    }

    // Difficulty enum
    enum Difficulty {
        EASY(12, 80), NORMAL(8, 100), HARD(6, 140);

        int attempts; int basePoints;
        Difficulty(int a, int p) { attempts=a; basePoints=p; }

        static Difficulty fromChoice(int c) {
            if (c==1) return EASY;
            if (c==3) return HARD;
            return NORMAL;
        }
    }

    // Play one round
    static void playRound(Difficulty diff) {
        int secret = rnd.nextInt(100) + 1;
        int maxAttempts = diff.attempts;
        int attempts = 0;
        boolean won = false;
        List<Integer> guesses = new ArrayList<>();
        Set<Puzzle> usedP = new HashSet<>();

        System.out.println("\n🔒 New Round started! (Number 1-100)");
        System.out.println("Difficulty: " + diff.name() + " | Attempts allowed: " + maxAttempts);

        while (attempts < maxAttempts) {
            System.out.print("\n➡ Attempt " + (attempts+1) + "/" + maxAttempts + " | Enter your guess: ");
            int g = readIntInRange(1,100);
            attempts++;
            guesses.add(g);

            if (g == secret) {
                int roundScore = calcScore(diff, maxAttempts, attempts);
                totalScore += roundScore;
                System.out.println("\n🎉 CORRECT! You found " + secret);
                System.out.println("🏆 Round Score: " + roundScore + "  |  Total Score: " + totalScore);
                won = true;
                updateHighscoreIfNeeded(totalScore);
                showRoundSummary(secret, guesses, attempts, roundScore, true);
                break;
            } else if (g < secret) {
                System.out.println("📉 Too Low!");
            } else {
                System.out.println("📈 Too High!");
            }

            // Unlock hints depending on difficulty and attempt number
            maybeUnlockPuzzleHint(diff, attempts, secret, usedP);

            // After each guess offer small tip (distance)
            int diffAbs = Math.abs(g - secret);
            if (diffAbs <= 3) System.out.println("🔥 You're extremely close!");
            else if (diffAbs <= 8) System.out.println("🙂 You're close.");
            else System.out.println("😅 You're far.");
        }

        if (!won) {
            System.out.println("\n💀 GAME OVER — Attempts exhausted.");
            System.out.println("🔢 Secret number was: " + secret);
            showRoundSummary(secret, guesses, attempts, 0, false);

            // option to save round summary
            System.out.println("\nDo you want to save this round summary to a file? (y/n)");
            String c = sc.next().trim().toLowerCase();
            if (c.equals("y") || c.equals("yes")) {
                saveRoundToFile(secret, guesses, attempts, 0);
            }
        }
    }

    // decide when to unlock puzzle/hint
    static void maybeUnlockPuzzleHint(Difficulty diff, int attempts, int secret, Set<Puzzle> usedP) {
        // Easy: puzzles at attempts 3 and 6
        // Normal: puzzles at 2 and 5
        // Hard: puzzles at 2 and 4 (less hints)
        boolean trigger = false;
        int hintType = 1;
        if (diff == Difficulty.EASY) {
            if (attempts == 3) { trigger=true; hintType=1; }
            if (attempts == 6) { trigger=true; hintType=2; }
        } else if (diff == Difficulty.NORMAL) {
            if (attempts == 2) { trigger=true; hintType=1; }
            if (attempts == 5) { trigger=true; hintType=2; }
        } else { // HARD
            if (attempts == 2) { trigger=true; hintType=1; }
            if (attempts == 4) { trigger=true; hintType=2; }
        }

        if (trigger) {
            Puzzle p = pickUnusedPuzzle(usedP);
            if (p == null) return;
            usedP.add(p);

            System.out.println("\n🔓 PUZZLE UNLOCKED: Solve to get a hint!");
            System.out.println("🧩 " + p.q);
            System.out.print("Your answer: ");
            int ans = safeReadInt();

            if (ans == p.a) {
                System.out.println("✅ Correct! (" + p.logic + ")");
                if (hintType == 1) {
                    System.out.println("💡 Hint: " + (secret % 2 == 0 ? "Secret is EVEN" : "Secret is ODD"));
                } else {
                    System.out.println("💡 Hint: " + (secret > 50 ? "Secret > 50" : "Secret ≤ 50"));
                }
            } else {
                System.out.println("❌ Wrong. Puzzle answer: " + p.a);
                System.out.println("🔒 Hint locked.");
            }
        }
    }

    static Puzzle pickUnusedPuzzle(Set<Puzzle> used) {
        List<Puzzle> available = new ArrayList<>();
        for (Puzzle p : puzzleBank) if (!used.contains(p)) available.add(p);
        if (available.isEmpty()) return null;
        return available.get(rnd.nextInt(available.size()));
    }

    // Score calculation: more points for harder difficulty and fewer attempts
    static int calcScore(Difficulty diff, int maxAttempts, int usedAttempts) {
        int base = diff.basePoints; // base per difficulty
        int bonus = Math.max(0, (maxAttempts - usedAttempts)) * 20;
        return base + bonus;
    }

    // Round summary display
    static void showRoundSummary(int secret, List<Integer> guesses, int attempts, int score, boolean won) {
        System.out.println("\n===== ROUND SUMMARY =====");
        System.out.println("Secret: " + (won ? secret : "REVEALED -> " + secret));
        System.out.println("Attempts used: " + attempts);
        System.out.println("Guesses: " + guesses);
        System.out.println("Round score: " + score);
        System.out.println("=========================");
    }

    // Save round summary to file
    static void saveRoundToFile(int secret, List<Integer> guesses, int attempts, int score) {
        String fname = "round_summary_" + System.currentTimeMillis() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            pw.println("Smart Number Guessing - Round Summary");
            pw.println("Secret: " + secret);
            pw.println("Attempts used: " + attempts);
            pw.println("Guesses: " + guesses);
            pw.println("Round score: " + score);
            pw.println("Total Score so far: " + totalScore);
            System.out.println("✅ Round summary saved to file: " + fname);
        } catch (IOException e) {
            System.out.println("⚠ Failed to save file: " + e.getMessage());
        }
    }

    // Highscore load/save
    static int loadHighscore() {
        File f = new File(HIGHSCORE_FILE);
        if (!f.exists()) return 0;
        try (Scanner s = new Scanner(f)) {
            if (s.hasNextInt()) return s.nextInt();
        } catch (Exception e) { /* ignore */ }
        return 0;
    }

    static void updateHighscoreIfNeeded(int currentTotal) {
        if (currentTotal > highScore) {
            highScore = currentTotal;
            try (PrintWriter pw = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
                pw.println(highScore);
                System.out.println("🏅 NEW HIGHSCORE! Saved to " + HIGHSCORE_FILE);
            } catch (IOException e) {
                System.out.println("⚠ Could not save highscore: " + e.getMessage());
            }
        }
    }

    // Utility: read int within range
    static int readIntInRange(int lo, int hi) {
        while (true) {
            int v = safeReadInt();
            if (v >= lo && v <= hi) return v;
            System.out.print("Enter a number between " + lo + " and " + hi + ": ");
        }
    }

    // safe int read (skips non-int)
    static int safeReadInt() {
        while (!sc.hasNextInt()) {
            System.out.print("⚠ Enter a valid integer: ");
            sc.next();
        }
        return sc.nextInt();
    }

    static int readChoice(int lo, int hi) {
        System.out.print("Enter choice: ");
        return readIntInRange(lo, hi);
    }
}
