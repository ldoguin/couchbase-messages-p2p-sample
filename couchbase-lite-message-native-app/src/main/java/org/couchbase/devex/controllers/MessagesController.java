package org.couchbase.devex.controllers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import org.couchbase.devex.models.Message;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.LiveQuery.ChangeEvent;
import com.couchbase.lite.LiveQuery.ChangeListener;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

public class MessagesController extends JFrame {

	public MessagesController(final Database database) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		super("NativeMessageApp");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(true);

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		this.setSize(400, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		JButton sendBtn = new JButton("Send");
		final JTextField inputField = new JTextField(20);
		Vector<String> data = new Vector<String>();
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Messages",data);
		JTable table = new JTable(model);

		inputPanel.add(inputField);
		inputPanel.add(sendBtn);
		panel.add(inputPanel);
		JScrollPane scroll_pane = new JScrollPane(table);
		getContentPane().add(BorderLayout.NORTH, scroll_pane);
		getContentPane().add(BorderLayout.CENTER, panel);

		this.setLocationByPlatform(true);
		inputField.requestFocus();
		this.setVisible(true);
		this.setResizable(false);
		loadData(database, model);
		sendBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Message.createMessage(database, inputField.getText());
				} catch (CouchbaseLiteException e1) {
					throw new RuntimeException(e1);
				}
				System.out.println("creating new message: "
						+ inputField.getText());
			}
		});
	}

	public void loadData(Database database, DefaultTableModel model) {
		LiveQuery lQuery = Message.findAllByDate(database).toLiveQuery();
		lQuery.addChangeListener(new MessagesUpdateListener(model));
		lQuery.start();
	}
}

class MessagesUpdateListener implements ChangeListener {
	DefaultTableModel model;

	public MessagesUpdateListener(DefaultTableModel model) {
		this.model = model;
	}

	@Override
	public void changed(ChangeEvent event) {
		model.getDataVector().removeAllElements();
		QueryEnumerator qe = event.getRows();
		while (qe.hasNext()){
			QueryRow qr = qe.next();
			Object message = qr.getDocument().getProperty("message");
			Vector v = new Vector(1);
			v.add(message);
			model.insertRow(0, v);
			
		}
	}
}