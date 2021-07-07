package com.huangwei.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.huangwei.crypto.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;

public class QRCodeUtil {
	protected static Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

	// 字符集
	private static final String CHARSET = StandardCharsets.UTF_8.name();

	public static final String FORMAT_PNG = "PNG";
	public static final String FORMAT_JPG = "jpg";
	public static final String FORMAT_JPEG = "JPEG";
	// 二维码尺寸
	private static final int QRCODE_SIZE = 300;
	// LOGO宽度
	private static final int WIDTH = 90;
	// LOGO高度
	private static final int HEIGHT = 90;

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            二维码内容
	 * @param width
	 *            二维码宽度
	 * @param height
	 *            二维码高度
	 * @param flag
	 *            是否需要base64字符串
	 * @return 二维码
	 */
	public static String createQrCode(String content, int width, int height, boolean flag) {
		String image = "";
		if (StringUtil.isBlank(content)) {
			logger.error("二维码内容为空");
			return image;
		}
		try {
			HashMap<EncodeHintType, Object> hints = new HashMap<>();
			hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
			// 指定二维码的纠错等级为中级
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			// 设置图片的边距
			hints.put(EncodeHintType.MARGIN, 1);
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", os);
			if (flag) {
				image = "data:image/png;base64," + Base64Util.encode(os.toByteArray());
			} else {
				image = Base64Util.encode(os.toByteArray());
			}
		} catch (Exception e) {
			logger.error("生成二维码出错", e);
		}
		return image;
	}

	/**
	 * 生成二维码
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param imgPath
	 *            生成二维码的路径
	 * @param needCompress
	 *            是否需要压缩
	 * @return
	 * @throws Exception
	 */
	private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (imgPath == null || "".equals(imgPath)) {
			return image;
		}
		// 插入图片
		insertImage(image, imgPath, needCompress);
		return image;
	}

	/**
	 * 插入图片
	 *
	 * @param source
	 *            底图
	 * @param imgPath
	 *            嵌入图片路径
	 * @param needCompress
	 *            是否需要压缩
	 * @throws Exception
	 */
	public static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
		File file = new File(imgPath);
		if (!file.exists()) {
			return;
		}
		BufferedImage src = ImageIO.read(new File(imgPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		int radius = 6;
		// 压缩LOGO
		if (needCompress) {
			if (width > WIDTH) {
				width = WIDTH;
			}
			if (height > HEIGHT) {
				height = HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = tag.createGraphics();
			tag = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g = tag.createGraphics();
			// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// g.setColor(Color.WHITE);
			// g.setBackground(Color.WHITE);
			// g.fill(new Rectangle2D.Double(0, 0, width, height));
			g.setClip(new RoundRectangle2D.Double(0, 0, width, height, 15, 15));
			// 绘制缩小后的图
			g.drawImage(image, 0, 0, null);
			g.dispose();
			ImageIO.write(tag, FORMAT_PNG, new File("H:\\Alpha\\t.png"));
			src = tag;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.setColor(Color.WHITE);
		graph.fill(new RoundRectangle2D.Float(x, y, width + 4, height + 4, 15, 15));
		graph.drawImage(src, x + 2, y + 2, width, height, null);
		// Shape shape = new RoundRectangle2D.Double(x, y, width, width, radius, radius);
		// graph.setStroke(new BasicStroke(3F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));// LOGO边框
		// graph.draw(shape);
		graph.dispose();
	}

	/**
	 * 插入图片
	 *
	 * @param source
	 *            底图
	 * @param src
	 *            嵌入图片
	 * @param x
	 *            坐标x
	 * @param y
	 *            坐标y
	 * @param needCompress
	 *            是否需要压缩
	 * @throws Exception
	 *             异常
	 */
	public static void insertImage(BufferedImage source, BufferedImage src, int x, int y, boolean needCompress)
			throws Exception {
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		int radius = 6;
		// 压缩LOGO
		if (needCompress) {
			if (width > WIDTH) {
				width = WIDTH;
			}
			if (height > HEIGHT) {
				height = HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = tag.createGraphics();
			tag = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g = tag.createGraphics();
			// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// g.setColor(Color.WHITE);
			// g.setBackground(Color.WHITE);
			// g.fill(new Rectangle2D.Double(0, 0, width, height));
			g.setClip(new RoundRectangle2D.Double(0, 0, width, height, 15, 15));
			// 绘制缩小后的图
			g.drawImage(image, 0, 0, null);
			g.dispose();
			ImageIO.write(tag, FORMAT_PNG, new File("H:\\Alpha\\t.png"));
			src = tag;
		}
		// 插入图片
		Graphics2D graph = source.createGraphics();
		graph.setColor(Color.WHITE);
//		graph.fill(new RoundRectangle2D.Float(x, y, width + 4, height + 4, 15, 15));
		graph.drawImage(src, x + 2, y + 2, width, height, null);

		graph.dispose();
	}

	/**
	 * 生成二维码
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param imgPath
	 *            嵌入二维码的图片路径
	 * @param destPath
	 *            生成之后二维码路径
	 * @param needCompress
	 *            是否需要压缩
	 * @throws Exception
	 */
	public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {
		BufferedImage image = createImage(content, imgPath, needCompress);
		mkdirs(destPath);
		ImageIO.write(image, FORMAT_PNG, new File(destPath));
	}

	/**
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param imgPath
	 *            嵌入二维码的图片路径
	 * @param destPath
	 *            生成之后二维码路径
	 * @throws Exception
	 */
	public static void encode(String content, String imgPath, String destPath) throws Exception {
		encode(content, imgPath, destPath, false);
	}

	/**
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param destPath
	 *            生成之后二维码路径
	 * @throws Exception
	 */
	public static void encode(String content, String destPath) throws Exception {
		encode(content, null, destPath, false);
	}

	/**
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param imgPath
	 *            嵌入二维码的图片路径
	 * @param output
	 *            输出流
	 * @param needCompress
	 *            是否需要压缩
	 * @throws Exception
	 */
	public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
			throws Exception {
		BufferedImage image = createImage(content, imgPath, needCompress);
		ImageIO.write(image, FORMAT_PNG, output);
	}

	/**
	 *
	 * @param content
	 *            扫描成功的内容
	 * @param output
	 *            输出流
	 * @throws Exception
	 */
	public static void encode(String content, OutputStream output) throws Exception {
		encode(content, null, output, false);
	}

	/**
	 * 对二维码进行解码
	 *
	 * @param file
	 *            二维码路径+二维码名称
	 * @return
	 * @throws Exception
	 */
	public static String decode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);// 优化精度
		hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);// 复杂模式，开启PURE_BARCODE模式
		result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	/**
	 * 对二维码进行解码
	 *
	 * @param path
	 *            二维码路径+二维码名称
	 * @return
	 * @throws Exception
	 */
	public static String decode(String path) throws Exception {
		return decode(new File(path));
	}

	/**
	 * 创建文件夹
	 *
	 * @param destPath
	 *            文件夹路径
	 */
	public static void mkdirs(String destPath) {
		File file = new File(destPath);
		// 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

}
