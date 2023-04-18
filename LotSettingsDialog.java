package Parking;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LotSettingsDialog extends TitleAreaDialog{

	private Combo comboZoneNumber;
	private Combo comboSpotsPerZone;
	private int numZones;
	private int spots_per_zone;

	public LotSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Parking lot settings");
		setMessage("When applied, currently occupied spots will not transfer to new parking lot.", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createSelectZoneNumber(container);
		createSelectSpotsPerZone(container);

		return area;
	}

	private void createSelectZoneNumber(Composite container) {
		// select zone number label
		Label lbtSelectZone = new Label(container, SWT.NONE);
		lbtSelectZone.setText("Select number of zones:");
		// combo box
		Combo comboZoneNumber= new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		comboZoneNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboZoneNumber.setVisibleItemCount(8);
		for(int i=0; i < 26; i++) {
			comboZoneNumber.add(Integer.toString(i+1));
		}
		comboZoneNumber.select(7);
		this.comboZoneNumber = comboZoneNumber;
	}

	private void createSelectSpotsPerZone(Composite container) {
		// select spots per zone label
		Label lbtSelectSpotsPerZone = new Label(container, SWT.NONE);
		lbtSelectSpotsPerZone.setText("Select number of spots per zone:");
		// combo box 
		Combo comboSpotsPerZone= new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		comboSpotsPerZone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboSpotsPerZone.setVisibleItemCount(8);
		for(int i=0; i < 32; i++) {
			comboSpotsPerZone.add(Integer.toString(i+1));
		}
		comboSpotsPerZone.select(9);
		this.comboSpotsPerZone = comboSpotsPerZone;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	
	private void saveInput() {
		numZones = comboZoneNumber.getSelectionIndex()+1; 
		spots_per_zone = comboSpotsPerZone.getSelectionIndex()+1;
	}

	@Override
	protected void okPressed() {
		saveInput(); // save selections as soon as dialog is exited
		super.okPressed();
	}

	public int getZoneNumber() {
		return numZones;
	}

	public int getSpotsPerZone() {
		return spots_per_zone;
	}
}