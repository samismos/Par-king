package Parking;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ZoneSelectionDialog extends TitleAreaDialog {

	private Combo comboZone;
	private boolean status;
	private parkingLot lot;
	private int zoneID;
	boolean selectedValue;

	public ZoneSelectionDialog(Shell parentShell, parkingLot lot) {
		super(parentShell);
		this.lot = lot;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Zone selection");
		setMessage("The action selected will affect all spots of the selected zone.", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		createSelectZone(container);
		createSelectAction(container);

		return area;
	}

	private void createSelectZone(Composite container) {
		Label lbtSelectZone = new Label(container, SWT.NONE);
		lbtSelectZone.setText("Select Zone");
		
		Combo comboSelectZone= new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		comboSelectZone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboSelectZone.setVisibleItemCount(8);
		
		for(int i=0; i < lot.getNumberOfZones(); i++) {
			String str = Character.toString(lot.getZone(i).getZoneLetter());
			comboSelectZone.add(str);
		}
		comboSelectZone.select(0);
		comboZone = comboSelectZone;
	}

	private void createSelectAction(Composite container) {
		Label lbtSelectZone = new Label(container, SWT.NONE);
		lbtSelectZone.setText("Set status");

		container.setLayout(new GridLayout(1, false));

		Button occupiedButton = new Button(container, SWT.RADIO);
		occupiedButton.setText("Occupied");

		Button freeButton = new Button(container, SWT.RADIO);
		freeButton.setText("Free");
		
		occupiedButton.addSelectionListener((SelectionListener) new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedValue = false; // Set selected value to false if occupiedButton is selected
			}
		});
		freeButton.addSelectionListener((SelectionListener) new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedValue = true; // Set selected value to true if freeButton is selected
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		zoneID = comboZone.getSelectionIndex(); 
		status = selectedValue;
	}

	@Override
	protected void okPressed() {
		saveInput(); // save selections as soon as dialog is exited
		super.okPressed();
	}

	public int getZoneID() {
		return zoneID;
	}

	public boolean getStatus() {
		return status;
	}
}