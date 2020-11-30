package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPConnection {
	private String mName = null;
	private FTPClient mFTPClient = null;
	private boolean mOpen = false;
	private String mHost = null;
	private int mPort = 0;

	private void changeWorkDir(String workDir) throws XException {
		try {
			if (workDir != null)
				changeWorkDirInternal(workDir);
		} catch (Exception e1) {
			try {
				Trace.warn("FTP connection may be gone, will try a second time");
				open(true);
				changeWorkDirInternal(workDir);
			} catch (FTPException e) {
				Vector<String> params = new Vector(1);
				params.add(workDir);
				throw new XException("E", "01", "010", "12", params);
			} catch (IOException e) {
				throw new XException("E", "01", "010", "0");
			}
		}
	}

	private void changeWorkDirInternal(String workDir) throws FTPException, IOException {
		if (!this.mFTPClient.changeWorkingDirectory(workDir))
			throw new FTPException();
	}

	private void open(boolean force) throws XException {
		if (!this.mOpen || force) {
			this.mFTPClient = new FTPClient();
			Configuration config = Configuration.getInstance();
			this.mHost = config.getValue("FTPConnection", this.mName, "Host");
			this.mPort = config.getValueAsIntOptional("FTPConnection", this.mName, "Port");
			try {
				if (this.mPort > 0) {
					this.mFTPClient.connect(this.mHost, this.mPort);
				} else {
					this.mFTPClient.connect(this.mHost);
				}
				int reply = getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					close();
					Vector<String> params = new Vector(2);
					params.add(this.mHost);
					params.add(getReplyString());
					throw new XException("E", "01", "010", "1", params);
				}
				this.mOpen = true;
				String user = config.getValue("FTPConnection", this.mName, "User");
				String password = config.getValueOptional("FTPConnection", this.mName, "Password");
				String account = config.getValueOptional("FTPConnection", this.mName, "Account");
				boolean successful = false;
				if (account == null) {
					successful = this.mFTPClient.login(user, password);
				} else {
					successful = this.mFTPClient.login(user, password, account);
				}
				if (!successful) {
					close();
					Vector<String> params = new Vector(3);
					params.add(this.mHost);
					params.add(user);
					if (account == null)
						throw new XException("E", "01", "010", "2", params);
					params.add(user);
					throw new XException("E", "01", "010", "3", params);
				}
				int fileTypeInt = 0;
				String fileTypeString = config.getValueOptional("FTPConnection", this.mName, "FileType");
				if (fileTypeString == null)
					fileTypeString = "ASCII";
				if (fileTypeString.toUpperCase().equals("ASCII")) {
					fileTypeInt = 0;
				} else if (fileTypeString.toUpperCase().equals("BINARY")) {
					fileTypeInt = 2;
				} else {
					close();
					Vector<String> params = new Vector(1);
					params.add(fileTypeString);
					throw new XException("E", "01", "010", "2", params);
				}
				this.mFTPClient.setFileType(fileTypeInt);
			} catch (XException e) {
				close();
				throw e;
			} catch (SocketException e) {
				close();
				throw new XException("E", "01", "010", "0", e);
			} catch (IOException e) {
				close();
				throw new XException("E", "01", "010", "0", e);
			}
		}
	}

	public void close() {
		if (this.mOpen)
			try {
				if (this.mFTPClient != null)
					this.mFTPClient.disconnect();
			} catch (Exception e) {
				Trace.warn("Cannot disconnect from FTP server for connection " + this.mName);
			}
		this.mOpen = false;
	}

	private int getReplyCode() {
		return this.mFTPClient.getReplyCode();
	}

	private String getReplyString() {
		return this.mFTPClient.getReplyString();
	}

	public boolean existsFile(String workDir, String fileName) throws XException {
		try {
			changeWorkDir(workDir);
			String[] files = this.mFTPClient.listNames(fileName);
			if (files != null && files.length == 1 && fileName.equals(files[0]))
				return true;
			return false;
		} catch (IOException e) {
			throw new XException("E", "01", "010", "0", e);
		}
	}

	public static String getWorkingDirectory(String name) {
		String workDir = null;
		int index = 0;
		if ((index = name.lastIndexOf("/")) >= 0)
			workDir = name.substring(0, index);
		if (workDir != null && !workDir.startsWith("/")) {
			StringBuffer newWorkDir = new StringBuffer(workDir.length() + 1);
			newWorkDir.append("/").append(workDir);
			workDir = newWorkDir.toString();
		}
		return workDir;
	}

	public static String getFileName(String name) throws XException {
		String fileName = null;
		int index = 0;
		int lastPos = name.length() - 1;
		if ((index = name.lastIndexOf("/")) < 0) {
			fileName = name;
		} else if (index < lastPos) {
			fileName = name.substring(index + 1);
		}
		if (fileName == null) {
			Vector<String> params = new Vector(1);
			params.add(name);
			throw new XException("E", "01", "010", "11", params);
		}
		return fileName;
	}

	public void delete(String workDir, String fileName) throws XException {
		if (this.mOpen)
			try {
				changeWorkDir(workDir);
				boolean successful = this.mFTPClient.deleteFile(fileName);
				if (!successful) {
					Vector<String> params = new Vector(2);
					params.add(fileName);
					params.add(getReplyString());
					throw new XException("E", "01", "010", "8", params);
				}
			} catch (IOException e) {
				throw new XException("E", "01", "010", "0", e);
			}
	}

	public String retrieveFile(String workDir, String fileName, String encoding) throws XException {
		try {
			changeWorkDir(workDir);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			boolean successful = this.mFTPClient.retrieveFile(fileName, outStream);
			if (!successful) {
				outStream.close();
				Vector<String> params = new Vector(2);
				params.add(fileName);
				params.add(getReplyString());
				throw new XException("E", "01", "010", "5", params);
			}
			String retString = outStream.toString(encoding);
			outStream.close();
			return retString;
		} catch (Throwable e) {
			throw new XException("E", "01", "010", "0", e);
		}
	}

	public void storeFile(String data, String workDir, String fileName, String encoding) throws XException {
		try {
			changeWorkDir(workDir);
			ByteArrayInputStream inStream = new ByteArrayInputStream(data.getBytes(encoding));
			boolean successful = this.mFTPClient.storeFile(fileName, inStream);
			inStream.close();
			if (!successful) {
				Vector<String> params = new Vector(2);
				params.add(fileName);
				params.add(getReplyString());
				throw new XException("E", "01", "010", "9", params);
			}
		} catch (UnsupportedEncodingException e) {
			throw new XException("E", "01", "010", "0", e);
		} catch (IOException e) {
			throw new XException("E", "01", "010", "0", e);
		}
	}
}
