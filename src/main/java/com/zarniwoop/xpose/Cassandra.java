package com.zarniwoop.xpose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

public class Cassandra {

	private static Logger logger = Logger.getLogger(Cassandra.class);
	private Cluster cluster = null;
	Session session = null;

	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : "172.16.50.50";
		String keyspace = args.length > 1 ? args[1] : "xpose";
		Cassandra inst = new Cassandra();
		inst.startup(host, keyspace);
	}
	
	public void init(String host) {
		cluster = Cluster.builder().addContactPoint(host).build();
	}
	
	public void startup(String host, String keyspace) {
		logger.info(String.format("starting cassandra ring with keyspace = %s", keyspace));
		init(host);
		try {
			session = cluster.connect(keyspace);
			logger.info("Connected.");
		} catch (InvalidQueryException e) {
			logger.warn(e.getMessage());
			session = cluster.connect();
			createKeyspace(keyspace);
			session = cluster.connect(keyspace);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
		
		createTable("host", "ip int, port int, proto text, stream uuid", "ip, port, proto");
		createTable("stream", "id uuid, accessed timestamp, stream text", "id, accessed");
		createTable("stream_http", "id uuid, stream_id uuid, header text, body text", "id, stream_id");
		// 74.126.19.7
		for (int i = 1249773333; i < 1249800000; i++) {
			// skip x.x.x.0
			if (i % 256 == 0) {
				continue;
			}
			
			String response = sendGetRequest(i, 80);
			if (response != null && response.length() > 0) {
				UUID streamId = insertStream(response);
				insertHTTPStream(streamId, response);
				insertHost(i, 80, "HTTP", streamId);
			}
		}
		System.exit(0);
	}
	
	public void createKeyspace(String keyspace) {
		String withOptions = "WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
		session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s %s", keyspace, withOptions));
	}
	
	public void createTable(String tableName, String columns, String primaryKey) {
		
		session.execute(String.format("CREATE TABLE IF NOT EXISTS %s \n(%s, \nPRIMARY KEY (%s));", tableName, columns, primaryKey));
	}

	public UUID insertStream(String stream) {
		UUID uuid = UUID.randomUUID();
		String query = "INSERT INTO Stream (id, accessed, stream) VALUES (?, ?, ?);";
		BoundStatement stmt = new BoundStatement(session.prepare(query));
		stmt.bind(uuid, new Date(), stream);
		session.execute(stmt);
		return uuid;
	}
	
	public UUID insertHTTPStream(UUID streamId, String stream) {
		UUID uuid = null;
		String[] chunk = stream.split("\r\n\r\n");
		if (chunk.length == 2) {
			uuid = UUID.randomUUID();
			String query = "INSERT INTO stream_http (id, stream_id, header, body) VALUES (?, ?, ?, ?);";
			BoundStatement stmt = new BoundStatement(session.prepare(query));
			stmt.bind(uuid, streamId, chunk[0], chunk[1]);
			session.execute(stmt);
		}
		return uuid;
	}
	
	public void insertHost(int ip, int port, String proto, UUID stream) {
		String query = "INSERT INTO Host (ip, port, proto, stream) VALUES (?, ?, ?, ?);";
		BoundStatement stmt = new BoundStatement(session.prepare(query));
		stmt.bind(ip, port, proto, stream);
		session.execute(stmt);
	}
	
	public String sendGetRequest(int hostID, int port) {
		StringBuilder response = new StringBuilder();
		String ipAddress = String.format("%d.%d.%d.%d", (hostID >> 24) & 0xFF, (hostID >> 16) & 0xFF, (hostID >> 8) & 0xFF, hostID & 0xFF);
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ipAddress, port), 3000);
			PrintWriter pw = new PrintWriter( socket.getOutputStream() );
			pw.print("GET / HTTP/1.1\r\n");
			pw.print("Host: ");
			pw.print(ipAddress);
			pw.print("\r\n\r\n");
			pw.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			int chr;
			while ( (chr = br.read()) != -1 && br.ready()) {
				response.append((char)chr);
			}
			br.close();
			socket.close();
			
			logger.info(ipAddress + " responded.");
		} catch (ConnectException e) {
			logger.warn(ipAddress + " refused connection.");
			// TODO store alive host
		} catch (SocketTimeoutException e) {
			logger.warn(ipAddress + " timed out.");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString().trim();
	}
}
