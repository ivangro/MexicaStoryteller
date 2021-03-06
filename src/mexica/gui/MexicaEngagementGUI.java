package mexica.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import mexica.CharacterName;
import mexica.MexicaParameters;
import mexica.MexicaRepository;
import mexica.context.*;
import mexica.core.*;
import mexica.engagement.*;
import mexica.story.*;
import subgraph.Solution;

/**
 * Class to apply an engagement action to the story
 * @author Ivan Guerrero
 */
public class MexicaEngagementGUI extends javax.swing.JDialog {
    private AtomsStore atomStore;
    private Action selectedAction;
    private Story story;
    private int actionsRemaining;
    private ActionStore actionList;
    
    /**
     * Creates new form MexicaEngagementStage
     */
    public MexicaEngagementGUI(java.awt.Frame parent, boolean modal, Story story) {
        super(parent, modal);
        this.story = story;
        atomStore = MexicaRepository.getInstance().getAtoms();
        initComponents();
        initData();
        getContentPane().setBackground(this.getBackground());
        actionsRemaining = MexicaParameters.MAX_ENGAGEMENT_ACTIONS;
        actionList = MexicaRepository.getInstance().getActions();
    }
    
    /**
     * Initializes the required data obtaining all the similar atoms to the given context
     */
    private void initData() {
        final List<AtomAvatarPair> atomList = new ArrayList<>();
        List<Avatar> avatars = story.getAvatarFactory().getActiveCharacters(false, story.getCurrentYear());
        int maxElemsInContext = 0;
        for (Avatar avatar : avatars) {
            if (maxElemsInContext < avatar.getContext().getFacts().size())
                maxElemsInContext = avatar.getContext().getFacts().size();
        }
        
        for (Avatar avatar : avatars) {
            for (Cell cell : atomStore.getCells()) {
                //Filter to remove non representative contexts
                if (MexicaParameters.INCLUDE_NON_REPRESENTATIVE_CONTEXTS || 
                    avatar.getContext().getFacts().size() * 2 >= maxElemsInContext) {
                    Map<Atom, Solution> atoms = cell.getAtoms(avatar.getContext().getFacts());
                    if (!atoms.isEmpty()) {
                        for (Atom atom : atoms.keySet()) {
                            atomList.add(new AtomAvatarPair(atom, avatar, atoms.get(atom)));
                        }
                    }
                }
            }
        }
        listAtoms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listAtoms.setModel(new AbstractListModel<AtomAvatarPair>() {
            @Override
            public int getSize() {
                return atomList.size();
            }

            @Override
            public AtomAvatarPair getElementAt(int i) {
                return atomList.get(i);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblAtoms = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listAtoms = new javax.swing.JList<AtomAvatarPair>();
        lblSimilarity = new javax.swing.JLabel();
        lblAtomContext = new javax.swing.JLabel();
        lblCharacterContext = new javax.swing.JLabel();
        atomPanel = new javax.swing.JPanel();
        contextPanel = new javax.swing.JPanel();
        lblNextActions = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listNextActions = new javax.swing.JList();
        bttnRandomPick = new javax.swing.JButton();
        bttnAddAction = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Engagement Stage");

        lblAtoms.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAtoms.setText("Atoms");

        listAtoms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAtomsValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listAtoms);

        lblSimilarity.setText("(Similarity %)");

        lblAtomContext.setText("Atom's context");

        lblCharacterContext.setText("Character's context:");

        atomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout atomPanelLayout = new javax.swing.GroupLayout(atomPanel);
        atomPanel.setLayout(atomPanelLayout);
        atomPanelLayout.setHorizontalGroup(
            atomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        atomPanelLayout.setVerticalGroup(
            atomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        contextPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout contextPanelLayout = new javax.swing.GroupLayout(contextPanel);
        contextPanel.setLayout(contextPanelLayout);
        contextPanelLayout.setHorizontalGroup(
            contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );
        contextPanelLayout.setVerticalGroup(
            contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );

        lblNextActions.setText("Next actions available for the atom");

        jScrollPane3.setViewportView(listNextActions);

        bttnRandomPick.setText("Random Pick");
        bttnRandomPick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnRandomPickActionPerformed(evt);
            }
        });

        bttnAddAction.setText("Add action");
        bttnAddAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttnAddActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblNextActions, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(11, 11, 11)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(bttnRandomPick)
                                .addComponent(bttnAddAction, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblAtoms)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSimilarity))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(atomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAtomContext, javax.swing.GroupLayout.Alignment.LEADING))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCharacterContext)
                    .addComponent(contextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAtoms)
                    .addComponent(lblSimilarity)
                    .addComponent(lblAtomContext)
                    .addComponent(lblCharacterContext))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(atomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(contextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNextActions)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bttnRandomPick)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bttnAddAction)
                        .addGap(0, 111, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bttnRandomPickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnRandomPickActionPerformed
        int index;
        index = (int)(listAtoms.getModel().getSize() * Math.random());
        listAtoms.setSelectedIndex(index);
        listAtomsValueChanged(null);
        index = (int)(listNextActions.getModel().getSize() * Math.random());
        listNextActions.setSelectedIndex(index);
    }//GEN-LAST:event_bttnRandomPickActionPerformed

    private void bttnAddActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttnAddActionActionPerformed
        try {
            NextAction nextAction = (NextAction)listNextActions.getSelectedValue();
            List<CharacterName> names = new ArrayList<>();
            boolean cancelOperation = false;
            List<String> actionChars = nextAction.getCharacters();

            for (int i=0; i<actionChars.size(); i++) {
                Object selection = JOptionPane.showInputDialog(this, 
                        "Select the character  " + actionChars.get(i).toUpperCase() + 
                        ": ", "Select character", JOptionPane.QUESTION_MESSAGE, null, 
                        story.getAvatarFactory().getAvailableCharacterNames().toArray(), null);
                if (selection instanceof CharacterName)
                    names.add((CharacterName)selection);
                else {
                    cancelOperation = true;
                    break;
                }
            }

            if (!cancelOperation) {
                Action action = (Action)nextAction;
                CharacterName[] characters = names.toArray(new CharacterName[0]);
                List<Action> list = MexicaRepository.getInstance().getActions().getActionList();
                if (list.contains(action)) {
                    int index = list.indexOf(action);
                    action = list.get(index);
                    story.addAction(action, characters);
                    actionsRemaining--;
                    if (actionsRemaining > 0) {
                        JOptionPane.showMessageDialog(this, "Actions remaining: " + actionsRemaining, 
                                                      "Action successfully added to the story", JOptionPane.INFORMATION_MESSAGE);
                        initData();
                        listAtoms.setSelectedIndex(0);
                    }
                    else
                        this.setVisible(false);
                } else if (action.getActionName().toLowerCase().equals("realised")) {
                    List<ActionInstantiated> actions = story.getActions();
                    //Obtains the fact that will be known by the characters
                    Object actionSelected = JOptionPane.showInputDialog(this, 
                            "Select an action", "Select an action", JOptionPane.QUESTION_MESSAGE, null, 
                            actions.toArray(), null);
                    ActionInstantiated selection = 
                            ActionInstantiated.createActionInstantiated((ActionInstantiated)actionSelected);
                    CompositeAction newAction = new CompositeAction(((NextAction)action).getAction(), selection, story.getAvatarFactory());
                    //Adds the characters of the selected action to the character's list
                    story.addAction(newAction, characters);
                    actionsRemaining--;
                    if (actionsRemaining > 0) {
                        JOptionPane.showMessageDialog(this, "Actions remaining: " + actionsRemaining, 
                                                      "Action successfully added to the story", JOptionPane.INFORMATION_MESSAGE);
                        initData();
                        listAtoms.setSelectedIndex(0);
                    }
                    else
                        this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "The action is not implemented, please select another", 
                    "Invalid Action Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (DeadAvatarException dae) {
            JOptionPane.showMessageDialog(this, dae.getMessage(), 
                    "Invalid Character Selection", JOptionPane.ERROR_MESSAGE);
        }  catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(this, "Please select an action to add", 
                    "Select an action first", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "Error during the execution", JOptionPane.ERROR_MESSAGE);
            System.err.println(e);
        }
    }//GEN-LAST:event_bttnAddActionActionPerformed

    private void listAtomsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listAtomsValueChanged
        final AtomAvatarPair atomAvatar = listAtoms.getSelectedValue();
        
        if (atomAvatar != null) {
            //Update the atom's context
            atomPanel.removeAll();
            atomPanel.setLayout(new GridLayout(1,1));
            atomPanel.add(ContextBuilder.getVisualizationElement(atomAvatar.atom));
            atomPanel.revalidate();
            listNextActions.setModel(new AbstractListModel() {
                @Override
                public int getSize() {
                    List<Action> next = atomAvatar.atom.getNextActions();
                    List<Action> list = new ArrayList<>();
                    for (Action action : next) {
                        Action act = actionList.getAction(action.getActionName(), true);
                        if (story.getGuidelines().satisfiesGuidelines(act))
                            list.add(action);
                    }
                    return list.size();
                }

                @Override
                public Object getElementAt(int index) {
                    List<Action> nextActs = atomAvatar.atom.getNextActions();
                    List<Action> list = new ArrayList<>();
                    for (Action action : nextActs) {
                        Action act = actionList.getAction(action.getActionName(), true);
                        if (story.getGuidelines().satisfiesGuidelines(act))
                            list.add(action);
                    }
                    Action act = list.get(index);
                    NextAction next = new NextAction(act);
                    
                    return next;
                }
            });
            
            lblCharacterContext.setText(atomAvatar.avatar + "'s Context");
            contextPanel.removeAll();
            contextPanel.setLayout(new GridLayout(1,1));
            contextPanel.add(ContextBuilder.getVisualizationElement(atomAvatar.avatar.getContext().getFacts()));
            contextPanel.revalidate();
        }
    }//GEN-LAST:event_listAtomsValueChanged

    public Action getSelectedAction() {
        return selectedAction;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel atomPanel;
    private javax.swing.JButton bttnAddAction;
    private javax.swing.JButton bttnRandomPick;
    private javax.swing.JPanel contextPanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAtomContext;
    private javax.swing.JLabel lblAtoms;
    private javax.swing.JLabel lblCharacterContext;
    private javax.swing.JLabel lblNextActions;
    private javax.swing.JLabel lblSimilarity;
    private javax.swing.JList<AtomAvatarPair> listAtoms;
    private javax.swing.JList listNextActions;
    // End of variables declaration//GEN-END:variables
}

class AtomAvatarPair {
    Atom atom;
    Avatar avatar;
    Solution solution;
    
    AtomAvatarPair(Atom atom, Avatar avatar, Solution solution) {
        this.atom = atom;
        this.avatar = avatar;
        this.solution = solution;
    }
    
    @Override
    public String toString() {
        return atom + " (" + solution.getSimilarity() + ")";
    }
}