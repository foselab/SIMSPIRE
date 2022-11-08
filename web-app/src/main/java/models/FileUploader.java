package models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import lungsimulator.LungSimulator;

public class FileUploader extends Composite<Component> {
	private LungSimulator lungSimulator;

	public FileUploader(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
	}

	@Override
	protected Component initContent() {
		Label patientModel = new Label("Insert patient model file");
		Label archModel = new Label("Insert archetype file");
		Label demoModel = new Label("Insert demographic data model file");
		
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload.setMaxFiles(1); // max 1 file

		upload.addSucceededListener(event -> {
			String fileName = event.getFileName();
			InputStream inputStream = buffer.getInputStream(fileName);
			try {
				lungSimulator.initCustomPatient(inputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		upload.addFileRejectedListener(event -> {
			String errorMessage = event.getErrorMessage();

			Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		
		MultiFileMemoryBuffer buffer2 = new MultiFileMemoryBuffer();
		Upload upload2 = new Upload(buffer2);
		upload2.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload2.setMaxFiles(1); // max 1 file

		upload2.addSucceededListener(event -> {
			String fileName = event.getFileName();
			InputStream inputStream = buffer2.getInputStream(fileName);
			try {
				lungSimulator.initCustomArchetype(inputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		upload2.addFileRejectedListener(event -> {
			String errorMessage = event.getErrorMessage();

			Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		
		MultiFileMemoryBuffer buffer3 = new MultiFileMemoryBuffer();
		Upload upload3 = new Upload(buffer3);
		upload3.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload3.setMaxFiles(1); // max 1 file

		upload3.addSucceededListener(event -> {
			String fileName = event.getFileName();
			InputStream inputStream = buffer3.getInputStream(fileName);
			try {
				lungSimulator.initCustomDemographic(inputStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		upload3.addFileRejectedListener(event -> {
			String errorMessage = event.getErrorMessage();

			Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		});
		
		return new VerticalLayout(patientModel, upload, archModel, upload2, demoModel, upload3);
	}
}
