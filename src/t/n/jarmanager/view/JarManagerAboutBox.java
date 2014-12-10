/*
* Copyright 2008 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 * * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * JarManagerAboutBox.java
 */

package t.n.jarmanager.view;

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.awt.Component;

import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;

import t.n.jarmanager.util.MessageUtil;

@SuppressWarnings("serial")
public class JarManagerAboutBox extends javax.swing.JDialog {

    public JarManagerAboutBox(java.awt.Frame parent) {
        super(parent);
        initComponents();
        getRootPane().setDefaultButton(closeButton);
    }

    @Action public void closeAboutBox() {
        dispose();
    }

    private void initComponents() {

        closeButton = new javax.swing.JButton();
        javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
        javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
        Component appHomepageLabel;
        javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();


        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        ApplicationContext context = org.jdesktop.application.Application.getInstance(t.n.jarmanager.JarManagerApp.class).getContext();
        javax.swing.ActionMap actionMap = context.getActionMap(JarManagerAboutBox.class, this);
        closeButton.setAction(actionMap.get("closeAboutBox")); // NOI18N

        setModal(true);
        setName("aboutBox"); // NOI18N
        setResizable(false);

        org.jdesktop.application.ResourceMap resourceMap = context.getResourceMap(JarManagerAboutBox.class);

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+4));
        String titleTxt = MessageUtil.getMessage(MSG_GLOBAL, "ApplicationTitle");
		appTitleLabel.setText(titleTxt); // NOI18N
		setTitle(titleTxt); // NOI18N
        appTitleLabel.setName("appTitleLabel"); // NOI18N

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
        String versionTxt = MessageUtil.getMessage(MSG_GLOBAL, "versionLabel.text");
		versionLabel.setText(versionTxt); // NOI18N
        versionLabel.setName("versionLabel"); // NOI18N

        String verstionTxt2 = MessageUtil.getMessage(MSG_GLOBAL, "Application.version");
		appVersionLabel.setText(verstionTxt2); // NOI18N
        appVersionLabel.setName("appVersionLabel"); // NOI18N

        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | java.awt.Font.BOLD));
        String venderTxt = MessageUtil.getMessage(MSG_GLOBAL, "vendorLabel.text");
		vendorLabel.setText(venderTxt); // NOI18N
        vendorLabel.setName("vendorLabel"); // NOI18N

        appVendorLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "Application.vendor")); // NOI18N
        appVendorLabel.setName("appVendorLabel"); // NOI18N

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "homepageLabel.text")); // NOI18N
        homepageLabel.setName("homepageLabel"); // NOI18N

//        appHomepageLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "Application.homepage")); // NOI18N
        String uri = MessageUtil.getMessage(MSG_GLOBAL, "Application.homepage");
        appHomepageLabel = HyperLinkUtil.genHyperLinkEditor(uri, null);
        appHomepageLabel.setName("appHomepageLabel"); // NOI18N

        appDescLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "appDescLabel.text")); // NOI18N
        appDescLabel.setName("appDescLabel"); // NOI18N

        imageLabel.setIcon(resourceMap.getIcon("about.icon")); // NOI18N
        imageLabel.setName("imageLabel"); // NOI18N

        String closeBtnText = MessageUtil.getMessage(MSG_GLOBAL, "closeButton.text");
		closeButton.setText(closeBtnText); // NOI18N
        closeButton.setName("closeButton"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(imageLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionLabel)
                            .addComponent(vendorLabel)
                            .addComponent(homepageLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appVersionLabel)
                            .addComponent(appVendorLabel)
                            .addComponent(appHomepageLabel)))
                    .addComponent(appTitleLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appDescLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                    .addComponent(closeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appDescLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(appVersionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vendorLabel)
                    .addComponent(appVendorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(homepageLabel)
                    .addComponent(appHomepageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }

    private javax.swing.JButton closeButton;

}
