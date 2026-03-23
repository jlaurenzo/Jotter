import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Main extends JFrame {

    // ── Palette (Apple Notes warm cream + Google Keep accent colors) ──
    private static final Color BG_SIDEBAR   = new Color(0xF7F3EE);
    private static final Color BG_MAIN      = new Color(0xFFFDF8);
    private static final Color ACCENT       = new Color(0xF5A623);
    private static final Color ACCENT_DARK  = new Color(0xD4881A);
    private static final Color TEXT_PRIMARY = new Color(0x1C1C1E);
    private static final Color TEXT_MUTED   = new Color(0x8E8E93);
    private static final Color DIVIDER      = new Color(0xE5DDD4);
    private static final Color CARD_BG      = Color.WHITE;
    private static final Color CARD_HOVER   = new Color(0xFFF8EE);
    private static final Color BTN_DELETE   = new Color(0xFF3B30);
    private static final Color BTN_EDIT     = new Color(0x30A9DE);

    // Keep-style note colors
    private static final Color[] NOTE_COLORS = {
            new Color(0xFFF9C4), new Color(0xC8E6C9), new Color(0xBBDEFB),
            new Color(0xF8BBD0), new Color(0xE1BEE7), new Color(0xFFCCBC)
    };

    private NoteManager noteManager = new NoteManager();
    private JPanel notesGrid;
    private JTextField searchField;
    private JLabel noteCountLabel;

    public Main() {
        setTitle("Jotter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(780, 500));
        setLocationRelativeTo(null);
        setBackground(BG_MAIN);

        // Use system look for native decorations
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ── LEFT SIDEBAR ──────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER));

        // App logo / title
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18));
        logoPanel.setBackground(BG_SIDEBAR);
        logoPanel.setOpaque(true);

        JLabel logo = new JLabel("✏ Jotter");
        logo.setFont(new Font("Georgia", Font.BOLD, 22));
        logo.setForeground(TEXT_PRIMARY);
        logoPanel.add(logo);

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Nav items
        JPanel nav = new JPanel();
        nav.setBackground(BG_SIDEBAR);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(8, 12, 8, 12));

        String[] navItems = {"📋  All Notes", "⭐  Favorites", "🗑  Trash"};
        for (int i = 0; i < navItems.length; i++) {
            JLabel item = new JLabel(navItems[i]);
            item.setFont(new Font("SansSerif", i == 0 ? Font.BOLD : Font.PLAIN, 14));
            item.setForeground(i == 0 ? ACCENT_DARK : TEXT_MUTED);
            item.setBorder(new EmptyBorder(10, 8, 10, 8));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            nav.add(item);
        }

        sidebar.add(nav, BorderLayout.CENTER);

        // Note count footer
        noteCountLabel = new JLabel("0 notes");
        noteCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteCountLabel.setForeground(TEXT_MUTED);
        noteCountLabel.setBorder(new EmptyBorder(0, 20, 18, 0));
        sidebar.add(noteCountLabel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);

        // ── MAIN CONTENT ──────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_MAIN);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_MAIN);
        topBar.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                new EmptyBorder(14, 24, 14, 24)
        ));

        // Search field
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBackground(new Color(0xF0EBE3));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(new CompoundBorder(
                new RoundBorder(20, DIVIDER),
                new EmptyBorder(7, 16, 7, 16)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "🔍  Search notes…");
        searchField.setMaximumSize(new Dimension(300, 36));
        searchField.setPreferredSize(new Dimension(260, 36));
        topBar.add(searchField, BorderLayout.WEST);

        // "+ New Note" button
        JButton newNoteBtn = new PillButton("+ New Note", ACCENT, Color.WHITE);
        newNoteBtn.addActionListener(e -> showCreateNoteDialog());
        topBar.add(newNoteBtn, BorderLayout.EAST);

        content.add(topBar, BorderLayout.NORTH);

        // Notes grid (scrollable)
        notesGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));
        notesGrid.setBackground(BG_MAIN);
        notesGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(notesGrid);
        scroll.setBorder(null);
        scroll.setBackground(BG_MAIN);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        content.add(scroll, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        refreshNotes();
    }

    // ── Refresh notes grid ─────────────────────────────────────────────
    private void refreshNotes() {
        notesGrid.removeAll();
        java.util.List<Note> notes = noteManager.ReadNote();
        int colorIdx = 0;

        if (notes.isEmpty()) {
            JLabel empty = new JLabel("No notes yet. Tap \"+ New Note\" to begin!");
            empty.setFont(new Font("Georgia", Font.ITALIC, 16));
            empty.setForeground(TEXT_MUTED);
            notesGrid.setLayout(new GridBagLayout());
            notesGrid.add(empty);
        } else {
            notesGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 16, 16));
            for (Note note : notes) {
                notesGrid.add(buildNoteCard(note, NOTE_COLORS[colorIdx % NOTE_COLORS.length]));
                colorIdx++;
            }
        }

        noteCountLabel.setText(notes.size() + (notes.size() == 1 ? " note" : " notes"));
        notesGrid.revalidate();
        notesGrid.repaint();
    }

    // ── Note card ──────────────────────────────────────────────────────
    private JPanel buildNoteCard(Note note, Color cardColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setBackground(cardColor);
        card.setPreferredSize(new Dimension(210, 180));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 10, 16));

        // Shadow effect via wrapper
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // soft shadow
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 8 * (5 - i)));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 18, 18);
                }
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 4, 4, 4));
        wrapper.setPreferredSize(new Dimension(218, 188));
        wrapper.add(card, BorderLayout.CENTER);

        // Title
        JLabel titleLbl = new JLabel(truncate(note.getTitle(), 22));
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 15));
        titleLbl.setForeground(TEXT_PRIMARY);

        // Body preview
        JTextArea bodyPreview = new JTextArea(truncate(note.getContent(), 120));
        bodyPreview.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bodyPreview.setForeground(new Color(0x3C3C43));
        bodyPreview.setBackground(new Color(0, 0, 0, 0));
        bodyPreview.setOpaque(false);
        bodyPreview.setEditable(false);
        bodyPreview.setLineWrap(true);
        bodyPreview.setWrapStyleWord(true);
        bodyPreview.setFocusable(false);
        bodyPreview.setBorder(null);
        bodyPreview.setRows(4);

        // Date / meta
        JLabel dateLbl = new JLabel(note.getDate() != null ? note.getDate() : "");
        dateLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dateLbl.setForeground(TEXT_MUTED);

        // Action buttons row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        actions.setOpaque(false);

        JButton readBtn  = iconButton("👁", "Read",   new Color(0x34C759));
        JButton editBtn  = iconButton("✏", "Edit",   BTN_EDIT);
        JButton delBtn   = iconButton("🗑", "Delete", BTN_DELETE);

        readBtn.addActionListener(e -> showReadDialog(note));
        editBtn.addActionListener(e -> showEditDialog(note));
        delBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + note.getTitle() + "\"?", "Delete Note",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                noteManager.deleteNote(note.getId());
                refreshNotes();
            }
        });

        actions.add(readBtn);
        actions.add(editBtn);
        actions.add(delBtn);

        // Top: title + date side by side
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(titleLbl, BorderLayout.CENTER);
        topRow.add(dateLbl, BorderLayout.EAST);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(actions, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);
        card.add(bodyPreview, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

        // Hover glow
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { card.setBackground(cardColor.darker()); }
            public void mouseExited(MouseEvent e)  { card.setBackground(cardColor); }
        });

        return wrapper;
    }

    // ── Dialogs ────────────────────────────────────────────────────────
    private void showCreateNoteDialog() {
        JDialog dlg = styledDialog("New Note", 460, 340);

        JTextField titleField = styledTextField("Title");
        JTextArea bodyField   = styledTextArea("Start writing…");

        JButton saveBtn = new PillButton("Save Note", ACCENT, Color.WHITE);
        saveBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String b = bodyField.getText().trim();
            if (t.isEmpty()) { titleField.setBorder(new RoundBorder(10, BTN_DELETE)); return; }
            noteManager.createNote(t, b);
            refreshNotes();
            dlg.dispose();
        });

        JButton cancelBtn = new PillButton("Cancel", DIVIDER, TEXT_MUTED);
        cancelBtn.addActionListener(e -> dlg.dispose());

        layoutDialog(dlg, "New Note", titleField, bodyField, saveBtn, cancelBtn);
        dlg.setVisible(true);
    }

    private void showReadDialog(Note note) {
        JDialog dlg = styledDialog("Read Note", 460, 340);

        JTextField titleField = styledTextField(note.getTitle());
        titleField.setText(note.getTitle());
        titleField.setEditable(false);
        titleField.setBackground(new Color(0xF5F0EA));

        JTextArea bodyField = styledTextArea("");
        bodyField.setText(note.getContent());
        bodyField.setEditable(false);
        bodyField.setBackground(new Color(0xF5F0EA));

        JButton closeBtn = new PillButton("Close", ACCENT, Color.WHITE);
        closeBtn.addActionListener(e -> dlg.dispose());

        layoutDialog(dlg, note.getTitle(), titleField, bodyField, closeBtn, null);
        dlg.setVisible(true);
    }

    private void showEditDialog(Note note) {
        JDialog dlg = styledDialog("Edit Note", 460, 340);

        JTextField titleField = styledTextField("Title");
        titleField.setText(note.getTitle());

        JTextArea bodyField = styledTextArea("");
        bodyField.setText(note.getContent());

        JButton saveBtn = new PillButton("Save Changes", ACCENT, Color.WHITE);
        saveBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String b = bodyField.getText().trim();
            if (t.isEmpty()) return;
            noteManager.updateNote(note.getId(), t, b);
            refreshNotes();
            dlg.dispose();
        });

        JButton cancelBtn = new PillButton("Cancel", DIVIDER, TEXT_MUTED);
        cancelBtn.addActionListener(e -> dlg.dispose());

        layoutDialog(dlg, "Edit Note", titleField, bodyField, saveBtn, cancelBtn);
        dlg.setVisible(true);
    }

    // ── Dialog helpers ─────────────────────────────────────────────────
    private JDialog styledDialog(String title, int w, int h) {
        JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(w, h);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(BG_MAIN);
        dlg.setResizable(false);
        return dlg;
    }

    private void layoutDialog(JDialog dlg, String heading,
                              JTextField titleF, JTextArea bodyF,
                              JButton primary, JButton secondary) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel h = new JLabel(heading);
        h.setFont(new Font("Georgia", Font.BOLD, 18));
        h.setForeground(TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(bodyF);
        scroll.setBorder(new RoundBorder(10, DIVIDER));
        scroll.setBackground(BG_MAIN);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(BG_MAIN);
        if (secondary != null) btnRow.add(secondary);
        btnRow.add(primary);

        p.add(h, BorderLayout.NORTH);
        JPanel fields = new JPanel(new BorderLayout(0, 10));
        fields.setBackground(BG_MAIN);
        fields.add(titleF, BorderLayout.NORTH);
        fields.add(scroll, BorderLayout.CENTER);
        p.add(fields, BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);

        dlg.setContentPane(p);
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Georgia", Font.BOLD, 15));
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(new RoundBorder(10, DIVIDER), new EmptyBorder(8, 12, 8, 12)));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    private JTextArea styledTextArea(String placeholder) {
        JTextArea a = new JTextArea();
        a.setFont(new Font("SansSerif", Font.PLAIN, 14));
        a.setForeground(TEXT_PRIMARY);
        a.setBackground(Color.WHITE);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(10, 12, 10, 12));
        return a;
    }

    // ── Utility ────────────────────────────────────────────────────────
    private JButton iconButton(String icon, String tip, Color color) {
        JButton btn = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setToolTipText(tip);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    // ── Inner classes ──────────────────────────────────────────────────

    /** Pill-shaped button */
    static class PillButton extends JButton {
        private final Color bg, fg;
        PillButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg; this.fg = fg;
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setForeground(fg);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 22, 9, 22));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isRollover() ? bg.darker() : bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded border */
    static class RoundBorder extends AbstractBorder {
        private final int radius; private final Color color;
        RoundBorder(int r, Color c) { radius = r; color = c; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
    }

    /** Flow layout that wraps */
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right - hgap * 2;
                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0, rowHeight = 0;
                for (Component m : target.getComponents()) {
                    if (!m.isVisible()) continue;
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        dim.width = Math.max(dim.width, rowWidth);
                        dim.height += rowHeight + vgap;
                        rowWidth = 0; rowHeight = 0;
                    }
                    rowWidth += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
                dim.width = Math.max(dim.width, rowWidth);
                dim.height += rowHeight + insets.top + insets.bottom + vgap * 2;
                return dim;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}