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
		Label lbtSelectZone = new Label(container, SWT.NONE);
		lbtSelectZone.setText("Select number of zones:");
		//Combo box 
		Combo comboZoneNumber= new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		comboZoneNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboZoneNumber.setVisibleItemCount(8);
		// Add items to the combo box
		for(int i=0; i < 26; i++) {
			comboZoneNumber.add(Integer.toString(i+1));
		}
		comboZoneNumber.select(7);
		this.comboZoneNumber = comboZoneNumber;
	}

	private void createSelectSpotsPerZone(Composite container) {
		Label lbtSelectSpotsPerZone = new Label(container, SWT.NONE);
		lbtSelectSpotsPerZone.setText("Select number of spots per zone:");
		//Combo box 
		Combo comboSpotsPerZone= new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		comboSpotsPerZone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboSpotsPerZone.setVisibleItemCount(8);
		// Add items to the combo box
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
		// save content of combo boxes
		// as soon as the Dialog closes
		numZones = comboZoneNumber.getSelectionIndex()+1; 
		spots_per_zone = comboSpotsPerZone.getSelectionIndex()+1;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public int getZoneNumber() {
		return numZones;
	}

	public int getSpotsPerZone() {
		return spots_per_zone;
	}
}







	