import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {
    private final JFrame frame;
    private final JTextArea codeTextArea;
    private final JTextPane errorTextPane;
    private final JTextPane treeTextPane;
    private final StyledDocument errorDoc;
    private final StyledDocument treeDoc;
    private final SimpleAttributeSet errorStyle;
    private final SimpleAttributeSet successStyle;
    private final SimpleAttributeSet treeStyle;
    private final Lexer lexer;
    private final Parser parser;
    private boolean isProcessing = false;
    private final Timer debounceTimer;
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color CARET_COLOR = new Color(255, 255, 255);
    private static final Color ERROR_COLOR = new Color(255, 50, 50);
    private static final Color SUCCESS_COLOR = new Color(100, 255, 100);
    private static final Color TREE_COLOR = new Color(180, 250, 180);
    private static final int ERROR_PANE_HEIGHT = 150;

    public Main() {
        this.lexer = new Lexer();
        this.parser = new Parser(this);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        DisplayMode mode = defaultScreen.getDisplayMode();
        int width = mode.getWidth();
        int height = mode.getHeight();

        // Main frame setup
        frame = new JFrame("Yup++");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Code editor area setup
        codeTextArea = new JTextArea();
        codeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 40));
        codeTextArea.setBackground(BACKGROUND_COLOR);
        codeTextArea.setForeground(TEXT_COLOR);
        codeTextArea.setCaretColor(CARET_COLOR);
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
        codeScrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        codeScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Error display area setup
        errorTextPane = new JTextPane();
        errorTextPane.setEditable(false);
        errorTextPane.setBackground(new Color(30, 30, 30));
        errorTextPane.setForeground(ERROR_COLOR);
        errorDoc = errorTextPane.getStyledDocument();

        // Text styles
        errorStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(errorStyle, ERROR_COLOR);
        StyleConstants.setBold(errorStyle, true);
        StyleConstants.setFontFamily(errorStyle, "Monospaced");
        StyleConstants.setFontSize(errorStyle, 16);

        successStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(successStyle, SUCCESS_COLOR);
        StyleConstants.setBold(successStyle, true);
        StyleConstants.setFontFamily(successStyle, "Monospaced");
        StyleConstants.setFontSize(successStyle, 16);

        // Error scroll pane
        JScrollPane errorScrollPane = new JScrollPane(errorTextPane);
        errorScrollPane.getViewport().setBackground(new Color(30, 30, 30));
        errorScrollPane.setBorder(BorderFactory.createEmptyBorder());
        errorScrollPane.setPreferredSize(new Dimension(frame.getWidth(), ERROR_PANE_HEIGHT));

        // Tree display area setup
        treeTextPane = new JTextPane();
        treeTextPane.setEditable(false);
        treeTextPane.setBackground(new Color(25, 25, 25));
        treeDoc = treeTextPane.getStyledDocument();

        // Tree text style
        treeStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(treeStyle, TREE_COLOR);
        StyleConstants.setFontFamily(treeStyle, "Monospaced");
        StyleConstants.setFontSize(treeStyle, 16);

        // Tree scroll pane
        JScrollPane treeScrollPane = new JScrollPane(treeTextPane);
        treeScrollPane.getViewport().setBackground(new Color(25, 25, 25));
        treeScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create vertical split for code and error areas
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScrollPane, errorScrollPane);
        verticalSplit.setDividerLocation(frame.getHeight() - ERROR_PANE_HEIGHT);
        verticalSplit.setResizeWeight(1.0); // Give more weight to the code area when resizing
        verticalSplit.setBorder(null);
        verticalSplit.setDividerSize(4);

        // Create horizontal split for code+error and tree areas
        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, verticalSplit, treeScrollPane);
        horizontalSplit.setDividerLocation(frame.getWidth() * 2 / 3);
        horizontalSplit.setResizeWeight(0.7); // Give more weight to the code+error area
        horizontalSplit.setBorder(null);
        horizontalSplit.setDividerSize(4);

        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.add(horizontalSplit);

        debounceTimer = new Timer(2000, e -> {
            processFullText();
        });
        debounceTimer.setRepeats(false);

        codeTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }
        });

        clearErrors();
        clearTreeDisplay();
    }

    private void processFullText() {
        if (isProcessing) return;
        isProcessing = true;

        SwingUtilities.invokeLater(() -> {
            clearErrors();
            clearTreeDisplay();
        });

        try {
            String text = codeTextArea.getText();
            if (text == null || text.isEmpty()) {
                return;
            }

            lexer.updateWithString(text);
            if (lexer.getTokens() != null && !lexer.getTokens().isEmpty()) {
                parser.setTokens(lexer.getTokens());
                parser.update();

                ArrayList<String> errors = parser.getErrors();

                final boolean hasErrors = (errors != null && !errors.isEmpty());

                SwingUtilities.invokeLater(() -> {
                    if (hasErrors) {
                        displayErrors(errors);
                    } else {
                        displaySuccessMessage();
                        parser.printParseTree();
                    }
                });
            }
        } catch (Exception e) {
            final String errorMsg = STR."Error during processing: \{e.getMessage()}";
            System.err.println(errorMsg);
            SwingUtilities.invokeLater(() -> {
                ArrayList<String> errors = new ArrayList<>();
                errors.add(errorMsg);
                displayErrors(errors);
            });
        } finally {
            isProcessing = false;
        }
    }

    public void displaySuccessMessage() {
        try {
            clearErrors();
            errorDoc.insertString(0, "No errors - code is valid", successStyle);

            errorTextPane.repaint();
            errorTextPane.getParent().repaint();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void displayErrors(ArrayList<String> errors) {
        try {
            if (errors == null || errors.isEmpty()) {
                return;
            }

            errorDoc.insertString(0, "Parsing Errors:\n\n", errorStyle);

            for (String error : errors) {
                errorDoc.insertString(errorDoc.getLength(), STR."• \{error}\n", errorStyle);
            }

            errorTextPane.repaint();
            errorTextPane.getParent().repaint();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void addToDisplayTree(String addition) {
        SwingUtilities.invokeLater(() -> {
            try {
                treeDoc.insertString(treeDoc.getLength(), addition, treeStyle);
                treeTextPane.repaint();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public void clearErrors() {
        try {
            if (errorDoc.getLength() > 0) {
                errorDoc.remove(0, errorDoc.getLength());
                errorTextPane.repaint();
                errorTextPane.getParent().repaint();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    public void clearTreeDisplay() {
        try {
            if (treeDoc.getLength() > 0) {
                treeDoc.remove(0, treeDoc.getLength());

                treeTextPane.repaint();
                treeTextPane.getParent().repaint();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainApp = new Main();
            SplashScreen splash = new SplashScreen(mainApp, 2500);
            splash.showSplash();

        });
    }
}