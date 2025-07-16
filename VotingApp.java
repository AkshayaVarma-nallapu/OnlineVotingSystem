import java.io.*;
import java.util.*;

public class VotingApp {
    static Map<String, String> candidates = new LinkedHashMap<>(); // roll -> name
    static Map<String, Integer> voteCount = new LinkedHashMap<>(); // roll -> vote count
    static Set<String> voters = new HashSet<>();

    static String candidatesFile = "candidates.txt";
    static String votesFile = "votes.txt";
    static String votersFile = "voters.txt";
    static String adminPassword = "admin123";

    public static void main(String[] args) throws IOException {
        loadCandidates();
        loadVotes();
        loadVoters();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Online Voting System ---");
            System.out.println("1. Login as Student\n2. Admin Login (View Results)\n3. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> studentLogin(sc);
                case 2 -> adminAccess(sc);
                case 3 -> {
                    saveAll();
                    System.out.println("Thank you for using the voting system!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void studentLogin(Scanner sc) throws IOException {
        System.out.print("\nEnter your Roll Number: ");
        String roll = sc.nextLine().trim();

        if (voters.contains(roll)) {
            System.out.println("❌ You have already participated (either as candidate or voter). Access denied.");
            return;
        }

        System.out.println("Welcome! What would you like to do?");
        System.out.println("1. Become a Candidate\n2. Vote for a Candidate");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt(); sc.nextLine();

        switch (choice) {
            case 1 -> {
                registerCandidate(sc, roll);
                voters.add(roll);
            }
            case 2 -> {
                vote(sc, roll);
                voters.add(roll);
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    static void registerCandidate(Scanner sc, String roll) throws IOException {
        System.out.print("Enter your full name: ");
        String name = sc.nextLine().trim();
        candidates.put(roll, name);
        voteCount.put(roll, 0);
        System.out.println("✅ You are now registered as a candidate! You cannot vote.");
    }

    static void vote(Scanner sc, String roll) {
        if (candidates.isEmpty()) {
            System.out.println("No candidates registered yet.");
            return;
        }

        System.out.println("\nAvailable Candidates:");
        List<String> rolls = new ArrayList<>(candidates.keySet());
        for (int i = 0; i < rolls.size(); i++) {
            String r = rolls.get(i);
            System.out.println((i + 1) + ". " + candidates.get(r) + " (" + r + ")");
        }

        System.out.print("Enter candidate number to vote: ");
        int choice = sc.nextInt();
        if (choice < 1 || choice > rolls.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        String selectedRoll = rolls.get(choice - 1);
        voteCount.put(selectedRoll, voteCount.get(selectedRoll) + 1);
        System.out.println("✅ Vote recorded for: " + candidates.get(selectedRoll));
    }

    static void adminAccess(Scanner sc) {
        System.out.print("\nEnter admin password: ");
        String input = sc.nextLine();
        if (input.equals(adminPassword)) {
            displayResults();
        } else {
            System.out.println("❌ Incorrect password. Access denied.");
        }
    }

    static void displayResults() {
        System.out.println("\n=== Final Election Results ===");
        String winner = "";
        int maxVotes = -1;
        for (String roll : voteCount.keySet()) {
            String name = candidates.get(roll);
            int votes = voteCount.get(roll);
            System.out.println(name + " (" + roll + "): " + votes + " votes");
            if (votes > maxVotes) {
                maxVotes = votes;
                winner = name;
            }
        }
        if (!winner.isEmpty()) {
            System.out.println("\n🎉 Winner: " + winner + " 🏆");
        }
    }

    static void loadCandidates() throws IOException {
        File file = new File(candidatesFile);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    candidates.put(parts[0], parts[1]);
                    voteCount.put(parts[0], 0);
                }
            }
        }
    }

    static void loadVotes() throws IOException {
        File file = new File(votesFile);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String roll = parts[0];
                    int votes = Integer.parseInt(parts[1]);
                    voteCount.put(roll, votes);
                }
            }
        }
    }

    static void loadVoters() throws IOException {
        File file = new File(votersFile);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                voters.add(line.trim());
            }
        }
    }

    static void saveAll() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(candidatesFile))) {
            for (Map.Entry<String, String> entry : candidates.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(votesFile))) {
            for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(votersFile))) {
            for (String voter : voters) {
                bw.write(voter);
                bw.newLine();
            }
        }
    }
}
