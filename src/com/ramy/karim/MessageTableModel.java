package com.ramy.karim;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.swing.table.AbstractTableModel;

import utilities.General;

public class MessageTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	Message message;
	private static final long serialVersionUID = 1L;
	private final String[] entetes = { "Expediteur", "Sujet", "Date"," " };
	List<Message> messageList = new ArrayList<Message>(); //

	public MessageTableModel(Message[] messages) {
		setMessages(messages);

	}

	public void setMessages(Message[] messages) {

		for (int i = messages.length - 1; i >= Math.max(0,0); i--) {
//			try {
//				if (!messages[i].isSet(Flags.Flag.SEEN)) {
					messageList.add(messages[i]);
//				}
//			} catch (MessagingException e) {
//				System.out.println("op impossible");
		}
	}

	// Get a message for the specified row.
	public Message getMessage(int row) {
		return (Message) messageList.get(row);
	}

	// Remove a message from the list.
	public void deleteMessage(int row) {
		messageList.remove(row);

		// Fire table row deletion notification to table.
		fireTableRowsDeleted(row, row);
	}

	// Get table's column count.
	public int getColumnCount() {
		return entetes.length;
	}

	// Get a column's name.
	public String getColumnName(int col) {
		return entetes[col];
	}

	// Get table's row count.
	public int getRowCount() {
		return messageList.size();
	}

	// Get value for a specific row and column combination.
	public String getValueAt(int row, int col) {

		try {
			
			switch (col) {
			case 0: // Sender
				if ( getMessage(row).getFrom().length > 0) {
					return General.decodeISO(getMessage(row).getFrom()[0].toString().split(" <")[0]);
				} else {
					return "[vide]";
				}
			case 1: // Subject;
				if (getMessage(row).getSubject() != null && getMessage(row).getSubject().length() > 0) {
					return General.decodeISO(getMessage(row).getSubject());
				} else {
					return "[vide]";
				}
			case 2: // Date

				if (getMessage(row).getSentDate() != null) {
					// message.isSet(Flags.Flag.SEEN);
					return dateToFr(getMessage(row).getSentDate());// dateToFr(date).toString();
				} else {
					return "[vide]";
				}
			case 3:
				return Integer.toString(row);

			}
		} catch (Exception e) {
			// Fail silently.
			return "";
		}

		return "";
	}

	public String dateToFr(Date date) {
		DateFormat dfl = DateFormat.getDateTimeInstance(DateFormat.DATE_FIELD,
				DateFormat.DEFAULT);
		return dfl.format(date);

	}

}
