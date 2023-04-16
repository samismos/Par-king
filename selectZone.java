package Parking;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

public class selectZone extends Dialog {

    private Combo combo;
    private String selectedValue;

    /**
	 * @return the selectedValue
	 */
	public String getSelectedValue() {
		return selectedValue;
	}

	/**
	 * @param selectedValue the selectedValue to set
	 */
	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	public selectZone(Shell parentShell) {
        super(parentShell);
    }

	/**
	 * @return the combo
	 */
	public Combo getCombo() {
		return combo;
	}

	/**
	 * @param combo the combo to set
	 */
	public void setCombo(Combo combo) {
		this.combo = combo;
	}
}


    