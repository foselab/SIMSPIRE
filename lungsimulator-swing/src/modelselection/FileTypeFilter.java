package modelselection;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Manages accepted file types
 */
public class FileTypeFilter extends FileFilter{
	/**
	 * Accepted extension
	 */
	private final transient String extension;
	
	/**
	 * Extension description
	 */
    private final transient String description;
     
    /**
     * Filter set up
     * @param extension accepted extension
     * @param description extension description
     */
    public FileTypeFilter(final String extension, final String description) {
        this.extension = extension;
        this.description = description;
    }
     
    @Override
    public boolean accept(final File file) {
        if (file.isDirectory()) {
            return true;
        }
        return file.getName().toLowerCase().endsWith(extension);
    }
     
    @Override
    public String getDescription() {
        return description + String.format(" (*%s)", extension);
    }
}
