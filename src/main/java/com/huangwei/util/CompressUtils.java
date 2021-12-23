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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 压缩工具
 */
public class CompressUtils {
	private static final Logger logger = LoggerFactory.getLogger(CompressUtils.class);

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
		try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(new FileOutputStream(zipFile.toFile()));) {
			addFileToZip(zipOut, target, "");
		} catch (Throwable e1) {
			logger.error("[创建压缩包]压缩包创建异常!", e1);
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
			try (FileInputStream in = new FileInputStream(f); )  {
				IOUtils.copy(in, zipOut);
			} finally {
				zipOut.closeArchiveEntry();
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
