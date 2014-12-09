package t.n.jarmanager.view;

import static t.n.jarmanager.view.IJarManagerView.MSG_CLASS_JAR_TAB;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import t.n.jarmanager.util.MessageUtil;

@SuppressWarnings("serial")
class BrowserOpenDialog extends JDialog {
	private static final Logger logger = Logger.getLogger(BrowserOpenDialog.class);

	JLabel guideLabel;
	Component hyperLink;
	JCheckBox directBrowserOpenCheckBox;
	JButton closeButton;
	private String uri = null;

	public BrowserOpenDialog(JFrame frame, ItemListener listener, String uri) {
		super(frame, true);
		this.uri = uri;

		setBounds(400, 200, 400, 150);
		setLayout(new GridLayout(2, 1));

		initComponents();
		directBrowserOpenCheckBox.addItemListener(listener);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		guideLabel = new JLabel(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "webpage_dialog_guidance.text"));
		hyperLink = HyperLinkUtil.genHyperLinkEditor(uri, logger);
		closeButton = new JButton();
		directBrowserOpenCheckBox = new JCheckBox();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("Form");

		hyperLink.setName("hyperLink1");
		closeButton.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "close_webpage_dialog.text"));
		closeButton.setName("jButton1");

		directBrowserOpenCheckBox.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "directBrowserOpenCheckBox.text"));
		directBrowserOpenCheckBox.setName("directWebOpenCheckBox");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addContainerGap(72, Short.MAX_VALUE)
								.addComponent(guideLabel).addGap(56, 56, 56))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(72, Short.MAX_VALUE)
								.addComponent(closeButton).addGap(56, 56, 56))
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addComponent(directBrowserOpenCheckBox)
								.addContainerGap(108, Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addComponent(hyperLink)
								.addContainerGap(155, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(guideLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										11, Short.MAX_VALUE)
								.addComponent(hyperLink)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										11, Short.MAX_VALUE)
								.addComponent(directBrowserOpenCheckBox)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(closeButton).addContainerGap()));

		pack();
	}

}
