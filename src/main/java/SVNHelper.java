import java.io.File;
import java.net.ConnectException;

import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Lucas
 * 
 */
public class SVNHelper {
	public static void main(String[] args) throws SVNException {
		String svnUrlStr = "https://58.246.62.194:8443/svn/SSTICreps/document/统一业务平台/需求文档/上海科委统一业务平台需求规格说明书.doc";
		String username = "lucas.yue";
		String password = "12345s";
		if (checkSVNURL(svnUrlStr, username, password)) {
			SVNClientManager client = createSVNClientManager(svnUrlStr,
					username, password);
			doExport(client, svnUrlStr);
			System.out.println(1);
		}
	}

	static {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	public static SVNClientManager createSVNClientManager(String url,
			String username, String password) {
		SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create(SVNURL
					.parseURIEncoded(url));
		} catch (SVNException e) {
			e.printStackTrace();
			return null;
		}
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(username, password);
		repository.setAuthenticationManager(authManager);
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options,
				authManager);
		return clientManager;
	}

	public static long doExport(SVNClientManager clientManager, String url) {
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		File destDir = createDestDirectory("export-source");
		try {
			return updateClient.doExport(SVNURL.parseURIEncoded(url), destDir,
					SVNRevision.HEAD, SVNRevision.HEAD, "native", true,
					SVNDepth.INFINITY);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public static long doCheckout(SVNClientManager clientManager, String url) {
		SVNUpdateClient updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		try {
			File destDir = createDestDirectory("checkout-source");
			return updateClient.doCheckout(SVNURL.parseURIEncoded(url),
					destDir, SVNRevision.HEAD, SVNRevision.HEAD,
					SVNDepth.INFINITY, false);
		} catch (SVNException e) {
			String msg = e.getMessage();
			if (msg.contains("refers to a file, not a directory")) {
				throw new RuntimeException("SVN源码目录不正确，请指定正确的SVN源码目录");
			}
		}
		return 0L;
	}

	private static File createDestDirectory(String dir) {
		String userDir = System.getProperty("user.dir") + File.separator + dir;
		File destDir = new File(userDir);
		if (destDir.exists()) {
			DeleteDirectory.deleteDir(destDir);
		}
		destDir.mkdir();
		return destDir;
	}

	public static File getExportDirectory() {
		String exportDir = System.getProperty("user.dir") + File.separator
				+ "export-source";
		return new File(exportDir);
	}

	public static boolean checkSVNURL(String url, String username,
			String password) {
		try {
			SVNRepository svnRepository = SVNRepositoryFactory.create(SVNURL
					.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil
					.createDefaultAuthenticationManager(username, password);
			svnRepository.setAuthenticationManager(authManager);
			SVNNodeKind nodeKind = svnRepository.checkPath("", -1L);
			return (nodeKind != SVNNodeKind.NONE);
		} catch (Throwable e) {
			e.printStackTrace();
			if (e instanceof SVNAuthenticationException) {
				throw new RuntimeException("SVN用户名或密码不正确");
			} else if (e instanceof ConnectException) {
				throw new RuntimeException("SVN连接超时");
			}
		}
		return false;
	}

}