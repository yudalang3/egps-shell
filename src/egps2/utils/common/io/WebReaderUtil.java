package egps2.utils.common.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;

/**
 * WebReaderUtil provides shared utility logic for eGPS modules and UI.
 */
public class WebReaderUtil {

	public static List<String> getContentFromUrl(String mainDataURL) throws IOException {
		List<String> ret = new ArrayList<>();

		URL url = new URL(mainDataURL); // 创建URL
		URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
		urlconn.connect();
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		int httpStatus = httpconn.getResponseCode();// 服务器返回的状态
		if (httpStatus != HttpURLConnection.HTTP_OK) {
			System.err.print("无法连接到");
		} else {
			// int filesize = urlconn.getContentLength(); // 取数据长度
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlconn.getInputStream(), "UTF-8"));
			String line; // 用来保存每行读取的内容
			while ((line = reader.readLine()) != null) {
				ret.add(line);
			}
			reader.close();
		}

		return ret;
	}

	public static List<String> getZippedContentFromUrl(String urlString) throws IOException {
		List<String> ret = new ArrayList<>();

		URL url = new URL(urlString); // 创建URL
		URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
		urlconn.connect();
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		int httpStatus = httpconn.getResponseCode();// 服务器返回的状态
		if (httpStatus != HttpURLConnection.HTTP_OK) {
			System.out.print("无法连接到");
		} else {
			ZipInputStream zipInput = new ZipInputStream(urlconn.getInputStream(), Charset.defaultCharset());
			ZipEntry ze;
			BufferedReader reader = null;
			while ((ze = zipInput.getNextEntry()) != null) {
				if (ze.isDirectory()) {
				} else {
					reader = new BufferedReader(new InputStreamReader(zipInput, "UTF-8"));
					String line; // 用来保存每行读取的内容
					while ((line = reader.readLine()) != null) {
						ret.add(line);
					}

				}
			}
			if (reader != null) {
				reader.close();
			}
			zipInput.close();
		}

		return ret;
	}

	/**
	 * 得到xz压缩之后的文件的第一个文件的内容，这个第一个文件得是一个文本文件。
	 * 
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static List<String> getXZCompressedFirstContentFromUrl(String urlString) throws IOException {
		List<String> ret = null;

		URL url = new URL(urlString); // 创建URL
		URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
		urlconn.connect();
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		int httpStatus = httpconn.getResponseCode();// 服务器返回的状态
		if (httpStatus != HttpURLConnection.HTTP_OK) {
			throw new IOException("Can not connect to the Internet.");
		} else {
			try (TarArchiveInputStream fin = new TarArchiveInputStream(
					new XZCompressorInputStream(httpconn.getInputStream()))) {
				TarArchiveEntry entry = null;
				// 将 tar 文件解压到 extractPath 目录下
				while ((entry = fin.getNextTarEntry()) != null) {
					// .开头的文件会就直接忽略了
					// System.out.println(entry.getName());
					if (!entry.getName().startsWith(".")) {
						ret = IOUtils.readLines(fin);
						break;
					}
				}
			}
		}

		return ret;
	}

	public static void main(String[] args) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(
				"/Users/yudalang/workDir/testWebNewFormat/mainDataFile.xz");
		List<String> ret = null;
		try (TarArchiveInputStream fin = new TarArchiveInputStream(new XZCompressorInputStream(fileInputStream))) {

			TarArchiveEntry entry;
			// 将 tar 文件解压到 extractPath 目录下
			while ((entry = fin.getNextTarEntry()) != null) {
				ret = IOUtils.readLines(fin);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(ret.get(0));
		System.out.println(ret.size());

	}

}
