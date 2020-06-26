import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

@SuppressWarnings("resource")
public class LinuxSystemTool extends Thread {

	public void run() {
		while (true) {
			Date date = new Date();// 创建一个时间对象，获取到当前的时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置时间显示格式
			String str = sdf.format(date);

			int[] mem = getMemInfo();
			float cpu = getCpuInfo();

			String file = "1.csv";
			try {
				FileOutputStream o = new FileOutputStream(file, true);
				o = new FileOutputStream(file, true);
				o.write(String.valueOf(str).getBytes());
				o.write(",".getBytes());
				for (int i = 0; i < mem.length; i++) {
					System.out.println(mem[i]);
					o.write(String.valueOf(mem[i]).getBytes());
					o.write(",".getBytes());
				}
				o.write((String.format("%.2f", (mem[1] * 1.0) / mem[0] * 100) + "%").getBytes());
				o.write(",".getBytes());
				o.write((String.format("%.2f", (100-((mem[1] * 1.0) / mem[0] * 100))) + "%").getBytes());
				o.write(",".getBytes());
				o.write((String.format("%.4f", cpu * 100) + "%").getBytes());
				o.write("\r\n".getBytes());

				o.flush();
				o.close();
				sleep(1 * 1000);

			} catch (InterruptedException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static int getpid() {
		File file = new File("ps -df | grep chrome");
		BufferedReader br;
		int result = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String str = null;
			StringTokenizer token = null;
			while ((str = br.readLine()) != null) {
				token = new StringTokenizer(str);
				if (!token.hasMoreTokens())
					continue;

				str = token.nextToken();
				if (!token.hasMoreTokens())
					continue;

				if (str.equalsIgnoreCase("PID"))
					result = Integer.parseInt(token.nextToken());
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

//	@SuppressWarnings("rawtypes")
	public static double gettop(String name, FileOutputStream o) {
		double cpuUsed = 0;
		Runtime rt = Runtime.getRuntime();
		BufferedReader in = null;
		try {
			String runstr = "top -b -n 1 | grep " + name + "";
			System.out.println(runstr);
			Process p = rt.exec(runstr);// 调用系统的“top"命令
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			System.out.println(in);
			String str = null;
			System.out.println(in.readLine());
			while ((str = in.readLine()) != null) {
				System.out.println(str);
				o.write(str.getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cpuUsed;
	}

	/**
	 * get memory by used info
	 * 
	 * @return int[] result
	 *         result.length==4;int[0]=MemTotal;int[1]=MemFree;int[2]=SwapTotal;int[3]=SwapFree;
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int[] getMemInfo() {
		File file = new File("/proc/meminfo");
		BufferedReader br;
		int[] result = new int[4];
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String str = null;
			StringTokenizer token = null;
			while ((str = br.readLine()) != null) {
				token = new StringTokenizer(str);
				if (!token.hasMoreTokens())
					continue;

				str = token.nextToken();
				if (!token.hasMoreTokens())
					continue;

				if (str.equalsIgnoreCase("MemTotal:"))
					result[0] = Integer.parseInt(token.nextToken());
				else if (str.equalsIgnoreCase("MemFree:"))
					result[1] = Integer.parseInt(token.nextToken());
				else if (str.equalsIgnoreCase("SwapTotal:"))
					result[2] = Integer.parseInt(token.nextToken());
				else if (str.equalsIgnoreCase("SwapFree:"))
					result[3] = Integer.parseInt(token.nextToken());
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * get memory by used info
	 * 
	 * @return float efficiency
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static float getCpuInfo() {
		File file = new File("/proc/stat");
		BufferedReader br, br1;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			StringTokenizer token = new StringTokenizer(br.readLine());
			token.nextToken();
			int user1 = Integer.parseInt(token.nextToken());
			int nice1 = Integer.parseInt(token.nextToken());
			int sys1 = Integer.parseInt(token.nextToken());
			int idle1 = Integer.parseInt(token.nextToken());

			Thread.sleep(1000);

			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			token = new StringTokenizer(br1.readLine());
			token.nextToken();
			int user2 = Integer.parseInt(token.nextToken());
			int nice2 = Integer.parseInt(token.nextToken());
			int sys2 = Integer.parseInt(token.nextToken());
			int idle2 = Integer.parseInt(token.nextToken());
			float uselv1 = (float) ((user2 + sys2 + nice2) - (user1 + sys1 + nice1));
			float uselv2 = (float) ((user2 + nice2 + sys2 + idle2) - (user1 + nice1 + sys1 + idle1));
			return uselv1 / uselv2;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (float) (1 / 1.0);
	}
}
