package com.vfinworks.vfsdk.http;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UploadFile {



	private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";

	/**
	 *
	 * @param params
	 *            传递的普通参数
	 * @param uploadFile
	 *            需要上传的文件
	 * @param fileFormName
	 *            需要上传文件表单中的名字
	 * @param newFileName
	 *            上传的文件名称，不填写将为uploadFile的名称
	 * @param urlStr
	 *            上传的服务器的路径
	 * @throws IOException
	 */
	public static String uploadForm(Map<String, String> params, String fileFormName,
						   File uploadFile, String newFileName, String urlStr)
			throws IOException {
		if (newFileName == null || newFileName.trim().equals("")) {
			if(uploadFile != null)
				newFileName = uploadFile.getName();
		}

		StringBuilder sb = new StringBuilder();
		/**
		 * 普通的表单数据
		 */
		for (String key : params.keySet()) {
			sb.append("--" + BOUNDARY + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + key + "\""
					+ "\r\n");
			sb.append("\r\n");
			sb.append(params.get(key) + "\r\n");
		}

		/**
		 * 上传文件的头
		 */
		sb.append("--" + BOUNDARY + "\r\n");
		if(uploadFile != null) {
			sb.append("Content-Disposition: form-data; name=\"" + fileFormName
					+ "\"; filename=\"" + newFileName + "\"" + "\r\n");
			sb.append("Content-Type: image/jpeg" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
			sb.append("\r\n");
		}
		byte[] headerInfo = sb.toString().getBytes("UTF-8");
		byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
		System.out.println(sb.toString());
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		if(uploadFile != null)
			conn.setRequestProperty("Content-Length", String
				.valueOf(headerInfo.length + uploadFile.length()
						+ endInfo.length));
		else
			conn.setRequestProperty("Content-Length", String
					.valueOf(headerInfo.length
							+ endInfo.length));
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		InputStream in = null;
		if(uploadFile != null)
			in = new FileInputStream(uploadFile);
		out.write(headerInfo);

		if(uploadFile != null) {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1)
				out.write(buf, 0, len);
		}

		out.write(endInfo);
		if(uploadFile != null)
			in.close();
		out.close();
		if (conn.getResponseCode() == 200) {
			System.out.println("上传成功");
		}
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String strRead = null;
		StringBuffer sbf = new StringBuffer();
		while ((strRead = reader.readLine()) != null) {
			sbf.append(strRead);
			sbf.append("\r\n");
		}
		reader.close();
		return sbf.toString();

	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 *
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static String post(String actionUrl, Map<String, String> params,
							Map<String, File> files) throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(conn
				.getOutputStream());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// 发送文件数据
		StringBuilder sb2 = new StringBuilder();
		if (files != null) {
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1
						.append("Content-Disposition: form-data; name=\"file[]\"; filename=\""
								+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			if (res == 200) {
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strRead = null;
				while ((strRead = reader.readLine()) != null) {
					sb2.append(strRead);
					sb2.append("\r\n");
				}
				System.out.println(sb2.toString());
			}
			outStream.close();
			conn.disconnect();
		}
		return sb2.toString();
	}
}
