package com.huangwei.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * 压缩工具
 */
public class CompressUtils {

	/**
	 * 创建压缩包
	 * 
	 * @param target
	 *            压缩目标（不能为空）
	 * @param zipFile
	 *            压缩结果（不能为空）
	 * @throws IOException
	 *             异常
	 */
	public static void createZip(Path target, Path zipFile) throws IOException {
		ZipArchiveOutputStream zipOut = null;
		try {
			zipOut = new ZipArchiveOutputStream(new FileOutputStream(zipFile.toFile()));

			addFileToZip(zipOut, target, "");
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
	}

	/**
	 * 添加文件/目录至压缩包
	 * 
	 * @param zipOut
	 *            压缩包输出流
	 * @param target
	 *            压缩目标
	 * @param pathName
	 *            路径名称
	 * @throws IOException
	 *             异常
	 */
	public static void addFileToZip(ZipArchiveOutputStream zipOut, Path target, String pathName) throws IOException {
		File f = target.toFile();
		String entryName = pathName + f.getName();
		zipOut.putArchiveEntry(new ZipArchiveEntry(f, entryName));
		if (f.isFile()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(f);
				IOUtils.copy(in, zipOut);
			} finally {
				zipOut.closeArchiveEntry();
				IOUtils.closeQuietly(in);
			}
		} else {
			zipOut.closeArchiveEntry();
			File[] children = f.listFiles();
			if (children != null) {
				for (File child : children) {
					addFileToZip(zipOut, Paths.get(child.getAbsolutePath()), entryName + "/");
				}
			}
		}
	}

}
