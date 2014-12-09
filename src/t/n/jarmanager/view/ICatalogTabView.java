package t.n.jarmanager.view;

import java.util.List;

public interface ICatalogTabView {
	public abstract void setEnabledJarSearchNextButton(final boolean b);
	public abstract void setEnabledJarSearchPrevButton(final boolean b);
	List<Integer> convertToViewIndexInCatalog(List<Integer> foundJarnameList);
	void clearJarFilenameSearch();
	void changeSelectionCatalogTable(Integer index, int colJarFilename,	boolean b1, boolean b2);

}
