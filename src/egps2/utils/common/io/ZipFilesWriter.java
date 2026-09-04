package egps2.utils.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FilenameUtils;

/**
 * ZipFilesWriter provides shared utility logic for eGPS modules and UI.
 */
public class ZipFilesWriter {

	// 4MB buffer
	private static final byte[] BUFFER = new byte[4096 * 1024];

	/**
	 * copy input to output stream - available in several StreamUtils or Streams
	 * classes
	 */
	public void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, bytesRead);
		}
	}

	public void writeXZCompressedURLs2zipFile(List<String> urlsStrings, String outFilePath) throws IOException {
		ZipOutputStream append = new ZipOutputStream(new FileOutputStream(outFilePath));
		// first, copy contents from input files
		for (String string : urlsStrings) {
			URL url = new URL(string); // 创建URL
			URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
			urlconn.connect();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			int httpStatus = httpconn.getResponseCode();// 服务器返回的状态
			if (httpStatus != HttpURLConnection.HTTP_OK) {
				String concat = "can not connect to\t".concat(string);
				throw new IOException(concat);
			} else {
				try (TarArchiveInputStream fin = new TarArchiveInputStream(
						new XZCompressorInputStream(httpconn.getInputStream()))) {
					TarArchiveEntry entry = null;
					// 将 tar 文件解压到 extractPath 目录下
					while ((entry = fin.getNextTarEntry()) != null) {
						String name = entry.getName();
						String newFileName = FilenameUtils.getBaseName(name).concat(".txt");
						ZipEntry zipEntry = new ZipEntry(newFileName);
						append.putNextEntry(zipEntry);
						copy(fin, append);
						append.closeEntry();
						break;
					}
				}

			}
		}
		append.close();
	}

	public void writeURLs2zipFile(List<String> urlsStrings, String outFilePath) throws IOException {
		ZipOutputStream append = new ZipOutputStream(new FileOutputStream(outFilePath));
		// first, copy contents from input files
		for (String string : urlsStrings) {
			System.out.println(string);
			URL url = new URL(string); // 创建URL
			URLConnection urlconn = url.openConnection(); // 试图连接并取得返回状态码
			urlconn.connect();
			HttpURLConnection httpconn = (HttpURLConnection) urlconn;
			int httpStatus = httpconn.getResponseCode();// 服务器返回的状态
			if (httpStatus != HttpURLConnection.HTTP_OK) {
				System.err.print("无法连接到\t".concat(string));
			} else {
				InputStream inputStream = urlconn.getInputStream();

				int lastIndexOf = string.lastIndexOf("/");
				ZipEntry zipEntry = new ZipEntry(string.substring(lastIndexOf + 1));
				append.putNextEntry(zipEntry);
				copy(inputStream, append);
				append.closeEntry();
			}
		}
		append.close();
	}

	public void writeFiles2zipFile(List<String> filePathStrings, String outFilePath) throws IOException {

		ZipOutputStream append = new ZipOutputStream(new FileOutputStream(outFilePath));
		// first, copy contents from input files
		for (String string : filePathStrings) {
			File file = new File(string);
			FileInputStream fileInputStream = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			append.putNextEntry(zipEntry);
			copy(fileInputStream, append);
			append.closeEntry();
		}
		append.close();
	}

	public static void main(String[] args) throws IOException {
		long currentTimeMillis = System.currentTimeMillis();
		List<String> list = new LinkedList<>();
		list.add("accessionNumbers_0617_a2.txt");
		list.add("mainDataFile_0617_a2.txt");
		list.add("aaMutAccelerate.json");
		list.add("firstSubmitter.json");
		list.add("selectionCof.json");
		list.add("mutationFreq.json");

		// new ZipFilesWriter().writeFiles2zipFile(list, "a.zip");

		list.clear();
		String prefix = "https://bigd.big.ac.cn/ncov/apis/data-latest/";
		list.add(prefix + "accessionNumbers.xz");
		list.add(prefix + "mainDataFile.xz");
		list.add(prefix + "firstSubmitter.xz");
		new ZipFilesWriter().writeXZCompressedURLs2zipFile(list, "testOutpu/a.zip");
		
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
	}

}
