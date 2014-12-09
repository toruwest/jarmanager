package t.n.jarmanager.util;

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import t.n.jarmanager.dto.JarFileViewProperty;

public class JarFileUtil {

	private static final Logger logger = Logger.getLogger(JarFileUtil.class);
	private static final String NEW_LINE = "\n";
	private static final int BUF_SIZE = 10240;
	private static DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

	public static JarFileViewProperty getJarFileProperty(File jarFile) {
		return new JarFileViewProperty(jarFile);
	}

	public static JarFileViewProperty getJarFileProperty(File jarFile, String checksum) {
		return new JarFileViewProperty(jarFile, checksum);
	}

	public static String calculateMD5sum(final File jarFile)
			throws FileNotFoundException, IOException {
		String result = null;

		long beginTime = 0, endTime = 0;
		if(logger.isDebugEnabled()) {
			beginTime = System.currentTimeMillis();
			logger.debug("calculateMD5sum() invoked");
		}

		FileInputStream fis = new FileInputStream(jarFile);

		byte[] bytesBuffer = new byte[BUF_SIZE];
		int readCount = -1;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((readCount = fis.read(bytesBuffer)) != -1) {
				md.update(bytesBuffer, 0, readCount);
			}
			result = getMD5Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		if(logger.isDebugEnabled()) {
			endTime = System.currentTimeMillis();
			logger.debug("calculateMD5sum() invoked. " + sdf.format(endTime - beginTime));
		}
		fis.close();

		return result;
	}

	private static String getMD5Hex(byte[] digest) {
		StringBuilder result = new StringBuilder();
		for (byte b : digest) {
			String hexString = Integer.toHexString(b & 0xff);
			if (hexString.length() == 1) {
				result.append("0");
			}
			result.append(hexString);
		}
		return result.toString();
	}

	public static StringBuilder wrapManifestContent(String text) {
		StringBuilder content = new StringBuilder();

		if(text != null && !text.isEmpty()) {
			String headerOfManifest = MessageUtil.getMessage(MSG_GLOBAL, "contentsOfManifest.text");
			String endOfManifest = MessageUtil.getMessage(MSG_GLOBAL, "endOfManifest.text");

			content.append(" ---------- ");
			content.append(headerOfManifest);
			content.append(" --------------");
			content.append(NEW_LINE);
			content.append(text);
			content.append(" ---------- ");
			content.append(endOfManifest);
			content.append(" --------------");
			content.append(NEW_LINE);
		} else {
			String noManifestFound = MessageUtil.getMessage(MSG_GLOBAL, "noManifestFound.text");

			content.append(" ---------- ");
			content.append(noManifestFound);
			content.append(" --------------");
			content.append(NEW_LINE);
		}

		return content;
	}
}
