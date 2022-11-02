package org.vaadin.example;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import view.SelectModelView;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
@Theme("my-theme")
public class AppShell implements AppShellConfigurator {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		SelectModelView mv = new SelectModelView();
	}
}
