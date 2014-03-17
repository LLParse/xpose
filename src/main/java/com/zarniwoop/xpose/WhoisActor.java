package com.zarniwoop.xpose;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class WhoisActor {

	public static void main(String[] args) {
		Socket s;
		try {
			s = new Socket("internic.net", 43);
			InputStream in = s.getInputStream();
			OutputStream out = s.getOutputStream();
			String str = "m-indya.com\r\n\r\n";
			byte buf[] = str.getBytes();
			out.write(buf);
			int c;
			while ((c=in.read()) != -1) {
				System.out.print((char)c);
			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
