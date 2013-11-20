import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(560, 300);
		shell.setLayout(new FormLayout());
		// Label控件
		createUI(shell);

		// shell.pack();
		shell.open();
		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void createUI(final Shell shell) {
		Label svnPath = new Label(shell, SWT.LEFT);
		svnPath.setText("SVN资源库路径：");
		Label svnUsername = new Label(shell, SWT.LEFT);
		svnUsername.setText("SVN用户名：");
		Label svnPassword = new Label(shell, SWT.LEFT);
		svnPassword.setText("SVN密码：");
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
		Text svnPathText = new Text(shell, SWT.BORDER);
		Text svnUsernameText = new Text(shell, SWT.BORDER);
		Text svnPasswordText = new Text(shell, SWT.BORDER);
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
		// 选择控件
		final String[] ratings = new String[] { "开发环境", "测试环境", "生产环境" };
		final Button[] radios = new Button[3];
		final FormData[] formDataRadios = new FormData[3];
		for (int i = 0; i < ratings.length; i++) {
			radios[i] = new Button(shell, SWT.RADIO);
			radios[i].setText(ratings[i]);
			formDataRadios[i] = new FormData(80, 25);
		}
		radios[1].setSelection(true);
		// 选择控件布局
		formDataRadios[0].top = new FormAttachment(60);
		formDataRadios[1].top = new FormAttachment(60);
		formDataRadios[2].top = new FormAttachment(60);
		formDataRadios[0].left = new FormAttachment(30);
		formDataRadios[1].left = new FormAttachment(45);
		formDataRadios[2].left = new FormAttachment(60);
		for (int i = 0; i < ratings.length; i++) {
			radios[i].setLayoutData(formDataRadios[i]);
		}
		// 按钮控件
		Button tagAndCheckoutButton = new Button(shell, SWT.PUSH);
		tagAndCheckoutButton.setText("更新Tag");
		Button packButton = new Button(shell, SWT.PUSH);
		packButton.setText("打包");
		// 按钮控件布局
		FormData formDataButtonTagAndCheckout = new FormData(80, 30);
		formDataButtonTagAndCheckout.top = new FormAttachment(75);
		formDataButtonTagAndCheckout.left = new FormAttachment(30);
		tagAndCheckoutButton.setLayoutData(formDataButtonTagAndCheckout);
		FormData formDataButtonPack = new FormData(80, 30);
		formDataButtonPack.top = new FormAttachment(75);
		formDataButtonPack.left = new FormAttachment(60);
		packButton.setLayoutData(formDataButtonPack);
		packButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < radios.length; i++) {
					if (radios[i].getSelection()) {
						System.out.println(ratings[i]);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				for (int i = 0; i < radios.length; i++) {
					if (radios[i].getSelection()) {
						System.out.println(ratings[i]);
					}
				}
			}
		});
	}

}
