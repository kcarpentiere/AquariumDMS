import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AquariumGUI {

    // Ticket pricing
    private static final double ADULT_PRICE = 25.00;
    private static final double CHILD_PRICE = 15.00;
    private static final int ADULT_AGE = 18;

    // User Storage
    private static final Map<String, String> accounts = new HashMap<>();      // username -> password
    private static final Map<String, Guest> guestDetails = new HashMap<>();   // username -> full guest
    private static Guest currentGuest = null;

    //Navigation
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private static final String PAGE_LOGIN = "login";
    private static final String PAGE_REGISTER = "register";
    private static final String PAGE_HOME = "home";
    private static final String PAGE_TICKETS = "tickets";

    //Register form
    private JTextField regFirst, regLast, regEmail, regDob, regPhone, regUser;
    private JPasswordField regPass;

    // Aquarium style
    private static final Color DEEP_BLUE = new Color(0x0B3D5C);
    private static final Color CORAL = new Color(0xE76F51);
    private static final Color SAND = new Color(0xE9C46A);
    private static final Color WAVE = new Color(0x2A9D8F);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AquariumGUI::new);
    }

    public AquariumGUI() {
        JFrame frame = new JFrame("Aquarium DMS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(680, 620);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        root.add(buildLoginPage(), PAGE_LOGIN);
        root.add(buildRegisterPage(), PAGE_REGISTER);
        root.add(buildHomePage(), PAGE_HOME);
        root.add(buildTicketPage(), PAGE_TICKETS);

        frame.add(root);
        cards.show(root, PAGE_LOGIN);
        frame.setVisible(true);
    }

    private void go(String page) {
        cards.show(root, page);
    }


    // PAGE 1 — Login

    private JPanel buildLoginPage() {
        JPanel panel = aquariumPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\uD83D\uDC1F Aquarium DMS");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Guest login & ticket purchase system");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(0xB8E0D2));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = styledField(18);
        JPasswordField passField = new JPasswordField(18);

        JLabel status = new JLabel(" ");
        status.setForeground(SAND);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = button("Log In", WAVE);
        JButton registerBtn = button("Create Account", CORAL);
        JButton guestBtn = button("Continue as Guest", new Color(0x6C757D));

        loginBtn.addActionListener(e -> tryLogin(userField, passField, status));
        registerBtn.addActionListener(e -> {
            clearRegisterForm();
            go(PAGE_REGISTER);
        });
        guestBtn.addActionListener(e -> {
            currentGuest = new Guest("Guest", "", "", "", "", "");
            go(PAGE_HOME);
        });

        panel.add(Box.createVerticalStrut(60));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));
        panel.add(formRow("Username", userField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(formRow("Password", passField));
        panel.add(Box.createVerticalStrut(20));
        panel.add(center(loginBtn));
        panel.add(Box.createVerticalStrut(8));
        panel.add(center(registerBtn));
        panel.add(Box.createVerticalStrut(8));
        panel.add(center(guestBtn));
        panel.add(Box.createVerticalStrut(12));
        panel.add(status);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void tryLogin(JTextField user, JPasswordField pass, JLabel status) {
        String u = user.getText().trim();
        String p = new String(pass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            status.setText("Please enter a username and password.");
            return;
        }
        if (accounts.containsKey(u) && accounts.get(u).equals(p)) {
            currentGuest = guestDetails.get(u);
            status.setText(" ");
            go(PAGE_HOME);
        } else {
            status.setText("Incorrect username or password.");
        }
    }

    // PAGE 2 — Create Account

    private JPanel buildRegisterPage() {
        JPanel panel = aquariumPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\uD83D\uDC4B Create Your Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("You must be 18 or older to register");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(SAND);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        regFirst = styledField(16);
        regLast = styledField(16);
        regEmail = styledField(16);
        regDob = styledField(16);
        regPhone = styledField(16);
        regUser = styledField(16);
        regPass = new JPasswordField(16);

        JLabel status = new JLabel(" ");
        status.setForeground(SAND);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton createBtn = button("Create Account", CORAL);
        createBtn.addActionListener(e -> tryRegister(
                regFirst, regLast, regEmail, regDob, regPhone,
                regUser, regPass, status));

        JButton backBtn = button("\u2190 Back to Login", new Color(0x6C757D));
        backBtn.addActionListener(e -> go(PAGE_LOGIN));

        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(14));
        panel.add(formRow("First name", regFirst));
        panel.add(Box.createVerticalStrut(8));
        panel.add(formRow("Last name", regLast));
        panel.add(Box.createVerticalStrut(8));
        panel.add(formRow("Email", regEmail));
        panel.add(Box.createVerticalStrut(8));
        panel.add(formRow("Birth date (YYYY-MM-DD)", regDob));
        panel.add(Box.createVerticalStrut(8));
        panel.add(formRow("Phone number", regPhone));
        panel.add(Box.createVerticalStrut(12));
        panel.add(formRow("Username", regUser));
        panel.add(Box.createVerticalStrut(8));
        panel.add(formRow("Password", regPass));
        panel.add(Box.createVerticalStrut(14));
        panel.add(center(createBtn));
        panel.add(Box.createVerticalStrut(6));
        panel.add(status);
        panel.add(Box.createVerticalStrut(8));
        panel.add(center(backBtn));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void tryRegister(JTextField first, JTextField last, JTextField email,
                             JTextField dob, JTextField phone,
                             JTextField user, JPasswordField pass, JLabel status) {

        String f = first.getText().trim();
        String l = last.getText().trim();
        String em = email.getText().trim();
        String birth = dob.getText().trim();
        String ph = phone.getText().trim();
        String u = user.getText().trim();
        String p = new String(pass.getPassword());

        //Required fields
        if (f.isEmpty() || l.isEmpty() || em.isEmpty() || birth.isEmpty()
                || ph.isEmpty() || u.isEmpty() || p.isEmpty()) {
            status.setText("All fields are required.");
            return;
        }
        // Email format
        if (!em.contains("@") || !em.contains(".")) {
            status.setText("Please enter a valid email address.");
            return;
        }
        //Password rules: length, 1 capital, 1 number
        if (p.length() < 4) {
            status.setText("Password must be at least 4 characters.");
            return;
        }
        if (!hasCapital(p)) {
            status.setText("Password must contain at least one capital letter.");
            return;
        }
        if (!hasDigit(p)) {
            status.setText("Password must contain at least one number.");
            return;
        }
        // --- Username uniqueness ---
        if (accounts.containsKey(u)) {
            status.setText("That username is already taken.");
            return;
        }
        // --- Phone must be digits ---
        if (phoneHasLetters(ph)) {
            status.setText("Phone number must contain only digits.");
            return;
        }
        //Birthday Checker
        if (!isAdult(birth)) {
            status.setText("You must be at least 18 years old to register.");
            return;
        }

        //Successful Account Creation
        accounts.put(u, p);
        Guest g = new Guest(f, l, em, birth, ph, u);
        guestDetails.put(u, g);
        currentGuest = g;
        status.setText(" ");
        go(PAGE_HOME);
    }

    private void clearRegisterForm() {
        regFirst.setText("");
        regLast.setText("");
        regEmail.setText("");
        regDob.setText("");
        regPhone.setText("");
        regUser.setText("");
        regPass.setText("");
    }


    // PAGE 3 — Home
    private JPanel buildHomePage() {
        JPanel panel = aquariumPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\uD83D\uDC20 Welcome to Aquarium DMS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel who = new JLabel(" ");
        who.setFont(new Font("SansSerif", Font.PLAIN, 15));
        who.setForeground(new Color(0xB8E0D2));
        who.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("Exhibits, feeding shows and hands-on tide pools await!");
        info.setForeground(SAND);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton buyBtn = button("Buy Tickets \u2192", CORAL);
        buyBtn.addActionListener(e -> {
            refreshHome(who);
            go(PAGE_TICKETS);
        });

        JButton logoutBtn = button("\u2190 Log Out", new Color(0x6C757D));
        logoutBtn.addActionListener(e -> {
            currentGuest = null;
            clearRegisterForm();
            go(PAGE_LOGIN);
        });

        panel.add(Box.createVerticalStrut(60));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(who);
        panel.add(Box.createVerticalStrut(30));
        panel.add(info);
        panel.add(Box.createVerticalStrut(30));
        panel.add(center(buyBtn));
        panel.add(Box.createVerticalStrut(14));
        panel.add(center(logoutBtn));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void refreshHome(JLabel who) {
        if (currentGuest != null) {
            who.setText("Logged in as: " + currentGuest.getUsername()
                    + "  (" + currentGuest.getFirstName() + " " + currentGuest.getLastName() + ")");
        }
    }


    // PAGE 4 — Tickets
    private JPanel buildTicketPage() {
        JPanel panel = aquariumPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\uD83C\uDFAB Aquarium Tickets");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSpinner adult = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        JSpinner child = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        styleSpinner(adult);
        styleSpinner(child);

        JLabel total = new JLabel("Total: $0.00");
        total.setFont(new Font("SansSerif", Font.BOLD, 20));
        total.setForeground(SAND);
        total.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel status = new JLabel(" ");
        status.setForeground(WAVE);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        Runnable updateTotal = () -> {
            int a = (Integer) adult.getValue();
            int c = (Integer) child.getValue();
            total.setText(String.format("Total: $%.2f", a * ADULT_PRICE + c * CHILD_PRICE));
        };
        adult.addChangeListener(e -> updateTotal.run());
        child.addChangeListener(e -> updateTotal.run());

        JButton buyBtn = button("Purchase Tickets", WAVE);
        buyBtn.addActionListener(e -> {
            int a = (Integer) adult.getValue();
            int c = (Integer) child.getValue();
            if (a == 0 && c == 0) {
                status.setText("Please select at least one ticket.");
                return;
            }
            if (!isAdult(currentGuest.getBirthDate())) {
                status.setText("Purchase blocked: you must be 18 or older to buy tickets.");
                return;
            }

            double cost = a * ADULT_PRICE + c * CHILD_PRICE;
            status.setText(String.format(
                    "Order confirmed for %s: %d adult(s), %d child(ren) \u2014 $%.2f",
                    currentGuest.getUsername(), a, c, cost));
            JOptionPane.showMessageDialog(panel,
                    String.format("Thanks, %s!\n\n%d Adult ticket(s)  @ $%.2f\n%d Child ticket(s)  @ $%.2f\n\nTotal: $%.2f",
                            currentGuest.getFirstName() + " " + currentGuest.getLastName(),
                            a, ADULT_PRICE, c, CHILD_PRICE, cost),
                    "Purchase Confirmed", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton backBtn = button("\u2190 Back to Home", new Color(0x6C757D));
        backBtn.addActionListener(e -> go(PAGE_HOME));

        panel.add(Box.createVerticalStrut(50));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(formRow("Adults  ($" + String.format("%.2f", ADULT_PRICE) + ")", adult));
        panel.add(Box.createVerticalStrut(12));
        panel.add(formRow("Children ($" + String.format("%.2f", CHILD_PRICE) + ")", child));
        panel.add(Box.createVerticalStrut(20));
        panel.add(total);
        panel.add(Box.createVerticalStrut(20));
        panel.add(center(buyBtn));
        panel.add(Box.createVerticalStrut(8));
        panel.add(status);
        panel.add(Box.createVerticalStrut(14));
        panel.add(center(backBtn));
        panel.add(Box.createVerticalGlue());

        return panel;
    }


    // Helpers
    private JPanel aquariumPanel() {
        JPanel p = new JPanel();
        p.setBackground(DEEP_BLUE);
        return p;
    }

    private JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(0xB8E0D2));
        lbl.setPreferredSize(new Dimension(140, 28));
        row.add(lbl);
        row.add(field);
        return row;
    }

    private JPanel center(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(220, 40));
        return b;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(new Font("SansSerif", Font.PLAIN, 14));
        s.setPreferredSize(new Dimension(120, 30));
    }

    private boolean phoneHasLetters(String s) {
        for (char c : s.toCharArray())
            if (Character.isLetter(c)) return true;
        return false;
    }

    private boolean hasCapital(String s) {
        for (char c : s.toCharArray())
            if (Character.isUpperCase(c)) return true;
        return false;
    }

    private boolean hasDigit(String s) {
        for (char c : s.toCharArray())
            if (Character.isDigit(c)) return true;
        return false;
    }

    private boolean isAdult(String birthDate) {
        try {
            LocalDate dob = LocalDate.parse(birthDate.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return Period.between(dob, LocalDate.now()).getYears() >= ADULT_AGE;
        } catch (Exception e) {
            return false;
        }
    }


    // Guest
    static class Guest {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String birthDate;
        private final String phone;
        private final String username;

        Guest(String firstName, String lastName, String email,
              String birthDate, String phone, String username) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.birthDate = birthDate;
            this.phone = phone;
            this.username = username;
        }
//Getters
        String getFirstName() { return firstName; }
        String getLastName() { return lastName; }
        String getEmail() { return email; }
        String getBirthDate() { return birthDate; }
        String getPhone() { return phone; }
        String getUsername() { return username; }
    }
}
