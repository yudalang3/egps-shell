package egps2.builtin.modules.voice;

import java.io.Serializable;
import java.util.Optional;

/**
 * BookMarkNode belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BookMarkNode implements Serializable {
	private static final long serialVersionUID = 3212209782237716313L;

	private String name;
	private String content;

	// red flag for the leaf node
	private boolean isFlag = false;
	// example node under Examples bookmark dir.
	private boolean isExample = false;
	// is Leaf node, exclude the category directory and the root
	private boolean isDesignAsLeaf = false;
	// is the category directory node?
	private boolean isCategoryDirectory = false;

	public BookMarkNode() {
	}

	public boolean isCategoryDirectory() {
		return isCategoryDirectory;
	}

	public void setCategoryDirectory(boolean categoryDirectory) {
		isCategoryDirectory = categoryDirectory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Optional<String> getContent() {
		return Optional.ofNullable(content);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isDesignAsLeaf() {
		return isDesignAsLeaf;
	}

	public void setDesignAsLeaf(boolean designAsLeaf) {
		isDesignAsLeaf = designAsLeaf;
	}


	public boolean isExample() {
		return isExample;
	}

	public void setExample(boolean example) {
		if (isCategoryDirectory){
			throw new IllegalStateException("Cannot set example to Category node, bug, please tell developers");
		}
		isExample = example;
	}

}
