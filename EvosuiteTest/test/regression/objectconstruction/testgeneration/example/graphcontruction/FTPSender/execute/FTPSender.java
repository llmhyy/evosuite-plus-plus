package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.Vector;

public class FTPSender {
	private XBUSSystem mDestination = null;

	private FTPConnection mFTPConnection = null;

	private FileSenderConfiguration mConfiguration = null;

	private String mWorkDir = null;

	private String mFileName = null;

	private String mTmpFileName = null;

	private String mRenameFileName = null;

	private boolean senderExecuted = false;

	public String execute(String function, String callData) throws XException {
		this.senderExecuted = true;
		this.mConfiguration = new FileSenderConfiguration(this.mDestination);
		this.mWorkDir = FTPConnection.getWorkingDirectory(this.mConfiguration.getFileNames()[0]);
		this.mFileName = FTPConnection.getFileName(this.mConfiguration.getFileNames()[0]);
		this.mTmpFileName = String.valueOf(this.mFileName) + "." + "xTmp";
		this.mRenameFileName = String.valueOf(this.mFileName) + Constants.getDateAsString();
		if (this.mConfiguration.getResolution().equals("Error")
				&& this.mFTPConnection.existsFile(this.mWorkDir, this.mFileName)) {
			Vector<String[]> params = new Vector(1);
			params.add(this.mConfiguration.getFileNames());
			throw new XException("E", "01", "010", "10", params);
		}
		if (this.mFTPConnection.existsFile(this.mWorkDir, this.mTmpFileName))
			this.mFTPConnection.delete(this.mWorkDir, this.mTmpFileName);
		if (this.mConfiguration.getResolution().equals("Append")
				&& this.mFTPConnection.existsFile(this.mWorkDir, this.mFileName)) {
			StringBuffer currentData = new StringBuffer(
					this.mFTPConnection.retrieveFile(this.mWorkDir, this.mFileName, this.mConfiguration.getEncoding()));
			if (currentData.length() > 0)
				if (currentData.substring(currentData.length() - 1).getBytes()[0] != 10)
					currentData.append(Constants.LINE_SEPERATOR);
			currentData.append(callData);
			this.mFTPConnection.storeFile(currentData.toString(), this.mWorkDir, this.mTmpFileName,
					this.mConfiguration.getEncoding());
		} else {
			this.mFTPConnection.storeFile(callData, this.mWorkDir, this.mTmpFileName,
					this.mConfiguration.getEncoding());
		}
		return null;
	}
}
