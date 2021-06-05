package com.Chatit.Server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class ServerApplicationTests {


	public void min(String ags[]) {
		URL url = null;
		try {
			url = new URL("https://chatit-server.herokuapp.com/login");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String data = "Email=abcd%40example.com&Password=123456";

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = http.getOutputStream();
			stream.write(out);

			System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			InputStream res = http.getInputStream();
			String ress = new String(res.readAllBytes());
			System.out.println(ress);
			http.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
