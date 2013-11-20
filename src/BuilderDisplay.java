import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public class BuilderDisplay {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.CLOSE);
		shell.setSize(new Point(560, 330));
		shell.setText("UBP-打包工具");
		shell.setLayout(new FormLayout());

		createUI(shell);

		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		if (x > width) {
			shell.getSize().x = width;
		}
		if (y > height) {
			shell.getSize().y = height;
		}
		shell.setLocation((width - x) / 2, (height - y) / 2);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void createUI(final Shell shell) {
		final Label svnPath = new Label(shell, SWT.LEFT);
		svnPath.setText("SVN源码目录");
		final Label svnUsername = new Label(shell, SWT.LEFT);
		svnUsername.setText("SVN用户名");
		final Label svnPassword = new Label(shell, SWT.LEFT);
		svnPassword.setText("SVN密码");
		// Label布局
		FormData formDataSvnPath = new FormData(100, 25);
		formDataSvnPath.top = new FormAttachment(10);
		formDataSvnPath.left = new FormAttachment(10);
		svnPath.setLayoutData(formDataSvnPath);
		FormData formDataSvnUsername = new FormData(100, 25);
		formDataSvnUsername.top = new FormAttachment(25);
		formDataSvnUsername.left = new FormAttachment(10);
		svnUsername.setLayoutData(formDataSvnUsername);
		FormData formDataSvnPassword = new FormData(100, 25);
		formDataSvnPassword.top = new FormAttachment(40);
		formDataSvnPassword.left = new FormAttachment(10);
		svnPassword.setLayoutData(formDataSvnPassword);
		// Text控件
		final Text svnPathText = new Text(shell, SWT.BORDER);
		final Text svnUsernameText = new Text(shell, SWT.BORDER);
		final Text svnPasswordText = new Text(shell, SWT.BORDER);
		// 状态
		final Label statusLabel = new Label(shell, SWT.CENTER | SWT.WRAP);
		// Text布局
		FormData formDataSvnPathText = new FormData(300, 25);
		formDataSvnPathText.top = new FormAttachment(10);
		formDataSvnPathText.left = new FormAttachment(30);
		svnPathText.setLayoutData(formDataSvnPathText);
		FormData formDataSvnUsernameText = new FormData(300, 25);
		formDataSvnUsernameText.top = new FormAttachment(25);
		formDataSvnUsernameText.left = new FormAttachment(30);
		svnUsernameText.setLayoutData(formDataSvnUsernameText);
		FormData formDataSvnPasswordText = new FormData(300, 25);
		formDataSvnPasswordText.top = new FormAttachment(40);
		formDataSvnPasswordText.left = new FormAttachment(30);
		svnPasswordText.setLayoutData(formDataSvnPasswordText);
		FormData formDataStatusLabel = new FormData(500, 35);
		formDataStatusLabel.top = new FormAttachment(60);
		formDataStatusLabel.left = new FormAttachment(5);
		statusLabel.setLayoutData(formDataStatusLabel);
		// 选择控件
		final String[] ratings = new String[] { "开发环境", "测试环境", "生产环境" };
		final Button[] radios = new Button[3];
		final FormData[] formDataRadios = new FormData[3];
		for (int i = 0; i < ratings.length; i++) {
			radios[i] = new Button(shell, SWT.RADIO);
			radios[i].setText(ratings[i]);
			formDataRadios[i] = new FormData(80, 25);
		}
		radios[0].setSelection(true);
		// 选择控件布局
		formDataRadios[0].top = new FormAttachment(70);
		formDataRadios[1].top = new FormAttachment(70);
		formDataRadios[2].top = new FormAttachment(70);
		formDataRadios[0].left = new FormAttachment(30);
		formDataRadios[1].left = new FormAttachment(45);
		formDataRadios[2].left = new FormAttachment(60);
		for (int i = 0; i < ratings.length; i++) {
			radios[i].setLayoutData(formDataRadios[i]);
		}
		// 按钮控件
		Button getSourceButton = new Button(shell, SWT.PUSH);
		getSourceButton.setText("更新源码");
		Button packButton = new Button(shell, SWT.PUSH);
		packButton.setText("打包");
		// 按钮控件布局
		FormData formDataButtonTagAndCheckout = new FormData(80, 30);
		formDataButtonTagAndCheckout.top = new FormAttachment(80);
		formDataButtonTagAndCheckout.left = new FormAttachment(30);
		getSourceButton.setLayoutData(formDataButtonTagAndCheckout);
		FormData formDataButtonPack = new FormData(80, 30);
		formDataButtonPack.top = new FormAttachment(80);
		formDataButtonPack.left = new FormAttachment(60);
		packButton.setLayoutData(formDataButtonPack);

		// 事件处理
		getSourceButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (doCheck(svnPathText, svnUsernameText, svnPasswordText)) {
					statusLabel.setText("正在更新源码......");
					doGetSource(svnPathText.getText(),
							svnUsernameText.getText(),
							svnPasswordText.getText());
				}
			}

			private void doGetSource(String url, String username,
					String password) {
				try {
					if (SVNHelper.checkSVNURL(url, username, password)) {
						SVNClientManager clientManager = SVNHelper
								.createSVNClientManager(url, username, password);
						SVNHelper.doExport(clientManager, url);
						statusLabel.setText("源码更新成功，在"
								+ SVNHelper.getExportDirectory());
					} else {
						statusLabel.setText("SVN源码目录不正确");
					}
				} catch (RuntimeException e) {
					statusLabel.setText(e.getMessage());
				}
			}

			private boolean doCheck(final Text svnPathText,
					final Text svnUsernameText, final Text svnPasswordText) {
				String svnPathMsg = svnPathText.getText();
				String svnUsernameMsg = svnUsernameText.getText();
				String svnPasswordMsg = svnPasswordText.getText();
				String msg = "";
				if (svnPathMsg == null || svnPathMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnPath.getText();
				}
				if (svnUsernameMsg == null || svnUsernameMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnUsername.getText();
				}
				if (svnPasswordMsg == null || svnPasswordMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnPassword.getText();
				}
				if (msg != "") {
					statusLabel.setText(msg + "不能为空");
					return false;
				}
				return true;
			}
		});
		packButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String packSelect = null;
				for (int i = 0; i < radios.length; i++) {
					if (radios[i].getSelection()) {
						packSelect = ratings[i];
					}
				}
				if (doCheck(svnPathText, svnUsernameText, svnPasswordText)) {
					System.out.println(packSelect + "打包:"
							+ svnPathText.getText() + ","
							+ svnUsernameText.getText() + ","
							+ svnPasswordText.getText());
					doPack(svnPathText.getText(), packSelect);
				}
			}

			private boolean doCheck(final Text svnPathText,
					final Text svnUsernameText, final Text svnPasswordText) {
				String svnPathMsg = svnPathText.getText();
				String svnUsernameMsg = svnUsernameText.getText();
				String svnPasswordMsg = svnPasswordText.getText();
				String msg = "";
				if (svnPathMsg == null || svnPathMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnPath.getText();
				}
				if (svnUsernameMsg == null || svnUsernameMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnUsername.getText();
				}
				if (svnPasswordMsg == null || svnPasswordMsg == "") {
					if (msg != "") {
						msg += "、";
					}
					msg += svnPassword.getText();
				}
				if (msg != "") {
					statusLabel.setText(msg + "不能为空");
					return false;
				}
				return true;
			}

			private void doPack(String svnPath, String packSelect) {
				File exportDir = SVNHelper.getExportDirectory();
				PackHelper packHelper = new PackHelper();
				if (svnPath.contains("ubp-framework")
						|| svnPath.contains("ubp-commonservice-client")) {
					packing(PackHelper.PackCommand.RunOnce, exportDir,
							packHelper);
				} else if ("开发环境".equals(packSelect)) {
					packing(PackHelper.PackCommand.RunOnceDev, exportDir,
							packHelper);
				} else if ("测试环境".equals(packSelect)) {
					packing(PackHelper.PackCommand.RunOnceQa, exportDir,
							packHelper);
				} else if ("生产环境".equals(packSelect)) {
					packing(PackHelper.PackCommand.RunOnceProduct, exportDir,
							packHelper);
				}
			}

			private void packing(PackHelper.PackCommand packCommand,
					File exportDir, PackHelper packHelper) {
				try {
					statusLabel.setText("正在打包......");
					boolean rs = packHelper.execute(
							packCommand.getPackCommand(), exportDir);
					statusLabel.setText("打包成功，在"
							+ SVNHelper.getExportDirectory() + File.separator
							+ "target");
					if (!rs) {
						statusLabel.setText("打包失败，请先更新源码再进行打包！");
					}
				} catch (Exception e) {
					e.printStackTrace();
					statusLabel.setText("打包失败，请先更新源码再进行打包！");
				}
			}
		});
	}
}
