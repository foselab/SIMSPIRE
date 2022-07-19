package utils;
import java.awt.Dialog;

// add plugin.jar to classpath during compilation
import netscape.javascript.JSObject;
import simulator.CirSim;

public class ImportExportAppletDialog extends Dialog implements ImportExportDialog {
	Action type;
	CirSim cframe;
	String circuitDump;

	ImportExportAppletDialog(CirSim f, Action type) throws Exception {
		super(f, (type == Action.EXPORT) ? "Export" : "Import", false);
		this.type = type;
		cframe = f;
		if (cframe.getApplet() == null)
			throw new Exception("Not running as an applet!");
	}

	@Override
	public void setDump(String dump) {
		circuitDump = dump;
	}

	@Override
	public void execute() {
		try {
			JSObject window = JSObject.getWindow(cframe.getApplet());
			if (type == Action.EXPORT) {
				// cframe.setVisible(false);
				window.call("exportCircuit", new Object[] { circuitDump });
			} else {
				// cframe.setVisible(false);
				circuitDump = (String) window.eval("importCircuit()");
				cframe.readSetup(circuitDump);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
