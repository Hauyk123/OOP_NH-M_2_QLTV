package org.example.UI;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class LoadingDialog extends JDialog {

    private JProgressBar progressBar;
    private JLabel messageLabel;
    private JButton cancelButton;
    private ActionListener cancelListener;

    public LoadingDialog(JFrame parent, String title) {
        super(parent, title, true);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(300, 100);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        messageLabel = new JLabel("Please wait...", SwingConstants.CENTER);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> {
            if (cancelListener != null) {
                cancelListener.actionPerformed(e);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(messageLabel, BorderLayout.NORTH);
        mainPanel.add(progressBar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void addCancelListener(ActionListener listener) {
        this.cancelListener = listener;
    }

    public static void showLoading(JFrame parent, String title, String message, Runnable task) {
        LoadingDialog dialog = new LoadingDialog(parent, title);
        dialog.setMessage(message);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }
}
