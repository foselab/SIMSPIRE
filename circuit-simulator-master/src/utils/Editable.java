package utils;

public interface Editable {
	EditInfo getEditInfo(int n);

	void setEditValue(int n, EditInfo ei);
}