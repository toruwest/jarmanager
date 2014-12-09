package t.n.jarmanager.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import t.n.jarmanager.aspect.CheckEDT;
import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.JarFileViewProperty;
import t.n.jarmanager.dto.JarInfo;

public class DBUtilImpl implements IDBUtil {
	private static final Logger logger = Logger.getLogger(DBUtilImpl.class);
	private static final String DEFAULT_DB_NAME = "JarManagerDB";
	private static final int CONTENT_COLUMN_SIZE = 1024;
	private static final int CONTENT_TYPE_MANIFEST = 0;
	private static final int CONTENT_TYPE_NORMALFILE = 1;

	private static Connection conn;

	private static PreparedStatement statementAddJarInfo;
	private static PreparedStatement statementAddClassInfo;
	private static PreparedStatement statementReplaceJarInfo;
	private static PreparedStatement statementSelectAllJarInfo;
	private static PreparedStatement statementSelectSpecifiedJarInfo;
	private static PreparedStatement statementFindClassLoose;
	private static PreparedStatement statementFindClassStrict;
	private static PreparedStatement statementGetClassListByID;
	private static PreparedStatement statementGetClassListByJarFile;
	private static PreparedStatement statementRegistCheck;
	private static PreparedStatement statementGetChecksum;
	private static PreparedStatement statementGetJarFileLastModified;
	private static PreparedStatement statementDeleteJarInfo;
	private static PreparedStatement statementDeleteClassInfo;
	private static PreparedStatement statementGetJarInfoKey;
	private static PreparedStatement statementGetJarContent;
	private static PreparedStatement statementDeleteJarContents;
	private static PreparedStatement statementAddJarContents;
	private boolean isReady;


	private DBUtilImpl(File dataDir) throws Exception {
		initDB(dataDir, DEFAULT_DB_NAME);
	}

	//During unit test, we don't use the default database name to avoid corrupt default database.
	private DBUtilImpl(File dataDir, String dbName) throws Exception {
		if(DEFAULT_DB_NAME.equals(dbName)) {
			throw new IllegalArgumentException("Do not specify the default database name :" + DEFAULT_DB_NAME);
		}
		initDB(dataDir, dbName);
	}

	public static DBUtilImpl getInstance(File dataDir) throws Exception {
		return new DBUtilImpl(dataDir);
	}

	public static DBUtilImpl getInstance(File dataDir, String dbName) throws Exception {
		return new DBUtilImpl(dataDir, dbName);
	}

	private void initDB(File appDataDir, String dbName) throws Exception {
		if(appDataDir == null){
			throw new IllegalStateException("Data directory is not specified:" + appDataDir);
		}
		if( dbName == null || dbName.isEmpty()) {
			throw new IllegalStateException("Database name is not specified:" + dbName);
		}
		if(appDataDir.exists() ) {
			if(!appDataDir.isDirectory()){
				throw new IllegalStateException("Data directory does not exist:" + appDataDir);
			}

			if(logger.isDebugEnabled()){
				logger.debug("property: derby.stream.error.file: " + System.getProperty("derby.stream.error.file"));
				logger.debug("property: derby.stream.error.logSeverityLevel: " + System.getProperty("derby.stream.error.logSeverityLevel"));
				logger.debug("property: derby.language.logStatementText: " + System.getProperty("derby.language.logStatementText"));
			}
		}

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

			//if the specified database does not exist, create it.
			conn = DriverManager.getConnection("jdbc:derby:"+ appDataDir + File.separator + dbName + ";create=true");

			// dropTables();
			if (!isTableExist()) {
				createTables();
			}
			statementRegistCheck = conn
					.prepareStatement("SELECT count(*) from jarinfo where folder = ? and jarname = ?");
			statementAddClassInfo = conn
					.prepareStatement("insert into classinfo (simpleClassname, fullQualifiedClassname, jarid) values (?,?,?)");
			statementAddJarInfo = conn
					.prepareStatement(
							"insert into jarinfo(jarname, folder, jarfilelastmodified, checksum, hasmainclass, issigned, registdate, status)values(?,?,?,?,?,?,?,?)",
							PreparedStatement.RETURN_GENERATED_KEYS);
			statementGetJarInfoKey = conn
					.prepareStatement("select jarid from jarinfo where jarname=? and folder=?");
			statementReplaceJarInfo = conn
					.prepareStatement("update jarinfo set jarname = ?, folder=?, jarfilelastmodified=?, checksum=?, hasmainclass=?, issigned=?, registdate=?, status=? where jarid=?");
			statementSelectAllJarInfo = conn
					.prepareStatement("SELECT jarid, jarname, folder, jarfilelastmodified, checksum, hasmainclass, issigned, registdate, status from jarinfo");
			statementSelectSpecifiedJarInfo = conn
					.prepareStatement("SELECT jarid, jarname, folder, jarfilelastmodified, checksum, hasmainclass, issigned, registdate, status from jarinfo where folder = ? and jarname = ?");
			statementFindClassLoose = conn
					.prepareStatement("select distinct jarname, folder from classinfo join jarinfo on classinfo.jarid = jarinfo.jarid where lower(fullQualifiedClassname) like '%' || ? || '%' ");
			statementFindClassStrict = conn
					.prepareStatement("select distinct jarname, folder from classinfo join jarinfo on classinfo.jarid = jarinfo.jarid where simpleClassname = ?");
			statementGetClassListByID = conn
					.prepareStatement("select fullQualifiedClassname from classinfo where jarid = ?");
			statementGetClassListByJarFile = conn
					.prepareStatement("select fullQualifiedClassname from jarinfo join classinfo on jarinfo.jarid=classinfo.jarid where folder = ? and jarname = ?");
			statementGetChecksum = conn
					.prepareStatement("SELECT checksum from jarinfo where folder = ? and jarname = ?");
			statementGetJarFileLastModified = conn
					.prepareStatement("SELECT jarfilelastmodified from jarinfo where folder = ? and jarname = ?");
			statementDeleteJarInfo = conn
					.prepareStatement("delete from jarinfo where jarname = ? and folder = ?");
			statementDeleteClassInfo = conn
					.prepareStatement("delete from classinfo where jarid = ?");
			statementGetJarContent = conn
					.prepareStatement("SELECT contents from jarcontents join jarinfo on jarinfo.jarid=jarcontents.jarid where folder = ? and jarname= ? and type = ? order by id");
			statementAddJarContents = conn.prepareStatement("insert into jarcontents (jarid, type, contents) values (?, ?, ?)");
			statementDeleteJarContents = conn.prepareStatement("delete from jarcontents where jarid = ?");
			isReady = true;
		} catch (Exception e) {
			logger.log(Level.FATAL, e);
			isReady = false;
			throw e;
		}
	}

	@Override
	public boolean isReady() {
		return isReady;
	}

	private static boolean isTableExist() {
		boolean result = false;
		String sqlCheck1 = "select * from SYS.SYStables where tablename='CLASSINFO'";
		String sqlCheck2 = "select * from SYS.SYStables where tablename='JARINFO'";
		String sqlCheck3 = "select * from SYS.SYStables where tablename='JARCONTENTS'";
		ResultSet rs;
		try {
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlCheck1);
			if (!rs.wasNull() && rs.next()) {
				rs = stmt.executeQuery(sqlCheck2);
				if (!rs.wasNull() && rs.next()) {
					rs = stmt.executeQuery(sqlCheck3);
					if (!rs.wasNull()&& rs.next()) {
						result = true;
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, "table CLASSINFO, JARINFO not found", e);
		}

		return result;
	}

	private static void dropTables() throws SQLException {
		Statement stmt = conn.createStatement();
		String sqlCreate1 = "drop table classinfo";
		stmt.executeUpdate(sqlCreate1);
		String sqlCreate2 = "drop table jarinfo";
		stmt.executeUpdate(sqlCreate2);
		String sqlDrop3 = "drop table jarcontents";
		stmt.executeUpdate(sqlDrop3);
	}

	private static void createTables() throws SQLException {
		// Note: at the end of each string are terminated by space character. if we remove this, the result sql statement will be wrong.
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("create table jarinfo (jarid INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT JARINFO_PK primary key, "
				+ "jarname varchar(1024) not null, folder varchar(1024) not null, jarfilelastmodified TIMESTAMP not null, "
				+ "checksum varchar(128) not null, hasmainclass BOOLEAN not null, issigned BOOLEAN not null, "
				+ "registdate TIMESTAMP not null, status int not null)");
		stmt.executeUpdate("create table jarcontents (id INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT JARCONTENTS_PK PRIMARY KEY,"
				+ "type int not null, contents varchar(" + CONTENT_COLUMN_SIZE + "), "
				+ "jarid int CONSTRAINT jarcontents_foreign_key REFERENCES jarinfo ON DELETE CASCADE ON UPDATE RESTRICT)");
		stmt.executeUpdate("create table classinfo (id INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT CLASSINFO_PK PRIMARY KEY, "
				+ "simpleClassname varchar(256) not null, fullQualifiedClassname varchar(256) not null, "
				+ "jarid int CONSTRAINT classinfo_foreign_key REFERENCES jarinfo ON DELETE CASCADE ON UPDATE RESTRICT)");
		stmt.executeUpdate("create index classinfoIndex on classinfo (simpleClassname)");
	}

	@Override
	@CheckEDT
	public CatalogRegistResult addOrReplaceCatalog(JarFileViewProperty prop,
			long registDate, CatalogEntryStatus status) {
		CatalogRegistResult result = null;
		File jarFile = prop.getJarFile();

		try {
			String folder  = jarFile.getParent();
			String jarname = jarFile.getName();
			long jarFileLastModified = jarFile.lastModified();
			String manifestContents = prop.getManifestContent();
			String otherFileContents = prop.getOtherFilesContent().toString();

			boolean hasMainClass = prop.hasMainClass();
			boolean isSigned = prop.isSigned();
			List<String> classList = prop.getJarFileList();
			String checksum = prop.getMD5Sum();

			// すでに登録されていないかのチェック。
			if (isAlreadyRegisterd(folder, jarname)) {
				//既に存在するレコードを置換
				result = replace(jarname, folder, jarFileLastModified, manifestContents, otherFileContents, checksum, hasMainClass, isSigned, registDate, status, classList);
			} else {
				//新規追加
				result = add(jarname, folder, jarFileLastModified, manifestContents, otherFileContents, checksum, hasMainClass, isSigned, registDate, status, classList);
			}
		} catch(Exception e){
			logger.log(Level.FATAL, "jarFile:" + jarFile.getAbsolutePath(), e);
			result = CatalogRegistResult.FAILED;
		}

		return result;
	}


	private CatalogRegistResult add(String jarname, String folder, long jarFileLastModified, String manifestContents, String otherFileContents,
			String checksum, boolean hasMainClass, boolean isSigned, long registDate, CatalogEntryStatus status, List<String> classList) throws SQLException {
		conn.setAutoCommit(false);
		statementAddJarInfo.setString(1, jarname);
		statementAddJarInfo.setString(2, folder);
		statementAddJarInfo.setTimestamp(3, getSQLDate(jarFileLastModified));
		statementAddJarInfo.setString(4, checksum);
		statementAddJarInfo.setBoolean(5, hasMainClass);
		statementAddJarInfo.setBoolean(6, isSigned);
		statementAddJarInfo.setTimestamp(7, getSQLDate(registDate));
		statementAddJarInfo.setInt(8, status.ordinal());
		statementAddJarInfo.execute();

		ResultSet rs = statementAddJarInfo.getGeneratedKeys();
		int jarId = -1;
		if (rs != null && rs.next()) {
			jarId = rs.getInt(1);//got primary key
			int[] batchResults1 = addJarContents(manifestContents, otherFileContents, jarId);
			if(checkBatchResult(batchResults1)){
				int[] batchResults2 = addClassesList(classList, jarId);
				if(checkBatchResult(batchResults2)){
					conn.commit();
					conn.setAutoCommit(true);
					return CatalogRegistResult.ADDED;
				}
			}

		}
		conn.rollback();
		conn.setAutoCommit(true);

		return CatalogRegistResult.FAILED;
	}

	private CatalogRegistResult replace(String jarname, String folder, long jarFileLastModified, String manifestContents, String normalFileContents, String checksum, boolean hasMainClass, boolean isSigned, long registDate, CatalogEntryStatus status, List<String> classList) throws SQLException {
		conn.setAutoCommit(false);
		statementGetJarInfoKey.setString(1, jarname);
		statementGetJarInfoKey.setString(2, folder);
		ResultSet rs = statementGetJarInfoKey.executeQuery();
		int jarId = -1;
		if (rs.next()) {
			jarId = rs.getInt(1);//got primary key.

			statementReplaceJarInfo.setString(1, jarname);
			statementReplaceJarInfo.setString(2, folder);
			statementReplaceJarInfo.setTimestamp(3, getSQLDate(jarFileLastModified));
			statementReplaceJarInfo.setString(4, checksum);
			statementReplaceJarInfo.setBoolean(5, hasMainClass);
			statementReplaceJarInfo.setBoolean(6, isSigned);
			statementReplaceJarInfo.setTimestamp(7, getSQLDate(registDate));
			statementReplaceJarInfo.setInt(8, status.ordinal());
			statementReplaceJarInfo.setInt(9, jarId);
			statementReplaceJarInfo.execute();

			int count = statementReplaceJarInfo.getUpdateCount();
			if(count == 1) {
				statementDeleteJarContents.setInt(1, jarId);
				statementDeleteJarContents.addBatch();
				int[] batchResults1 = statementDeleteJarContents.executeBatch();
				if(checkBatchResult(batchResults1)){
					int[] batchResults2 = addJarContents(manifestContents,
							normalFileContents, jarId);
					if(checkBatchResult(batchResults2)){
						// if a JAR file is replaced, registered classes are invalid. So we have to remove them before inserting new classes.
						statementDeleteClassInfo.setInt(1, jarId);
						statementDeleteClassInfo.addBatch();
						int[] batchResults3 = statementDeleteClassInfo.executeBatch();
						if(checkBatchResult(batchResults3)){
							addClassesList(classList, jarId);
							if(checkBatchResult(batchResults2)) {
								conn.commit();
								conn.setAutoCommit(true);
								return CatalogRegistResult.REPLACED;
							}
						}
					}
				}
			}
		}
		conn.rollback();
		conn.setAutoCommit(true);
		return CatalogRegistResult.FAILED;
	}

	private int[] addJarContents(String manifestContents,
			String otherFileContents, int jarId) throws SQLException {
		String[] contentArray1 = SliceUtil.slice(manifestContents, CONTENT_COLUMN_SIZE);
		for(int i = 0; i < contentArray1.length; i++) {
			statementAddJarContents.setInt(1, jarId);
			statementAddJarContents.setInt(2, CONTENT_TYPE_MANIFEST);
			statementAddJarContents.setString(3, contentArray1[i]);
			statementAddJarContents.addBatch();
		}
		String[] contentArray2 = SliceUtil.slice(otherFileContents, CONTENT_COLUMN_SIZE);
		for(int i = 0; i < contentArray2.length; i++) {
			statementAddJarContents.setInt(1, jarId);
			statementAddJarContents.setInt(2, CONTENT_TYPE_NORMALFILE);
			statementAddJarContents.setString(3, contentArray2[i]);
			statementAddJarContents.addBatch();
		}
		return statementAddJarContents.executeBatch();
	}

	private int[] addClassesList(List<String> classList, int jarId)
			throws SQLException {
		// Insert the new classes extracted from JAR file
		for (String fullQualifiedClassname : classList) {
			statementAddClassInfo.setString(1, ClassnameUtil.extractSimpleClassname(fullQualifiedClassname));
			statementAddClassInfo.setString(2, fullQualifiedClassname);
			statementAddClassInfo.setInt(3, jarId);
			statementAddClassInfo.addBatch();
		}

		return statementAddClassInfo.executeBatch();
	}

	//true if succeed
	private static boolean checkBatchResult(int[] batchResults) {

		if(batchResults.length==0)return true;

		int successCount = 0;
		int failCount = 0;

		for (int batchResult : batchResults) {
			if (batchResult >= 0) {
				successCount++;
			} else {
				failCount++;
			}
		}
		if(successCount > 0 && failCount == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static java.sql.Timestamp getSQLDate(long datetime){
		return new Timestamp(datetime);
	}

	@Override
	@CheckEDT
	public List<JarInfo> getJarEntryList() {
		List<JarInfo> result = new ArrayList<JarInfo>();
		try {
			ResultSet rs = statementSelectAllJarInfo.executeQuery();
			while (rs.next()) {
				int ordinal = rs.getInt(9);
				CatalogEntryStatus status = null;
				switch (ordinal) {
				case 0:
					status = CatalogEntryStatus.NORMAL;
					break;
				case 1:
					status = CatalogEntryStatus.REPLACED;
					break;
				case 2:
					status = CatalogEntryStatus.DELETED;
					break;
				default:
				}
				JarInfo jarInfo = new JarInfo(
						rs.getInt(1),//id
						rs.getString(2),//jarname
						rs.getString(3), //folder
						rs.getTimestamp(4).getTime(), //lastmodified
						rs.getString(5),// md5sum
						rs.getBoolean(6),//hasmainclass
						rs.getBoolean(7),//isSigned
						rs.getTimestamp(8).getTime(),//registdate
						status);

				result.add(jarInfo);
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, e);
		}

		return result;
	}

	@Override
	@CheckEDT
	public List<String> findClassLoose(String targetName) {
		List<String> result = new ArrayList<String>();
		try {
			statementFindClassLoose.setString(1, targetName.toLowerCase());
			ResultSet rs = statementFindClassLoose.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(2) + File.separator + rs.getString(1));
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, "", e);
		}
		Collections.sort(result);
		return result;
	}

	@Override
	@CheckEDT
	public List<String> findClassStrict(String targetName) {
		List<String> result = new ArrayList<String>();
		try {
			statementFindClassStrict.setString(1, targetName);
			ResultSet rs = statementFindClassStrict.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(2) + File.separator + rs.getString(1));
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, "", e);
		}
		Collections.sort(result);
		return result;
	}

	@Override
	@CheckEDT
	public long getLastUpdatedDateFromCatalog(File jarFile) {
		long lastUpdated = 0L;
		if(jarFile != null) {
			String jarname = jarFile.getName();
			String folder = jarFile.getParent();

			if (!folder.isEmpty() && !jarname.isEmpty()) {
				try {
					statementGetJarFileLastModified.setString(1, folder);
					statementGetJarFileLastModified.setString(2, jarname);
					ResultSet rs = statementGetJarFileLastModified.executeQuery();
					if (rs.next()) {
						lastUpdated = rs.getTimestamp(1).getTime();
					}
				} catch (SQLException e) {
					logger.error(e);
					try {
						statementGetChecksum.close();
					} catch (SQLException e1) {
						logger.log(Level.FATAL, jarFile.toString(), e1);
					} finally {
						;
					}
				}
			}
		}
		return lastUpdated;
	}

	/**
	 * @param jarFile
	 * @return checksum
	 */
	@Override
	@CheckEDT
	public String getChecksumFromCatalog(File jarFile) {
		String checksum = null;
		String jarname = jarFile.getName();
		String folder = jarFile.getParent();
		if (!folder.isEmpty() && !jarname.isEmpty()) {
			try {
				statementGetChecksum.setString(1, folder);
				statementGetChecksum.setString(2, jarname);
				ResultSet rs = statementGetChecksum.executeQuery();
				if (rs.next()) {
					checksum = rs.getString(1);
				}
			} catch (SQLException e) {
				logger.error(e);
				try {
					statementGetChecksum.close();
				} catch (SQLException e1) {
					logger.error(e1);
				} finally {
					;
				}
			}
		}
		return checksum;
	}

	/**
	 * Remove a specified JAR file. Classes contained in this JAR files are also removed by cascade-delete of SQL.
	 * @param jarFile
	 */
	@Override
	@CheckEDT
	public CatalogRegistResult removeJar(File jarFile) {
		String jarname = jarFile.getName();
		String folder = jarFile.getParent();
		CatalogRegistResult result = CatalogRegistResult.UNDEFINED;

		try {
			statementDeleteJarInfo.setString(1, jarname);
			statementDeleteJarInfo.setString(2, folder);
			int count = -1;
			if (!statementDeleteJarInfo.execute()) {
				count = statementDeleteJarInfo.getUpdateCount();
				if(count == 1) {
					result = CatalogRegistResult.REMOVED;
				}
			}
			if(statementDeleteJarInfo.getMoreResults()) {
				logger.log(Level.DEBUG, "removeJar():hasMoreResults()==true");
			}
		} catch (SQLException e) {
			logger.log(Level.FATAL, jarFile.toString(), e);
		}
		return result;
	}

	@Override
	@CheckEDT
	public boolean isAlreadyRegisterd(String folder, String jarname) {
		boolean result = false;
		try {
			statementRegistCheck.setString(1, folder);
			statementRegistCheck.setString(2, jarname);
			ResultSet rs = statementRegistCheck.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			result = (count == 1);
		} catch (SQLException e) {
			logger.log(Level.FATAL, folder + File.separator + jarname, e);
		}
		return result;
	}

	@Override
	@CheckEDT
	public List<String> getClassListInCatalog(String folder, String jarname) {
		List<String> result = new ArrayList<String>();
		try {
			statementGetClassListByJarFile.setString(1, folder);
			statementGetClassListByJarFile.setString(2, jarname);
			ResultSet rs = statementGetClassListByJarFile.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			logger.error(e);
		}

		return result;
	}

	@Override
	@CheckEDT
	public List<String> getClassListInCatalog(JarInfo jarInfo) {
		List<String> result = new ArrayList<String>();
		try {
			statementGetClassListByID.setInt(1, jarInfo.getId());
			ResultSet rs = statementGetClassListByID.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			logger.error(e);
		}

		return result;
	}

	@Override
	@CheckEDT
	public JarFileViewProperty getJarContentInCatalog(File jarFile) {
		JarFileViewProperty prop = null;
		try {
			JarInfo jarInfo = getJarInfo(jarFile.getParent(), jarFile.getName());
			List<String> classesList = getClassListInCatalog(jarInfo);
			String manifestContent = getManifestContentInCatalog(jarFile.getParent(), jarFile.getName());
			String otherFilesContent = getOtherFilesContentInCatalog(jarFile.getParent(), jarFile.getName());
			prop = new JarFileViewProperty(jarFile, jarInfo, manifestContent, classesList, otherFilesContent);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		}

		return prop;
	}

	private JarInfo getJarInfo(String dir, String jarname) {
		JarInfo info = null;
		try {
			statementSelectSpecifiedJarInfo.setString(1, dir);
			statementSelectSpecifiedJarInfo.setString(2, jarname);
			ResultSet rs = statementSelectSpecifiedJarInfo.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String jarname2 = rs.getString(2);
				String folder2  = rs.getString(3);
				long lastModifiedDate = rs.getTimestamp(4).getTime();//TIMESTAMP
				String checksum = rs.getString(5);
				boolean hasMainCLass = rs.getBoolean(6);
				boolean isSigned = rs.getBoolean(7);
				long registDate = rs.getDate(8).getTime();

				CatalogEntryStatus stat = values[rs.getInt(9)];

				info = new JarInfo(id, jarname2, folder2, lastModifiedDate, checksum, hasMainCLass, isSigned, registDate, stat);
			}
		} catch (SQLException e) {
			logger.fatal("folder:" + dir + ", jarname:" + jarname, e);
		}

		return info;
	}

	private final CatalogEntryStatus[] values = CatalogEntryStatus.values();

	private String getManifestContentInCatalog(String folder, String jarname) {
		String result = null;
		try {
			result = getJarContents(folder, jarname, ContentType.CONTENT_TYPE_MANIFEST);
		} catch (SQLException e) {
			logger.log(Level.FATAL, folder + File.separator + jarname, e);
		}
		return result;
	}

	private String getOtherFilesContentInCatalog(String folder, String jarname) {
		String result = null;
		try {
			result = getJarContents(folder, jarname, ContentType.CONTENT_TYPE_OTHER_FILE);
		} catch (SQLException e) {
			logger.log(Level.FATAL, folder + File.separator + jarname, e);
		}
		return result;
	}

	private String getJarContents(String folder, String jarname, ContentType contentType) throws SQLException {
		StringBuilder result = new StringBuilder();
		statementGetJarContent.setString(1, folder);
		statementGetJarContent.setString(2, jarname);
		statementGetJarContent.setInt(3, contentType.ordinal());
		ResultSet rs = statementGetJarContent.executeQuery();
		while (rs.next()) {
			result.append(rs.getString(1));
		}
		return result.toString();
	}

	@Override
	public void shutdown() {
		try {
			if(conn != null) {
				do {
					conn.close();
				} while(!conn.isClosed());
			}
		} catch (SQLException e) {
			logger.error(e);
		}
	}

	@Override
	@CheckEDT
	public void setAutoCommit(boolean b) throws SQLException {
		if(conn != null) {
			conn.setAutoCommit(b);
		}
	}

	@Override
	@CheckEDT
	public void rollback() throws SQLException {
		if(conn != null) {
			conn.rollback();
		}
	}


}
