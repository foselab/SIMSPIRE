package simspire.web.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import lungsimulator.LungSimulator;

/**
 * Custom file uploader
 */
public class FileUploader extends Composite<VerticalLayout> implements HasComponents {

	/**
	 * Creates a component for uploading chosen model files
	 * @param lungSimulator backend access
	 */
	public FileUploader(final LungSimulator lungSimulator) {
		final Label patientModel = new Label("Insert patient model file");
		final Label archModel = new Label("Insert archetype file");
		final Label demoModel = new Label("Insert demographic simspire.web.data model file");

		final MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		final Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload.setMaxFiles(1); // max 1 file

		upload.addSucceededListener(event -> {
			final String fileName = event.getFileName();
			final InputStream inputStream = buffer.getInputStream(fileName);
			try {
				lungSimulator.initCustomPatient(inputStream);
			} catch (FileNotFoundException e) {
				Notification.show("File not found");
			} catch (IOException e) {
				Notification.show("Incorrect file structure");
			}
		});

		upload.addFileRejectedListener(event -> errorNotification(event.getErrorMessage()));

		final MultiFileMemoryBuffer buffer2 = new MultiFileMemoryBuffer();
		final Upload upload2 = new Upload(buffer2);
		upload2.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload2.setMaxFiles(1); // max 1 file

		upload2.addSucceededListener(event -> {
			final String fileName = event.getFileName();
			final InputStream inputStream = buffer2.getInputStream(fileName);
			try {
				lungSimulator.initCustomArchetype(inputStream);
			} catch (FileNotFoundException e) {
				Notification.show("File not found");
			} catch (IOException e) {
				Notification.show("Incorrect file structure");
			}
		});

		upload2.addFileRejectedListener(event -> errorNotification(event.getErrorMessage()));

		final MultiFileMemoryBuffer buffer3 = new MultiFileMemoryBuffer();
		final Upload upload3 = new Upload(buffer3);
		upload3.setAcceptedFileTypes(".yaml"); // only yaml files are valid
		upload3.setMaxFiles(1); // max 1 file

		upload3.addSucceededListener(event -> {
			final String fileName = event.getFileName();
			final InputStream inputStream = buffer3.getInputStream(fileName);
			try {
				lungSimulator.initCustomDemographic(inputStream);
			} catch (FileNotFoundException e) {
				Notification.show("File not found");
			} catch (IOException e) {
				Notification.show("Incorrect file structure");
			}
		});

		upload3.addFileRejectedListener(event -> errorNotification(event.getErrorMessage()));

		add(patientModel, upload, archModel, upload2, demoModel, upload3);
	}

	private void errorNotification(final String errorMessage) {
		final Notification notification = Notification.show(errorMessage, 5000, Notification.Position.MIDDLE);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
	}
}
