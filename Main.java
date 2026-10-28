import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// start

public class Main {
    private static GymManager gymManager = new GymManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Login comes first — nothing else is reachable until it succeeds.
        if (!loginScreen()) {
            System.out.println("Too many failed attempts. Exiting.");
            scanner.close();
            return;
        }

        seedSampleData();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> memberMenu();
                case 2 -> planMenu();
                case 3 -> trainerMenu();
                case 4 -> paymentMenu();
                case 0 -> { running = false; System.out.println("Logged out. Goodbye!"); }
                default -> System.out.println("Invalid choice, please try again.\n");
            }
        }
        scanner.close();
    }

    // ---------------- Login ----------------
    private static boolean loginScreen() {
        System.out.println("===== GYM MANAGEMENT SYSTEM - LOGIN =====");
        int attemptsLeft = 3;
        while (attemptsLeft > 0) {
            String username = readString("Username: ");
            String password = readString("Password: ");
            if (gymManager.login(username, password)) {
                System.out.println("Login successful. Welcome, " + username + "!\n");
                return true;
            }
            attemptsLeft--;
            if (attemptsLeft > 0) {
                System.out.println("Invalid username or password. Attempts left: " + attemptsLeft + "\n");
            }
        }
        return false;
    }

    private static void printMainMenu() {
        System.out.println("===== GYM MANAGEMENT SYSTEM =====");
        System.out.println("1. Member Management");
        System.out.println("2. Membership Plan Management");
        System.out.println("3. Trainer Management");
        System.out.println("4. Payment Management");
        System.out.println("0. Logout & Exit");
    }

    private static void memberMenu() {
        System.out.println("\n--- Member Management ---");
        System.out.println("1. Register New Member");
        System.out.println("2. View All Members");
        System.out.println("3. Search Member by Name");
        System.out.println("4. Check Registration (look up by ID)");
        System.out.println("5. Update Member");
        System.out.println("6. Delete Member");
        System.out.println("0. Back");
        int choice = readInt("Enter your choice: ");

        switch (choice) {
            case 1 -> {
                String name = readString("Name: ");
                int age = readInt("Age: ");
                String contact = readString("Contact: ");
                Member registered = gymManager.registerMember(name, age, contact);
                System.out.println("Registration successful!");
                System.out.println("Assigned Member ID: " + registered.getId());
                System.out.println("Registration Date : " + registered.getJoinDate());
                System.out.println();
            }
            case 2 -> {
                List<Member> all = gymManager.getAllMembers();
                if (all.isEmpty()) System.out.println("No members found.\n");
                else { for (Member m : all) System.out.println(m); System.out.println(); }
            }
            case 3 -> {
                String keyword = readString("Enter name to search: ");
                List<Member> results = gymManager.searchMemberByName(keyword);
                if (results.isEmpty()) System.out.println("No matching members found.\n");
                else { for (Member m : results) System.out.println(m); System.out.println(); }
            }
            case 4 -> {
                String id = readString("Enter Member ID to verify: ");
                if (gymManager.isRegistered(id)) {
                    System.out.println("Member " + id + " is registered:");
                    System.out.println(gymManager.findMemberById(id));
                } else {
                    System.out.println("No registration found for ID " + id + ".");
                }
                System.out.println();
            }
            case 5 -> {
                String id = readString("Member ID to update: ");
                String name = readString("New name: ");
                int age = readInt("New age: ");
                String contact = readString("New contact: ");
                boolean updated = gymManager.updateMember(id, name, age, contact);
                System.out.println(updated ? "Member updated.\n" : "Member not found.\n");
            }
            case 6 -> {
                String id = readString("Member ID to delete: ");
                boolean deleted = gymManager.deleteMember(id);
                System.out.println(deleted ? "Member deleted.\n" : "Member not found.\n");
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice.\n");
        }
    }

    private static void planMenu() {
        System.out.println("\n--- Membership Plan Management ---");
        System.out.println("1. Add Plan\n2. View All Plans\n0. Back");
        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1 -> {
                String id = readString("Plan ID: ");
                String name = readString("Plan name: ");
                int duration = readInt("Duration (months): ");
                double fee = readDouble("Fee: ");
                gymManager.addPlan(new MembershipPlan(id, name, duration, fee));
                System.out.println("Plan added successfully.\n");
            }
            case 2 -> {
                List<MembershipPlan> all = gymManager.getAllPlans();
                if (all.isEmpty()) System.out.println("No plans found.\n");
                else { for (MembershipPlan p : all) System.out.println(p); System.out.println(); }
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice.\n");
        }
    }

    private static void trainerMenu() {
        System.out.println("\n--- Trainer Management ---");
        System.out.println("1. Add Trainer\n2. View All Trainers\n3. Assign Trainer to Member\n0. Back");
        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1 -> {
                String id = readString("Trainer ID: ");
                String name = readString("Name: ");
                String specialization = readString("Specialization: ");
                String contact = readString("Contact: ");
                gymManager.addTrainer(new Trainer(id, name, specialization, contact));
                System.out.println("Trainer added successfully.\n");
            }
            case 2 -> {
                List<Trainer> all = gymManager.getAllTrainers();
                if (all.isEmpty()) System.out.println("No trainers found.\n");
                else { for (Trainer t : all) System.out.println(t); System.out.println(); }
            }
            case 3 -> {
                String trainerId = readString("Trainer ID: ");
                String memberId = readString("Member ID: ");
                boolean assigned = gymManager.assignTrainerToMember(trainerId, memberId);
                System.out.println(assigned ? "Trainer assigned.\n" : "Trainer or member not found.\n");
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice.\n");
        }
    }

    private static void paymentMenu() {
        System.out.println("\n--- Payment Management ---");
        System.out.println("1. Record Payment\n2. View All Payments\n3. View Payments for a Member\n0. Back");
        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1 -> {
                String paymentId = readString("Payment ID: ");
                String memberId = readString("Member ID: ");
                String planId = readString("Plan ID: ");
                String date = readString("Payment date (DD-MM-YYYY): ");
                String method = readString("Method (Cash/Card/Mobile Banking): ");
                Payment p = gymManager.recordPayment(paymentId, memberId, planId, date, method);
                System.out.println(p != null ? "Payment recorded:\n" + p + "\n" : "Invalid member or plan ID.\n");
            }
            case 2 -> {
                List<Payment> all = gymManager.getAllPayments();
                if (all.isEmpty()) System.out.println("No payments found.\n");
                else { for (Payment p : all) System.out.println(p); System.out.println(); }
            }
            case 3 -> {
                String memberId = readString("Member ID: ");
                List<Payment> results = gymManager.getPaymentsForMember(memberId);
                if (results.isEmpty()) System.out.println("No payments found for this member.\n");
                else { for (Payment p : results) System.out.println(p); System.out.println(); }
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice.\n");
        }
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) { System.out.print("Please enter a valid number: "); scanner.next(); }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) { System.out.print("Please enter a valid amount: "); scanner.next(); }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static void seedSampleData() {
        gymManager.addPlan(new MembershipPlan("P1", "Monthly", 1, 1500));
        gymManager.addPlan(new MembershipPlan("P2", "Quarterly", 3, 4000));
        gymManager.addPlan(new MembershipPlan("P3", "Yearly", 12, 14000));

        Member m1 = gymManager.registerMember("Rafiq Islam", 24, "01710000001");
        gymManager.registerMember("Nusrat Jahan", 29, "01810000002");

        gymManager.addTrainer(new Trainer("T1", "Kamal Hossain", "Strength Training", "01910000003"));
        gymManager.assignTrainerToMember("T1", m1.getId());
    }
}

// ---------------------------------------------

class GymManager {
    // ---------------- Login credentials ----------------
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private List<Member> members;
    private List<MembershipPlan> plans;
    private List<Trainer> trainers;
    private List<Payment> payments;
    private int nextMemberNumber;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public GymManager() {
        members = new ArrayList<>();
        plans = new ArrayList<>();
        trainers = new ArrayList<>();
        payments = new ArrayList<>();
        nextMemberNumber = 1;
    }

    // ---------------- Login ----------------
    public boolean login(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    // ---------------- Registration ----------------
    public Member registerMember(String name, int age, String contact) {
        String id = generateMemberId();
        String registrationDate = LocalDate.now().format(DATE_FORMAT);
        Member m = new Member(id, name, age, contact, registrationDate);
        members.add(m);
        return m;
    }

    private String generateMemberId() {
        String id = String.format("M%03d", nextMemberNumber);
        nextMemberNumber++;
        return id;
    }

    public boolean isRegistered(String id) {
        return findMemberById(id) != null;
    }

    // ---------------- Member CRUD ----------------
    public void addMember(Member m) { members.add(m); }

    public List<Member> getAllMembers() { return members; }

    public Member findMemberById(String id) {
        for (Member m : members) {
            if (m.getId().equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    public List<Member> searchMemberByName(String keyword) {
        List<Member> results = new ArrayList<>();
        for (Member m : members) {
            if (m.getName().toLowerCase().contains(keyword.toLowerCase())) results.add(m);
        }
        return results;
    }

    public boolean updateMember(String id, String newName, int newAge, String newContact) {
        Member m = findMemberById(id);
        if (m == null) return false;
        m.setName(newName);
        m.setAge(newAge);
        m.setContact(newContact);
        return true;
    }

    public boolean deleteMember(String id) {
        Member m = findMemberById(id);
        if (m == null) return false;
        members.remove(m);
        return true;
    }

    // ---------------- Membership Plan management ----------------
    public void addPlan(MembershipPlan p) { plans.add(p); }

    public List<MembershipPlan> getAllPlans() { return plans; }

    public MembershipPlan findPlanById(String planId) {
        for (MembershipPlan p : plans) {
            if (p.getPlanId().equalsIgnoreCase(planId)) return p;
        }
        return null;
    }

    // ---------------- Trainer management ----------------
    public void addTrainer(Trainer t) { trainers.add(t); }

    public List<Trainer> getAllTrainers() { return trainers; }

    public Trainer findTrainerById(String trainerId) {
        for (Trainer t : trainers) {
            if (t.getTrainerId().equalsIgnoreCase(trainerId)) return t;
        }
        return null;
    }

    public boolean assignTrainerToMember(String trainerId, String memberId) {
        Trainer t = findTrainerById(trainerId);
        Member m = findMemberById(memberId);
        if (t == null || m == null) return false;
        t.assignMember(memberId);
        return true;
    }

    // ---------------- Payment handling ----------------
    public Payment recordPayment(String paymentId, String memberId, String planId,
                                  String paymentDate, String method) {
        MembershipPlan plan = findPlanById(planId);
        Member member = findMemberById(memberId);
        if (plan == null || member == null) return null;
        Payment payment = new Payment(paymentId, memberId, planId, plan.getFee(), paymentDate, method);
        payments.add(payment);
        member.setActive(true);
        return payment;
    }

    public List<Payment> getPaymentsForMember(String memberId) {
        List<Payment> results = new ArrayList<>();
        for (Payment p : payments) {
            if (p.getMemberId().equalsIgnoreCase(memberId)) results.add(p);
        }
        return results;
    }

    public List<Payment> getAllPayments() { return payments; }

    public int getActiveMemberCount() {
        int count = 0;
        for (Member m : members) {
            if (m.isActive()) count++;
        }
        return count;
    }
}

// ---------------------------------------------
class Member {
    private String id;
    private String name;
    private int age;
    private String contact;
    private String joinDate;
    private boolean active;

    public Member(String id, String name, int age, String contact, String joinDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.joinDate = joinDate;
        this.active = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getContact() { return contact; }
    public String getJoinDate() { return joinDate; }
    public boolean isActive() { return active; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setContact(String contact) { this.contact = contact; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format(
                "ID: %-6s | Name: %-18s | Age: %-3d | Contact: %-14s | Joined: %-10s | Status: %s",
                id, name, age, contact, joinDate, active ? "Active" : "Inactive"
        );
    }
}

// ---------------------------------------------
class MembershipPlan {
    private String planId;
    private String planName;
    private int durationMonths;
    private double fee;

    public MembershipPlan(String planId, String planName, int durationMonths, double fee) {
        this.planId = planId;
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.fee = fee;
    }

    public String getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public int getDurationMonths() { return durationMonths; }
    public double getFee() { return fee; }

    public void setPlanName(String planName) { this.planName = planName; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }
    public void setFee(double fee) { this.fee = fee; }

    @Override
    public String toString() {
        return String.format(
                "Plan ID: %-6s | Name: %-12s | Duration: %-2d month(s) | Fee: %.2f",
                planId, planName, durationMonths, fee
        );
    }
}

// ---------------------------------------------

class Trainer {
    private String trainerId;
    private String name;
    private String specialization;
    private String contact;
    private List<String> assignedMemberIds;

    public Trainer(String trainerId, String name, String specialization, String contact) {
        this.trainerId = trainerId;
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
        this.assignedMemberIds = new ArrayList<>();
    }

    public String getTrainerId() { return trainerId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getContact() { return contact; }
    public List<String> getAssignedMemberIds() { return assignedMemberIds; }

    public void assignMember(String memberId) {
        if (!assignedMemberIds.contains(memberId)) {
            assignedMemberIds.add(memberId);
        }
    }

    public void unassignMember(String memberId) {
        assignedMemberIds.remove(memberId);
    }

    @Override
    public String toString() {
        return String.format(
                "Trainer ID: %-6s | Name: %-18s | Specialization: %-12s | Contact: %-14s | Members assigned: %d",
                trainerId, name, specialization, contact, assignedMemberIds.size()
        );
    }
}

// ---------------------------------------------
class Payment {
    private String paymentId;
    private String memberId;
    private String planId;
    private double amount;
    private String paymentDate;
    private String method;

    public Payment(String paymentId, String memberId, String planId,
                    double amount, String paymentDate, String method) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.planId = planId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public String getPaymentId() { return paymentId; }
    public String getMemberId() { return memberId; }
    public String getPlanId() { return planId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getMethod() { return method; }

    @Override
    public String toString() {
        return String.format(
                "Receipt: %-6s | Member: %-6s | Plan: %-6s | Amount: %.2f | Date: %-10s | Method: %s",
                paymentId, memberId, planId, amount, paymentDate, method
        );
    }
}