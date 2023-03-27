import java.io.*;
import java.net.*;

public class MyClient {

	public static void main(String[] args) {
		try {

			Socket s = new Socket("localhost", 50000);
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));

			dout.write(("HELO\n").getBytes());
			System.out.println((String) dis.readLine());
			dout.flush();

			String username = System.getProperty("user.name");

			dout.write(("AUTH " + username + "\n").getBytes());
			String lastmessage = (String) dis.readLine();
			System.out.println(lastmessage);
			dout.flush();

			int times = 0;
			String largestServer = "";

			while (!(lastmessage.equals("NONE"))) {

				dout.write(("REDY\n").getBytes());
				String jobs = (String) dis.readLine();
				System.out.println(jobs);
				dout.flush();

				if (jobs.equals("NONE")) {
					System.out.println("it came here");
					break;
				}

				String[] jobsIndivData = jobs.split(" ");
				int jobId = Integer.parseInt(jobsIndivData[2]);

				int numberOfLargeServers = 0;
				if (times < 1) {

					dout.write(("GETS All\n").getBytes());
					String data = dis.readLine();
					dout.flush();

					dout.write(("OK\n").getBytes());
					dout.flush();

					String[] indivData = data.split(" ");
					int nRecs = Integer.parseInt(indivData[1]);

					String servers = "";
					String[][] serversList = new String[nRecs][];
					int largestServerCores = 0;

					System.out.println("We been here");
					for (int i = 0; i < nRecs; i++) {
						servers = (String) dis.readLine();
						System.out.println(servers);

						String[] serverIndiv = servers.split(" ");
						serversList[i] = serverIndiv;

						if (Integer.parseInt(serverIndiv[4]) > largestServerCores) {
							largestServer = serverIndiv[0];
							largestServerCores = Integer.parseInt(serverIndiv[4]);
						}

					}

					System.out.println(largestServer);

					for (int i = 0; i < nRecs; i++) {
						if (serversList[i][0].equals(largestServer)) {
							numberOfLargeServers += 1;
						}
					}
					dout.write(("OK\n").getBytes());
					dis.readLine();
					dout.flush();
				}

				if (jobsIndivData[0].equals("JOBN")) {
					String schedCommand = "SCHD " + jobId + " " + largestServer + " 0";
					System.out.println(schedCommand);
					dout.write((schedCommand + "\n").getBytes());
					System.out.println((String) dis.readLine() + "\n");
					dout.flush();
				}

				else {
					System.out.println("\n\n");
				}

				times += 1;

			}

			dout.write(("QUIT\n").getBytes());
			System.out.println((String) dis.readLine());
			dout.flush();

			// dout.write(("SCHD 0 joon 0\n").getBytes());
			// System.out.println((String) dis.readLine());
			// dout.flush();

			dout.close();
			dis.close();
			s.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
