package regression.objectconstruction.testgeneration.example.graphcontruction.InternalGmHeroFrame.valueChanged;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class InternalGmHeroFrame extends JInternalFrame implements TreeSelectionListener {
	private static final long serialVersionUID = -8647088721012280920L;
	public JTree tree;
	public JButton btnExec;
	public MultiHeroTreeModel model;
	public JRadioButton radAttack;
	public JRadioButton radDefense;
	public String frameName;

	public void valueChanged(TreeSelectionEvent e) {
		if (!this.model.isLeaf(e.getPath().getLastPathComponent())
				|| (e.getPath().getLastPathComponent().getClass().equals(Weapon.class)
						&& this.frameName.equals("Heros"))) {
			this.tree.getSelectionModel().clearSelection();
			this.btnExec.setEnabled(false);
		} else {
			this.btnExec.setEnabled(true);
		}
		if (!this.frameName.equals("Heros"))
			try {
				if (this.tree.getSelectionPath().getLastPathComponent().getClass().equals(Weapon.class)) {
					this.radDefense.setEnabled(true);
					this.radAttack.setEnabled(true);
				} else {
					this.radDefense.setEnabled(false);
					this.radAttack.setEnabled(false);
				}
			} catch (Exception e1) {
				this.radDefense.setEnabled(false);
				this.radAttack.setEnabled(false);
				return;
			}
	}
}
