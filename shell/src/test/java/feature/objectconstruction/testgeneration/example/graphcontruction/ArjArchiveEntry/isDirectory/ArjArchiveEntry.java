package feature.objectconstruction.testgeneration.example.graphcontruction.ArjArchiveEntry.isDirectory;

public class ArjArchiveEntry {
	private final LocalFileHeader localFileHeader;

	public boolean isDirectory() {
		return (this.localFileHeader.fileType == 3);
	}

	public ArjArchiveEntry() {
		this.localFileHeader = new LocalFileHeader();
	}

	ArjArchiveEntry(LocalFileHeader localFileHeader) {
		this.localFileHeader = localFileHeader;
	}
}
