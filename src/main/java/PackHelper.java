import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

/**
 * @author LucasYue
 * 
 */
public class PackHelper {
	public static void main(String[] args) {
		PackHelper pH = new PackHelper();
		boolean rs;
		try {
			rs = pH.execute("cmd.exe /c call mvn clean package -P dev",
					new File("G://shkwcode130509/ubp-builder/target/export"));
			if (rs) {
				System.out.println("打包完成。");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	enum PackCommand {
		RunOnce("cmd.exe /c call mvn clean install"), RunOnceDev(
				"cmd.exe /c call mvn clean package -P dev"), RunOnceQa(
				"cmd.exe /c call mvn clean package -P qa"), RunOnceProduct(
				"cmd.exe /c call mvn clean package -P product");
		private String cmd;

		PackCommand(String cmd) {
			this.cmd = cmd;
		}

		public String getPackCommand() {
			return this.cmd;
		}

	}

	public boolean execute(String cmd, File baseDir) throws Exception {
		try {
			Process proc = Runtime.getRuntime().exec(cmd, null, baseDir);
			CountDownLatch threadSignal = new CountDownLatch(2);// 初始化countDown
			StreamGobbler errorGobbler = new StreamGobbler(
					proc.getErrorStream(), "Error", threadSignal);
			StreamGobbler outputGobbler = new StreamGobbler(
					proc.getInputStream(), "Output", threadSignal);
			errorGobbler.start();
			outputGobbler.start();
			threadSignal.await();
			if (outputGobbler.getErrorFlag() != null
					|| errorGobbler.getErrorFlag() != null) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}

	class StreamGobbler extends Thread {
		private CountDownLatch threadsSignal;
		private String errorFlag;

		public String getErrorFlag() {
			return errorFlag;
		}

		InputStream is;
		String type;

		public StreamGobbler(InputStream is, String type,
				CountDownLatch threadsSignal) {
			this.is = is;
			this.type = type;
			this.threadsSignal = threadsSignal;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (type.equals("Error")) {
						System.out.println("Error " + line);
					} else {
						System.out.println("Running " + line);
					}
					if (line.contains("BUILD FAILURE")) {
						errorFlag = "请先更新源码再进行打包！";
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				threadsSignal.countDown();
			}
		}
	}
}
