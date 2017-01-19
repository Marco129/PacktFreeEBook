package com.marco129.packtfreeebook;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.FailedLoginException;
import javax.swing.SwingWorker;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Processor extends SwingWorker<Boolean, Object> {

	private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:46.0) Gecko/20100101 Firefox/46.0";
	private final String sessionCookieName = "SESS_live";

	private String email, password, formId, sessionCookie, claimUrl, bookTitle;
	private ProcessorListener listener;
	private OkHttpClient httpClient;

	public Processor(String email, String password, ProcessorListener listener) {
		this.email = email;
		this.password = password;
		this.listener= listener;
	}

	@Override
	public Boolean doInBackground() {
		try {
			prepare();
			authentication();
			claim();
			logout();
			listener.onSuccess(getBookTitle());
			return true;
		} catch (Exception e) {
			listener.onFailure();
			return false;
		}
	}

	private void prepare() throws Exception {
		// Access page for retrieving variables
		initHttpClient(true);
		Request request = new Request.Builder()
				.header("User-Agent", userAgent)
				.url("https://www.packtpub.com/packt/offers/free-learning")
				.get()
				.build();
		try {
			Response response = httpClient.newCall(request).execute();

			// Get form id
			String body = response.body().string();
			Pattern pattern = Pattern.compile("value=\"form-(.*?)\"");
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				formId = matcher.group(1);
			} else {
				throw new Exception("Missing form ID");
			}

			// Retrieve claim url
			pattern = Pattern.compile("href=\"/freelearning-claim/(.*?)\"");
			matcher = pattern.matcher(body);
			if (matcher.find()) {
				claimUrl = matcher.group(1);
			} else {
				throw new Exception("Missing claim url");
			}

			// Retrieve eBook title
			pattern = Pattern.compile("<div class=\"dotd-title\">\\s+<h2>\\s+(.*?)<");
			matcher = pattern.matcher(body);
			if (matcher.find()) {
				setBookTitle(matcher.group(1));
			} else {
				throw new Exception("Missing eBook title");
			}
		} catch (IOException e) {
			throw new IOException();
		}
	}

	private void authentication() throws Exception {
		// Authentication
		initHttpClient(false);
		RequestBody formBody = new FormBody.Builder()
				.add("email", email)
				.add("password", password)
				.add("form_id", "packt_user_login_form")
				.add("form-" + formId, "form-" + formId)
				.add("op", "Login")
				.build();
		Request request = new Request.Builder()
				.header("User-Agent", userAgent)
				.url("https://www.packtpub.com/packt/offers/free-learning")
				.post(formBody)
				.build();
		try {
			Response response = httpClient.newCall(request).execute();

			// Retrieve session cookie
			List<String> cookie = response.headers("Set-Cookie");
			Pattern pattern = Pattern.compile(sessionCookieName + "=(.*?);");
			for (String str : cookie) {
				Matcher matcher = pattern.matcher(str);
				if (matcher.find()) {
					sessionCookie = matcher.group(1);
				}
			}
			if (sessionCookie == null) {
				throw new FailedLoginException();
			}
		} catch (IOException e) {
			throw new IOException();
		}
	}

	private void claim() throws Exception {
		// Claim free eBook
		initHttpClient(true);
		Request request = new Request.Builder()
				.header("User-Agent", userAgent)
				.header("Cookie", sessionCookieName + "=" + sessionCookie)
				.url("https://www.packtpub.com/freelearning-claim/" + claimUrl)
				.get()
				.build();
		try {
			Response response = httpClient.newCall(request).execute();
			
			// Check for success response
			if (!response.priorResponse().header("Location").equals("https://www.packtpub.com/account/my-ebooks")) {
				throw new Exception("Cannot claim eBook");
			}
		} catch (IOException e) {
			throw new IOException();
		}
	}

	private void logout() throws Exception {
		// Logout
		initHttpClient(true);
		Request request = new Request.Builder()
				.header("User-Agent", userAgent)
				.header("Cookie", sessionCookieName + "=" + sessionCookie)
				.url("https://www.packtpub.com/logout")
				.get()
				.build();
		try {
			Response response = httpClient.newCall(request).execute();

			// Check for success response
			if (!response.isSuccessful()) {
				throw new Exception("Cannot logout");
			}
		} catch (IOException e) {
			throw new IOException();
		}
	}

	private void initHttpClient(boolean followRedirect) {
		httpClient = new OkHttpClient().newBuilder()
				.followRedirects(followRedirect)
				.followSslRedirects(followRedirect)
				.build();
	}

	private String getBookTitle() {
		return bookTitle;
	}

	private void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle.trim();
	}

	interface ProcessorListener {
		void onSuccess(String bookTitle);
		void onFailure();
	}

}
